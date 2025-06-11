
package com.pha.trainees.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public class KunTraineesEntity extends Entity {
    public KunTraineesEntity(EntityType<?> type, Level level) {
        super(type, level);
    }

    @Override
    protected void defineSynchedData() {
        // 不需要同步数据，留空
    }

    @Override
    public void tick() {
        super.tick();

    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        // 从 NBT 标签读取数据（若需持久化数据）
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        // 将数据写入 NBT 标签（若需持久化数据）
    }
}

