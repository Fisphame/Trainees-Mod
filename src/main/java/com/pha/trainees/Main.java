package com.pha.trainees;

import com.mojang.logging.LogUtils;
import com.pha.trainees.entity.KunAntiEntity;
import com.pha.trainees.entity.KunTraineesEntity;
import com.pha.trainees.event.*;
import com.pha.trainees.registry.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import org.slf4j.Logger;

@Mod(Main.MODID)
public class Main {

    public static final String MODID = "trainees";
    public static final Logger LOGGER = LogUtils.getLogger();

    //实际上应该是387420489F，电脑算力还是有限的（，但其实也受加载距离影响。
    public static final float MATH99 = 38742F;

    public Main() {
        var bus = FMLJavaModLoadingContext.get().getModEventBus();
        var ebus = MinecraftForge.EVENT_BUS;

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


        ebus.register(AbilityHandler.class);
        ebus.register(FoodHandler.class);
        ebus.register(RealPickaxeEvents.class);
        ebus.register(SweepHandler.class);


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