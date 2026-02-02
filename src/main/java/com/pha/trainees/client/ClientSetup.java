package com.pha.trainees.client;

import com.pha.trainees.Main;
import com.pha.trainees.multiblock.TrainerAltarPattern;
import com.pha.trainees.registry.ModBlocks;
import com.pha.trainees.registry.ModMenuTypes;
import com.pha.trainees.screen.PurificationStationScreen;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = Main.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientSetup {

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        // 注册GUI屏幕和流体渲染
        event.enqueueWork(() -> {
            MenuScreens.register(ModMenuTypes.PURIFICATION_STATION_MENU.get(), PurificationStationScreen::new);

//            // 设置流体渲染为半透明
//            ItemBlockRenderTypes.setRenderLayer(ModFluid.SOURCE_CHE_HBP.get(), RenderType.translucent());
//            ItemBlockRenderTypes.setRenderLayer(ModFluid.FLOWING_CHE_HBP.get(), RenderType.translucent());
//            ItemBlockRenderTypes.setRenderLayer(ModBlocks.CHE_HBP_BLOCK.get(), RenderType.translucent());
        });

        event.enqueueWork(() -> {
            BlockEntityRenderers.register(ModBlocks.ModBlockEntities.KUN_ALTAR_ENTITY.get(),
                    Renderer.KunAltarBlockEntityRenderer::new);
        });

        event.enqueueWork(() -> {
            Main.LOGGER.info("Initializing multiblock structure system...");

            // 注册 Trainer Altar 结构
            TrainerAltarPattern.register();

            // 可以在这里注册其他结构
            // OtherStructurePattern.register();

            Main.LOGGER.info("Multiblock structure system initialized");
        });

    }
}