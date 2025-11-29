package com.pha.trainees.item;

import com.pha.trainees.way.chemistry.ReactionSystem;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

public class KunCourseItem {

    public static boolean on(ItemStack stack, ItemEntity entity) {
        if (!entity.level().isClientSide) {
            return ReactionSystem.ReactionRegistry.triggerReactions(stack, entity);
        }
        return false;
    }

    public static class KunNuggetItem extends Item {

        public KunNuggetItem(Properties properties) {
            super(properties);
        }

        @Override
        public boolean onEntityItemUpdate(ItemStack stack, ItemEntity entity) {
            return on(stack, entity);
        }

    }

    public static class TwoHalfIngotItem extends Item {

        public TwoHalfIngotItem(Properties properties) { super(properties); }

        @Override
        public boolean onEntityItemUpdate(ItemStack stack, ItemEntity entity) {
            return on(stack, entity);
        }
    }

    public static class TwoHalfIngotBlockItem extends BlockItem {

        public TwoHalfIngotBlockItem(Block p_40565_, Properties p_40566_) {
            super(p_40565_, p_40566_);
        }

        @Override
        public boolean onEntityItemUpdate(ItemStack stack, ItemEntity entity) {
            return on(stack, entity);
        }
    }




}
