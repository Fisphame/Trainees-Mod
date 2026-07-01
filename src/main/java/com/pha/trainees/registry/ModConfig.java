package com.pha.trainees.registry;

import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class ModConfig {
    public static class Common {
        public final ForgeConfigSpec.ConfigValue<String> deepSeekApiKey;

        Common(ForgeConfigSpec.Builder builder) {
            builder.comment("DeepSeek API Settings").push("deepseek");
            deepSeekApiKey = builder
                    .comment("Your DeepSeek API Key (get from platform.deepseek.com)")
                    .define("apiKey", "");
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