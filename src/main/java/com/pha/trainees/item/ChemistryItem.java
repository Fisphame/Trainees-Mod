package com.pha.trainees.item;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

public class ChemistryItem {
    /*
        Ji   钅鸡（以下称鸡）  原子序数119   金属元素    +1               易失1电子 成Ji-  （e+）
            -> 只因粒 两锭半 两锭半块
        Bp   石黑（以下称黑）  原子序数117   !金属元素   +1 -1 -3 -5 -7   易得1电子 成Bp+
            -> 黑粉及若干压缩
        JiBp   黑鸡（反相篮球素/黑化鸡/黑鸡盐）
            -> 反相篮球素 反相篮球
        JiOH   鸡碱（氢氧化鸡）
        HBp    黑酸
        Ji2O   一氧化二鸡
        Ji2SO4 硫酸鸡
        JiNO3  硝酸鸡
        Ji2CO3 碳酸鸡
        BpNH4  铵黑

        反应：
        1.  Ji + Bp == JiBp
        2.  2Ji + 2H2O == 2JiOH + H2↑
        3.  4Bp + 2H2O == 4HBp + O2↑
        4.  JiOH + HBp == JiBp + H2O
        5.  2Ji + O2 =点燃= Ji2O
        6.  Ji2SO4 + 2Na == Na2SO4 + Ji2

        2体现为将

    */
    //鸡碱
    public static class JiOH extends Item {
        public JiOH(Properties p_41383_) {super(p_41383_);}

        @Override
        public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag flag) {
            super.appendHoverText(stack, level, tooltipComponents, flag);

            tooltipComponents.add(Component.translatable("tooltip.trainees.jioh"));
            tooltipComponents.add(Component.translatable("tooltip.trainees.jioh.2"));
        }
    }

    //黑酸
    public static class HBp extends Item {
        public HBp(Properties p_41383_) {super(p_41383_);}

        @Override
        public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag flag) {
            super.appendHoverText(stack, level, tooltipComponents, flag);

            tooltipComponents.add(Component.translatable("tooltip.trainees.hpb"));
            tooltipComponents.add(Component.translatable("tooltip.trainees.hpb.2"));
        }
    }

    //黑鸡
    public static class JiBp extends Item {
        public JiBp(Properties p_41383_) {super(p_41383_);}

        @Override
        public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag flag) {
            super.appendHoverText(stack, level, tooltipComponents, flag);

            tooltipComponents.add(Component.translatable("tooltip.trainees.jibp"));
            tooltipComponents.add(Component.translatable("tooltip.trainees.jibp.2"));
        }
    }

    //一氧化二鸡
    public static class Ji2O extends Item {
        public Ji2O(Properties p_41383_) {super(p_41383_);}

        @Override
        public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag flag) {
            super.appendHoverText(stack, level, tooltipComponents, flag);

            tooltipComponents.add(Component.translatable("tooltip.trainees.ji2o"));
            tooltipComponents.add(Component.translatable("tooltip.trainees.ji2o.2"));
        }
    }
}
