package com.pha.trainees.item;

import com.pha.trainees.way.chemistry.ReactionSystem;
import net.minecraft.client.gui.screens.Screen;
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

    public static class ChemistryBookItem extends BookItem {


        public ChemistryBookItem(Properties properties) {
            super(properties);
        }

        @Override
        public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag flag) {
            super.appendHoverText(stack, level, tooltipComponents, flag);

            if (flag.isAdvanced()) {
                tooltipComponents.add(Component.translatable("tooltip.trainees.che_book.title"));
                tooltipComponents.add(Component.translatable("tooltip.trainees.che_book.pre"));
                tooltipComponents.add(Component.translatable("tooltip.trainees.che_book.content"));
            } else {
                tooltipComponents.add(Component.translatable("tooltip.trainees.item.press_shift"));
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
    public static class Ji2O extends Item {
        public Ji2O(Properties p_41383_) {super(p_41383_);}

        @Override
        public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag flag) {
            super.appendHoverText(stack, level, tooltipComponents, flag);

            if (flag.isAdvanced()) {
                tooltipComponents.add(Component.translatable("tooltip.trainees.ji2o"));
                tooltipComponents.add(Component.translatable("tooltip.trainees.ji2o.2"));
            } else {
                tooltipComponents.add(Component.translatable("tooltip.trainees.item.press_shift"));
            }
        }
    }

    // 过氧化鸡
    public static class Ji2O2 extends Item {
        public Ji2O2(Properties p_41383_) {super(p_41383_);}

        @Override
        public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag flag) {
            super.appendHoverText(stack, level, tooltipComponents, flag);

            if (flag.isAdvanced()) {
                tooltipComponents.add(Component.translatable("tooltip.trainees.ji2o2"));
                tooltipComponents.add(Component.translatable("tooltip.trainees.ji2o2.2"));
            } else {
                tooltipComponents.add(Component.translatable("tooltip.trainees.item.press_shift"));
            }
        }
    }

    // 二氧化黑
    public static class BpO2 extends Item {
        public BpO2(Properties p_41383_) {super(p_41383_);}

        @Override
        public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag flag) {
            super.appendHoverText(stack, level, tooltipComponents, flag);

            if (flag.isAdvanced()) {
                tooltipComponents.add(Component.translatable("tooltip.trainees.bpo2"));
                tooltipComponents.add(Component.translatable("tooltip.trainees.bpo2.2"));
            } else {
                tooltipComponents.add(Component.translatable("tooltip.trainees.item.press_shift"));
            }
        }
    }

    // 三氧化黑
    public static class BpO3 extends Item {
        public BpO3(Properties p_41383_) {super(p_41383_);}

        @Override
        public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag flag) {
            super.appendHoverText(stack, level, tooltipComponents, flag);

            if (flag.isAdvanced()) {
                tooltipComponents.add(Component.translatable("tooltip.trainees.bpo3"));
                tooltipComponents.add(Component.translatable("tooltip.trainees.bpo3.2"));
            } else {
                tooltipComponents.add(Component.translatable("tooltip.trainees.item.press_shift"));
            }
        }
    }




///     酸

    // 黑化氢
    public static class HBp extends Item {
        public HBp(Properties p_41383_) {super(p_41383_);}

        @Override
        public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag flag) {
            super.appendHoverText(stack, level, tooltipComponents, flag);

            if (flag.isAdvanced()) {
                tooltipComponents.add(Component.translatable("tooltip.trainees.hbp"));
                tooltipComponents.add(Component.translatable("tooltip.trainees.hbp.2"));
            } else {
                tooltipComponents.add(Component.translatable("tooltip.trainees.item.press_shift"));
            }
        }
    }

    // 次黑酸
    public static class HBpO extends Item {
        public HBpO(Properties p_41383_) {super(p_41383_);}

        public static boolean on(ItemStack stack, ItemEntity entity) {
            if (!entity.level().isClientSide) {
                return ReactionSystem.ReactionRegistry.triggerReactions(stack, entity);
            }
            return false;
        }

        @Override
        public boolean onEntityItemUpdate(ItemStack stack, ItemEntity entity) {
            return on(stack, entity);
        }

        @Override
        public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag flag) {
            super.appendHoverText(stack, level, tooltipComponents, flag);

            if (flag.isAdvanced()) {
                tooltipComponents.add(Component.translatable("tooltip.trainees.hbpo"));
                tooltipComponents.add(Component.translatable("tooltip.trainees.hbpo.2"));
            } else {
                tooltipComponents.add(Component.translatable("tooltip.trainees.item.press_shift"));
            }
        }
    }

    // 黑酸
    public static class HBpO3 extends Item {
        public HBpO3(Properties p_41383_) {super(p_41383_);}

        @Override
        public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag flag) {
            super.appendHoverText(stack, level, tooltipComponents, flag);

            if (flag.isAdvanced()) {
                tooltipComponents.add(Component.translatable("tooltip.trainees.hbpo3"));
                tooltipComponents.add(Component.translatable("tooltip.trainees.hbpo3.2"));
            } else {
                tooltipComponents.add(Component.translatable("tooltip.trainees.item.press_shift"));
            }
        }
    }

    // 高黑酸
    public static class HBpO4 extends Item {
        public HBpO4(Properties p_41383_) {super(p_41383_);}

        @Override
        public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag flag) {
            super.appendHoverText(stack, level, tooltipComponents, flag);

            if (flag.isAdvanced()) {
                tooltipComponents.add(Component.translatable("tooltip.trainees.hbpo4"));
                tooltipComponents.add(Component.translatable("tooltip.trainees.hbpo4.2"));
            } else {
                tooltipComponents.add(Component.translatable("tooltip.trainees.item.press_shift"));
            }
        }
    }

    /// 碱

    // 氢氧化鸡
    public static class JiOH extends Item {
        public JiOH(Properties p_41383_) {super(p_41383_);}

        @Override
        public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag flag) {
            super.appendHoverText(stack, level, tooltipComponents, flag);

            if (flag.isAdvanced()) {
                tooltipComponents.add(Component.translatable("tooltip.trainees.jioh"));
                tooltipComponents.add(Component.translatable("tooltip.trainees.jioh.2"));
            } else {
                tooltipComponents.add(Component.translatable("tooltip.trainees.item.press_shift"));
            }
        }
    }


    /// 盐
    // 黑化鸡
    public static class JiBp extends Item {
        public JiBp(Properties p_41383_) {super(p_41383_);}

        @Override
        public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag flag) {
            super.appendHoverText(stack, level, tooltipComponents, flag);

            if (flag.isAdvanced()) {
                tooltipComponents.add(Component.translatable("tooltip.trainees.jibp"));
                tooltipComponents.add(Component.translatable("tooltip.trainees.jibp.2"));
            } else {
                tooltipComponents.add(Component.translatable("tooltip.trainees.item.press_shift"));
            }
        }
    }

    // 次黑酸鸡
    public static class JiBpO extends Item {
        public JiBpO(Properties p_41383_) {super(p_41383_);}

        @Override
        public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag flag) {
            super.appendHoverText(stack, level, tooltipComponents, flag);

            if (flag.isAdvanced()) {
                tooltipComponents.add(Component.translatable("tooltip.trainees.jibpo"));
                tooltipComponents.add(Component.translatable("tooltip.trainees.jibpo.2"));
            } else {
                tooltipComponents.add(Component.translatable("tooltip.trainees.item.press_shift"));
            }
        }
    }

    // 黑酸鸡
    public static class JiBpO3 extends Item {
        public JiBpO3(Properties p_41383_) {super(p_41383_);}

        @Override
        public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag flag) {
            super.appendHoverText(stack, level, tooltipComponents, flag);

            if (flag.isAdvanced()) {
                tooltipComponents.add(Component.translatable("tooltip.trainees.jibpo3"));
                tooltipComponents.add(Component.translatable("tooltip.trainees.jibpo3.2"));
            } else {
                tooltipComponents.add(Component.translatable("tooltip.trainees.item.press_shift"));
            }
        }
    }

    // 高黑酸鸡
    public static class JiBpO4 extends Item {
        public JiBpO4(Properties p_41383_) {super(p_41383_);}

        @Override
        public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag flag) {
            super.appendHoverText(stack, level, tooltipComponents, flag);

            if (flag.isAdvanced()) {
                tooltipComponents.add(Component.translatable("tooltip.trainees.jibpo4"));
                tooltipComponents.add(Component.translatable("tooltip.trainees.jibpo4.2"));
            } else {
                tooltipComponents.add(Component.translatable("tooltip.trainees.item.press_shift"));
            }
        }
    }




}
