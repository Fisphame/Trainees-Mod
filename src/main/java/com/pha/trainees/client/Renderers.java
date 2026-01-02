package com.pha.trainees.client;

import com.pha.trainees.Main;
import com.pha.trainees.entity.CalledSwordEntity;
import com.pha.trainees.entity.KunAntiEntity;
import com.pha.trainees.entity.KunTraineesEntity;
import net.minecraft.client.model.ChickenModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class Renderers {
    public static class CalledSwordRenderer extends EntityRenderer<CalledSwordEntity> {
        public CalledSwordRenderer(EntityRendererProvider.Context context) {
            super(context);
        }

        @Override
        public ResourceLocation getTextureLocation(CalledSwordEntity entity) {
            return new ResourceLocation(Main.MODID, "textures/entity/called_sword.png");
        }
    }

    public static class KunTraineesRenderer extends MobRenderer<KunTraineesEntity, ChickenModel<KunTraineesEntity>> {
        public KunTraineesRenderer(EntityRendererProvider.Context context) {
            super(context, new ChickenModel<>(context.bakeLayer(ModelLayers.CHICKEN)), 0.3F);
        }

        @Override
        public ResourceLocation getTextureLocation(KunTraineesEntity entity) {
            return new ResourceLocation(Main.MODID, "textures/entity/kun_trainees.png");
        }
    }

    public static class KunAntiRenderer extends MobRenderer<KunAntiEntity, ChickenModel<KunAntiEntity>> {
        public KunAntiRenderer(EntityRendererProvider.Context context) {
            super(context, new ChickenModel<>(context.bakeLayer(ModelLayers.CHICKEN)), 0.3F);
        }

        @Override
        public ResourceLocation getTextureLocation(KunAntiEntity entity) {
            return new ResourceLocation(Main.MODID, "textures/entity/kun_anti.png");
        }
    }
}
