package com.pha.trainees.util.game;

import net.minecraft.world.item.Item;

import java.util.ArrayList;
import java.util.List;
// 物品对管理器
public class ItemPair4Manager {
    private final List<ItemPair4> pairs = new ArrayList<>();

    public void registerPair(ItemPair4 pair) {
        pairs.add(pair);
    }

    // 查找匹配的物品对
    public ItemPair4 findMatchingPair(Item item1, Item item2, Item item3, Item item4) {
        for (ItemPair4 pair : pairs) {
            if (pair.matches(item1, item2, item3, item4)) {
                return pair;
            }
        }
        return null;
    }

    public ItemPair4 findMatchingPair(ItemPair4 pair) {
        return findMatchingPair(pair.getItem1(), pair.getItem2(), pair.getItem3(), pair.getItem4());
    }


    // 批量添加物品对
    public void addPairs(List<ItemPair4> pairsToAdd) {
        pairs.addAll(pairsToAdd);
    }

    // 获取所有物品对
    public List<ItemPair4> getAllPairs() {
        return new ArrayList<>(pairs);
    }
}
