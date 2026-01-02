package com.pha.trainees;

import com.mojang.logging.LogUtils;
import com.pha.trainees.event.*;
import com.pha.trainees.registry.*;
import com.pha.trainees.thetwice.Object;
import com.pha.trainees.way.chemistry.ChemicalReaction;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(Main.MODID)
public class Main {

    public static final String MODID = "trainees";
    public static final Logger LOGGER = LogUtils.getLogger();



    public Main() {
        var bus = FMLJavaModLoadingContext.get().getModEventBus();
        IEventBus ebus = MinecraftForge.EVENT_BUS;

        Object.ITEMS.register(bus);


        ModBlocks.BLOCKS.register(bus);
        ModChemistry.ModChemistryBlocks.BLOCKS.register(bus);
        ModSounds.SOUNDS.register(bus);
        ModEnchantments.ENCHANTMENTS.register(bus);
        ModItems.ITEMS.register(bus);
        ModChemistry.ModChemistryBlockItems.ITEMS.register(bus);
        ModChemistry.ModChemistryItems.ITEMS.register(bus);
        ModEntities.ENTITIES.register(bus);
        Something.SomethingBlocks.BLOCKS.register(bus);
        Something.SomethingItems.ITEMS.register(bus);
        Something.PrankItems.ITEMS.register(bus);
        Something.Paintings.PAINTING_VARIANTS.register(bus);
        Something.OttoMother.ITEMS.register(bus);
        HiddenItem.BLOCKS.register(bus);
        HiddenItem.ITEMS.register(bus);
        ModCreativeModeTabs.CREATIVE_MODE_TABS.register(bus);
        ModBlockEntities.BLOCK_ENTITIES.register(bus);
        ModMenuTypes.MENUS.register(bus);
        ModRecipeTypes.RECIPE_SERIALIZERS.register(bus);
        ModRecipeTypes.RECIPE_TYPES.register(bus);
        ModFluid.FLUID_TYPES.register(bus);
        ModFluid.FLUIDS.register(bus);
        ModCommand.register();
//        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, TraineesConfig.SPEC);


        bus.register(new RegisterAttributes());


        ebus.register(AbilityHandler.class);
        ebus.register(FoodHandler.class);
        ebus.register(MiningSoundEvents.class);
        ebus.register(SweepHandler.class);
//        ebus.register(RegisterModels.class);
//        ebus.register(RegisterAttributes.class);

        // 注册配置屏幕
//        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onClientSetup);

//        setupMixinCompatibility();

    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        // 延迟执行，确保注册表已就绪
        event.enqueueWork(ChemicalReaction::registerAllReactions);
    }

//    private void setupMixinCompatibility() {
//        // 检查是否有冲突的 Mixin
//        try {
//            // 确保使用正确的映射表
//            System.setProperty("mixin.env.compatLevel", "JAVA_17");
//            System.setProperty("mixin.checks", "true");
//        } catch (Exception e) {
//            LOGGER.warn("Mixin compatibility setup failed, but continuing...");
//        }
//    }

//    private void onClientSetup(final FMLClientSetupEvent event) {
//        // 注册配置屏幕工厂
//        ModLoadingContext.get().registerExtensionPoint(
//                ConfigScreenHandler.ConfigScreenFactory.class,
//                () -> new ConfigScreenHandler.ConfigScreenFactory(
//                        (minecraft, screen) -> new TraineesConfigScreen(screen)
//                )
//        );
//    }
}