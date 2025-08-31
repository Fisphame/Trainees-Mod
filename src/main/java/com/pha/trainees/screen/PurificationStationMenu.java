package com.pha.trainees.screen;

import com.pha.trainees.blockentity.PurificationStationBlockEntity;
import com.pha.trainees.registry.ModBlocks;
import com.pha.trainees.registry.ModItems;
import com.pha.trainees.registry.ModMenuTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.SlotItemHandler;

public class PurificationStationMenu extends AbstractContainerMenu {

    public PurificationStationMenu(int containerId, Inventory playerInventory, BlockPos pos) {
        this(containerId, playerInventory, getBlockEntity(playerInventory, pos));
    }
    public final PurificationStationBlockEntity blockEntity;

    // 添加安全的getBlockEntity方法
    private static PurificationStationBlockEntity getBlockEntity(Inventory playerInventory, BlockPos pos) {
        if (pos == BlockPos.ZERO) {
            // 创建临时方块实体时设置level
            Level level = playerInventory.player.level();
            PurificationStationBlockEntity temp = new PurificationStationBlockEntity(BlockPos.ZERO, ModBlocks.PURIFICATION_STATION.get().defaultBlockState());
            temp.setLevel(level); // 手动设置level
            return temp;
        }

        Level level = playerInventory.player.level();
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof PurificationStationBlockEntity) {
            return (PurificationStationBlockEntity) blockEntity;
        }
        throw new IllegalStateException("Block entity at " + pos + " is not correct!");
    }

    public PurificationStationMenu(int id, Inventory playerInventory, PurificationStationBlockEntity entity) {
        super(ModMenuTypes.PURIFICATION_STATION_MENU.get(), id);
        this.blockEntity = entity;

        // 添加玩家物品栏
        addPlayerInventory(playerInventory);
        addPlayerHotbar(playerInventory);

        // 添加方块实体的物品栏槽位
        blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(handler -> {
            // 输入槽1 - ingredient (索引0)
            this.addSlot(new SlotItemHandler(handler, 0, 56, 17) {
                @Override
                public boolean mayPlace(ItemStack stack) {
                    return true; // 可以放入任何物品
                }
            });

            // 输入槽2 - theme (索引1)
            this.addSlot(new SlotItemHandler(handler, 1, 56, 53) {
                @Override
                public boolean mayPlace(ItemStack stack) {
                    return stack.is(ModItems.UPGRADE_THEME.get()) || stack.is(ModItems.UPGRADE_THEME_ARMOR.get());
                }
            });

            // 输出槽 (索引2) - 不可放置物品
            this.addSlot(new SlotItemHandler(handler, 2, 116, 35) {
                @Override
                public boolean mayPlace(ItemStack stack) {
                    return false;
                }
            });
        });
    }

    public PurificationStationMenu(int id, Inventory inv, FriendlyByteBuf extraData) {
        this(id, inv, (PurificationStationBlockEntity) inv.player.level().getBlockEntity(extraData.readBlockPos()));
    }

    @Override
    public ItemStack quickMoveStack(Player p_38941_, int p_38942_) {
        return null;
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    private void addPlayerInventory(Inventory playerInventory) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }
    }

    private void addPlayerHotbar(Inventory playerInventory) {
        for (int i = 0; i < 9; i++) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
        }
    }

    // 当玩家按下按钮时，这个方法会被调用
    public void craft() {
        blockEntity.craft();
    }
}