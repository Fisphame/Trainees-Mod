//// src/main/java/com/pha/trainees/event/ClientEvents.java
//package com.pha.trainees.event;
//
//import com.pha.trainees.Main;
//import com.pha.trainees.registry.ModBlocks;
//import net.minecraft.client.Minecraft;
//import net.minecraft.client.multiplayer.ClientLevel;
//import net.minecraft.client.player.LocalPlayer;
//import net.minecraft.core.BlockPos;
//import net.minecraft.world.entity.player.Player;
//import net.minecraft.world.level.block.state.BlockState;
//import net.minecraft.world.phys.BlockHitResult;
//import net.minecraft.world.phys.HitResult;
//import net.minecraftforge.api.distmarker.Dist;
//import net.minecraftforge.client.event.RenderLevelStageEvent;
//import net.minecraftforge.eventbus.api.SubscribeEvent;
//import net.minecraftforge.fml.common.Mod;
//
//@Mod.EventBusSubscriber(modid = Main.MODID, value = Dist.CLIENT)
//public class ClientEvents {
//
//    // 上次动画同步时间
//    private static long lastSyncTime = 0;
//
//    @SubscribeEvent
//    public static void onRenderLevel(RenderLevelStageEvent event) {
//        // 每秒同步一次（避免频繁操作）
//        if (System.currentTimeMillis() - lastSyncTime < 100) return;
//        lastSyncTime = System.currentTimeMillis();
//
//        Minecraft mc = Minecraft.getInstance();
//        LocalPlayer player = mc.player;
//        if (player == null) return;
//
//        // 获取玩家当前瞄准的方块位置
//        HitResult hitResult = mc.hitResult;
//        if (hitResult == null || hitResult.getType() != HitResult.Type.BLOCK) return;
//        BlockPos breakingPos = ((BlockHitResult) hitResult).getBlockPos();
//        ClientLevel level = mc.level;
//        if (level == null) return;
//
//        BlockState state = level.getBlockState(breakingPos);
//
//        // 检查是否是myblock方块
//        if (state.getBlock() != ModBlocks.myblock.get()) return;
//
//        // 添加边界检查
//        if (player.isCreative() || player.isSpectator()) return;
//
//        // 确保玩家正在挖掘
//        if (!mc.options.keyAttack.isDown()) return;
//
//        // 同步动画进度
//        syncMiningAnimation(player, breakingPos);
//    }
//
//    private static void syncMiningAnimation(Player player, BlockPos pos) {
//        // 获取服务端计算的速度因子
//        Float speedFactor = ServerEvents.playerSpeedFactors.get(player);
//        if (speedFactor == null) return;
//
//        // 根据速度因子调整客户端动画
//        if (speedFactor > 1.0f) {
//            // 加速动画 - 直接修改挖掘进度
//            player.level().destroyBlockProgress(player.getId(), pos, (int)(10 * speedFactor));
//        } else if (speedFactor < 1.0f) {
//            // 减速动画 - 降低挖掘进度
//            player.level().destroyBlockProgress(player.getId(), pos, (int)(10 * speedFactor));
//        }
//    }
//
//
//
//}