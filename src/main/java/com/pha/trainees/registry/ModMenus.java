package com.pha.trainees.registry;

import com.pha.trainees.Main;
//import com.pha.trainees.blockentity.ReactionMachineBlockEntity;
//import com.pha.trainees.menu.ReactionMachineMenu;
import com.pha.trainees.screen.PurificationStationMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModMenus {
    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(ForgeRegistries.MENU_TYPES, Main.MODID);

    public static final RegistryObject<MenuType<PurificationStationMenu>> PURIFICATION_STATION_MENU = MENUS.register(
            "purification_station_menu",
            () -> IForgeMenuType.create((windowId, inv, data) ->
                    new PurificationStationMenu(windowId, inv, data.readBlockPos())
            )
    );

//    public static final RegistryObject<MenuType<ReactionMachineMenu>> REACTION_MACHINE =
//            MENUS.register("reaction_machine",
//                    () -> IForgeMenuType.create((windowId, inv, data) -> {
//                        if (data == null) {
//                            // 防御：如果数据为空，创建虚拟菜单并警告
//                            Main.LOGGER.warn("Received null data when opening ReactionMachineMenu, using default position");
//                            return new ReactionMachineMenu(windowId, inv, BlockPos.ZERO);
//                        }
//                        BlockPos pos = data.readBlockPos();
//                        return new ReactionMachineMenu(windowId, inv, pos);
//                    }
//                    )
//            );
}