package com.pha.trainees.item;

import com.pha.trainees.registry.ModBlocks;
import com.pha.trainees.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.List;


public class Duihuanquan extends Item {
    public Duihuanquan(Properties p_41383_){
        super(p_41383_);
    }

    public InteractionResult useOn(UseOnContext context){
        Level level = context.getLevel();
        BlockPos blockpos = context.getClickedPos();
        BlockState blockstate = level.getBlockState(blockpos);
        Block block = blockstate.getBlock();
        if (block == ModBlocks.MYBLOCK.get()) {
            Player player = context.getPlayer();
            ItemStack itemstack = new ItemStack(ModItems.UPGRADE_THEME.get(), 1);
            ItemEntity itemEntity = new ItemEntity(level, blockpos.getX(), blockpos.getY() + 1, blockpos.getZ(), itemstack);
            itemEntity.level().addFreshEntity(itemEntity);

            itemstack.shrink(1);
            level.playSound(player, blockpos, SoundEvents.CHICKEN_DEATH, SoundSource.BLOCKS, 1.0F, 1.0F);
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        return super.useOn(context);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltipComponents, flag);

        tooltipComponents.add(Component.translatable("tooltip.trainees.duihuanquan"));
        tooltipComponents.add(Component.translatable("tooltip.trainees.duihuanquan.2"));
    }
}

