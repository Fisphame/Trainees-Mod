package com.pha.trainees.registry;

import com.pha.trainees.Main;
import com.pha.trainees.screen.PurificationStationMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModMenuTypes {
    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(ForgeRegistries.MENU_TYPES, Main.MODID);

    public static final RegistryObject<MenuType<PurificationStationMenu>> PURIFICATION_STATION_MENU = MENUS.register(
            "purification_station_menu",
            () -> IForgeMenuType.create((windowId, inv, data) ->
                    new PurificationStationMenu(windowId, inv, data.readBlockPos())
            )
    );
}