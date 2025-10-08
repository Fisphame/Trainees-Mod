package com.pha.trainees.event;

import com.pha.trainees.registry.ModItems;
import com.pha.trainees.registry.Something;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class SweepHandler {
    @SubscribeEvent
    public static void onSweepAttack(AttackEntityEvent event) {
        Player player = event.getEntity();
        ItemStack stack = player.getMainHandItem();
        int[] SweepNum = {0, 30, 60, 200}, SweepArea = {0, 2, 4, 6};
        Item item = stack.getItem(),
                S1 = ModItems.KUN_SCYTHE.get() ,
                S2 = ModItems.KUN_COMPOUND_SCYTHE.get() ,
                S3 = Something.PrankItems.POWDER_ANTI_99_SCYTHE.get();

        if(item == S1 || item == S2 || item == S3){
            if (player.level() instanceof ServerLevel serverLevel) {
                Vec3 pos = event.getTarget().position();
                int p = item == S1 ? 1 : (item == S2 ? 2 : 3);
                serverLevel.sendParticles(
                        ParticleTypes.SWEEP_ATTACK, pos.x, pos.y + 1.0, pos.z,
                        SweepNum[p], SweepArea[p], SweepArea[p], SweepArea[p],
                        0.0
                );
            }
        }
    }
}
