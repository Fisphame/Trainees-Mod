package com.pha.trainees.util;

import com.pha.trainees.registry.ModItems;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.LazyLoadedValue;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public enum ModTiers implements Tier {
    SCYTHE(3, 913, 8.0F, 3.0F, 15, () -> Ingredient.of(ModItems.TWO_HALF_INGOT.get()));

    private final int level;          // 工具等级（钻石为3）
    private final int uses;           // 耐久
    private final float speed;        // 挖掘速度
    private final float attackDamage; // 攻击伤害加成
    private final int enchantmentValue; // 附魔概率
    private final LazyLoadedValue<Ingredient> repairIngredient;

    ModTiers(int level, int uses, float speed, float attackDamage, int enchantmentValue, Supplier<Ingredient> Ingredient) {
        this.level = level;
        this.uses = uses;
        this.speed = speed;
        this.attackDamage = attackDamage;
        this.enchantmentValue = enchantmentValue;
        this.repairIngredient = new LazyLoadedValue<>(Ingredient);
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
    public @NotNull Ingredient getRepairIngredient() {
        return this.repairIngredient.get();
    }


}