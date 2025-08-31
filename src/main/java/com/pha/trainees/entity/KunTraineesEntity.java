package com.pha.trainees.entity;

import com.pha.trainees.Main;
import com.pha.trainees.registry.ModEntities;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.entity.monster.Skeleton;
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

    // 重写繁殖方法，生成同类型实体
    @Override
    public KunTraineesEntity getBreedOffspring(ServerLevel level, AgeableMob otherParent) {
        return ModEntities.KUN_TRAINEES.get().create(level);
    }
}
