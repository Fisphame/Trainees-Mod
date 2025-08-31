package com.pha.trainees.recipe;

import com.google.gson.JsonObject;
import com.pha.trainees.Main;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;



public class PurificationRecipe implements Recipe<Container> {
    public static final String RECIPE_ID = "purification_upgrade";

    private final ResourceLocation id;
    private final Ingredient ingredient;
    private final Ingredient theme;
    private final ItemStack result;    // 处理时间（刻）

    public PurificationRecipe(ResourceLocation id, Ingredient ingredient,
                              Ingredient theme, ItemStack result) {
        this.id = id;
        this.ingredient = ingredient;
        this.theme = theme;
        this.result = result;
    }

    // 更新 matches 方法
    @Override
    public boolean matches(Container container, Level level) {
        return ingredient.test(container.getItem(0)) && theme.test(container.getItem(1));
    }

    @Override
    public @NotNull ItemStack assemble(Container container, RegistryAccess registryAccess) {
        return result.copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public @NotNull ItemStack getResultItem(RegistryAccess registryAccess) {
        return result.copy();
    }

    @Override
    public @NotNull ResourceLocation getId() {
        return id;
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return Serializer.INSTANCE;
    }

    @Override
    public @NotNull RecipeType<?> getType() {
        return Type.INSTANCE;
    }

//    public int getProcessingTime() {
//        return processingTime;
//    }
//
//    public Ingredient getObjectInput() {
//        return objectInput;
//    }
//
//    public Ingredient getThemeInput() {
//        return themeInput;
//    }

    // 配方类型注册
    public static class Type implements RecipeType<PurificationRecipe> {
        private Type() {}
        public static final PurificationRecipe.Type INSTANCE = new PurificationRecipe.Type();
        public static final String ID = RECIPE_ID;
    }

    // 配方序列化器
    public static class Serializer implements RecipeSerializer<PurificationRecipe> {
        public static final Serializer INSTANCE = new Serializer();
        public static final String RID = RECIPE_ID;
        public static final ResourceLocation ID =
                new ResourceLocation(Main.MODID, RECIPE_ID);

        @Override
        public @NotNull PurificationRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
            Ingredient ingredient = Ingredient.fromJson(GsonHelper.getAsJsonObject(json, "ingredient"));
            Ingredient theme = Ingredient.fromJson(GsonHelper.getAsJsonObject(json, "theme"));
            ItemStack result = ShapedRecipe.itemStackFromJson(json.get("result").getAsJsonObject());
            // 解析输出
//            ItemStack result = CraftingHelper.getItemStack(GsonHelper.getAsJsonObject(json, "result"), true);

            return new PurificationRecipe(recipeId, ingredient, theme, result);
        }

        @Override
        public PurificationRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
            Ingredient ingredient = Ingredient.fromNetwork(buffer);
            Ingredient theme = Ingredient.fromNetwork(buffer);
            ItemStack result = buffer.readItem();
            return new PurificationRecipe(recipeId, ingredient, theme, result);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, PurificationRecipe recipe) {
            recipe.ingredient.toNetwork(buffer);
            recipe.theme.toNetwork(buffer);
            buffer.writeItem(recipe.result);
        }
    }
}
