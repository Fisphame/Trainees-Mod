package com.pha.trainees.event;

import com.pha.trainees.Main;
import com.pha.trainees.util.game.Tools;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.WeakHashMap;

@Mod.EventBusSubscriber(modid = Main.MODID)
public class PlayerTickHandler {
    // 用于跟踪状态变化，避免重复触发
    private static final WeakHashMap<Player, Boolean> armorStateMap = new WeakHashMap<>();
    private static int tick;
    private static final int COOLDOWN_TICKS = 100;

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (++tick < COOLDOWN_TICKS) return;

        Player player = event.player;

        // 检查是否穿着完整套装
        // 获取之前的状态
        boolean isWearingSet = Tools.ArmorChecker.isWearingFullArmorSet(player);
        Boolean wasWearingSet = armorStateMap.get(player);


        // 如果状态发生变化
        if (wasWearingSet == null || wasWearingSet != isWearingSet) {
            armorStateMap.put(player, isWearingSet);

            if (isWearingSet) {
                onFullArmorEquipped(player);
            } else if (wasWearingSet != null) {
                onFullArmorRemoved(player);
            }
        }

        if (wasWearingSet != null && wasWearingSet) {
            triggerFullArmor(player);
            tick = 0;
        }
    }

    private static void onFullArmorEquipped(Player player) {
        player.displayClientMessage(
                Component.literal("--[ ↑ ]--").withStyle(ChatFormatting.GREEN),
                true
        );
        tick = COOLDOWN_TICKS;
    }

    private static void onFullArmorRemoved(Player player) {
        player.displayClientMessage(
                Component.literal("--[-↓-]--").withStyle(ChatFormatting.RED),
                true
        );
        Tools.Particle.send(player.level(), ParticleTypes.SOUL, player.getX(), player.getY(), player.getZ(),
                55, 0, 0, 0, 0.15);
        player.removeEffect(MobEffects.DAMAGE_RESISTANCE);
        player.removeEffect(MobEffects.FIRE_RESISTANCE);
        tick = 0;
    }

    private static void triggerFullArmor(Player player){
        Tools.Particle.send(player.level(), ParticleTypes.LAVA, player.getX(), player.getY(), player.getZ(),
                20, 0, 0, 0, 0.15);
        player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 110, 2));
        player.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 110));
    }

}
