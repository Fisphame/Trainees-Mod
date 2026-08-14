package com.pha.trainees.blockentity;

import appeng.api.config.Actionable;
import appeng.api.networking.*;
import appeng.api.networking.security.IActionHost;
import appeng.api.networking.security.IActionSource;
import appeng.api.stacks.AEItemKey;
import appeng.api.storage.IStorageMounts;
import appeng.api.storage.IStorageProvider;
import appeng.api.storage.MEStorage;
import appeng.api.storage.StorageHelper;
import appeng.api.util.AECableType;
import com.pha.trainees.Main;
import com.pha.trainees.registry.ModBlocks;
import com.pha.trainees.registry.ModItems;
import com.pha.trainees.util.game.enums.AbsorbWorkModel;
import com.pha.trainees.util.game.ParticleHelper;
import com.pha.trainees.util.game.Tools;
import com.pha.trainees.util.interfaces.IMachine;
import com.pha.trainees.util.interfaces.ITraversal;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

public class AbsorbBlockEntity extends ItemHandlerBlockEntity implements ITraversal, IMachine,
        IInWorldGridNodeHost, IStorageProvider, IGridNodeListener<AbsorbBlockEntity>, IActionHost {
    public AbsorbBlockEntity(BlockEntityType<?> p_155228_, BlockPos p_155229_, BlockState p_155230_) {
        super(p_155228_, p_155229_, p_155230_);
    }

    public AbsorbBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlocks.ModBlockEntities.ABSORB_BLOCK_ENTITY.get(), pos, state);
    }


    @Override
    public int getLimit() {
        return Integer.MAX_VALUE;
    }


    private final IManagedGridNode mainNode = GridHelper.createManagedNode(this, this)
            .setInWorldNode(true)
            .setVisualRepresentation(AEItemKey.of(this.getBlockState().getBlock().asItem()))
            .addService(IStorageProvider.class, this);
    private static final Capability<IInWorldGridNodeHost> IN_WORLD_GRID_NODE_HOST_CAP =
            CapabilityManager.get(new CapabilityToken<>() {});

    private final Map<BlockPos, ItemHandlerBlockEntity> map = new HashMap<>();
    private int timer = 0;
    private static final int COOLDOWN_TICK = 8;
    private AbsorbWorkModel model = AbsorbWorkModel.HINDERING;




    // 处理玩家交互
    public InteractionResult handleInteraction(Player player, InteractionHand hand) {
        ItemStack heldItem = player.getItemInHand(hand);
        ItemStack storedItem = itemHandler.getStackInSlot(0);
        Item item = heldItem.getItem();

        if (item == ModItems.STONE_STICK.get()) {
            player.displayClientMessage(
                    Component.literal(
                            MessageFormat.format("item : {0}, count : {1}   workModel : {2}",
                                    storedItem.getItem().toString(), storedItem.getCount(), getModel().getId()))
                            .withStyle(ChatFormatting.GRAY),
                    true
            );

            return InteractionResult.SUCCESS;
        }

        // 获取玩家持久数据中用于配置的标签
        String configKey = Main.MODID + "_config";
        CompoundTag persistentData = player.getPersistentData();
        CompoundTag configData = persistentData.getCompound(configKey);

        if (item == ModItems.STONE_STICK_T.get() && !configData.getBoolean("SettingMode")) {
            AbsorbWorkModel model = getModel().next();
            setModel(model);
            player.displayClientMessage(
                    Component.literal(
                            MessageFormat.format("modified workModel to : {0}", model.getId()))
                            .withStyle(ChatFormatting.GRAY),
                            true
            );

            return InteractionResult.SUCCESS;
        }

        // 潜行+空手：取出物品
        if (!storedItem.isEmpty() && player.isShiftKeyDown() && player.getUseItem().isEmpty()) {
            int size = storedItem.getMaxStackSize();
            int count = storedItem.getCount();
            int finalCount = Math.min(size, count);
            ItemStack putStack = storedItem.copy();
            putStack.setCount(finalCount);
            player.getInventory().add(putStack);
            storedItem.shrink(finalCount);
            if (storedItem.isEmpty()) {
                itemHandler.setStackInSlot(0, ItemStack.EMPTY);
            } else {
                itemHandler.setStackInSlot(0, storedItem);
            }

            return InteractionResult.SUCCESS;
        }

        if (!storedItem.isEmpty() && item != ModItems.STONE_STICK.get() && item != ModItems.STONE_STICK_T.get()) {
            // 如果方块中已有物品，将其掉落
            dropStoredItem();
            if (level != null) {
                ParticleHelper.send(
                        level, ParticleTypes.SOUL_FIRE_FLAME, worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(),
                        15, 0.3, 0.3, 0.3, 0.1
                );
            }

            if (level != null && !level.isClientSide()) {
                level.playSound(null, worldPosition,
                        SoundEvents.DEEPSLATE_HIT, SoundSource.BLOCKS, 0.5F, 1.0F
                );
            }
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }

    public static <T extends BlockEntity> void tick(Level level, BlockPos blockPos, BlockState state, T t) {
        if (!(t instanceof AbsorbBlockEntity absorb)) return;
        if (level.isClientSide) return;

        if (++absorb.timer <= COOLDOWN_TICK) return;
        absorb.timer = 0;

        if (level.getBestNeighborSignal(blockPos) > 0) return;

        absorb.find(level, blockPos);

        // 获取当前 Absorb 自身存储的物品
        ItemStack currentStored = absorb.itemHandler.getStackInSlot(0);

        // 遍历所有邻居，并让每个邻居的处理返回更新后的 currentStored
        for (Map.Entry<BlockPos, ItemHandlerBlockEntity> entry : absorb.map.entrySet()) {
            currentStored = processNeighbor(entry.getValue(), currentStored, absorb);
        }
    }

    /**
     * 处理单个邻居方块的提取逻辑，返回更新后的 Absorb 槽位物品
     */
    private static ItemStack processNeighbor(ItemHandlerBlockEntity neighbor, ItemStack currentStored, AbsorbBlockEntity absorb) {
        LazyOptional<IItemHandler> capOptional = neighbor.getCapability(ForgeCapabilities.ITEM_HANDLER, null);
        final ItemStack[] result = {currentStored}; // 用数组做容器，在 Lambda 内更新

        capOptional.ifPresent(neighborHandler -> {
            int slots = neighborHandler.getSlots();
            if (slots <= 0) return;

            int totalExtracted = 0;
            ItemStack targetItem = null;
            boolean rejected = false;

            // 1. 找第一个非空槽位
            int firstNonEmptySlot = -1;
            for (int i = 0; i < slots; i++) {
                ItemStack stack = neighborHandler.getStackInSlot(i);
                if (!stack.isEmpty()) {
                    firstNonEmptySlot = i;
                    targetItem = stack.copy();
                    targetItem.setCount(1);
                    break;
                }
            }
            if (targetItem == null) return;

            // 2. 冲突检查（使用 result[0] 即当前 Absorb 内物品）
            if (!result[0].isEmpty() && !ItemStack.isSameItemSameTags(result[0], targetItem)) {
                return; // 拒绝该邻居
            }

            // 3. 遍历槽位提取
            for (int i = firstNonEmptySlot; i < slots; i++) {
                ItemStack stack = neighborHandler.getStackInSlot(i);
                if (stack.isEmpty()) continue;

                if (!ItemStack.isSameItemSameTags(stack, targetItem)) {
                    break;
                }

                int maxCanTake = 64 - totalExtracted;
                if (maxCanTake <= 0) break;

                int toTake = Math.min(stack.getCount(), maxCanTake);
                ItemStack extracted = neighborHandler.extractItem(i, toTake, false);
                if (extracted.isEmpty()) continue;

                ItemStack remainder = absorb.itemHandler.insertItem(0, extracted, false);
                if (!remainder.isEmpty()) {
                    // 回退未插入部分
                    neighborHandler.insertItem(i, remainder, false);
                    int actuallyInserted = extracted.getCount() - remainder.getCount();
                    totalExtracted += actuallyInserted;
                    break;
                } else {
                    totalExtracted += extracted.getCount();
                }

                if (totalExtracted >= 64) break;
            }

            // 4. 如果成功提取了物品，更新 result[0] 为最新的 Absorb 物品
            if (totalExtracted > 0) {
                result[0] = absorb.itemHandler.getStackInSlot(0);
            }
        });

        return result[0];
    }

    private void find(Level level, BlockPos pos) {
        int ux = pos.getX();
        int uy = pos.getY();
        int uz = pos.getZ();
        map.clear();
        for (int i = 0; i < 6; i++) {
            int vx = ux + dx3[i];
            int vy = uy + dy3[i];
            int vz = uz + dz3[i];
            BlockPos vPos = new BlockPos(vx, vy, vz);
            BlockEntity entity = level.getBlockEntity(vPos);
            if (entity instanceof IMachine && entity instanceof ItemHandlerBlockEntity blockEntity) {
                map.put(vPos, blockEntity);
            }
        }
    }

    public AbsorbWorkModel getModel() {
        return model;
    }

    public void setModel(AbsorbWorkModel model) {
        this.model = model;
        setChanged();
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag); // 保存父类数据（物品栏等）
        // 保存工作模式，使用枚举名称
        tag.putString("WorkModel", model.name());
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag); // 加载父类数据
        if (tag.contains("WorkModel")) {
            String modelName = tag.getString("WorkModel");
            try {
                this.model = AbsorbWorkModel.valueOf(modelName);
            } catch (IllegalArgumentException e) {
                // 如果读取到未知名称，使用默认值 HINDERING
                this.model = AbsorbWorkModel.HINDERING;
            }
        } else {
            // 如果没有该字段，保持默认值
            this.model = AbsorbWorkModel.HINDERING;
        }
    }

    // AE2

    // ========== IInWorldGridNodeHost 实现 ==========

    @Nullable
    @Override
    public IGridNode getGridNode(Direction dir) {
        return mainNode.getNode();
    }

    @Override
    public AECableType getCableConnectionType(Direction dir) {
        return AECableType.SMART;
    }

    // ========== IStorageProvider ==========

    @Override
    public void mountInventories(IStorageMounts mounts) {
        mounts.mount(new AbsorbInventoryAdapter(this), 0);
    }
    // ========== 主动推送方法 ==========

//    public void pushStoredItemToNetwork() {
//        Main.LOGGER.info("[Absorb] pushStoredItemToNetwork called, current stored: {}", getStoredItem());
//        if (level == null || level.isClientSide()) {return;}
//        // 关键：仅当节点已连接到物理网络时才进行推送
//        if (!mainNode.isOnline()) {
//            Main.LOGGER.debug("[Absorb] Not online, skipping push");
//            return;
//        }
//        ItemStack stored = itemHandler.getStackInSlot(0);
//        if (stored.isEmpty()) {
//            Main.LOGGER.info("[Absorb] pushStoredItemToNetwork: stored is empty, returning");
//            return;
//        }
//        IGridNode node = mainNode.getNode();
//        if (node == null) {
//            Main.LOGGER.info("[Absorb] pushStoredItemToNetwork: node is null, returning");
//            return;
//        }
//        MEStorage storage = node.getGrid().getStorageService().getInventory();
//        if (storage == null) {
//            Main.LOGGER.info("[Absorb] pushStoredItemToNetwork: storage is null, returning");
//            return;
//        }
//        AEItemKey what = AEItemKey.of(stored);
//        if (what == null) {
//            Main.LOGGER.info("[Absorb] pushStoredItemToNetwork: what is null, returning");
//            return;
//        }
//        IActionSource source = IActionSource.ofMachine(this);
//        long inserted = storage.insert(what, stored.getCount(), Actionable.MODULATE, source);
//        Main.LOGGER.info("[Absorb] pushStoredItemToNetwork: inserted = {}", inserted);
//        if (inserted > 0) {
//            int remaining = stored.getCount() - (int) inserted;
//            Main.LOGGER.info("[Absorb] pushStoredItemToNetwork: remaining = {}", remaining);
//            if (remaining <= 0) {
//                itemHandler.setStackInSlot(0, ItemStack.EMPTY);
//            } else {
//                ItemStack newStack = stored.copy();
//                newStack.setCount(remaining);
//                itemHandler.setStackInSlot(0, newStack);
//            }
//            setChanged();
//        }
//    }

    @Override
    public void setStoredItem(ItemStack itemStack) {
        super.setStoredItem(itemStack);
    }

    @Override
    public IGridNode getActionableNode() {
        return mainNode.getNode();
    }

    // ========== 生命周期 ==========

    @Override
    public void onLoad() {
        super.onLoad();
        mainNode.create(level, worldPosition);
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        mainNode.destroy();
    }

    // ========== Capability 暴露（供 AE2 发现） ==========

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == IN_WORLD_GRID_NODE_HOST_CAP) {
            return LazyOptional.of(() -> this).cast();
        }
        return super.getCapability(cap, side);
    }

    // ========== IGridNodeListener 实现 ==========

    @Override
    public void onSaveChanges(AbsorbBlockEntity nodeOwner, IGridNode node) {
        // 节点状态变化时保存
        setChanged();
    }

    @Override
    public void onStateChanged(AbsorbBlockEntity nodeOwner, IGridNode node, State state) {
        // 节点状态变化时的处理（可选）
    }

    @Override
    public void onGridChanged(AbsorbBlockEntity nodeOwner, IGridNode node) {
        // 网格变化时的处理（可选）
    }

    @Override
    public void onOwnerChanged(AbsorbBlockEntity nodeOwner, IGridNode node) {
        // 所有者变化时的处理（可选）
    }

    @Override
    public void onInWorldConnectionChanged(AbsorbBlockEntity nodeOwner, IGridNode node) {
        // 连接变化时的处理（可选）
    }
}
