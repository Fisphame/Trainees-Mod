package com.pha.trainees.item;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

public class ChemistryBookItem extends BookItem{
    
    private final String bookTitle;
    private final String bookContent;
    
    public ChemistryBookItem(Properties properties, String title, String content) {
        super(properties);
        this.bookTitle = title;
        this.bookContent = content;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level,
                                List<Component> tooltipComponents, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltipComponents, flag);

        // 添加本地化的工具提示
        tooltipComponents.add(Component.translatable("tooltip.trainees.che_book.title"));
        tooltipComponents.add(Component.translatable("tooltip.trainees.che_book.pre"));
        tooltipComponents.add(Component.translatable("tooltip.trainees.che_book.content"));
    }
    
    public String getBookTitle() {
        return bookTitle;
    }
    
    public String getBookContent() {
        return bookContent;
    }
}