package com.pha.trainees.item;

import com.pha.trainees.way.ChemicalReaction;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

public class KunCourseItem {
    public static class KunNuggetItem extends Item {

        public KunNuggetItem(Properties properties) {
            super(properties);
        }

        @Override
        public boolean onEntityItemUpdate(ItemStack stack, ItemEntity entity) {
            return ChemicalReaction.JiAndH2O(1, stack, entity);
        }

    }

    public static class TwoHalfIngotItem extends Item {

        public TwoHalfIngotItem(Properties properties) { super(properties); }

        @Override
        public boolean onEntityItemUpdate(ItemStack stack, ItemEntity entity) {
            return ChemicalReaction.JiAndH2O(2, stack, entity);
        }
    }

    public static class TwoHalfIngotBlockItem extends BlockItem {

        public TwoHalfIngotBlockItem(Block p_40565_, Properties p_40566_) {
            super(p_40565_, p_40566_);
        }

        @Override
        public boolean onEntityItemUpdate(ItemStack stack, ItemEntity entity) {
            return ChemicalReaction.JiAndH2O(3, stack, entity);
        }
    }




}
