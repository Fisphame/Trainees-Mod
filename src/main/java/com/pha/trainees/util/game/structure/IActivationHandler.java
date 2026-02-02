package com.pha.trainees.util.game.structure;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public interface IActivationHandler {
    /**
     * 结构激活时调用
     */
    void onActivate(Level level, BlockPos matchPos);

    /**
     * 预激活检查
     */
    default boolean preActivateCheck(Level level, BlockPos matchPos) {
        return true;
    }

    /**
     * 激活失败时调用
     */
    default void onActivationFailed(Level level, BlockPos matchPos, String reason) {
        // 默认不执行任何操作
    }

    /**
     * 激活结构被破坏时调用
     */
    default void onStructureBroken(Level level, BlockPos matchPos) {
        // 默认不执行任何操作
    }

    /**
     * 每tick更新激活状态（用于粒子效果等）
     */
    default void onActiveTick(Level level, BlockPos matchPos, long activeTime) {
        // 默认不执行任何操作
    }

    /**
     * 获取粒子效果的tick间隔
     */
    default int getParticleTickInterval() {
        return 20; // 默认每秒一次
    }

    /**
     * 是否允许重新激活已激活的结构
     */
    default boolean allowReactivate() {
        return false;
    }
}
