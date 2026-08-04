package com.pha.trainees.chemistry.engine;

import com.pha.trainees.Main;
import com.pha.trainees.chemistry.container.IChemicalContainer;
import com.pha.trainees.chemistry.particle.IonType;
import com.pha.trainees.chemistry.particle.Phase;
import com.pha.trainees.chemistry.reaction.ReactionEdge;
import com.pha.trainees.chemistry.reaction.ReactionGraph;
import com.pha.trainees.chemistry.reaction.ReactionRule;
import com.pha.trainees.config.ChemConfig;
import com.pha.trainees.registry.ModChemistry;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * 化学反应引擎
 * 采用双模式驱动：事件驱动（快速反应）+ Tick轮询（慢速反应）
 */
public class ReactionEngine {

    // ============================================================
    // 一、常量配置
    // ============================================================

    /** 每 Tick 最多处理的连锁反应数量，防止栈溢出 */
    private static final int MAX_CHAIN_REACTIONS_PER_TICK = 10;

    /** 反应进度截断阈值，防止 Zeno 悖论 */
    private static final double EPSILON = 1e-6;

    /** Tick 轮询间隔（每 N Tick 执行一次完整轮询） */
    private static final int POLLING_INTERVAL = 5;

    /** 单次轮询最多处理的规则数量 */
    private static final int MAX_RULES_PER_POLL = 20;

    // ============================================================
    // 二、状态管理（线程安全）
    // ============================================================

    /**
     * 待处理事件队列
     * 存储新加入的离子及其所属容器，等待对应容器在下一个 Tick 处理。
     * 必须关联容器：多个容器同时存在时，避免离子被错误地应用于其他容器。
     */
    private record QueuedEntry(IChemicalContainer container, IonType ion) {}

    private static final ConcurrentLinkedQueue<QueuedEntry> PENDING_QUEUE = new ConcurrentLinkedQueue<>();

    /**
     * 挂起规则集
     * 存储因缺少反应物而暂时无法执行的规则
     * 在 Tick 轮询时会重检这些规则
     */
    private static final Set<ReactionRule> PENDING_RULES = Collections.synchronizedSet(new HashSet<>());

    // ============================================================
    // 三、最近反应记录（环形缓冲区，蓝本 §14.2）
    // ============================================================

    private static final int MAX_RECENT_REACTIONS = 20;
    private static final ArrayDeque<ReactionRecord> RECENT_REACTIONS = new ArrayDeque<>(MAX_RECENT_REACTIONS);

    /** 一次成功执行的反应记录 */
    public record ReactionRecord(String ruleId, double deltaXi, double heatKj, long gameTime) {}

    private static void recordReaction(ReactionRule rule, double deltaXi, double heatKj, long gameTime) {
        synchronized (RECENT_REACTIONS) {
            if (RECENT_REACTIONS.size() >= MAX_RECENT_REACTIONS) {
                RECENT_REACTIONS.removeFirst();
            }
            RECENT_REACTIONS.addLast(new ReactionRecord(rule.getId().toString(), deltaXi, heatKj, gameTime));
        }
    }

    /** 获取最近反应记录（最旧在前），用于调试命令 /chemtester last */
    public static List<ReactionRecord> getRecentReactions() {
        synchronized (RECENT_REACTIONS) {
            return new ArrayList<>(RECENT_REACTIONS);
        }
    }

    // ============================================================
    // 三、核心入口方法
    // ============================================================

    /**
     * 事件驱动入口
     * 当有新粒子进入容器时调用
     * 将粒子加入待处理队列，不立即执行
     */
    public static void trigger(IChemicalContainer container, IonType source) {
        if (container == null || source == null) {
            Main.LOGGER.debug("[Engine] trigger called with null container or source");
            return;
        }

        // 将粒子加入队列（关联所属容器），延迟到对应容器下一 Tick 处理
        PENDING_QUEUE.offer(new QueuedEntry(container, source));

        // 重置该容器的平衡状态（新粒子的加入可能打破平衡）
        container.resetBalancedRules();

        Main.LOGGER.debug("[Engine] trigger() called for {} (queue size: {})",
                source.getId().getPath(), PENDING_QUEUE.size());    }

    /**
     * Tick 轮询驱动入口
     * 在容器的 tick() 方法中调用
     */
    public static void tick(IChemicalContainer container) {
        if (container == null || container.isRemoved()) {
//            Main.LOGGER.info("[Engine] ❌ tick() skipped: container is null or removed");
            return;
        }

        long gameTime = container.getLevel().getGameTime();
//        Main.LOGGER.info("[Engine] ⏰ tick() called (gameTime: {}, queue size: {}, pending rules: {})",
//                gameTime, PENDING_QUEUE.size(), PENDING_RULES.size());

        // ====== 1. 处理待处理队列（事件驱动） ======
        // 只处理属于当前容器的队列项；其他容器的项保留在队列中，由其自身的 tick 处理
        int processed = 0;
        var iterator = PENDING_QUEUE.iterator();
        while (iterator.hasNext() && processed < MAX_CHAIN_REACTIONS_PER_TICK) {
            QueuedEntry entry = iterator.next();
            if (entry.container() != container) continue;
            iterator.remove();
            Main.LOGGER.debug("[Engine] Processing queued ion: {}", entry.ion().getId().getPath());
            processTrigger(container, entry.ion());
            processed++;
        }

        // ====== 2. 定期执行 Tick 轮询（慢速反应 + 挂起规则重检） ======
        if (gameTime % POLLING_INTERVAL == 0) {
//            Main.LOGGER.info("[Engine] 🔄 Polling triggered (gameTime % {} == 0)", POLLING_INTERVAL);
            processPolling(container);
        }
    }

    // ============================================================
    // 四、核心处理方法
    // ============================================================

    /**
     * 处理单个离子的触发事件
     */
    private static void processTrigger(IChemicalContainer container, IonType source) {
        List<ReactionEdge> edges = ReactionGraph.getInstance().getEdgesFrom(source);

        if (edges.isEmpty()) {
            Main.LOGGER.debug("[Engine] No edges from {}", source.getId().getPath());
            return;
        }

        List<ReactionEdge> executableEdges = new ArrayList<>();
        for (ReactionEdge edge : edges) {
            ReactionRule rule = edge.getRule();

            if (container.isRuleBalanced(rule)) {
                continue;
            }

            if (!rule.checkPreconditions(container)) {
                continue;
            }

            if (container.getTemperature() < rule.getMinTemperature()) {
                continue;
            }

            if (!container.containsAll(rule.getReactants())) {
                // 检查是否有至少一种反应物仍然存在
                boolean hasAnyReactant = false;
                for (IonType ion : rule.getReactants().keySet()) {
                    if (container.getAmount(ion) > EPSILON) {
                        hasAnyReactant = true;
                        break;
                    }
                }
                if (hasAnyReactant) {
                    // H⁺ 和 OH⁻ 单独存在时不挂起，避免污染
                    boolean isCommonIon = source == ModChemistry.ModIons.H_plus || source == ModChemistry.ModIons.OH_minus;
                    if (!isCommonIon) {
                        Main.LOGGER.debug("[Engine] Reactants incomplete for {} (some remain), pending",
                                rule.getId().getPath());
                        PENDING_RULES.add(rule);
                    }
                }
                continue;
            }

            executableEdges.add(edge);
        }

        if (!executableEdges.isEmpty()) {
            executableEdges.sort((a, b) -> {
                double scoreA = a.getRule().calculatePriority(container);
                double scoreB = b.getRule().calculatePriority(container);
                return Double.compare(scoreB, scoreA);
            });

            ReactionEdge bestEdge = executableEdges.get(0);
            Main.LOGGER.debug("[Engine] Executing best edge: {} → {}",
                    bestEdge.getSource().getId().getPath(),
                    bestEdge.getTarget().getId().getPath());
            executeRule(container, bestEdge);
        }
    }

    /**
     * Tick 轮询处理
     */
    private static void processPolling(IChemicalContainer container) {
        int executed = 0;

        // ====== 重检挂起规则 ======
        synchronized (PENDING_RULES) {
            Iterator<ReactionRule> iterator = PENDING_RULES.iterator();
            while (iterator.hasNext() && executed < MAX_RULES_PER_POLL) {
                ReactionRule rule = iterator.next();

                // 跳过已平衡的规则
                if (container.isRuleBalanced(rule)) {
                    iterator.remove();
                    continue;
                }

                // 检查反应物是否现在齐全了
                if (container.containsAll(rule.getReactants())) {
                    // 尝试执行该规则
                    // 需要找到对应的边
                    List<ReactionEdge> edges = ReactionGraph.getInstance().getAllEdges();
                    for (ReactionEdge edge : edges) {
                        if (edge.getRule().equals(rule)) {
                            if (executeRule(container, edge)) {
                                executed++;
                                iterator.remove();
                                break;
                            }
                        }
                    }
                }
            }
        }

        // ====== 处理自环（分解反应） ======
        if (executed < MAX_RULES_PER_POLL) {
            for (IonType ion : container.getPresentIons()) {
                List<ReactionEdge> edges = ReactionGraph.getInstance().getEdgesFrom(ion);
                for (ReactionEdge edge : edges) {
                    if (edge.isSelfLoop()) {
                        ReactionRule rule = edge.getRule();

                        // 跳过已平衡的规则
                        if (container.isRuleBalanced(rule)) {
                            continue;
                        }

                        // 检查前置条件和温度
                        if (!rule.checkPreconditions(container) ||
                                container.getTemperature() < rule.getMinTemperature()) {
                            continue;
                        }

                        // 检查反应物（自环只有一个反应物）
                        if (container.containsAll(rule.getReactants())) {
                            if (executeRule(container, edge)) {
                                executed++;
                                break;
                            }
                        }
                    }
                }
                if (executed >= MAX_RULES_PER_POLL) break;
            }
        }
    }

    // ============================================================
    // 五、规则执行引擎（核心数学计算）
    // ============================================================

    /**
     * 执行一条规则
     * @return true 如果成功执行了反应
     */
    private static boolean executeRule(IChemicalContainer container, ReactionEdge edge) {
        ReactionRule rule = edge.getRule();

        // 再次验证所有条件（安全）
        if (!rule.checkPreconditions(container)) return false;
        if (container.getTemperature() < rule.getMinTemperature()) return false;
        if (!container.containsAll(rule.getReactants())) return false;

        // 电解反应（需电功）未通电时不可运行
        if (rule.getElectricalWorkPerMol() > 0 && !container.isElectricallyPowered()) return false;

        // ====== 1. 计算当前浓度 ======
        Map<IonType, Double> concentrations = new HashMap<>();
        for (IonType ion : rule.getReactants().keySet()) {
            concentrations.put(ion, container.getEffectiveConcentration(ion));
        }

        // ====== 2. 计算反应商 Q ======
        double Q = calculateQ(container, rule);

        // ====== 3. 计算当前平衡常数 K ======
        double K = rule.calculateEquilibriumConstant(container.getTemperature(), container);

        // ====== 4. 如果 Q >= K，反应已经处于平衡或逆向，不执行 ======
        if (Q >= K) {
            container.markRuleBalanced(rule);
            return false;
        }

        // ====== 5. 计算速率常数 k ======
        double k = rule.calculateRateConstant(container.getTemperature());

        // ====== 6. 计算正向速率 ======
        double forwardRate = k;
        for (Map.Entry<IonType, Integer> entry : rule.getReactants().entrySet()) {
            double conc = concentrations.getOrDefault(entry.getKey(), 0.0);
            forwardRate *= Math.pow(conc, entry.getValue());
        }

        // ====== 7. 计算净速率（考虑平衡限制） ======
        double netRate = forwardRate * (1.0 - Q / K);
        if (netRate < 0) {
            container.markRuleBalanced(rule);
            return false;
        }

        // ====== 8. 计算本 Tick 的反应进度 Δξ ======
        double deltaTime = 0.05; // 1 Tick = 0.05 秒
        double deltaXi = netRate * deltaTime;

        // ====== 9. 进度截断（防 Zeno） ======
        if (deltaXi < EPSILON) {
            container.markRuleBalanced(rule);
            return false;
        }

        // ====== 10. 检查是否有足够的反应物 ======
        for (Map.Entry<IonType, Integer> entry : rule.getReactants().entrySet()) {
            IonType ion = entry.getKey();
            double required = entry.getValue() * deltaXi;
            if (container.getAmount(ion) < required - EPSILON) {
                // 反应物不足，调整 deltaXi
                double maxDelta = container.getAmount(ion) / entry.getValue();
                deltaXi = Math.min(deltaXi, maxDelta);
            }
        }

        // 如果调整后太小，放弃
        if (deltaXi < EPSILON) {
            return false;
        }

        // ====== 10.4 平衡约束（防欧拉越界） ======
        // 高速反应（高温/高浓度）Δξ 可能一步冲过平衡（Q 越过 K）。
        // 二分缩小 Δξ，使推进后的 Q ≈ K，保证不越过平衡点。
        if (deltaXi > 0) {
            double qAfter = calculateQAfter(container, rule, deltaXi);
            if (qAfter > K) {
                double lo = 0.0, hi = deltaXi;
                for (int i = 0; i < 8; i++) {
                    double mid = (lo + hi) / 2.0;
                    if (calculateQAfter(container, rule, mid) > K) {
                        hi = mid;
                    } else {
                        lo = mid;
                    }
                }
                deltaXi = lo;
                if (deltaXi < EPSILON) {
                    return false;
                }
            }
        }

        // ====== 10.5 电解耗电 ======
        // 消耗 electricalWorkPerMol × Δξ（kJ）；电能不足则本次不执行
        double electricalWork = rule.getElectricalWorkPerMol();
        if (electricalWork > 0 && !container.consumeElectricalEnergy(electricalWork * deltaXi)) {
            return false;
        }

        // ====== 11. 应用反应 ======
        // 消耗反应物（不触发引擎）
        for (Map.Entry<IonType, Integer> entry : rule.getReactants().entrySet()) {
            IonType ion = entry.getKey();
            double consumed = entry.getValue() * deltaXi;
            container.removeIon(ion, consumed, false);
        }

        // 生成产物（不触发引擎）
        for (Map.Entry<IonType, Integer> entry : rule.getProducts().entrySet()) {
            IonType ion = entry.getKey();
            double produced = entry.getValue() * deltaXi;
            container.addIon(ion, produced, false);
            // 手动入队到 PENDING_QUEUE（仅当有出边时，关联所属容器）
            if (!ReactionGraph.getInstance().getEdgesFrom(ion).isEmpty()) {
                PENDING_QUEUE.offer(new QueuedEntry(container, ion));
            }
        }

        // ====== 12. 热力学反馈 ======
        // 反应释放的热量 = -ΔH × Δξ（容器吸收热量，温度升高）
        double reactionHeat = -rule.getDeltaH() * deltaXi; // kJ（正值为放热）
        container.addThermalEnergy(reactionHeat * 1000); // 转为 J

        // 记录最近反应（环形缓冲区）
        recordReaction(rule, deltaXi, reactionHeat, container.getLevel().getGameTime());

        Main.LOGGER.debug("[Engine] Executed {}: Δξ={}, heat={}kJ",
                rule.getId(), String.format("%.6f", deltaXi), String.format("%.2f", reactionHeat));

        return true;
    }

    // ============================================================
    // 六、辅助计算函数
    // ============================================================

    /**
     * 计算反应商 Q
     */
    private static double calculateQ(IChemicalContainer container, ReactionRule rule) {
        double numerator = 1.0;
        double denominator = 1.0;

        for (Map.Entry<IonType, Integer> entry : rule.getProducts().entrySet()) {
            double conc = container.getEffectiveConcentration(entry.getKey());
            numerator *= Math.pow(conc, entry.getValue());
        }

        for (Map.Entry<IonType, Integer> entry : rule.getReactants().entrySet()) {
            double conc = container.getEffectiveConcentration(entry.getKey());
            denominator *= Math.pow(conc, entry.getValue());
        }

        if (denominator < EPSILON) return Double.MAX_VALUE;
        return numerator / denominator;
    }

    /**
     * 计算按给定 Δξ 推进后的反应商 Q（不实际修改容器）。
     * 用于平衡约束：避免高速反应（高温/高浓度）一步越过平衡（Q 冲过 K）。
     */
    private static double calculateQAfter(IChemicalContainer container, ReactionRule rule, double deltaXi) {
        double numerator = 1.0;
        double denominator = 1.0;
        double volume = container.getVolume();
        for (Map.Entry<IonType, Integer> entry : rule.getProducts().entrySet()) {
            IonType ion = entry.getKey();
            double amount = container.getAmount(ion) + entry.getValue() * deltaXi;
            double conc = getActivityOrConcentration(ion, amount, volume);
            numerator *= Math.pow(conc, entry.getValue());
        }
        for (Map.Entry<IonType, Integer> entry : rule.getReactants().entrySet()) {
            IonType ion = entry.getKey();
            double amount = container.getAmount(ion) - entry.getValue() * deltaXi;
            double conc = getActivityOrConcentration(ion, Math.max(0, amount), volume);
            denominator *= Math.pow(conc, entry.getValue());
        }
        if (denominator < 1e-9) return Double.MAX_VALUE;
        return numerator / denominator;
    }

    /** 与 IChemicalContainer.getEffectiveConcentration 同语义：LIQUID/SOLID 活度 1，GAS/AQUEOUS 浓度 n/V */
    private static double getActivityOrConcentration(IonType ion, double amount, double volume) {
        Phase phase = ion.getPhase();
        if (phase == Phase.LIQUID || phase == Phase.SOLID) return 1.0;
        if (volume <= 0) return 0;
        return amount / volume;
    }

    // ============================================================
    // 七、调试与状态查询
    // ============================================================

    public static int getPendingQueueSize() {
        return PENDING_QUEUE.size();
    }

    public static int getPendingRulesCount() {
        return PENDING_RULES.size();
    }

    public static void clearPendingQueue() {
        PENDING_QUEUE.clear();
    }

    public static void clearPendingRules() {
        PENDING_RULES.clear();
    }
}