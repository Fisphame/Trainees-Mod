package com.pha.trainees.registry;

import com.pha.trainees.Main;
import com.pha.trainees.item.RealPickaxeItem;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.*;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class PrankItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, Main.MODID);
    //模组中专门整蛊的部分……

    //顺序：方块（block item） -> 工具与实用物品 -> 战斗用品 -> 食物与饮品 -> 原材料 -> 刷怪蛋
    //“钻石镐” 24*24
    public static final RegistryObject<Item> REAL_DIAMOND_PICKAXE = ITEMS.register("real_diamond_pickaxe",
            () -> new RealPickaxeItem(
                    Tiers.DIAMOND,
                    1,
                    -2.8F,
                    new Item.Properties()
                            .durability(1561)
                            .rarity(Rarity.UNCOMMON)
            ));

    //“钻 石 镐” 32*32
    public static final RegistryObject<Item> LONG_REAL_DIAMOND_PICKAXE = ITEMS.register("long_real_diamond_pickaxe",
            () -> new RealPickaxeItem(
                    Tiers.DIAMOND,
                    2,
                    -2.9F,
                    new Item.Properties()
                            .durability(1561)
                            .rarity(Rarity.UNCOMMON)
            ));

    //“钻  石  镐” 40*40
    public static final RegistryObject<Item> LONGER_REAL_DIAMOND_PICKAXE = ITEMS.register("longer_real_diamond_pickaxe",
            () -> new RealPickaxeItem(
                    Tiers.DIAMOND,
                    2,
                    -2.9F,
                    new Item.Properties()
                            .durability(1561)
                            .rarity(Rarity.UNCOMMON)
            ));

    //“钻   石   镐” 48*48
    public static final RegistryObject<Item> LONGEST_REAL_DIAMOND_PICKAXE = ITEMS.register("longest_real_diamond_pickaxe",
            () -> new RealPickaxeItem(
                    Tiers.DIAMOND,
                    3,
                    -3.0F,
                    new Item.Properties()
                            .durability(1561)
                            .rarity(Rarity.UNCOMMON)
            ));

    //“钻    石    镐” 56*56
    public static final RegistryObject<Item> THE_LONGEST_REAL_DIAMOND_PICKAXE = ITEMS.register("the_longest_real_diamond_pickaxe",
            () -> new RealPickaxeItem(
                    Tiers.DIAMOND,
                    3,
                    -3.0F,
                    new Item.Properties()
                            .durability(1561)
                            .rarity(Rarity.UNCOMMON)
            ));

    //“钻     石     镐” 64*64
    public static final RegistryObject<Item> THE_THE_LONGEST_REAL_DIAMOND_PICKAXE = ITEMS.register("the_the_longest_real_diamond_pickaxe",
            () -> new RealPickaxeItem(
                    Tiers.DIAMOND,
                    4,
                    -3.0F,
                    new Item.Properties()
                            .durability(1561)
                            .rarity(Rarity.UNCOMMON)
            ));

    //“钻石镐”.zip
    public static final RegistryObject<Item> REAL_DIAMOND_PICKAXE_ZIP = ITEMS.register("real_diamond_pickaxe_zip",
            () -> new PickaxeItem(
                    Tiers.DIAMOND,
                    20,
                    -1.5F,
                    new Item.Properties()
                            .durability(114514)
                            .rarity(Rarity.EPIC)
            ));

    //长木棍
    public static final RegistryObject<Item> LONG_STICK = ITEMS.register("long_stick",
            ()-> new Item(new Item.Properties())
    );
    //长长木棍
    public static final RegistryObject<Item> LONGER_STICK = ITEMS.register("longer_stick",
            ()-> new Item(new Item.Properties())
    );
    //长长长木棍
    public static final RegistryObject<Item> LONGEST_STICK = ITEMS.register("longest_stick",
            ()-> new Item(new Item.Properties())
    );
    //左半镐
    public static final RegistryObject<Item> LEFT_HALF_DIAMOND_PICKAXE = ITEMS.register("left_half_diamond_pickaxe",
            ()-> new Item(new Item.Properties())
    );
    //右半镐
    public static final RegistryObject<Item> RIGHT_HALF_DIAMOND_PICKAXE = ITEMS.register("right_half_diamond_pickaxe",
            ()-> new Item(new Item.Properties())
    );

    //渲染超格物品
    public static void registerModels(ModelEvent.RegisterAdditional event) {
        event.register(new ModelResourceLocation(
                new ResourceLocation("trainees", "real_diamond_pickaxe"), "inventory"
        ));
    }
}
