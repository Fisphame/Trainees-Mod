package com.pha.trainees.way;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.level.Level;

public class CodeMuseum {
    // 1 - tnt数组和神秘for循环
    public static void DoTnt(Level level, int x, int y, int z, float power, int surface, int distance){
        int O = 0;
        level.playSound(null, x, y, z, SoundEvents.TNT_PRIMED, SoundSource.BLOCKS, 1.0F, 1.0F);

        PrimedTnt[] tnts = new PrimedTnt[]{
                new PrimedTnt(level, x, y+distance, z, null) {
                    @Override
                    protected void explode() {
                        level.explode(this, this.getX(), this.getY(0.0625D), this.getZ(),
                                power, Level.ExplosionInteraction.TNT);
                    }
                },
                new PrimedTnt(level, x, y-distance, z, null) {
                    @Override
                    protected void explode() {
                        level.explode(this, this.getX(), this.getY(0.0625D), this.getZ(),
                                power, Level.ExplosionInteraction.TNT);
                    }
                },
                new PrimedTnt(level, x+distance, y, z, null) {
                    @Override
                    protected void explode() {
                        level.explode(this, this.getX(), this.getY(0.0625D), this.getZ(),
                                power, Level.ExplosionInteraction.TNT);
                    }
                },
                new PrimedTnt(level, x-distance, y, z, null) {
                    @Override
                    protected void explode() {
                        level.explode(this, this.getX(), this.getY(0.0625D), this.getZ(),
                                power, Level.ExplosionInteraction.TNT);
                    }
                },
                new PrimedTnt(level, x, y, z+distance, null) {
                    @Override
                    protected void explode() {
                        level.explode(this, this.getX(), this.getY(0.0625D), this.getZ(),
                                power, Level.ExplosionInteraction.TNT);
                    }
                },
                new PrimedTnt(level, x, y, z-distance, null) {
                    @Override
                    protected void explode() {
                        level.explode(this, this.getX(), this.getY(0.0625D), this.getZ(),
                                power, Level.ExplosionInteraction.TNT);
                    }
                },
        };


        //欸嘿^v^
        for (int i = O; !(i >= surface) ; i -= -1) {
            tnts[i].setFuse(O);
            level.addFreshEntity(tnts[i]);
        }
    }

}
