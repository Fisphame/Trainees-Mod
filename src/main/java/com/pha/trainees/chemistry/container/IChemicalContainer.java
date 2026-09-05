package com.pha.trainees.chemistry.container;

import com.pha.trainees.chemistry.engine.ReactionEngine;
import com.pha.trainees.chemistry.engine.ReactionFailure;
import com.pha.trainees.chemistry.particle.IonType;
import com.pha.trainees.chemistry.particle.Phase;
import com.pha.trainees.chemistry.reaction.ReactionRule;
import com.pha.trainees.config.ChemConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import java.util.Map;
import java.util.Set;

/**
 * 化学容器接口
 * 所有可进行化学反应的方块实体（烧杯、反应釜、电解槽等）需实现此接口
 */
public interface IChemicalContainer {

    // ==================== 粒子管理 ====================

    /** 获取容器内所有粒子及其物质的量（mol） */
    Map<IonType, Double> getContents();

    /** 获取指定粒子的物质的量（mol），不存在则返回 0 */
    double getAmount(IonType ion);

    /** 添加粒子（增加物质的量），返回实际增加量 */
    double addIon(IonType ion, double moles);

    /**
     * 添加离子，可指定是否触发引擎
     * 默认行为：调用 addIon(ion, moles) 并触发引擎
     */
    default double addIon(IonType ion, double moles, boolean triggerEngine) {
        // 默认实现：直接调用原方法，由子类重写更精确的控制
        return addIon(ion, moles);
    }

    /** 移除粒子（减少物质的量），返回实际移除量（可能小于请求量） */
    double removeIon(IonType ion, double moles);

    default double removeIon(IonType ion, double moles, boolean triggerEngine) {
        return removeIon(ion, moles);
    }

    /** 检查容器是否包含指定粒子且数量足够 */
    boolean contains(IonType ion, double minMoles);

    /** 获取容器中所有粒子的种类集合 */
    Set<IonType> getPresentIons();

    // ==================== 物理状态 ====================

    /** 获取当前温度（开尔文 K） */
    double getTemperature();

    /** 设置温度（开尔文 K），通常由热力学引擎调用 */
    void setTemperature(double temperatureKelvin);

    /** 获取容器有效容积（升 L），用于浓度计算 */
    double getVolume();

    /** 设置容器容积（通常由玩家操作或机器状态改变） */
    void setVolume(double volumeLiters);

    /** 获取容器当前总压（仅对气相有效，返回 Pa 或 atm） */
    double getPressure();

    // ==================== 热力学 ====================

    /** 获取容器总热容（J/K） */
    double getTotalHeatCapacity();

    /** 向容器添加热能（焦耳 J），温度相应上升 */
    void addThermalEnergy(double joules);

    // ==================== 标识 ====================

    /** 获取容器所在的 Level */
    Level getLevel();

    /** 获取容器所在的 BlockPos */
    BlockPos getBlockPos();

    /** 检查容器是否已被破坏或移除 */
    boolean isRemoved();

    /** 标记容器需要同步更新（发给客户端） */
    void setChanged();

    // ==================== 引擎查询接口 ====================

    /**
     * 获取指定离子在当前容器中的有效浓度（mol/L），物态感知（蓝本 §4.2）：
     * - GAS / AQUEOUS：浓度 = n / volume（参与速率方程与反应商的浓度乘积）
     * - LIQUID / SOLID：活度恒为 1.0（不参与速率方程与反应商的浓度乘积）
     * 消耗/生成的数量判断仍基于 getAmount()（摩尔数），不受此方法影响。
     */
    default double getEffectiveConcentration(IonType ion) {
        Phase phase = ion.getPhase();
        if (phase == Phase.LIQUID || phase == Phase.SOLID) {
            return 1.0;
        }
        double volume = getVolume();
        if (volume <= 0) return 0;
        return getAmount(ion) / volume;
    }

    /**
     * 检查容器中是否所有反应物种类都存在（非零量）。
     * 注意：只检查"存在"，不要求达到完整计量数——具体每次消耗量由引擎按 Δξ 缩放，
     * 计量数 >1 的反应（如 2NaCl → 2Na + Cl₂）在存量低于完整计量数时仍应能继续部分反应。
     */
    default boolean containsAll(Map<IonType, Integer> requirements) {
        for (Map.Entry<IonType, Integer> entry : requirements.entrySet()) {
            if (getAmount(entry.getKey()) <= 1e-9) {
                return false;
            }
        }
        return true;
    }

    /**
     * 获取容器内所有粒子的总摩尔数
     */
    default double getTotalMoles() {
        return getContents().values().stream().mapToDouble(Double::doubleValue).sum();
    }

    // ==================== 电解/通电（蓝本 §17） ====================

    /**
     * 容器当前是否通电。
     * 理论阶段由配置 ELECTROLYZER_FREE_POWER 决定（无限电调试）；
     * 未来接入机器能量系统后，由容器的 IEnergyStorage 决定（覆盖此方法）。
     */
    default boolean isElectricallyPowered() {
        return ChemConfig.ELECTROLYZER_FREE_POWER.get();
    }

    /**
     * 消耗电能（kJ），返回是否成功扣除。
     * 理论阶段无限电时恒成功；未来接入能量系统后从 IEnergyStorage 扣减并返回结果。
     */
    default boolean consumeElectricalEnergy(double kilojoules) {
        return isElectricallyPowered();
    }

    // ==================== 平衡状态管理（用于引擎） ====================

    /**
     * 标记某个反应规则在容器中已达到平衡（暂不触发）
     */
    void markRuleBalanced(ReactionRule rule);

    /**
     * 检查某个反应规则是否已被标记为平衡状态
     */
    boolean isRuleBalanced(ReactionRule rule);

    /**
     * 重置该容器中所有规则的平衡状态
     * 当容器成分发生变化时（添加/移除粒子），应调用此方法
     */
    void resetBalancedRules();

    // ==================== 失败诊断记录（§19.6/19.7） ====================

    /**
     * 记录一次规则执行失败（供失败反馈/反应预测器使用）。
     * 默认实现不记录；烧杯等容器可覆写为保留最近 N 条（仅运行时，不存 NBT）。
     */
    default void recordFailure(ReactionFailure failure) {
    }

    /**
     * 获取本容器最近的失败记录（最旧在前）。
     * 默认实现返回空列表。
     */
    default java.util.List<ReactionFailure> getRecentFailures() {
        return java.util.List.of();
    }

    // ==================== 产物处理钩子（§19.10 电解槽） ====================

    /**
     * 引擎在应用反应产物时调用：容器可自行处理该产物（如电解槽按电极/膜分拣导出）。
     * @return true = 容器已处理该产物（引擎不再 addIon 入 contents、不触发连锁入队）；
     *         false = 默认行为（引擎正常 addIon 入 contents 并视出边入队）。
     * 烧杯不覆写此方法，行为零变化。
     */
    default boolean onProductGenerated(ReactionRule rule, IonType ion, double moles) {
        return false;
    }
}