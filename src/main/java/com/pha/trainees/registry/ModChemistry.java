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
                                .sound(SoundType.METAL)
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

        //鸡碱锭
        public static final RegistryObject<Item> CHE_JIOH = ITEMS.register("che_jioh",
                () -> new ChemistryItem.JiOH(new Item.Properties()
                ));
        //鸡碱粒
        public static final RegistryObject<Item> CHE_JIOH_NUGGET = ITEMS.register("che_jioh_nugget",
                () -> new ChemistryItem.JiOH(new Item.Properties()
                ));
        //黑化氢
        public static final RegistryObject<Item> CHE_HBP = ITEMS.register("che_hbp",
                () -> new ChemistryItem.HBp(new Item.Properties()
                ));
        //次黑酸
        public static final RegistryObject<Item> CHE_HBPO = ITEMS.register("che_hbpo",
                () -> new ChemistryItem.HBpO(new Item.Properties()
                ));
        //反相素
        public static final RegistryObject<Item> CHE_JIBP = ITEMS.register("che_jibp",
                () -> new ChemistryItem.JiBp(new Item.Properties()
                        .rarity(Rarity.UNCOMMON)
                ));
        //氧化鸡
        public static final RegistryObject<Item> CHE_JI2O = ITEMS.register("che_ji2o",
                () -> new ChemistryItem.Ji2O(new Item.Properties()
                ));
        //氧化鸡粒
        public static final RegistryObject<Item> CHE_JI2O_NUGGET = ITEMS.register("che_ji2o_nugget",
                () -> new ChemistryItem.Ji2O(new Item.Properties()
                ));
    }

}
