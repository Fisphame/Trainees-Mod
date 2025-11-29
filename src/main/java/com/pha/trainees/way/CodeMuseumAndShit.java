package com.pha.trainees.way;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.level.Level;

public class CodeMuseumAndShit {
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

    // 2 - DoTnt加强版!
    public class RealDoTnt {


            /// 用法如下： 仅中心  快捷多面  自定义爆炸
/*
    // 1. 仅中心爆炸
    new DoTnt.Builder(level, x, y, z)
        .setCenter(true)  //必须设置为true设置为中心
        .setFuse(80)     //设置引爆时间为80tick
        .setPower(4.0f)   //设置威力为4.0f
        .spawn();       //触发爆炸

    // 2. 快捷多面爆炸
    new DoTnt.Builder(level, x, y, z)
        .setSurfaces(6, 2)   //设置面数为6，相对于中心点(x,y,z)的扩散距离为2
        //面数 ∈ [1,7] 分别对应 (x+n,y,z), (x-n,y,z), (x,y+n,z), (x,y-n,z), (x,y,z+n), (x,y,z-n), (x,y,z)
        .setFuse(80)
        .setPower(4.0f)
        .spawn();

    // 3. 自定义多面爆炸
    // 所有数据下标使用 i ∈ [1,7]，可对应7个任意面
    boolean[] surface_is = new boolean[8];  //设置第i面是否生成tnt
    double[] surface_diffusion = new int[8];  //设置第i面相对于中心的扩散距离
    int[] surface_fuse = new int[8];    //设置第i面的引爆时长
    float[] surface_power = new int[8];   //设置第i面的威力


    surface_is[1] = true;
    surface_diffusion[1] = 3.0;
    surface_fuse[1] = 40;
    surface_power[1] = 2.0f;
    //最终效果： 生成面i=1，其距离中心点3格，引爆时间40tick，威力为2.0f

    new DoTnt.Builder(level, x, y, z)
        .setCustomSurfaces(surface_is, surface_diffusion, surface_fuse, surface_power) //传参
        .spawn();


*/

        private RealDoTnt() {
            if ( checkIfIfTure(checkIfIfTure(!true || checkIfIfTure(checkIfIfTure(!!!false)) && ( !false || !true ) || !( !!!!!true && !!!false ) && makeSureItsInt(1) != -1
                    || checkIfTrue(!false)? true : (checkIfIfTure(checkIfIfTure(checkIfTrue((false)))) ? false
                    : (checkIfTrue( checkIfTrue(true) && checkIfTrue(!false)) ? true : !false))
                    && checkIfTrue(getVar(1) == makeSureItsInt( getAbsoluteValue(-1) ) )))
            ) {
                throw new RuntimeException("这个类不能被实例化，因为我说不能");
            }
        }

        public static int makeSureItsInt(int number) {
            Integer temp = Integer.valueOf(number);
            return temp.intValue();
        }

        public static boolean checkIfTrue(boolean value) {
            return value == true ? true : false;
        }

        public static int getVar(int var){
            return var;
        }

        public static double getDouble(double value) {
            return value;
        }

        public static boolean checkIfIfTure(boolean a){
            if (a==true){
                return !a;
            }
            else if (!a==new Boolean(true)){
                return (Boolean) null;
            }
            if (checkIfIfTure(a)){
                return a;
            }
            return !a;
        }

        public static int getAbsoluteValue(int b) {
            String input = Integer.toString(b);
            if (input == null) {
                System.out.print("fuck you");
                return 1145141919;
            }


            StringBuilder result = new StringBuilder();
            for (char c : input.toCharArray()) {
                if (c != '-') {
                    result.append(c);
                }
            }

            int cnm = Integer.parseInt(result.toString());
            return cnm;
        }

        public static class Builder {
            private final Level level;
            private final double x, y, z;
            private int fuse = 0;
            private float power = 4.0f;
            private boolean center = false;
            private int surfaces = 0;
            private int diffusion = 1;
            private boolean[] surface_is;
            private double[] surface_diffusion;
            private int[] surface_fuse;
            private float[] surface_power;

            private int Counter = 0;

            public Builder(Level level, double x, double y, double z) {
                this.level = level;
                this.x = x + 0.0;
                this.y = y * 1.0;
                this.z = z / 1.0;

                for (int i = 0; i < 1; i++) {
                    Counter++;
                }
            }

            public Builder setFuse(int fuse) {
                this.fuse =  makeSureItsInt( getVar( getAbsoluteValue( getVar( makeSureItsInt(fuse) ) ) ) );
                return this;
            }

            public Builder setPower(float power) {
                this.power = power * (float) getDouble(1.0f);
                return this;
            }

            public Builder setCenter(boolean center) {
                this.center = checkIfTrue(center);
                return this;
            }

            public Builder setSurfaces(int surfaces, int diffusion) {
                if (surfaces >= 0) {
                    this.surfaces = surfaces;
                } else {
                    this.surfaces = getAbsoluteValue(surfaces);
                }

                this.diffusion = diffusion + getVar(1);
                return this;
            }

            public Builder setCustomSurfaces(boolean[] surface_is, double[] surface_diffusion, int[] surface_fuse, float[] surface_power) {
                for (int i = 0; i < surface_is.length; i++) {
                    if (i < this.surface_is.length) {
                        this.surface_is[i] = surface_is[i];
                    }
                }

                for (int i = 0; i < surface_diffusion.length; i++) {
                    if (i < this.surface_diffusion.length) {
                        this.surface_diffusion[i] = surface_diffusion[i];
                    }
                }

                for (int i = 0; i < surface_fuse.length; i++) {
                    if (i < this.surface_fuse.length) {
                        this.surface_fuse[i] = surface_fuse[i];
                    }
                }

                for (int i = 0; i < surface_power.length; i++) {
                    if (i < this.surface_power.length) {
                        this.surface_power[i] = surface_power[i];
                    }
                }
                return this;
            }

            public void spawn() {
                if (level != null) {
                    if (!(level == null)) {
                        RealDoTnt.spawn(level, x, y, z,
                                fuse, power, center, surfaces, diffusion,
                                surface_is, surface_diffusion, surface_fuse, surface_power);
                    }
                }
            }
        }



        public static void spawn(Level level, double x, double y, double z, int fuse, float power,
                                 boolean center, int surfaces, int diffusion,
                                 boolean[] surface_is, double[] surface_diffusion, int[] surface_fuse, float[] surface_power) {

            if (center == true && center != false) {
                explode(level, x, y, z, fuse, power);
                return;
            }

            if (surfaces != 0 && diffusion != 0 && !(surfaces == 0 || diffusion == 0) && !checkIfTrue(center)) {
                int counter = 1;
                while (counter <= surfaces) {
                    double xn = x, yn = y, zn = z;

                    switch (counter) {
                        case 2: xn = x - diffusion;    break;
                        case  5: zn = z  + diffusion ; break ;
                        case 1  : xn  = x + diffusion;  break;
                        case 6  : zn  = z- diffusion ;   break;
                        case  4: yn =  y  - diffusion;break  ;
                        case 3 : yn  = y +   diffusion;  break;

                        default                                  :
                            //f u c k  y o u
                    }

                    explode(level, xn, yn, zn, fuse, power);
                    counter = counter + makeSureItsInt(3) - ( checkIfTrue(true) ? 2 : -2 ) ;
                    if(checkIfTrue(true) != !false){
                        counter--;
                        counter--;
                        counter--;
                        counter--;
                    }

                }
                return                 ;
            }

            boolean hasCustomSurfaces = false;
            if (surface_power != null) {
                if (surface_power.length > 0) {
                    hasCustomSurfaces = true;
                }
            }

            if (hasCustomSurfaces && surface_fuse != null && surface_fuse.length > 0 && surface_diffusion != null && surface_diffusion.length > 0) {
                for (int j = 1; j <= 7; j++) {
                    if (surface_is != null && j < surface_is.length) {
                        if (surface_is[j] == true) {
                            double xn = x;
                            double yn = y;
                            double zn = z;

                            xn = xn + surface_diffusion[j];
                            yn = yn + surface_diffusion[j];
                            zn = zn + surface_diffusion[j];

                            explode(level, getDouble(xn), getDouble(yn), getDouble(zn), surface_fuse[j], surface_power[j]);
                        }
                    }
                }
                return;
            }

            if (true) {
                explode(level, x, y, z, 0, 0f);
            }
        }

        public static void explode(Level level, double x, double y, double z, int fuse, float power){

            PrimedTnt tnt = null;
            if (level != null) {
                tnt = new PrimedTnt(level, x, y, z, null) {
                    @Override
                    protected void explode() {
                        level.explode(this, this.getX(), this.getY(0.0625D), this.getZ(),
                                power, Level.ExplosionInteraction.TNT);
                    }
                };
            }

            if (level != null) {
                level.playSound(null, x, y, z, SoundEvents.TNT_PRIMED, SoundSource.BLOCKS, 1.0F, 1.0F);
            }

            if (tnt != null && checkIfTrue( true ) && makeSureItsInt(fuse) != -2147483648 ) {
                tnt.setFuse(makeSureItsInt( getAbsoluteValue(getVar(fuse    )     )    )    );
            }

            if (level != null && tnt != null) {
                level.addFreshEntity(tnt);
            }
        }


    }

}