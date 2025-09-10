package com.pha.trainees.item;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;

public class KunCourseItem {
    public static class KunNuggetItem extends Item {

        public KunNuggetItem(Properties properties) {
            super(properties);
        }


        // 重写inventoryTick方法，检测物品是否在水中（如在物品栏中掉入水中）
        @Override
        public void inventoryTick(ItemStack stack, Level level, net.minecraft.world.entity.Entity entity, int slotId, boolean isSelected) {
/*
        if (!level.isClientSide && entity != null && entity.isInWater()) {
            triggerWaterExplosion(level, entity);
            if (entity instanceof Player player && !player.getAbilities().instabuild) {
                stack.shrink(1); // 消耗物品
            }
        }
*/
            super.inventoryTick(stack, level, entity, slotId, isSelected);
        }

//    // 创建一个自定义的ItemEntity来处理掉落物情况
//    public static class ExplosiveItemEntity extends ItemEntity {
//        public ExplosiveItemEntity(Level level, double x, double y, double z, ItemStack stack) {
//            super(level, x, y, z, stack);
//            // 设置拾取延迟，防止立即被拾取
//            this.setPickUpDelay(10); // 10 ticks (0.5秒)的延迟
//        }
//
//        @Override
//        public void tick() {
//            super.tick();
//
//            // 检查是否在水中
//            if (!this.level().isClientSide && this.isInWater()) {
//                ItemStack stack = this.getItem();
//                if (stack.getItem() instanceof TwoHalfIngotItem) {
//                    // 创建TNT实体
//                    PrimedTnt tnt = new PrimedTnt(
//                            this.level(),
//                            this.getX(),
//                            this.getY(),
//                            this.getZ(),
//                            null
//                    );
//                    tnt.setFuse(0); // 立即爆炸
//
//                    // 添加TNT实体到世界
//                    this.level().addFreshEntity(tnt);
//
//                    // 播放声音
//                    this.level().playSound(
//                            null,
//                            this.getX(),
//                            this.getY(),
//                            this.getZ(),
//                            SoundEvents.TNT_PRIMED,
//                            SoundSource.BLOCKS,
//                            1.0F,
//                            1.0F
//                    );
//
//                    // 移除物品实体
//                    this.discard();
//                }
//            }
//        }
//    }

        // 触发水中爆炸的方法
        private void triggerWaterExplosion(Level level, net.minecraft.world.entity.Entity entity) {
            // 创建TNT实体
            PrimedTnt tnt = new PrimedTnt(
                    level,
                    entity.getX(),
                    entity.getY(),
                    entity.getZ(),
                    null
            );
            tnt.setFuse(0); // 立即爆炸
            // 添加TNT实体到世界
            level.addFreshEntity(tnt);
            // 播放声音
            level.playSound(
                    null,
                    entity.getX(),
                    entity.getY(),
                    entity.getZ(),
                    SoundEvents.TNT_PRIMED,
                    SoundSource.BLOCKS,
                    1.0F,
                    1.0F
            );
            // 发送游戏事件
            level.gameEvent(entity, GameEvent.PRIME_FUSE, entity.blockPosition());
        }

        // 重写该方法以创建自定义的ItemEntity
        @Override
        public boolean onEntityItemUpdate(ItemStack stack, ItemEntity entity) {
            if (!entity.level().isClientSide && entity.isInWater()) {
                // 创建TNT实体
                PrimedTnt tnt = new PrimedTnt(
                        entity.level(),
                        entity.getX(),
                        entity.getY(),
                        entity.getZ(),
                        null
                );
                tnt.setFuse(0); // 立即爆炸
                // 添加TNT实体到世界
                entity.level().addFreshEntity(tnt);
                // 播放声音
                entity.level().playSound(
                        null,
                        entity.getX(),
                        entity.getY(),
                        entity.getZ(),
                        SoundEvents.TNT_PRIMED,
                        SoundSource.BLOCKS,
                        1.0F,
                        1.0F
                );
                // 移除物品实体
                entity.discard();
                return true;
            }
            return false;
        }
    }

    public static class TwoHalfIngotItem extends Item {

        public TwoHalfIngotItem(Properties properties) {
            super(properties);
        }


        // 重写inventoryTick方法，检测物品是否在水中（如在物品栏中掉入水中）
        @Override
        public void inventoryTick(ItemStack stack, Level level, net.minecraft.world.entity.Entity entity, int slotId, boolean isSelected) {
/*
        if (!level.isClientSide && entity != null && entity.isInWater()) {
            triggerWaterExplosion(level, entity);
            if (entity instanceof Player player && !player.getAbilities().instabuild) {
                stack.shrink(1); // 消耗物品
            }
        }
*/
            super.inventoryTick(stack, level, entity, slotId, isSelected);
        }

//    // 创建一个自定义的ItemEntity来处理掉落物情况
//    public static class ExplosiveItemEntity extends ItemEntity {
//        public ExplosiveItemEntity(Level level, double x, double y, double z, ItemStack stack) {
//            super(level, x, y, z, stack);
//            // 设置拾取延迟，防止立即被拾取
//            this.setPickUpDelay(10); // 10 ticks (0.5秒)的延迟
//        }
//
//        @Override
//        public void tick() {
//            super.tick();
//
//            // 检查是否在水中
//            if (!this.level().isClientSide && this.isInWater()) {
//                ItemStack stack = this.getItem();
//                if (stack.getItem() instanceof TwoHalfIngotItem) {
//                    // 创建TNT实体
//                    PrimedTnt tnt = new PrimedTnt(
//                            this.level(),
//                            this.getX(),
//                            this.getY(),
//                            this.getZ(),
//                            null
//                    );
//                    tnt.setFuse(0); // 立即爆炸
//
//                    // 添加TNT实体到世界
//                    this.level().addFreshEntity(tnt);
//
//                    // 播放声音
//                    this.level().playSound(
//                            null,
//                            this.getX(),
//                            this.getY(),
//                            this.getZ(),
//                            SoundEvents.TNT_PRIMED,
//                            SoundSource.BLOCKS,
//                            1.0F,
//                            1.0F
//                    );
//
//                    // 移除物品实体
//                    this.discard();
//                }
//            }
//        }
//    }

        // 触发水中爆炸的方法
        private void triggerWaterExplosion(Level level, net.minecraft.world.entity.Entity entity) {
            // 创建TNT实体
            PrimedTnt tnt = new PrimedTnt(
                    level,
                    entity.getX(),
                    entity.getY(),
                    entity.getZ(),
                    null
            );
            tnt.setFuse(0); // 立即爆炸
            // 添加TNT实体到世界
            level.addFreshEntity(tnt);
            // 播放声音
            level.playSound(
                    null,
                    entity.getX(),
                    entity.getY(),
                    entity.getZ(),
                    SoundEvents.TNT_PRIMED,
                    SoundSource.BLOCKS,
                    1.0F,
                    1.0F
            );
            // 发送游戏事件
            level.gameEvent(entity, GameEvent.PRIME_FUSE, entity.blockPosition());
        }

        // 重写该方法以创建自定义的ItemEntity
        @Override
        public boolean onEntityItemUpdate(ItemStack stack, ItemEntity entity) {
            if (!entity.level().isClientSide && entity.isInWater()) {
                // 创建TNT实体
                PrimedTnt tnt = new PrimedTnt(
                        entity.level(),
                        entity.getX(),
                        entity.getY(),
                        entity.getZ(),
                        null
                );
                tnt.setFuse(0); // 立即爆炸
                // 添加TNT实体到世界
                entity.level().addFreshEntity(tnt);
                // 播放声音
                entity.level().playSound(
                        null,
                        entity.getX(),
                        entity.getY(),
                        entity.getZ(),
                        SoundEvents.TNT_PRIMED,
                        SoundSource.BLOCKS,
                        1.0F,
                        1.0F
                );
                // 移除物品实体
                entity.discard();
                return true;
            }
            return false;
        }
    }
}
