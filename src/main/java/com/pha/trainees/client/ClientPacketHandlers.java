package com.pha.trainees.client;

import com.pha.trainees.client.gui.AnalyzerScreen;
import com.pha.trainees.client.gui.ReactionGraphScreen;
import com.pha.trainees.network.AnalyzerReportPacket;
import net.minecraft.client.Minecraft;

import java.util.List;

/**
 * 客户端专用数据包处理（§19.21）。
 *
 * <p><b>为什么单独抽一个类</b>：数据包类会被**双端**加载；如果数据包类的字节码里直接引用
 * 客户端类（`Screen` / `Minecraft`），专用服务器在注册数据包时就会尝试加载它们，
 * 触发 `Attempted to load class net/minecraft/client/gui/screens/Screen for invalid dist DEDICATED_SERVER`
 * （模组在服务器上直接加载失败）。把客户端代码集中到这里、并**只在 Dist.CLIENT 的延迟 lambda 里调用**，
 * 服务器端就永远不会加载本类。</p>
 */
public final class ClientPacketHandlers {

    private ClientPacketHandlers() {}

    public static void openReactionGraph(List<String> nodes, List<String> edges) {
        Minecraft.getInstance().setScreen(new ReactionGraphScreen(nodes, edges));
    }

    public static void openAnalyzer(AnalyzerReportPacket report) {
        Minecraft.getInstance().setScreen(new AnalyzerScreen(report));
    }
}
