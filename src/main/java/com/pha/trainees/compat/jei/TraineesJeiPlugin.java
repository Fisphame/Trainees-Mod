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
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
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
        // ⚠ 相变等 quiet 规则不进"化学反应"百科（§19.17.1）：它们是**物理过程**，
        //    列进化学百科既不是化学知识、又会把 8 条相变边刷满列表；引擎侧同样不记它们的失败诊断。
        Map<String, ReactionRule> byId = new LinkedHashMap<>();
        for (ReactionEdge edge : ReactionGraph.getInstance().getAllEdges()) {
            ReactionRule rule = edge.getRule();
            if (rule == null || rule.getId() == null) continue;
            if (rule.isQuiet()) continue;
            byId.putIfAbsent(rule.getId().toString(), rule);
        }
        registration.addRecipes(ReactionRecipeCategory.TYPE, new ArrayList<>(byId.values()));

        // 日化品图鉴（§19.16 / §19.18）：把"有效氯"这个国标口径与规格窗口写清楚——
        // 否则玩家只看到一瓶"含氯消毒液"，不知道 5~8% 是怎么量出来的、也不知道次品为什么危险。
        registration.addIngredientInfo(new ItemStack(ModChemistry.ModChemistryItems.BLEACH_GOOD.get()),
                VanillaTypes.ITEM_STACK, Component.translatable("jei.trainees.bleach.info"));
        registration.addIngredientInfo(new ItemStack(ModChemistry.ModChemistryItems.BLEACH_PREMIUM.get()),
                VanillaTypes.ITEM_STACK, Component.translatable("jei.trainees.bleach.premium_info"));
        // 次品图鉴：只有**危险**类别存在物品（§19.18 决策：浓度不符不灌装，故无对应物品）
        for (var defective : List.of(
                ModChemistry.ModChemistryItems.DEFECTIVE_CHLORINE,
                ModChemistry.ModChemistryItems.DEFECTIVE_ALKALI,
                ModChemistry.ModChemistryItems.DEFECTIVE_ACID,
                ModChemistry.ModChemistryItems.DEFECTIVE_CHLORATE)) {
            registration.addIngredientInfo(new ItemStack(defective.get()),
                    VanillaTypes.ITEM_STACK, Component.translatable("jei.trainees.defective.info"));
        }
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
