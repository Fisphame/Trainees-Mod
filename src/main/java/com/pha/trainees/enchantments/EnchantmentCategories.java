package com.pha.trainees.enchantments;

import com.pha.trainees.util.interfaces.Chargeable;
import com.pha.trainees.util.interfaces.KineticWeapon;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public class EnchantmentCategories {
    public static final EnchantmentCategory KINETIC_WEAPON = EnchantmentCategory.create(
            "kinetic_weapon",
            (item) -> item instanceof KineticWeapon
    );

    public static final EnchantmentCategory CHARGEABLE = EnchantmentCategory.create(
            "chargeable",
            (item) -> item instanceof Chargeable
    );
}
