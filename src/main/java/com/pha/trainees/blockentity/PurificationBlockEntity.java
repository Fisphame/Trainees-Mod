//package com.pha.trainees.blockentity;
//
//import com.pha.trainees.Main;
//import com.pha.trainees.menu.PurificationMenu;
//import com.pha.trainees.recipe.PurificationRecipe;
//import com.pha.trainees.registry.ModBlockEntities;
//import com.pha.trainees.registry.ModItems;
//import com.pha.trainees.registry.ModRecipeTypes;
//import net.minecraft.core.BlockPos;
//import net.minecraft.core.Direction;
//import net.minecraft.nbt.CompoundTag;
//import net.minecraft.network.chat.Component;
//import net.minecraft.world.Container;
//import net.minecraft.world.MenuProvider;
//import net.minecraft.world.entity.player.Inventory;
//import net.minecraft.world.entity.player.Player;
//import net.minecraft.world.inventory.AbstractContainerMenu;
//import net.minecraft.world.item.ItemStack;
//import net.minecraft.world.level.Level;
//import net.minecraft.world.level.block.Block;
//import net.minecraft.world.level.block.entity.BlockEntity;
//import net.minecraft.world.level.block.state.BlockState;
//import net.minecraft.world.level.block.state.StateDefinition;
//import net.minecraft.world.level.block.state.properties.BlockStateProperties;
//import net.minecraft.world.level.block.state.properties.BooleanProperty;
//import net.minecraftforge.common.capabilities.Capability;
//import net.minecraftforge.common.capabilities.ForgeCapabilities;
//import net.minecraftforge.common.util.LazyOptional;
//import net.minecraftforge.items.IItemHandler;
//import net.minecraftforge.items.ItemStackHandler;
//import org.jetbrains.annotations.NotNull;
//
//import javax.annotation.Nullable;
//import java.util.Optional;
//
//public class PurificationStationBlockEntity extends BlockEntity implements MenuProvider, Container {
//    public PurificationStationBlockEntity(BlockPos pos, BlockState state) {
//        super(ModBlockEntities.PURIFICATION_STATION.get(), pos, state);
//    }
//    @Override
//    public Component getDisplayName() {
//        return Component.translatable("container.trainees.purification_station");
//    }
//
//    @Nullable
//    @Override
//    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
//        // 使用BlockPos创建菜单
//        return new PurificationMenu(containerId, playerInventory, this.getBlockPos());
//    }
//
//    // 槽位定义
//    public static final int FUEL_SLOT = 0;      // 任意物品
//    public static final int INPUT_SLOT = 1;     // 特定物品
//    public static final int OUTPUT_SLOT = 2;    // 输出槽
//    public static final int SLOT_COUNT = 3;     // 槽位总数
//    // 在Block类中添加LIT属性
//    public static final BooleanProperty LIT = BlockStateProperties.LIT;
//    private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();
//    private final ItemStackHandler itemHandler = new ItemStackHandler(SLOT_COUNT) {
//        @Override
//        protected void onContentsChanged(int slot) {
//            setChanged(); // 标记数据改变需要保存
//        }
//    };
//
//
//    //物品验证方法
//    public boolean isItemValidForInputSlot(ItemStack stack) {
//        // 仅允许放入升级主题或护甲升级物品
//        return stack.is(ModItems.UPGRADE_THEME.get()) || stack.is(ModItems.UPGRADE_THEME_ARMOR.get());
//    }
//    //getItemHandler方法
//    public ItemStackHandler getItemHandler() {
//        return itemHandler;
//    }
//    public int getProgress() {
//        return 0;
//    }
//    public int getMaxProgress() {
//        return 100;
//    }
//
//    // 能力提供
//    @Override
//    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
//        if (cap == ForgeCapabilities.ITEM_HANDLER) {
//            return lazyItemHandler.cast();
//        }
//        return super.getCapability(cap, side);
//    }
//
//    @Override
//    public void onLoad() {
//        super.onLoad();
//        lazyItemHandler = LazyOptional.of(() -> itemHandler);
//    }
//
//    @Override
//    public void invalidateCaps() {
//        super.invalidateCaps();
//        lazyItemHandler.invalidate();
//    }
//
//    // 数据保存与加载
//    @Override
//    protected void saveAdditional(CompoundTag tag) {
//        tag.put("inventory", itemHandler.serializeNBT());
//        super.saveAdditional(tag);
//    }
//
//    @Override
//    public void load(CompoundTag tag) {
//        super.load(tag);
//        itemHandler.deserializeNBT(tag.getCompound("inventory"));
//    }
//
//    // 实现Container接口方法
//    @Override
//    public int getContainerSize() {
//        return SLOT_COUNT;
//    }
//
//    @Override
//    public boolean isEmpty() {
//        for(int i = 0; i < itemHandler.getSlots(); i++) {
//            if(!itemHandler.getStackInSlot(i).isEmpty()) {
//                return false;
//            }
//        }
//        return true;
//    }
//
//    @Override
//    public ItemStack getItem(int slot) {
//        return itemHandler.getStackInSlot(slot);
//    }
//
//    @Override
//    public ItemStack removeItem(int slot, int amount) {
//        return itemHandler.extractItem(slot, amount, false);
//    }
//
//    @Override
//    public ItemStack removeItemNoUpdate(int slot) {
//        ItemStack stack = getItem(slot);
//        if (stack.isEmpty()) {
//            return ItemStack.EMPTY;
//        }
//        setItem(slot, ItemStack.EMPTY);
//        return stack;
//    }
//
//    @Override
//    public void setItem(int slot, ItemStack stack) {
//        itemHandler.setStackInSlot(slot, stack);
//        setChanged();
//    }
//
//    @Override
//    public void setChanged() {
//        super.setChanged();
//        if (this.level != null) {
//            this.level.sendBlockUpdated(this.worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
//        }
//    }
//
//    @Override
//    public boolean stillValid(Player player) {
//        if (this.level.getBlockEntity(this.worldPosition) != this) {
//            return false;
//        } else {
//            return player.distanceToSqr(
//                    (double) this.worldPosition.getX() + 0.5D,
//                    (double) this.worldPosition.getY() + 0.5D,
//                    (double) this.worldPosition.getZ() + 0.5D
//            ) <= 64.0D;
//        }
//    }
//
//    @Override
//    public void clearContent() {
//        for(int i = 0; i < itemHandler.getSlots(); i++) {
//            itemHandler.setStackInSlot(i, ItemStack.EMPTY);
//        }
//    }
//
//    @Override
//    public void setLevel(Level level) {
//        super.setLevel(level);
//        this.level = level; // 确保字段被更新
//        Main.LOGGER.debug("she zhi level: {}", level);
//    }
//
//    // 添加按钮触发方法
//    public void tryCraft() {
//        // 使用getLevel()而不是直接访问level字段
//        Level currentLevel = getLevel();
//        // 安全获取level ???????????????????????????????????????????????
////        Level currentLevel = getSafeLevel();
//
//        if (currentLevel == null) {
//            Main.LOGGER.error("can not get available level");
//            return;
//        }
//
//        // 双重检查确保只在服务端执行
//        if (currentLevel.isClientSide) {
//            Main.LOGGER.error("this is not be allowed");
//            return;
//        }
//        if (currentLevel == null) {
//            // 尝试从世界重新获取
//            if (this.level != null) {
//                currentLevel = this.level;
//            } else {
//                Main.LOGGER.error("wu fa huo qu you xiao de level!");
//                return;
//            }
//        }
//
//
//        // 现在可以安全使用currentLevel
//        Optional<PurificationRecipe> recipe = currentLevel.getRecipeManager().getRecipeFor(
//                ModRecipeTypes.PURIFICATION_RECIPE.get(),
//                this,
//                currentLevel
//        );
//
//        // 添加调试日志
//        Main.LOGGER.info("chang shi he cheng:ran liao cao={},zhu ti cao={}",
//                getItemHandler().getStackInSlot(FUEL_SLOT).getItem(),
//                getItemHandler().getStackInSlot(INPUT_SLOT).getItem());
//
//        if (recipe.isPresent()) {
//            Main.LOGGER.info("zhao dao pei fang:{}", recipe.get().getId());
//        } else {
//            Main.LOGGER.info("wei zhao dao pi pei pei fang");
//        }
//
//
//
//        if (recipe.isPresent()) {
//            PurificationRecipe r = recipe.get();
//            ItemStack outputStack = getItemHandler().getStackInSlot(OUTPUT_SLOT);
//            ItemStack result = r.getResultItem(level.registryAccess());
//
//            // 检查输出槽是否可以接受
//            boolean canOutput = outputStack.isEmpty() ||
//                    (outputStack.is(result.getItem()) &&
//                            outputStack.getCount() + result.getCount() <= outputStack.getMaxStackSize());
//
//            if (canOutput) {
//                // 立即处理
//                craftItem(r);
//            }
//        }
//
//        // 更新方块状态
//        BlockState state = level.getBlockState(worldPosition);
//        level.setBlock(worldPosition, state.setValue(LIT, isProcessing()), Block.UPDATE_ALL);
//    }
//
//    // 修改 craftItem 方法
//    private void craftItem(PurificationRecipe recipe) {
//        // 消耗燃料
//        ItemStack fuelStack = getItemHandler().getStackInSlot(FUEL_SLOT);
//        if (!fuelStack.isEmpty()) fuelStack.shrink(1);
//
//        // 消耗主题物品
//        ItemStack themeStack = getItemHandler().getStackInSlot(INPUT_SLOT);
//        if (!themeStack.isEmpty()) themeStack.shrink(1);
//
//        // 设置输出
//        ItemStack result = recipe.getResultItem(level.registryAccess()).copy();
//        ItemStack outputStack = getItemHandler().getStackInSlot(OUTPUT_SLOT);
//
//        if (outputStack.isEmpty()) {
//            getItemHandler().setStackInSlot(OUTPUT_SLOT, result);
//        } else if (outputStack.is(result.getItem())) {
//            outputStack.grow(result.getCount());
//        }
//
//        setChanged(); // 标记方块实体已更改
//    }
//
//    // 添加处理状态检查方法
//    public boolean isProcessing() {
//        // 检查是否有有效配方
//        Optional<PurificationRecipe> recipe = Optional.empty();
//        if (level != null) {
//            recipe = level.getRecipeManager().getRecipeFor(
//                    ModRecipeTypes.PURIFICATION_RECIPE.get(),
//                    this,
//                    level
//            );
//        }
//        return recipe.isPresent();
//    }
//
//
//    // 在方块注册中设置默认状态
//    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
//        builder.add(LIT);
//    }
//
//
//
//}