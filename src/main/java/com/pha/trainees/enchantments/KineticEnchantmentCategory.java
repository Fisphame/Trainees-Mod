package com.pha.trainees.enchantments;

import com.pha.trainees.item.interfaces.KineticWeapon;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public class KineticEnchantmentCategory {
    public static final EnchantmentCategory KINETIC_WEAPON = EnchantmentCategory.create(
            "kinetic_weapon",
            (item) -> item instanceof KineticWeapon
    );
}
