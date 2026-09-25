package com.pha.trainees.registry;

import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class ModConfig {
    public static class Common {
        public final ForgeConfigSpec.ConfigValue<String> deepSeekApiKey;

        // ---- 汲取方块（§19.21 主动推送） ----
        public final ForgeConfigSpec.BooleanValue absorbPushEnabled;
        public final ForgeConfigSpec.IntValue absorbPushInterval;
        public final ForgeConfigSpec.IntValue absorbPushBatchSize;
        public final ForgeConfigSpec.BooleanValue absorbNeighborPush;

        // ---- 打包机（§19.18 / §19.25 自动化） ----
        public final ForgeConfigSpec.IntValue packerEnergyPerBottle;
        public final ForgeConfigSpec.IntValue packerEnergyCapacity;
        public final ForgeConfigSpec.IntValue packerCheckInterval;

        Common(ForgeConfigSpec.Builder builder) {
            builder.comment("DeepSeek API Settings").push("deepseek");
            deepSeekApiKey = builder
                    .comment("Your DeepSeek API Key (get from platform.deepseek.com)")
                    .define("apiKey", "");
            builder.pop();

            builder.comment("Absorb Block（汲取方块）：主动把内容物弹回 ME 网络/邻接容器，替代输出总线轮询")
                    .push("absorb");
            absorbPushEnabled = builder
                    .comment("启用主动推送（关闭则退回旧行为：只由外部总线抽取）")
                    .define("pushEnabled", true);
            absorbPushInterval = builder
                    .comment("推送周期（tick）：每隔这么多 tick 尝试弹出一批")
                    .defineInRange("pushInterval", 8, 1, 200);
            absorbPushBatchSize = builder
                    .comment("单次推送上限（个/批）")
                    .defineInRange("pushBatchSize", 64, 1, 4096);
            absorbNeighborPush = builder
                    .comment("ME 网络不可用（离线/无电）时，回退推送到邻接容器",
                            "为避免回环，推送目标会排除本模组机器与其他汲取方块")
                    .define("neighborPush", true);
            builder.pop();

            // ---- 打包机（§19.18 / §19.25 自动化：自驱 + FE 门槛） ----
            builder.comment("Packer（打包机）：自动质检灌装。1 FE = 1 J（与电解槽同口径）")
                    .push("packer");
            packerEnergyPerBottle = builder
                    .comment("每瓶电费（FE）。默认 20000 FE = 20 kJ（质检 + 灌装的机械功，远低于电解的 ΔG 量级）")
                    .defineInRange("energyPerBottle", 20000, 0, 100_000_000);
            packerEnergyCapacity = builder
                    .comment("内部电缓冲上限（FE）")
                    .defineInRange("energyCapacity", 400000, 1000, 1_000_000_000);
            packerCheckInterval = builder
                    .comment("自驱检查周期（tick）：每隔这么多 tick 尝试打包一次")
                    .defineInRange("checkInterval", 20, 1, 400);
            builder.pop();
        }
    }

    public static final Common COMMON;
    public static final ForgeConfigSpec COMMON_SPEC;

    static {
        final Pair<Common, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(Common::new);
        COMMON_SPEC = specPair.getRight();
        COMMON = specPair.getLeft();
    }
}