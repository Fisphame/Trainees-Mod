package com.pha.trainees.multiblock;

import com.pha.trainees.registry.ModItems;
import com.pha.trainees.util.game.ItemPair4;
import com.pha.trainees.util.game.ItemPair4Manager;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class TrainerAltarRecipes {
    public static final ItemPair4[] PAIRS = new ItemPair4[]{
            new ItemPair4(
                    ModItems.TWO_HALF_INGOT.get(),
                    ModItems.TWO_HALF_INGOT.get(),
                    ModItems.TWO_HALF_INGOT.get(),
                    ModItems.TWO_HALF_INGOT.get(),
                    new ItemStack(ModItems.TWO_HALF_INGOT.get(), 9)
            ),
            new ItemPair4(
                    ModItems.GOLD_FEATHER.get(),
                    ModItems.GOLD_FEATHER.get(),
                    ModItems.GOLD_FEATHER.get(),
                    Items.AIR,
                    new ItemStack(ModItems.KUN_EGG.get(), 1)
            )
    };

    public static void registerRecipes(ItemPair4Manager manager) {
        for (ItemPair4 pair : PAIRS) {
            manager.registerPair(pair);
        }
    }
}
