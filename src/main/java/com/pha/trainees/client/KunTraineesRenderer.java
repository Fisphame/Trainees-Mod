package com.pha.trainees.client;

import com.pha.trainees.Main;
import com.pha.trainees.entity.KunTraineesEntity;
import net.minecraft.client.model.ChickenModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class KunTraineesRenderer extends MobRenderer<KunTraineesEntity, ChickenModel<KunTraineesEntity>> {
    // 使用原版鸡的模型
    public KunTraineesRenderer(EntityRendererProvider.Context context) {
        super(context, new ChickenModel<>(context.bakeLayer(ModelLayers.CHICKEN)), 0.3F);
    }

    @Override
    public ResourceLocation getTextureLocation(KunTraineesEntity entity) {
        return new ResourceLocation(Main.MODID, "textures/entity/kun_trainees.png");
    }
}