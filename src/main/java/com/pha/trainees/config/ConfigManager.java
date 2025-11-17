//package com.pha.trainees.config;
//
//import com.pha.trainees.Main;
//
//public class ConfigManager {
//    private static Integer cachedMovementMax = null;
//
//    public static int getEntityRandomMovementMax() {
//        if (cachedMovementMax == null) {
//            try {
//                cachedMovementMax = TraineesConfig.ENTITY_RANDOM_MOVEMENT_MAX.get();
//            } catch (IllegalStateException e) {
//                // 如果配置未加载，使用默认值并记录警告
//                Main.LOGGER.warn("Config not available, using default value: 10");
//                cachedMovementMax = 10;
//            }
//        }
//        return cachedMovementMax;
//    }
//
//    // 配置重载时清除缓存
//    public static void clearCache() {
//        cachedMovementMax = null;
//    }
//}