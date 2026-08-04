package com.pha.trainees.blockentity;

import appeng.api.config.Actionable;
import appeng.api.networking.security.IActionSource;
import appeng.api.storage.MEStorage;
import appeng.api.stacks.AEKey;
import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.KeyCounter;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;

public class AbsorbInventoryAdapter implements MEStorage {

    private final AbsorbBlockEntity absorb;

    public AbsorbInventoryAdapter(AbsorbBlockEntity absorb) {
        this.absorb = absorb;
    }

    // ===== 必须实现的抽象方法 =====
    @Override
    public Component getDescription() {
        // 显示在 AE2 终端中的名称（可本地化）
        return Component.literal("Absorb Block Storage");
    }

    // ===== 重写 insert/extract（基类默认返回 0） =====
    @Override
    public long insert(@Nonnull AEKey what, long amount, @Nonnull Actionable mode, @Nonnull IActionSource source) {
        // 只处理物品类型
        if (!(what instanceof AEItemKey itemKey)) return 0;

        ItemStack stored = absorb.getStoredItem();
        ItemStack toInsert = itemKey.toStack((int) amount);

        // 如果已有物品且不匹配，拒绝插入
        if (!stored.isEmpty() && !ItemStack.isSameItemSameTags(stored, toInsert)) {
            return 0;
        }

        // 模拟模式：直接返回可插入数量
        if (mode == Actionable.SIMULATE) {
            return amount;
        }

        // 实际插入
        if (stored.isEmpty()) {
            absorb.setStoredItem(toInsert);
        } else {
            int newCount = stored.getCount() + (int) amount;
            // 防止溢出（Integer.MAX_VALUE 是上限）
            if (newCount < 0) newCount = Integer.MAX_VALUE;
            ItemStack newStack = stored.copy();
            newStack.setCount(Math.min(newCount, Integer.MAX_VALUE));
            absorb.setStoredItem(newStack);
        }

        return amount;
    }

    @Override
    public long extract(@Nonnull AEKey what, long amount, @Nonnull Actionable mode, @Nonnull IActionSource source) {
        if (!(what instanceof AEItemKey itemKey)) return 0;

        ItemStack stored = absorb.getStoredItem();
        if (stored.isEmpty()) return 0;

        AEItemKey storedKey = AEItemKey.of(stored);
        if (storedKey == null || !storedKey.equals(itemKey)) {
            return 0;
        }

        long extractable = Math.min(amount, stored.getCount());

        if (mode == Actionable.SIMULATE) {
            return extractable;
        }

        int toExtract = (int) extractable;
        ItemStack newStack = stored.copy();
        newStack.shrink(toExtract);

        if (newStack.isEmpty()) {
            absorb.clearStoredItem();
        } else {
            absorb.setStoredItem(newStack);
        }

        return extractable;
    }

    // ===== 重写 getAvailableStacks(KeyCounter)（基类有默认实现，但我们需要提供内容） =====
    @Override
    public void getAvailableStacks(KeyCounter out) {
        ItemStack stored = absorb.getStoredItem();
        if (!stored.isEmpty()) {
            AEItemKey key = AEItemKey.of(stored);
            if (key != null) {
                out.add(key, stored.getCount());
            }
        }
    }

    // 如果只实现 getAvailableStacks(KeyCounter)，则 getAvailableStacks() 无参方法会自动调用它，无需重写。
}