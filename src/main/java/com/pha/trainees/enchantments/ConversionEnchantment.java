package com.pha.trainees.enchantments;

import com.pha.trainees.item.interfaces.KineticWeapon;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import org.jetbrains.annotations.NotNull;

import static com.pha.trainees.enchantments.KineticEnchantmentCategory.KINETIC_WEAPON;

public class ConversionEnchantment extends Enchantment {
    // 每级增加的伤害转化系数
    public static final float DAMAGE_FACTOR_BONUS_PER_LEVEL = 4.0f;

    public ConversionEnchantment(Rarity rarity) {
        super(rarity, KINETIC_WEAPON, new EquipmentSlot[]{
                EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND
        });
    }

    @Override
    public int getMaxLevel() {
        return 5;
    }

    @Override
    public int getMinCost(int level) {

        return 10 + (level - 1) * 10;
    }

    @Override
    public int getMaxCost(int level) {
        // 附魔的最大经验成本
        return getMinCost(level) + 20;
    }

    @Override
    public boolean canEnchant(ItemStack stack) {
        // 只允许附魔在动能武器上
        return stack.getItem() instanceof KineticWeapon;
    }

    @Override
    public boolean canApplyAtEnchantingTable(@NotNull ItemStack stack) {
        // 可以在附魔台上附魔
        return canEnchant(stack);
    }

    @Override
    public boolean isAllowedOnBooks() {
        return true;
    }

    @Override
    public boolean isTreasureOnly() {
        return false;
    }

    @Override
    public boolean isTradeable() {
        return true;
    }

    public static float getDamageFactorBonus(int level) {
        return DAMAGE_FACTOR_BONUS_PER_LEVEL * level;
    }
}
