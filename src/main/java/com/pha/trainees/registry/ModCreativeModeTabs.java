package com.pha.trainees.registry;

import com.pha.trainees.Main;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;



public class ModCreativeModeTabs{
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Main.MODID);

    //练习生
    public static final RegistryObject<CreativeModeTab> kun_tab = CREATIVE_MODE_TABS.register("kun_tab",() -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.trainees.kun_tab"))
            .icon(() -> new ItemStack(ModItems.KUN_NUGGET.get()))
            .displayItems((parm,output) -> {
                ModItems.ITEMS.getEntries().forEach(item -> output.accept(item.get()));
            })
            .build()
    );

    //豪哥
    public static final RegistryObject<CreativeModeTab> hao_tab = CREATIVE_MODE_TABS.register("hao_tab",() -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.trainees.hao_tab"))
            .icon(() -> new ItemStack(HaoItems.HAO_GE.get()))
            .displayItems((parm,output) -> {
                HaoItems.ITEMS.getEntries().forEach(item -> output.accept(item.get()));
            })
            .build()
    );


    //整蛊
    public static final RegistryObject<CreativeModeTab> prank_tab = CREATIVE_MODE_TABS.register("prank_tab",() -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.trainees.prank_tab"))
            .icon(() -> new ItemStack(PrankItems.REAL_DIAMOND_PICKAXE.get()))
            .displayItems((parm,output) -> {
                PrankItems.ITEMS.getEntries().forEach(item -> output.accept(item.get()));
            })
            .build()
    );
}
