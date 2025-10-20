package com.pha.trainees.block;

import com.pha.trainees.entity.AntiGravityFallingBlockEntity;
import com.pha.trainees.registry.ModBlocks;
import com.pha.trainees.registry.Something;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashSet;
import java.util.Set;

public class BlackHoleBlock extends Block {

    public BlackHoleBlock(Properties properties) {
        super(properties);
    }

    @Mod.EventBusSubscriber
    public static class EventHandler {

        private static final int range = 16;

        private static final Set<BlockPos> heidongPositions = new HashSet<>();

        @SubscribeEvent
        public static void onBlockPlaced(BlockEvent.EntityPlaceEvent event) {
            BlockState state = event.getPlacedBlock();
            if (state.getBlock() instanceof BlackHoleBlock) {
                BlockPos pos = event.getPos();
                Level level = null;
                if (event.getEntity() != null) {
                    level = event.getEntity().level();
                }

                if (level != null && level.isClientSide()) return;

                heidongPositions.add(pos.immutable());

                makeBlocksFall(level, pos);
            }
        }

        @SubscribeEvent
        public static void onBlockBroken(BlockEvent.BreakEvent event) {
            BlockState state = event.getState();
            if (state.getBlock() instanceof BlackHoleBlock) {
                BlockPos pos = event.getPos();
                heidongPositions.remove(pos);
            }
        }

        @SubscribeEvent
        public static void onEntityTick(LivingEvent.LivingTickEvent event) {
            LivingEntity entity = event.getEntity();
            Level world = entity.level();

            if (world.isClientSide) return;

            BlockPos entityPos = entity.blockPosition();

            for (BlockPos pos : heidongPositions) {
                if (entityPos.distSqr(pos) <= range * range) {
                    applyAttraction(entity, pos);
                }
            }
        }

        private static void makeBlocksFall(Level level, BlockPos centerPos) {
            BlockPos minPos = centerPos.offset(-range, -range, -range);
            BlockPos maxPos = centerPos.offset(range, range, range);

            for (BlockPos pos : BlockPos.betweenClosed(minPos, maxPos)) {
                BlockState state = level.getBlockState(pos);

                if (state.isAir() || pos.equals(centerPos) || state.is(Something.SomethingBlocks.BLACK_HOLE.get()) ) continue;

                if (canBlockFall(level, pos, state)) {
                    AntiGravityFallingBlockEntity fallingBlock = AntiGravityFallingBlockEntity.fall(level, pos, state);

                    Vec3 center = Vec3.atCenterOf(centerPos);
                    Vec3 blockPos = Vec3.atCenterOf(pos);
                    Vec3 direction = center.subtract(blockPos).normalize();
                    fallingBlock.setDeltaMovement(direction.scale(0.1));

                }
            }
        }

        private static boolean canBlockFall(Level level, BlockPos pos, BlockState state) {
            if (state.getDestroySpeed(level, pos) < 0) return false;

            if (state.getFluidState().isSource()) return false;

            return !state.isAir() && state.getBlock() != Blocks.BEDROCK;
        }

        private static void applyAttraction(Entity entity, BlockPos centerPos) {
            Vec3 center = Vec3.atCenterOf(centerPos);
            Vec3 entityPos = entity.position();
            Vec3 direction = center.subtract(entityPos);

            double distance = direction.length();
            if (distance < 0.1) return;

            double strength = 0.15 * (1.0 - Math.min(distance, 8.0) / 8.0);

            Vec3 force = direction.normalize().scale(strength);

            entity.setDeltaMovement(entity.getDeltaMovement().add(force));

            if (distance < 1.0) {
                entity.hurt(entity.damageSources().magic(), 1.0f);
            }
        }
    }
}