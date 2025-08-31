package com.pha.trainees;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.pha.trainees.entity.KunAntiEntity;
import com.pha.trainees.entity.KunTraineesEntity;
import com.pha.trainees.event.*;
import com.pha.trainees.registry.*;
//import com.pha.trainees.structure.Chicken_2;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.slf4j.Logger;


// The value here should match an entry in the META-INF/mods.toml file
@Mod(Main.MODID)
public class Main {
    // Define mod id in a common place for everything to reference
    public static final String MODID = "trainees";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, MODID);
    private static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

//    // 在类内部添加
//    public static final DeferredRegister<StructureType<?>> STRUCTURES = DeferredRegister.create(Registries.STRUCTURE_TYPE, MODID);
//
//    public static final RegistryObject<StructureType<Chicken_2>> CHICKEN_2 =
//            STRUCTURES.register("chicken_2", () -> explicitStructureTypeTyping(Chicken_2.CODEC));

    // 辅助方法
    private static <S extends Structure> StructureType<S> explicitStructureTypeTyping(Codec<S> codec) {
        return () -> codec;
    }


    public Main() {
        var bus = FMLJavaModLoadingContext.get().getModEventBus();
        var ebus = MinecraftForge.EVENT_BUS;
        BLOCKS.register(bus);
        ITEMS.register(bus);
        CREATIVE_MODE_TABS.register(bus);
        ENTITIES.register(bus);


        ModBlocks.BLOCKS.register(bus);
        ModSounds.SOUNDS.register(bus);
        ModEnchantments.ENCHANTMENTS.register(bus);
        ModItems.ITEMS.register(bus);
        ModEntities.ENTITIES.register(bus);
        Something.SomethingBlocks.BLOCKS.register(bus);
        Something.SomethingItems.ITEMS.register(bus);
        Something.PrankItems.ITEMS.register(bus);
        Something.Paintings.PAINTING_VARIANTS.register(bus);
        Something.OttoMother.ITEMS.register(bus);
        ModCreativeModeTabs.CREATIVE_MODE_TABS.register(bus);
        ModBlockEntities.BLOCK_ENTITIES.register(bus);
        ModMenuTypes.MENUS.register(bus);
        ModRecipeTypes.RECIPE_SERIALIZERS.register(bus);
        ModRecipeTypes.RECIPE_TYPES.register(bus);
//        STRUCTURES.register(bus);


        ebus.register(AbilityHandler.class);
        ebus.register(FoodHandler.class);
//        ebus.register(PickupHandler.class);
        ebus.register(RealPickaxeEvents.class);
        ebus.register(SweepHandler.class);
//        ebus.register(ThrowHandler.class);

    }


    public class ClientModEvents {
        @SubscribeEvent
        public static void registerModels(ModelEvent.RegisterAdditional event) {
            Something.PrankItems.registerModels(event);
        }
    }

    @Mod.EventBusSubscriber(modid = Main.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public class CommonModEvents {
        @SubscribeEvent
        public static void registerAttributes(EntityAttributeCreationEvent event) {
            // 确保注册你的实体属性
            event.put(ModEntities.KUN_TRAINEES.get(), KunTraineesEntity.createAttributes().build());
            event.put(ModEntities.KUN_ANTI.get(), KunAntiEntity.createAttributes().build());
        }
    }
}