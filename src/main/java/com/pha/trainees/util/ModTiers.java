package com.pha.trainees.util;

import net.minecraft.world.item.Items;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;

public enum ModTiers implements Tier {
    SCYTHE(3, 913, 8.0F, 3.0F, 15);

    private final int level;          // 工具等级（钻石为3）
    private final int uses;           // 耐久
    private final float speed;        // 挖掘速度
    private final float attackDamage; // 攻击伤害加成
    private final int enchantmentValue; // 附魔概率

    ModTiers(int level, int uses, float speed, float attackDamage, int enchantmentValue) {
        this.level = level;
        this.uses = uses;
        this.speed = speed;
        this.attackDamage = attackDamage;
        this.enchantmentValue = enchantmentValue;
    }

    @Override
    public int getUses() { return uses; }

    @Override
    public float getSpeed() { return speed; }

    @Override
    public float getAttackDamageBonus() { return attackDamage; }

    @Override
    public int getLevel() { return level; }

    @Override
    public int getEnchantmentValue() { return enchantmentValue; }

    @Override
    public Ingredient getRepairIngredient() {
        return Ingredient.of(Items.NETHERITE_INGOT); // 修复材料
    }
}