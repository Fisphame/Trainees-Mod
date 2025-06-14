//package com.pha.trainees.event;
//
//import com.pha.trainees.Main;
//import com.pha.trainees.item.KunPickaxeFinal;
//import com.pha.trainees.registry.ModBlocks;
//import com.pha.trainees.util.AdvancementUtil;
//import net.minecraft.core.BlockPos;
//import net.minecraft.network.chat.Component;
//import net.minecraft.resources.ResourceLocation;
//import net.minecraft.server.level.ServerPlayer;
//import net.minecraft.world.entity.player.Player;
//import net.minecraft.world.item.ItemStack;
//import net.minecraft.world.level.Level;
//import net.minecraft.world.level.block.state.BlockState;
//import net.minecraftforge.common.ForgeHooks;
//import net.minecraftforge.event.entity.player.PlayerEvent;
//import net.minecraftforge.event.level.BlockEvent;
//import net.minecraftforge.eventbus.api.SubscribeEvent;
//import net.minecraftforge.fml.common.Mod;
//
//import java.util.HashMap;
//import java.util.Map;
//import java.util.Optional;
//
//
//
// @Mod.EventBusSubscriber(modid = Main.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
//
//
// public class ServerEvents {
//     private static final ResourceLocation MYBLOCK_ADVANCEMENT =
//             new ResourceLocation("trainees", "items/myblock");
//     static final Map<Player, Float> playerSpeedFactors = new HashMap<>();
//
//
//     // 处理挖掘速度变化
//     @SubscribeEvent
//     public static void onBreakSpeed(PlayerEvent.BreakSpeed event) {
//         Player player = event.getEntity();
//         ItemStack heldItem = player.getMainHandItem();
//         Optional<BlockPos> posOptional = event.getPosition();
//         BlockState state = event.getState();
//         if (!posOptional.isPresent()) return;
//         BlockPos pos = posOptional.get();
//
//         // 获取玩家所在的世界
//         Level level = player.level();
//         // 1. 检查是否使用终极镐
//         if (!(heldItem.getItem() instanceof KunPickaxeFinal)) {
//             playerSpeedFactors.remove(player); // 移除状态缓存
//             return;
//         }
//         // 2. 检查是否是myblock方块
//         if (state.getBlock() != ModBlocks.myblock.get()) {
//             playerSpeedFactors.remove(player); // 移除状态缓存
//             return;
//         }
//         // 3. 检查玩家是否能够挖掘这个方块
//         if (!ForgeHooks.isCorrectToolForDrops(state, player)) {
//             playerSpeedFactors.remove(player); // 移除状态缓存
//             return;
//         }
//         // 4. 确保是服务端玩家
//         if (!(player instanceof ServerPlayer serverPlayer)) return;
//         // 5. 根据成就状态调整速度
//         if (AdvancementUtil.hasPlayerAchievement(serverPlayer, MYBLOCK_ADVANCEMENT)) {
//             event.setNewSpeed(event.getNewSpeed() * 4.0f); // 速度加倍
//             serverPlayer.displayClientMessage(Component.literal("这个成就已完成"), true);
//             playerSpeedFactors.put(player, 4.0f); // 存储速度因子
//         }else{
//             event.setNewSpeed(event.getNewSpeed() * 0.5f); // 速度减半
//             serverPlayer.displayClientMessage(Component.translatable("message.trainees.mining_myblock"), true);
//             playerSpeedFactors.put(player, 0.5f); // 存储速度因子
//         }
//     }
//     // 添加方块破坏事件处理
//     @SubscribeEvent
//     public static void onBlockBreak(net.minecraftforge.event.level.BlockEvent.BreakEvent event) {
//         Player player = event.getPlayer();
//         playerSpeedFactors.remove(player); // 方块破坏后清除状态
//     }
//
//     // 添加玩家登出事件处理
//     @SubscribeEvent
//     public static void onPlayerLogout(net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedOutEvent event) {
//         Player player = event.getEntity();
//         playerSpeedFactors.remove(player); // 玩家退出时清除状态
//     }
// }