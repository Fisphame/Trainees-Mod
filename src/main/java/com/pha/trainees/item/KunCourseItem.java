package com.pha.trainees.item;

import com.pha.trainees.Main;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraftforge.registries.ForgeRegistries;

public class KunCourseItem {
    public static class KunNuggetItem extends Item {

        public KunNuggetItem(Properties properties) {
            super(properties);
        }

        private void triggerWaterExplosion(Level level, Entity entity) {
            Do2(level, entity);
        }

        @Override
        public boolean onEntityItemUpdate(ItemStack stack, ItemEntity entity) {
            return Do(1, stack, entity);
        }

    }

    public static class TwoHalfIngotItem extends Item {

        public TwoHalfIngotItem(Properties properties) { super(properties); }

        private void triggerWaterExplosion(Level level, Entity entity) {
            Do2(level, entity);
        }

        @Override
        public boolean onEntityItemUpdate(ItemStack stack, ItemEntity entity) {
            return Do(2, stack, entity);
        }
    }

    public static boolean Do(int number, ItemStack stack, ItemEntity entity) {
        if (!entity.level().isClientSide && entity.isInWater()) {
            Level level = entity.level();
            double X = entity.getX(), Y = entity.getY(), Z = entity.getZ();

            level.playSound(null, X, Y, Z,
                    SoundEvents.TNT_PRIMED,
                    SoundSource.BLOCKS,
                    1.0F, 1.0F
            );

            //+3 创建TNT实体 立即爆炸 添加TNT实体到世界
            PrimedTnt tnt = new PrimedTnt(level, X, Y, Z, null);
            tnt.setFuse(0);
            level.addFreshEntity(tnt);

            //创建物品 获取原物品堆叠个数 创建物品堆叠 创建掉落物实体 设置无敌 设置默认拾取延迟 生成实体
            var item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(Main.MODID, number==1 ? "che_jioh_nugget" : "che_jioh"));
            if (item != null) {
                int count = stack.getCount();
                ItemStack itemStack = new ItemStack(item, count);
                ItemEntity itemEntity = new ItemEntity(level, X, Y, Z, itemStack);
                itemEntity.setInvulnerable(true);
                itemEntity.setDefaultPickUpDelay();
                level.addFreshEntity(itemEntity);
            }

            // 移除物品实体
            entity.discard();
            return true;
        }
        return false;
    }

    public static void Do2(Level level, Entity entity){
        double X = entity.getX(), Y = entity.getY(), Z = entity.getZ();
        // 创建TNT实体
        PrimedTnt tnt = new PrimedTnt(level, X, Y, Z, null);
        tnt.setFuse(0); // 立即爆炸
        // 添加TNT实体到世界
        level.addFreshEntity(tnt);
        level.playSound(null, X, Y, Z, SoundEvents.TNT_PRIMED, SoundSource.BLOCKS, 1.0F, 1.0F);
        // 发送游戏事件
        level.gameEvent(entity, GameEvent.PRIME_FUSE, entity.blockPosition());
    }
}
