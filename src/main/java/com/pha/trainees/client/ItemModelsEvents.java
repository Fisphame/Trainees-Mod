package com.pha.trainees.client;

import com.pha.trainees.Main;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

// 客户端专用的事件处理器
@Mod.EventBusSubscriber(modid = Main.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ItemModelsEvents {
    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void registerModels(ModelEvent.RegisterAdditional event) {
        event.register(new ModelResourceLocation(
                new ResourceLocation(Main.MODID, "real_diamond_pickaxe"), "inventory"
        ));
        event.register(new ModelResourceLocation(
                new ResourceLocation(Main.MODID, "long_stick"),"inventory"
        ));
    }
}
