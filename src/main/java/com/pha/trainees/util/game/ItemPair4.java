package com.pha.trainees.util.game;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class ItemPair4 {
    private static Item item1;
    private static Item item2;
    private static Item item3;
    private static Item item4;
    private static ItemStack re;

    public ItemPair4(Item item1, Item item2, Item item3, Item item4, ItemStack re){
        ItemPair4.item1 = item1;
        ItemPair4.item2 = item2;
        ItemPair4.item3 = item3;
        ItemPair4.item4 = item4;
        ItemPair4.re = re;
    }


    @Override
    public String toString(){
        return String.format("%s & %s & %s & %s", item1.toString(), item2.toString(), item3.toString(), item4.toString());
    }

    // 获取规范化表示：排序后的列表，null放在最后
    public List<Item> normalized() {
        List<Item> items = Arrays.asList(item1, item2, item3, item4);
        items.sort(Comparator.nullsLast(Comparator.comparingInt(System::identityHashCode)));
        return items;
    }

    // 判断输入是否匹配此物品对（忽略顺序）
    public boolean matches(Item input1, Item input2, Item input3, Item input4) {
        // 规范化输入
        List<Item> inputItems = Arrays.asList(input1, input2, input3, input4);
        inputItems.sort(Comparator.nullsLast(Comparator.comparingInt(System::identityHashCode)));

        List<Item> myItems = normalized();

        // 逐个比较
        for (int i = 0; i < 4; i++) {
            if (inputItems.get(i) != myItems.get(i)) {
                return false;
            }
        }
        return true;
    }

    public Item getItem1(){
        return item1;
    }
    public Item getItem2(){
        return item2;
    }
    public Item getItem3(){
        return item3;
    }
    public Item getItem4(){
        return item4;
    }
    public ItemStack getReItem(){
        return re;
    }

}
