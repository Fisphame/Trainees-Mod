package com.pha.trainees.compat.jei;

import com.pha.trainees.Main;
import com.pha.trainees.chemistry.particle.IonType;
import com.pha.trainees.chemistry.util.IonDisplay;
import com.pha.trainees.recipe.PackerRecipe;
import com.pha.trainees.registry.ModChemistry;
import mezz.jei.api.constants.VanillaTypes;
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
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

/**
 * JEI「打包机规格」分类（§19.18.6）：把 {@link PackerRecipe}（规格的展示投影）画成"规格 + 产出"。
 *
 * <p>布局：左侧 = 规格文本（有效成分窗口 / 各项门禁 / 优质档 / 每瓶取量）；
 * 右侧 = 产出槽（合格品、优质品）；底部 = 一句提示（展示用配方，不参与合成；不合格不会被自动灌装）。
 * 背景与图标**全部代码绘制**，不新增任何贴图（与 {@link ReactionRecipeCategory} 同款做法）。</p>
 */
@SuppressWarnings({"deprecation", "removal"})
public class PackerRecipeCategory implements IRecipeCategory<PackerRecipe> {

    public static final ResourceLocation UID = new ResourceLocation(Main.MODID, "packer");
    public static final RecipeType<PackerRecipe> TYPE = new RecipeType<>(UID, PackerRecipe.class);

    private static final int WIDTH = 190;
    private static final int HEIGHT = 118;
    private static final int PAD = 4;
    private static final int LINE_H = 10;
    private static final int SLOT = 18;
    /** 右侧产出槽的 x（两个槽竖排） */
    private static final int OUT_X = WIDTH - SLOT - PAD;
    private static final int OUT_Y1 = 6;
    private static final int OUT_Y2 = OUT_Y1 + SLOT + 4;

    private final IDrawable background;
    private final IDrawable icon;

    public PackerRecipeCategory(IGuiHelper helper) {
        this.background = new PanelBackground(WIDTH, HEIGHT);
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK,
                new ItemStack(ModChemistry.ModChemistryBlockItems.PACKER.get()));
    }

    @Override
    public @NotNull RecipeType<PackerRecipe> getRecipeType() {
        return TYPE;
    }

    @Override
    public @NotNull Component getTitle() {
        return Component.translatable("jei.trainees.packer");
    }

    @Override
    public @NotNull IDrawable getBackground() {
        return background;
    }

    @Override
    public @NotNull IDrawable getIcon() {
        return icon;
    }

    /** 只放产出槽：原料是"容器里的一份分布"，没有 item 原料可放（§19.25）。 */
    @Override
    public void setRecipe(@NotNull IRecipeLayoutBuilder builder, @NotNull PackerRecipe recipe,
                          @NotNull IFocusGroup focuses) {
        ItemStack good = resolve(recipe.getGoodItemId());
        if (!good.isEmpty()) {
            builder.addSlot(RecipeIngredientRole.OUTPUT, OUT_X, OUT_Y1).addItemStack(good);
        }
        ItemStack premium = resolve(recipe.getPremiumItemId());
        if (!premium.isEmpty()) {
            builder.addSlot(RecipeIngredientRole.OUTPUT, OUT_X, OUT_Y2).addItemStack(premium);
        }
    }

    @Override
    public void draw(@NotNull PackerRecipe recipe, @NotNull IRecipeSlotsView view,
                     @NotNull GuiGraphics guiGraphics, double mouseX, double mouseY) {
        Minecraft mc = Minecraft.getInstance();

        // 左侧标题：规格
        guiGraphics.drawString(mc.font,
                Component.translatable("jei.trainees.packer.header.spec").withStyle(ChatFormatting.BOLD),
                PAD, PAD, 0xFFE0E0E0, false);

        int y = PAD + LINE_H + 2;
        // 有效成分窗口（required）
        for (PackerRecipe.SpecRule rule : recipe.getRequired()) {
            guiGraphics.drawString(mc.font, Component.translatable("jei.trainees.packer.window",
                    speciesName(rule.speciesId()), pct(rule.minFraction()), pct(rule.maxFraction())),
                    PAD, y, 0xFFFFFFFF, false);
            y += LINE_H;
        }
        // 门禁：上限 ≤ 0 → "禁用"
        for (PackerRecipe.SpecRule rule : recipe.getLimits()) {
            Component line = rule.isForbidden()
                    ? Component.translatable("jei.trainees.packer.forbidden", speciesName(rule.speciesId()))
                    : Component.translatable("jei.trainees.packer.limit", speciesName(rule.speciesId()),
                            pct(rule.maxFraction()));
            guiGraphics.drawString(mc.font, line, PAD, y, rule.isForbidden() ? 0xFFFF8080 : 0xFFFFD080, false);
            y += LINE_H;
        }
        // 优质档（更窄子区间）
        for (PackerRecipe.SpecRule rule : recipe.getPremium()) {
            guiGraphics.drawString(mc.font, Component.translatable("jei.trainees.packer.premium.window",
                    speciesName(rule.speciesId()), pct(rule.minFraction()), pct(rule.maxFraction())),
                    PAD, y, 0xFF9FD8FF, false);
            y += LINE_H;
        }
        // 每瓶取量
        guiGraphics.drawString(mc.font, Component.translatable("jei.trainees.packer.bottle",
                trim(recipe.getBottleMoles())), PAD, y, 0xFFAAAAAA, false);

        // 右侧产出标签
        int labelX = OUT_X - 30;
        guiGraphics.drawString(mc.font, Component.translatable("jei.trainees.packer.out.good"),
                labelX, OUT_Y1 + 5, 0xFFFFFFFF, false);
        if (!resolve(recipe.getPremiumItemId()).isEmpty()) {
            guiGraphics.drawString(mc.font, Component.translatable("jei.trainees.packer.out.premium"),
                    labelX, OUT_Y2 + 5, 0xFFFFFFFF, false);
        }

        // 底部提示（一行）
        guiGraphics.drawString(mc.font,
                Component.translatable("jei.trainees.packer.source").withStyle(ChatFormatting.GRAY),
                PAD, HEIGHT - LINE_H - 1, 0xFFAAAAAA, false);
    }

    // ==================== 工具 ====================

    /** 物种显示名：真实离子走 {@link IonDisplay}；折算物种（如有效氯）走专用语言键，兜底显示原 id。 */
    private static Component speciesName(String speciesId) {
        ResourceLocation id = ResourceLocation.tryParse(speciesId);
        IonType ion = id == null ? null : ModChemistry.ModIons.getById(id);
        if (ion != null) {
            return Component.literal(IonDisplay.format(ion.getId().getPath()));
        }
        String path = speciesId;
        int colon = path.indexOf(':');
        if (colon >= 0) path = path.substring(colon + 1);
        return Component.translatable("jei.trainees.packer.species." + path);
    }

    private static ItemStack resolve(String itemId) {
        if (itemId == null || itemId.isEmpty()) return ItemStack.EMPTY;
        var item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(itemId));
        return item == null ? ItemStack.EMPTY : new ItemStack(item);
    }

    private static String pct(double fraction) {
        return String.format(Locale.ROOT, "%.2f%%", fraction * 100.0);
    }

    private static String trim(double value) {
        return String.format(Locale.ROOT, "%.2f", value);
    }

    // ==================== 背景（代码绘制，无需贴图） ====================
    // 与 ReactionRecipeCategory.PanelBackground 同款（该类在既有文件中，此处按其样式复制；
    // 如需去重可抽成公共类，本次为不动既有代码而保留副本）。

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
}
