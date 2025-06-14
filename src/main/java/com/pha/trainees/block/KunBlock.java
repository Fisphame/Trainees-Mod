//package com.pha.trainees.block;
//
//import com.pha.trainees.item.KunPickaxeItem;
//import net.minecraft.world.entity.player.Player;
//import net.minecraft.world.item.ItemStack;
//import net.minecraft.world.level.block.Block;
//import net.minecraft.world.level.block.state.BlockState;
//
//public class KunBlock extends Block {
//    public void Kunblock(Properties properties) {
//        super(properties.requiresCorrectToolForDrops());
//    }
//
//
//
//    public boolean canHarvestBlock(BlockState state, net.minecraft.world.level.LevelReader level,
//                                   net.minecraft.core.BlockPos pos, Player player) {
//        ItemStack stack = player.getMainHandItem();
//        // 检查玩家手持的是否是KunPickaxe
//        return stack.getItem() instanceof KunPickaxeItem;
//
//    }
//}