package com.pha.trainees.item;

import com.pha.trainees.item.interfaces.Chemistry;
import com.pha.trainees.item.interfaces.HoverText;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.BookItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

public class ChemistryItem {

    public static class ChemistryBookItem extends BookItem implements HoverText {


        public ChemistryBookItem(Properties properties) {
            super(properties);
        }

        @Override
        public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag flag) {
            super.appendHoverText(stack, level, tooltipComponents, flag);

            if (flag.isAdvanced()) {
                String id = "che_book";
                tooltipComponents.add(getTooltip(id, "title"));
                tooltipComponents.add(getTooltip(id, "pre"));
                tooltipComponents.add(getTooltip(id, "context"));
            } else {
                addTip(tooltipComponents);
            }
        }
    }

    /*
        Ji   钅鸡（以下称鸡）  原子序数119   金属元素    +1
        Bp   石黑（以下称黑）  原子序数117   非金属元素   +1 -1 -3 -5 -7
        JiBp   黑化鸡
        JiOH   氢氧化鸡
        HBp    黑化氢   相酸：黑化氢的水溶液
        Ji2O   氧化鸡
        Ji2SO4 硫酸鸡
        JiNO3  硝酸鸡
        Ji2CO3 碳酸鸡
        BpNH4  铵黑

        反应：
            Bp2 + H2O == HBpO + HBp
            Bp2 + 2O2 =点燃= 2BpO2
            2HBpO =光照= 2HBp + O2↑
            2BpO2 + 2H2O == 2HBpO3 + H2↑
            2BpO2 + O2 =催化剂= 2BpO3
            2BpO3 + 2H2O == 2HBpO4 + H2↑



        1.  Ji + Bp == JiBp
        2.  2Ji + 2H2O == 2JiOH + H2↑

        4.
        4.  JiOH + HBp == JiBp + H2O
        5.  2Ji + O2 =点燃= Ji2O
        6.  Ji2SO4 + 2Na == Na2SO4 + Ji2


    */


    /// 氧化物
    // 氧化鸡
    public static class Ji2O extends Item implements Chemistry, HoverText {
        public Ji2O(Properties p_41383_) {super(p_41383_);}

        @Override
        public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag flag) {
            super.appendHoverText(stack, level, tooltipComponents, flag);
            addHoverText(stack, level, tooltipComponents, flag, "ji2o");
        }
    }

    // 过氧化鸡
    public static class Ji2O2 extends Item implements Chemistry, HoverText{
        public Ji2O2(Properties p_41383_) {super(p_41383_);}

        @Override
        public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag flag) {
            super.appendHoverText(stack, level, tooltipComponents, flag);
            addHoverText(stack, level, tooltipComponents, flag, "ji2o2");
        }
    }

    // 二氧化黑
    public static class BpO2 extends Item implements Chemistry, HoverText{
        public BpO2(Properties p_41383_) {super(p_41383_);}

        @Override
        public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag flag) {
            super.appendHoverText(stack, level, tooltipComponents, flag);
            addHoverText(stack, level, tooltipComponents, flag, "bpo2");
        }
    }

    // 三氧化黑
    public static class BpO3 extends Item implements Chemistry, HoverText{
        public BpO3(Properties p_41383_) {super(p_41383_);}

        @Override
        public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag flag) {
            super.appendHoverText(stack, level, tooltipComponents, flag);
            addHoverText(stack, level, tooltipComponents, flag, "bpo3");
        }
    }


///     酸

    // 黑化氢
    public static class HBp extends Item implements Chemistry, HoverText{
        public HBp(Properties p_41383_) {super(p_41383_);}

        @Override
        public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag flag) {
            super.appendHoverText(stack, level, tooltipComponents, flag);
            addHoverText(stack, level, tooltipComponents, flag, "hbp");
        }
    }

    // 次黑酸
    public static class HBpO extends Item implements Chemistry, HoverText{
        public HBpO(Properties p_41383_) {super(p_41383_);}

        @Override
        public boolean onEntityItemUpdate(ItemStack stack, ItemEntity entity) {
            return on(stack, entity);
        }

        @Override
        public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag flag) {
            super.appendHoverText(stack, level, tooltipComponents, flag);
            addHoverText(stack, level, tooltipComponents, flag, "hbpo");
        }
    }

    // 黑酸
    public static class HBpO3 extends Item implements Chemistry, HoverText{
        public HBpO3(Properties p_41383_) {super(p_41383_);}

        @Override
        public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag flag) {
            super.appendHoverText(stack, level, tooltipComponents, flag);
            addHoverText(stack, level, tooltipComponents, flag, "hbpo3");
        }
    }

    // 高黑酸
    public static class HBpO4 extends Item implements Chemistry, HoverText{
        public HBpO4(Properties p_41383_) {super(p_41383_);}

        @Override
        public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag flag) {
            super.appendHoverText(stack, level, tooltipComponents, flag);
            addHoverText(stack, level, tooltipComponents, flag, "hbpo4");
        }
    }


    /// 碱

    // 氢氧化鸡
    public static class JiOH extends Item implements Chemistry, HoverText{
        public JiOH(Properties p_41383_) {super(p_41383_);}

        @Override
        public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag flag) {
            super.appendHoverText(stack, level, tooltipComponents, flag);
            addHoverText(stack, level, tooltipComponents, flag, "jioh");
        }
    }


    /// 盐
    // 黑化鸡
    public static class JiBp extends Item implements Chemistry, HoverText{
        public JiBp(Properties p_41383_) {super(p_41383_);}

        @Override
        public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag flag) {
            super.appendHoverText(stack, level, tooltipComponents, flag);
            addHoverText(stack, level, tooltipComponents, flag, "jibp");
        }
    }

    // 次黑酸鸡
    public static class JiBpO extends Item implements Chemistry, HoverText{
        public JiBpO(Properties p_41383_) {super(p_41383_);}

        @Override
        public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag flag) {
            super.appendHoverText(stack, level, tooltipComponents, flag);
            addHoverText(stack, level, tooltipComponents, flag, "jibpo");
        }
    }

    // 黑酸鸡
    public static class JiBpO3 extends Item implements Chemistry, HoverText{
        public JiBpO3(Properties p_41383_) {super(p_41383_);}

        @Override
        public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag flag) {
            super.appendHoverText(stack, level, tooltipComponents, flag);
            addHoverText(stack, level, tooltipComponents, flag, "jibpo3");
        }
    }

    // 高黑酸鸡
    public static class JiBpO4 extends Item implements Chemistry, HoverText {
        public JiBpO4(Properties p_41383_) {
            super(p_41383_);
        }

        @Override
        public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag flag) {
            super.appendHoverText(stack, level, tooltipComponents, flag);
            addHoverText(stack, level, tooltipComponents, flag, "jibpo4");
        }
    }
}


