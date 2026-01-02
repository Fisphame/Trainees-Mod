package com.pha.trainees.item;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

public class LongCourseItem {
    public static class LongItem extends Item {
        public LongItem(Properties p_41383_) {
            super(p_41383_);
        }
    }

    public static class RealPickaxeItem extends PickaxeItem {
        public RealPickaxeItem(Tier p_42961_, int p_42962_, float p_42963_, Properties p_42964_) {
            super(p_42961_, p_42962_, p_42963_, p_42964_);
        }

        @Override
        public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag flag) {
            super.appendHoverText(stack, level, tooltipComponents, flag);

            if (flag.isAdvanced()) {
                tooltipComponents.add(Component.translatable("tooltip.trainees.real_pickaxe_item"));
                tooltipComponents.add(Component.translatable("tooltip.trainees.real_pickaxe_item.2"));
            } else {
                tooltipComponents.add(Component.translatable("tooltip.trainees.item.press_shift"));
            }
        }
    }
}
