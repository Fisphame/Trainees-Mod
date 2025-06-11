package com.pha.trainees.registry;

import com.pha.trainees.Main;
import com.pha.trainees.enchantments.TenThousandSwordEnchantment;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

// ModEnchantments.java
public class ModEnchantments {
    public static final DeferredRegister<Enchantment> ENCHANTMENTS =
            DeferredRegister.create(ForgeRegistries.ENCHANTMENTS, Main.MODID);

    public static final RegistryObject<Enchantment> ten_thousand_sword =
            ENCHANTMENTS.register("ten_thousand_sword",
                    () -> new TenThousandSwordEnchantment(Enchantment.Rarity.VERY_RARE)
            );
}