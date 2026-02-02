package com.pha.trainees.item;

import com.pha.trainees.item.interfaces.Chargeable;
import com.pha.trainees.item.interfaces.KineticWeapon;
import com.pha.trainees.util.game.Tools;
import com.pha.trainees.util.math.MAth;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class AuriversiteRapierItem extends SwordItem implements Chargeable, KineticWeapon {
    public static final String TAG_KINETIC_UPDATE_ENABLED = "KineticUpdateEnabled";

    public AuriversiteRapierItem(Tier tier, int attackDamage, float attackSpeed, Properties properties) {
        super(tier, attackDamage, attackSpeed, properties);
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        float damage = applyKineticDamage(stack, target, attacker);
        if (KineticWeapon.isKineticUpdateEnabled(stack) && attacker instanceof Player player) {
            Tools.Particle.send(player.level(), ParticleTypes.LAVA, target.getX(), target.getY(), target.getZ(),
                    MAth.inInterval((int) (damage * 2.5f), 10, 500), 0.1, 0.1, 0.1, 0.1);
        }

        return super.hurtEnemy(stack, target, attacker);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        super.inventoryTick(stack, level, entity, slotId, isSelected);

        // 只在服务器端更新动能
        if (!level.isClientSide) {
            // 根据NBT状态决定是否更新动能
            if (KineticWeapon.isKineticUpdateEnabled(stack)) {
                updateKineticEnergy(level, entity, stack, isSelected);
            }
        }
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, Player player, @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (!player.isCrouching()) {
            return charge(level, player, hand);
        }
        if (!level.isClientSide()) {
            boolean currentState = KineticWeapon.isKineticUpdateEnabled(stack);
            boolean newState = !currentState;
            KineticWeapon.setKineticUpdateEnabled(stack, newState);

            Component message = Component.literal(newState ? "--[ - ]--" : "--[ x ]--")
                    .withStyle(newState ? ChatFormatting.GREEN : ChatFormatting.RED);
            player.displayClientMessage(message, true);
            player.playSound(SoundEvents.UI_BUTTON_CLICK.get(), 0.5f, 1.0f);
        }

        player.swing(hand); // 播放挥手动画

        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());

    }

    @Override
    public int getChargeCooldown() {
        return 15;
    }

    /**
     * 切换物品的动能更新状态
     * @return 切换后的新状态
     */
    public static boolean toggleKineticUpdateEnabled(ItemStack stack) {
        boolean current = KineticWeapon.isKineticUpdateEnabled(stack);
        boolean newState = !current;
        KineticWeapon.setKineticUpdateEnabled(stack, newState);
        return newState;
    }

    @Override
    public void appendHoverText(ItemStack stack, @javax.annotation.Nullable Level level, List<Component> tooltipComponents, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltipComponents, flag);

        if (flag.isAdvanced()) {
            boolean enabled = KineticWeapon.isKineticUpdateEnabled(stack);
            tooltipComponents.add(Component.translatable("tooltip.trainees.auriversite_rapier"));
            tooltipComponents.add(Component.translatable("tooltip.trainees.auriversite_rapier.2.1"));
            tooltipComponents.add(Component.translatable("tooltip.trainees.auriversite_rapier.2.2"));
            tooltipComponents.add(Component.translatable("tooltip.trainees.auriversite_rapier.state")
                    .append(Component.translatable(enabled ? "tooltip.trainees.auriversite_rapier.on" :
                                    "tooltip.trainees.auriversite_rapier.off")
                            .withStyle(enabled ? ChatFormatting.GREEN : ChatFormatting.RED)));
        } else {
            tooltipComponents.add(Component.translatable("tooltip.trainees.item.press_shift"));
        }
    }
}