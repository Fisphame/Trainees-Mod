package com.pha.trainees.util.interfaces;

import com.pha.trainees.registry.ModSounds;
import com.pha.trainees.util.game.Tools;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;


public interface MineBlock {
    default void whatDoesItMeanMining(Level level, BlockPos pos) {
        if (!level.isClientSide()) {
            SoundEvent sound = Tools.SoundCourse.getIndexSound(Tools.SoundCourse.MINING_SOUNDS.get(), level);
            level.playSound(null, pos, sound, SoundSource.BLOCKS, 0.8F, 1.0F);
        }
    }

    default void finalMining(Level level, BlockPos pos) {
        if (!level.isClientSide()) {
            SoundEvent sound = Tools.SoundCourse.getIndexSound(Tools.SoundCourse.FINAL_MINING_SOUNDS.get(), level);
            level.playSound(null, pos,
                    sound, SoundSource.BLOCKS, 0.8F, 1.0F + (level.getRandom().nextFloat() - 0.5F) * 0.2F
            );
        }
        Tools.Particle.send(level, ParticleTypes.FLAME, pos.getX(), pos.getY(), pos.getZ(), Tools.randomInRange(level, 5, 10),
                0.3, 0.3, 0.3, 0.05);
    }
}
