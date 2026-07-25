package com.pha.trainees.blockentity;

import com.pha.trainees.Main;
import com.pha.trainees.registry.ModBlocks;
import com.pha.trainees.registry.ModItems;
import com.pha.trainees.util.game.enums.AbsorbWorkModel;
import com.pha.trainees.util.game.Tools;
import com.pha.trainees.util.interfaces.IMachine;
import com.pha.trainees.util.interfaces.ITraversal;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
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
import net.minecraft.world.level.block.state.BlockState;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

public class AbsorbBlockEntity extends ItemHandlerBlockEntity implements ITraversal, IMachine {
    public AbsorbBlockEntity(BlockEntityType<?> p_155228_, BlockPos p_155229_, BlockState p_155230_) {
        super(p_155228_, p_155229_, p_155230_);
    }



    @Override
    public int getLimit() {
        return Integer.MAX_VALUE;
    }


    private final Map<BlockPos, ItemHandlerBlockEntity> map = new HashMap<>();
    private int timer = 0;
    private static final int COOLDOWN_TICK = 8;
    private AbsorbWorkModel model = AbsorbWorkModel.HINDERING;

    public AbsorbBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlocks.ModBlockEntities.ABSORB_BLOCK_ENTITY.get(), pos, state);
    }


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
                Tools.Particle.send(
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
        // 红石信号
        if (level.getBestNeighborSignal(blockPos) > 0) return;
        absorb.find(level, blockPos);

        for (Map.Entry<BlockPos, ItemHandlerBlockEntity> entry : absorb.map.entrySet()) {
            ItemHandlerBlockEntity vEntity = entry.getValue();
            if (vEntity.hasStoredItem()) {
                ItemStack vStack = vEntity.getStoredItem();
                int vCount = vStack.getCount();
                if (absorb.hasStoredItem()){
                    ItemStack abStack = absorb.getStoredItem();
                    int abCount = abStack.getCount();
                    if (ItemStack.isSameItemSameTags(vStack, abStack)) {
                        vEntity.clearStoredItem();
                        long total = (long)abCount + vCount;
                        if (total > Integer.MAX_VALUE) {
                            abStack.setCount(Integer.MAX_VALUE);
                        } else {
                            abStack.setCount((int)total);
                        }
                        absorb.setStoredItem(abStack);
                    }
                }
                else {
                    vEntity.clearStoredItem();
                    absorb.setStoredItem(vStack);
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
}
