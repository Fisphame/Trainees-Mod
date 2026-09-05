package com.pha.trainees.chemistry.gas;

import com.pha.trainees.Main;
import com.pha.trainees.config.ChemConfig;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.level.ChunkEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * 气体网格生命周期事件（Phase 9）：区块加载/卸载 → LOD 坍缩与水合；服务端 tick → 节流扩散。
 */
@Mod.EventBusSubscriber(modid = Main.MODID)
public class GasGridEvents {

    @SubscribeEvent
    public static void onChunkLoad(ChunkEvent.Load event) {
        if (!(event.getLevel() instanceof ServerLevel level)) return;
        if (event.getChunk() == null) return;
        GasGridManager manager = GasGridManager.get(level);
        manager.bind(level.dimension());
        manager.onChunkLoad(level, event.getChunk().getPos());
    }

    @SubscribeEvent
    public static void onChunkUnload(ChunkEvent.Unload event) {
        if (!(event.getLevel() instanceof ServerLevel level)) return;
        if (event.getChunk() == null) return;
        GasGridManager.get(level).onChunkUnload(level, event.getChunk().getPos());
    }

    /** 服务端 tick：按各维度 gameTime 节流执行扩散（每维度独立节奏） */
    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        MinecraftServer server = event.getServer();
        if (server == null) return;
        int interval = ChemConfig.GAS_DIFFUSION_INTERVAL.get();
        if (interval <= 1) interval = 1;
        for (ServerLevel level : server.getAllLevels()) {
            if (level.getGameTime() % interval != 0) continue;
            GasGridManager manager = GasGridManager.get(level);
            manager.bind(level.dimension());
            manager.tickDiffusion(level);
        }
    }
}
