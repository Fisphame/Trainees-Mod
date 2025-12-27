package com.pha.trainees.event;



import com.pha.trainees.item.KunCourseItem;
import com.pha.trainees.item.LongCourseItem;
import com.pha.trainees.way.game.Tools;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;


public class MiningSoundEvents {


    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        Player player = event.getPlayer();
        if (player == null || player.isCreative()) return;

        // 检查玩家是否使用 RealPickaxe类镐子
        ItemStack heldItem = player.getMainHandItem();
        if (heldItem.getItem() instanceof LongCourseItem.RealPickaxeItem){
            if (!player.level().isClientSide) {
                // 随机选择音效
                SoundEvent sound = Tools.getIndexSound(Tools.MINING_SOUNDS, player);
                player.level().playSound(
                        null, event.getPos(),
                        sound, SoundSource.PLAYERS,
                        0.8F, 1.0F
                );
            }
        }
        else if (heldItem.getItem() instanceof KunCourseItem.KunPickaxeFinal) {
            if (!player.level().isClientSide) {
                SoundEvent sound = Tools.getIndexSound(Tools.FINAL_MINING_SOUND, player);
                player.level().playSound(
                        null, event.getPos(),
                        sound, SoundSource.PLAYERS,
                        0.8F, 1.0F + (player.getRandom().nextFloat() - 0.5F) * 0.2F // 随机音调变化
                );
            }



        }
    }
}
