package com.pha.trainees.thetwice;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public record Textview() {
    public static boolean isLifegame;
    public static int SiHittime=0;
    public static boolean SiCanTwiceJump =true;
    public static BlockState getPlayerUnderBlockstate(Player player, Level level, int a){
        return level.getBlockState(BlockPos.containing(player.getX(), player.getY() - a, player.getZ()));
    }
    public static void seedMsg(String s,Player player){
        Component message = Component.literal(s);
        player.sendSystemMessage(message);
    }
    public static boolean PlayerIsOnground(Player player){
        double p = player.getY();
        return (p - (int) p) <= 0.1;
    }
}
