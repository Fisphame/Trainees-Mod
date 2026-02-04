package com.pha.trainees.registry;

import com.pha.trainees.Main;
import com.pha.trainees.block.*;
import com.pha.trainees.block.entity.KunAltarBlockEntity;
import com.pha.trainees.blockentity.PurificationStationBlockEntity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;


public class ModBlocks {

    public static class ModBlockEntities {
        public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
                DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, Main.MODID);

        public static final RegistryObject<BlockEntityType<KunAltarBlockEntity>> KUN_ALTAR_ENTITY =
                BLOCK_ENTITIES.register("kun_altar_block",
                        () -> BlockEntityType.Builder.of(
                                KunAltarBlockEntity::new,
                                ModBlocks.KUN_ALTAR.get()
                        ).build(null));

        public static final RegistryObject<BlockEntityType<PurificationStationBlockEntity>> PURIFICATION_STATION =
                BLOCK_ENTITIES.register("purification_station",
                        () -> BlockEntityType.Builder.of(
                                PurificationStationBlockEntity::new,
                                ModBlocks.PURIFICATION_STATION.get()
                        ).build(null)
                );
    }

    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, Main.MODID);

    //顺序：建筑方块 -> 染色方块 -> 自然方块 -> 功能方块 -> 红石方块

    public static final RegistryObject<Block> TWO_HALF_INGOT_BLOCK = BLOCKS.register("two_half_ingot_block",
            () -> new TwoHalfIngotCourseBlock.TwoHalfIngotBlock(
            BlockBehaviour.Properties.of()
                    .strength(2f,6f)
                    .sound(SoundType.METAL)
                    .requiresCorrectToolForDrops()
    ));

    public static final RegistryObject<Block> WAXED_TWO_HALF_INGOT_BLOCK = BLOCKS.register("waxed_two_half_ingot_block",
            () -> new TwoHalfIngotCourseBlock.WaxedTwoHalfIngotBlock(
                    BlockBehaviour.Properties.of()
                            .strength(2f,6f)
                            .sound(SoundType.METAL)
                            .requiresCorrectToolForDrops()
            ));
// Pullium Inversine   Pullium inverside
    // 金矽块   /ɔːrɪvərsaɪt/
    public static final RegistryObject<Block> AURIVERSITE_BLOCK = BLOCKS.register("auriversite_block",
            () -> new AuriversiteBlock(
               BlockBehaviour.Properties.of()
                       .mapColor(MapColor.METAL)
                       .strength(3f, 8f)
                       .sound(SoundType.METAL)
                       .requiresCorrectToolForDrops()
            ));

    public static final RegistryObject<Block> FEATHER_BLOCK = BLOCKS.register("feather_block",
            () -> new Block(
            BlockBehaviour.Properties.of()
                    .strength(0.05f,0f)
                    .sound(SoundType.WOOL)
                    .instabreak()
                    .noCollission()
    ));

    // 鈅
    public static final RegistryObject<Block> SELENAURITE_ORE = BLOCKS.register("selenaurite_ore",
            () -> new Block(
            BlockBehaviour.Properties.of()
                    .strength(3f,3f)
                    .sound(SoundType.STONE)
                    .requiresCorrectToolForDrops()
            )
    );
    public static final RegistryObject<Block> DEEPSLATE_SELENAURITE_ORE = BLOCKS.register("deepslate_selenaurite_ore",
            () -> new Block(
                BlockBehaviour.Properties.of()
                        .strength(4.5f, 3f)
                        .sound(SoundType.DEEPSLATE)
                        .requiresCorrectToolForDrops()
            )
    );
    public static final RegistryObject<Block> SELENAURITE_BLOCK = BLOCKS.register("selenaurite_block",
            () -> new Block(
                    BlockBehaviour.Properties.of()
                            .strength(5f,6f)
                            .sound(SoundType.METAL)
                            .requiresCorrectToolForDrops()
            )
    );
    // Nyctium  Ny
    // 钅辐
    public static final RegistryObject<Block> NYCTIUM_ORE = BLOCKS.register("nyctium_ore",
            () -> new Block(
                    BlockBehaviour.Properties.of()
                            .strength(3f,3f)
                            .sound(SoundType.STONE)
                            .requiresCorrectToolForDrops()
            )
    );
    public static final RegistryObject<Block> DEEPSLATE_NYCTIUM_ORE = BLOCKS.register("deepslate_nyctium_ore",
            () -> new Block(
                    BlockBehaviour.Properties.of()
                            .strength(4.5f, 3f)
                            .sound(SoundType.DEEPSLATE)
                            .requiresCorrectToolForDrops()
            )
    );
    public static final RegistryObject<Block> NYCTIUM_BLOCK = BLOCKS.register("nyctium_block",
            () -> new Block(
                    BlockBehaviour.Properties.of()
                            .strength(5f,6f)
                            .sound(SoundType.METAL)
                            .requiresCorrectToolForDrops()
            )
    );
    // Terapium   Te
    // 钅寺
    public static final RegistryObject<Block> TERAPIUM_ORE = BLOCKS.register("terapium_ore",
            () -> new Block(
                    BlockBehaviour.Properties.of()
                            .strength(3f,3f)
                            .sound(SoundType.STONE)
                            .requiresCorrectToolForDrops()
            )
    );
    public static final RegistryObject<Block> DEEPSLATE_TERAPIUM_ORE = BLOCKS.register("deepslate_terapium_ore",
            () -> new Block(
                    BlockBehaviour.Properties.of()
                            .strength(4.5f, 3f)
                            .sound(SoundType.DEEPSLATE)
                            .requiresCorrectToolForDrops()
            )
    );
    public static final RegistryObject<Block> TERAPIUM_BLOCK = BLOCKS.register("terapium_block",
            () -> new Block(
                    BlockBehaviour.Properties.of()
                            .strength(5f,6f)
                            .sound(SoundType.METAL)
                            .requiresCorrectToolForDrops()
            )
    );
    // Banalium  Bn
    // 钅朋
    public static final RegistryObject<Block> BANALIUM_ORE = BLOCKS.register("banalium_ore",
            () -> new Block(
                    BlockBehaviour.Properties.of()
                            .strength(3f,3f)
                            .sound(SoundType.STONE)
                            .requiresCorrectToolForDrops()
            )
    );
    public static final RegistryObject<Block> DEEPSLATE_BANALIUM_ORE = BLOCKS.register("deepslate_banalium_ore",
            () -> new Block(
                    BlockBehaviour.Properties.of()
                            .strength(4.5f, 3f)
                            .sound(SoundType.DEEPSLATE)
                            .requiresCorrectToolForDrops()
            )
    );
    public static final RegistryObject<Block> BANALIUM_BLOCK = BLOCKS.register("banalium_block",
            () -> new Block(
                    BlockBehaviour.Properties.of()
                            .strength(5f,6f)
                            .sound(SoundType.METAL)
                            .requiresCorrectToolForDrops()
            )
    );
    // Nivtium  Nt

    // 钅宁
    public static final RegistryObject<Block> NIVTIUM_ORE = BLOCKS.register("nivtium_ore",
            () -> new Block(
                    BlockBehaviour.Properties.of()
                            .strength(3f,3f)
                            .sound(SoundType.STONE)
                            .requiresCorrectToolForDrops()
            )
    );
    public static final RegistryObject<Block> DEEPSLATE_NIVTIUM_ORE = BLOCKS.register("deepslate_nivtium_ore",
            () -> new Block(
                    BlockBehaviour.Properties.of()
                            .strength(4.5f, 3f)
                            .sound(SoundType.DEEPSLATE)
                            .requiresCorrectToolForDrops()
            )
    );
    public static final RegistryObject<Block> NIVTIUM_BLOCK = BLOCKS.register("nivtium_block",
            () -> new Block(
                    BlockBehaviour.Properties.of()
                            .strength(5f,6f)
                            .sound(SoundType.METAL)
                            .requiresCorrectToolForDrops()
            )
    );
    // 钅克
    // Crucium  Ck
    public static final RegistryObject<Block> CRUCIUM_ORE = BLOCKS.register("crucium_ore",
            () -> new Block(
                    BlockBehaviour.Properties.of()
                            .strength(3f,3f)
                            .sound(SoundType.STONE)
                            .requiresCorrectToolForDrops()
            )
    );
    public static final RegistryObject<Block> DEEPSLATE_CRUCIUM_ORE = BLOCKS.register("deepslate_crucium_ore",
            () -> new Block(
                    BlockBehaviour.Properties.of()
                            .strength(4.5f, 3f)
                            .sound(SoundType.DEEPSLATE)
                            .requiresCorrectToolForDrops()
            )
    );
    public static final RegistryObject<Block> CRUCIUM_BLOCK = BLOCKS.register("crucium_block",
            () -> new Block(
                    BlockBehaviour.Properties.of()
                            .strength(5f,6f)
                            .sound(SoundType.METAL)
                            .requiresCorrectToolForDrops()
            )
    );
    // 钅丝
    // Sertium  St
    public static final RegistryObject<Block> SERTIUM_ORE = BLOCKS.register("sertium_ore",
            () -> new Block(
                    BlockBehaviour.Properties.of()
                            .strength(3f,3f)
                            .sound(SoundType.STONE)
                            .requiresCorrectToolForDrops()
            )
    );
    public static final RegistryObject<Block> DEEPSLATE_SERTIUM_ORE = BLOCKS.register("deepslate_sertium_ore",
            () -> new Block(
                    BlockBehaviour.Properties.of()
                            .strength(4.5f, 3f)
                            .sound(SoundType.DEEPSLATE)
                            .requiresCorrectToolForDrops()
            )
    );
    public static final RegistryObject<Block> SERTIUM_BLOCK = BLOCKS.register("sertium_block",
            () -> new Block(
                    BlockBehaviour.Properties.of()
                            .strength(5f,6f)
                            .sound(SoundType.METAL)
                            .requiresCorrectToolForDrops()
            )
    );
    // Placium  Pl
    // 钅比
    public static final RegistryObject<Block> PLACIUM_ORE = BLOCKS.register("placium_ore",
            () -> new Block(
                    BlockBehaviour.Properties.of()
                            .strength(3f,3f)
                            .sound(SoundType.STONE)
                            .requiresCorrectToolForDrops()
            )
    );
    public static final RegistryObject<Block> DEEPSLATE_PLACIUM_ORE = BLOCKS.register("deepslate_placium_ore",
            () -> new Block(
                    BlockBehaviour.Properties.of()
                            .strength(4.5f, 3f)
                            .sound(SoundType.DEEPSLATE)
                            .requiresCorrectToolForDrops()
            )
    );
    public static final RegistryObject<Block> PLACIUM_BLOCK = BLOCKS.register("placium_block",
            () -> new Block(
                    BlockBehaviour.Properties.of()
                            .strength(5f,6f)
                            .sound(SoundType.METAL)
                            .requiresCorrectToolForDrops()
            )
    );




    public static final RegistryObject<Block> MYBLOCK = BLOCKS.register("myblock",
            () -> new Block(
            BlockBehaviour.Properties.of()
                    .strength(15.0f,1200f)
                    .sound(SoundType.NETHERITE_BLOCK)
                    .requiresCorrectToolForDrops()
                    .lightLevel(state -> 7)
                    .emissiveRendering((state,world,pos) -> true)
    ));



    //提纯台
    public static final RegistryObject<Block> PURIFICATION_STATION = BLOCKS.register("purification_station",
            () -> new PurificationStationBlock(
                    BlockBehaviour.Properties.of()
                            .strength(3.0f, 6.0f)
                            .sound(SoundType.METAL)
                            .requiresCorrectToolForDrops()
            )
    );

    //反相篮球
    public static final RegistryObject<Block> BASKETBALL_ANTI_BLOCK = BLOCKS.register("basketball_anti_block",
            () -> new BasketballAntiBlock(
                    BlockBehaviour.Properties.of()
                            .strength(0.01f,3f)
                            .sound(SoundType.WOOL)
            )
    );

    //高爆炸抗性的反相篮球
    public static final RegistryObject<Block> BASKETBALL_ANTI_BLOCK_RGT = BLOCKS.register("basketball_anti_block_rgt",
            () -> new BasketballAntiBlock(
                    BlockBehaviour.Properties.of()
                            .strength(0.5f,1200f)
                            .sound(SoundType.WOOL)
            )
    );

    public static final RegistryObject<Block> KUN_ALTAR = BLOCKS.register("kun_altar",
            () -> new KunAltarBlock(
                    BlockBehaviour.Properties.of()
                            .strength(2.5f, 2.5f)
                            .sound(SoundType.STONE)
                            .mapColor(MapColor.STONE)
                            .requiresCorrectToolForDrops()
            )
    );

    public static final RegistryObject<Block> ALTAR_CORE_BLOCK = BLOCKS.register("altar_core_block",
            () -> new AltarCoreBlock(
                    BlockBehaviour.Properties.of()
                            .strength(7.5f, 15f)
                            .sound(SoundType.NETHER_BRICKS)
                            .mapColor(MapColor.STONE)
                            .requiresCorrectToolForDrops()
            )
    );

    public static final RegistryObject<Block> REACTING_FURNACE = BLOCKS .register("reacting_furnace",
            () -> new ReactingFurnaceBlock(
                    BlockBehaviour.Properties.of()
                            .strength(1.0f, 2.0f)
                            .sound(SoundType.STONE)
                            .requiresCorrectToolForDrops()
            ));





}






