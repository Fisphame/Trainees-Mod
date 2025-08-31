//package com.pha.trainees.recipe;
//
//import com.google.gson.JsonElement;
//import com.pha.trainees.Main;
//import net.minecraft.core.NonNullList;
//import net.minecraft.core.RegistryAccess;
//import net.minecraft.resources.ResourceLocation;
//import net.minecraft.util.GsonHelper;
//import net.minecraft.world.item.Item;
//import net.minecraft.world.item.ItemStack;
//import net.minecraft.world.item.crafting.*;
//import net.minecraft.world.level.Level;
//import net.minecraftforge.common.crafting.CraftingHelper;
//import org.jetbrains.annotations.NotNull;
//
//import javax.json.JsonObject;
//import java.awt.*;
//
//public class PurificationUpgradeRecipe implements Recipe<Container> {
//    private final ResourceLocation id;
//    private final Ingredient ingredient;
//    private final Ingredient theme;
//    private final ItemStack result;
//
//    public PurificationUpgradeRecipe(ResourceLocation id,Ingredient ingredient, Ingredient theme, ItemStack result){
//        this.id = id;
//        this.ingredient = ingredient;
//        this.theme = theme;
//        this.result = result;
//    }
//    public static final String RECIPE_ID = "purification_upgrade";
//
//
//    @Override
//    public boolean matches(Container container, Level level) {
//        return ingredient.test(container.getItem(0)) && theme.test(container.getItem(1));
//    }
//
//    @Override
//    public ItemStack assemble(Container container, RegistryAccess registryAccess) {
//        return result.copy();
//    }
//
//    @Override
//    public boolean canCraftInDimensions(int width, int height) {
//        return true;
//    }
//
//    @Override
//    public ItemStack getResultItem(RegistryAccess registryAccess) {
//        return result.copy();
//    }
//
//    @Override
//    public ResourceLocation getId() {
//        return id;
//    }
//
//    @Override
//    public RecipeSerializer<?> getSerializer() {
//        return Serializer.INSTANCE;
//    }
//
//    @Override
//    public RecipeType<?> getType() {
//        return Type.INSTANCE;
//    }
//
//    @Override
//    public NonNullList<Ingredient> getIngredients() {
//        NonNullList<Ingredient> ingredients = NonNullList.create();
//        ingredients.add(ingredient);
//        ingredients.add(theme);
//        return ingredients;
//    }
//
//    public static class Type implements RecipeType<PurificationUpgradeRecipe> {
//        private Type() {}
//        public static final Type INSTANCE = new Type();
//        public static final String ID = RECIPE_ID;
//    }
//
//    public static class Serializer implements RecipeSerializer<PurificationUpgradeRecipe> {
//        public static final Serializer INSTANCE = new Serializer();
//        public static final ResourceLocation ID =
//                new ResourceLocation(Main.MODID, RECIPE_ID);
//
//        @Override
//        public @NotNull PurificationRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
//            JsonObject objectJson = GsonHelper.getAsJsonObject(json, "object_input");
//            Ingredient ingredient = Ingredient.fromJson(GsonHelper.getAsJsonObject(json,"ingredient"));
//        }
//
//    }
//}
