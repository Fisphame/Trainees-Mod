package com.pha.trainees.event;

import com.pha.trainees.registry.ModItems;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class SweepHandler {
    @SubscribeEvent
    public static void onSweepAttack(AttackEntityEvent event) {
        Player player = event.getEntity();
        ItemStack stack = player.getMainHandItem();

        // 检查是否持有自定义镰刀
        if (stack.getItem() == ModItems.KUN_SCYTHE.get()) {
            // 强制触发横扫粒子效果
            if (player.level() instanceof ServerLevel serverLevel) {
                Vec3 pos = event.getTarget().position();
                serverLevel.sendParticles(
                        ParticleTypes.SWEEP_ATTACK,
                        pos.x, pos.y + 1.0, pos.z,
                        15, // 粒子数量
                        1, 1, 1,
                        0.0
                );
            }
        }

        else if (stack.getItem() == ModItems.KUN_COMPOUND_SCYTHE.get()) {
            // 强制触发横扫粒子效果
            if (player.level() instanceof ServerLevel serverLevel) {
                Vec3 pos = event.getTarget().position();
                serverLevel.sendParticles(
                        ParticleTypes.SWEEP_ATTACK,
                        pos.x, pos.y + 1.0, pos.z,
                        30, // 粒子数量
                        2,2, 2,
                        0.0
                );
            }
        }


    }
}
