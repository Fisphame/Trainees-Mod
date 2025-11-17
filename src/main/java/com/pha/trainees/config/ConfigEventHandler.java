//package com.pha.trainees.config;
//
//import com.pha.trainees.Main;
//import net.minecraftforge.eventbus.api.SubscribeEvent;
//import net.minecraftforge.fml.common.Mod;
//import net.minecraftforge.fml.event.config.ModConfigEvent;
//
//@Mod.EventBusSubscriber(modid = Main.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
//public class ConfigEventHandler {
//
//    @SubscribeEvent
//    public static void onConfigLoading(ModConfigEvent.Loading event) {
//        if (event.getConfig().getModId().equals(Main.MODID)) {
//            Main.LOGGER.info("Trainees config loading...");
//        }
//    }
//
//    @SubscribeEvent
//    public static void onConfigLoaded(ModConfigEvent.Loading event) {
//        if (event.getConfig().getModId().equals(Main.MODID)) {
//            // 配置加载完成，设置标志
//            ConfigManager.setConfigLoaded();
//            Main.LOGGER.info("Trainees config loaded!");
//        }
//    }
//
//    @SubscribeEvent
//    public static void onConfigReloading(ModConfigEvent.Reloading event) {
//        if (event.getConfig().getModId().equals(Main.MODID)) {
//            Main.LOGGER.info("Trainees config reloading...");
//        }
//    }
//
//    @SubscribeEvent
//    public static void onConfigReloaded(ModConfigEvent.Reloading event) {
//        if (event.getConfig().getModId().equals(Main.MODID)) {
//            Main.LOGGER.info("Trainees config reloaded!");
//        }
//    }
//}