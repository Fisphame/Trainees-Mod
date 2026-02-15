package com.pha.trainees;

import com.mojang.logging.LogUtils;
import com.pha.trainees.event.*;
import com.pha.trainees.registry.*;
import com.pha.trainees.util.game.chemistry.ChemicalReaction;
import com.pha.trainees.util.game.chemistry.ReactionConditions;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

@Mod(Main.MODID)
public class Main {

    public static final String MODID = "trainees";
    public static final Logger LOGGER = LogUtils.getLogger();

    public Main() {
        var bus = FMLJavaModLoadingContext.get().getModEventBus();
        IEventBus ebus = MinecraftForge.EVENT_BUS;

        ModBlocks.BLOCKS.register(bus);
        ModBlocks.ModBlockEntities.BLOCK_ENTITIES.register(bus);
        ModChemistry.ModChemistryBlocks.BLOCKS.register(bus);
        ModSounds.SOUNDS.register(bus);
        ModEnchantments.ENCHANTMENTS.register(bus);
        ModItems.ITEMS.register(bus);
        ModChemistry.ModChemistryBlockItems.ITEMS.register(bus);
        ModChemistry.ModChemistryItems.ITEMS.register(bus);
        ModEntities.ENTITIES.register(bus);
        Something.PrankBlocks.BLOCKS.register(bus);
        Something.PrankItems.ITEMS.register(bus);
        Something.Paintings.PAINTING_VARIANTS.register(bus);
        HiddenItem.BLOCKS.register(bus);
        HiddenItem.ITEMS.register(bus);
        ModCreativeModeTabs.CREATIVE_MODE_TABS.register(bus);
        ModMenuTypes.MENUS.register(bus);
        ModRecipeTypes.RECIPE_SERIALIZERS.register(bus);
        ModRecipeTypes.RECIPE_TYPES.register(bus);
        ModFluid.FLUID_TYPES.register(bus);
        ModFluid.FLUIDS.register(bus);
        ModCommand.register();

        bus.register(new Register());
        bus.addListener(this::commonSetup);
        ebus.register(AbilityHandler.class);
        ebus.register(FoodHandler.class);
    }

    private void commonSetup(final @NotNull FMLCommonSetupEvent event) {
        Main.LOGGER.info("Chemistry System: Starting commonSetup");

        event.enqueueWork(() -> {
            Main.LOGGER.info("Chemistry System: Executing enqueueWork");
            try {
                // Pre-initialize conditions
                Main.LOGGER.info("Chemistry System: Pre-initializing conditions...");
                ReactionConditions.IS_JI.get();
                ReactionConditions.hbpoDecomposeCondition.get();
                ReactionConditions.bp2AndWaterCondition.get();
                Main.LOGGER.info("Chemistry System: Condition pre-initialization complete");

                // Register reactions
                Main.LOGGER.info("Chemistry System: Calling registerAllReactions()");
                ChemicalReaction.registerAllReactions();
                Main.LOGGER.info("Chemistry System: registerAllReactions() call complete");

            } catch (Exception e) {
                Main.LOGGER.error("Chemistry System: Initialization failed", e);
            }
        });

        Main.LOGGER.info("Chemistry System: commonSetup completed");
    }
}