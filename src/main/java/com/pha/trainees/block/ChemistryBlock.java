package com.pha.trainees.block;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

import javax.annotation.Nullable;
import java.util.List;

public class ChemistryBlock {
    public static class JiOHBlock extends Block {
        public JiOHBlock(Properties p_49795_) {super(p_49795_);}


        public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag flag) {
            super.appendHoverText(stack, level, tooltipComponents, flag);

            tooltipComponents.add(Component.translatable("tooltip.trainees.jioh"));
            tooltipComponents.add(Component.translatable("tooltip.trainees.jioh.2"));
        }
    }
}
