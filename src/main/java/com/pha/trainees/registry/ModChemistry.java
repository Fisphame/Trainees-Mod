package com.pha.trainees.registry;

import com.pha.trainees.Main;
import com.pha.trainees.block.CheJibpBlock;
import com.pha.trainees.chemistry.block.BeakerBlock;
import com.pha.trainees.chemistry.block.ElectrolysisCellBlock;
import com.pha.trainees.chemistry.blockentity.BeakerBlockEntity;
import com.pha.trainees.chemistry.blockentity.ElectrolysisCellBlockEntity;
//import com.pha.trainees.chemistry.fluid.HydrochloricAcidFluid;
import com.pha.trainees.chemistry.item.AnalyzerItemCreative;
import com.pha.trainees.chemistry.item.AnalyzerItemNormal;
import com.pha.trainees.chemistry.item.SubstanceItem;
import com.pha.trainees.chemistry.particle.IonTags;
import com.pha.trainees.chemistry.particle.IonType;
import com.pha.trainees.chemistry.particle.Phase;
import com.pha.trainees.chemistry.reaction.ReactionEdge;
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

        // 电解槽（§19.10）：双电极接入面、双面供电、产物分拣
        public static final RegistryObject<ElectrolysisCellBlock> ELECTROLYSIS_CELL = BLOCKS.register(
                "electrolysis_cell",
                () -> new ElectrolysisCellBlock(BlockBehaviour.Properties.of()
                        .mapColor(MapColor.METAL)
                        .strength(3.0f, 6.0f)
                        .requiresCorrectToolForDrops()
                        .sound(SoundType.METAL)
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

        // 电解槽
        public static final RegistryObject<Item> ELECTROLYSIS_CELL = ITEMS.register("electrolysis_cell",
                () -> new BlockItem(ModChemistryBlocks.ELECTROLYSIS_CELL.get(),
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

        public static final RegistryObject<BlockEntityType<ElectrolysisCellBlockEntity>> ELECTROLYSIS_CELL =
                BLOCK_ENTITIES.register("electrolysis_cell",
                        () -> BlockEntityType.Builder.of(ElectrolysisCellBlockEntity::new,
                                ModChemistryBlocks.ELECTROLYSIS_CELL.get()).build(null)
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

        // 离子交换膜（§19.10 电解槽：放入膜槽使产物分侧纯化；后期材料升级版后续再换材质/配方）
        public static final RegistryObject<Item> ION_MEMBRANE = ITEMS.register("ion_membrane",
                () -> new Item(
                        new Item.Properties()
                                .stacksTo(16)
                ));
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
                .specificHeat(29.1)
                .formationEnthalpy(-92.3)
                .formationGibbs(-95.3)
                .toxicityLevel(2)
                .flameColor(0xCCFFCC)
                .tag(IonTags.ACID)
                .tag(IonTags.VOLATILE)
                .build()
        );

        // 盐酸水溶液（强电解质，溶于水后完全电离为 H⁺ + Cl⁻）
        public static final IonType HCL_AQ = register(new IonType.Builder(
                new ResourceLocation(Main.MODID, "hcl_aq"), Phase.AQUEOUS)
                .molarMass(36.46)
                .specificHeat(74.0)
                .formationEnthalpy(-167.2)
                .formationGibbs(-131.2)
                .toxicityLevel(2)
                .tag(IonTags.ACID)
                .build()
        );

        public static final IonType Cl2 = register(new IonType.Builder(
                new ResourceLocation(Main.MODID, "cl2"), Phase.GAS)
                .molarMass(70.90)
                .specificHeat(33.9)
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
                .specificHeat(29.4)
                .formationEnthalpy(0)
                .formationGibbs(0)
                .tag(IonTags.OXIDIZER)
                .build()
        );

        public static final IonType N2 = register(new IonType.Builder(
                new ResourceLocation(Main.MODID, "n2"), Phase.GAS)
                .molarMass(28.01)
                .specificHeat(29.1)
                .formationEnthalpy(0)
                .formationGibbs(0)
                .build()
        );

        public static final IonType CO = register(new IonType.Builder(
                new ResourceLocation(Main.MODID, "co"), Phase.GAS)
                .molarMass(28.01)
                .specificHeat(29.1)
                .formationEnthalpy(-110.5)
                .formationGibbs(-137.2)
                .toxicityLevel(2)
                .tag(IonTags.REDUCER)
                .build()
        );

        public static final IonType C = register(new IonType.Builder(
                new ResourceLocation(Main.MODID, "c"), Phase.SOLID)
                .molarMass(12.01)
                .specificHeat(8.5)
                .formationEnthalpy(0)
                .formationGibbs(0)
                .tag(IonTags.REDUCER)
                .build()
        );

        public static final IonType H2 = register(new IonType.Builder(
                new ResourceLocation(Main.MODID, "h2"), Phase.GAS)
                .molarMass(2.016)
                .specificHeat(28.8)
                .formationEnthalpy(0)
                .formationGibbs(0)
                .tag(IonTags.REDUCER)
                .build()
        );

        public static final IonType CO2 = register(new IonType.Builder(
                new ResourceLocation(Main.MODID, "co2"), Phase.GAS)
                .molarMass(44.01)
                .specificHeat(37.2)
                .formationEnthalpy(-393.5)
                .formationGibbs(-394.4)
                .build()
        );

        public static final IonType SO2 = register(new IonType.Builder(
                new ResourceLocation(Main.MODID, "so2"), Phase.GAS)
                .molarMass(64.07)
                .specificHeat(39.9)
                .formationEnthalpy(-296.8)
                .formationGibbs(-300.1)
                .toxicityLevel(2)
                .tag(IonTags.ACID)
                .tag(IonTags.REDUCER)
                .build()
        );

        public static final IonType SO3 = register(new IonType.Builder(
                new ResourceLocation(Main.MODID, "so3"), Phase.GAS)
                .molarMass(80.07)
                .specificHeat(50.0)
                .formationEnthalpy(-395.7)
                .formationGibbs(-371.1)
                .toxicityLevel(2)
                .tag(IonTags.ACID)
                .build()
        );

        public static final IonType NH3 = register(new IonType.Builder(
                new ResourceLocation(Main.MODID, "nh3"), Phase.GAS)
                .molarMass(17.03)
                .specificHeat(35.1)
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

        public static final IonType H2O2 = register(new IonType.Builder(
                new ResourceLocation(Main.MODID, "h2o2"), Phase.LIQUID)
                .molarMass(34.01)
                .specificHeat(89.1)
                .formationEnthalpy(-187.8)
                .formationGibbs(-120.4)
                .tag(IonTags.OXIDIZER)
                .tag(IonTags.REDUCER)  // 既可作氧化剂也可作还原剂
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

        public static final IonType MnO2 = register(new IonType.Builder(
                new ResourceLocation(Main.MODID, "mno2"), Phase.SOLID)
                .molarMass(86.94)
                .specificHeat(46.9)
                .formationEnthalpy(-520.0)
                .formationGibbs(-465.0)
                .tag(IonTags.CATALYST)
                .tag(IonTags.OXIDE)
                .build()
        );

        public static final IonType CaCO3 = register(new IonType.Builder(
                new ResourceLocation(Main.MODID, "caco3"), Phase.SOLID)
                .molarMass(100.09)
                .specificHeat(84.1)
                .formationEnthalpy(-1207.6)
                .formationGibbs(-1128.8)
                .tag(IonTags.PRECIPITATE)
                .build()
        );

        public static final IonType CaO = register(new IonType.Builder(
                new ResourceLocation(Main.MODID, "cao"), Phase.SOLID)
                .molarMass(56.08)
                .specificHeat(42.8)
                .formationEnthalpy(-635.1)
                .formationGibbs(-604.0)
                .tag(IonTags.OXIDE)
                .build()
        );

        public static final IonType Ca_OH_2 = register(new IonType.Builder(
                new ResourceLocation(Main.MODID, "caoh2"), Phase.SOLID)
                .molarMass(74.09)
                .specificHeat(87.5)
                .formationEnthalpy(-986.1)
                .formationGibbs(-898.5)
                .tag(IonTags.BASE)
                .build()
        );

        public static final IonType NaHSO4 = register(new IonType.Builder(
                new ResourceLocation(Main.MODID, "nahso4"), Phase.SOLID)
                .molarMass(120.06)
                .specificHeat(120.0)
                .formationEnthalpy(-1125.5)
                .formationGibbs(-992.8)
                .tag(IonTags.ACID)
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
                .specificHeat(139.3)
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
                .specificHeat(107.0)         // J/(mol·K)（原按 J/(g·K) 填写，已×摩尔质量换算）
                .formationEnthalpy(-824.2)
                .formationGibbs(-742.2)
                .tag(IonTags.OXIDE)
                .tag(IonTags.VALUABLE)       // 有效成分（矿石中有价值的部分）
                .build()
        );

        public static final IonType SiO2 = register(new IonType.Builder(
                new ResourceLocation(Main.MODID, "sio2"), Phase.SOLID)
                .molarMass(60.08)
                .specificHeat(43.9)
                .formationEnthalpy(-910.9)
                .formationGibbs(-856.3)
                .tag(IonTags.OXIDE)
                .tag(IonTags.GANGUE)         // 脉石（杂质）
                .build()
        );

        public static final IonType Cu2O = register(new IonType.Builder(
                new ResourceLocation(Main.MODID, "cu2o"), Phase.SOLID)
                .molarMass(143.09)
                .specificHeat(61.5)
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
                .specificHeat(253.6)
                .formationEnthalpy(-249.9)
                .formationGibbs(6.4)
                .tag(IonTags.HYDROCARBON)
                .tag(IonTags.ALKANE)
                .build()
        );

        public static final IonType C10H22 = register(new IonType.Builder(
                new ResourceLocation(Main.MODID, "c10h22"), Phase.LIQUID)
                .molarMass(142.28)
                .specificHeat(310.2)
                .formationEnthalpy(-300.9)
                .formationGibbs(17.3)
                .tag(IonTags.HYDROCARBON)
                .tag(IonTags.ALKANE)
                .build()
        );

        public static final IonType C12H26 = register(new IonType.Builder(
                new ResourceLocation(Main.MODID, "c12h26"), Phase.LIQUID)
                .molarMass(170.34)
                .specificHeat(366.2)
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
                .specificHeat(134.3)
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
                .specificHeat(22.8)
                .formationEnthalpy(0)
                .formationGibbs(0)
                .tag(IonTags.NONMETAL)
                .tag(IonTags.REDUCER)
                .build()
        );

        // ==================== 氮氧化物与碳酸氢根（可逆/多反应物/浓度依赖反应用） ====================
        public static final IonType N2O4 = register(new IonType.Builder(
                new ResourceLocation(Main.MODID, "n2o4"), Phase.GAS)
                .molarMass(92.01)
                .specificHeat(77.3)
                .formationEnthalpy(9.16)
                .formationGibbs(97.8)
                .toxicityLevel(2)
                .flameColor(0xB4552A)
                .build()
        );

        public static final IonType NO2 = register(new IonType.Builder(
                new ResourceLocation(Main.MODID, "no2"), Phase.GAS)
                .molarMass(46.01)
                .specificHeat(37.2)
                .formationEnthalpy(33.2)
                .formationGibbs(51.3)
                .toxicityLevel(2)
                .flameColor(0xB4552A)
                .build()
        );

        public static final IonType HCO3_minus = register(new IonType.Builder(
                new ResourceLocation(Main.MODID, "hco3_minus"), Phase.AQUEOUS)
                .molarMass(61.02)
                .formationEnthalpy(-692.0)
                .formationGibbs(-586.8)
                .tag(IonTags.BASE)
                .build()
        );

        // ==================== 金属钠（电解产物，蓝本 §17） ====================
        public static final IonType Na = register(new IonType.Builder(
                new ResourceLocation(Main.MODID, "na"), Phase.SOLID)
                .molarMass(22.99)
                .specificHeat(28.2)
                .formationEnthalpy(0)
                .formationGibbs(0)
                .tag(IonTags.METAL)
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
            // 清空已有边，保证重复调用（如服务器重启）不会累积重复规则
            GRAPH.clear();

            // ====== 酸碱中和：H+ + OH- → H2O ======
            ReactionRule neutralization = new ReactionRule.Builder(
                    new ResourceLocation(Main.MODID, "neutralization"))
                    .reactant(ModIons.H_plus, 1)
                    .reactant(ModIons.OH_minus, 1)
                    .product(ModIons.H2O, 1)
                    .deltaHComputed()   // ΔH = ΔHf(H2O) - ΔHf(H+) - ΔHf(OH-) = -55.8 kJ/mol
                    .deltaGComputed()   // ΔG = ΔGf(H2O) - ΔGf(H+) - ΔGf(OH-) = -79.9 kJ/mol
                    .activationEnergy(5.0)      // 极低能垒
                    .preExponentialFactor(1e11)
                    .build();
            // 双向边：确保无论谁最后加入都能触发
            GRAPH.addBidirectionalEdges(ModIons.H_plus, ModIons.OH_minus, neutralization);

            // ====== 沉淀反应：Ag+ + Cl- → AgCl↓ ======
            // 注意：AgCl 需要先注册（我们暂时没有 Ag+，可以后续添加）
            // 这里留作示例，暂不启用

            // ====== 分解反应：H2O2 → H2O + O2（自环） ======
            // ====== 双氧水分解（无催化剂） ======
            ReactionRule h2o2DecomposeNoCatalyst = new ReactionRule.Builder(
                    new ResourceLocation(Main.MODID, "h2o2_decompose_no_catalyst")
            )
                    .reactant(ModIons.H2O2, 2)
                    .product(ModIons.H2O, 2)
                    .product(ModIons.O2, 1)
                    .deltaHComputed()   // ΔH = 2ΔHf(H2O) - 2ΔHf(H2O2) = -196.0 kJ/mol
                    .deltaGComputed()   // ΔG = 2ΔGf(H2O) - 2ΔGf(H2O2) = -233.4 kJ/mol
                    .activationEnergy(75.0)   // 较高能垒，常温下几乎不反应
                    .preExponentialFactor(1e6)
                    .selfLoop(true)
                    .build();

            // 无催化剂版本：自环，低优先级
            GRAPH.addEdge(ModIons.H2O2, ModIons.H2O2, h2o2DecomposeNoCatalyst);

            // ====== 双氧水分解（有催化剂 MnO₂） ======
            ReactionRule h2o2DecomposeWithCatalyst = new ReactionRule.Builder(
                    new ResourceLocation(Main.MODID, "h2o2_decompose_with_catalyst")
            )
                    .reactant(ModIons.H2O2, 2)
                    .product(ModIons.H2O, 2)
                    .product(ModIons.O2, 1)
                    .precondition(ModIons.MnO2, 1)   // 需要 MnO₂ 作为催化剂（不消耗）
                    .deltaHComputed()                // 与无催化剂版本相同
                    .deltaGComputed()                // 与无催化剂版本相同
                    .activationEnergy(30.0)          // 低能垒，常温下快速反应
                    .preExponentialFactor(1e10)      // 比无催化剂版本高一个数量级
                    .gamePriorityBias(2.0)           // 提高优先级评分
                    .selfLoop(true)
                    .build();

            // 有催化剂版本：自环，高优先级
            GRAPH.addEdge(ModIons.H2O2, ModIons.H2O2, h2o2DecomposeWithCatalyst);

            // ====== 碳酸钙沉淀：Ca²⁺ + CO₃²⁻ → CaCO₃↓ ======
            ReactionRule calciumCarbonatePrecipitate = new ReactionRule.Builder(
                    new ResourceLocation(Main.MODID, "caco3_precipitate")
            )
                    .reactant(ModIons.Ca_2, 1)
                    .reactant(ModIons.CO3_2minus, 1)
                    .product(ModIons.CaCO3, 1)
                    .deltaHComputed()   // ΔH = ΔHf(CaCO3) - ΔHf(Ca2+) - ΔHf(CO3 2-) = +12.3 kJ/mol
                    .deltaGComputed()   // ΔG = ΔGf(CaCO3) - ΔGf(Ca2+) - ΔGf(CO3 2-) = -47.4 kJ/mol（自发）
                    .activationEnergy(10.0)   // 低能垒
                    .preExponentialFactor(1e10)
                    .build();

            // 双向边
            GRAPH.addBidirectionalEdges(ModIons.Ca_2, ModIons.CO3_2minus, calciumCarbonatePrecipitate);

            // ====== NaCl 电离：NaCl(s) → Na⁺ + Cl⁻（强电解质） ======
            // 水作为电离介质（前置条件：检测存在但不消耗），符合蓝本 §5.1 催化剂机制
            ReactionRule naclDissociation = new ReactionRule.Builder(
                    new ResourceLocation(Main.MODID, "nacl_dissociation"))
                    .reactant(ModIons.NaCl, 1)
                    .product(ModIons.Na_1, 1)
                    .product(ModIons.Cl_minus, 1)
                    .precondition(ModIons.H2O, 1)
                    .deltaHComputed()   // ΔH = ΔHf(Na+) + ΔHf(Cl-) - ΔHf(NaCl) = +3.8 kJ/mol（微吸热，符合实际）
                    .deltaGComputed()   // ΔG = ΔGf(Na+) + ΔGf(Cl-) - ΔGf(NaCl) = -9.0 kJ/mol（自发）
                    // 物理 K(298)≈38（≈NaCl 溶解度积 Ksp）。常规用量下 Q=[Na⁺][Cl⁻] < 38 仍几乎完全电离。
                    .activationEnergy(5.0)          // 极低能垒，遇水即电离
                    .preExponentialFactor(1e11)
                    .selfLoop(true)
                    .build();

            // 自环：分解/电离类反应，由 Tick 轮询驱动
            GRAPH.addEdge(ModIons.NaCl, ModIons.NaCl, naclDissociation);

            // ====== 可逆反应：N₂O₄ ⇌ 2NO₂（蓝本 §2 可逆反应） ======
            // 正、逆视为两个独立 ReactionRule（各自自环），ΔH/ΔG 由生成数据计算，
            // 使 K_rev = 1/K_fwd（范特霍夫），引擎以 Q vs K 自动决定反应方向，不震荡
            ReactionRule n2o4Decompose = new ReactionRule.Builder(
                    new ResourceLocation(Main.MODID, "n2o4_decompose"))
                    .reactant(ModIons.N2O4, 1)
                    .product(ModIons.NO2, 2)
                    .deltaHComputed()   // ΔH = 2ΔHf(NO2) - ΔHf(N2O4) = +57.2 kJ/mol（吸热）
                    .deltaGComputed()   // ΔG = 2ΔGf(NO2) - ΔGf(N2O4) = +4.8 kJ/mol（常温偏向 N₂O₄）
                    .equilibriumConstant(1.0)   // 显式覆盖（可逆玩法）：K°=1.0，约293K时 K≈0.676 与实测平衡一致
                    .equilibriumOverridden()
                    .activationEnergy(60.0)
                    .preExponentialFactor(1e8)
                    .selfLoop(true)
                    .build();
            GRAPH.addEdge(ModIons.N2O4, ModIons.N2O4, n2o4Decompose);

            ReactionRule no2Combine = new ReactionRule.Builder(
                    new ResourceLocation(Main.MODID, "no2_combine"))
                    .reactant(ModIons.NO2, 2)
                    .product(ModIons.N2O4, 1)
                    .deltaHComputed()   // ΔG = -4.8 kJ/mol（自发），优先级略高于正反应
                    .deltaGComputed()
                    .equilibriumConstant(1.0)   // 显式覆盖：与正反应同锚点，维持可逆平衡
                    .equilibriumOverridden()
                    .activationEnergy(60.0)
                    .preExponentialFactor(1e8)
                    .selfLoop(true)
                    .build();
            GRAPH.addEdge(ModIons.NO2, ModIons.NO2, no2Combine);

            // ====== 三反应物完全矩阵：NH₃ + CO₂ + H₂O → NH₄⁺ + HCO₃⁻（制碱法步骤） ======
            // 三个反应物两两双向，共 3×2 = 6 条边，保证任意添加顺序都能触发
            ReactionRule ammoniaBicarbonate = new ReactionRule.Builder(
                    new ResourceLocation(Main.MODID, "ammonia_bicarbonate"))
                    .reactant(ModIons.NH3, 1)
                    .reactant(ModIons.CO2, 1)
                    .reactant(ModIons.H2O, 1)
                    .product(ModIons.NH4_plus, 1)
                    .product(ModIons.HCO3_minus, 1)
                    .deltaHComputed()   // ΔH = -99.1 kJ/mol（放热）
                    .deltaGComputed()   // ΔG = -18.2 kJ/mol（自发）
                    .activationEnergy(40.0)
                    .preExponentialFactor(1e9)
                    .build();
            GRAPH.addBidirectionalEdges(ModIons.NH3, ModIons.CO2, ammoniaBicarbonate);
            GRAPH.addBidirectionalEdges(ModIons.NH3, ModIons.H2O, ammoniaBicarbonate);
            GRAPH.addBidirectionalEdges(ModIons.CO2, ModIons.H2O, ammoniaBicarbonate);

            // ====== 浓度依赖复杂反应：碳酸盐遇酸（蓝本 §5.2） ======
            // 同一反应物 CO₃²⁻ 与 H⁺，产物由 [H⁺] 决定：低酸 → HCO₃⁻，高酸 → CO₂↑
            // 两条竞争规则各绑一个以 [H⁺] 为自变量的动态优先级函数，引擎按浓度选择主导路径
            ReactionRule carbonateToBicarbonate = new ReactionRule.Builder(
                    new ResourceLocation(Main.MODID, "carbonate_to_bicarbonate"))
                    .reactant(ModIons.H_plus, 1)
                    .reactant(ModIons.CO3_2minus, 1)
                    .product(ModIons.HCO3_minus, 1)
                    .deltaHComputed()   // ΔG = -59.0 kJ/mol
                    .deltaGComputed()
                    .activationEnergy(40.0)
                    .preExponentialFactor(1e9)
                    .dynamicPriorityBias(c -> {
                        double h = c.getEffectiveConcentration(ModIons.H_plus);
                        return 4.0 / (4.0 + h);   // 低酸路径：随 [H⁺] 升高而减弱
                    })
                    .build();
            GRAPH.addBidirectionalEdges(ModIons.H_plus, ModIons.CO3_2minus, carbonateToBicarbonate);

            ReactionRule carbonateToCo2 = new ReactionRule.Builder(
                    new ResourceLocation(Main.MODID, "carbonate_to_co2"))
                    .reactant(ModIons.H_plus, 2)
                    .reactant(ModIons.CO3_2minus, 1)
                    .product(ModIons.H2O, 1)
                    .product(ModIons.CO2, 1)
                    .deltaHComputed()   // ΔG = -103.7 kJ/mol（更彻底）
                    .deltaGComputed()
                    .activationEnergy(40.0)
                    .preExponentialFactor(1e9)
                    .dynamicPriorityBias(c -> {
                        double h = c.getEffectiveConcentration(ModIons.H_plus);
                        return h / (4.0 + h);   // 高酸路径：随 [H⁺] 升高而占优
                    })
                    .build();
            GRAPH.addBidirectionalEdges(ModIons.H_plus, ModIons.CO3_2minus, carbonateToCo2);

            // ====== 电解：2NaCl(熔融) → 2Na + Cl₂（蓝本 §17） ======
            // 自环分解反应。通电时引擎用 ΔG_eff = ΔG - W 参与优先级与平衡（W 取理论最小电功 = ΔG，
            // 由生成数据折算），未通电则被门控拦截、不可运行。
            ReactionRule naclElectrolysis = new ReactionRule.Builder(
                    new ResourceLocation(Main.MODID, "nacl_electrolysis"))
                    .reactant(ModIons.NaCl_MOLTEN, 2)
                    .product(ModIons.Na, 2)
                    .product(ModIons.Cl2, 1)
                    .deltaHComputed()          // ΔH = +771.6 kJ（吸热）
                    .deltaGComputed()          // ΔG = +718.8 kJ（非自发，需通电）
                    .electricalWorkComputed()  // W = ΔG = 718.8 kJ（理论最小电功）
                    .activationEnergy(50.0)
                    .preExponentialFactor(1e8)  // k≈0.17 s⁻¹，约12秒可见推进
                    .selfLoop(true)
                    .build();
            GRAPH.addEdge(ModIons.NaCl_MOLTEN, ModIons.NaCl_MOLTEN, naclElectrolysis);

            // ====== 硫磺燃烧：S + O₂ → SO₂ ======
            ReactionRule sulfurBurn = new ReactionRule.Builder(
                    new ResourceLocation(Main.MODID, "sulfur_burn"))
                    .reactant(ModIons.SULFUR, 1)
                    .reactant(ModIons.O2, 1)
                    .product(ModIons.SO2, 1)
                    .deltaHComputed()   // ΔH = -296.8 kJ（放热）
                    .deltaGComputed()   // ΔG = -300.1 kJ（自发）
                    // 物理公式 K = exp(-ΔG(T)/RT)：任意温度 K 巨大，燃烧自发推进，无需手设 K°
                    .activationEnergy(40.0)
                    .preExponentialFactor(1e9)
                    .build();
            GRAPH.addBidirectionalEdges(ModIons.SULFUR, ModIons.O2, sulfurBurn);

            // ====== 接触法：2SO₂ + O₂ ⇌ 2SO₃（可逆工业平衡） ======
            ReactionRule so2ToSo3 = new ReactionRule.Builder(
                    new ResourceLocation(Main.MODID, "so2_to_so3"))
                    .reactant(ModIons.SO2, 2)
                    .reactant(ModIons.O2, 1)
                    .product(ModIons.SO3, 2)
                    .deltaHComputed()   // ΔH = -197.8 kJ（放热）
                    .deltaGComputed()   // ΔG = -142 kJ（自发）
                    .equilibriumConstant(10.0)     // 显式覆盖（可逆玩法）：手调中等 K 维持接触法可逆平衡
                    .equilibriumOverridden()
                    .activationEnergy(50.0)
                    .preExponentialFactor(1e8)
                    .build();
            GRAPH.addBidirectionalEdges(ModIons.SO2, ModIons.O2, so2ToSo3);

            ReactionRule so3ToSo2 = new ReactionRule.Builder(
                    new ResourceLocation(Main.MODID, "so3_to_so2"))
                    .reactant(ModIons.SO3, 2)
                    .product(ModIons.SO2, 2)
                    .product(ModIons.O2, 1)
                    .deltaHComputed()   // ΔG = +142 kJ（非自发，K_rev = 1/K_fwd）
                    .deltaGComputed()
                    .equilibriumConstant(0.1)      // 显式覆盖：与正反应 K 互为倒数
                    .equilibriumOverridden()
                    .activationEnergy(50.0)
                    .preExponentialFactor(1e8)
                    .selfLoop(true)
                    .build();
            GRAPH.addEdge(ModIons.SO3, ModIons.SO3, so3ToSo2);

            // ====== 硫酸吸收：SO₃ + H₂O → H₂SO₄ ======
            ReactionRule so3Absorb = new ReactionRule.Builder(
                    new ResourceLocation(Main.MODID, "so3_absorb"))
                    .reactant(ModIons.SO3, 1)
                    .reactant(ModIons.H2O, 1)
                    .product(ModIons.H2SO4, 1)
                    .deltaHComputed()   // ΔH = -132.5 kJ（放热）
                    .deltaGComputed()   // ΔG = -81.8 kJ（自发）
                    .activationEnergy(40.0)
                    .preExponentialFactor(1e9)
                    .build();
            GRAPH.addBidirectionalEdges(ModIons.SO3, ModIons.H2O, so3Absorb);

            // ====== 电解水：2H₂O → 2H₂ + O₂（自环，需通电） ======
            ReactionRule waterElectrolysis = new ReactionRule.Builder(
                    new ResourceLocation(Main.MODID, "water_electrolysis"))
                    .reactant(ModIons.H2O, 2)
                    .product(ModIons.H2, 2)
                    .product(ModIons.O2, 1)
                    .deltaHComputed()          // ΔH = +571.6 kJ（吸热）
                    .deltaGComputed()          // ΔG = +474.2 kJ（非自发，需通电）
                    .electricalWorkComputed()  // W = ΔG
                    .activationEnergy(50.0)
                    .preExponentialFactor(1e8)
                    .selfLoop(true)
                    .build();
            GRAPH.addEdge(ModIons.H2O, ModIons.H2O, waterElectrolysis);

            // ====== 合成盐酸：Cl₂ + H₂ → 2HCl ======
            ReactionRule hclSynthesis = new ReactionRule.Builder(
                    new ResourceLocation(Main.MODID, "hcl_synthesis"))
                    .reactant(ModIons.Cl2, 1)
                    .reactant(ModIons.H2, 1)
                    .product(ModIons.HCl, 2)
                    .deltaHComputed()   // ΔH = -184.6 kJ（放热）
                    .deltaGComputed()   // ΔG = -190.6 kJ（自发）
                    // 物理公式 K = exp(-ΔG(T)/RT)：任意温度 K 巨大，合成自发推进
                    .activationEnergy(50.0)
                    .preExponentialFactor(1e9)
                    .build();
            GRAPH.addBidirectionalEdges(ModIons.Cl2, ModIons.H2, hclSynthesis);

            // ====== 盐酸溶解：HCl(g) + H₂O → HCl(aq) ======
            // 气体盐酸通入水中形成盐酸水溶液（溶解放热）
            ReactionRule hclDissolve = new ReactionRule.Builder(
                    new ResourceLocation(Main.MODID, "hcl_dissolve"))
                    .reactant(ModIons.HCl, 1)
                    .reactant(ModIons.H2O, 1)
                    .product(ModIons.HCL_AQ, 1)
                    .deltaHComputed()   // ΔH ≈ ΔHf(HCl·aq) - ΔHf(HCl,g) ≈ -75 kJ（溶解放热）
                    .deltaGComputed()   // 自发溶解
                    .activationEnergy(5.0)
                    .preExponentialFactor(1e10)
                    .build();
            GRAPH.addBidirectionalEdges(ModIons.HCl, ModIons.H2O, hclDissolve);

            // ====== 盐酸电离：HCl(aq) → H⁺ + Cl⁻（强电解质，完全电离） ======
            // 水作为电离介质（前置条件：检测存在但不消耗）
            ReactionRule hclIonize = new ReactionRule.Builder(
                    new ResourceLocation(Main.MODID, "hcl_ionize"))
                    .reactant(ModIons.HCL_AQ, 1)
                    .product(ModIons.H_plus, 1)
                    .product(ModIons.Cl_minus, 1)
                    .precondition(ModIons.H2O, 1)
                    .deltaHComputed()   // 电离吸热（≈ -ΔHf(HCl·aq) 部分抵消）
                    .deltaGComputed()   // 强酸完全电离
                    .activationEnergy(3.0)   // 极低能垒，即电离
                    .preExponentialFactor(1e12)
                    .selfLoop(true)
                    .build();
            // 自环：分解/电离类反应，由 Tick 轮询驱动
            GRAPH.addEdge(ModIons.HCL_AQ, ModIons.HCL_AQ, hclIonize);

            // ====== 哈伯法：N₂ + 3H₂ ⇌ 2NH₃（可逆合成氨） ======
            ReactionRule haberSynthesis = new ReactionRule.Builder(
                    new ResourceLocation(Main.MODID, "haber_synthesis"))
                    .reactant(ModIons.N2, 1)
                    .reactant(ModIons.H2, 3)
                    .product(ModIons.NH3, 2)
                    .deltaHComputed()   // ΔH = -92.2 kJ（放热）
                    .deltaGComputed()   // ΔG = -32.8 kJ（自发，常温可逆）
                    .equilibriumConstant(10.0)     // 显式覆盖（可逆玩法）：手调中等 K 维持合成氨可逆平衡
                    .equilibriumOverridden()
                    .activationEnergy(110.0)   // 高Ea：室温几乎不反应，需高温（工业合成氨）
                    .preExponentialFactor(1e8)
                    .build();
            GRAPH.addBidirectionalEdges(ModIons.N2, ModIons.H2, haberSynthesis);

            ReactionRule haberDecompose = new ReactionRule.Builder(
                    new ResourceLocation(Main.MODID, "haber_decompose"))
                    .reactant(ModIons.NH3, 2)
                    .product(ModIons.N2, 1)
                    .product(ModIons.H2, 3)
                    .deltaHComputed()   // ΔG = +32.8 kJ（K_rev = 1/K_fwd）
                    .deltaGComputed()
                    .equilibriumConstant(0.1)   // 显式覆盖：与正反应 K 互为倒数
                    .equilibriumOverridden()
                    .activationEnergy(110.0)   // 与正反应一致的高Ea
                    .preExponentialFactor(1e8)
                    .selfLoop(true)
                    .build();
            GRAPH.addEdge(ModIons.NH3, ModIons.NH3, haberDecompose);

            // ====== 铜氢还原：Cu₂O + H₂ → 2Cu + H₂O（需加热，室温动力学几乎不发生） ======
            ReactionRule copperReduce = new ReactionRule.Builder(
                    new ResourceLocation(Main.MODID, "copper_reduce"))
                    .reactant(ModIons.Cu2O, 1)
                    .reactant(ModIons.H2, 1)
                    .product(ModIons.Cu, 2)
                    .product(ModIons.H2O, 1)
                    .deltaHComputed()   // ΔH = -117.2 kJ（放热）
                    .deltaGComputed()   // ΔG = -91.1 kJ（自发但动力学需加热）
                    .activationEnergy(120.0)   // 高Ea：室温速率可忽略，加热后Arrhenius加速
                    .preExponentialFactor(1e9)
                    .build();
            GRAPH.addBidirectionalEdges(ModIons.Cu2O, ModIons.H2, copperReduce);

            // ====== 钠遇水（剧烈）：2Na + 2H₂O → 2NaOH + H₂（再生 H₂，形成循环） ======
            ReactionRule sodiumWater = new ReactionRule.Builder(
                    new ResourceLocation(Main.MODID, "sodium_water"))
                    .reactant(ModIons.Na, 2)
                    .reactant(ModIons.H2O, 2)
                    .product(ModIons.NaOH, 2)
                    .product(ModIons.H2, 1)
                    .deltaHComputed()   // ΔH = -279.6 kJ（剧烈放热）
                    .deltaGComputed()   // ΔG = -285.2 kJ（极自发）
                    // 物理公式 K = exp(-ΔG(T)/RT)：任意温度 K 巨大，遇水剧烈反应
                    .activationEnergy(25.0)   // 极低能垒，几乎瞬间
                    .preExponentialFactor(1e9)
                    .build();
            GRAPH.addBidirectionalEdges(ModIons.Na, ModIons.H2O, sodiumWater);

            // ====== 石灰煅烧：CaCO₃ → CaO + CO₂↑（需高温窑） ======
            ReactionRule limeCalcination = new ReactionRule.Builder(
                    new ResourceLocation(Main.MODID, "lime_calcination"))
                    .reactant(ModIons.CaCO3, 1)
                    .product(ModIons.CaO, 1)
                    .product(ModIons.CO2, 1)
                    .deltaHComputed()   // ΔH = +179 kJ（强吸热）
                    .deltaGComputed()   // ΔG = +130 kJ（常温不自发）
                    // 物理公式：常温 K≈4e-23 冻结，1300K 时 K≈25 推进（吸热反应高温有利，物理温度依赖）
                    .activationEnergy(140.0)   // 高Ea：室温忽略，石灰窑高温煅烧
                    .preExponentialFactor(1e8)
                    .selfLoop(true)
                    .build();
            GRAPH.addEdge(ModIons.CaCO3, ModIons.CaCO3, limeCalcination);

            // ====== 石灰消化：CaO + H₂O → Ca(OH)₂（室温放热快） ======
            ReactionRule limeSlake = new ReactionRule.Builder(
                    new ResourceLocation(Main.MODID, "lime_slake"))
                    .reactant(ModIons.CaO, 1)
                    .reactant(ModIons.H2O, 1)
                    .product(ModIons.Ca_OH_2, 1)
                    .deltaHComputed()   // ΔH = -65.2 kJ（放热）
                    .deltaGComputed()   // ΔG = -57.4 kJ（自发）
                    .activationEnergy(35.0)
                    .preExponentialFactor(1e9)
                    .build();
            GRAPH.addBidirectionalEdges(ModIons.CaO, ModIons.H2O, limeSlake);

            // ====== 实验室制盐酸：NaCl + H₂SO₄ → NaHSO₄ + HCl↑（需加热） ======
            ReactionRule hclPrepare = new ReactionRule.Builder(
                    new ResourceLocation(Main.MODID, "hcl_prepare"))
                    .reactant(ModIons.NaCl, 1)
                    .reactant(ModIons.H2SO4, 1)
                    .product(ModIons.NaHSO4, 1)
                    .product(ModIons.HCl, 1)
                    .deltaHComputed()   // ΔH = +7.3 kJ（微吸热）
                    .deltaGComputed()   // ΔG = -14.0 kJ（自发但需加热启动）
                    .activationEnergy(80.0)   // 高Ea：室温忽略，需加热（约200°C）
                    .preExponentialFactor(1e9)
                    .build();
            GRAPH.addBidirectionalEdges(ModIons.NaCl, ModIons.H2SO4, hclPrepare);

            // ====== 焦炭燃烧：C + O₂ → CO₂（需点火/加热） ======
            ReactionRule carbonBurn = new ReactionRule.Builder(
                    new ResourceLocation(Main.MODID, "carbon_burn"))
                    .reactant(ModIons.C, 1)
                    .reactant(ModIons.O2, 1)
                    .product(ModIons.CO2, 1)
                    .deltaHComputed()   // ΔH = -393.5 kJ（放热）
                    .deltaGComputed()   // ΔG = -394.4 kJ（自发但需点火）
                    // 物理公式 K = exp(-ΔG(T)/RT)：1300K 时 K 仍 ~1e16（修复此前高温 K 坍缩导致 C+O₂ 卡死）
                    .activationEnergy(90.0)   // 需点火加热
                    .preExponentialFactor(1e9)
                    .build();
            GRAPH.addBidirectionalEdges(ModIons.C, ModIons.O2, carbonBurn);

            // ====== 布多尔反应：C + CO₂ → 2CO（制CO，需高温，强吸热） ======
            ReactionRule boudouard = new ReactionRule.Builder(
                    new ResourceLocation(Main.MODID, "boudouard"))
                    .reactant(ModIons.C, 1)
                    .reactant(ModIons.CO2, 1)
                    .product(ModIons.CO, 2)
                    .deltaHComputed()   // ΔH = +172.5 kJ（强吸热）
                    .deltaGComputed()   // ΔG = +120 kJ（室温不自发，高温 K 增大）
                    // 物理公式：常温 K 冻结，1300K 时 K≈187 制 CO（吸热反应高温有利，物理温度依赖）
                    .activationEnergy(140.0)   // 高Ea，需高温
                    .preExponentialFactor(1e8)
                    .build();
            GRAPH.addBidirectionalEdges(ModIons.C, ModIons.CO2, boudouard);

            // ====== 高炉炼铁：Fe₂O₃ + 3CO → 2Fe + 3CO₂（需高温） ======
            ReactionRule ironBlast = new ReactionRule.Builder(
                    new ResourceLocation(Main.MODID, "iron_blast"))
                    .reactant(ModIons.Fe2O3, 1)
                    .reactant(ModIons.CO, 3)
                    .product(ModIons.Fe, 2)
                    .product(ModIons.CO2, 3)
                    .deltaHComputed()   // ΔH = -24.8 kJ
                    .deltaGComputed()   // ΔG = -29.4 kJ（自发但动力学需高温）
                    .activationEnergy(120.0)   // 高Ea，高炉温度
                    .preExponentialFactor(1e8)
                    .build();
            GRAPH.addBidirectionalEdges(ModIons.Fe2O3, ModIons.CO, ironBlast);

            Main.LOGGER.info("[Chemistry] Reaction graph ready: {} nodes, {} edges",
                    GRAPH.getAllNodes().size(), GRAPH.getAllEdges().size());
            // 逐边信息仅用于调试（默认不输出）
            for (ReactionEdge edge : GRAPH.getAllEdges()) {
                Main.LOGGER.debug("[Chemistry]   {} → {} (rule: {})",
                        edge.getSource().getId().getPath(),
                        edge.getTarget().getId().getPath(),
                        edge.getRule().getId().getPath());
            }
        }

        /**
         * 获取反应图（供其他类使用）
         */
        public static ReactionGraph getGraph() {
            return GRAPH;
        }
    }
}
