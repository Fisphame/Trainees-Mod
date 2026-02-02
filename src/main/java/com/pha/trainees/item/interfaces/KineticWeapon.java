package com.pha.trainees.item.interfaces;


import com.pha.trainees.util.physics.KineticEnergySystem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public interface KineticWeapon {
    /**
     * 更新动能（在inventoryTick中调用）
     */
    default void updateKineticEnergy(Level level, Entity entity, ItemStack stack, boolean isSelected) {
        if (!level.isClientSide && entity instanceof Player player && isSelected) {
            KineticEnergySystem.updateKineticEnergy(player, stack);
        }
    }

    /**
     * 应用动能伤害（在hurtEnemy中调用）
     */
    default float applyKineticDamage(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (!attacker.level().isClientSide() && attacker instanceof Player player) {
            float damageMultiplier = KineticEnergySystem.consumeKineticEnergy(stack);
            float baseDamage = ((net.minecraft.world.item.SwordItem) stack.getItem()).getDamage();
            float bonusDamage = baseDamage * damageMultiplier;
            if (bonusDamage > 0) {
                target.hurt(attacker.damageSources().playerAttack(player), bonusDamage);
            }
            return bonusDamage;
        }
        return 0;
    }

    /**
     * 默认的动能更新状态键名
     */
    static String getKineticUpdateEnabledKey() {
        return "KineticUpdateEnabled";
    }

    /**
     * 检查动能更新是否启用（默认实现）
     */
    static boolean isKineticUpdateEnabled(ItemStack stack) {
        if (!stack.hasTag()) {
            return true;
        }
        CompoundTag tag = stack.getTag();
        String key = getKineticUpdateEnabledKey();
        return !tag.contains(key) || tag.getBoolean(key);
    }

    /**
     * 设置动能更新状态（默认实现）
     */
    static void setKineticUpdateEnabled(ItemStack stack, boolean enabled) {
        CompoundTag tag = stack.getOrCreateTag();
        tag.putBoolean(getKineticUpdateEnabledKey(), enabled);
    }
}
