package com.pha.trainees;

import com.mojang.logging.LogUtils;
import com.pha.trainees.event.*;
import com.pha.trainees.registry.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(Main.MODID)
public class Main {
    // Define mod id in a common place for everything to reference
    public static final String MODID = "trainees";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    private static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    public Main() {
        var bus = FMLJavaModLoadingContext.get().getModEventBus();
        var ebus = MinecraftForge.EVENT_BUS;
        BLOCKS.register(bus);
        ITEMS.register(bus);
        CREATIVE_MODE_TABS.register(bus);


        ModBlocks.BLOCKS.register(bus);
        ModSounds.SOUNDS.register(bus);
        ModEnchantments.ENCHANTMENTS.register(bus);
        ModItems.ITEMS.register(bus);
        ModEntities.ENTITIES.register(bus);

        ebus.register(AbilityHandler.class);
        //ebus.register(ClientEvents.class);
        ebus.register(FoodHandler.class);
        ebus.register(PickupHandler.class);
        ebus.register(RealPickaxeEvents.class);
        ebus.register(SweepHandler.class);
        ebus.register(ThrowHandler.class);

    }
    //创造模式物品栏
    public static final RegistryObject<CreativeModeTab> kun_tab = CREATIVE_MODE_TABS.register("kun_tab",() -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.trainees.kun_tab"))
            .icon(() -> new ItemStack(ModItems.KUN_NUGGET.get()))
            .displayItems((parm,output) -> {
                ModItems.ITEMS.getEntries().forEach(item -> output.accept(item.get()));
            })
            .build()
    );


}