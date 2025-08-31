package com.pha.trainees.registry;

import com.pha.trainees.Main;
import com.pha.trainees.recipe.PurificationRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModRecipeTypes {
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS =
            DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, Main.MODID);

    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES =
            DeferredRegister.create(ForgeRegistries.RECIPE_TYPES, Main.MODID);

    // 注册配方类型
//    public static final RegistryObject<RecipeType<PurificationRecipe>> PURIFICATION_UPGRADE_RECIPE =
//            RECIPE_TYPES.register(PurificationRecipe.RECIPE_ID,
//                    () -> new RecipeType<>() {
//                        public String toString() {
//                            return PurificationRecipe.RECIPE_ID;
//                        }
//                    });

    // 注册配方类型
    public static final RegistryObject<RecipeType<PurificationRecipe>> PURIFICATION_RECIPE =
            RECIPE_TYPES.register(PurificationRecipe.RECIPE_ID,
                    () -> RecipeType.simple(new ResourceLocation(Main.MODID, PurificationRecipe.RECIPE_ID)));



    // 注册配方序列化器
    public static final RegistryObject<RecipeSerializer<?>> PURIFICATION_SERIALIZER =
            RECIPE_SERIALIZERS.register(PurificationRecipe.RECIPE_ID,
                    () -> PurificationRecipe.Serializer.INSTANCE);
}
