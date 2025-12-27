package com.pha.trainees.item;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

public class AxeMaceItem extends AxeItem {
    public AxeMaceItem(Tier p_40521_, float p_40522_, float p_40523_, Properties p_40524_) {
        super(p_40521_, p_40522_, p_40523_, p_40524_);
    }
    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltipComponents, flag);

        boolean isShiftPressed = Screen.hasShiftDown();
        if (isShiftPressed) {
            tooltipComponents.add(Component.translatable("tooltip.trainees.axemace_item"));
            tooltipComponents.add(Component.translatable("tooltip.trainees.axemace_item.2"));
        } else {
            tooltipComponents.add(Component.translatable("tooltip.trainees.item.press_shift"));
        }
    }
}
