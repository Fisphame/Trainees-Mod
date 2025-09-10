package com.pha.trainees.item;

import com.pha.trainees.Main;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

import javax.annotation.Nullable;
import java.util.List;

public class ScytheCourseItem {
    public static class ScytheItem extends SwordItem {
        public ScytheItem(Tier tier, int attackDamage, float attackSpeed, Properties properties) {
            super(tier, attackDamage, attackSpeed, properties);
        }
        private static final float SWEEP_RADIUS = 2F; // 横扫范围（原版为1.0）
        private static final float SWEEP_DAMAGE_MULTIPLIER = 2F; // 伤害倍率（原版为1.0）

        @Override
        public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
            // 仅在主线程处理，避免客户端逻辑干扰
            if (!attacker.level().isClientSide && attacker instanceof Player player) {
                // 检测是否满足横扫条件（原版逻辑）
                float attackStrength = player.getAttackStrengthScale(0.5F);
                if (attackStrength > 0.9F) {
                    // 获取范围内的所有生物
                    AABB area = target.getBoundingBox().inflate(SWEEP_RADIUS, 0.25, SWEEP_RADIUS);
                    List<LivingEntity> entities = player.level().getEntitiesOfClass(
                            LivingEntity.class,
                            area,
                            e -> e != player && e != target && !e.isAlliedTo(player)
                    );

                    // 对每个生物应用伤害
                    for (LivingEntity entity : entities) {
                        float baseDamage = (float) player.getAttributeValue(Attributes.ATTACK_DAMAGE);
                        float sweepDamage = baseDamage * SWEEP_DAMAGE_MULTIPLIER;
                        entity.hurt(player.damageSources().playerAttack(player), sweepDamage);
                    }
                }
            }
            return super.hurtEnemy(stack, target, attacker);
        }

        @Override
        public void appendHoverText(ItemStack stack, @Nullable Level level,
                                    List<Component> tooltipComponents, TooltipFlag flag) {
            super.appendHoverText(stack, level, tooltipComponents, flag);

            // 添加本地化的工具提示
            tooltipComponents.add(Component.translatable("tooltip.trainees.scythe_item"));
            tooltipComponents.add(Component.translatable("tooltip.trainees.scythe_item.2"));
        }
    }

    public static class CompoundScytheItem extends SwordItem {
        public CompoundScytheItem(Tier tier, int attackDamage, float attackSpeed, Properties properties) {
            super(tier, attackDamage, attackSpeed, properties);
        }
        private static final float SWEEP_RADIUS = 4F; // 横扫范围（原版为1.0）
        private static final float SWEEP_DAMAGE_MULTIPLIER = 6F; // 伤害倍率（原版为1.0）

        @Override
        public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
            // 仅在主线程处理，避免客户端逻辑干扰
            if (!attacker.level().isClientSide && attacker instanceof Player player) {
                // 检测是否满足横扫条件（原版逻辑）
                float attackStrength = player.getAttackStrengthScale(0.5F);
                if (attackStrength > 0.9F) {
                    // 获取范围内的所有生物
                    AABB area = target.getBoundingBox().inflate(SWEEP_RADIUS, 0.25, SWEEP_RADIUS);
                    List<LivingEntity> entities = player.level().getEntitiesOfClass(
                            LivingEntity.class,
                            area,
                            e -> e != player && e != target && !e.isAlliedTo(player)
                    );

                    // 对每个生物应用伤害
                    for (LivingEntity entity : entities) {
                        float baseDamage = (float) player.getAttributeValue(Attributes.ATTACK_DAMAGE);
                        float sweepDamage = baseDamage * SWEEP_DAMAGE_MULTIPLIER;
                        entity.hurt(player.damageSources().playerAttack(player), sweepDamage);
                    }
                }
            }
            return super.hurtEnemy(stack, target, attacker);
        }

        @Override
        public void appendHoverText(ItemStack stack, @Nullable Level level,
                                    List<Component> tooltipComponents, TooltipFlag flag) {
            super.appendHoverText(stack, level, tooltipComponents, flag);

            // 添加本地化的工具提示
            tooltipComponents.add(Component.translatable("tooltip.trainees.compound_scythe_item"));
            tooltipComponents.add(Component.translatable("tooltip.trainees.compound_scythe_item.2"));
        }
    }

    public static class BlackPowderScytheItem extends SwordItem {
        public BlackPowderScytheItem(Tier tier, int attackDamage, float attackSpeed, Properties properties) {
            super(tier, attackDamage, attackSpeed, properties);
        }
        private static final float SWEEP_RADIUS = Main.MATH99; // 横扫范围（原版为1.0）
        private static final float SWEEP_DAMAGE_MULTIPLIER = Main.MATH99; // 伤害倍率（原版为1.0）

        @Override
        public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
            // 仅在主线程处理，避免客户端逻辑干扰
            if (!attacker.level().isClientSide && attacker instanceof Player player) {
                // 检测是否满足横扫条件（原版逻辑）
                float attackStrength = player.getAttackStrengthScale(0.5F);
                if (attackStrength > 0.9F) {
                    // 获取范围内的所有生物
                    AABB area = target.getBoundingBox().inflate(SWEEP_RADIUS, 0.25, SWEEP_RADIUS);
                    List<LivingEntity> entities = player.level().getEntitiesOfClass(
                            LivingEntity.class,
                            area,
                            e -> e != player && e != target && !e.isAlliedTo(player)
                    );

                    // 对每个生物应用伤害
                    for (LivingEntity entity : entities) {
                        float baseDamage = (float) player.getAttributeValue(Attributes.ATTACK_DAMAGE);
                        float sweepDamage = baseDamage * SWEEP_DAMAGE_MULTIPLIER;
                        entity.hurt(player.damageSources().playerAttack(player), sweepDamage);
                    }
                }
            }
            return super.hurtEnemy(stack, target, attacker);
        }

        @Override
        public void appendHoverText(ItemStack stack, @Nullable Level level,
                                    List<Component> tooltipComponents, TooltipFlag flag) {
            super.appendHoverText(stack, level, tooltipComponents, flag);

            // 添加本地化的工具提示
            tooltipComponents.add(Component.translatable("tooltip.trainees.black_powder_scythe_item"));
            tooltipComponents.add(Component.translatable("tooltip.trainees.black_powder_scythe_item.2"));
        }
    }
}
