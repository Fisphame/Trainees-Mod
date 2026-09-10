package com.pha.trainees.compat.jei;

import com.pha.trainees.Main;
import com.pha.trainees.chemistry.material.FirstClassSubstances;
import com.pha.trainees.chemistry.particle.IonType;
import com.pha.trainees.chemistry.reaction.ReactionRule;
import com.pha.trainees.chemistry.util.ReactionFormula;
import com.pha.trainees.chemistry.util.ReactionInfo;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

/**
 * JEI「化学反应」分类（§19.16，百科式）：把 {@link ReactionRule} 以方程式 + 条件区呈现。
 *
 * <p>布局：上排 = 反应物槽位 → 产物槽位（每物种一个槽，计量数在方程式与 tooltip 里）；
 * 中间 = 粗体方程式（{@link ReactionFormula}）；下方 = 条件区（{@link ReactionInfo}）。
 * 槽位统一使用裸离子条目（{@link ChemicalIngredientTypes#ION}），因此任意物种都可 R/U 聚焦；
 * 物质物品层就绪后再对有物品的物种改放 ItemStack 槽位（§19.16 混用模型）。</p>
 */
@SuppressWarnings({"deprecation", "removal"})
public class ReactionRecipeCategory implements IRecipeCategory<ReactionRule> {

    public static final ResourceLocation UID = new ResourceLocation(Main.MODID, "chemical_reaction");
    public static final RecipeType<ReactionRule> TYPE = new RecipeType<>(UID, ReactionRule.class);

    private static final int WIDTH = 200;
    private static final int HEIGHT = 132;
    private static final int SLOT = 18;
    private static final int SLOT_GAP = 2;
    private static final int PITCH = SLOT + SLOT_GAP;   // 20
    private static final int SLOT_Y = 4;
    private static final int EQ_Y = 28;
    private static final int COND_Y = 44;
    private static final int LINE_H = 10;
    private static final int MAX_PER_SIDE = 6;

    private final IDrawable background;
    private final IDrawable icon;

    public ReactionRecipeCategory(IGuiHelper helper) {
        this.background = new PanelBackground(WIDTH, HEIGHT);
        this.icon = new IconDrawable();
    }

    @Override
    public @NotNull RecipeType<ReactionRule> getRecipeType() {
        return TYPE;
    }

    @Override
    public @NotNull Component getTitle() {
        return Component.translatable("jei.trainees.reaction");
    }

    @Override
    public @NotNull IDrawable getBackground() {
        return background;
    }

    @Override
    public @NotNull IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, ReactionRule rule, @NotNull IFocusGroup focuses) {
        int i = 0;
        for (Map.Entry<IonType, Integer> e : rule.getReactants().entrySet()) {
            if (i >= MAX_PER_SIDE) break;
            addSpeciesSlot(builder, RecipeIngredientRole.INPUT, 4 + i * PITCH, SLOT_Y, e);
            i++;
        }
        i = 0;
        for (Map.Entry<IonType, Integer> e : rule.getProducts().entrySet()) {
            if (i >= MAX_PER_SIDE) break;
            addSpeciesSlot(builder, RecipeIngredientRole.OUTPUT, WIDTH - 4 - SLOT - i * PITCH, SLOT_Y, e);
            i++;
        }
    }

    /**
     * 槽位呈现（§19.16 混用模型）：有真物品的一级物质用 {@code ItemStack} 槽位（R/U 同时命中物品配方与化学方程式），
     * 否则退回裸离子条目（裸离子/二级物质在物品层没有对应物）。
     */
    private void addSpeciesSlot(IRecipeLayoutBuilder builder, RecipeIngredientRole role,
                                int x, int y, Map.Entry<IonType, Integer> species) {
        var slot = builder.addSlot(role, x, y)
                .addRichTooltipCallback((view, tooltip) ->
                        tooltip.add(Component.translatable("jei.trainees.reaction.coefficient", species.getValue())));
        FirstClassSubstances.itemFor(species.getKey())
                .ifPresentOrElse(slot::addItemStack,
                        () -> slot.addIngredient(ChemicalIngredientTypes.ION, species.getKey()));
    }

    @Override
    public void draw(ReactionRule rule, @NotNull IRecipeSlotsView view,
                     @NotNull GuiGraphics guiGraphics, double mouseX, double mouseY) {
        Minecraft mc = Minecraft.getInstance();

        // 箭头（位于两侧槽位之间）
        int leftRight = 4 + Math.min(rule.getReactants().size(), MAX_PER_SIDE) * PITCH;
        int rightLeft = WIDTH - 4 - Math.min(rule.getProducts().size(), MAX_PER_SIDE) * PITCH;
        int arrowX = Math.max(leftRight + 2, (leftRight + rightLeft) / 2 - 4);
        guiGraphics.drawString(mc.font, "→", arrowX, SLOT_Y + 5, 0xFFFFFF, false);

        // 方程式（粗体）
        Component equation = Component.literal(ReactionFormula.format(rule))
                .withStyle(ChatFormatting.BOLD);
        guiGraphics.drawString(mc.font, equation, 4, EQ_Y, 0xE0E0E0, false);

        // 条件区
        List<Component> lines = ReactionInfo.conditionLines(rule);
        int y = COND_Y;
        for (Component line : lines) {
            if (y > HEIGHT - LINE_H) break;
            guiGraphics.drawString(mc.font, line, 4, y, 0xFFFFFF, false);
            y += LINE_H;
        }
    }

    // ==================== 背景与图标（代码绘制，无需贴图） ====================

    private static class PanelBackground implements IDrawable {
        private final int width;
        private final int height;

        PanelBackground(int width, int height) {
            this.width = width;
            this.height = height;
        }

        @Override
        public int getWidth() {
            return width;
        }

        @Override
        public int getHeight() {
            return height;
        }

        @Override
        public void draw(@NotNull GuiGraphics guiGraphics, int xOffset, int yOffset) {
            guiGraphics.fill(xOffset, yOffset, xOffset + width, yOffset + height, 0xFF101010);
            guiGraphics.fill(xOffset, yOffset, xOffset + width, yOffset + 1, 0xFF555555);
            guiGraphics.fill(xOffset, yOffset + height - 1, xOffset + width, yOffset + height, 0xFF555555);
            guiGraphics.fill(xOffset, yOffset, xOffset + 1, yOffset + height, 0xFF555555);
            guiGraphics.fill(xOffset + width - 1, yOffset, xOffset + width, yOffset + height, 0xFF555555);
        }
    }

    private static class IconDrawable implements IDrawable {
        @Override
        public int getWidth() {
            return 16;
        }

        @Override
        public int getHeight() {
            return 16;
        }

        @Override
        public void draw(@NotNull GuiGraphics guiGraphics, int xOffset, int yOffset) {
            guiGraphics.fill(xOffset, yOffset, xOffset + 16, yOffset + 16, 0xFF1B3A2A);
            guiGraphics.fill(xOffset + 1, yOffset + 1, xOffset + 15, yOffset + 15, 0xFF2E7D4F);
            guiGraphics.drawString(Minecraft.getInstance().font, "Rx",
                    xOffset + 3, yOffset + 4, 0xFFFFFF, false);
        }
    }
}
