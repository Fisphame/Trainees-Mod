package com.pha.trainees.registry;

import com.pha.trainees.Main;
import com.pha.trainees.item.CompoundScytheItem;
import com.pha.trainees.item.EnchantedFoodItem;
import com.pha.trainees.item.ScytheItem;
import com.pha.trainees.util.ModTiers;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, Main.MODID);

    //两锭半块
    public static final RegistryObject<Item> TWO_HALF_INGOT_BLOCK_ITEM = ITEMS.register("two_half_ingot_block",
            () -> new BlockItem(ModBlocks.TWO_HALF_INGOT_BLOCK.get(),
            new Item.Properties()
    ));
    //羽毛方块
    public static final RegistryObject<Item> FEATHER_BLOCK_ITEM = ITEMS.register("feather_block",
            () -> new BlockItem(ModBlocks.FEATHER_BLOCK.get(),
            new Item.Properties()
    ));
    //聚酯纤维矿
    public static final RegistryObject<Item> POLYESTER_BLOCK_ITEM = ITEMS.register("polyester_block",
            () -> new BlockItem(ModBlocks.POLYESTER_BLOCK.get(),
            new Item.Properties()
    ));
    //只因方块
    public static final RegistryObject<Item> myblockitem = ITEMS.register("myblock",
            () -> new BlockItem(ModBlocks.myblock.get(),
            new Item.Properties()
    ));




    //“钻石镐”
    public static final RegistryObject<Item> REAL_DIAMOND_PICKAXE = ITEMS.register("real_diamond_pickaxe",
            () -> new PickaxeItem(
                    Tiers.DIAMOND,
                    1,
                    -2.8F,
                    new Item.Properties()
                            .durability(1561)
                            .rarity(Rarity.UNCOMMON)
            ));

    //“钻 石 镐”
    public static final RegistryObject<Item> LONG_REAL_DIAMOND_PICKAXE = ITEMS.register("long_real_diamond_pickaxe",
            () -> new PickaxeItem(
                    Tiers.DIAMOND,
                    2,
                    -2.9F,
                    new Item.Properties()
                            .durability(1561)
                            .rarity(Rarity.UNCOMMON)
            ));

    //“钻  石  镐”
    public static final RegistryObject<Item> LONGER_REAL_DIAMOND_PICKAXE = ITEMS.register("longer_real_diamond_pickaxe",
            () -> new PickaxeItem(
                    Tiers.DIAMOND,
                    2,
                    -2.9F,
                    new Item.Properties()
                            .durability(1561)
                            .rarity(Rarity.UNCOMMON)
            ));

    //“钻   石   镐”
    public static final RegistryObject<Item> LONGEST_REAL_DIAMOND_PICKAXE = ITEMS.register("longest_real_diamond_pickaxe",
            () -> new PickaxeItem(
                    Tiers.DIAMOND,
                    3,
                    -3.0F,
                    new Item.Properties()
                            .durability(1561)
                            .rarity(Rarity.UNCOMMON)
            ));

    //“钻    石    镐”
    public static final RegistryObject<Item> THE_LONGEST_REAL_DIAMOND_PICKAXE = ITEMS.register("the_longest_real_diamond_pickaxe",
            () -> new PickaxeItem(
                    Tiers.DIAMOND,
                    3,
                    -3.0F,
                    new Item.Properties()
                            .durability(1561)
                            .rarity(Rarity.UNCOMMON)
            ));

    //“钻     石     镐”
    public static final RegistryObject<Item> THE_THE_LONGEST_REAL_DIAMOND_PICKAXE = ITEMS.register("the_the_longest_real_diamond_pickaxe",
            () -> new PickaxeItem(
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
    //只因锹
    public static final RegistryObject<Item> KUN_SHOVEL= ITEMS.register("kun_shovel" ,
            ()-> new ShovelItem(
                    Tiers.DIAMOND,
                    1,
                    -3F,
                    new Item.Properties()
                            .durability(913)
            )
    );
    //只因镐
    public static final RegistryObject<Item> KUN_PICKAXE= ITEMS.register("kun_pickaxe" ,
            ()-> new PickaxeItem(
                    Tiers.DIAMOND,
                    0,
                    -2.8F,
                    new Item.Properties()
                            .durability(913)
            )
    );
    //只因斧
    public static final RegistryObject<Item> KUN_AXE= ITEMS.register("kun_axe" ,
            ()-> new AxeItem(
                    Tiers.DIAMOND,
                    6,
                    -3.1F,
                    new Item.Properties()
                            .durability(913)
            )
    );
    //只因锄
    public static final RegistryObject<Item> KUN_HOE= ITEMS.register("kun_hoe" ,
            ()-> new HoeItem(
                    Tiers.DIAMOND,
                    -3,
                    -1.0F,
                    new Item.Properties()
                            .durability(913)
            )
    );



    //篮球
    public static final RegistryObject<Item> KUN_BASKETBALL = ITEMS.register("kun_basketball",
            () -> new Item(new Item.Properties()
                    .stacksTo(1)    // 堆叠数量为1
                    .durability(250) // 耐久度
                    .rarity(Rarity.UNCOMMON)
            )
    );




    //只因剑
    public static final RegistryObject<Item> KUN_SWORD = ITEMS.register("kun_sword" ,
            ()-> new SwordItem(
                    Tiers.DIAMOND,
                    4,
                    -2.4F,
                    new Item.Properties()
                            .durability(913)
            )
    );

    //只因短匕
    public static final RegistryObject<Item> KUN_DAGGER = ITEMS.register("kun_dagger" ,
            ()-> new SwordItem(
                    Tiers.DIAMOND,
                    0,
                    -1.8F,
                    new Item.Properties()
                            .durability(913)
            )
    );
    //只因镰刀
    public static final RegistryObject<Item> KUN_SCYTHE = ITEMS.register("kun_scythe",
            () -> new ScytheItem(
                    ModTiers.SCYTHE, // 自定义工具等级（见步骤2）
                    0,               // 基础攻击伤害（实际伤害 = 5 + 等级加成）
                    -2.6F,           // 攻击速度（原版钻石剑为-2.4）
                    new Item.Properties().durability(913)
                            .rarity(Rarity.COMMON)
            )
    );
    //0+1+3=5
    //横扫伤害为4*3=12

    //只因锤
    public static final RegistryObject<Item> KUN_MACE = ITEMS.register("kun_mace" ,
            ()-> new AxeItem(
                    Tiers.DIAMOND,
                    11,
                    -3.3F,
                    new Item.Properties()
                            .durability(913)
            )
    );

    //只因复合镰刀
    public static final RegistryObject<Item> KUN_COMPOUND_SCYTHE = ITEMS.register("kun_compound_scythe",
            () -> new CompoundScytheItem(
                    ModTiers.SCYTHE,
                    4,
                    -2.0F,
                    new Item.Properties().durability(913 * 2)
                            .rarity(Rarity.UNCOMMON)
            )
    );
    //4+1+3=8
    //横扫伤害为8*6=48
    //提升4倍

    //只因阔斧锤
    public static final RegistryObject<Item> KUN_ACXEMACE = ITEMS.register("kun_axemace" ,
            ()-> new AxeItem(
                    Tiers.DIAMOND,
                    56,
                    -2.6F,
                    new Item.Properties()
                            .durability(913 * 2)
                            .rarity(Rarity.UNCOMMON)
            )
    );
    //15*4=60 提升4倍

    //阿只因苹果
    public static final RegistryObject<Item> AHKUN_APPLE = ITEMS.register("ahkun_apple",
            () -> new EnchantedFoodItem(new Item.Properties()
                    .food(new FoodProperties.Builder()
                            .nutrition(4)                    // 回复饥饿值4
                            .saturationMod(1.2f)            // 饱和度 = 4 * 1.2 * 2 = 9.6
                            .alwaysEat()                    // 允许满饱食度时食用
                            .build()
                    )
                    .rarity(Rarity.EPIC)
            ));

    //大阿只因苹果
    public static final RegistryObject<Item> BIG_AHKUN_APPLE = ITEMS.register("big_ahkun_apple",
            () -> new EnchantedFoodItem(new Item.Properties()
                    .food(new FoodProperties.Builder()
                            .nutrition(5)                    // 回复饥饿值5
                            .saturationMod(1.9f)            // 饱和度 = 4 * 1.9 * 2 = 15.2
                            .alwaysEat()                    // 允许满饱食度时食用
                            .build()
                    )
                    .rarity(Rarity.EPIC)
            ));

    //“苹果”
    public static final RegistryObject<Item> REAL_APPLE = ITEMS.register("real_apple",
            () -> new Item(new Item.Properties()
                    .food(new FoodProperties.Builder()
                            .nutrition(4)
                            .saturationMod(0.3f)
                            .alwaysEat()
                            .build()
                    )
                    .rarity(Rarity.COMMON)
            ));

    //石棍
    public static final RegistryObject<Item> STONE_STICK = ITEMS.register("stone_stick",
            ()-> new Item(new Item.Properties())
    );
    //只因粒
    public static final RegistryObject<Item> KUN_NUGGET = ITEMS.register("kun_nugget",
            ()-> new Item(new Item.Properties())
    );
    //两锭半
    public static final RegistryObject<Item> TWO_HALF_INGOT = ITEMS.register("two_half_ingot",
            ()-> new Item(new Item.Properties())
    );
    //聚酯纤维（PET）
    public static final RegistryObject<Item> POLYESTER = ITEMS.register("polyester",
            ()-> new Item(new Item.Properties())
    );
    //弹性材料
    public static final RegistryObject<Item> ELASTOMERIC_MATERIAL = ITEMS.register("elastomeric_material",
            ()-> new Item(new Item.Properties())
    );
    //弹性核心
    public static final RegistryObject<Item> ELASTOMERIC_CORE = ITEMS.register("elastomeric_core",
            ()-> new Item(new Item.Properties())
    );
    //升级模版
    public static final RegistryObject<Item> UPGRADE_THEME = ITEMS.register("upgrade_theme",
            ()-> new Item(new Item.Properties()
                    .rarity(Rarity.RARE)
            ));
    //左半镐
    public static final RegistryObject<Item> LEFT_OF_DIAMOND_PICKAXE = ITEMS.register("left_of_diamond_pickaxe",
            ()-> new Item(new Item.Properties())
    );
    //右半镐
    public static final RegistryObject<Item> RIGHT_OF_DIAMOND_PICKAXE = ITEMS.register("right_of_diamond_pickaxe",
            ()-> new Item(new Item.Properties())
    );
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
}
