package com.pha.trainees.event;

import com.pha.trainees.network.NetworkHandler;
import com.pha.trainees.network.PlaySoundPacket;
import com.pha.trainees.registry.ModItems;
import com.pha.trainees.registry.ModSounds;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.List;

public class RealPickaxeEvents {
    private static final List<SoundEvent> MINING_SOUNDS = List.of(
            ModSounds.MINING_SOUND_1.get(),
            ModSounds.MINING_SOUND_2.get(),
            ModSounds.MINING_SOUND_3.get(),
            ModSounds.MINING_SOUND_4.get(),
            ModSounds.MINING_SOUND_5.get(),
            ModSounds.MINING_SOUND_6.get()
    );

    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        Player player = event.getPlayer();
        if (player == null) return;
        if (player.isCreative()) return;

        // 检查玩家是否使用 real_diamond_pickaxe系列镐子
        ItemStack heldItem = player.getMainHandItem();

        if (       (heldItem.getItem() == ModItems.REAL_DIAMOND_PICKAXE.get())
//                || (heldItem.getItem() == ModItems.LONG_REAL_DIAMOND_PICKAXE.get())
//                || (heldItem.getItem() == ModItems.LONGER_REAL_DIAMOND_PICKAXE.get())
//                || (heldItem.getItem() == ModItems.LONGEST_REAL_DIAMOND_PICKAXE.get())
//                || (heldItem.getItem() == ModItems.THE_LONGEST_REAL_DIAMOND_PICKAXE.get())
//                || (heldItem.getItem() == ModItems.THE_THE_LONGEST_REAL_DIAMOND_PICKAXE.get())
                ){
            // 仅在服务端触发，避免客户端重复播放
            if (!event.getLevel().isClientSide()) {
                // 随机选择音效
                int index = player.getRandom().nextInt(MINING_SOUNDS.size());
                SoundEvent sound = MINING_SOUNDS.get(index);

                // 向客户端发送播放音效的数据包
                NetworkHandler.sendToClient(
                        new PlaySoundPacket(sound, player.getX(), player.getY(), player.getZ()),
                        (ServerPlayer) player
                );
            }
        }
    }
}
