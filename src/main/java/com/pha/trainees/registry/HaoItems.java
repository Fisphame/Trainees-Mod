package com.pha.trainees.registry;

import com.pha.trainees.Main;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Tiers;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class HaoItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, Main.MODID);
    //豪哥的模组……

    //顺序：方块（block item） -> 工具与实用物品 -> 战斗用品 -> 食物与饮品 -> 原材料 -> 刷怪蛋

    public static final RegistryObject<Item> HAO_GE = ITEMS.register("hao_ge",
            ()-> new AxeItem(
                    Tiers.NETHERITE,
                    8,
                    -2.0F,
                    new Item.Properties()
                            .food(new FoodProperties.Builder()
                                    .nutrition(10)
                                    .saturationMod(0.3f)
                                    .alwaysEat()
                                    .build()
                            )
                            .durability(250)
                            .rarity(Rarity.UNCOMMON)
            )
    );



}
