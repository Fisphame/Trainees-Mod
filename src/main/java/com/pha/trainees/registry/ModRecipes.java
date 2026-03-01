package com.pha.trainees.registry;

import com.pha.trainees.Main;
import com.pha.trainees.recipe.TrainerAltarRecipe;
import com.pha.trainees.util.game.ItemPair4;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static com.pha.trainees.registry.ModItems.*;
import static com.pha.trainees.registry.ModChemistry.ModChemistryItems.*;

import java.util.ArrayList;
import java.util.List;

public class ModRecipes {
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS =
            DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, Main.MODID);
    public static final DeferredRegister<RecipeType<?>> TYPES =
            DeferredRegister.create(Registries.RECIPE_TYPE, Main.MODID);

    public static final RegistryObject<RecipeSerializer<TrainerAltarRecipe>> TRAINER_ALTAR_SERIALIZER =
            SERIALIZERS.register("trainer_altar", TrainerAltarRecipe.Serializer::new);

    public static final RegistryObject<RecipeType<TrainerAltarRecipe>> TRAINER_ALTAR_TYPE =
            TYPES.register("trainer_altar", () -> new RecipeType<>() {
                @Override
                public String toString() {
                    return new ResourceLocation(Main.MODID, "trainer_altar").toString();
                }
            });

//    public static final Lazy<List<TrainerAltarRecipe>> TRAINER_ALTAR_RECIPES = Lazy.of(() -> {
//        List<TrainerAltarRecipe> list = new ArrayList<>();
//        // 从旧的 ItemPair4 转换，或直接添加新配方
//        // 示例：四个石头 -> 钻石
//        list.add(createRecipe("stone_to_diamond",
//                Ingredient.of(Items.STONE), Ingredient.of(Items.STONE),
//                Ingredient.of(Items.STONE), Ingredient.of(Items.STONE),
//                new ItemStack(Items.DIAMOND)));
//
//        // 四个原木 -> 绿宝石
//        list.add(createRecipe("log_to_emerald",
//                Ingredient.of(Items.OAK_LOG), Ingredient.of(Items.OAK_LOG),
//                Ingredient.of(Items.OAK_LOG), Ingredient.of(Items.OAK_LOG),
//                new ItemStack(Items.EMERALD)));
//
//        // 四个二分之一锭 -> 四根石棒
//        list.add(createRecipe("ingot_to_stick",
//                Ingredient.of(ModItems.TWO_HALF_INGOT.get()), Ingredient.of(ModItems.TWO_HALF_INGOT.get()),
//                Ingredient.of(ModItems.TWO_HALF_INGOT.get()), Ingredient.of(ModItems.TWO_HALF_INGOT.get()),
//                new ItemStack(ModItems.STONE_STICK.get(), 4)));
//
//        return list;
//    });
//
//    private static TrainerAltarRecipe createRecipe(String name, Ingredient i1, Ingredient i2,
//                                                   Ingredient i3, Ingredient i4, ItemStack result) {
//        return new TrainerAltarRecipe(
//                new ResourceLocation(Main.MODID, name),
//                i1, i2, i3, i4, result
//        );
//    }
//
//
//    public static final Lazy<List<ItemPair4>> TRAINER_ALTAR_RECIPES = Lazy.of(() -> {
//        List<ItemPair4> list = new ArrayList<>();
//        // 仅祭坛合成
//        addRecipe(list, UPGRADE_THEME.get(), Items.DIAMOND, Items.DIAMOND, TWO_HALF_INGOT.get(), UPGRADE_THEME.get(), 2);
//        addRecipe(list, KUN_MACE.get(), KUN_AXE.get(), UPGRADE_THEME.get(), TWO_HALF_INGOT.get(), KUN_AXEMACE.get(), 1);
//        addRecipe(list, KUN_SCYTHE.get(), KUN_SWORD.get(), KUN_DAGGER.get(), UPGRADE_THEME.get(), KUN_COMPOUND_SCYTHE.get(), 1);
//        addRecipe(list, KUN_PICKAXE.get(), UPGRADE_THEME.get(), TWO_HALF_INGOT.get(), TWO_HALF_INGOT.get(), KUN_PICKAXE_FINAL.get(), 1);
//        addRecipe(list, TWO_HALF_INGOT.get(), Items.EGG, Items.EGG, ELASTOMERIC_CORE.get(), KUN_BASKETBALL.get(), 1);
//        addRecipe(list,TRAIN_II_HELMET.get(), UPGRADE_THEME_ARMOR.get(), TWO_HALF_INGOT.get(), TWO_HALF_INGOT.get(),
//                TRAIN_III_HELMET.get(), 1);
//        addRecipe(list,TRAIN_II_CHESTPLATE.get(), UPGRADE_THEME_ARMOR.get(), TWO_HALF_INGOT.get(), TWO_HALF_INGOT.get(),
//                TRAIN_III_CHESTPLATE.get(), 1);
//        addRecipe(list,TRAIN_II_LEGGINGS.get(), UPGRADE_THEME_ARMOR.get(), TWO_HALF_INGOT.get(), TWO_HALF_INGOT.get(),
//                TRAIN_III_LEGGINGS.get(), 1);
//        addRecipe(list, TRAIN_II_BOOTS.get(), UPGRADE_THEME_ARMOR.get(), TWO_HALF_INGOT.get(), TWO_HALF_INGOT.get(),
//                TRAIN_III_BOOTS.get(), 1);
//        addRecipe(list, Items.EGG, Items.GOLD_BLOCK, Items.GOLD_BLOCK, Items.GOLD_BLOCK, GOLD_EGG.get(), 1);
//
//        // 比直接合成便宜
//        addRecipe(list, KUN_BASKETBALL.get(), CHE_JIBP_PIECE.get(), CHE_JIBP_PIECE.get(), CHE_JIBP_PIECE.get(), BASKETBALL_ANTI.get(), 1);
//        addRecipe(list, Items.GOLDEN_APPLE, Items.GOLD_INGOT, Items.GOLD_INGOT, Items.GOLD_INGOT, Items.ENCHANTED_GOLDEN_APPLE, 1);
//        addRecipe(list, Items.ENCHANTED_GOLDEN_APPLE, Items.GOLD_INGOT, Items.FEATHER, TWO_HALF_INGOT.get(), AHKUN_APPLE.get(), 1);
//        addRecipe(list, AHKUN_APPLE.get(), Items.GOLD_INGOT, Items.FEATHER, TWO_HALF_INGOT.get(), BIG_AHKUN_APPLE.get(), 1);
//        addRecipe(list, UPGRADE_THEME.get(), CHE_JIBP_PIECE.get(), CHE_JIBP_PIECE.get(), CHE_JIBP_PIECE.get(),
//                UPGRADE_THEME_ARMOR.get(), 3);
//
//        // 与直接合成无异
//        addRecipe(list, POWDER_ANTI.get(), POWDER_ANTI.get(), POWDER_ANTI.get(), POWDER_ANTI.get(), POWDER_ANTI_4.get(), 1);
//
//        return list;
//    });
//    private static void addRecipe(List<ItemPair4> list, Item a, Item b, Item c, Item d, ItemStack re) {
//        list.add(new ItemPair4(a, b, c, d, re));
//    }
//    private static void addRecipe(List<ItemPair4> list, Item a, Item b, Item c, Item d, Item re, int count) {
//        addRecipe(list, a, b, c, d, new ItemStack(re, count));
//    }



}
