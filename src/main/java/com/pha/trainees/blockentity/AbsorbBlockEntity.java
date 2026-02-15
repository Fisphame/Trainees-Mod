package com.pha.trainees.blockentity;

import com.pha.trainees.registry.ModBlocks;
import com.pha.trainees.registry.ModItems;
import com.pha.trainees.util.game.Tools;
import com.pha.trainees.util.interfaces.Machine;
import com.pha.trainees.util.interfaces.Traversal;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

public class AbsorbBlockEntity extends ItemHandlerBlockEntity implements Traversal, Machine {
    public AbsorbBlockEntity(BlockEntityType<?> p_155228_, BlockPos p_155229_, BlockState p_155230_) {
        super(p_155228_, p_155229_, p_155230_);
    }

    @Override
    public int getLimit() {
        return Integer.MAX_VALUE;
    }


    private final Map<BlockPos, KunAltarBlockEntity> map = new HashMap<>();
    private int timer = 0;
    private static final int COOLDOWN_TICK = 8;

    public AbsorbBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlocks.ModBlockEntities.ABSORB_BLOCK_ENTITY.get(), pos, state);
    }


    // 处理玩家交互
    public InteractionResult handleInteraction(Player player, InteractionHand hand) {
        ItemStack heldItem = player.getItemInHand(hand);
        ItemStack storedItem = itemHandler.getStackInSlot(0);

        if (heldItem.getItem() == ModItems.STONE_STICK.get()) {
            player.displayClientMessage(
                    Component.literal(
                            MessageFormat.format("item : {0}, count : {1}", storedItem.getItem().toString(), storedItem.getCount()))
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

        if (!storedItem.isEmpty()) {
            // 如果方块中已有物品，将其掉落
            dropStoredItem();
            if (level != null) {
                Tools.Particle.send(
                        level, ParticleTypes.SOUL_FIRE_FLAME, worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(),
                        5, 0.3, 0.3, 0.3, 0.1
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
        // 红石信号
        if (level.getBestNeighborSignal(blockPos) > 0) return;
        absorb.find(level, blockPos);

        for (Map.Entry<BlockPos, KunAltarBlockEntity> entry : absorb.map.entrySet()) {
            KunAltarBlockEntity altar = entry.getValue();
            if (altar.hasStoredItem()) {
                ItemStack alStack = altar.getStoredItem();
                int alCount = alStack.getCount();
                if (absorb.hasStoredItem()){
                    ItemStack abStack = absorb.getStoredItem();
                    int abCount = abStack.getCount();
                    if (ItemStack.isSameItemSameTags(alStack, abStack)) {
                        altar.clearStoredItem();
                        long total = (long)abCount + alCount;
                        if (total > Integer.MAX_VALUE) {
                            abStack.setCount(Integer.MAX_VALUE);
                        } else {
                            abStack.setCount((int)total);
                        }
                        absorb.setStoredItem(abStack);
                    }
                }
                else {
                    altar.clearStoredItem();
                    absorb.setStoredItem(alStack);
                }
            }
        }
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
            if (entity instanceof KunAltarBlockEntity altar) {
                map.put(vPos, altar);
            }
        }
    }
}
