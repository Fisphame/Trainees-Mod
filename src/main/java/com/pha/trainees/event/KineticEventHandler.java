//package com.pha.trainees.event;
//
//import com.pha.trainees.item.AuriversiteRapierItem;
//import net.minecraft.world.entity.player.Player;
//import net.minecraft.world.item.ItemStack;
//import net.minecraftforge.event.TickEvent;
//import net.minecraftforge.event.entity.living.LivingHurtEvent;
//import net.minecraftforge.eventbus.api.SubscribeEvent;
//import net.minecraftforge.fml.common.Mod;
//
//import java.util.HashMap;
//import java.util.Map;
//import java.util.UUID;
//
//@Mod.EventBusSubscriber
//public class KineticEventHandler {
//
//    // 存储玩家上次检查的tick，避免每次tick都检查
//    private static final Map<UUID, Integer> lastProcessedTick = new HashMap<>();
//
//    /**
//     * 玩家tick事件，定期更新动能
//     */
//    @SubscribeEvent
//    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
//        if (event.phase != TickEvent.Phase.END) {
//            return;
//        }
//
//        Player player = event.player;
//
//        // 只在服务器端处理
//        if (player.level().isClientSide) {
//            return;
//        }
//
//        // 检查主手物品是否为动能剑
//        ItemStack mainHand = player.getMainHandItem();
//        if (mainHand.getItem() instanceof AuriversiteRapierItem) {
//            // 让物品自身的inventoryTick处理动能更新
//            // 这里不需要额外处理，因为AuriversiteRapierItem的inventoryTick已经处理了
//        }
//    }
//
//    /**
//     * 受伤事件，处理动能额外伤害（备用方案）
//     */
//    @SubscribeEvent
//    public static void onLivingHurt(LivingHurtEvent event) {
//        // 如果伤害来源是玩家
//        if (event.getSource().getEntity() instanceof Player player) {
//            ItemStack weapon = player.getMainHandItem();
//
//            // 检查是否是动能剑
//            if (weapon.getItem() instanceof AuriversiteRapierItem) {
//                // 注意：这个事件处理是备用的，主要逻辑在AuriversiteRapierItem的hurtEnemy方法中
//                // 这里可以添加一些额外效果，比如粒子效果、声音等
//            }
//        }
//    }
//}
