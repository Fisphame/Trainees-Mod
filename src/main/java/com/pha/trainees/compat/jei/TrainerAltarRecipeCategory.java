package com.pha.trainees.compat.jei;

import com.pha.trainees.Main;
import com.pha.trainees.recipe.TrainerAltarRecipe;
import com.pha.trainees.registry.ModBlocks;
import com.pha.trainees.util.interfaces.HoverText;
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

public class TrainerAltarRecipeCategory implements IRecipeCategory<TrainerAltarRecipe>, HoverText {
    public static final ResourceLocation UID = new ResourceLocation(Main.MODID, "trainer_altar");
    public static final RecipeType<TrainerAltarRecipe> TYPE = new RecipeType<>(UID, TrainerAltarRecipe.class);

    // 背景图片路径：assets/trainees/textures/gui/jei_trainer_altar.png
    private static final ResourceLocation TEXTURE =
            new ResourceLocation(Main.MODID, "textures/gui/jei_trainer_altar.png");
    private final IDrawable background;
    private final IDrawable icon;

    public TrainerAltarRecipeCategory(IGuiHelper helper) {
        // 背景图尺寸为 116x54，从左上角(0,0)开始绘制整个图片
        this.background = helper.createDrawable(TEXTURE, 0, 0, 116, 54);
        // 图标使用多方块核心方块
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
    public void setRecipe(IRecipeLayoutBuilder builder, TrainerAltarRecipe recipe, @NotNull IFocusGroup focuses) {
        // 输入槽：四个田字形槽位，左上角坐标依次为 (10,7), (30,7), (10,27), (30,27)
        // 槽位宽高均为20像素，但JEI的槽位渲染是自动的，只需指定左上角坐标即可
        builder.addSlot(RecipeIngredientRole.INPUT, 10, 7)
                .addIngredients(recipe.getInput1());

        builder.addSlot(RecipeIngredientRole.INPUT, 30, 7)
                .addIngredients(recipe.getInput2());

        builder.addSlot(RecipeIngredientRole.INPUT, 10, 27)
                .addIngredients(recipe.getInput3());

        builder.addSlot(RecipeIngredientRole.INPUT, 30, 27)
                .addIngredients(recipe.getInput4());

        // 输出槽：左上角坐标 (86,17)，宽20像素
        builder.addSlot(RecipeIngredientRole.OUTPUT, 86, 17)
                .addItemStack(recipe.getResultItem());
    }
}