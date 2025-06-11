package com.pha.trainees.util;


import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CooldownTracker {
        private static final Map<UUID, Integer> cooldowns = new HashMap<>();

        public static void setCooldown(Player player, int ticks) {
            cooldowns.put(player.getUUID(), ticks);
        }

        public static boolean isOnCooldown(Player player) {
            return cooldowns.containsKey(player.getUUID());
        }

        @SubscribeEvent
        public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
            if (event.phase != TickEvent.Phase.END) return;
            UUID uuid = event.player.getUUID();
            if (cooldowns.containsKey(uuid)) {
                int remaining = cooldowns.get(uuid) - 1;
                if (remaining <= 0) {
                    cooldowns.remove(uuid);
                } else {
                    cooldowns.put(uuid, remaining);
                }
            }
        }
    }

