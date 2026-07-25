package com.pha.trainees.registry;

import com.pha.trainees.Main;
import com.pha.trainees.block.CheJibpBlock;
import com.pha.trainees.chemistry.block.BeakerBlock;
import com.pha.trainees.chemistry.blockentity.BeakerBlockEntity;
//import com.pha.trainees.chemistry.fluid.HydrochloricAcidFluid;
import com.pha.trainees.chemistry.item.AnalyzerItemCreative;
import com.pha.trainees.chemistry.item.AnalyzerItemNormal;
import com.pha.trainees.chemistry.item.SubstanceItem;
import com.pha.trainees.chemistry.particle.IonTags;
import com.pha.trainees.chemistry.particle.IonType;
import com.pha.trainees.chemistry.particle.Phase;
import com.pha.trainees.chemistry.reaction.ReactionGraph;
import com.pha.trainees.chemistry.reaction.ReactionRule;
import com.pha.trainees.util.game.chemistry.ChemicalEquation;
import com.pha.trainees.util.game.chemistry.ReactionConditions;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.List;

import static com.pha.trainees.item.ChemicalItem.*;
import static com.pha.trainees.block.ChemistryBlock.*;

@SuppressWarnings({"deprecation", "removal"})
public class ModChemistry {

    public static class ModChemistryBlocks {
        public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, Main.MODID);

        //氢氧化鸡方块
        public static final RegistryObject<Block> CHE_JIOH_BLOCK = BLOCKS.register("che_jioh_block",
                () -> new JiOHBlock(
                        BlockBehaviour.Properties.of()
                                .strength(2f,6f)
                                .sound(SoundType.BONE_BLOCK)
                                .requiresCorrectToolForDrops()
                ));

        // 氧化鸡方块
        public static final RegistryObject<Block> CHE_JI2O_BLOCK = BLOCKS.register("che_ji2o_block",
                () -> new Ji2OBlock(
                        BlockBehaviour.Properties.of()
                                .strength(2f, 6f)
                                .sound(SoundType.BONE_BLOCK)
                                .requiresCorrectToolForDrops()
                ));

        // 涂蜡的氧化鸡方块
        public static final RegistryObject<Block> CHE_WAXED_JI2O_BLOCK = BLOCKS.register("che_waxed_ji2o_block",
                () -> new WaxedJi2OBlock(
                        BlockBehaviour.Properties.of()
                                .strength(2f, 6f)
                                .sound(SoundType.BONE_BLOCK)
                                .requiresCorrectToolForDrops()
                ));

        // 欲焰鸡方块
        public static final RegistryObject<Block> CHE_JI2O2_BLOCK = BLOCKS.register("che_ji2o2_block",
                () -> new Ji2O2Block(
                        BlockBehaviour.Properties.of()
                                .strength(2f, 6f)
                                .sound(SoundType.BONE_BLOCK)
                                .requiresCorrectToolForDrops()
                ));

        //反相素方块
        public static final RegistryObject<Block> CHE_JIBP_BLOCK = BLOCKS.register("che_jibp_block",
                () -> new CheJibpBlock(
                        BlockBehaviour.Properties.of()
                                .strength(0.5f,3f)
                                .sound(SoundType.WET_GRASS)
                )
        );

        // RGT反相素方块
        public static final RegistryObject<Block> CHE_JIBP_BLOCK_RGT = BLOCKS.register("che_jibp_block_rgt",
                () -> new CheJibpBlock(
                        BlockBehaviour.Properties.of()
                                .strength(0.5f,1200f)
                                .sound(SoundType.WET_GRASS)
                )
        );

        public static final RegistryObject<LiquidBlock> CHE_HBP_BLOCK = BLOCKS.register("che_hbp_block",
                () -> new LiquidBlock(ModFluids.SOURCE_CHE_HBP,
                        BlockBehaviour.Properties.of()
                                .mapColor(MapColor.WATER)
                                .replaceable()
                                .noCollission()
                                .strength(100.0F)
                                .pushReaction(PushReaction.DESTROY)
                                .noLootTable()
                )
        );

//        public static final RegistryObject<LiquidBlock> JI_LIQUID_BLOCK =BLOCKS.register("ji_liquid_block",
//                () -> new LiquidBlock(ModFluid.SOURCE_JI,
//                        BlockBehaviour.Properties.of()
//                                .mapColor(MapColor.GOLD)
//                                .strength(100.0F)
//                                .replaceable()
//                                .noCollission()
//                                .pushReaction(PushReaction.DESTROY)
//                                .noLootTable()
//                        )
//        );

        public static final RegistryObject<BeakerBlock> BEAKER = BLOCKS.register("beaker",
                () -> new BeakerBlock(BlockBehaviour.Properties.of()
                        .mapColor(MapColor.TERRACOTTA_WHITE)
                        .strength(0.5f)
                        .noOcclusion()
                        .isViewBlocking((state, level, pos) -> false)
                        .isSuffocating((state, level, pos) -> false)
                        .sound(SoundType.GLASS)
                ));
    }

    public static class ModChemistryBlockItems {
        public static final DeferredRegister<Item> ITEMS =
                DeferredRegister.create(ForgeRegistries.ITEMS, Main.MODID);

        //氢氧化鸡方块
        public static final RegistryObject<Item> CHE_JIOH_BLOCK_ITEM = ITEMS.register("che_jioh_block",
                () -> new JiOH_B(ModChemistryBlocks.CHE_JIOH_BLOCK.get(),
                        new Item.Properties()
                ));
        // 氧化鸡方块
        public static final RegistryObject<Item> CHE_JI2O_BLOCK_ITEM = ITEMS.register("che_ji2o_block",
                () -> new Ji2O_B(ModChemistryBlocks.CHE_JI2O_BLOCK.get(),
                        new Item.Properties()
                ));
        // 涂蜡的氧化鸡方块
        public static final RegistryObject<Item> CHE_WAXED_JI2O_BLOCK_ITEM = ITEMS.register("che_waxed_ji2o_block",
                () -> new Waxed_Ji2O_B(ModChemistryBlocks.CHE_WAXED_JI2O_BLOCK.get(),
                        new Item.Properties()
                ));
        // 欲焰鸡方块
        public static final RegistryObject<Item> CHE_JI2O2_BLOCK_ITEM = ITEMS.register("che_ji2o2_block",
                () -> new Ji2O2_B(ModChemistryBlocks.CHE_JI2O2_BLOCK.get(),
                        new Item.Properties()
                ));
        //反相素方块
        public static final RegistryObject<Item> CHE_JIBP_BLOCK_ITEM = ITEMS.register("che_jibp_block",
                () -> new JiBp_B(ModChemistryBlocks.CHE_JIBP_BLOCK.get(),
                        new Item.Properties()
                                .rarity(Rarity.UNCOMMON)
                ));
        // 烧杯
        public static final RegistryObject<Item> BEAKER = ITEMS.register("beaker",
                () -> new BlockItem(ModChemistryBlocks.BEAKER.get(),
                        new Item.Properties()
                ));

    }

    public static class ModChemistryBlockEntities {
        public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
                DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, Main.MODID);

        public static final RegistryObject<BlockEntityType<BeakerBlockEntity>> BEAKER =
                BLOCK_ENTITIES.register("beaker",
                        () -> BlockEntityType.Builder.of(BeakerBlockEntity::new,
                                ModChemistryBlocks.BEAKER.get()).build(null)
                );
    }

    public static class ModChemistryItems {
        public static final DeferredRegister<Item> ITEMS =
                DeferredRegister.create(ForgeRegistries.ITEMS, Main.MODID);


        //化学书
        public static final RegistryObject<Item> CHEMISTRY_BOOK = ITEMS.register("chemistry_book",
                () -> new ChemistryBookItem(
                        new Item.Properties()
                                .stacksTo(1)
                                .rarity(Rarity.UNCOMMON)
                )
        );

        // 分析仪
        public static final RegistryObject<AnalyzerItemNormal> ANALYZER = ITEMS.register("analyzer",
                () -> new AnalyzerItemNormal(
                        new Item.Properties()
                                .stacksTo(1)
                )
        );
        public static final RegistryObject<AnalyzerItemCreative> ANALYZER_CREATIVE = ITEMS.register("analyzer_creative",
                () -> new AnalyzerItemCreative(
                        new Item.Properties()
                                .stacksTo(1)
                                .rarity(Rarity.EPIC)
                )
        );

        // ====== 物质基类物品 ======
        public static final RegistryObject<SubstanceItem> SUBSTANCE = ITEMS.register("substance",
                () -> new SubstanceItem(new Item.Properties().stacksTo(64)));
        //相酸桶
//        public static final RegistryObject<Item> CHE_HBP_BUCKET = ITEMS.register("che_hbp_bucket",
//                () -> new BucketItem(
//                        ModFluid.SOURCE_CHE_HBP,
//                        new Item.Properties()
//                                .craftRemainder(BUCKET)
//                                .stacksTo(1)
//                ));
        //鸡桶
//        public static final RegistryObject<Item> JI_BUCKET = ITEMS.register("ji_bucket",
//                () -> new BucketItem(
//                        ModFluid.,
//                        new Item.Properties()
//                                .craftRemainder(Items.BUCKET)
//                                .stacksTo(1)
//                ));

//        public static final Item JI_BUCKET = registerItem("ji_bucket",
//                new BucketItem(ModFluid.JI, (new Item.Properties()).craftRemainder(BUCKET).stacksTo(1)));

        // 杂质
        public static final RegistryObject<Item> IMPERFECTION = ITEMS.register("imperfection",
                () -> new Item(
                        new Item.Properties()
                                .fireResistant()
                                .rarity(Rarity.UNCOMMON)
                                .setNoRepair()
                ));
        // 氧化鸡锭
        public static final RegistryObject<Item> CHE_JI2O_INGOT = ITEMS.register("che_ji2o_ingot",
                () -> new Ji2O(new Item.Properties()
                ));
        // 氧化鸡粒
        public static final RegistryObject<Item> CHE_JI2O_NUGGET = ITEMS.register("che_ji2o_nugget",
                () -> new Ji2O(new Item.Properties()
                ));
        // 过氧化鸡锭
        public static final RegistryObject<Item> CHE_JI2O2_INGOT = ITEMS.register("che_ji2o2_ingot",
                () -> new Ji2O2(new Item.Properties()
                ));
        // 过氧化鸡粒
        public static final RegistryObject<Item> CHE_JI2O2_NUGGET = ITEMS.register("che_ji2o2_nugget",
                () -> new Ji2O2(new Item.Properties()
                ));
        // 二氧化黑固体
        public static final RegistryObject<Item> CHE_BPO2_SOLID = ITEMS.register("che_bpo2_solid",
                () -> new BpO2(new Item.Properties()
                ));
        // 三氧化黑固体
        public static final RegistryObject<Item> CHE_BPO3_SOLID = ITEMS.register("che_bpo3_solid",
                () -> new BpO3(new Item.Properties()
                ));
        // 黑化氢粉末
        public static final RegistryObject<Item> CHE_HBP_POWDER = ITEMS.register("che_hbp_powder",
                () -> new HBp(new Item.Properties()
                ));
        // 次黑酸粉末
        public static final RegistryObject<Item> CHE_HBPO_POWDER = ITEMS.register("che_hbpo_powder",
                () -> new HBpO(new Item.Properties()
                ));
        // 黑酸粉末
        public static final RegistryObject<Item> CHE_HBPO_3_POWDER = ITEMS.register("che_hbpo3_powder",
                () -> new HBpO3(new Item.Properties()
                ));
        // 高黑酸粉末
        public static final RegistryObject<Item> CHE_HBPO_4_POWDER = ITEMS.register("che_hbpo4_powder",
                () -> new HBpO4(new Item.Properties()
                ));
        // 氢氧化鸡锭
        public static final RegistryObject<Item> CHE_JIOH_INGOT = ITEMS.register("che_jioh_ingot",
                () -> new JiOH(new Item.Properties()
                ));
        // 氢氧化鸡粒
        public static final RegistryObject<Item> CHE_JIOH_NUGGET = ITEMS.register("che_jioh_nugget",
                () -> new JiOH(new Item.Properties()
                ));
        // 反相素
        public static final RegistryObject<Item> CHE_JIBP_PIECE = ITEMS.register("che_jibp_piece",
                () -> new JiBp(new Item.Properties()
                        .rarity(Rarity.UNCOMMON)
                ));
        // 次黑酸鸡
        public static final RegistryObject<Item> CHE_JIBPO_CRYSTALLIZATION = ITEMS.register("che_jibpo_crystallization",
                () -> new JiBpO(new Item.Properties()
                ));
        // 黑酸鸡
        public static final RegistryObject<Item> CHE_JIBPO_3_CRYSTALLIZATION = ITEMS.register("che_jibpo3_crystallization",
                () -> new JiBpO3(new Item.Properties()
                ));
        // 黑酸鸡
        public static final RegistryObject<Item> CHE_JIBPO_4_CRYSTALLIZATION = ITEMS.register("che_jibpo4_crystallization",
                () -> new JiBpO4(new Item.Properties()
                ));

        // 氢氧化钠
        public static final RegistryObject<Item> CHE_NAOH = ITEMS.register("che_naoh",
                () -> new Item(new Item.Properties())
        );


//        public static final RegistryObject<Item> HYDROCHLORIC_ACID_BUCKET = ITEMS.register(
//                "hydrochloric_acid_bucket",
//                () -> new BucketItem(
//                        ModChemistryFluids.HYDROCHLORIC_ACID,
//                        new Item.Properties().stacksTo(1)
//                )
//        );


    }

//    public static class ModChemistryFluidBlocks {
//        public static final DeferredRegister<Block> BLOCKS =
//                DeferredRegister.create(ForgeRegistries.BLOCKS, Main.MODID);
//
//        public static final RegistryObject<LiquidBlock> HYDROCHLORIC_ACID = BLOCKS.register(
//                "hydrochloric_acid",
//                () -> new LiquidBlock(
//                        ModChemistryFluids.HYDROCHLORIC_ACID,
//                        BlockBehaviour.Properties.of()
//                                .mapColor(MapColor.COLOR_LIGHT_GREEN)
//                                .strength(100f)
//                                .noCollission()
//                                .noLootTable()
//                                .replaceable()
//                                .liquid()
//                )
//        );
//    }

    // ==================== 流体注册 ====================
//    public static class ModChemistryFluids {
//        public static final DeferredRegister<FluidType> FLUID_TYPES =
//                DeferredRegister.create(ForgeRegistries.Keys.FLUID_TYPES, Main.MODID);
//        public static final DeferredRegister<Fluid> FLUIDS =
//                DeferredRegister.create(ForgeRegistries.FLUIDS, Main.MODID);
//
//        // ----- 盐酸流体 -----
//        public static final RegistryObject<FluidType> HYDROCHLORIC_ACID_TYPE = FLUID_TYPES.register(
//                "hydrochloric_acid",
//                () -> new FluidType(
//                        FluidType.Properties.create()
//                                .descriptionId("fluid.trainees.hydrochloric_acid")
//                                .temperature(293) // 常温
//                                .density(1100)    // 比水略重
//                                .viscosity(1000)
//                                .canSwim(false)
//                                .canDrown(true)
//                                .supportsBoating(false)
//                ) {
//                    @Override
//                    public net.minecraft.network.chat.Component getDescription() {
//                        return net.minecraft.network.chat.Component.translatable("fluid.trainees.hydrochloric_acid");
//                    }
//                }
//        );
//
//        public static final RegistryObject<FlowingFluid> HYDROCHLORIC_ACID = FLUIDS.register(
//                "hydrochloric_acid",
//                () -> new ForgeFlowingFluid.Source(
//                        new ForgeFlowingFluid.Properties(
//                                HYDROCHLORIC_ACID_TYPE,
//                                HYDROCHLORIC_ACID,
//                                HYDROCHLORIC_ACID_FLOWING
//                        ).block(ModChemistryFluidBlocks.HYDROCHLORIC_ACID)
//                )
//        );
//
//        public static final RegistryObject<FlowingFluid> HYDROCHLORIC_ACID_FLOWING = FLUIDS.register(
//                "hydrochloric_acid_flowing",
//                () -> new ForgeFlowingFluid.Flowing(
//                        new ForgeFlowingFluid.Properties(
//                                HYDROCHLORIC_ACID_TYPE,
//                                HYDROCHLORIC_ACID,
//                                HYDROCHLORIC_ACID_FLOWING
//                        ).block(ModChemistryFluidBlocks.HYDROCHLORIC_ACID)
//                )
//        );
//
//
//    }



    public static class ModChemistryEquations {
        // HBpO分解反应: 2HBpO =光照= 2HBp + O2↑
        public static final ChemicalEquation HBPO_DECOMPOSITION =
                new ChemicalEquation.Builder("hbpo_decomposition")
                        .withName("HBpO光分解")
                        .withDescription("次黑酸在光照下分解为纽黑粉末和氧气")
                        .addReactant(ModChemistry.ModChemistryItems.CHE_HBPO_POWDER.get(), 2, "次黑酸")
                        .addProduct(ModChemistry.ModChemistryItems.CHE_HBP_POWDER.get(), 2, "纽黑粉末")
                        // .addProduct(ModChemistry.ModChemistryItems.CHE_O2.get(), 1, "氧气")
                        .withTimedConditions(
                                ReactionConditions.hbpoDecomposeCondition.get(),
                                ReactionConditions.hbpoDurationProvider.get()
                        )
//                        .addTag("decomposition")
//                        .addTag("photochemical")
//                        .addTag("redox")
                        .build();

        // Bp2与水反应: Bp2 + H2O == HBp + HBpO
        public static final ChemicalEquation BP2_WATER_REACTION =
                new ChemicalEquation.Builder("bp2_water_reaction")
                        .withName("黑单质水解")
                        .addReactant(ModItems.POWDER_ANTI.get(), 1, "黑单质")
                        .addProduct(ModChemistry.ModChemistryItems.CHE_HBP_POWDER.get(), 1, "纽黑粉末")
                        .addProduct(ModChemistry.ModChemistryItems.CHE_HBPO_POWDER.get(), 1, "次黑酸")
                        .withTimedConditions(
                                ReactionConditions.bp2AndWaterCondition.get(),
                                ReactionConditions.random5to10
                        )
//                        .addTag("hydrolysis")
//                        .addTag("redox")
                        .build();

        // Ji与水反应
        public static final ChemicalEquation JI_WATER_REACTION =
                new ChemicalEquation.Builder("ji_water_reaction")
                        .withName("鸡单质水解")
                        .addReactant(ModItems.TWO_HALF_INGOT.get(), 2)
                        .addProduct(ModChemistryItems.CHE_JIOH_INGOT.get(), 2)
                        .withTimedConditions(
                                ReactionConditions.jiAndWaterCondition.get(),
                                ReactionConditions.dur0
                        )
//                        .addTag("hydrolysis")
//                        .addTag("redox")
                        .build();

//        MachineChemicalEquation example = new MachineChemicalEquation.Builder("ji_water_reaction_machine")
//                .addReactant(2, ModItems.TWO_HALF_INGOT.get())
//                .addProduct(2, ModChemistryItems.CHE_JIOH_INGOT.get())
//                .addCondition(new CatalystCondition(List.of(new ItemStack(SomeItem.ITEM)), 0.1)) // 消耗概率10%
//                .withSequence(1)
//                .withDuration(100) // 100 ticks = 5秒
//                .addTag("machine")
//                .build();
    }

    public static class ModIons {

        public static final List<IonType> ALL_IONS = new ArrayList<>();

        // ==================== 阳离子（水溶液态） ====================
        // 命名规则：金属正1价写 _1，正2价及以上写 _[价数]；非金属正1价写 _plus

        public static final IonType H_plus = register(new IonType.Builder(
                new ResourceLocation(Main.MODID, "h_plus"), Phase.AQUEOUS)
                .molarMass(1.008)
                .specificHeat(0)
                .formationEnthalpy(0)
                .formationGibbs(0)
                .tag(IonTags.ACID)
                .tag(IonTags.CATION)
                .build()
        );

        public static final IonType Na_1 = register(new IonType.Builder(
                new ResourceLocation(Main.MODID, "na_1"), Phase.AQUEOUS)
                .molarMass(22.99)
                .formationEnthalpy(-240.1)
                .formationGibbs(-261.9)
                .tag(IonTags.CATION)
                .build()
        );

        public static final IonType K_1 = register(new IonType.Builder(
                new ResourceLocation(Main.MODID, "k_1"), Phase.AQUEOUS)
                .molarMass(39.10)
                .formationEnthalpy(-252.4)
                .formationGibbs(-283.3)
                .tag(IonTags.CATION)
                .build()
        );

        public static final IonType NH4_plus = register(new IonType.Builder(
                new ResourceLocation(Main.MODID, "nh4_plus"), Phase.AQUEOUS)
                .molarMass(18.04)
                .formationEnthalpy(-132.5)
                .formationGibbs(-79.3)
                .tag(IonTags.ACID)     // 铵根是弱酸
                .tag(IonTags.CATION)
                .build()
        );

        public static final IonType Fe_2 = register(new IonType.Builder(
                new ResourceLocation(Main.MODID, "fe_2"), Phase.AQUEOUS)
                .molarMass(55.85)
                .formationEnthalpy(-89.1)
                .formationGibbs(-78.9)
                .tag(IonTags.CATION)
                .tag(IonTags.REDUCER)   // Fe²⁺可被氧化
                .build()
        );

        public static final IonType Fe_3 = register(new IonType.Builder(
                new ResourceLocation(Main.MODID, "fe_3"), Phase.AQUEOUS)
                .molarMass(55.85)
                .formationEnthalpy(-48.5)
                .formationGibbs(-4.7)
                .tag(IonTags.CATION)
                .tag(IonTags.OXIDIZER)  // Fe³⁺可被还原
                .build()
        );

        public static final IonType Cu_1 = register(new IonType.Builder(
                new ResourceLocation(Main.MODID, "cu_1"), Phase.AQUEOUS)
                .molarMass(63.55)
                .formationEnthalpy(71.7)
                .formationGibbs(49.9)
                .tag(IonTags.CATION)
                .build()
        );

        public static final IonType Cu_2 = register(new IonType.Builder(
                new ResourceLocation(Main.MODID, "cu_2"), Phase.AQUEOUS)
                .molarMass(63.55)
                .formationEnthalpy(64.8)
                .formationGibbs(65.5)
                .tag(IonTags.CATION)
                .tag(IonTags.OXIDIZER)  // Cu²⁺可被还原
                .build()
        );

        // ==================== 阴离子（水溶液态） ====================
        // 命名规则：非金属负1价写 _minus，负2价及以上写 _[价数]minus

        public static final IonType Cl_minus = register(new IonType.Builder(
                new ResourceLocation(Main.MODID, "cl_minus"), Phase.AQUEOUS)
                .molarMass(35.45)
                .formationEnthalpy(-167.2)
                .formationGibbs(-131.2)
                .tag(IonTags.HALIDE)
                .tag(IonTags.ANION)
                .build()
        );

        public static final IonType OH_minus = register(new IonType.Builder(
                new ResourceLocation(Main.MODID, "oh_minus"), Phase.AQUEOUS)
                .molarMass(17.01)
                .formationEnthalpy(-230.0)
                .formationGibbs(-157.2)
                .tag(IonTags.BASE)
                .tag(IonTags.ANION)
                .build()
        );

        public static final IonType NO3_minus = register(new IonType.Builder(
                new ResourceLocation(Main.MODID, "no3_minus"), Phase.AQUEOUS)
                .molarMass(62.00)
                .formationEnthalpy(-207.4)
                .formationGibbs(-111.3)
                .tag(IonTags.OXIDIZER)
                .tag(IonTags.ANION)
                .build()
        );

        public static final IonType SO4_2minus = register(new IonType.Builder(
                new ResourceLocation(Main.MODID, "so4_2minus"), Phase.AQUEOUS)
                .molarMass(96.06)
                .formationEnthalpy(-909.3)
                .formationGibbs(-744.5)
                .tag(IonTags.ANION)
                .build()
        );

        public static final IonType CO3_2minus = register(new IonType.Builder(
                new ResourceLocation(Main.MODID, "co3_2minus"), Phase.AQUEOUS)
                .molarMass(60.01)
                .formationEnthalpy(-677.1)
                .formationGibbs(-527.8)
                .tag(IonTags.ANION)
                .build()
        );

        // ==================== 常见分子 ====================

        // ---- 气体（默认无后缀即为气态） ----
        public static final IonType HCl = register(new IonType.Builder(
                new ResourceLocation(Main.MODID, "hcl"), Phase.GAS)
                .molarMass(36.46)
                .specificHeat(0.798)
                .formationEnthalpy(-92.3)
                .formationGibbs(-95.3)
                .toxicityLevel(2)
                .flameColor(0xCCFFCC)
                .tag(IonTags.ACID)
                .tag(IonTags.VOLATILE)
                .build()
        );

        public static final IonType Cl2 = register(new IonType.Builder(
                new ResourceLocation(Main.MODID, "cl2"), Phase.GAS)
                .molarMass(70.90)
                .specificHeat(0.478)
                .formationEnthalpy(0)
                .formationGibbs(0)
                .toxicityLevel(3)
                .flameColor(0xC8FF00)
                .tag(IonTags.OXIDIZER)
                .tag(IonTags.HALOGEN)
                .build()
        );

        public static final IonType O2 = register(new IonType.Builder(
                new ResourceLocation(Main.MODID, "o2"), Phase.GAS)
                .molarMass(32.00)
                .specificHeat(0.918)
                .formationEnthalpy(0)
                .formationGibbs(0)
                .tag(IonTags.OXIDIZER)
                .build()
        );

        public static final IonType H2 = register(new IonType.Builder(
                new ResourceLocation(Main.MODID, "h2"), Phase.GAS)
                .molarMass(2.016)
                .specificHeat(14.30)
                .formationEnthalpy(0)
                .formationGibbs(0)
                .tag(IonTags.REDUCER)
                .build()
        );

        public static final IonType CO2 = register(new IonType.Builder(
                new ResourceLocation(Main.MODID, "co2"), Phase.GAS)
                .molarMass(44.01)
                .specificHeat(0.846)
                .formationEnthalpy(-393.5)
                .formationGibbs(-394.4)
                .build()
        );

        public static final IonType SO2 = register(new IonType.Builder(
                new ResourceLocation(Main.MODID, "so2"), Phase.GAS)
                .molarMass(64.07)
                .specificHeat(0.622)
                .formationEnthalpy(-296.8)
                .formationGibbs(-300.1)
                .toxicityLevel(2)
                .tag(IonTags.ACID)
                .tag(IonTags.REDUCER)
                .build()
        );

        public static final IonType NH3 = register(new IonType.Builder(
                new ResourceLocation(Main.MODID, "nh3"), Phase.GAS)
                .molarMass(17.03)
                .specificHeat(2.06)
                .formationEnthalpy(-46.1)
                .formationGibbs(-16.4)
                .toxicityLevel(2)
                .tag(IonTags.BASE)
                .tag(IonTags.REDUCER)
                .build()
        );

        public static final IonType H2O_GAS = register(new IonType.Builder(
                new ResourceLocation(Main.MODID, "h2o_gas"), Phase.GAS)
                .molarMass(18.02)
                .specificHeat(33.6)
                .formationEnthalpy(-241.8)
                .formationGibbs(-228.6)
                .build()
        );

        // ---- 液体 ----
        public static final IonType H2O = register(new IonType.Builder(
                new ResourceLocation(Main.MODID, "h2o"), Phase.LIQUID)
                .molarMass(18.02)
                .specificHeat(75.3)
                .formationEnthalpy(-285.8)
                .formationGibbs(-237.1)
                .build()
        );

        // ---- 固体（默认无后缀即为固态） ----
        public static final IonType NaCl = register(new IonType.Builder(
                new ResourceLocation(Main.MODID, "nacl"), Phase.SOLID)
                .molarMass(58.44)
                .specificHeat(50.5)
                .formationEnthalpy(-411.1)
                .formationGibbs(-384.1)
                .build()
        );

        public static final IonType NaCl_MOLTEN = register(new IonType.Builder(
                new ResourceLocation(Main.MODID, "nacl_molten"), Phase.LIQUID)
                .molarMass(58.44)
                .specificHeat(66.9)
                .formationEnthalpy(-385.8)
                .formationGibbs(-359.4)
                .tag(IonTags.MOLTEN_SALT)
                .build()
        );

        public static final IonType NaOH = register(new IonType.Builder(
                new ResourceLocation(Main.MODID, "naoh"), Phase.SOLID)
                .molarMass(40.00)
                .specificHeat(59.5)
                .formationEnthalpy(-425.6)
                .formationGibbs(-379.7)
                .toxicityLevel(1)
                .tag(IonTags.BASE)
                .build()
        );

        public static final IonType NaOH_MOLTEN = register(new IonType.Builder(
                new ResourceLocation(Main.MODID, "naoh_molten"), Phase.LIQUID)
                .molarMass(40.00)
                .specificHeat(65.3)
                .formationEnthalpy(-397.8)
                .formationGibbs(-350.2)
                .tag(IonTags.MOLTEN_SALT)
                .build()
        );

        public static final IonType Na2CO3 = register(new IonType.Builder(
                new ResourceLocation(Main.MODID, "na2co3"), Phase.SOLID)
                .molarMass(105.99)
                .specificHeat(87.6)
                .formationEnthalpy(-1130.7)
                .formationGibbs(-1044.4)
                .tag(IonTags.BASE)
                .build()
        );

        // ---- 金属单质 ----
        public static final IonType Fe = register(new IonType.Builder(
                new ResourceLocation(Main.MODID, "fe"), Phase.SOLID)
                .molarMass(55.85)
                .specificHeat(25.1)
                .formationEnthalpy(0)
                .formationGibbs(0)
                .tag(IonTags.METAL)
                .tag(IonTags.REDUCER)
                .build()
        );

        public static final IonType Cu = register(new IonType.Builder(
                new ResourceLocation(Main.MODID, "cu"), Phase.SOLID)
                .molarMass(63.55)
                .specificHeat(24.4)
                .formationEnthalpy(0)
                .formationGibbs(0)
                .tag(IonTags.METAL)
                .tag(IonTags.REDUCER)
                .build()
        );

        // ---- 特殊：浓硫酸（液态分子） ----
        public static final IonType H2SO4 = register(new IonType.Builder(
                new ResourceLocation(Main.MODID, "h2so4"), Phase.LIQUID)
                .molarMass(98.08)
                .specificHeat(1.42)
                .formationEnthalpy(-814.0)
                .formationGibbs(-690.0)
                .tag(IonTags.ACID)
                .tag(IonTags.OXIDIZER)   // 浓硫酸有强氧化性
                .tag(IonTags.VOLATILE)
                .build()
        );

        // ==================== 矿物与氧化物 ====================
        public static final IonType Fe2O3 = register(new IonType.Builder(
                new ResourceLocation(Main.MODID, "fe2o3"), Phase.SOLID)
                .molarMass(159.69)
                .specificHeat(0.67)          // J/(g·K) 近似
                .formationEnthalpy(-824.2)
                .formationGibbs(-742.2)
                .tag(IonTags.OXIDE)
                .tag(IonTags.VALUABLE)       // 有效成分（矿石中有价值的部分）
                .build()
        );

        public static final IonType SiO2 = register(new IonType.Builder(
                new ResourceLocation(Main.MODID, "sio2"), Phase.SOLID)
                .molarMass(60.08)
                .specificHeat(0.73)
                .formationEnthalpy(-910.9)
                .formationGibbs(-856.3)
                .tag(IonTags.OXIDE)
                .tag(IonTags.GANGUE)         // 脉石（杂质）
                .build()
        );

        public static final IonType Cu2O = register(new IonType.Builder(
                new ResourceLocation(Main.MODID, "cu2o"), Phase.SOLID)
                .molarMass(143.09)
                .specificHeat(0.43)
                .formationEnthalpy(-168.6)
                .formationGibbs(-146.0)
                .tag(IonTags.OXIDE)
                .tag(IonTags.VALUABLE)
                .build()
        );

        // ==================== 金属离子（水溶液态） ====================
        public static final IonType Mg_2 = register(new IonType.Builder(
                new ResourceLocation(Main.MODID, "mg_2"), Phase.AQUEOUS)
                .molarMass(24.31)
                .formationEnthalpy(-466.9)
                .formationGibbs(-454.8)
                .tag(IonTags.CATION)
                .build()
        );

        public static final IonType Ca_2 = register(new IonType.Builder(
                new ResourceLocation(Main.MODID, "ca_2"), Phase.AQUEOUS)
                .molarMass(40.08)
                .formationEnthalpy(-542.8)
                .formationGibbs(-553.6)
                .tag(IonTags.CATION)
                .build()
        );

        // ==================== 有机物（原油组分） ====================
// 烷烃类
        public static final IonType C8H18 = register(new IonType.Builder(
                new ResourceLocation(Main.MODID, "c8h18"), Phase.LIQUID)
                .molarMass(114.23)
                .specificHeat(2.22)
                .formationEnthalpy(-249.9)
                .formationGibbs(6.4)
                .tag(IonTags.HYDROCARBON)
                .tag(IonTags.ALKANE)
                .build()
        );

        public static final IonType C10H22 = register(new IonType.Builder(
                new ResourceLocation(Main.MODID, "c10h22"), Phase.LIQUID)
                .molarMass(142.28)
                .specificHeat(2.18)
                .formationEnthalpy(-300.9)
                .formationGibbs(17.3)
                .tag(IonTags.HYDROCARBON)
                .tag(IonTags.ALKANE)
                .build()
        );

        public static final IonType C12H26 = register(new IonType.Builder(
                new ResourceLocation(Main.MODID, "c12h26"), Phase.LIQUID)
                .molarMass(170.34)
                .specificHeat(2.15)
                .formationEnthalpy(-352.0)
                .formationGibbs(28.1)
                .tag(IonTags.HYDROCARBON)
                .tag(IonTags.ALKANE)
                .build()
        );

        // 芳香烃
        public static final IonType C6H6 = register(new IonType.Builder(
                new ResourceLocation(Main.MODID, "c6h6"), Phase.LIQUID)
                .molarMass(78.11)
                .specificHeat(1.72)
                .formationEnthalpy(49.0)
                .formationGibbs(124.5)
                .toxicityLevel(2)           // 苯有毒
                .tag(IonTags.HYDROCARBON)
                .tag(IonTags.AROMATIC)
                .tag(IonTags.VOLATILE)
                .build()
        );

        // ==================== 单质 ====================
        public static final IonType SULFUR = register(new IonType.Builder(
                new ResourceLocation(Main.MODID, "sulfur"), Phase.SOLID)
                .molarMass(32.07)
                .specificHeat(0.71)
                .formationEnthalpy(0)
                .formationGibbs(0)
                .tag(IonTags.NONMETAL)
                .tag(IonTags.REDUCER)
                .build()
        );

        // ==================== 辅助方法 ====================

        private static IonType register(IonType type) {
            ALL_IONS.add(type);
            return type;
        }

        public static IonType getById(ResourceLocation id) {
            return ALL_IONS.stream()
                    .filter(ion -> ion.getId().equals(id))
                    .findFirst()
                    .orElse(null);
        }
    }

    // ==================== 反应注册 ====================
    public static class Reactions {
        private static final ReactionGraph GRAPH = ReactionGraph.getInstance();

        public static void registerAll() {
            // ====== 酸碱中和：H+ + OH- → H2O ======
            ReactionRule neutralization = new ReactionRule.Builder(
                    new ResourceLocation(Main.MODID, "neutralization"))
                    .reactant(ModIons.H_plus, 1)
                    .reactant(ModIons.OH_minus, 1)
                    .product(ModIons.H2O, 1)
                    .deltaH(-57.3)      // kJ/mol
                    .deltaG(-79.9)
                    .equilibriumConstant(1e14)  // 非常彻底
                    .activationEnergy(5.0)      // 极低能垒
                    .preExponentialFactor(1e11)
                    .build();
            // 双向边：确保无论谁最后加入都能触发
            GRAPH.addBidirectionalEdges(ModIons.H_plus, ModIons.OH_minus, neutralization);

            // ====== 沉淀反应：Ag+ + Cl- → AgCl↓ ======
            // 注意：AgCl 需要先注册（我们暂时没有 Ag+，可以后续添加）
            // 这里留作示例，暂不启用

            // ====== 分解反应：H2O2 → H2O + O2（自环） ======
            // 注意：H2O2 需要先注册，暂不启用

            Main.LOGGER.info("[Chemistry] Registered {} reaction rules", GRAPH.getAllEdges().size());
        }

        /**
         * 获取反应图（供其他类使用）
         */
        public static ReactionGraph getGraph() {
            return GRAPH;
        }
    }
}
