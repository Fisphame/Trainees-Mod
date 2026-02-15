package com.pha.trainees.registry;

import com.pha.trainees.Main;
import com.pha.trainees.item.ChemistryItem;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;



public class ModCreativeModeTabs{
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Main.MODID);

    // 练习生
    public static final RegistryObject<CreativeModeTab> KUN_TAB = CREATIVE_MODE_TABS.register("kun_tab",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.trainees.kun_tab"))
                    .icon(() -> new ItemStack(ModItems.KUN_NUGGET.get()))
                    .displayItems((parm,output) -> {
                        ModItems.ITEMS.getEntries().forEach(item -> output.accept(item.get()));
                    })
                    .build()
    );

    // 整蛊
    public static final RegistryObject<CreativeModeTab> PRANK_TAB = CREATIVE_MODE_TABS.register("prank_tab",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.trainees.prank_tab"))
                    .icon(() -> new ItemStack(Something.PrankItems.REAL_DIAMOND_PICKAXE.get()))
                    .displayItems((parm,output) -> {
                        Something.PrankItems.ITEMS.getEntries().forEach(item -> output.accept(item.get()));
                    })
                    .build()
    );

    // 化学讲堂
    public static final RegistryObject<CreativeModeTab> CHEMISTRY_TAB = CREATIVE_MODE_TABS.register("chemistry_tab",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.trainees.chemistry_tab"))
                    .icon(() -> new ItemStack(ModChemistry.ModChemistryItems.CHEMISTRY_BOOK.get()))
                    .displayItems( (parm, output) -> {
                        output.accept(ModItems.TWO_HALF_INGOT_BLOCK_ITEM.get());
                        ModChemistry.ModChemistryBlockItems.ITEMS.getEntries().forEach(item -> output.accept(item.get()));
                        output.accept(Items.WATER_BUCKET.asItem());
                        output.accept(ModItems.TWO_HALF_INGOT.get());
                        output.accept(ModItems.KUN_NUGGET.get());
                        output.accept(ModItems.POWDER_ANTI.get());
                        output.accept(ModItems.POWDER_ANTI_4.get());
                        output.accept(ModItems.POWDER_ANTI_9.get());
                        ModChemistry.ModChemistryItems.ITEMS.getEntries().forEach(item -> output.accept(item.get()));
                    })
                    .build()
    );
}
