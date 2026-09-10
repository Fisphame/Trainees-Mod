package com.pha.trainees.chemistry.report;

import com.pha.trainees.chemistry.container.IChemicalContainer;
import com.pha.trainees.chemistry.engine.ExecutedRule;
import com.pha.trainees.chemistry.engine.PredictedRule;
import com.pha.trainees.chemistry.engine.ReactionEngine;
import com.pha.trainees.chemistry.engine.ReactionFailure;
import com.pha.trainees.chemistry.particle.IonType;
import com.pha.trainees.chemistry.particle.Phase;
import com.pha.trainees.chemistry.util.IonDisplay;
import com.pha.trainees.chemistry.util.ReactionFormula;
import com.pha.trainees.config.ChemConfig;
import com.pha.trainees.network.AnalyzerReportPacket;
import com.pha.trainees.network.ModNetwork;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * 分析仪诊断报告生成器（§19.6/19.7 合流设计的服务端侧）。
 *
 * <p>同一份诊断数据供两个出口使用：</p>
 * <ul>
 *   <li><b>GUI（方案 a）</b>：{@link #sendToClient} 打包推给客户端 {@code AnalyzerScreen}（诊断页 + 预测页）；</li>
 *   <li><b>聊天简报（方案 b）</b>：{@link #sendChatBrief} 潜行右键时发到聊天框（快速查看）。</li>
 * </ul>
 *
 * <p><b>本地化（§19.16）</b>：服务端只发"翻译键 + 参数"（{@link ReportLine}），
 * 玩家可见文本全部由客户端渲染；聊天简报直接用 {@link Component#translatable}（客户端同样会本地化）。</p>
 *
 * <p>数据来源全部是引擎既有查询：{@link ReactionEngine#predictReactions}（正向预测）、
 * 容器的 {@code getRecentFailures}（失败原因）与 {@code getRecentExecutedRules}（§19.7 决策 2c 竞争信息）。</p>
 */
public final class AnalyzerReport {

    private AnalyzerReport() {}

    private static final int MAX_FAILURE_LINES = 5;
    private static final int MAX_EXECUTED_LINES = 20;
    private static final int MAX_BLOCKED_CHAT_LINES = 5;
    private static final int MAX_FAILURE_CHAT_LINES = 3;
    private static final int MAX_EXECUTED_CHAT_LINES = 2;

    // ==================== 对外出口 ====================

    /** 打开诊断 GUI：服务端组装结构化报告并推给该玩家客户端。 */
    public static void sendToClient(ServerPlayer player, IChemicalContainer container) {
        ModNetwork.get().send(PacketDistributor.PLAYER.with(() -> player), build(container));
    }

    /** 聊天简报：保持原有便捷出口（潜行右键）。文本走可翻译组件，客户端本地化。 */
    public static void sendChatBrief(ServerPlayer player, IChemicalContainer container) {
        List<PredictedRule> predicted = ReactionEngine.predictReactions(container);

        player.sendSystemMessage(Component.translatable("gui.trainees.analyzer.chat.title")
                .withStyle(net.minecraft.ChatFormatting.GOLD, net.minecraft.ChatFormatting.BOLD));
        player.sendSystemMessage(Component.translatable("gui.trainees.analyzer.chat.header",
                Component.translatable(containerIdOf(container)), fmt(container.getTemperature(), 1)));

        // 可执行
        player.sendSystemMessage(Component.translatable("gui.trainees.analyzer.section.executable")
                .withStyle(net.minecraft.ChatFormatting.YELLOW));
        boolean anyExec = false;
        List<PredictedRule> blocked = new ArrayList<>();
        for (PredictedRule p : predicted) {
            if (p.status() == PredictedRule.Status.EXECUTABLE) {
                player.sendSystemMessage(Component.translatable("gui.trainees.analyzer.line.executable",
                        ReactionFormula.bold(ReactionFormula.format(p.rule())),
                        argOr(p, 0, "-"), argOr(p, 1, "-")));
                anyExec = true;
            } else {
                blocked.add(p);
            }
        }
        if (!anyExec) {
            player.sendSystemMessage(Component.translatable("gui.trainees.analyzer.chat.none"));
        }

        // 未达条件（含可操作建议）
        player.sendSystemMessage(Component.translatable("gui.trainees.analyzer.section.blocked")
                .withStyle(net.minecraft.ChatFormatting.RED));
        int shown = 0;
        for (PredictedRule p : blocked) {
            if (shown++ >= MAX_BLOCKED_CHAT_LINES) break;
            player.sendSystemMessage(Component.translatable("gui.trainees.analyzer.line.blocked",
                    Component.translatable(predictedNameKey(p.status())),
                    ReactionFormula.bold(ReactionFormula.format(p.rule()))));
            player.sendSystemMessage(detailComponent(p));
            player.sendSystemMessage(suggestionComponent(container, p));
        }
        if (blocked.isEmpty()) {
            player.sendSystemMessage(Component.translatable("gui.trainees.analyzer.chat.none"));
        }

        // 最近失败
        List<ReactionFailure> failures = container.getRecentFailures();
        player.sendSystemMessage(Component.translatable("gui.trainees.analyzer.chat.failures_header",
                failures.size()).withStyle(net.minecraft.ChatFormatting.GOLD));
        if (failures.isEmpty()) {
            player.sendSystemMessage(Component.translatable("gui.trainees.analyzer.chat.none"));
        } else {
            int n = 0;
            for (int i = failures.size() - 1; i >= 0 && n < MAX_FAILURE_CHAT_LINES; i--, n++) {
                ReactionFailure f = failures.get(i);
                player.sendSystemMessage(Component.translatable("gui.trainees.analyzer.line.failure",
                        Component.translatable(failureNameKey(f.type())),
                        ReactionFormula.bold(ReactionFormula.formatById(f.ruleId()))));
                player.sendSystemMessage(failureDetailComponent(f));
            }
        }

        // 最近执行 + 竞争信息（2c）
        List<ExecutedRule> executed = container.getRecentExecutedRules();
        if (!executed.isEmpty()) {
            player.sendSystemMessage(Component.translatable("gui.trainees.analyzer.section.executed")
                    .withStyle(net.minecraft.ChatFormatting.GOLD));
            int n = 0;
            for (int i = executed.size() - 1; i >= 0 && n < MAX_EXECUTED_CHAT_LINES; i--, n++) {
                ExecutedRule r = executed.get(i);
                player.sendSystemMessage(Component.translatable("gui.trainees.analyzer.line.executed",
                        ReactionFormula.bold(ReactionFormula.formatById(r.ruleId())), fmt(r.deltaXi(), 4)));
                for (ExecutedRule.CompetitionNote note : r.competition()) {
                    player.sendSystemMessage(Component.translatable("gui.trainees.analyzer.line.competition",
                            note.blockedRuleId(), IonDisplay.format(pathOf(note.reactantId()))));
                }
            }
        }
    }

    // ==================== 报告组装 ====================

    public static AnalyzerReportPacket build(IChemicalContainer container) {
        return new AnalyzerReportPacket(
                containerIdOf(container),
                summaryLines(container),
                contentLines(container),
                failureLines(container),
                executedLines(container),
                predictedLines(container, true),
                predictedLines(container, false));
    }

    private static List<ReportLine> summaryLines(IChemicalContainer container) {
        List<ReportLine> lines = new ArrayList<>();
        double temp = container.getTemperature();
        lines.add(ReportLine.of("gui.trainees.analyzer.line.temperature", fmt(temp, 1), fmt(temp - 273.15, 1)));
        lines.add(ReportLine.of("gui.trainees.analyzer.line.volume", fmt(container.getVolume(), 2)));
        lines.add(ReportLine.of("gui.trainees.analyzer.line.total",
                fmt(container.getTotalMoles(), 3), fmt(ChemConfig.MAX_TOTAL_MOLES.get(), 0)));
        if (container.isElectricallyPowered()) {
            lines.add(ReportLine.of("gui.trainees.analyzer.line.powered", fmt(container.getVoltageFactor(), 2)));
        } else {
            lines.add(ReportLine.of("gui.trainees.analyzer.line.unpowered"));
        }
        // 机器类容器的专属状态（§19.14：电解槽的电极/隔膜/储能等走这个钩子）
        container.appendDiagnostics(lines);
        lines.add(ReportLine.of("gui.trainees.analyzer.line.position", container.getBlockPos().toShortString()));
        return lines;
    }

    private static List<ReportLine> contentLines(IChemicalContainer container) {
        List<ReportLine> lines = new ArrayList<>();
        Map<IonType, Double> contents = container.getContents();
        if (contents.isEmpty()) {
            lines.add(ReportLine.of("gui.trainees.analyzer.line.empty"));
            return lines;
        }
        double total = Math.max(container.getTotalMoles(), 1e-9);
        contents.entrySet().stream()
                .sorted(Comparator.comparingDouble((Map.Entry<IonType, Double> e) -> e.getValue()).reversed())
                .forEach(e -> {
                    IonType ion = e.getKey();
                    double pct = e.getValue() / total * 100.0;
                    lines.add(ReportLine.raw("gui.trainees.analyzer.line.content", List.of(
                            ReportLine.ReportArg.of(IonDisplay.format(ion.getId().getPath())),
                            ReportLine.ReportArg.of(fmt(e.getValue(), 4)),
                            ReportLine.ReportArg.of(fmt(pct, 1)),
                            ReportLine.ReportArg.key(ion.getForm().getTranslationKey()))));
                });
        return lines;
    }

    private static List<ReportLine> failureLines(IChemicalContainer container) {
        List<ReportLine> lines = new ArrayList<>();
        List<ReactionFailure> failures = container.getRecentFailures();
        if (failures.isEmpty()) {
            lines.add(ReportLine.of("gui.trainees.analyzer.line.empty"));
            return lines;
        }
        int shown = 0;
        for (int i = failures.size() - 1; i >= 0 && shown < MAX_FAILURE_LINES; i--, shown++) {
            ReactionFailure f = failures.get(i);
            lines.add(ReportLine.raw("gui.trainees.analyzer.line.failure", List.of(
                    ReportLine.ReportArg.key(failureNameKey(f.type())),
                    ReportLine.ReportArg.of(ReactionFormula.bold(ReactionFormula.formatById(f.ruleId()))))));
            lines.add(failureDetail(f));
        }
        return lines;
    }

    private static List<ReportLine> executedLines(IChemicalContainer container) {
        List<ReportLine> lines = new ArrayList<>();
        List<ExecutedRule> executed = container.getRecentExecutedRules();
        if (executed.isEmpty()) {
            lines.add(ReportLine.of("gui.trainees.analyzer.line.no_executed"));
            return lines;
        }
        int shown = 0;
        for (int i = executed.size() - 1; i >= 0 && shown < MAX_EXECUTED_LINES; i--, shown++) {
            ExecutedRule r = executed.get(i);
            lines.add(ReportLine.of("gui.trainees.analyzer.line.executed",
                    ReactionFormula.bold(ReactionFormula.formatById(r.ruleId())), fmt(r.deltaXi(), 4)));
            for (ExecutedRule.CompetitionNote note : r.competition()) {
                lines.add(ReportLine.of("gui.trainees.analyzer.line.competition",
                        note.blockedRuleId(), IonDisplay.format(pathOf(note.reactantId()))));
            }
        }
        return lines;
    }

    /** 预测页：executable=true 取可执行，false 取未达条件（带可操作建议）。 */
    private static List<ReportLine> predictedLines(IChemicalContainer container, boolean executable) {
        List<ReportLine> lines = new ArrayList<>();
        for (PredictedRule p : ReactionEngine.predictReactions(container)) {
            boolean isExec = p.status() == PredictedRule.Status.EXECUTABLE;
            if (isExec != executable) continue;
            String equation = ReactionFormula.bold(ReactionFormula.format(p.rule()));
            if (isExec) {
                lines.add(ReportLine.of("gui.trainees.analyzer.line.executable",
                        equation, argOr(p, 0, "-"), argOr(p, 1, "-")));
            } else {
                lines.add(ReportLine.raw("gui.trainees.analyzer.line.blocked", List.of(
                        ReportLine.ReportArg.key(predictedNameKey(p.status())),
                        ReportLine.ReportArg.of(equation))));
                lines.add(predictedDetail(p));
                lines.add(suggestion(container, p));
            }
        }
        if (lines.isEmpty()) {
            lines.add(ReportLine.of(executable
                    ? "gui.trainees.analyzer.line.no_executable"
                    : "gui.trainees.analyzer.line.no_blocked"));
        }
        return lines;
    }

    // ==================== 失败 / 预测 的明细与建议（结构化） ====================

    /** 失败明细行：按类型给键 + 参数 */
    private static ReportLine failureDetail(ReactionFailure f) {
        String key = failureDetailKey(f.type());
        List<ReportLine.ReportArg> args = new ArrayList<>();
        for (String a : f.args()) args.add(ReportLine.ReportArg.of(a));
        return ReportLine.raw(key, args);
    }

    /** 预测明细行：按状态（含 ALREADY_BALANCED 的两种变体）给键 + 参数 */
    private static ReportLine predictedDetail(PredictedRule p) {
        String key = predictedDetailKey(p);
        List<ReportLine.ReportArg> args = new ArrayList<>();
        for (String a : p.args()) args.add(ReportLine.ReportArg.of(a));
        return ReportLine.raw(key, args);
    }

    /** 可操作建议（§19.7：不只说"缺什么"，还要说"怎么补"） */
    private static ReportLine suggestion(IChemicalContainer container, PredictedRule p) {
        return switch (p.status()) {
            case TEMPERATURE_TOO_LOW -> ReportLine.of("gui.trainees.analyzer.suggest.temperature",
                    fmt(p.rule().getMinTemperature(), 0), fmt(container.getTemperature(), 0));
            case MISSING_PRECONDITION -> ReportLine.of("gui.trainees.analyzer.suggest.precondition",
                    names(p.rule().getPreconditions(), container));
            case MISSING_REACTANT -> ReportLine.of("gui.trainees.analyzer.suggest.reactant",
                    names(p.rule().getReactants(), container));
            case NOT_POWERED -> ReportLine.of("gui.trainees.analyzer.suggest.power");
            case ALREADY_BALANCED -> ReportLine.of("gui.trainees.analyzer.suggest.balanced");
            case EXECUTABLE -> ReportLine.of("gui.trainees.analyzer.suggest.executable");
        };
    }

    // ==================== 聊天简报的组件版本 ====================

    private static Component failureDetailComponent(ReactionFailure f) {
        Object[] args = f.args().toArray();
        return Component.translatable(failureDetailKey(f.type()), args);
    }

    private static Component detailComponent(PredictedRule p) {
        return Component.translatable(predictedDetailKey(p), p.args().toArray());
    }

    private static Component suggestionComponent(IChemicalContainer container, PredictedRule p) {
        ReportLine line = suggestion(container, p);
        Object[] args = line.args().stream()
                .map(a -> a.translatable() ? (Object) Component.translatable(a.value()) : (Object) a.value())
                .toArray();
        return Component.translatable(line.key(), args);
    }

    // ==================== 键名映射 ====================

    /** 失败原因名（玩家可读，客户端本地化） */
    public static String failureNameKey(ReactionFailure.Type type) {
        return "gui.trainees.analyzer.failure." + type.name().toLowerCase();
    }

    private static String failureDetailKey(ReactionFailure.Type type) {
        return "gui.trainees.analyzer.failure_detail." + type.name().toLowerCase();
    }

    private static String predictedNameKey(PredictedRule.Status status) {
        return "gui.trainees.analyzer.predicted." + status.name().toLowerCase();
    }

    /** ALREADY_BALANCED 有两种来源（引擎已标记 / Q≥K），用参数个数区分键 */
    private static String predictedDetailKey(PredictedRule p) {
        String base = "gui.trainees.analyzer.predicted_detail." + p.status().name().toLowerCase();
        if (p.status() == PredictedRule.Status.ALREADY_BALANCED) {
            return p.args().size() >= 2 ? base + ".q" : base + ".marked";
        }
        return base;
    }

    // ==================== 辅助 ====================

    private static String argOr(PredictedRule p, int index, String fallback) {
        return index < p.args().size() ? p.args().get(index) : fallback;
    }

    /** "namespace:path" → "path"（IonDisplay 需要 path） */
    private static String pathOf(String ionId) {
        int sep = ionId.indexOf(':');
        return sep >= 0 ? ionId.substring(sep + 1) : ionId;
    }

    /** 列出容器中缺失（≈0）的指定离子显示名；全齐时返回"（无缺失）" */
    private static String names(Map<IonType, Integer> required, IChemicalContainer container) {
        StringBuilder sb = new StringBuilder();
        for (IonType ion : required.keySet()) {
            if (container.getAmount(ion) <= 1e-9) {
                if (sb.length() > 0) sb.append("、");
                sb.append(IonDisplay.format(ion.getId().getPath()));
            }
        }
        return sb.length() == 0 ? "?" : sb.toString();
    }

    /**
     * 容器显示名的**翻译键**（方块 id 的 descriptionId，如 {@code block.trainees.beaker}）：
     * 由客户端本地化（§19.14 点 2 的同一规则：方块显示统一取 id 翻译键）。
     */
    private static String containerIdOf(IChemicalContainer container) {
        try {
            if (container.getLevel() != null) {
                return container.getLevel().getBlockState(container.getBlockPos()).getBlock()
                        .getDescriptionId();
            }
        } catch (Exception ignored) {
        }
        return "?";
    }

    private static String fmt(double v, int decimals) {
        return String.format("%." + decimals + "f", v);
    }
}
