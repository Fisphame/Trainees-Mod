package com.pha.trainees.block;

import com.pha.trainees.registry.ModChemistry;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import javax.annotation.Nullable;
import java.util.List;

public class ChemistryBlock {
    public static class JiOHBlock extends Block {
        public JiOHBlock(Properties p_49795_) {super(p_49795_);}


        public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag flag) {
            super.appendHoverText(stack, level, tooltipComponents, flag);

            tooltipComponents.add(Component.translatable("tooltip.trainees.jioh"));
            tooltipComponents.add(Component.translatable("tooltip.trainees.jioh.2"));



        }


    }

    public static class Ji2OBlock extends Block {
        public Ji2OBlock(Properties p_49795_) {
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
                    Block targetBlock = ModChemistry.ModChemistryBlocks.CHE_WAXED_JI2O_BLOCK.get();

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
    }

    public static class WaxedJi2OBlock extends Block {
        public WaxedJi2OBlock(Properties p_49795_) {
            super(p_49795_);
        }
    }

    public static class Ji2O2Block extends Block {
        public Ji2O2Block(Properties p_49795_) {
            super(p_49795_);
        }
    }
}
