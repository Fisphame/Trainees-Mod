//package com.pha.trainees.recipe;
//
//import com.google.gson.JsonObject;
//import com.pha.trainees.inventory.ReactingFurnaceContainer;
//import net.minecraft.core.NonNullList;
//import net.minecraft.core.RegistryAccess;
//import net.minecraft.network.FriendlyByteBuf;
//import net.minecraft.resources.ResourceLocation;
//import net.minecraft.util.GsonHelper;
//import net.minecraft.world.item.ItemStack;
//import net.minecraft.world.item.crafting.Ingredient;
//import net.minecraft.world.item.crafting.Recipe;
//import net.minecraft.world.item.crafting.RecipeSerializer;
//import net.minecraft.world.item.crafting.RecipeType;
//import net.minecraft.world.level.Level;
//import net.minecraftforge.common.crafting.CraftingHelper;
//
//public class ReactingFurnaceRecipe implements Recipe<ReactingFurnaceContainer> {
//    private final ResourceLocation id;
//    private final Ingredient ingredient1;
//    private final Ingredient ingredient2;
//    private final ItemStack result;
//
//    public ReactingFurnaceRecipe(ResourceLocation id, Ingredient ingredient1, Ingredient ingredient2, ItemStack result) {
//        this.id = id;
//        this.ingredient1 = ingredient1;
//        this.ingredient2 = ingredient2;
//        this.result = result;
//    }
//
//    @Override
//    public boolean matches(ReactingFurnaceContainer container, Level level) {
//        return ingredient1.test(container.getItem(0)) && ingredient2.test(container.getItem(1));
//    }
//
//    @Override
//    public ItemStack assemble(ReactingFurnaceContainer container, RegistryAccess registryAccess) {
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
//        return result;
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
//        ingredients.add(ingredient1);
//        ingredients.add(ingredient2);
//        return ingredients;
//    }
//
//    public static class Type implements RecipeType<ReactingFurnaceRecipe> {
//        private Type() {}
//        public static final Type INSTANCE = new Type();
//        public static final String ID = "reacting_furnace";
//    }
//
//    public static class Serializer implements RecipeSerializer<ReactingFurnaceRecipe> {
//        public static final Serializer INSTANCE = new Serializer();
//        public static final ResourceLocation ID = new ResourceLocation("trainees", "reacting_furnace");
//
//        @Override
//        public ReactingFurnaceRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
//            Ingredient ingredient1 = Ingredient.fromJson(json.get("ingredient1"));
//            Ingredient ingredient2 = Ingredient.fromJson(json.get("ingredient2"));
//            ItemStack result = CraftingHelper.getItemStack(GsonHelper.getAsJsonObject(json, "result"), true);
//            return new ReactingFurnaceRecipe(recipeId, ingredient1, ingredient2, result);
//        }
//
//        @Override
//        public ReactingFurnaceRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
//            Ingredient ingredient1 = Ingredient.fromNetwork(buffer);
//            Ingredient ingredient2 = Ingredient.fromNetwork(buffer);
//            ItemStack result = buffer.readItem();
//            return new ReactingFurnaceRecipe(recipeId, ingredient1, ingredient2, result);
//        }
//
//        @Override
//        public void toNetwork(FriendlyByteBuf buffer, ReactingFurnaceRecipe recipe) {
//            recipe.ingredient1.toNetwork(buffer);
//            recipe.ingredient2.toNetwork(buffer);
//            buffer.writeItem(recipe.result);
//        }
//    }
//}
