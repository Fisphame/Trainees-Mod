package com.pha.trainees.chemistry.blockentity;

import com.pha.trainees.Main;
import com.pha.trainees.chemistry.container.IChemicalContainer;
import com.pha.trainees.chemistry.engine.ExecutedRule;
import com.pha.trainees.chemistry.engine.ReactionEngine;
import com.pha.trainees.chemistry.engine.ReactionFailure;
import com.pha.trainees.chemistry.gas.GasGridManager;
import com.pha.trainees.chemistry.particle.IonType;
import com.pha.trainees.chemistry.particle.Phase;
import com.pha.trainees.chemistry.reaction.ReactionRule;
import com.pha.trainees.chemistry.util.FluidIonMapper;
import com.pha.trainees.config.ChemConfig;
import com.pha.trainees.registry.ModChemistry;
import com.pha.trainees.registry.ModChemistry.ModIons;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class BeakerBlockEntity extends BlockEntity implements IChemicalContainer, IFluidHandler {

    private static final double DEFAULT_TEMPERATURE = ChemConfig.DEFAULT_TEMPERATURE.get();
    private static final double DEFAULT_VOLUME = ChemConfig.DEFAULT_VOLUME.get();
    private static final double MAX_SAFE_TEMPERATURE = ChemConfig.MAX_SAFE_TEMPERATURE.get();

    private static final Map<Block, Double> HEAT_SOURCE_TEMPS = new HashMap<>();
    static {
        HEAT_SOURCE_TEMPS.put(Blocks.FIRE, ChemConfig.HEAT_SOURCE_FIRE.get());
        HEAT_SOURCE_TEMPS.put(Blocks.SOUL_FIRE, ChemConfig.HEAT_SOURCE_SOUL_FIRE.get());
        HEAT_SOURCE_TEMPS.put(Blocks.LAVA, ChemConfig.HEAT_SOURCE_LAVA.get());
//        HEAT_SOURCE_TEMPS.put(Blocks.FLOWING_LAVA, 1500.0);
        HEAT_SOURCE_TEMPS.put(Blocks.MAGMA_BLOCK, ChemConfig.HEAT_SOURCE_MAGMA.get());
        HEAT_SOURCE_TEMPS.put(Blocks.CAMPFIRE, ChemConfig.HEAT_SOURCE_CAMPFIRE.get());
        HEAT_SOURCE_TEMPS.put(Blocks.SOUL_CAMPFIRE, ChemConfig.HEAT_SOURCE_SOUL_CAMPFIRE.get());
    }

    private final Map<IonType, Double> contents = new ConcurrentHashMap<>();
    /** 逸散缓冲：容器扣减后先在此累积，达到网格消散阈值才抛入网格（防微量尾巴格） */
    private final Map<IonType, Double> gridLeakBuffer = new HashMap<>();
    private double temperature = DEFAULT_TEMPERATURE;
    private double volume = DEFAULT_VOLUME;
    private double totalHeatCapacity = ChemConfig.BEAKER_BASE_HEAT_CAPACITY.get();

    private final Set<ReactionRule> balancedRules = ConcurrentHashMap.newKeySet();


    public BeakerBlockEntity(BlockPos pos, BlockState state) {
        this(ModChemistry.ModChemistryBlockEntities.BEAKER.get(), pos, state);
    }

    /** 子类（如电解槽）可传入自己的 BE 类型，复用烧杯的全部容器实现 */
    protected BeakerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    // ==================== IChemicalContainer 实现 ====================

    @Override
    public Map<IonType, Double> getContents() {
        return Map.copyOf(contents);
    }

    @Override
    public double getAmount(IonType ion) {
        return contents.getOrDefault(ion, 0.0);
    }


    @Override
    public synchronized double addIon(IonType ion, double moles) {
        return addIon(ion, moles, true);
    }

    @Override
    public synchronized double addIon(IonType ion, double moles, boolean triggerEngine) {
        if (moles <= 0 || ion == null) return 0;
        double current = contents.getOrDefault(ion, 0.0);
        double newAmount = current + moles;
        contents.put(ion, newAmount);
        recalcHeatCapacity();
        setChanged();
        syncToClient();
        resetBalancedRules();
        if (triggerEngine && level != null && !level.isClientSide) {
            ReactionEngine.trigger(this, ion);
        }
        return moles;
    }

    @Override
    public synchronized double removeIon(IonType ion, double moles) {
        return removeIon(ion, moles, true);
    }

    @Override
    public synchronized double removeIon(IonType ion, double moles, boolean triggerEngine) {
        if (moles <= 0 || ion == null) return 0;
        double current = contents.getOrDefault(ion, 0.0);
        double removed = Math.min(current, moles);
        if (removed <= 0) return 0;
        double newAmount = current - removed;
        if (newAmount < 1e-9) {
            contents.remove(ion);
        } else {
            contents.put(ion, newAmount);
        }
        recalcHeatCapacity();
        setChanged();
        syncToClient();
        resetBalancedRules();
        if (triggerEngine && level != null && !level.isClientSide) {
            ReactionEngine.trigger(this, ion);
        }
        return removed;
    }


    @Override
    public boolean contains(IonType ion, double minMoles) {
        return contents.getOrDefault(ion, 0.0) >= minMoles - 1e-9;
    }

    @Override
    public Set<IonType> getPresentIons() {
        return contents.keySet();
    }

    @Override
    public double getTemperature() {
        return temperature;
    }

    @Override
    public void setTemperature(double temperatureKelvin) {
        this.temperature = Math.max(0, temperatureKelvin);
        setChanged();
        syncToClient();
    }

    @Override
    public double getVolume() {
        return volume;
    }

    @Override
    public void setVolume(double volumeLiters) {
        if (volumeLiters > 0) {
            this.volume = volumeLiters;
            setChanged();
            syncToClient();
        }
    }

    @Override
    public double getPressure() {
        // 压强仅由气相粒子贡献（蓝本 §4.2）
        double gasMoles = contents.entrySet().stream()
                .filter(e -> e.getKey().getPhase() == Phase.GAS)
                .mapToDouble(Map.Entry::getValue)
                .sum();
        if (gasMoles < 1e-9 || volume < 1e-9) return 0;
        double R = 8.314;
        // 注意单位：V 以升(L)计，nRT/V 得到的是 kPa（1 J/L = 1 kPa）。
        // 除以 101.325 才得到标准大气压（atm）。（此前误除以 101325，结果小 1000 倍）
        double pressureKpa = (gasMoles * R * temperature) / volume;
        return pressureKpa / 101.325;
    }

    @Override
    public double getTotalHeatCapacity() {
        return totalHeatCapacity;
    }

    @Override
    public void addThermalEnergy(double joules) {
        if (totalHeatCapacity < 1e-9) return;
        double deltaT = joules / totalHeatCapacity;
        double newTemp = temperature + deltaT;
        double oldTemp = temperature;
        this.temperature = Math.min(Math.max(0, newTemp), MAX_SAFE_TEMPERATURE);
        // 温度变化时重置平衡标记：K 随温度按范特霍夫变化，需重新评估平衡状态，
        // 否则加热/冷却后已达平衡的反应会被永久跳过
        if (Math.abs(this.temperature - oldTemp) > 1e-9) {
            resetBalancedRules();
        }
        setChanged();
        syncToClient();
    }

    @Override
    public Level getLevel() {
        return super.getLevel();
    }

    @Override
    public @NotNull BlockPos getBlockPos() {
        return super.getBlockPos();
    }

    @Override
    public boolean isRemoved() {
        return super.isRemoved();
    }

    // ==================== 内部方法 ====================

    private void recalcHeatCapacity() {
        double sum = 0;
        for (Map.Entry<IonType, Double> entry : contents.entrySet()) {
            IonType ion = entry.getKey();
            double moles = entry.getValue();
            sum += moles * ion.getSpecificHeat();
        }
        this.totalHeatCapacity = sum + ChemConfig.BEAKER_BASE_HEAT_CAPACITY.get();
        if (this.totalHeatCapacity < ChemConfig.MIN_HEAT_CAPACITY.get()) {
            this.totalHeatCapacity = ChemConfig.MIN_HEAT_CAPACITY.get();
        }
    }

    private void syncToClient() {
        if (getLevel() != null && !getLevel().isClientSide) {
            getLevel().sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
        }
    }

    /**
     * 忽略阈值（mol）：低于此量的物种视为不存在。
     * 与显示精度（%.4f）对齐——反应跑完后留下的浮点残渣会被清理，
     * 而有意义的平衡量（如碳酸根交叉产物 ~0.0015）远高于此、不受影响。
     */
    private static final double NEGLIGIBLE_AMOUNT = 1e-4;

    /**
     * 清理低于忽略阈值的物种（仅剩"已完成反应"的浮点尾巴时，让显示为诚实的 0）
     */
    private void sweepNegligibleSpecies() {
        var it = contents.entrySet().iterator();
        while (it.hasNext()) {
            if (it.next().getValue() < NEGLIGIBLE_AMOUNT) {
                it.remove();
            }
        }
    }

    /**
     * 格式化当前内容物，用于调试日志（如 "h_plus=1.0000, co2=0.5000"）
     */
    private String formatContents() {
        if (contents.isEmpty()) return "empty";
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<IonType, Double> e : contents.entrySet()) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(e.getKey().getId().getPath()).append('=')
                    .append(String.format("%.4f", e.getValue()));
        }
        return sb.toString();
    }

    // ==================== 网络同步（核心修复） ====================

    @Override
    public CompoundTag getUpdateTag() {
        // 客户端在加载时调用的更新标签（容器内容物/温度等仍需同步给客户端 HUD）
        CompoundTag tag = super.getUpdateTag();
        saveAdditional(tag);
        return tag;
    }

    @Override
    public void handleUpdateTag(CompoundTag tag) {
        // 客户端接收更新标签时调用，直接加载数据
        load(tag);
    }

    @Nullable
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        // 服务端发送更新包
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        // 客户端接收更新包时调用
        if (pkt.getTag() != null) {
            handleUpdateTag(pkt.getTag());
        }
    }

    // ==================== Tick 逻辑 ====================

    public static void tick(Level level, BlockPos pos, BlockState state, BeakerBlockEntity beaker) {
        if (level.isClientSide) return;

        beaker.detectAndApplyHeat(level, pos);

        // 环境换热（牛顿冷却，对称）：高于环境散热、低于环境回温。
        // 使用独立的环境系数（弱于热源），使烧杯有热惯量：可被热源烧热并保持住，
        // 不会瞬间弹回环境温度。修复原"只散热不回温"不对称
        double envTemp = beaker.getEnvironmentTemperature(level, pos);
        double heatDelta = ChemConfig.AMBIENT_HEAT_TRANSFER_COEFFICIENT.get() * (envTemp - beaker.temperature) * 0.05;
        if (Math.abs(heatDelta) > 1e-6) {
            beaker.addThermalEnergy(heatDelta * beaker.getTotalHeatCapacity());
        }

        ReactionEngine.tick(beaker);

        // 开口面气体交换（Phase 9）：敞口容器与上方网格格按浓度差双向交换（逸散/回吸）
        beaker.exchangeGasWithGrid(level, pos);

        // 每 20 tick（1秒）清理忽略阈值下的残渣，并输出烧杯内容物与温度（调试用）
        if (level.getGameTime() % 20 == 0) {
            beaker.sweepNegligibleSpecies();
            Main.LOGGER.debug("[Beaker] {} contents: {} | T={}K",
                    pos, beaker.formatContents(), String.format("%.1f", beaker.temperature));
        }

        // 临界温度警告节流：每 100 tick（5秒）最多输出一次，避免持续刷屏
        if (beaker.temperature >= ChemConfig.MAX_SAFE_TEMPERATURE.get() * ChemConfig.CRITICAL_TEMPERATURE_RATIO.get()
                && level.getGameTime() % 100 == 0) {
            Main.LOGGER.warn("[Beaker] Beaker at {} is reaching critical temperature! {}K",
                    pos, String.format("%.1f", beaker.temperature));
        }
    }

    private void detectAndApplyHeat(Level level, BlockPos pos) {
        BlockPos below = pos.below();
        BlockState belowState = level.getBlockState(below);
        Block belowBlock = belowState.getBlock();

        Double heatSourceTemp = HEAT_SOURCE_TEMPS.get(belowBlock);
        if (heatSourceTemp == null || heatSourceTemp <= temperature) return;

        double deltaT = ChemConfig.BEAKER_HEAT_TRANSFER_COEFFICIENT.get() * (heatSourceTemp - temperature) * 0.05;
        double heatEnergy = deltaT * totalHeatCapacity;
        addThermalEnergy(heatEnergy);
    }

    /**
     * 开口面气体交换（Phase 9 §10.3 + §9-7）：容器顶面与上方网格格按浓度差**双向**交换。
     * 单开口胞模型：烧杯 = 五面封闭的气体胞，仅顶面与上方网格格交换。
     * - 容器浓度 > 外部 → 逸散（泄漏）；
     * - 外部浓度 > 容器 → 回吸（收集，受成分上限约束）。
     * 密封/吹气等控制手段为后续扩展（改此处开口状态即可）。
     */
    protected void exchangeGasWithGrid(Level level, BlockPos pos) {
        if (!ChemConfig.GAS_CONTAINER_LEAK_ENABLED.get()) return;
        if (level.isClientSide) return;
        if (!(level instanceof ServerLevel serverLevel)) return;
        // 顶部必须开放（空气）
        if (!level.getBlockState(pos.above()).isAir()) return;
        double temp = getTemperature();
        double volL = Math.max(getVolume(), 0.001);
        double rateBase = ChemConfig.GAS_CONTAINER_LEAK_RATE.get();
        GasGridManager manager = GasGridManager.get(serverLevel);
        double maxPerComponent = ChemConfig.MAX_MOLES_PER_COMPONENT.get();
        boolean changed = false;

        // 1) 向外逸散（容器中气相；先入缓冲，累积足量再入网格，避免微量尾巴格）
        for (Map.Entry<IonType, Double> e : contents.entrySet()) {
            if (e.getKey().getPhase() != Phase.GAS) continue;
            double n = e.getValue();
            if (n <= 1e-9) continue;
            double cIn = n / volL; // mol/L
            double cOut = manager.getAmount(pos.above(), e.getKey()) / 1000.0; // 网格格 1m³ → mol/L
            if (cIn <= cOut) continue;
            double rate = rateBase
                    * Math.sqrt(29.0 / Math.max(1, e.getKey().getMolarMass()))
                    * Math.sqrt(Math.max(1, temp) / 293.0);
            double move = rate * (cIn - cOut) * volL; // → mol
            move = Math.min(move, n * 0.5);
            if (move > 1e-12) {
                removeIon(e.getKey(), move, false); // 容器持续扣减（敞口会漏光）
                gridLeakBuffer.merge(e.getKey(), move, Double::sum);
                changed = true;
            }
        }
        // 缓冲足量 → 一次性抛给网格（消散阈值 GasGridManager.dissolveThreshold()）
        if (!gridLeakBuffer.isEmpty()) {
            var it = gridLeakBuffer.entrySet().iterator();
            while (it.hasNext()) {
                var b = it.next();
                if (b.getValue() >= GasGridManager.dissolveThreshold()) {
                    manager.addGas(serverLevel, pos.above(), b.getKey(), b.getValue());
                    it.remove();
                }
            }
        }

        // 2) 向内回吸（收集：外部浓度高于容器，且容器未满）
        double totalIn = getTotalMoles();
        double totalMax = ChemConfig.MAX_TOTAL_MOLES.get();
        for (Map.Entry<IonType, Double> e : new java.util.HashMap<>(manager.get(pos.above()) != null
                ? manager.get(pos.above()).getContents() : java.util.Map.of()).entrySet()) {
            if (e.getKey().getPhase() != Phase.GAS) continue;
            double nOut = e.getValue();
            if (nOut <= 1e-9) continue;
            double cOut = nOut / 1000.0; // mol/L
            double cIn = getAmount(e.getKey()) / volL; // mol/L
            if (cOut <= cIn) continue;
            double rate = rateBase
                    * Math.sqrt(29.0 / Math.max(1, e.getKey().getMolarMass()))
                    * Math.sqrt(Math.max(1, temp) / 293.0);
            double move = rate * (cOut - cIn) * volL; // → mol（进入容器）
            // 容量约束
            move = Math.min(move, maxPerComponent - getAmount(e.getKey()));
            move = Math.min(move, totalMax - totalIn);
            move = Math.min(move, nOut * 0.5);
            if (move > 1e-12) {
                manager.removeGas(serverLevel, pos.above(), e.getKey(), move);
                addIon(e.getKey(), move, false);
                totalIn += move;
                changed = true;
            }
        }

        if (changed) {
            setChanged();
        }
    }

    /** 环境温度（K）：基准 293K，随高度递减 0.6K/100m（子类 tick 复用） */
    protected double getEnvironmentTemperature(Level level, BlockPos pos) {
        double base = ChemConfig.ENVIRONMENT_TEMPERATURE_BASE.get();
        double yFactor = Math.max(0, (pos.getY() - 64) / 100.0) * ChemConfig.ENVIRONMENT_TEMPERATURE_LAPSE_RATE.get();
        // 钳制非负：极端高 Y 时递减项不应把环境温度推到负值
        return Math.max(0, base - yFactor);
    }

    // ==================== NBT ====================

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);

        tag.putDouble("temperature", temperature);
        tag.putDouble("volume", volume);

        ListTag contentsList = new ListTag();
        for (Map.Entry<IonType, Double> entry : contents.entrySet()) {
            CompoundTag entryTag = new CompoundTag();
            entryTag.putString("id", entry.getKey().getId().toString());
            entryTag.putDouble("moles", entry.getValue());
            contentsList.add(entryTag);
        }
        tag.put("contents", contentsList);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);

        temperature = tag.getDouble("temperature");
        if (temperature <= 0) temperature = DEFAULT_TEMPERATURE;

        volume = tag.getDouble("volume");
        if (volume <= 0) volume = DEFAULT_VOLUME;

        contents.clear();
        ListTag contentsList = tag.getList("contents", Tag.TAG_COMPOUND);
        for (int i = 0; i < contentsList.size(); i++) {
            CompoundTag entryTag = contentsList.getCompound(i);
            String idStr = entryTag.getString("id");
            double moles = entryTag.getDouble("moles");
            if (moles > 1e-9) {
                ResourceLocation id = ResourceLocation.tryParse(idStr);
                if (id != null) {
                    IonType ion = ModIons.getById(id);
                    if (ion != null) {
                        contents.put(ion, moles);
                    }
                }
            }
        }

        recalcHeatCapacity();
    }

    @Override
    public void markRuleBalanced(ReactionRule rule) {
        balancedRules.add(rule);
    }

    @Override
    public boolean isRuleBalanced(ReactionRule rule) {
        return balancedRules.contains(rule);
    }

    @Override
    public void resetBalancedRules() {
        if (!balancedRules.isEmpty()) {
            balancedRules.clear();
            Main.LOGGER.debug("[Beaker] Balanced rules reset due to container change");
        }
    }

    // ==================== IFluidHandler（Forge 流体桥接，开发计划 §17.2） ====================

    private final LazyOptional<IFluidHandler> fluidHandler = LazyOptional.of(() -> this);

    /** 1 mol 对应的 mB：1 桶(1000mB) = BUCKET_TO_MOL_WATER mol（严格映射） */
    private static double mbPerMol() {
        return 1000.0 / ChemConfig.BUCKET_TO_MOL_WATER.get();
    }

    @Override
    public int getTanks() { return 1; }

    @Override
    public FluidStack getFluidInTank(int tank) {
        IonType best = null;
        double bestMoles = 0;
        for (Map.Entry<IonType, Double> e : contents.entrySet()) {
            if (FluidIonMapper.getFluidForIon(e.getKey()) != null && e.getValue() > bestMoles) {
                best = e.getKey();
                bestMoles = e.getValue();
            }
        }
        if (best == null) return FluidStack.EMPTY;
        return new FluidStack(FluidIonMapper.getFluidForIon(best), (int) (bestMoles * mbPerMol()));
    }

    @Override
    public int getTankCapacity(int tank) {
        return (int) (ChemConfig.MAX_MOLES_PER_COMPONENT.get() * mbPerMol());
    }

    @Override
    public boolean isFluidValid(int tank, FluidStack stack) {
        return FluidIonMapper.getIonForFluid(stack.getFluid()) != null;
    }

    @Override
    public int fill(FluidStack resource, FluidAction action) {
        if (resource.isEmpty()) return 0;
        IonType ion = FluidIonMapper.getIonForFluid(resource.getFluid());
        if (ion == null) return 0;
        double maxAdd = ChemConfig.MAX_MOLES_PER_COMPONENT.get() - getAmount(ion);
        double toAdd = Math.min(resource.getAmount() / mbPerMol(), maxAdd);
        if (toAdd <= 0) return 0;
        if (action == FluidAction.EXECUTE) {
            addIon(ion, toAdd);
        }
        return (int) (toAdd * mbPerMol());
    }

    @Override
    public FluidStack drain(FluidStack resource, FluidAction action) {
        if (resource.isEmpty()) return FluidStack.EMPTY;
        IonType ion = FluidIonMapper.getIonForFluid(resource.getFluid());
        if (ion == null) return FluidStack.EMPTY;
        int toDrain = (int) Math.min(resource.getAmount(), getAmount(ion) * mbPerMol());
        if (toDrain <= 0) return FluidStack.EMPTY;
        if (action == FluidAction.EXECUTE) {
            removeIon(ion, toDrain / mbPerMol());
        }
        return new FluidStack(resource.getFluid(), toDrain);
    }

    @Override
    public FluidStack drain(int maxDrain, FluidAction action) {
        IonType best = null;
        double bestMoles = 0;
        for (Map.Entry<IonType, Double> e : contents.entrySet()) {
            if (FluidIonMapper.getFluidForIon(e.getKey()) != null && e.getValue() > bestMoles) {
                best = e.getKey();
                bestMoles = e.getValue();
            }
        }
        if (best == null) return FluidStack.EMPTY;
        return drain(new FluidStack(FluidIonMapper.getFluidForIon(best), maxDrain), action);
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.FLUID_HANDLER) {
            return fluidHandler.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        fluidHandler.invalidate();
    }

    // ==================== 失败诊断记录（§19.6/19.7） ====================

    /** 容器侧保留的最近失败记录条数（仅运行时，不存 NBT） */
    private static final int MAX_RECENT_FAILURES = 10;

    private final ArrayDeque<ReactionFailure> recentFailures = new ArrayDeque<>();

    @Override
    public void recordFailure(ReactionFailure failure) {
        if (failure == null) return;
        // 仅保留运行时记录供分析仪诊断读取；不同步客户端、不触发更新包（§19.14：HUD 失败提示已移除）
        synchronized (recentFailures) {
            if (recentFailures.size() >= MAX_RECENT_FAILURES) {
                recentFailures.removeFirst();
            }
            recentFailures.addLast(failure);
        }
    }

    @Override
    public List<ReactionFailure> getRecentFailures() {
        synchronized (recentFailures) {
            return new ArrayList<>(recentFailures);
        }
    }

    // ==================== 实际执行记录（§19.7 决策 2c） ====================

    /** 容器侧保留的最近执行记录条数（仅运行时，不存 NBT）；§19.14 点 3：扩到 20 条 */
    private static final int MAX_RECENT_EXECUTED = 20;

    private final ArrayDeque<ExecutedRule> recentExecuted = new ArrayDeque<>();

    @Override
    public void recordExecutedRule(ExecutedRule record) {
        if (record == null) return;
        synchronized (recentExecuted) {
            if (recentExecuted.size() >= MAX_RECENT_EXECUTED) {
                recentExecuted.removeFirst();
            }
            recentExecuted.addLast(record);
        }
    }

    @Override
    public List<ExecutedRule> getRecentExecutedRules() {
        synchronized (recentExecuted) {
            return new ArrayList<>(recentExecuted);
        }
    }
}