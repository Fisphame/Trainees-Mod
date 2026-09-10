package com.pha.trainees.network;

import com.pha.trainees.Main;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

/**
 * 模组网络通道（简单网络，用于反应图可视化等客户端展示）
 */
public class ModNetwork {
    private static final String PROTOCOL_VERSION = "1";
    private static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(Main.MODID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals);

    public static void register() {
        int id = 0;
        CHANNEL.registerMessage(id++, OpenGraphPacket.class,
                OpenGraphPacket::encode, OpenGraphPacket::decode, OpenGraphPacket::handle);
        CHANNEL.registerMessage(id++, AnalyzerReportPacket.class,
                AnalyzerReportPacket::encode, AnalyzerReportPacket::decode, AnalyzerReportPacket::handle);
    }

    public static SimpleChannel get() {
        return CHANNEL;
    }
}
