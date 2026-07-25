package com.pha.trainees.compat.jei;

import com.pha.trainees.Main;
import com.pha.trainees.recipe.TrainerAltarRecipe;
import com.pha.trainees.registry.ModBlocks;
import com.pha.trainees.util.interfaces.IHoverText;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings({"deprecation", "removal"})
public class TrainerAltarRecipeCategory implements IRecipeCategory<TrainerAltarRecipe>, IHoverText {
    public static final ResourceLocation UID = new ResourceLocation(Main.MODID, "trainer_altar");
    public static final RecipeType<TrainerAltarRecipe> TYPE = new RecipeType<>(UID, TrainerAltarRecipe.class);

    // ===== 可配置参数 =====
    private static final int BG_WIDTH = 136;          // 背景图宽度（像素）
    private static final int BG_HEIGHT = 68;         // 背景图高度（像素）
    private static final int GAP = 6;                // 槽位边缘之间的绝对间隔（像素）
    private static final int SLOT_SIZE = 20;           // 物品槽边长（固定20像素）
    // =====================

    // 背景中心坐标（整数除法取整，中心允许偏移0.5像素，不影响槽位放置）
    private static final int CENTER_X = BG_WIDTH / 2;
    private static final int CENTER_Y = BG_HEIGHT / 2;

    // 输出槽左上角坐标（槽中心与背景中心重合）
    private static final int OUTPUT_X = CENTER_X - SLOT_SIZE / 2;
    private static final int OUTPUT_Y = CENTER_Y - SLOT_SIZE / 2;

    // 四个输入槽左上角坐标（顺时针：左、上、右、下）
    private static final int LEFT_X   = OUTPUT_X - SLOT_SIZE - GAP;
    private static final int LEFT_Y   = OUTPUT_Y;
    private static final int TOP_X    = OUTPUT_X;
    private static final int TOP_Y    = OUTPUT_Y - SLOT_SIZE - GAP;
    private static final int RIGHT_X  = OUTPUT_X + SLOT_SIZE + GAP;
    private static final int RIGHT_Y  = OUTPUT_Y;
    private static final int BOTTOM_X = OUTPUT_X;
    private static final int BOTTOM_Y = OUTPUT_Y + SLOT_SIZE + GAP;

    private final IDrawable background;
    private final IDrawable icon;

    public TrainerAltarRecipeCategory(IGuiHelper helper) {
        // 背景图路径不变，尺寸需与 BG_WIDTH, BG_HEIGHT 一致
        // 背景图片路径：assets/trainees/textures/gui/jei_trainer_altar.png
        ResourceLocation texture = new ResourceLocation(Main.MODID, "textures/gui/jei_trainer_altar.png");
        this.background = helper.createDrawable(texture, 0, 0, BG_WIDTH, BG_HEIGHT);
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModBlocks.ALTAR_CORE_BLOCK.get()));
    }

    @Override
    public @NotNull RecipeType<TrainerAltarRecipe> getRecipeType() {
        return TYPE;
    }

    @Override
    public @NotNull Component getTitle() {
        return tr("jei.trainees.trainer_altar");
    }

    @Override
    public IDrawable getBackground() {
        return background;
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, TrainerAltarRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, LEFT_X, LEFT_Y)
                .addIngredients(recipe.getInput1());
        builder.addSlot(RecipeIngredientRole.INPUT, TOP_X, TOP_Y)
                .addIngredients(recipe.getInput2());
        builder.addSlot(RecipeIngredientRole.INPUT, RIGHT_X, RIGHT_Y)
                .addIngredients(recipe.getInput3());
        builder.addSlot(RecipeIngredientRole.INPUT, BOTTOM_X, BOTTOM_Y)
                .addIngredients(recipe.getInput4());

        // 输出槽（中心）
        builder.addSlot(RecipeIngredientRole.OUTPUT, OUTPUT_X, OUTPUT_Y)
                .addItemStack(recipe.getResultItem());
    }
}