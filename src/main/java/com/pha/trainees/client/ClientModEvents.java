package com.pha.trainees.client;

import com.pha.trainees.Main;
import com.pha.trainees.registry.ModEntities;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Main.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientModEvents {
    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.CALLED_SWORD.get(), Renderers.CalledSwordRenderer::new);
        event.registerEntityRenderer(ModEntities.KUN_TRAINEES.get(), Renderers.KunTraineesRenderer::new);
        event.registerEntityRenderer(ModEntities.KUN_ANTI.get(), Renderers.KunAntiRenderer::new);
    }

//    @SubscribeEvent
//    public static void onKeyRegister(RegisterKeyMappingsEvent event) {
//        event.register(KeyBindings.LEFT_SHIFT_KEY);
//    }
}