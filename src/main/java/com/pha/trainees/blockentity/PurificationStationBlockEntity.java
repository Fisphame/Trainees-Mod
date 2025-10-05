package com.pha.trainees.blockentity;

import com.pha.trainees.Main;
import com.pha.trainees.recipe.PurificationRecipe;
import com.pha.trainees.registry.ModBlockEntities;
import com.pha.trainees.screen.PurificationStationMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class PurificationStationBlockEntity extends BlockEntity implements MenuProvider {

    public PurificationStationBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.PURIFICATION_STATION.get(), pos, state);
    }

    private final ItemStackHandler itemHandler = new ItemStackHandler(3) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };

    private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();


    @Override
    public Component getDisplayName() {
        return Component.translatable("container.trainees.purification_station");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory playerInventory, Player player) {
        return new PurificationStationMenu(id, playerInventory, this);
    }

    public void craft() {
        if(level == null) return;

        Container container = new SimpleContainer(2);
        container.setItem(0, itemHandler.getStackInSlot(0));
        container.setItem(1, itemHandler.getStackInSlot(1));

        Optional<PurificationRecipe> recipe = level.getRecipeManager()
                .getRecipeFor(PurificationRecipe.Type.INSTANCE, container, level);

        if(recipe.isPresent()) {
            // 消耗输入物品
            itemHandler.extractItem(0, 1, false);
            itemHandler.extractItem(1, 1, false);

            // 设置输出物品
            ItemStack result = recipe.get().getResultItem(null).copy();
            ItemStack currentOutput = itemHandler.getStackInSlot(2);

            if(currentOutput.isEmpty()) {
                itemHandler.setStackInSlot(2, result);
            } else if(currentOutput.getItem() == result.getItem()) {
                currentOutput.grow(result.getCount());
            }
        }
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if(cap == ForgeCapabilities.ITEM_HANDLER) {
            return lazyItemHandler.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        lazyItemHandler = LazyOptional.of(() -> itemHandler);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        lazyItemHandler.invalidate();
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        tag.put("inventory", itemHandler.serializeNBT());
        super.saveAdditional(tag);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if(tag.contains("inventory")) {
            itemHandler.deserializeNBT(tag.getCompound("inventory"));
        }
    }
}