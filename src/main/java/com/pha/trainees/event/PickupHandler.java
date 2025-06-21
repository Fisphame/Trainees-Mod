//package com.pha.trainees.event;
//
//import com.pha.trainees.entity.BasketballEntity;
//import com.pha.trainees.registry.ModItems;
//import net.minecraft.world.entity.player.Player;
//import net.minecraft.world.item.ItemStack;
//import net.minecraftforge.event.TickEvent;
//import net.minecraftforge.eventbus.api.SubscribeEvent;
//
//public class PickupHandler {
//    @SubscribeEvent
//    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
//        if (event.phase != TickEvent.Phase.END) return;
//
//        Player player = event.player;
//        player.level().getEntitiesOfClass(
//                BasketballEntity.class,
//                player.getBoundingBox().inflate(1.5),
//                ball -> ball.isOnGround() && ball.distanceTo(player) < 1.0
//        ).forEach(ball -> {
//            if (!player.addItem(new ItemStack(ModItems.KUN_BASKETBALL.get()))) {
//                ball.spawnAtLocation(ModItems.KUN_BASKETBALL.get());
//            }
//            ball.discard();
//        });
//    }
//}