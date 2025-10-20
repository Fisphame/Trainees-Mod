package com.pha.trainees.way;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.level.Level;

public class DoTnt {
    public static Level level;
    public static double x;
    public static double y;
    public static double z;
    public static int fuse;
    //默认4.0f
    public static float power = 4.0f;
    public static boolean center = false;
    // 快捷n面 x+dif x-dif y+dif y-dif z+dif z-dif 中心
    public static int surfaces = 0;
    public static int diffusion = 1;
    // [1] 是否创建对应面 [2] 对应面偏移 [3] 对应面fuse [4] 对应面威力
    public static boolean[] surface_is;
    public static int[] surface_diffusion;
    public static int[] surface_fuse;
    public static int[] surface_power;



    //仅中心
    public DoTnt(boolean center, Level level, double x, double y, double z, float power, int fuse){
        this.center = center;
        this.level = level;
        this.x = x;
        this.y = y;
        this.z = z;
        this.power = power;
        this.fuse = fuse;
    }

    //快捷多面 (1-7)
    public DoTnt(Level level, double x, double y, double z, float power, int fuse, int surfaces, int diffusion){
        this.level = level;
        this.x = x;
        this.y = y;
        this.z = z;
        this.power = power;
        this.fuse = fuse;
        this.surfaces = surfaces;
        this.diffusion = diffusion;
    }

    //全部自定义 (最多7面)
    public DoTnt(Level level, double x, double y, double z, float power, int fuse, boolean[] surface_is, int[] surface_diffusion, int[] surface_fuse, int[] surface_power){
        this.level = level;
        this.x = x;
        this.y = y;
        this.z = z;
        this.power = power;
        this.fuse = fuse;
        this.surface_is = surface_is;
        this.surface_diffusion = surface_diffusion;
        this.surface_fuse = surface_fuse;
        this.surface_power = surface_power;
    }



    public static void Do(){

        if (center) {
            PrimedTnt tnt = new PrimedTnt(level, x, y, z, null) {
                    @Override
                    protected void explode() {
                        level.explode(this, this.getX(), this.getY(0.0625D), this.getZ(),
                                power, Level.ExplosionInteraction.TNT);
                    }
            };

            explode(level, x, y, z, tnt, fuse);
            return;
        }

        if (surfaces != 0 && diffusion != 0) {
            for (int i = 1; i <= surfaces ; i ++){
                double xn = (i == 1 ? x + diffusion : (i == 2 ? x - diffusion : x)),
                        yn = (i == 3 ? y + diffusion : (i == 4 ? y - diffusion : y)),
                        zn = (i == 5 ? z + diffusion : (i == 6 ? z - diffusion : z));
                PrimedTnt tnt = new PrimedTnt(level, xn, yn, zn, null) {
                    @Override
                    protected void explode() {
                        level.explode(this, this.getX(), this.getY(0.0625D), this.getZ(),
                                power, Level.ExplosionInteraction.TNT);
                    }
                };

                explode(level, xn, yn, zn, tnt, fuse);
            }
            return;
        }

        if (surface_power.length != 0 && surface_fuse.length != 0 && surface_diffusion.length != 0) {
            for (int j = 1; j <= 7; j++) {
                int i = j;
                if (!surface_is[i]) continue;
                double xn = x + surface_diffusion[i], yn = y + surface_diffusion[i], zn = z + surface_diffusion[i];
                PrimedTnt tnt = new PrimedTnt(level, xn, yn, zn, null) {
                    @Override
                    protected void explode() {
                        level.explode(this, this.getX(), this.getY(0.0625D), this.getZ(),
                                surface_power[i], Level.ExplosionInteraction.TNT);
                    }
                };

                explode(level, xn, yn, zn, tnt, surface_fuse[i]);
            }
            return;
        }

        PrimedTnt tnt = new PrimedTnt(level, x, y, z, null) {
            @Override
            protected void explode() {
                level.explode(this, this.getX(), this.getY(0.0625D), this.getZ(),
                        0f, Level.ExplosionInteraction.TNT);
            }
        };
        explode(level, x, y, z, tnt, 0);

    }

    public static void explode(Level level, double x, double y, double z, PrimedTnt tnt, int fuse){
        level.playSound(null, x, y, z, SoundEvents.TNT_PRIMED, SoundSource.BLOCKS, 1.0F, 1.0F);
        tnt.setFuse(fuse);
        level.addFreshEntity(tnt);
    }
}
