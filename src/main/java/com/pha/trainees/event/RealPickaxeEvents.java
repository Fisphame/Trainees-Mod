package com.pha.trainees.event;


import com.pha.trainees.item.LongCourseItem;
import com.pha.trainees.registry.ModItems;
import com.pha.trainees.registry.ModSounds;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
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

        // 检查玩家是否使用 RealPickaxe类镐子
        ItemStack heldItem = player.getMainHandItem();
        if (heldItem.getItem() instanceof LongCourseItem.RealPickaxeItem){
            if (!player.level().isClientSide) {
                // 随机选择音效
                int index = player.getRandom().nextInt(MINING_SOUNDS.size());
                SoundEvent sound = MINING_SOUNDS.get(index);
                player.level().playSound(
                        null,
                        player.getX(), player.getY(), player.getZ(),
                        sound,
                        SoundSource.PLAYERS,
                        1.0f, 1.0f
                );

            }
        }
    }
}
