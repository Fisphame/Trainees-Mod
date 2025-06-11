package com.pha.trainees.registry;

import com.pha.trainees.Main;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, Main.MODID);

    public static final RegistryObject<Block> myblock = BLOCKS.register("myblock", () -> new Block(BlockBehaviour.Properties.of()
            .strength(3.0f)
            .sound(SoundType.CROP)
    ));

    public static final RegistryObject<Block> TWO_HALF_INGOT_BLOCK = BLOCKS.register("two_half_ingot_block", () -> new Block(
            BlockBehaviour.Properties.of()
                    .strength(2f,6f)
                    .sound(SoundType.METAL)
                    .requiresCorrectToolForDrops()
    ));

    public static final RegistryObject<Block> POLYESTER_BLOCK = BLOCKS.register("polyester_block", () -> new Block(
            BlockBehaviour.Properties.of()
                    .strength(2f,2f)
                    .sound(SoundType.STONE)
                    .requiresCorrectToolForDrops()
    ));

    public static final RegistryObject<Block> FEATHER_BLOCK = BLOCKS.register("feather_block", () -> new Block(
            BlockBehaviour.Properties.of()
                    .strength(0.05f,0f)
                    .sound(SoundType.WOOL)
                    .jumpFactor(1.5f)
                    .lightLevel(state -> 5)
                    .instabreak()
                    .noCollission()
    ));

}



