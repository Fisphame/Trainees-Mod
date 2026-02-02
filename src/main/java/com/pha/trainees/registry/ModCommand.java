package com.pha.trainees.registry;

import net.minecraft.world.level.GameRules;

public class ModCommand {

    // 定义游戏规则键
    public static GameRules.Key<GameRules.BooleanValue> ENCHANT_BROKEN_BLOCK_DROP;
    public static GameRules.Key<GameRules.BooleanValue> ENCHANT_BREAK_BLOCK;
    public static GameRules.Key<GameRules.BooleanValue> ALLOW_REACTION_EXPLODE;

    // 注册方法
    public static void register() {
        ENCHANT_BROKEN_BLOCK_DROP = GameRules.register(
                "enchantbrokenblockdrop",
                GameRules.Category.PLAYER,
                createBooleanRule(false)
        );

        ENCHANT_BREAK_BLOCK = GameRules.register(
                "enchantbreakblock",
                GameRules.Category.PLAYER,
                createBooleanRule(false)
        );

        ALLOW_REACTION_EXPLODE = GameRules.register(
                "allowreactionexplode",
                GameRules.Category.PLAYER,
                createBooleanRule(true)
        );
    }

    // 创建布尔规则
    private static GameRules.Type<GameRules.BooleanValue> createBooleanRule(boolean defaultValue) {
        return GameRules.BooleanValue.create(defaultValue);
    }
}