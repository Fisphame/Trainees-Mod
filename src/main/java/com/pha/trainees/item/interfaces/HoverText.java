package com.pha.trainees.item.interfaces;

import com.pha.trainees.Main;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.text.MessageFormat;
import java.util.List;

public interface HoverText {
    /* 键格式：
            "tooltip.trainees.id"
            "tooltip.trainees.id.2"
            "tooltip.trainees.item.press_shift"
        */
    default void addHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag flag,
                              String id) {
        if (flag.isAdvanced()) {
            tooltipComponents.add(getTooltip(id));
            tooltipComponents.add(getTooltip(id, 2));
        } else {
            tooltipComponents.add(getDefault());
        }
    }

    default MutableComponent tr(String s) {
        return Component.translatable(s);
    }
    default String ky(String id) {
        return MessageFormat.format("tooltip.{0}.{1}", Main.MODID, id);
    }
    default String ky(String id, int index) {
        return MessageFormat.format("tooltip.{0}.{1}.{2}", Main.MODID, id, index);
    }
    default String ky(String id, int index1, int index2) {
        return MessageFormat.format("tooltip.{0}.{1}.{2}.{3}", Main.MODID, id, index1, index2);
    }
    default String ky(String id, String dex) {
        return MessageFormat.format("tooltip.{0}.{1}.{2}", Main.MODID, id, dex);
    }
    default String indexUp(String s, int index) {
        return MessageFormat.format("{0}.{1}", s, index);
    }
    default MutableComponent getTooltip(String id) {
        return tr(ky(id));
    }
    default MutableComponent getTooltip(String id, int index) {
        return tr(ky(id, index));
    }
    default MutableComponent getTooltip(String id, int index1, int index2) {
        return tr(ky(id, index1, index2));
    }
    default MutableComponent getTooltip(String id, String dex) {
        return tr(ky(id, dex));
    }
    default MutableComponent getDefault(){
        return Component.translatable(MessageFormat.format("tooltip.{0}.item.press_shift", Main.MODID));
    }
    default void addTip(List<Component> tooltipComponents, MutableComponent tip) {
        tooltipComponents.add(tip);
    }
    default void addTip(List<Component> tooltipComponents) {
        tooltipComponents.add(getDefault());
    }
    default void print(String s) {
        System.out.println(s);
    }

}
