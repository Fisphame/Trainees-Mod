package com.pha.trainees.compat.jei;

import com.pha.trainees.chemistry.particle.IonType;
import com.pha.trainees.chemistry.util.IonDisplay;
import com.mojang.blaze3d.vertex.PoseStack;
import mezz.jei.api.ingredients.IIngredientRenderer;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.TooltipFlag;

import java.util.ArrayList;
import java.util.List;

/**
 * 裸离子条目的 JEI 渲染器（§19.16）：在 16×16 空间里画"色块 + 化学式缩写"，
 * tooltip 给完整化学式与物理化学数据。
 *
 * <p>正式形态贴图（粉末/晶体/溶液…）落地后，可把色块替换为形态底图 + tint，接口不变。</p>
 */
public class IonIngredientRenderer implements IIngredientRenderer<IonType> {

    private static final int SIZE = 16;

    @Override
    public void render(GuiGraphics guiGraphics, IonType ion) {
        int color = ion.getDisplayColor();
        // 底 + 色块（后续可换成形态贴图 tint）
        guiGraphics.fill(0, 0, SIZE, SIZE, 0xFF101010);
        guiGraphics.fill(1, 1, SIZE - 1, SIZE - 1, color);

        // 化学式缩写（缩放 0.5 绘制，保证 16px 内可读）；亮色用黑字、暗色用白字
        String label = shortLabel(ion);
        int fg = isBright(color) ? 0xFF202020 : 0xFFFFFFFF;
        PoseStack pose = guiGraphics.pose();
        pose.pushPose();
        pose.scale(0.5f, 0.5f, 1.0f);
        guiGraphics.drawString(Minecraft.getInstance().font, label, 3, 6, fg, false);
        pose.popPose();
    }

    /** 取化学式前 3 个字符作为缩写（下标/上标字符也计 1 个） */
    private String shortLabel(IonType ion) {
        String full = IonDisplay.format(ion.getId().getPath());
        return full.length() <= 3 ? full : full.substring(0, 3);
    }

    /** 感知亮度判据（用于选择前景文字色） */
    private boolean isBright(int argb) {
        int r = (argb >> 16) & 0xFF;
        int g = (argb >> 8) & 0xFF;
        int b = argb & 0xFF;
        return (r * 299 + g * 587 + b * 114) / 1000 > 150;
    }

    /**
     * tooltip（JEI 也用它做搜索）。注意：JEI 15 里 {@code getTooltip(V, TooltipFlag)} 仍是抽象方法，
     * 新签名 {@code getTooltip(V, Player, TooltipFlag)} 默认委托到它。
     */
    @SuppressWarnings("deprecation")
    @Override
    public List<Component> getTooltip(IonType ion, TooltipFlag tooltipFlag) {
        List<Component> lines = new ArrayList<>();
        lines.add(Component.literal(IonDisplay.format(ion.getId().getPath())).withStyle(ChatFormatting.BOLD));
        lines.add(Component.translatable("jei.trainees.ion.id", ion.getId().toString())
                .withStyle(ChatFormatting.DARK_GRAY));
        lines.add(Component.translatable("jei.trainees.ion.form",
                Component.translatable(ion.getForm().getTranslationKey()).getString()));
        lines.add(Component.translatable("jei.trainees.ion.molar_mass", fmt(ion.getMolarMass(), 2)));
        lines.add(Component.translatable("jei.trainees.ion.formation",
                fmt(ion.getFormationEnthalpy(), 1), fmt(ion.getFormationGibbs(), 1)));
        if (ion.getToxicityLevel() > 0) {
            lines.add(Component.translatable("jei.trainees.ion.toxicity", ion.getToxicityLevel()));
        }
        if (!ion.getTags().isEmpty()) {
            lines.add(Component.translatable("jei.trainees.ion.tags", ion.getTags().toString()));
        }
        return lines;
    }

    private String fmt(double v, int decimals) {
        return String.format("%." + decimals + "f", v);
    }
}
