package com.pha.trainees.client;

import com.pha.trainees.entity.KunTraineesEntity;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class KunTraineesRenderer extends EntityRenderer<KunTraineesEntity> {
    public KunTraineesRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public ResourceLocation getTextureLocation(KunTraineesEntity p_114482_) {
        return new ResourceLocation("trainees","textures/entity/kun_trainees.png");
    }
}

