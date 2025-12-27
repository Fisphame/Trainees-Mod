package com.pha.trainees.registry;

import com.pha.trainees.Main;
import com.pha.trainees.item.WhatDoesItMeanItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class HiddenItem {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Main.MODID);
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, Main.MODID);

    //FUCK
    public static final RegistryObject<Item> FUCK = ITEMS.register("fuck",
            () -> new WhatDoesItMeanItem.FuckItem(new Item.Properties()));

    //RGT反相篮球
    public static final RegistryObject<Item> BASKETBALL_ANTI_BLOCK_RGT_ITEM= ITEMS.register("basketball_anti_block_rgt",
            () -> new BlockItem(ModBlocks.BASKETBALL_ANTI_BLOCK_RGT.get(),
                    new Item.Properties()
            ));

    //RGT反相素方块
    public static final RegistryObject<Item> CHE_JIBP_BLOCK_RGT_ITEM = ITEMS.register("che_jibp_block_rgt",
            () -> new BlockItem(ModChemistry.ModChemistryBlocks.CHE_JIBP_BLOCK_RGT.get(),
                    new Item.Properties()
            ));
}
