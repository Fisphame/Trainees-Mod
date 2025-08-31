package com.pha.trainees.registry;

import com.pha.trainees.Main;
import com.pha.trainees.blockentity.PurificationStationBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, Main.MODID);

    public static final RegistryObject<BlockEntityType<PurificationStationBlockEntity>> PURIFICATION_STATION =
            BLOCK_ENTITIES.register("purification_station",
                    () -> BlockEntityType.Builder.of(
                            PurificationStationBlockEntity::new,
                            ModBlocks.PURIFICATION_STATION.get()
                    ).build(null)
            );
}