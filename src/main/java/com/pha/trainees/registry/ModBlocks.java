package com.pha.trainees.registry;

import com.pha.trainees.Main;
import com.pha.trainees.block.BasketballAntiBlock;
import com.pha.trainees.block.PurificationStationBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;



public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, Main.MODID);

    //顺序：建筑方块 -> 染色方块 -> 自然方块 -> 功能方块 -> 红石方块

    public static final RegistryObject<Block> TWO_HALF_INGOT_BLOCK = BLOCKS.register("two_half_ingot_block", () -> new Block(
            BlockBehaviour.Properties.of()
                    .strength(2f,6f)
                    .sound(SoundType.METAL)
                    .requiresCorrectToolForDrops()
    ));

    public static final RegistryObject<Block> FEATHER_BLOCK = BLOCKS.register("feather_block", () -> new Block(
            BlockBehaviour.Properties.of()
                    .strength(0.05f,0f)
                    .sound(SoundType.WOOL)
                    .instabreak()
                    .noCollission()
    ));

    public static final RegistryObject<Block> POLYESTER_ORE = BLOCKS.register("polyester_ore", () -> new Block(
            BlockBehaviour.Properties.of()
                    .strength(2f,2f)
                    .sound(SoundType.STONE)
                    .requiresCorrectToolForDrops()
    ));

    public static final RegistryObject<Block> DEEPSLATE_POLYESTER_ORE = BLOCKS.register("deepslate_polyester_ore",() ->
            new Block(BlockBehaviour.Properties.copy(Blocks.DEEPSLATE_IRON_ORE))
    );

    public static final RegistryObject<Block> myblock = BLOCKS.register("myblock", () -> new Block(
            BlockBehaviour.Properties.of()
                    .strength(15.0f,1200f)
                    .sound(SoundType.NETHERITE_BLOCK)
                    .requiresCorrectToolForDrops()
                    .lightLevel(state -> 7)
                    .emissiveRendering((state,world,pos) -> true)
    ));


    //提纯台
    public static final RegistryObject<Block> PURIFICATION_STATION = BLOCKS.register(
            "purification_station",
            () -> new PurificationStationBlock(
                    BlockBehaviour.Properties.of()
                            .strength(3.0f, 6.0f)
                            .sound(SoundType.METAL)
                            .requiresCorrectToolForDrops()
            )
    );

    //反相篮球
    public static final RegistryObject<Block> BASKETBALL_ANTI_BLOCK = BLOCKS.register(
            "basketball_anti_block",
            () -> new BasketballAntiBlock(
                    BlockBehaviour.Properties.of()
                            .strength(0.5f,0.5f) // 较软的方块
                            .sound(SoundType.WOOL) // 羊毛般的声音
                            .noOcclusion() // 不阻挡视线
                            .instabreak()
            )
    );


}



