package com.pha.trainees.thetwice;

import com.pha.trainees.Main;
import com.pha.trainees.util.ModTiers;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class Object {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, Main.MODID);

    public static final RegistryObject< Item > SIJIAN = ITEMS.register("sijian" , () -> new SiJian(ModTiers.SCYTHE , 5 , -2.5F , new Item.Properties()));
}
