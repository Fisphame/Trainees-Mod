package com.pha.trainees.entity;

import com.pha.trainees.Main;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.level.Level;

public class KunTraineesEntity extends Chicken {
    public KunTraineesEntity(EntityType<? extends Chicken> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Chicken.createAttributes()
                .add(Attributes.MAX_HEALTH, 6.0) // 可调整属性值
                .add(Attributes.MOVEMENT_SPEED, 0.25);
    }

    @Override
    protected ResourceLocation getDefaultLootTable() {
        return new ResourceLocation(Main.MODID, "entities/kun_trainees"); // 指向自定义战利品表
    }
}
