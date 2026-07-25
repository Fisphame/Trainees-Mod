package com.pha.trainees.chemistry.container;

import com.pha.trainees.chemistry.particle.IonType;
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

    /** 移除粒子（减少物质的量），返回实际移除量（可能小于请求量） */
    double removeIon(IonType ion, double moles);

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
}