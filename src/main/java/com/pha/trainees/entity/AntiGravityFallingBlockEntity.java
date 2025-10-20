package com.pha.trainees.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class AntiGravityFallingBlockEntity extends FallingBlockEntity {
    private static final java.lang.reflect.Field BLOCK_STATE_FIELD;

    static {
        java.lang.reflect.Field field = null;
        try {
            field = FallingBlockEntity.class.getDeclaredField("blockState");
            field.setAccessible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        BLOCK_STATE_FIELD = field;
    }

    public AntiGravityFallingBlockEntity(EntityType<? extends FallingBlockEntity> entityType, Level level) {
        super(entityType, level);
    }

    public AntiGravityFallingBlockEntity(Level level, double x, double y, double z, BlockState state) {
        super(EntityType.FALLING_BLOCK, level);

        // 使用反射设置 blockState
        if (BLOCK_STATE_FIELD != null) {
            try {
                BLOCK_STATE_FIELD.set(this, state);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        this.blocksBuilding = true;
        this.setPos(x, y, z);
        this.setDeltaMovement(Vec3.ZERO);
        this.xo = x;
        this.yo = y;
        this.zo = z;
        this.setStartPos(this.blockPosition());
    }

    public static AntiGravityFallingBlockEntity fall(Level level, BlockPos pos, BlockState state) {
        AntiGravityFallingBlockEntity antiFBE = new AntiGravityFallingBlockEntity(
                level,
                (double)pos.getX() + 0.5D,
                (double)pos.getY(),
                (double)pos.getZ() + 0.5D,
                state
        );
        level.addFreshEntity(antiFBE);
        return antiFBE;
    }

    @Override
    public @NotNull BlockState getBlockState() {
        // 优先使用反射获取，失败时回退到父类
        if (BLOCK_STATE_FIELD != null) {
            try {
                return (BlockState) BLOCK_STATE_FIELD.get(this);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return super.getBlockState();
    }
}