//package com.pha.trainees.menu;
//
//import com.pha.trainees.Main;
//import com.pha.trainees.blockentity.PurificationStationBlockEntity;
//import com.pha.trainees.registry.ModBlocks;
//import com.pha.trainees.registry.ModMenuTypes;
//import net.minecraft.core.BlockPos;
//import net.minecraft.network.FriendlyByteBuf;
//import net.minecraft.world.entity.player.Inventory;
//import net.minecraft.world.entity.player.Player;
//import net.minecraft.world.inventory.AbstractContainerMenu;
//import net.minecraft.world.inventory.ContainerData;
//import net.minecraft.world.inventory.ContainerLevelAccess;
//import net.minecraft.world.inventory.Slot;
//import net.minecraft.world.item.ItemStack;
//import net.minecraft.world.level.Level;
//import net.minecraft.world.level.block.entity.BlockEntity;
//import net.minecraftforge.items.ItemStackHandler;
//import net.minecraftforge.items.SlotItemHandler;
//
//public class PurificationMenu extends AbstractContainerMenu {
//    public final PurificationStationBlockEntity blockEntity;
//    private final Level level;
//    private final ContainerData data;
//
//    // 修改构造函数
//    public PurificationMenu(int containerId, Inventory playerInventory, BlockPos pos) {
//        this(containerId, playerInventory, getBlockEntity(playerInventory, pos));
//    }
//
//
//
//    // 主构造函数 - 接收方块实体
//    public PurificationMenu(int containerId, Inventory playerInventory,
//                            PurificationStationBlockEntity blockEntity) {
//        super(ModMenuTypes.PURIFICATION_MENU.get(), containerId);
//        this.blockEntity = blockEntity;
//        this.level = playerInventory.player.level();
//
//        // 修改数据访问对象，添加按钮状态
//        this.data = new ContainerData() {
//            @Override
//            public int get(int index) {
//                // 返回方块实体的数据
//                return switch (index) {
//                    case 0 -> blockEntity.getProgress();
//                    case 1 -> blockEntity.getMaxProgress();
//                    default -> 0;
//                };
//            }
//
//            @Override
//            public void set(int index, int value) {
//                // 确保只在服务端执行
//                if (blockEntity.getLevel() != null && !blockEntity.getLevel().isClientSide()) {
//                    if (index == 2 && value == 1) {
//                        Main.LOGGER.info("服务端: 接收到合成请求");
//                        blockEntity.tryCraft();
//                    }
//                }
//            }
//
//            @Override
//            public int getCount() {
//                return 3; // 三个数据槽
//            }
//        };
//
//        addPlayerInventory(playerInventory);
//        addPlayerHotbar(playerInventory);
//        addSlots();
//
//        // 添加数据槽
//        addDataSlots(data);
//    }
//
//    public void triggerCrafting() {
//        Main.LOGGER.info("ke hu duan: chu fa he cheng an niu");
//        this.data.set(2, 1); // 触发按钮状态变化
//    }
//
//
//
//    // 更新基于FriendlyByteBuf的构造函数
//    public PurificationMenu(int containerId, Inventory playerInventory, FriendlyByteBuf buf) {
//        this(containerId, playerInventory,
//                buf != null ? buf.readBlockPos() : BlockPos.ZERO // 处理buf为null的情况
//        );
//    }
//
//    // 添加安全的getBlockEntity方法
//    private static PurificationStationBlockEntity getBlockEntity(Inventory playerInventory, BlockPos pos) {
//        if (pos == BlockPos.ZERO) {
//            // 创建临时方块实体时设置level
//            Level level = playerInventory.player.level();
//            PurificationStationBlockEntity temp = new PurificationStationBlockEntity(BlockPos.ZERO, ModBlocks.PURIFICATION_STATION.get().defaultBlockState());
//            temp.setLevel(level); // 手动设置level
//            return temp;
//        }
//
//        Level level = playerInventory.player.level();
//        BlockEntity blockEntity = level.getBlockEntity(pos);
//        if (blockEntity instanceof PurificationStationBlockEntity) {
//            return (PurificationStationBlockEntity) blockEntity;
//        }
//        throw new IllegalStateException("Block entity at " + pos + " is not correct!");
//    }
//
//    // 添加玩家物品栏
//    private void addPlayerInventory(Inventory playerInventory) {
//        for (int i = 0; i < 3; ++i) {
//            for (int j = 0; j < 9; ++j) {
//                this.addSlot(new Slot(playerInventory, j + i * 9 + 9,
//                        8 + j * 18, 84 + i * 18));
//            }
//        }
//    }
//    // 添加玩家快捷栏
//    private void addPlayerHotbar(Inventory playerInventory) {
//        for (int i = 0; i < 9; ++i) {
//            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
//        }
//    }
//    // 更新槽位添加方法
//    private void addSlots() {
//        ItemStackHandler handler = blockEntity.getItemHandler();
//
//        // 物品槽 - 可放入任意物品
//        this.addSlot(new SlotItemHandler(handler,
//                PurificationStationBlockEntity.FUEL_SLOT, 56, 53) {
//            @Override
//            public boolean mayPlace(ItemStack stack) {
//                return true; // 允许任何物品
//            }
//        });
//
//        // 升级槽 - 仅限特定物品
//        this.addSlot(new SlotItemHandler(handler,
//                PurificationStationBlockEntity.INPUT_SLOT, 56, 17) {
//            @Override
//            public boolean mayPlace(ItemStack stack) {
//                return blockEntity.isItemValidForInputSlot(stack);
//            }
//        });
//
//        // 输出槽 - 不可放入物品
//        this.addSlot(new SlotItemHandler(handler,
//                PurificationStationBlockEntity.OUTPUT_SLOT, 116, 35) {
//            @Override
//            public boolean mayPlace(ItemStack stack) {
//                return false;
//            }
//        });
//    }
//
//    // 更新快速移动逻辑
//    @Override
//    public ItemStack quickMoveStack(Player player, int index) {
//        ItemStack itemstack = ItemStack.EMPTY;
//        Slot slot = this.slots.get(index);
//
//        if (slot != null && slot.hasItem()) {
//            ItemStack itemstack1 = slot.getItem();
//            itemstack = itemstack1.copy();
//
//            final int slotCount = PurificationStationBlockEntity.SLOT_COUNT;
//            final int invStart = slotCount;
//            final int invEnd = invStart + 27; // 玩家物品栏
//            final int hotbarStart = invEnd;
//            final int hotbarEnd = hotbarStart + 9; // 快捷栏
//
//            // 从机器槽位移动到玩家物品栏
//            if (index < slotCount) {
//                if (!this.moveItemStackTo(itemstack1, invStart, hotbarEnd, true)) {
//                    return ItemStack.EMPTY;
//                }
//                slot.onQuickCraft(itemstack1, itemstack);
//            }
//            // 从玩家物品栏移动到机器槽位
//            else {
//                // 尝试放入输入槽（仅限特定物品）
//                if (blockEntity.isItemValidForInputSlot(itemstack1)) {
//                    if (!this.moveItemStackTo(itemstack1,
//                            PurificationStationBlockEntity.INPUT_SLOT,
//                            PurificationStationBlockEntity.INPUT_SLOT + 1,
//                            false)) {
//                        return ItemStack.EMPTY;
//                    }
//                }
//                // 尝试放入燃料槽（任意物品）
//                else if (!this.moveItemStackTo(itemstack1,
//                        PurificationStationBlockEntity.FUEL_SLOT,
//                        PurificationStationBlockEntity.FUEL_SLOT + 1,
//                        false)) {
//                    return ItemStack.EMPTY;
//                }
//                // 如果是快捷栏槽位，移动到主物品栏
//                else if (index >= hotbarStart && index < hotbarEnd) {
//                    if (!this.moveItemStackTo(itemstack1, invStart, invEnd, false)) {
//                        return ItemStack.EMPTY;
//                    }
//                }
//                // 如果是主物品栏，移动到快捷栏
//                else if (index >= invStart && index < invEnd) {
//                    if (!this.moveItemStackTo(itemstack1, hotbarStart, hotbarEnd, false)) {
//                        return ItemStack.EMPTY;
//                    }
//                }
//            }
//
//            if (itemstack1.isEmpty()) {
//                slot.set(ItemStack.EMPTY);
//            } else {
//                slot.setChanged();
//            }
//
//            if (itemstack1.getCount() == itemstack.getCount()) {
//                return ItemStack.EMPTY;
//            }
//
//            slot.onTake(player, itemstack1);
//        }
//        return itemstack;
//    }
//
//    @Override
//    public boolean stillValid(Player player) {
//        return stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()),
//                player, ModBlocks.PURIFICATION_STATION.get());
//    }
//
//    // 获取进度数据
//    public int getProgress() {
//        return data.get(0);
//    }
//
//    public int getMaxProgress() {
//        return data.get(1);
//    }
//    public boolean isProcessing() {
//        return getProgress() > 0;
//    }
//}
