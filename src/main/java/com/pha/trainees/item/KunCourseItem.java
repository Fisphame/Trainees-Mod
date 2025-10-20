package com.pha.trainees.item;

import com.pha.trainees.Main;
import com.pha.trainees.way.DoTnt;
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
            DoTnt doTnt = new DoTnt(true, level, X, Y, Z, 4.0f, 0);
            DoTnt.Do();

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
        DoTnt doTnt = new DoTnt(true, level, X, Y, Z, 4.0f, 0);
        DoTnt.Do();
        // 发送游戏事件
        level.gameEvent(entity, GameEvent.PRIME_FUSE, entity.blockPosition());
    }
}
