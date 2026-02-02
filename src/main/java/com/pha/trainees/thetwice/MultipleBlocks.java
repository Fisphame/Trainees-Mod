package com.pha.trainees.thetwice;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.pattern.BlockPattern;
import net.minecraft.world.level.block.state.pattern.BlockPatternBuilder;

public class MultipleBlocks {

    public static final BlockPattern AIR_PATTERN = BlockPatternBuilder.start()
            .aisle("AAA",
                             "AAA",
                             "DDD")

            .aisle("BCB",
                             "BBB",
                             "DDD")

            .aisle("AAA",
                             "AAA",
                             "AAA")


            .build();

    public static BlockPos blockPos;

    public static boolean detectAndActivate(Level level, BlockPos centerPos, Player player) {
        BlockPattern.BlockPatternMatch match = AIR_PATTERN.find(level, centerPos);


        if (match != null) {
            activateStructure(level, match, player);
            return true;
        }

        return false;
    }

    private static void activateStructure(Level level, BlockPattern.BlockPatternMatch match, Player player) {
        BlockPos patternCenter = match.getFrontTopLeft().offset(0, -1, -1); // 计算中心位置

        blockPos=patternCenter;

        level.playSound(player, patternCenter, SoundEvents.BEACON_ACTIVATE,
                SoundSource.BLOCKS, 1.0F, 1.0F);

        for (int i = 0; i < 20; i++) {
            double x = patternCenter.getX() + 0.5D + (level.random.nextDouble() - 0.5D) * 3.0D;
            double y = patternCenter.getY() + 0.5D + (level.random.nextDouble() - 0.5D) * 3.0D;
            double z = patternCenter.getZ() + 0.5D + (level.random.nextDouble() - 0.5D) * 3.0D;

            level.addParticle(ParticleTypes.ENCHANT, x, y, z, 0.0D, 0.0D, 0.0D);
        }





        // 生成特殊实体
        // 给予玩家效果
        // 转换方块
        // 打开传送门等
    }

}