package com.pha.trainees.registry;

import com.pha.trainees.Main;
import com.pha.trainees.item.*;
import com.pha.trainees.materials.TRAIN;
import com.pha.trainees.util.ModTiers;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.*;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, Main.MODID);

    ///顺序：方块（block item） -> 工具与实用物品 -> 战斗用品 -> 食物与饮品 -> 原材料 （化学最后）-> 刷怪蛋

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
    public static final RegistryObject<Item> POLYESTER_ORE_ITEM = ITEMS.register("polyester_ore",
            () -> new BlockItem(ModBlocks.POLYESTER_ORE.get(),
            new Item.Properties()
    ));
    //只因方块
    public static final RegistryObject<Item> myblockitem = ITEMS.register("myblock",
            () -> new BlockItem(ModBlocks.myblock.get(),
            new Item.Properties()
                    .rarity(Rarity.RARE)
    ));
    //提纯台
    public static final RegistryObject<Item> PURIFICATION_STATION_ITEM = ITEMS.register("purification_station",
            () -> new BlockItem(ModBlocks.PURIFICATION_STATION.get(),
                    new Item.Properties()
            )
    );
    //反相篮球
    public static final RegistryObject<Item> BASKETBALL_ANTI_BLOCK_ITEM = ITEMS.register("basketball_anti_block",
            () -> new BlockItem(ModBlocks.BASKETBALL_ANTI_BLOCK.get(),
                    new Item.Properties()
            )


    );

    //只因锹
    public static final RegistryObject<Item> KUN_SHOVEL= ITEMS.register("kun_shovel" ,
            ()-> new ShovelItem(
                    Tiers.IRON,
                    1,
                    -3F,
                    new Item.Properties()
                            .durability(913)
            )
    );
    //只因镐
    public static final RegistryObject<Item> KUN_PICKAXE= ITEMS.register("kun_pickaxe" ,
            ()-> new PickaxeItem(
                    Tiers.IRON,
                    1,
                    -2.8F,
                    new Item.Properties()
                            .durability(913)
            )
    );
    //终极只因镐
    public static final RegistryObject<Item> KUN_PICKAXE_FINAL= ITEMS.register("kun_pickaxe_final" ,
            ()-> new KunPickaxeFinal(
                    Tiers.DIAMOND,
                    3,
                    -2.8F,
                    new Item.Properties()
                            .durability(913 * 2)
                            .rarity(Rarity.UNCOMMON)
            )
    );
    //四倍挖掘 -> 提升4倍

    //只因斧
    public static final RegistryObject<Item> KUN_AXE= ITEMS.register("kun_axe" ,
            ()-> new AxeItem(
                    Tiers.IRON,
                    6,
                    -3.1F,
                    new Item.Properties()
                            .durability(913)
            )
    );
    //只因锄
    public static final RegistryObject<Item> KUN_HOE= ITEMS.register("kun_hoe" ,
            ()-> new HoeItem(
                    Tiers.IRON,
                    -2,
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

    //反相篮球
    public static final RegistryObject<Item> BASKETBALL_ANTI = ITEMS.register("basketball_anti",
            () -> new BasketballAntiItem(new Item.Properties()
                    .stacksTo(16)
                    .rarity(Rarity.UNCOMMON)

            ));





    //只因剑
    public static final RegistryObject<Item> KUN_SWORD = ITEMS.register("kun_sword" ,
            ()-> new SwordItem(
                    Tiers.IRON,
                    3,
                    -2.4F,
                    new Item.Properties()
                            .durability(913)
            )
    );

    //只因短匕
    public static final RegistryObject<Item> KUN_DAGGER = ITEMS.register("kun_dagger" ,
            ()-> new SwordItem(
                    Tiers.IRON,
                    0,
                    0F,
                    new Item.Properties()
                            .durability(913)
            )
    );
    //只因镰刀
    public static final RegistryObject<Item> KUN_SCYTHE = ITEMS.register("kun_scythe",
            () -> new ScytheCourseItem.ScytheItem(
                    ModTiers.SCYTHE, // 自定义工具等级（见步骤2）
                    2,               // 基础攻击伤害（实际伤害 = 5 + 等级加成）
                    -2.8F,           // 攻击速度（原版钻石剑为-2.4）
                    new Item.Properties().durability(913)
                            .rarity(Rarity.COMMON)
            )
    );
    //0+1+3=5
    //横扫伤害为4*3=12

    //只因锤
    public static final RegistryObject<Item> KUN_MACE = ITEMS.register("kun_mace" ,
            ()-> new AxeMaceItem(
                    Tiers.IRON,
                    11,
                    -3.4F,
                    new Item.Properties()
                            .durability(913)
            )
    );

    //只因复合镰刀
    public static final RegistryObject<Item> KUN_COMPOUND_SCYTHE = ITEMS.register("kun_compound_scythe",
            () -> new ScytheCourseItem.CompoundScytheItem(
                    ModTiers.SCYTHE,
                    4,
                    -2.4F,
                    new Item.Properties().durability(913 * 2)
                            .rarity(Rarity.UNCOMMON)
            )
    );
    //4+1+3=8
    //横扫伤害为8*6=48
    //提升4倍

    //只因阔斧锤
    public static final RegistryObject<Item> KUN_ACXEMACE = ITEMS.register("kun_axemace" ,
            ()-> new AxeMaceItem(
                    Tiers.IRON,
                    53,
                    -3F,
                    new Item.Properties()
                            .durability(913 * 2)
                            .rarity(Rarity.UNCOMMON)
            )
    );
    //15*4=60 提升4倍

    //盔甲lv1
    public static final RegistryObject<Item> TRAIN_I_HELMET = ITEMS.register("train_i_helmet",
            () -> new ArmorItem(
                    new TRAIN.I(),
                    ArmorItem.Type.HELMET,
                    new Item.Properties()
            )
    );
    public static final RegistryObject<Item> TRAIN_I_CHESTPLATE = ITEMS.register("train_i_chestplate",
            () -> new ArmorItem(
                    new TRAIN.I(),
                    ArmorItem.Type.CHESTPLATE,
                    new Item.Properties()
            )
    );

    public static final RegistryObject<Item> TRAIN_I_LEGGINGS = ITEMS.register("train_i_leggings",
            () -> new ArmorItem(
                    new TRAIN.I(),
                    ArmorItem.Type.LEGGINGS,
                    new Item.Properties()
            )
    );

    public static final RegistryObject<Item> TRAIN_I_BOOTS = ITEMS.register("train_i_boots",
            () -> new ArmorItem(
                    new TRAIN.I(),
                    ArmorItem.Type.BOOTS,
                    new Item.Properties()
            )
    );

    //盔甲lv2
    public static final RegistryObject<Item> TRAIN_II_HELMET = ITEMS.register("train_ii_helmet",
            () -> new ArmorItem(
                    new TRAIN.II(),
                    ArmorItem.Type.HELMET,
                    new Item.Properties()
                            .rarity(Rarity.UNCOMMON)
            )
    );
    public static final RegistryObject<Item> TRAIN_II_CHESTPLATE = ITEMS.register("train_ii_chestplate",
            () -> new ArmorItem(
                    new TRAIN.II(),
                    ArmorItem.Type.CHESTPLATE,
                    new Item.Properties()
                            .rarity(Rarity.UNCOMMON)
            )
    );

    public static final RegistryObject<Item> TRAIN_II_LEGGINGS = ITEMS.register("train_ii_leggings",
            () -> new ArmorItem(
                    new TRAIN.II(),
                    ArmorItem.Type.LEGGINGS,
                    new Item.Properties()
                            .rarity(Rarity.UNCOMMON)
            )
    );

    public static final RegistryObject<Item> TRAIN_II_BOOTS = ITEMS.register("train_ii_boots",
            () -> new ArmorItem(
                    new TRAIN.II(),
                    ArmorItem.Type.BOOTS,
                    new Item.Properties()
                            .rarity(Rarity.UNCOMMON)
            )
    );

    //盔甲lv3
    public static final RegistryObject<Item> TRAIN_III_HELMET = ITEMS.register("train_iii_helmet",
            () -> new ArmorItem(
                    new TRAIN.III(),
                    ArmorItem.Type.HELMET,
                    new Item.Properties()
                            .rarity(Rarity.RARE)
            )
    );
    public static final RegistryObject<Item> TRAIN_III_CHESTPLATE = ITEMS.register("train_iii_chestplate",
            () -> new ArmorItem(
                    new TRAIN.III(),
                    ArmorItem.Type.CHESTPLATE,
                    new Item.Properties()
                            .rarity(Rarity.RARE)
            )
    );

    public static final RegistryObject<Item> TRAIN_III_LEGGINGS = ITEMS.register("train_iii_leggings",
            () -> new ArmorItem(
                    new TRAIN.III(),
                    ArmorItem.Type.LEGGINGS,
                    new Item.Properties()
                            .rarity(Rarity.RARE)
            )
    );

    public static final RegistryObject<Item> TRAIN_III_BOOTS = ITEMS.register("train_iii_boots",
            () -> new ArmorItem(
                    new TRAIN.III(),
                    ArmorItem.Type.BOOTS,
                    new Item.Properties()
                            .rarity(Rarity.RARE)
            )
    );


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




    //石棍
    public static final RegistryObject<Item> STONE_STICK = ITEMS.register("stone_stick",
            ()-> new Item(new Item.Properties())
    );
    //只因粒
    public static final RegistryObject<Item> KUN_NUGGET = ITEMS.register("kun_nugget",
            ()-> new KunCourseItem.KunNuggetItem(new Item.Properties())
    );
    //两锭半
    public static final RegistryObject<Item> TWO_HALF_INGOT = ITEMS.register("two_half_ingot",
            () -> new KunCourseItem.TwoHalfIngotItem(new Item.Properties()
            ));
    //黑粉
    public static final RegistryObject<Item> POWDER_ANTI = ITEMS.register("powder_anti",
            () -> new PowderAntiCourseItem.PowderAntiItem(new Item.Properties()
            ));
    //黑粉 4x
    public static final RegistryObject<Item> POWDER_ANTI_4 = ITEMS.register("powder_anti_4",
            () -> new PowderAntiCourseItem.PowderAnti4Item(new Item.Properties()
            ));
    //一小堆黑粉
    public static final RegistryObject<Item> POWDER_ANTI_9 = ITEMS.register("powder_anti_9",
            () -> new PowderAntiCourseItem.PowderAnti9Item(new Item.Properties()
            ));

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
    //兑换券
    public static final RegistryObject<Item> duihuanquan = ITEMS.register("duihuanquan",
            () -> new Duihuanquan(new Item.Properties()
                    .rarity(Rarity.RARE)
            ));
    //升级模版
    public static final RegistryObject<Item> UPGRADE_THEME = ITEMS.register("upgrade_theme",
            ()-> new Item(new Item.Properties()
                    .rarity(Rarity.RARE)
            ));
    //盔甲升级模版
    public static final RegistryObject<Item> UPGRADE_THEME_ARMOR = ITEMS.register("upgrade_theme_armor",
            ()-> new Item(new Item.Properties()
                    .rarity(Rarity.RARE)
            ));

    //只因蛋
    public static final RegistryObject<Item> KUN_EGG = ITEMS.register("kun_egg",
            () -> new EggCourseItem.KunEggItem(new Item.Properties()
                    .stacksTo(16)
            ));

    //黑蛋
    public static final RegistryObject<Item> BLACK_EGG = ITEMS.register("black_egg",
            () -> new EggCourseItem.BlackEggItem(new Item.Properties()
                    .stacksTo(16)
            ));

    //化学书
    public static final RegistryObject<Item> CHEMISTRY_BOOK = ITEMS.register("chemistry_book",
            () -> new ChemistryBookItem(
                    new Item.Properties()
                            .stacksTo(1)
                            .rarity(Rarity.UNCOMMON),
                    "化学·练习生选择性必修一",
                    "真的要看吗……"
            )
    );

    //鸡碱锭
    public static final RegistryObject<Item> CHE_JIOH = ITEMS.register("che_jioh",
            () -> new ChemistryItem.JiOH(new Item.Properties()
            ));

    //鸡碱粒
    public static final RegistryObject<Item> CHE_JIOH_NUGGET = ITEMS.register("che_jioh_nugget",
            () -> new ChemistryItem.JiOH(new Item.Properties()
            ));

    //黑酸
    public static final RegistryObject<Item> CHE_HBP = ITEMS.register("che_hbp",
            () -> new ChemistryItem.HBp(new Item.Properties()
            ));

    //黑鸡
    public static final RegistryObject<Item> CHE_JIBP = ITEMS.register("che_jibp",
            () -> new ChemistryItem.JiBp(new Item.Properties()
                    .rarity(Rarity.UNCOMMON)
            ));

    //一氧化二鸡
    public static final RegistryObject<Item> CHE_JI2O = ITEMS.register("che_ji2o",
            () -> new ChemistryItem.Ji2O(new Item.Properties()
            ));

    //一氧化二鸡粒
    public static final RegistryObject<Item> CHE_JI2O_NUGGET = ITEMS.register("che_ji2o_nugget",
            () -> new ChemistryItem.Ji2O(new Item.Properties()
            ));


    //练习鸡刷怪蛋
    public static final RegistryObject<Item> KUN_TRAINEES_SPAWN_EGG = ITEMS.register("kun_trainees_spawn_egg",
            () -> new ForgeSpawnEggItem(
                    ModEntities.KUN_TRAINEES,
                    0xFFFFFF,// 主颜色（蛋底色）
                    0xff7f27,// 副颜色（斑点色）
                    new Item.Properties()
                            .stacksTo(64)
            )
    );
    //黑粉刷怪蛋
    public static final RegistryObject<Item> KUN_ANTI_SPAWN_EGG = ITEMS.register("kun_anti_spawn_egg",
            () -> new ForgeSpawnEggItem(
                    ModEntities.KUN_ANTI,
                    0x9f9f9f,// 主颜色（蛋底色）
                    0x272727,// 副颜色（斑点色）
                    new Item.Properties()
                            .stacksTo(64)
            )
    );




}
