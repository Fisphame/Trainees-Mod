package com.pha.trainees.item;


import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;


public class AuriversiteRapierItem extends SwordItem {
    public AuriversiteRapierItem(Tier p_43269_, int p_43270_, float p_43271_, Properties p_43272_) {
        super(p_43269_, p_43270_, p_43271_, p_43272_);
    }

    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, Player player, @NotNull InteractionHand interaction) {
        ItemStack stack = player.getItemInHand(interaction);
        Vec3 look = player.getLookAngle();

        look = new Vec3(look.x, 0, look.z).normalize();
        player.setDeltaMovement(look);
        player.awardStat(Stats.ITEM_USED.get(this));
        player.getCooldowns().addCooldown(this, 15);
        if (!player.isCreative()) {
            stack.hurtAndBreak(1, player, (e) -> e.broadcastBreakEvent(EquipmentSlot.MAINHAND));
        }

        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

}
