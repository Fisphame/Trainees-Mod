package com.pha.trainees.client;

import com.pha.trainees.entity.CalledSwordEntity;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class CalledSwordRenderer extends EntityRenderer<CalledSwordEntity> {
    public CalledSwordRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public ResourceLocation getTextureLocation(CalledSwordEntity entity) {
        return new ResourceLocation("trainees", "textures/entity/called_sword.png");
    }


}

