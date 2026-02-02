package com.pha.trainees.util.game.structure;

import com.pha.trainees.Main;
import com.pha.trainees.util.game.Booleanf;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;


import javax.annotation.Nullable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class MultiblockStructure {
    public static final Map<String, MultiblockPattern> REGISTERED_STRUCTURES = new HashMap<>();
    private static final Map<String, Long> COOLDOWNS = new HashMap<>();

    // 冷却时间配置（毫秒）
    public static final long DEFAULT_COOLDOWN = 5000;

    /**
     * 注册一个多方块结构
     */
    public static void registerStructure(String id, MultiblockPattern pattern) {
        if (REGISTERED_STRUCTURES.containsKey(id)) {
           Main.LOGGER.warn("Structure with id '{}' already registered, overwriting", id);
        }
        REGISTERED_STRUCTURES.put(id, pattern);
        Main.LOGGER.debug("Registered multiblock structure: {}", id);
    }

    /**
     * 检查某个位置是否存在特定的多方块结构
     */
    @Nullable
    public static MultiblockPattern.MatchResult checkStructure(Level level, BlockPos pos, String structureId) {
        MultiblockPattern pattern = REGISTERED_STRUCTURES.get(structureId);
        if (pattern == null) {
            Main.LOGGER.warn("Unknown structure id: {}", structureId);
            return null;
        }

        // 检查冷却
        String cooldownKey = structureId + ":" + pos.asLong();
        if (isOnCooldown(cooldownKey)) {
            return null;
        }

        return pattern.check(level, pos);
    }

    /**
     * 尝试激活结构
     */
    public static Booleanf tryActivateStructure(Level level, BlockPos pos, String structureId, @Nullable Player player) {

        // 检查结构是否存在
        MultiblockPattern pattern = REGISTERED_STRUCTURES.get(structureId);
        if (pattern == null) {
            return new Booleanf(false, 1);
        }

        // 检查冷却
        String cooldownKey = structureId + ":" + pos.asLong();
        if (isOnCooldown(cooldownKey)) {
            return new Booleanf(false, 2);
        }

        // 检查是否已激活
        ActiveStructureManager manager = ActiveStructureManager.get(level);
        if (!manager.shouldActivate(level, structureId, pos)) {

            return new Booleanf(false, 4);
        }


        MultiblockPattern.MatchResult match = pattern.check(level, pos);

        if (match != null) {
            if (pattern.getActivationHandler() != null) {
                // 预检查
                if (!pattern.getActivationHandler().preActivateCheck(level, match.matchPos)) {
                    return new Booleanf(false, 3);
                }

                // 激活结构
                pattern.getActivationHandler().onActivate(level, match.matchPos);

                // 设置冷却
                COOLDOWNS.put(cooldownKey, System.currentTimeMillis());

                return new Booleanf(true, 1);
            }
        }
        else if (player != null && pattern.getActivationHandler() != null) {
                // 激活失败，通知玩家
                pattern.getActivationHandler().onActivationFailed(level, pos, "Structure incomplete");
        }

        return new Booleanf(false, 0);
    }

    /**
     * 查找附近的结构
     */
    @Nullable
    public static MultiblockPattern.MatchResult findNearbyStructure(Level level, BlockPos centerPos, String structureId, int radius) {
        MultiblockPattern pattern = REGISTERED_STRUCTURES.get(structureId);
        if (pattern == null) return null;

        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    BlockPos checkPos = centerPos.offset(x, y, z);
                    MultiblockPattern.MatchResult result = pattern.check(level, checkPos);
                    if (result != null) {
                        return result;
                    }
                }
            }
        }
        return null;
    }

    /**
     * 获取所有注册的结构ID
     */
    public static Set<String> getAllStructureIds() {
        return Collections.unmodifiableSet(REGISTERED_STRUCTURES.keySet());
    }

    /**
     * 清除所有注册的结构
     */
    public static void clearAll() {
        REGISTERED_STRUCTURES.clear();
        COOLDOWNS.clear();
        Main.LOGGER.debug("Cleared all multiblock structures");
    }

    /**
     * 检查是否在冷却中
     */
    private static boolean isOnCooldown(String key) {
        Long lastActivation = COOLDOWNS.get(key);
        if (lastActivation == null) return false;

        long currentTime = System.currentTimeMillis();
        return (currentTime - lastActivation) < DEFAULT_COOLDOWN;
    }

    /**
     * 清理过期的冷却记录
     */
    public static void cleanupCooldowns() {
        long currentTime = System.currentTimeMillis();
        COOLDOWNS.entrySet().removeIf(entry ->
                (currentTime - entry.getValue()) >= DEFAULT_COOLDOWN
        );
    }
}

