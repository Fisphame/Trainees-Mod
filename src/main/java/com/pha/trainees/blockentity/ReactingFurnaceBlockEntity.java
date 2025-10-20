//package com.pha.trainees.blockentity;
//
//import com.pha.trainees.recipe.ReactingFurnaceRecipe;
//import net.minecraft.core.BlockPos;
//import net.minecraft.core.Direction;
//import net.minecraft.nbt.CompoundTag;
//import net.minecraft.world.Container;
//import net.minecraft.world.SimpleContainer;
//import net.minecraft.world.item.ItemStack;
//import net.minecraft.world.level.Level;
//import net.minecraft.world.level.block.entity.BlockEntity;
//import net.minecraft.world.level.block.state.BlockState;
//import net.minecraftforge.common.capabilities.Capability;
//import net.minecraftforge.common.capabilities.ForgeCapabilities;
//import net.minecraftforge.common.util.LazyOptional;
//import net.minecraftforge.items.IItemHandler;
//import net.minecraftforge.items.ItemStackHandler;
//import org.checkerframework.checker.units.qual.C;
//
//import javax.annotation.Nonnull;
//import javax.annotation.Nullable;
//import java.util.Optional;
//
//public class ReactingFurnaceBlockEntity extends BlockEntity {
//    private final ItemStackHandler inputHandler = createInputHandler();
//    private final ItemStackHandler outputHandler = createOutputHandler();
//    private final LazyOptional<IItemHandler> inputOptional = LazyOptional.of(() -> inputHandler);
//    private final LazyOptional<IItemHandler> outputOptional = LazyOptional.of(() -> outputHandler);
//
//    public ReactingFurnaceBlockEntity(BlockPos pos, BlockState state) {
//        super(null, pos, state); // 这里需要你的BlockEntityType
//    }
//
//    private ItemStackHandler createInputHandler() {
//        return new ItemStackHandler(2) {
//            @Override
//            protected void onContentsChanged(int slot) {
//                setChanged();
//            }
//
//            @Override
//            public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
//                return true; // 所有物品都可以放入
//            }
//        };
//    }
//
//    private ItemStackHandler createOutputHandler() {
//        return new ItemStackHandler(1) {
//            @Override
//            protected void onContentsChanged(int slot) {
//                setChanged();
//            }
//
//            @Override
//            public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
//                return false; // 输出槽不能手动放入物品
//            }
//        };
//    }
//
//    public void craft(Level level) {
//        if (level.isClientSide) return;
//
//        Container container = new SimpleContainer(2);
//        container.setItem(0, inputHandler.getStackInSlot(0));
//        container.setItem(1, inputHandler.getStackInSlot(1));
//
//
//        Optional<ReactingFurnaceRecipe> recipe = level.getRecipeManager()
//                .getRecipeFor(ReactingFurnaceRecipe.Type.INSTANCE, container, level);
//
//        if (recipe.isPresent()) {
//            ItemStack result = recipe.get().getResultItem(level.registryAccess());
//            ItemStack currentOutput = outputHandler.getStackInSlot(0);
//
//            // 检查输出槽是否可以放入结果
//            if (currentOutput.isEmpty() ||
//                    (ItemStack.isSameItemSameTags(currentOutput, result) &&
//                            currentOutput.getCount() + result.getCount() <= currentOutput.getMaxStackSize())) {
//
//                // 消耗输入物品
//                inputHandler.extractItem(0, 1, false);
//                inputHandler.extractItem(1, 1, false);
//
//                // 添加输出物品
//                if (currentOutput.isEmpty()) {
//                    outputHandler.setStackInSlot(0, result.copy());
//                } else {
//                    currentOutput.grow(result.getCount());
//                }
//            }
//        }
//    }
//
//    public IItemHandler getInputHandler() {
//        return inputHandler;
//    }
//
//    public IItemHandler getOutputHandler() {
//        return outputHandler;
//    }
//
//    @Override
//    public void load(CompoundTag tag) {
//        super.load(tag);
//        inputHandler.deserializeNBT(tag.getCompound("Input"));
//        outputHandler.deserializeNBT(tag.getCompound("Output"));
//    }
//
//    @Override
//    protected void saveAdditional(CompoundTag tag) {
//        super.saveAdditional(tag);
//        tag.put("Input", inputHandler.serializeNBT());
//        tag.put("Output", outputHandler.serializeNBT());
//    }
//
//    @Nonnull
//    @Override
//    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
//        if (cap == ForgeCapabilities.ITEM_HANDLER) {
//            if (side == null || side == Direction.UP) {
//                return inputOptional.cast();
//            } else if (side == Direction.DOWN) {
//                return outputOptional.cast();
//            } else {
//                return inputOptional.cast(); // 侧面也可以输入
//            }
//        }
//        return super.getCapability(cap, side);
//    }
//}
