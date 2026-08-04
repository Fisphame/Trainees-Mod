package com.pha.trainees.client;

import com.pha.trainees.Main;

import com.pha.trainees.registry.ModBlocks;
import com.pha.trainees.registry.ModMenus;
import com.pha.trainees.registry.ModRecipes;
import net.minecraft.client.RecipeBookCategories;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.event.RegisterRecipeBookCategoriesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import java.util.List;

@Mod.EventBusSubscriber(modid = Main.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientEvents {

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        // 注册GUI屏幕和流体渲染
        event.enqueueWork(() -> {
//            MenuScreens.register(ModMenus.PURIFICATION_STATION_MENU.get(), PurificationStationScreen::new);
//            MenuScreens.register(ModMenus.REACTION_MACHINE.get(), ReactionMachineScreen::new);

//            // 设置流体渲染为半透明
//            ItemBlockRenderTypes.setRenderLayer(ModFluid.SOURCE_CHE_HBP.get(), RenderType.translucent());
//            ItemBlockRenderTypes.setRenderLayer(ModFluid.FLOWING_CHE_HBP.get(), RenderType.translucent());
//            ItemBlockRenderTypes.setRenderLayer(ModBlocks.CHE_HBP_BLOCK.get(), RenderType.translucent());
        });

        event.enqueueWork(() -> {
            BlockEntityRenderers.register(ModBlocks.ModBlockEntities.KUN_ALTAR_ENTITY.get(),
                    Renderer.KunAltarBlockEntityRenderer::new);
        });

        // 注意：Trainer Altar 多方块结构的注册已移至服务器端（Main.onServerStarted），
        // 因为结构匹配/激活逻辑全部在服务端运行，且结构 NBT 位于 data/ 命名空间，
        // 客户端的资源管理器（仅索引 assets/）无法读取。

    }

    // 土申祭坛配方在原版配方书中的分类（消除 "Unknown recipe category" 警告）
    // 惰性创建：不能在静态初始化中解析 RegistryObject（此时注册表尚未就绪，会 NPE）
    private static RecipeBookCategories trainerAltarCategory;

    private static RecipeBookCategories getTrainerAltarCategory() {
        if (trainerAltarCategory == null) {
            trainerAltarCategory = RecipeBookCategories.create("trainees_trainer_altar",
                    new ItemStack(ModBlocks.ALTAR_CORE_BLOCK.get()));
        }
        return trainerAltarCategory;
    }

    @SubscribeEvent
    public static void onRegisterRecipeBookCategories(RegisterRecipeBookCategoriesEvent event) {
        // 将祭坛配方类型映射到分类：消除警告，并让原版配方书正确归类祭坛配方
        RecipeBookCategories category = getTrainerAltarCategory();
        event.registerRecipeCategoryFinder(ModRecipes.TRAINER_ALTAR_TYPE.get(),
                recipe -> category);
        // 注册为独立的配方书标签，供原版配方书展示
        event.registerAggregateCategory(category, List.of(category));
    }

    @SubscribeEvent
    public static void onRegisterOverlays(RegisterGuiOverlaysEvent event) {
        event.registerAboveAll("beaker_info", new BeakerHudOverlay());
        event.registerAboveAll("analyzer_info", new AnalyzerHudOverlay());
    }
}