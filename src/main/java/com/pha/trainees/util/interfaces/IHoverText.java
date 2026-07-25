package com.pha.trainees.util.interfaces;

import com.pha.trainees.Main;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;

public interface IHoverText {
    /* 键格式：
            "tooltip.trainees.id"
            "tooltip.trainees.id.2"
            "tooltip.trainees.item.press_shift"
        */
    String Success = "--[ - ]--";
    String Success2 = "-- [-√-] --";
    String Fail = "--x--";
    String Fail2 = "-[-x-]-";
    String Fail3 = "--[-x-]--";
    String Cooldown = "···";
    String Up = "--[ ↑ ]--";
    String Down = "--[ ↓ ]--";
    String Lose = "--?--";
    String Warn = "--- ! ---";
    MutableComponent SuccessC = Component.literal(Success).withStyle(ChatFormatting.GREEN);
    MutableComponent Success2C = Component.literal(Success2).withStyle(ChatFormatting.GREEN);
    MutableComponent FailC = Component.literal(Fail).withStyle(ChatFormatting.RED);
    MutableComponent Fail2C = Component.literal(Fail2).withStyle(ChatFormatting.RED);
    MutableComponent Fail3C = Component.literal(Fail3).withStyle(ChatFormatting.RED);
    MutableComponent CooldownC = Component.literal(Cooldown);
    MutableComponent UpC = Component.literal(Up);
    MutableComponent DownC = Component.literal(Down);
    MutableComponent LoseC = Component.literal(Lose).withStyle(ChatFormatting.RED);
    MutableComponent WarnC = Component.literal(Warn);


    default void addHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents,
                              TooltipFlag flag, String id) {
        if (flag.isAdvanced()) {
            tooltipComponents.add(getTooltip(id));
            tooltipComponents.add(getTooltip(id, 2));
        } else {
            tooltipComponents.add(getDefault());
        }
    }
    default void addChemicalFormula(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents,
                                    TooltipFlag flag, String id) {
        if (flag.isAdvanced()) {
            tooltipComponents.add(getTooltip(id).withStyle(ChatFormatting.GRAY));
        } else {
            tooltipComponents.add(getDefault());
        }
    }
    default void addChemicalMolarMass(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents,
                                      TooltipFlag flag, double moss) {
        if (flag.isAdvanced()) {
            tooltipComponents.add(Component.literal(String.valueOf(moss)).withStyle(ChatFormatting.GRAY));
        } else {
            tooltipComponents.add(getDefault());
        }
    }

    default MutableComponent tr(String s) {
        return Component.translatable(s);
    }
    default String tr(String head, String tail, String... strings){
        return head + "." + Main.MODID + "." + String.join(".", strings) + "." + tail;
    }
    default String tr(String head, String tail){
        return head + Main.MODID + tail;
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
