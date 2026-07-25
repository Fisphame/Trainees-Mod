package com.pha.trainees.recipe;

import com.pha.trainees.registry.ModRecipes;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TrainerAltarRecipe implements Recipe<Container> {
    private final ResourceLocation id;
    private final Ingredient input1;
    private final Ingredient input2;
    private final Ingredient input3;
    private final Ingredient input4;
    private final ItemStack result;

    public TrainerAltarRecipe(ResourceLocation id, Ingredient input1, Ingredient input2,
                              Ingredient input3, Ingredient input4, ItemStack result) {
        this.id = id;
        this.input1 = input1;
        this.input2 = input2;
        this.input3 = input3;
        this.input4 = input4;
        this.result = result;
    }

    @Override
    public boolean matches(Container container, Level level) {
        // 此方法不会被多方块结构直接调用，保留默认实现，或者可依据容器内容判断
        // 如果将来需要从容器匹配，可以在这里实现逻辑
        return false;
    }

    @Override
    public @NotNull ItemStack assemble(Container container, RegistryAccess access) {
        return result.copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public @NotNull ItemStack getResultItem(RegistryAccess access) {
        return result;
    }

    public ItemStack getResultItem() {
        return result;
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.TRAINER_ALTAR_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipes.TRAINER_ALTAR_TYPE.get();
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> ingredients = NonNullList.create();
        ingredients.add(input1);
        ingredients.add(input2);
        ingredients.add(input3);
        ingredients.add(input4);
        return ingredients;
    }

    public boolean matches(ItemStack stack1, ItemStack stack2, ItemStack stack3, ItemStack stack4) {
        // 1. 输入物品列表（过滤空物品）
        List<ItemStack> inputs = new ArrayList<>();
        addIfNotEmpty(inputs, stack1);
        addIfNotEmpty(inputs, stack2);
        addIfNotEmpty(inputs, stack3);
        addIfNotEmpty(inputs, stack4);

        // 2. 配方 Ingredient 列表
        List<Ingredient> ingredients = List.of(input1, input2, input3, input4);

        // 3. 如果输入物品数量不等于4（有空的），直接失败
        if (inputs.size() != 4) {
            return false;
        }

        // 4. 尝试为每个 Ingredient 匹配一个输入物品
        List<ItemStack> remaining = new ArrayList<>(inputs);
        for (Ingredient ingredient : ingredients) {
            boolean matched = false;
            Iterator<ItemStack> iterator = remaining.iterator();
            while (iterator.hasNext()) {
                ItemStack candidate = iterator.next();
                if (ingredient.test(candidate)) {
                    iterator.remove(); // 匹配后移除，处理重复物品
                    matched = true;
                    break;
                }
            }
            if (!matched) {
                return false;
            }
        }

        // 5. 所有 Ingredient 都匹配成功，且剩余列表为空（因为数量相等）
        return true;
    }

    private void addIfNotEmpty(List<ItemStack> list, ItemStack stack) {
        if (stack != null && !stack.isEmpty()) {
            list.add(stack);
        }
    }

    public Ingredient getInput1() {
        return input1;
    }
    public Ingredient getInput2() {
        return input2;
    }
    public Ingredient getInput3() {
        return input3;
    }
    public Ingredient getInput4() {
        return input4;
    }

    public static class Serializer implements RecipeSerializer<TrainerAltarRecipe> {
        @Override
        public TrainerAltarRecipe fromJson(ResourceLocation recipeId, com.google.gson.JsonObject json) {
            Ingredient input1 = Ingredient.fromJson(json.get("input1"));
            Ingredient input2 = Ingredient.fromJson(json.get("input2"));
            Ingredient input3 = Ingredient.fromJson(json.get("input3"));
            Ingredient input4 = Ingredient.fromJson(json.get("input4"));
            ItemStack result = ShapedRecipe.itemStackFromJson(json.getAsJsonObject("result"));
            return new TrainerAltarRecipe(recipeId, input1, input2, input3, input4, result);
        }

        @Override
        public TrainerAltarRecipe fromNetwork(ResourceLocation recipeId, net.minecraft.network.FriendlyByteBuf buffer) {
            Ingredient input1 = Ingredient.fromNetwork(buffer);
            Ingredient input2 = Ingredient.fromNetwork(buffer);
            Ingredient input3 = Ingredient.fromNetwork(buffer);
            Ingredient input4 = Ingredient.fromNetwork(buffer);
            ItemStack result = buffer.readItem();
            return new TrainerAltarRecipe(recipeId, input1, input2, input3, input4, result);
        }

        @Override
        public void toNetwork(net.minecraft.network.FriendlyByteBuf buffer, TrainerAltarRecipe recipe) {
            recipe.input1.toNetwork(buffer);
            recipe.input2.toNetwork(buffer);
            recipe.input3.toNetwork(buffer);
            recipe.input4.toNetwork(buffer);
            buffer.writeItem(recipe.result);
        }
    }
}
