package com.pha.trainees.event;

import com.pha.trainees.Main;
import com.pha.trainees.client.CalledSwordRenderer;
import com.pha.trainees.client.KunTraineesRenderer;
import com.pha.trainees.registry.ModEntities;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Main.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientModEvents {
    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.CALLED_SWORD.get(),
                CalledSwordRenderer::new);
        event.registerEntityRenderer(ModEntities.KUN_TRAINEES.get(),
                KunTraineesRenderer::new);
        //event.registerEntityRenderer(ModEntities.BASKETBALL.get(),                );
    }
}
