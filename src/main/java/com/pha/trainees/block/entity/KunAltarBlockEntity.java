package com.pha.trainees.block.entity;

import com.pha.trainees.registry.ModBlocks;
import com.pha.trainees.registry.ModItems;
import com.pha.trainees.util.game.KunAltarType;
import com.pha.trainees.util.game.Tools;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class KunAltarBlockEntity extends BlockEntity {

    private KunAltarType altarType = KunAltarType.COMPLETE;

    public KunAltarBlockEntity(BlockEntityType<?> p_155228_, BlockPos p_155229_, BlockState p_155230_) {
        super(p_155228_, p_155229_, p_155230_);
    }

    private final ItemStackHandler itemHandler = new ItemStackHandler(1) {
        @Override
        protected void onContentsChanged(int slot) {
            // 当物品变化时，同步数据到客户端
            setChanged();
            if (level != null && !level.isClientSide()) {
                level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
            }
        }
    };

    private final LazyOptional<IItemHandler> handler = LazyOptional.of(() -> itemHandler);

    public KunAltarBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlocks.ModBlockEntities.KUN_ALTAR_ENTITY.get(), pos, state);
    }

    // 获取存储的物品
    public ItemStack getStoredItem() {
        return itemHandler.getStackInSlot(0).copy();
    }

    // 设置存储的物品
    public void setStoredItem(ItemStack itemStack) {
        itemHandler.setStackInSlot(0, itemStack);
    }

    // 清空存储的物品
    public void clearStoredItem() {
        itemHandler.setStackInSlot(0, ItemStack.EMPTY);
    }

    // 判断是否有物品
    public boolean hasStoredItem() {
        return !itemHandler.getStackInSlot(0).isEmpty();
    }

    // 处理玩家交互
    public InteractionResult handleInteraction(Player player, InteractionHand hand) {
        ItemStack heldItem = player.getItemInHand(hand);
        ItemStack storedItem = itemHandler.getStackInSlot(0);

        if (heldItem.getItem() == storedItem.getItem()){
            return InteractionResult.PASS;
        }

        if (heldItem.getItem() == ModItems.STONE_STICK.get() && !player.isShiftKeyDown()) {
            // 切换祭坛类型
            toggleAltarType();

            // 同步数据到客户端
            setChanged();
            if (level != null) {
                level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
            }

            return InteractionResult.SUCCESS;
        }

        if (!storedItem.isEmpty() && player.isShiftKeyDown()) {
            // 潜行+空手：取出物品
            player.getInventory().add(storedItem);
            itemHandler.setStackInSlot(0, ItemStack.EMPTY);
            return InteractionResult.SUCCESS;
        }

        if (!storedItem.isEmpty()) {
            // 如果方块中已有物品，先将其掉落
            dropStoredItem();
        }

        if (!heldItem.isEmpty()) {
            // 存储玩家手中的一个物品
            ItemStack toStore = heldItem.copyWithCount(1);
            itemHandler.setStackInSlot(0, toStore);

            // 减少玩家手中的物品数量
            if (!player.isCreative()) {
                heldItem.shrink(1);
            }

            if (level != null && !level.isClientSide()) {
                level.playSound(null, worldPosition,
                        SoundEvents.END_PORTAL_FRAME_FILL,
                        SoundSource.BLOCKS,
                        0.5F, 1.0F);
            }

            if (level != null) {
                Tools.Particle.send(
                        level,
                        ParticleTypes.SOUL_FIRE_FLAME,
                        worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(),
                        5,          // 数量
                        0.3, 0.3, 0.3,  // 偏移
                        0.1       // 速度
                );
            }

            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }
    

    // 掉落存储的物品
    private void dropStoredItem() {
        if (level == null || level.isClientSide()) return;

        ItemStack storedItem = itemHandler.getStackInSlot(0);
        if (!storedItem.isEmpty()) {
            double x = worldPosition.getX() + 0.5;
            double y = worldPosition.getY() + 1.0;
            double z = worldPosition.getZ() + 0.5;

            ItemEntity itemEntity = new ItemEntity(level, x, y, z, storedItem.copy());
            itemEntity.setDefaultPickUpDelay();
            level.addFreshEntity(itemEntity);

            itemHandler.setStackInSlot(0, ItemStack.EMPTY);
        }
    }

    // 获取存储的物品（供渲染器使用）
    public ItemStack getDisplayItem() {
        return itemHandler.getStackInSlot(0).copy();
    }

    // 切换祭坛类型
    public void toggleAltarType() {
        altarType = altarType.next();

        // 播放切换音效
        if (level != null && !level.isClientSide()) {
            level.playSound(null, worldPosition,
                    SoundEvents.END_PORTAL_FRAME_FILL,
                    SoundSource.BLOCKS,
                    0.5F, 1.0F);
        }

        if (level != null) {
            Tools.Particle.send(
                    level,
                    ParticleTypes.SOUL_FIRE_FLAME,
                    worldPosition.getX() + 0.5, worldPosition.getY() + 0.2, worldPosition.getZ() + 0.5,
                    7,          // 数量
                    0.5, 0.2, 0.5,  // 偏移
                    0.1       // 速度
            );
        }
    }

    // 获取当前祭坛类型
    public KunAltarType getAltarType() {
        return altarType;
    }

    // 设置祭坛类型
    public void setAltarType(KunAltarType type) {
        this.altarType = type;
        setChanged();
        if (level != null) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    // NBT数据保存/加载
    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("inventory", itemHandler.serializeNBT());
        tag.putString("AltarType", altarType.name());
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains("inventory")) {
            itemHandler.deserializeNBT(tag.getCompound("inventory"));
        }
        if (tag.contains("AltarType")) {
            try {
                altarType = KunAltarType.valueOf(tag.getString("AltarType"));
            } catch (IllegalArgumentException e) {
                altarType = KunAltarType.COMPLETE;
            }
        }
    }

    // 客户端数据同步
    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        saveAdditional(tag);
        return tag;
    }

    @Override
    public void handleUpdateTag(CompoundTag tag) {
        load(tag);
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        handleUpdateTag(pkt.getTag());
    }

    // 能力系统
    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return handler.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        handler.invalidate();
    }
}
