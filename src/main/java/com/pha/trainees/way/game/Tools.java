package com.pha.trainees.way.game;

import com.pha.trainees.Main;
import com.pha.trainees.config.TraineesConfig;
import com.pha.trainees.registry.ModItems;
import com.pha.trainees.registry.ModTags;
import com.pha.trainees.registry.Something;
import com.pha.trainees.way.math.LogarithmicFunc;
import com.pha.trainees.way.math.Math;
import com.pha.trainees.way.math.QuadraticFuncVertexT;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class Tools {

    // 有关游戏的若干方法



    // 方法
    public static boolean isInTag(Item item, TagKey<Item> tag) {
        return item.getDefaultInstance().is(tag);
    }

    public static void AddEntity(Level level, ItemEntity entity, Item item, int count, boolean ifMove){
        ItemStack itemStack = new ItemStack(item, count);
        double x = entity.getX(), y = entity.getY(), z = entity.getZ();
        ItemEntity itemEntity = new ItemEntity(level, x, y, z, itemStack);
        itemEntity.setDefaultPickUpDelay();
        if (ifMove) {
            LogarithmicFunc func = new LogarithmicFunc(3);


            entity.setDeltaMovement(
                    entity.level().random.nextDouble() * 2 - 1.0,
                    entity.level().random.nextDouble() - 0.5,
                    entity.level().random.nextDouble() * 2 - 1.0
            );
        }


        entity.level().addFreshEntity(itemEntity);
    }

    public static int getPowderMultiplier(Item item) {
        if (item == ModItems.POWDER_ANTI.get()) return Math.POW[0];
        if (item == ModItems.POWDER_ANTI_4.get()) return 4;
        if (item == ModItems.POWDER_ANTI_9.get()) return Math.POW[1];
        if (item == Something.PrankItems.POWDER_ANTI_92.get()) return Math.POW[2];
        if (item == Something.PrankItems.POWDER_ANTI_93.get()) return Math.POW[3];
        if (item == Something.PrankItems.POWDER_ANTI_94.get()) return Math.POW[4];
        if (item == Something.PrankItems.POWDER_ANTI_95.get()) return Math.POW[5];
        if (item == Something.PrankItems.POWDER_ANTI_96.get()) return Math.POW[6];
        if (item == Something.PrankItems.POWDER_ANTI_97.get()) return Math.POW[7];
        if (item == Something.PrankItems.POWDER_ANTI_98.get()) return Math.POW[8];

        return 1;
    }


    public static void Move(ItemStack itemStack){
        int count = itemStack.getCount();
        if (count <= Math.MaxCountMove){

        }

    }

    public static void DoTnt_center(Level level, double x, double y, double z){
        new DoTnt.Builder(level, x, y, z)
                .setCenter(true)
                .setFuse(0)
                .setPower(4.0f)
                .spawn();
    }

    public static void DoTnt_6(Level level, double x, double y, double z, float power, int surfaces, int diffusion){
        new DoTnt.Builder(level, x, y, z)
                .setSurfaces(surfaces, diffusion)
                .setFuse(0)
                .setPower(power)
                .spawn();
    }

    public static double punishmentTimeToSunlight(ItemEntity entity) {
        if ( isInSunlight(entity)) {
            Level level = entity.level();
            long daytime = level.getDayTime() % 24000;

            return new QuadraticFuncVertexT(8 * 10e-8, 6000, 1).getY(daytime >= 23000 ? daytime - 24000 : daytime);
        }

        return 9999;
    }

    public static boolean isInSunlight(ItemEntity entity) {
        Level level = entity.level();
        long daytime = level.getDayTime() % 24000;

        // 检查是否为白天
        //是否下雷雨
        return (daytime >= 0 && daytime <= 13000) || (daytime >= 23000) //是否为白天
                && level.canSeeSky(entity.blockPosition())  //是否看到天空
//                && level.getBrightness(LightLayer.SKY, entity.blockPosition()) == 15 //天空光照等级
                && !level.isRaining()  //是否下雨
                && !level.isThundering();

    }

    public static void setDeltaMovement(ItemEntity entity, int number) {
        // 安全地获取配置值
        int movementMax = TraineesConfig.ENTITY_RANDOM_MOVEMENT_MAX.get();

        if (number >= 1) {
            entity.setDeltaMovement(entity.getDeltaMovement().x, 0.5, entity.getDeltaMovement().z);
        } else {
            entity.setDeltaMovement(
                    entity.level().random.nextDouble() * 2 - 1.0,
                    entity.level().random.nextDouble() - 0.5,
                    entity.level().random.nextDouble() * 2 - 1.0
            );
        }
    }

}