package com.pha.trainees.registry;

import com.pha.trainees.Main;
import com.pha.trainees.block.CheJibpBlock;
import com.pha.trainees.block.ChemistryBlock;
import com.pha.trainees.item.ChemistryItem;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static net.minecraft.world.item.Items.BUCKET;
import static net.minecraft.world.item.Items.registerItem;

public class ModChemistry {
    public static class ModChemistryBlocks {

        public static final DeferredRegister<Block> BLOCKS =
                DeferredRegister.create(ForgeRegistries.BLOCKS, Main.MODID);

        //氢氧化鸡方块
        public static final RegistryObject<Block> CHE_JIOH_BLOCK = BLOCKS.register("che_jioh_block",
                () -> new ChemistryBlock.JiOHBlock(
                        BlockBehaviour.Properties.of()
                                .strength(2f,6f)
                                .sound(SoundType.BONE_BLOCK)
                                .requiresCorrectToolForDrops()
                ));

        // 氧化鸡方块
        public static final RegistryObject<Block> CHE_JI2O_BLOCK = BLOCKS.register("che_ji2o_block",
                () -> new ChemistryBlock.Ji2OBlock(
                        BlockBehaviour.Properties.of()
                                .strength(2f, 6f)
                                .sound(SoundType.BONE_BLOCK)
                                .requiresCorrectToolForDrops()
                ));

        // 涂蜡的氧化鸡方块
        public static final RegistryObject<Block> CHE_WAXED_JI2O_BLOCK = BLOCKS.register("che_waxed_ji2o_block",
                () -> new ChemistryBlock.WaxedJi2OBlock(
                        BlockBehaviour.Properties.of()
                                .strength(2f, 6f)
                                .sound(SoundType.BONE_BLOCK)
                                .requiresCorrectToolForDrops()
                ));

        // 欲焰鸡方块
        public static final RegistryObject<Block> CHE_JI2O2_BLOCK = BLOCKS.register("che_ji2o2_block",
                () -> new ChemistryBlock.Ji2O2Block(
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
                () -> new LiquidBlock(ModFluid.SOURCE_CHE_HBP,
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
    }

    public static class ModChemistryBlockItems {
        public static final DeferredRegister<Item> ITEMS =
                DeferredRegister.create(ForgeRegistries.ITEMS, Main.MODID);

        //氢氧化鸡方块
        public static final RegistryObject<Item> CHE_JIOH_BLOCK_ITEM = ITEMS.register("che_jioh_block",
                () -> new BlockItem(ModChemistryBlocks.CHE_JIOH_BLOCK.get(),
                        new Item.Properties()
                ));
        // 氧化鸡方块
        public static final RegistryObject<Item> CHE_JI2O_BLOCK_ITEM = ITEMS.register("che_ji2o_block",
                () -> new BlockItem(ModChemistryBlocks.CHE_JI2O_BLOCK.get(),
                        new Item.Properties()
                ));
        // 涂蜡的氧化鸡方块
        public static final RegistryObject<Item> CHE_WAXED_JI2O_BLOCK_ITEM = ITEMS.register("che_waxed_ji2o_block",
                () -> new BlockItem(ModChemistryBlocks.CHE_WAXED_JI2O_BLOCK.get(),
                        new Item.Properties()
                ));
        // 欲焰鸡方块
        public static final RegistryObject<Item> CHE_JI2O2_BLOCK_ITEM = ITEMS.register("che_ji2o2_block",
                () -> new BlockItem(ModChemistryBlocks.CHE_JI2O2_BLOCK.get(),
                        new Item.Properties()
                ));
        //反相素方块
        public static final RegistryObject<Item> CHE_JIBP_BLOCK_ITEM = ITEMS.register("che_jibp_block",
                () -> new BlockItem(ModChemistryBlocks.CHE_JIBP_BLOCK.get(),
                        new Item.Properties()
                                .rarity(Rarity.UNCOMMON)
                ));

    }


    public static class ModChemistryItems {
        public static final DeferredRegister<Item> ITEMS =
                DeferredRegister.create(ForgeRegistries.ITEMS, Main.MODID);


        //化学书
        public static final RegistryObject<Item> CHEMISTRY_BOOK = ITEMS.register("chemistry_book",
                () -> new ChemistryItem.ChemistryBookItem(
                        new Item.Properties()
                                .stacksTo(1)
                                .rarity(Rarity.UNCOMMON)
                )
        );

        //相酸桶
        public static final RegistryObject<Item> CHE_HBP_BUCKET = ITEMS.register("che_hbp_bucket",
                () -> new BucketItem(
                        ModFluid.SOURCE_CHE_HBP,
                        new Item.Properties()
                                .craftRemainder(BUCKET)
                                .stacksTo(1)
                ));
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

        // 氧化鸡锭
        public static final RegistryObject<Item> CHE_JI2O = ITEMS.register("che_ji2o_ingot",
                () -> new ChemistryItem.Ji2O(new Item.Properties()
                ));
        // 氧化鸡粒
        public static final RegistryObject<Item> CHE_JI2O_NUGGET = ITEMS.register("che_ji2o_nugget",
                () -> new ChemistryItem.Ji2O(new Item.Properties()
                ));
        // 过氧化鸡锭
        public static final RegistryObject<Item> CHE_JI2O2_INGOT = ITEMS.register("che_ji2o2_ingot",
                () -> new ChemistryItem.Ji2O2(new Item.Properties()
                ));
        // 过氧化鸡粒
        public static final RegistryObject<Item> CHE_JI2O2_NUGGET = ITEMS.register("che_ji2o2_nugget",
                () -> new ChemistryItem.Ji2O2(new Item.Properties()
                ));
        // 二氧化黑固体
        public static final RegistryObject<Item> CHE_BPO2_SOLID = ITEMS.register("che_bpo2_solid",
                () -> new ChemistryItem.BpO2(new Item.Properties()
                ));
        // 三氧化黑固体
        public static final RegistryObject<Item> CHE_BPO3_SOLID = ITEMS.register("che_bpo3_solid",
                () -> new ChemistryItem.BpO3(new Item.Properties()
                ));
        // 黑化氢粉末
        public static final RegistryObject<Item> CHE_HBP = ITEMS.register("che_hbp_powder",
                () -> new ChemistryItem.HBp(new Item.Properties()
                ));
        // 次黑酸粉末
        public static final RegistryObject<Item> CHE_HBPO = ITEMS.register("che_hbpo_powder",
                () -> new ChemistryItem.HBpO(new Item.Properties()
                ));
        // 黑酸粉末
        public static final RegistryObject<Item> CHE_HBPO3 = ITEMS.register("che_hbpo3_powder",
                () -> new ChemistryItem.HBpO3(new Item.Properties()
                ));
        // 高黑酸粉末
        public static final RegistryObject<Item> CHE_HBPO4 = ITEMS.register("che_hbpo4_powder",
                () -> new ChemistryItem.HBpO4(new Item.Properties()
                ));
        // 氢氧化鸡锭
        public static final RegistryObject<Item> CHE_JIOH = ITEMS.register("che_jioh_ingot",
                () -> new ChemistryItem.JiOH(new Item.Properties()
                ));
        // 氢氧化鸡粒
        public static final RegistryObject<Item> CHE_JIOH_NUGGET = ITEMS.register("che_jioh_nugget",
                () -> new ChemistryItem.JiOH(new Item.Properties()
                ));
        // 反相素
        public static final RegistryObject<Item> CHE_JIBP = ITEMS.register("che_jibp_piece",
                () -> new ChemistryItem.JiBp(new Item.Properties()
                        .rarity(Rarity.UNCOMMON)
                ));
        // 次黑酸鸡
        public static final RegistryObject<Item> CHE_JIBPO = ITEMS.register("che_jibpo_crystallization",
                () -> new ChemistryItem.JiBpO(new Item.Properties()
                ));
        // 黑酸鸡
        public static final RegistryObject<Item> CHE_JIBPO3 = ITEMS.register("che_jibpo3_crystallization",
                () -> new ChemistryItem.JiBpO3(new Item.Properties()
                ));
        // 黑酸鸡
        public static final RegistryObject<Item> CHE_JIBPO4 = ITEMS.register("che_jibpo4_crystallization",
                () -> new ChemistryItem.JiBpO4(new Item.Properties()
                ));
    }

}
