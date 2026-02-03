package com.pha.trainees.item;

import com.pha.trainees.item.interfaces.Chemistry;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;

public class PowderAntiCourseItem {

    public static class PowderAntiItem extends Item implements Chemistry {
        public PowderAntiItem(Properties p_41383_) {
            super(p_41383_);
        }

        @Override
        public InteractionResult useOn(UseOnContext context){
            return JiBpCombination_1(context, 9);
        }

        @Override
        public boolean onEntityItemUpdate(ItemStack stack, ItemEntity entity) {
            return on(stack, entity);
        }
    }

    public static class PowderAnti4Item extends Item implements Chemistry{
        public PowderAnti4Item(Properties p_41383_) {
            super(p_41383_);
        }

        @Override
        public boolean onEntityItemUpdate(ItemStack stack, ItemEntity entity) {
            return on(stack, entity);
        }
    }

    public static class PowderAnti9Item extends Item implements Chemistry{
        public PowderAnti9Item(Properties p_41383_) {
            super(p_41383_);
        }

        @Override
        public InteractionResult useOn(UseOnContext context){
            return JiBpCombination_1(context, 1);
        }

        @Override
        public boolean onEntityItemUpdate(ItemStack stack, ItemEntity entity) {
            return on(stack, entity);
        }
    }

    public static class PowderAnti92Item extends Item implements Chemistry{
        public PowderAnti92Item(Properties p_41383_) {
            super(p_41383_);
        }

        @Override
        public boolean onEntityItemUpdate(ItemStack stack, ItemEntity entity) {
            return on(stack, entity);
        }
    }

    public static class PowderAnti93Item extends Item implements Chemistry{
        public PowderAnti93Item(Properties p_41383_) {
            super(p_41383_);
        }

        @Override
        public boolean onEntityItemUpdate(ItemStack stack, ItemEntity entity) {
            return on(stack, entity);
        }
    }

    public static class PowderAnti94Item extends Item implements Chemistry{
        public PowderAnti94Item(Properties p_41383_) {
            super(p_41383_);
        }

        @Override
        public boolean onEntityItemUpdate(ItemStack stack, ItemEntity entity) {
            return on(stack, entity);
        }
    }

    public static class PowderAnti95Item extends Item implements Chemistry{
        public PowderAnti95Item(Properties p_41383_) {
            super(p_41383_);
        }

        @Override
        public boolean onEntityItemUpdate(ItemStack stack, ItemEntity entity) {
            return on(stack, entity);
        }
    }

    public static class PowderAnti96Item extends Item implements Chemistry{
        public PowderAnti96Item(Properties p_41383_) {
            super(p_41383_);
        }

        @Override
        public boolean onEntityItemUpdate(ItemStack stack, ItemEntity entity) {
            return on(stack, entity);
        }
    }

    public static class PowderAnti97Item extends Item implements Chemistry{
        public PowderAnti97Item(Properties p_41383_) {
            super(p_41383_);
        }

        @Override
        public boolean onEntityItemUpdate(ItemStack stack, ItemEntity entity) {
            return on(stack, entity);
        }
    }

    public static class PowderAnti98Item extends Item implements Chemistry{
        public PowderAnti98Item(Properties p_41383_) {
            super(p_41383_);
        }

        @Override
        public boolean onEntityItemUpdate(ItemStack stack, ItemEntity entity) {
            return on(stack, entity);
        }
    }

}
