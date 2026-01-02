package com.pha.trainees.block;

import com.pha.trainees.registry.ModBlocks;
import com.pha.trainees.registry.ModChemistry;
import com.pha.trainees.way.game.Tools;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;

public class TwoHalfIngotCourseBlock {
    public static class TwoHalfIngotBlock extends Block {
        public TwoHalfIngotBlock(Properties p_49795_) {
            super(p_49795_);
        }

        @Override
        public InteractionResult use(BlockState state, Level level, BlockPos pos,
                                     Player player, InteractionHand hand, BlockHitResult hit) {
            ItemStack itemInHand = player.getItemInHand(hand);

            if (itemInHand.getItem() == Items.HONEYCOMB) {
                if (!level.isClientSide) {
                    if (!player.isCreative()) {
                        itemInHand.shrink(1);
                    }
                    Block targetBlock = ModBlocks.WAXED_TWO_HALF_INGOT_BLOCK.get();

                            level.setBlock(pos, targetBlock.defaultBlockState(), 3);
                    level.playSound(null, pos,
                            net.minecraft.sounds.SoundEvents.HONEYCOMB_WAX_ON,
                            net.minecraft.sounds.SoundSource.BLOCKS,
                            1.0F, 1.0F);

                }

                return InteractionResult.sidedSuccess(level.isClientSide);
            }

            return super.use(state, level, pos, player, hand, hit);
        }

        @Override
        public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
            super.onPlace(state, level, pos, oldState, isMoving);

            // 只在服务器端安排计划刻
            if (!level.isClientSide && level instanceof ServerLevel serverLevel) {

                // 随机
                int randomTicks = Tools.randomInRange(level, 300, 450);

                // 安排一个计划刻，在 randomTicks 后执行
                serverLevel.scheduleTick(pos, this, randomTicks);
            }
        }

        @Override
        public void tick(@NotNull BlockState state, @NotNull ServerLevel level, @NotNull BlockPos pos, @NotNull RandomSource random) {
            super.tick(state, level, pos, random);

            // 确保当前方块还是 TwoHalfIngotBlock
            if (state.getBlock() == this) {
                // 转换为目标方块
                Block targetBlock = ModChemistry.ModChemistryBlocks.CHE_JI2O_BLOCK.get();

                // 替换方块，保持相同的 BlockState 属性（如果有）
                level.setBlock(pos, targetBlock.defaultBlockState(), 3);

                // 在替换方块前播放声音
                level.playSound(null, pos,
                        SoundEvents.FIRE_EXTINGUISH,
                        SoundSource.BLOCKS,
                        0.5F, 1.0F);

                // 生成粒子效果
                for (int i = 0; i < 10; i++) {
                    double d0 = pos.getX() + random.nextDouble();
                    double d1 = pos.getY() + random.nextDouble();
                    double d2 = pos.getZ() + random.nextDouble();
                    level.addParticle(ParticleTypes.SMOKE,
                            d0, d1, d2, 2.0D, 2.0D, 2.0D);
                }
            }
        }

        @Override
        public boolean isRandomlyTicking(BlockState state) {
            return false;
        }
    }

    public static class WaxedTwoHalfIngotBlock extends Block {
        public WaxedTwoHalfIngotBlock(Properties p_49795_) {
            super(p_49795_);
        }
    }
}
