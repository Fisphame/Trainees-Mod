package com.pha.trainees.registry;

import com.pha.trainees.Main;
import com.pha.trainees.item.FuckItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class HiddenItem {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Main.MODID);
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, Main.MODID);

    //高爆炸抗性反相篮球
    public static final RegistryObject<Item> BASKETBALL_ANTI_BLOCK_RGT_ITEM= ITEMS.register("basketball_anti_block_rgt",
            () -> new BlockItem(ModBlocks.BASKETBALL_ANTI_BLOCK_RGT.get(),
                    new Item.Properties()
            ));

    //FUCK
    public static final RegistryObject<Item> FUCK = ITEMS.register("fuck",
            () -> new FuckItem(new Item.Properties()));
}
