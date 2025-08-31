package com.pha.trainees.client;

import com.pha.trainees.Main;
import com.pha.trainees.registry.ModMenuTypes;
import com.pha.trainees.screen.PurificationStationScreen;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = Main.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientSetup {

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        // 注册GUI屏幕
        event.enqueueWork(() -> {
            MenuScreens.register(ModMenuTypes.PURIFICATION_STATION_MENU.get(), PurificationStationScreen::new);
        });
    }
}
