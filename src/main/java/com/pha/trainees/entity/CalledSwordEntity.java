
package com.pha.trainees.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.List;


public class CalledSwordEntity extends Entity {

    public CalledSwordEntity(EntityType<?> type, Level level) {
        super(type, level);
        this.damage = 0.0f ;
        this.direction = Direction.NORTH;
        this.setDeltaMovement(
                Direction.NORTH.getStepX(),
                0,
                Direction.NORTH.getStepZ());
    }

    //传入伤害
    public void setDamage(float damage) {
        this.damage = damage;
    }

    //传入方向和移动向量
    private final float speed = 0.75f; //速度
    public void setDirection(Direction direction){
        this.direction = direction;
        this.setDeltaMovement(
                direction.getStepX() * speed,
                0,
                direction.getStepZ() * speed
        );
    }








    private float damage;
    private Direction direction;
    private int lifespan = 20 * 4;
    @Override
    protected void defineSynchedData() {
        // 不需要同步数据，留空
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide) return;

        // 移动逻辑
        if (lifespan-- <= 0) {
            this.discard();
            return;
        }
        this.setPos(this.position().add(this.getDeltaMovement()));

        // 检测碰撞并造成伤害
        checkHitEntities();
        // 破坏方块
        destroyBlocks();
    }

    private void checkHitEntities() {
        List<LivingEntity> entities = this.level().getEntitiesOfClass(
                LivingEntity.class,
                this.getBoundingBox().inflate(1.0)
        );
        for (LivingEntity entity : entities) {
            if (entity instanceof Player) continue; // 不伤害玩家
            entity.hurt(this.damageSources().magic(), damage);
        }
    }

    private void destroyBlocks() {
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                for (int dz = -1; dz <= 1; dz++) {
                    pos.set(
                            this.getX() + dx,
                            this.getY() + dy,
                            this.getZ() + dz
                    );
                    this.level().destroyBlock(pos, false); // 不生成掉落物
                }
            }
        }
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

