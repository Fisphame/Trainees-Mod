package com.pha.trainees.compat.jei;

import com.pha.trainees.Main;
import com.pha.trainees.chemistry.reaction.ReactionEdge;
import com.pha.trainees.chemistry.reaction.ReactionGraph;
import com.pha.trainees.chemistry.reaction.ReactionRule;
import com.pha.trainees.recipe.TrainerAltarRecipe;
import com.pha.trainees.registry.ModBlocks;
import com.pha.trainees.registry.ModChemistry;
import com.pha.trainees.registry.ModRecipes;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IModIngredientRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings({"deprecation", "removal"})
@JeiPlugin
public class TraineesJeiPlugin implements IModPlugin {
    private static final ResourceLocation PLUGIN_UID = new ResourceLocation(Main.MODID, "jei_plugin");

    @Override
    public @NotNull ResourceLocation getPluginUid() {
        return PLUGIN_UID;
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        // 注册你的自定义配方类别
        registration.addRecipeCategories(new TrainerAltarRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
        // 化学反应（§19.16）：百科式呈现全部反应规则
        registration.addRecipeCategories(new ReactionRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
    }

    /**
     * 注册自定义条目类型（§19.16）：裸离子在 JEI 中作为可搜索、可 R/U 聚焦的条目。
     * 物质（一级真物品 / 二级 NBT 物质）仍走 ItemStack 槽位，从而 R/U 同时命中物品配方与化学方程式。
     */
    @Override
    public void registerIngredients(@NotNull IModIngredientRegistration registration) {
        registration.register(ChemicalIngredientTypes.ION,
                ModChemistry.ModIons.ALL_IONS,
                new IonIngredientHelper(),
                new IonIngredientRenderer());
    }

    @Override
    public void registerRecipes(@NotNull IRecipeRegistration registration) {
        // 需要在客户端有 level 时才能访问 RecipeManager
        if (Minecraft.getInstance().level != null) {
            RecipeManager recipeManager = Minecraft.getInstance().level.getRecipeManager();
            List<TrainerAltarRecipe> recipes = recipeManager.getAllRecipesFor(ModRecipes.TRAINER_ALTAR_TYPE.get())
                    .stream()
                    .toList();
            registration.addRecipes(TrainerAltarRecipeCategory.TYPE, recipes);
        }

        // 化学反应：直接取反应图全部规则（客户端也已注册），按 id 去重（多条边共享同一规则）
        Map<String, ReactionRule> byId = new LinkedHashMap<>();
        for (ReactionEdge edge : ReactionGraph.getInstance().getAllEdges()) {
            ReactionRule rule = edge.getRule();
            if (rule != null && rule.getId() != null) {
                byId.putIfAbsent(rule.getId().toString(), rule);
            }
        }
        registration.addRecipes(ReactionRecipeCategory.TYPE, new ArrayList<>(byId.values()));
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.ALTAR_CORE_BLOCK.get()), TrainerAltarRecipeCategory.TYPE);
        // 化学反应可在烧杯/电解槽中进行
        registration.addRecipeCatalyst(new ItemStack(ModChemistry.ModChemistryBlockItems.BEAKER.get()),
                ReactionRecipeCategory.TYPE);
        registration.addRecipeCatalyst(new ItemStack(ModChemistry.ModChemistryBlockItems.ELECTROLYSIS_CELL.get()),
                ReactionRecipeCategory.TYPE);
    }
}
