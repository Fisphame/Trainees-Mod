package com.pha.trainees.entity;

import com.pha.trainees.registry.ModEntities;
import com.pha.trainees.registry.ModItems;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class BasketballEntity extends ThrowableItemProjectile {
    private static final EntityDataAccessor<Boolean> ON_GROUND =
            SynchedEntityData.defineId(BasketballEntity.class, EntityDataSerializers.BOOLEAN);

    public BasketballEntity(EntityType<? extends BasketballEntity> type, Level level) {
        super(type, level);
    }

    public BasketballEntity(Level level, LivingEntity owner) {
        super(ModEntities.BASKETBALL.get(), owner, level);
    }

    @Override
    protected Item getDefaultItem() {
        return ModItems.KUN_BASKETBALL.get();
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(ON_GROUND, false);
    }

    @Override
    public void tick() {
        super.tick();

        // 抛物线运动（继承自ThrowableItemProjectile，自带重力）
        if (!this.isOnGround() && this.getDeltaMovement().length() > 0.01) {
            Vec3 motion = this.getDeltaMovement();
            this.setDeltaMovement(motion.scale(0.95)); // 空气阻力
        } else {
            this.setOnGround(true);
        }

        // 碰撞检测
        if (!this.level().isClientSide && !this.isOnGround()) {
            List<LivingEntity> entities = this.level().getEntitiesOfClass(
                    LivingEntity.class,
                    this.getBoundingBox().inflate(0.2),
                    e -> e != this.getOwner()
            );

            if (!entities.isEmpty()) {
                entities.forEach(e ->
                        e.hurt(this.damageSources().thrown(this, this.getOwner()), 5.0F)
                );
                this.setDeltaMovement(Vec3.ZERO);
                this.setOnGround(true);
            }
        }
    }

    public boolean isOnGround() {
        return this.entityData.get(ON_GROUND);
    }

    public void setOnGround(boolean value) {
        this.entityData.set(ON_GROUND, value);
    }
}
