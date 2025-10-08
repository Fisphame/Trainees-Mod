package com.pha.trainees.event;

import com.pha.trainees.Main;
import com.pha.trainees.entity.KunAntiEntity;
import com.pha.trainees.entity.KunTraineesEntity;
import com.pha.trainees.registry.ModEntities;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Main.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class RegisterAttributes {
    @SubscribeEvent
    public static void entityAttributeCreation(EntityAttributeCreationEvent event) {
        event.put(ModEntities.KUN_TRAINEES.get(), KunTraineesEntity.createAttributes().build());
        event.put(ModEntities.KUN_ANTI.get(), KunAntiEntity.createAttributes().build());
    }
}