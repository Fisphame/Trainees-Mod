package com.pha.trainees.item.interfaces;

import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public interface Chargeable {
    default InteractionResultHolder<ItemStack> charge(Level level, Player player, InteractionHand hand){
        ItemStack stack = player.getItemInHand(hand);
        Vec3 look = player.getLookAngle();

        look = new Vec3(look.x, 0, look.z).normalize();
        player.setDeltaMovement(look);
        player.awardStat(Stats.ITEM_USED.get(stack.getItem()));
        player.getCooldowns().addCooldown(stack.getItem(), getChargeCooldown());
        if (consumesDurabilityOnCharge(player)) {
            stack.hurtAndBreak(1, player, (e) -> e.broadcastBreakEvent(EquipmentSlot.MAINHAND));
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    default int getChargeCooldown() {
        return 20;
    }

    default boolean consumesDurabilityOnCharge(Player player) {
        return !player.isCreative();
    }

}
