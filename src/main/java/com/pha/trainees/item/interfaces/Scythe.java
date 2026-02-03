package com.pha.trainees.item.interfaces;

import com.pha.trainees.util.math.MAth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;

import java.util.List;

public interface Scythe {
    float[] SWEEP_RADIUS = {2f, 4f, MAth.MATH99};
    float[] SWEEP_DAMAGE_MULTIPLIER = {2f, 6f, MAth.MATH99};

    default void attack(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (!attacker.level().isClientSide && attacker instanceof Player player) {
            // 检测是否满足横扫条件（原版逻辑）
            float attackStrength = player.getAttackStrengthScale(0.5F);
            if (attackStrength > 0.9F) {
                // 获取范围内的所有生物
                AABB area = target.getBoundingBox().inflate(getSweepRadius(), getSweepHigh(), getSweepRadius());
                List<LivingEntity> entities = player.level().getEntitiesOfClass(
                        LivingEntity.class, area,
                        e -> e != player && e != target && !e.isAlliedTo(player)
                );

                // 对每个生物应用伤害
                for (LivingEntity entity : entities) {
                    float baseDamage = (float) player.getAttributeValue(Attributes.ATTACK_DAMAGE);
                    float sweepDamage = baseDamage * getSweepDamageMultiplier();
                    entity.hurt(player.damageSources().playerAttack(player), sweepDamage);
                }
            }
        }
    }

    default boolean onHurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        attack(stack, target, attacker);
        return true;
    }

    int getTierIndex();

    default float getSweepRadius() {
        int index = getTierIndex();
        return index < SWEEP_RADIUS.length ? SWEEP_RADIUS[index] : 2f;
    }

    default float getSweepDamageMultiplier() {
        int index = getTierIndex();
        return index < SWEEP_DAMAGE_MULTIPLIER.length ? SWEEP_DAMAGE_MULTIPLIER[index] : 2f;
    }

    default float getSweepHigh(){
        return 0.25f;
    }
}
