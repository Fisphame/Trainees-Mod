package com.pha.trainees.item;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class WhatDoesItMeanItem {
    public static class FuckItem extends Item {
        public FuckItem(Properties p_41383_) {
            super(p_41383_);
        }

        @Override
        public @NotNull InteractionResult useOn(UseOnContext context){
            for (int i = 1; i <= getRandom(5,10); i++) {
                System.out.println(fuck(getRandom(10, 45)));
            }
            return InteractionResult.PASS;
        }

        public static int fuck(int fuck){
            if (fuck <= 2) return 1;
            return fuck(fuck-2) + fuck(fuck-1);
        }

        public static int getRandom(int min, int max) {
            return ThreadLocalRandom.current().nextInt(min, max + 1);
        }

        @Override
        public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag flag) {
            super.appendHoverText(stack, level, tooltipComponents, flag);

            if (flag.isAdvanced()) {
                tooltipComponents.add(Component.translatable("tooltip.trainees.fuck"));
                tooltipComponents.add(Component.translatable("tooltip.trainees.fuck2"));
            } else {
                tooltipComponents.add(Component.translatable("tooltip.trainees.item.press_shift"));
            }
        }
    }


}
