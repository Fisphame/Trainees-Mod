package com.pha.trainees.item;

import com.pha.trainees.registry.ModBlocks;
import com.pha.trainees.way.DoTnt;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;

public class PowderAntiCourseItem {
    public static class PowderAntiItem extends Item{
        public PowderAntiItem(Properties p_41383_) {
            super(p_41383_);
        }

        @Override
        public InteractionResult useOn(UseOnContext context){
            return Do(context, 9);
        }
    }

    public static class PowderAnti4Item extends Item{
        public PowderAnti4Item(Properties p_41383_) {
            super(p_41383_);
        }

    }

    public static class PowderAnti9Item extends Item {

        public PowderAnti9Item(Properties p_41383_) {
            super(p_41383_);
        }

        @Override
        public InteractionResult useOn(UseOnContext context){
            return Do(context, 1);
        }
    }

    public static InteractionResult Do(UseOnContext context, int num){
        //检测端
        if (context.getLevel().isClientSide()) return InteractionResult.FAIL;

        //检测数量
        Player player = context.getPlayer();
        if (context.getItemInHand().getCount() < num && !player.isCreative()) return InteractionResult.FAIL;

        //检测方块
        Level level = context.getLevel();
        BlockPos clickedPos = context.getClickedPos();
        BlockState clickedState = context.getLevel().getBlockState(clickedPos);
        ResourceLocation clickedBlockId = ForgeRegistries.BLOCKS.getKey(clickedState.getBlock());
        BlockPos belowPos = clickedPos.below();
        BlockState belowState = level.getBlockState(belowPos);
        ResourceLocation belowBlockId = ForgeRegistries.BLOCKS.getKey(belowState.getBlock());
        double x = clickedPos.getX(), y = clickedPos.getY(), z = clickedPos.getZ();
        boolean isRight = clickedBlockId != null && clickedBlockId.toString().equals("trainees:two_half_ingot_block")
                && belowBlockId != null && belowBlockId.toString().equals("minecraft:campfire");

        if (!isRight) return InteractionResult.FAIL;

        //检测篝火状态
        boolean isLit = false;
        if (belowState.hasProperty(net.minecraft.world.level.block.CampfireBlock.LIT)) {
            isLit = belowState.getValue(net.minecraft.world.level.block.CampfireBlock.LIT);
        }
        if (!isLit) return InteractionResult.FAIL;



        //实现逻辑
        Block targetBlock = ForgeRegistries.BLOCKS.getValue(ModBlocks.BASKETBALL_ANTI_BLOCK_RGT.getId());

        if (targetBlock != null) {
            // 替换方块
            context.getLevel().setBlock(clickedPos, targetBlock.defaultBlockState(), 3);
            if (player != null && !player.isCreative()) {
                context.getItemInHand().shrink(num);
            }
            context.getLevel().playSound(null, clickedPos, SoundEvents.STONE_PLACE, SoundSource.BLOCKS, 1.0F, 1.0F);
            DoTnt(level, x, y, z, 6.0F, 6, 1);

            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    public static void DoTnt(Level level, double x, double y, double z, float power, int surfaces, int diffusion){
        DoTnt doTnt = new DoTnt(level, x, y, z, power, 0, surfaces, diffusion);
        DoTnt.Do();
    }
}
