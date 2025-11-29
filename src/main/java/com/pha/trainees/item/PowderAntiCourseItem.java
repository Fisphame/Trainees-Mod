package com.pha.trainees.item;

import com.pha.trainees.way.chemistry.ChemicalReaction;
import com.pha.trainees.way.chemistry.ReactionSystem;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;

public class PowderAntiCourseItem {

    public static InteractionResult JiBpCombination_1(UseOnContext context, int number){
        if (ChemicalReaction.JiBpCombination_1(context, number)){
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.FAIL;
    }

    public static boolean on(ItemStack stack, ItemEntity entity) {
        if (!entity.level().isClientSide) {
            return ReactionSystem.ReactionRegistry.triggerReactions(stack, entity);
        }
        return false;
    }

    public static class PowderAntiItem extends Item{
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

    public static class PowderAnti4Item extends Item{
        public PowderAnti4Item(Properties p_41383_) {
            super(p_41383_);
        }

        @Override
        public boolean onEntityItemUpdate(ItemStack stack, ItemEntity entity) {
            return on(stack, entity);
        }
    }

    public static class PowderAnti9Item extends Item {
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

    public static class PowderAnti92Item extends Item {
        public PowderAnti92Item(Properties p_41383_) {
            super(p_41383_);
        }

        @Override
        public boolean onEntityItemUpdate(ItemStack stack, ItemEntity entity) {
            return on(stack, entity);
        }
    }

    public static class PowderAnti93Item extends Item {
        public PowderAnti93Item(Properties p_41383_) {
            super(p_41383_);
        }

        @Override
        public boolean onEntityItemUpdate(ItemStack stack, ItemEntity entity) {
            return on(stack, entity);
        }
    }

    public static class PowderAnti94Item extends Item {
        public PowderAnti94Item(Properties p_41383_) {
            super(p_41383_);
        }

        @Override
        public boolean onEntityItemUpdate(ItemStack stack, ItemEntity entity) {
            return on(stack, entity);
        }
    }

    public static class PowderAnti95Item extends Item {
        public PowderAnti95Item(Properties p_41383_) {
            super(p_41383_);
        }

        @Override
        public boolean onEntityItemUpdate(ItemStack stack, ItemEntity entity) {
            return on(stack, entity);
        }
    }

    public static class PowderAnti96Item extends Item {
        public PowderAnti96Item(Properties p_41383_) {
            super(p_41383_);
        }

        @Override
        public boolean onEntityItemUpdate(ItemStack stack, ItemEntity entity) {
            return on(stack, entity);
        }
    }

    public static class PowderAnti97Item extends Item {
        public PowderAnti97Item(Properties p_41383_) {
            super(p_41383_);
        }

        @Override
        public boolean onEntityItemUpdate(ItemStack stack, ItemEntity entity) {
            return on(stack, entity);
        }
    }

    public static class PowderAnti98Item extends Item {
        public PowderAnti98Item(Properties p_41383_) {
            super(p_41383_);
        }

        @Override
        public boolean onEntityItemUpdate(ItemStack stack, ItemEntity entity) {
            return on(stack, entity);
        }
    }

}
