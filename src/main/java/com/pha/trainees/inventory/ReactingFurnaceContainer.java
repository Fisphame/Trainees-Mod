//package com.pha.trainees.inventory;
//
//import com.pha.trainees.blockentity.ReactingFurnaceBlockEntity;
//import net.minecraft.world.Container;
//import net.minecraft.world.entity.player.Player;
//import net.minecraft.world.inventory.AbstractContainerMenu;
//import net.minecraft.world.item.ItemStack;
//import net.minecraft.world.level.block.entity.BlockEntity;
//import net.minecraftforge.items.IItemHandler;
//import net.minecraftforge.items.SlotItemHandler;
//import net.minecraftforge.items.wrapper.InvWrapper;
//
//public class ReactingFurnaceContainer extends AbstractContainerMenu implements Container {
//    private final BlockEntity blockEntity;
//    private final Player player;
//    private final IItemHandler playerInventory;
//
//    public ReactingFurnaceContainer(int windowId, net.minecraft.world.inventory.ContainerLevelAccess access, Player player) {
//        super(null, windowId); // 这里需要你的MenuType
//        this.blockEntity = access.evaluate((level, pos) -> level.getBlockEntity(pos)).orElse(null);
//        this.player = player;
//        this.playerInventory = new InvWrapper(player.getInventory());
//
//        if (blockEntity instanceof ReactingFurnaceBlockEntity furnace) {
//            // 输入槽1 (原物品槽)
//            addSlot(new SlotItemHandler(furnace.getInputHandler(), 0, 56, 17));
//            // 输入槽2 (原燃料槽，现在可放任意物品)
//            addSlot(new SlotItemHandler(furnace.getInputHandler(), 1, 56, 53));
//            // 输出槽
//            addSlot(new SlotItemHandler(furnace.getOutputHandler(), 0, 116, 35));
//        }
//
//        // 玩家物品栏
//        layoutPlayerInventorySlots(8, 84);
//    }
//
//    private void layoutPlayerInventorySlots(int leftCol, int topRow) {
//        // 玩家主物品栏
//        addSlotBox(playerInventory, 9, leftCol, topRow, 9, 18, 3, 18);
//        // 快捷栏
//        topRow += 58;
//        addSlotRange(playerInventory, 0, leftCol, topRow, 9, 18);
//    }
//
//    private int addSlotRange(IItemHandler handler, int index, int x, int y, int amount, int dx) {
//        for (int i = 0; i < amount; i++) {
//            addSlot(new SlotItemHandler(handler, index, x, y));
//            x += dx;
//            index++;
//        }
//        return index;
//    }
//
//    private int addSlotBox(IItemHandler handler, int index, int x, int y, int dx, int dy, int horAmount, int verAmount) {
//        for (int j = 0; j < verAmount; j++) {
//            index = addSlotRange(handler, index, x, y, horAmount, dx);
//            y += dy;
//        }
//        return index;
//    }
//
//    @Override
//    public ItemStack quickMoveStack(Player player, int index) {
//        // 快速移动物品的逻辑
//        return ItemStack.EMPTY;
//    }
//
//    @Override
//    public int getContainerSize() {
//        return 0;
//    }
//
//    @Override
//    public boolean isEmpty() {
//        return false;
//    }
//
//    @Override
//    public ItemStack getItem(int p_18941_) {
//        return null;
//    }
//
//    @Override
//    public ItemStack removeItem(int p_18942_, int p_18943_) {
//        return null;
//    }
//
//    @Override
//    public ItemStack removeItemNoUpdate(int p_18951_) {
//        return null;
//    }
//
//    @Override
//    public void setItem(int p_18944_, ItemStack p_18945_) {
//
//    }
//
//    @Override
//    public void setChanged() {
//
//    }
//
//    @Override
//    public boolean stillValid(Player player) {
//        return true;
//    }
//
//    @Override
//    public void clearContent() {
//
//    }
//}