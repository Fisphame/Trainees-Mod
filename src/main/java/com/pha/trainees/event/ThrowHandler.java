package com.pha.trainees.event;

import com.pha.trainees.entity.BasketballEntity;
import com.pha.trainees.registry.ModItems;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ThrowHandler {
    @SubscribeEvent
    public static void onRightClickItem(PlayerInteractEvent.RightClickItem event) {
        Player player = event.getEntity();
        ItemStack stack = player.getItemInHand(event.getHand());

        if (stack.getItem() == ModItems.KUN_BASKETBALL.get()) {
            event.setCanceled(true);

            if (!player.level().isClientSide) {
                BasketballEntity ball = new BasketballEntity(
                        player.level(),
                        player // 传递所有者
                );

                // 设置初始位置和速度
                ball.shootFromRotation(
                        player,
                        player.getXRot(),
                        player.getYRot(),
                        0.0F,
                        1.5F, // 初始速度
                        1.0F  // 精度偏移
                );

                player.level().addFreshEntity(ball);
                if (!player.isCreative()) stack.shrink(1);
            }
        }
    }
}