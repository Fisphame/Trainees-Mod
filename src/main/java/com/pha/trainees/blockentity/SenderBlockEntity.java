package com.pha.trainees.blockentity;

import com.pha.trainees.Main;
import com.pha.trainees.item.StoneStickItem;
import com.pha.trainees.multiblock.TrainerAltarPattern;
import com.pha.trainees.registry.ModBlocks;
import com.pha.trainees.util.game.Tools;
import com.pha.trainees.util.game.structure.ActiveStructureManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;

public class SenderBlockEntity extends ItemHandlerBlockEntity {

    // ========== 配置常量 ==========
    private static final int SEND_INTERVAL = 2; // 每 n tick 尝试发送一次
    private static final int SLOT_COUNT = 4;

    // ========== 数据字段 ==========
    @Nullable
    private BlockPos boundCorePos;          // 绑定的核心位置
    private final ItemStackHandler inventory = new ItemStackHandler(SLOT_COUNT) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };
    private int timer = 0;

    // ========== 构造函数 ==========
    public SenderBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlocks.ModBlockEntities.SENDER_BLOCK_ENTITY.get(), pos, state);
    }

    // ========== 核心逻辑 ==========

    private void trySend(Level level) {
        // 1. 检查是否已绑定
        if (boundCorePos == null) return;

        // 2. 检查红石信号（有信号则暂停）
        if (level.hasNeighborSignal(worldPosition)) return;

        // 3. 检查结构是否仍激活
        ActiveStructureManager manager = ActiveStructureManager.get(level);
        if (!manager.isPositionActive(level, TrainerAltarPattern.STRUCTURE_ID, boundCorePos)) {
            // 结构已失效，但仍保留绑定信息，不自动清除
            return;
        }

        // 4. 获取四个祭坛位置（顺序：北、南、西、东 → 对应 matches 参数顺序）
        BlockPos[] altarPositions = getAltarPositions(boundCorePos);
        BlockPos pos = getBlockPos();

        // 5. 遍历槽位，尝试发送
        for (int slot = 0; slot < SLOT_COUNT; slot++) {
            ItemStack stack = inventory.getStackInSlot(slot);
            if (stack.isEmpty()) continue;

            BlockPos altarPos = altarPositions[slot];
            if (!(level.getBlockEntity(altarPos) instanceof KunAltarBlockEntity altar)) continue;

            // 检查祭坛是否为空
            if (!altar.getStoredItem().isEmpty()) continue;

            // 取出 1 个物品
            ItemStack toSend = stack.copy();
            toSend.setCount(1);
            stack.shrink(1);

            // 存入祭坛
            altar.setStoredItem(toSend);
            Tools.Particle.spawnArcParticle(level,
                    Tools.randomInRange(level, 1, 2) == 1 ? ParticleTypes.FLAME : ParticleTypes.SOUL_FIRE_FLAME,
                    Tools.BlockCourse.getCenter(pos),
                    Tools.BlockCourse.getCenter(altarPos),
                    5,
                    5
            );

            // 发送成功，退出循环（防止单 tick 发送多个）
            setChanged();
            return;
        }
    }

    public InteractionResult handleInteraction(Player player, InteractionHand hand) {

        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }

        ItemStack handItem = player.getItemInHand(hand);

        // 检查是否手持普通石棍（StoneStickItem）
        if (handItem.getItem() instanceof StoneStickItem) {
            // 构建显示信息
            StringBuilder message = new StringBuilder();
            message.append("§6[发送方块槽位信息]\n");

            for (int i = 0; i < 4; i++) {
                ItemStack stack = getSlotStack(i);
                String slotName = switch (i) {
                    case 0 -> "北";
                    case 1 -> "南";
                    case 2 -> "西";
                    case 3 -> "东";
                    default -> "未知";
                };
                if (stack.isEmpty()) {
                    message.append("  ").append(slotName).append(": §7空\n");
                } else {
                    String itemId = stack.getItem().getDescriptionId();
                    int count = stack.getCount();
                    message.append("  ").append(slotName).append(": §f")
                            .append(itemId)
                            .append(" §7x§f ").append(count)
                            .append("\n");
                }
            }

            // 发送消息给玩家（多行显示）
            String[] lines = message.toString().split("\n");
            for (String line : lines) {
                player.displayClientMessage(Component.literal(line), false);
            }

            return InteractionResult.SUCCESS;
        }


        return InteractionResult.PASS;
    }

    // ========== 辅助方法 ==========
    private BlockPos[] getAltarPositions(BlockPos corePos) {
        // 顺序：北(z-)、南(z+)、西(x-)、东(x+) → 对应 matches 参数顺序
        return new BlockPos[]{
                corePos.offset(0, 2, -3),  // 北
                corePos.offset(0, 2, 3),   // 南
                corePos.offset(-3, 2, 0),  // 西
                corePos.offset(3, 2, 0)    // 东
        };
    }

    private BlockPos getAltarPosForSlot(BlockPos corePos, int slot) {
        return switch (slot) {
            case 0 -> corePos.offset(0, 2, -3);  // 北 (z-)
            case 1 -> corePos.offset(0, 2, 3);   // 南 (z+)
            case 2 -> corePos.offset(-3, 2, 0);  // 西 (x-)
            case 3 -> corePos.offset(3, 2, 0);   // 东 (x+)
            default -> throw new IllegalArgumentException("Invalid slot: " + slot);
        };
    }

    // ========== 绑定方法 ==========
    public void bindToCore(BlockPos corePos) {
        this.boundCorePos = corePos.immutable();
        setChanged();
    }

    public boolean isBound() {
        return boundCorePos != null;
    }

    @Nullable
    public BlockPos getBoundCorePos() {
        return boundCorePos;
    }

    // ========== 比较器输出 ==========
    public int getComparatorOutput() {
        int nonEmpty = 0;
        for (int i = 0; i < SLOT_COUNT; i++) {
            if (!inventory.getStackInSlot(i).isEmpty()) {
                nonEmpty++;
            }
        }
        return nonEmpty; // 0 ~ 4
    }

    // ========== NBT 序列化 ==========
    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);

        // 保存绑定的核心位置
        if (boundCorePos != null) {
            tag.put("BoundCorePos", NbtUtils.writeBlockPos(boundCorePos));
        }

        // 保存物品栏
        tag.put("Inventory", inventory.serializeNBT());
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);

        // 读取绑定的核心位置
        if (tag.contains("BoundCorePos")) {
            boundCorePos = NbtUtils.readBlockPos(tag.getCompound("BoundCorePos"));
        } else {
            boundCorePos = null;
        }

        // 读取物品栏
        if (tag.contains("Inventory")) {
            inventory.deserializeNBT(tag.getCompound("Inventory"));
        }
    }

    // ========== Capability 暴露（供漏斗等使用） ==========
    private final LazyOptional<IItemHandler> itemHandler = LazyOptional.of(() -> inventory);

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return itemHandler.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        itemHandler.invalidate();
    }

    @Override
    public void reviveCaps() {
        super.reviveCaps();
        itemHandler.invalidate();
        // 重新创建 LazyOptional
    }

    public static <T extends BlockEntity> void tick(Level level, BlockPos blockPos, BlockState state, T t) {
        if (level.isClientSide) return;
        if (!(t instanceof SenderBlockEntity sender)) return;
        if (++sender.timer <= SEND_INTERVAL) return;
        sender.timer = 0;

        // 发送
        sender.trySend(level);
    }

    public ItemStack getSlotStack(int slot) {
        if (slot < 0 || slot >= SLOT_COUNT) {
            return ItemStack.EMPTY;
        }
        return inventory.getStackInSlot(slot);
    }

    @Override
    public int getLimit() {
        return 1;
    }
}