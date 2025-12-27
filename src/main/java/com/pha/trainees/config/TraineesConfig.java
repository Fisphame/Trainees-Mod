//package com.pha.trainees.config;
//
//import net.minecraftforge.common.ForgeConfigSpec;
//
//public class TraineesConfig {
//    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
//    public static final ForgeConfigSpec SPEC;
//
//    // 定义配置项
//    public static final ForgeConfigSpec.IntValue ENTITY_RANDOM_MOVEMENT_MAX;
//
//    static {
//        BUILDER.push("Trainees Mod Configuration");
//
//        ENTITY_RANDOM_MOVEMENT_MAX = BUILDER
//                .comment("生成物随机运动上限")
//                .comment("这个值会影响物品实体等随机移动的范围")
//                .defineInRange("entity_random_movement_max", 10, 0, 10000);
//
//        BUILDER.pop();
//        SPEC = BUILDER.build();
//    }
//}
