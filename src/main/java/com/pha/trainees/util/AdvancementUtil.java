package com.pha.trainees.util;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

public class AdvancementUtil {
    public static boolean hasPlayerAchievement(ServerPlayer player, ResourceLocation advancementId) {
        MinecraftServer server = player.server;
        Advancement advancement = server.getAdvancements().getAdvancement(advancementId);

        if (advancement != null) {
            AdvancementProgress progress = player.getAdvancements().getOrStartProgress(advancement);
            return progress.isDone();
        }
        return false;
    }
}