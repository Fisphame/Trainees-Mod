package com.pha.trainees.menu;

import com.pha.trainees.Main;
import com.pha.trainees.blockentity.ReactionMachineBlockEntity;
import com.pha.trainees.registry.ModMenus;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;

public class ReactionMachineMenu extends AbstractContainerMenu {
    private final ReactionMachineBlockEntity blockEntity;
    private final ContainerData data;

    public ReactionMachineMenu(int id, Inventory inventory, BlockPos pos) {
        super(ModMenus.REACTION_MACHINE.get(), id);
        this.blockEntity = (ReactionMachineBlockEntity) inventory.player.level().getBlockEntity(pos);
        // 获取方块实体，可能为 null（如虚拟位置）
//        BlockEntity be = inventory.player.level().getBlockEntity(pos);
//        if (be instanceof ReactionMachineBlockEntity machine) {
//            this.blockEntity = machine;
//        } else {
//            // 如果获取失败，创建一个临时的空处理器（仅用于避免 NPE）
//            this.blockEntity = null;
//            Main.LOGGER.warn("Failed to get ReactionMachineBlockEntity at {} from inventory", pos);
//        }

        // 定义数据同步（安全处理）
        this.data = new ContainerData() {
            @Override
            public int get(int index) {
                if (blockEntity == null) return 0;
                return switch (index) {
                    case 0 -> blockEntity.getTemperatureC();
                    case 1 -> blockEntity.getReactionRemainingTicks();
                    case 2 -> blockEntity.isReacting() ? 1 : 0;
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {}

            @Override
            public int getCount() {
                return 3;
            }
        };
        addDataSlots(data);

        // 添加槽位（如果 blockEntity 为 null，使用临时容器）
        IItemHandler handler = blockEntity != null ? blockEntity.getItemHandler() : new ItemStackHandler(7);
        // 输入槽
        for (int i = 0; i < 3; i++) {
            addSlot(new SlotItemHandler(handler, i, 8 + i * 18, 18));
        }
        // 催化剂槽
        addSlot(new SlotItemHandler(handler, 3, 80, 40));
        // 输出槽
        for (int i = 0; i < 3; i++) {
            addSlot(new SlotItemHandler(handler, 4 + i, 8 + i * 18, 78));
        }

        // 玩家背包槽位（不变）
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                addSlot(new Slot(inventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }
        for (int i = 0; i < 9; i++) {
            addSlot(new Slot(inventory, i, 8 + i * 18, 142));
        }
    }

    @Override
    public ItemStack quickMoveStack(Player player, int slotIndex) {
        Slot slot = slots.get(slotIndex);
        if (!slot.hasItem()) return ItemStack.EMPTY;
        ItemStack stack = slot.getItem();
        ItemStack copy = stack.copy();

        int inputStart = 0, inputEnd = 3;
        int catalystSlot = 3;
        int outputStart = 4, outputEnd = 7;

        if (slotIndex < outputEnd) {
            // 从机器内部槽位移出
            if (!moveItemStackTo(stack, outputEnd, slots.size(), true)) {
                return ItemStack.EMPTY;
            }
        } else {
            // 从玩家背包移入机器
            if (stack.getCount() > 0) {
                if (!moveItemStackTo(stack, inputStart, inputEnd, false)) {
                    if (!moveItemStackTo(stack, catalystSlot, catalystSlot + 1, false)) {
                        return ItemStack.EMPTY;
                    }
                }
            }
        }

        if (stack.isEmpty()) {
            slot.set(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }
        return copy;
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    public ContainerData getData() {
        return data;
    }
}
