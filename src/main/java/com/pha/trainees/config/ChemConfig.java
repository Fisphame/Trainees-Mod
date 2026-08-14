package com.pha.trainees.config;

import net.minecraftforge.common.ForgeConfigSpec;

import java.util.HashMap;
import java.util.Map;

public class ChemConfig {

//    public static final ForgeConfigSpec COMMON_SPEC;
    public static final ForgeConfigSpec SERVER_SPEC;

    // ============================================================
    // 一、难度选择
    // ============================================================
    public static final ForgeConfigSpec.EnumValue<DifficultyLevel> GAME_DIFFICULTY;

    // ============================================================
    // 二、资源获取难度
    // ============================================================
    public static final ForgeConfigSpec.DoubleValue ORE_VALUABLE_RATIO_MULTIPLIER;
    public static final ForgeConfigSpec.DoubleValue ORE_GANGUE_RATIO_MULTIPLIER;

    // ============================================================
    // 三、污染与环境压力
    // ============================================================
    public static final ForgeConfigSpec.DoubleValue POLLUTION_DIFFUSION_SPEED;
    public static final ForgeConfigSpec.DoubleValue POLLUTION_TOXICITY_THRESHOLD;
    public static final ForgeConfigSpec.DoubleValue POLLUTION_PLANT_GROWTH_PENALTY;

    // ============================================================
    // 四、能源效率
    // ============================================================
    public static final ForgeConfigSpec.DoubleValue ENERGY_CONSUMPTION_MULTIPLIER;
    public static final ForgeConfigSpec.DoubleValue ENERGY_GENERATION_MULTIPLIER;

    // ============================================================
    // 五、严格映射（所有难度保持一致）
    // ============================================================
    public static final ForgeConfigSpec.DoubleValue BUCKET_TO_MOL_WATER;
    public static final ForgeConfigSpec.DoubleValue SOLID_INGOT_TO_MOL;
    public static final ForgeConfigSpec.DoubleValue SOLID_NUGGET_TO_MOL;
    public static final ForgeConfigSpec.DoubleValue MAX_MOLES_PER_COMPONENT;
    public static final ForgeConfigSpec.DoubleValue MAX_TOTAL_MOLES;

    // ============================================================
    // 六、难度指标权重
    // ============================================================
    public static final ForgeConfigSpec.DoubleValue WEIGHT_RESOURCE_GETTING;
    public static final ForgeConfigSpec.DoubleValue WEIGHT_POLLUTION;
    public static final ForgeConfigSpec.DoubleValue WEIGHT_ENERGY;

    // ============================================================
    // 七、固定物理参数（由材料决定，保留用于自定义）
    // ============================================================
    public static final ForgeConfigSpec.DoubleValue DEFAULT_VOLUME;
    public static final ForgeConfigSpec.DoubleValue BEAKER_BASE_HEAT_CAPACITY;
    public static final ForgeConfigSpec.DoubleValue BEAKER_HEAT_TRANSFER_COEFFICIENT;
    public static final ForgeConfigSpec.DoubleValue AMBIENT_HEAT_TRANSFER_COEFFICIENT;
    public static final ForgeConfigSpec.DoubleValue MIN_HEAT_CAPACITY;
    public static final ForgeConfigSpec.DoubleValue DEFAULT_TEMPERATURE;
    public static final ForgeConfigSpec.DoubleValue MAX_SAFE_TEMPERATURE;
    public static final ForgeConfigSpec.DoubleValue CRITICAL_TEMPERATURE_RATIO;
    public static final ForgeConfigSpec.DoubleValue ENVIRONMENT_TEMPERATURE_BASE;
    public static final ForgeConfigSpec.DoubleValue ENVIRONMENT_TEMPERATURE_LAPSE_RATE;

    // ============================================================
    // 八、热源温度（固定）
    // ============================================================
    public static final ForgeConfigSpec.DoubleValue HEAT_SOURCE_FIRE;
    public static final ForgeConfigSpec.DoubleValue HEAT_SOURCE_SOUL_FIRE;
    public static final ForgeConfigSpec.DoubleValue HEAT_SOURCE_LAVA;
    public static final ForgeConfigSpec.DoubleValue HEAT_SOURCE_MAGMA;
    public static final ForgeConfigSpec.DoubleValue HEAT_SOURCE_CAMPFIRE;
    public static final ForgeConfigSpec.DoubleValue HEAT_SOURCE_SOUL_CAMPFIRE;

    // ============================================================
    // 九、反应动力学默认值
    // ============================================================
    public static final ForgeConfigSpec.DoubleValue DEFAULT_ACTIVATION_ENERGY;
    public static final ForgeConfigSpec.DoubleValue DEFAULT_PRE_EXPONENTIAL_FACTOR;
    public static final ForgeConfigSpec.DoubleValue DEFAULT_EQUILIBRIUM_CONSTANT;
    public static final ForgeConfigSpec.DoubleValue DEFAULT_MIN_TEMPERATURE;

    // ============================================================
    // 十、反应优先级计算
    // ============================================================
    public static final ForgeConfigSpec.DoubleValue PRIORITY_EPSILON;
    public static final ForgeConfigSpec.DoubleValue PRIORITY_TEMPERATURE_MODIFIER;
    public static final ForgeConfigSpec.DoubleValue PRIORITY_TEMPERATURE_REFERENCE;

    // ============================================================
    // 十一、烧杯交互
    // ============================================================
    public static final ForgeConfigSpec.DoubleValue BEAKER_DEFAULT_FLUID_ADD_AMOUNT;
    public static final ForgeConfigSpec.DoubleValue BEAKER_DEFAULT_SOLID_ADD_AMOUNT;

    // ============================================================
    // 十二、环境模拟
    // ============================================================
    public static final ForgeConfigSpec.DoubleValue PRESSURE_STANDARD_ATMOSPHERE;

    // ============================================================
    // 十三、电解（理论调试）
    // ============================================================
    public static final ForgeConfigSpec.BooleanValue ELECTROLYZER_FREE_POWER;

    // ============================================================
    // 十四、引擎调度（原 ReactionEngine 硬编码常量，蓝本 §14.3）
    // ============================================================
    public static final ForgeConfigSpec.IntValue ENGINE_POLLING_INTERVAL;
    public static final ForgeConfigSpec.IntValue ENGINE_MAX_RULES_PER_POLL;
    public static final ForgeConfigSpec.IntValue ENGINE_MAX_CHAIN_REACTIONS_PER_TICK;
    public static final ForgeConfigSpec.DoubleValue ENGINE_EPSILON;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

        // ============================================================
        // 一、难度选择
        // ============================================================
        builder.push("difficulty");

        GAME_DIFFICULTY = builder
                .comment("游戏难度档位", "EASY: 简单  |  NORMAL: 普通  |  HARD: 困难  |  CUSTOM: 自定义")
                .defineEnum("gameDifficulty", DifficultyLevel.NORMAL);

        builder.pop();

        // ============================================================
        // 二、资源获取难度
        // ============================================================
        builder.push("resource_getting");

        ORE_VALUABLE_RATIO_MULTIPLIER = builder
                .comment("矿石中有效成分的比例乘数", "简单:1.2  普通:1.0  困难:0.7")
                .defineInRange("oreValuableRatioMultiplier", 1.0, 0.1, 3.0);

        ORE_GANGUE_RATIO_MULTIPLIER = builder
                .comment("矿石中脉石（杂质）的比例乘数", "简单:0.8  普通:1.0  困难:1.3")
                .defineInRange("oreGangueRatioMultiplier", 1.0, 0.1, 3.0);

        builder.pop();

        // ============================================================
        // 三、污染与环境压力
        // ============================================================
        builder.push("pollution");

        POLLUTION_DIFFUSION_SPEED = builder
                .comment("污染物在空气中的扩散速度倍率", "简单:0.5  普通:1.0  困难:2.0")
                .defineInRange("diffusionSpeed", 1.0, 0.0, 10.0);

        POLLUTION_TOXICITY_THRESHOLD = builder
                .comment("毒性触发阈值（分压 atm），值越小越容易中毒", "简单:2.0  普通:1.0  困难:0.5")
                .defineInRange("toxicityThreshold", 1.0, 0.01, 10.0);

        POLLUTION_PLANT_GROWTH_PENALTY = builder
                .comment("污染对植物生长的惩罚系数（0~1），1=完全停止生长")
                .defineInRange("plantGrowthPenalty", 0.5, 0.0, 1.0);

        builder.pop();

        // ============================================================
        // 四、能源效率
        // ============================================================
        builder.push("energy");

        ENERGY_CONSUMPTION_MULTIPLIER = builder
                .comment("机器耗电倍数", "简单:0.7  普通:1.0  困难:1.5")
                .defineInRange("energyConsumptionMultiplier", 1.0, 0.1, 10.0);

        ENERGY_GENERATION_MULTIPLIER = builder
                .comment("机器发电倍数", "简单:1.5  普通:1.0  困难:0.7")
                .defineInRange("energyGenerationMultiplier", 1.0, 0.1, 10.0);

        builder.pop();

        // ============================================================
        // 五、严格映射（所有难度保持一致）
        // ============================================================
        builder.push("strict_mapping");

        BUCKET_TO_MOL_WATER = builder
                .comment("1 桶（1000mB）水对应的摩尔数")
                .defineInRange("bucketToMolWater", 64.0, 0.01, 10000.0);

        SOLID_INGOT_TO_MOL = builder
                .comment("1 个金属锭对应的摩尔数")
                .defineInRange("solidIngotToMol", 1.0, 0.01, 1000.0);

        SOLID_NUGGET_TO_MOL = builder
                .comment("1 个金属粒对应的摩尔数（固定值，9粒=1锭，不受难度影响）")
                .defineInRange("solidNuggetToMol", 1.0 / 9.0, 0.0001, 100.0);

        MAX_MOLES_PER_COMPONENT = builder
                .comment("容器中单一成分的最大摩尔数限制")
                .defineInRange("maxMolesPerComponent", 64.0, 0.1, 10000.0);

        MAX_TOTAL_MOLES = builder
                .comment("容器中所有成分总摩尔数的最大限制")
                .defineInRange("maxTotalMoles", 64.0, 0.1, 100000.0);

        builder.pop();

        // ============================================================
        // 六、难度指标权重
        // ============================================================
        builder.push("difficulty_weights");

        WEIGHT_RESOURCE_GETTING = builder
                .comment("资源获取难度在综合难度指标中的权重")
                .defineInRange("weightResourceGetting", 40.0, 0.0, 100.0);

        WEIGHT_POLLUTION = builder
                .comment("污染压力在综合难度指标中的权重")
                .defineInRange("weightPollution", 35.0, 0.0, 100.0);

        WEIGHT_ENERGY = builder
                .comment("能源效率在综合难度指标中的权重")
                .defineInRange("weightEnergy", 25.0, 0.0, 100.0);

        builder.pop();

        // ============================================================
        // 七、固定物理参数（由材料决定，保留用于自定义）
        // ============================================================
        builder.push("physics");

        DEFAULT_VOLUME = builder
                .comment("容器的默认有效容积（升 L）")
                .defineInRange("defaultVolume", 1.0, 0.01, 1000.0);

        BEAKER_BASE_HEAT_CAPACITY = builder
                .comment("烧杯玻璃本身的基础热容（J/K），不包括内容物")
                .defineInRange("beakerBaseHeatCapacity", 4000.0, 100.0, 100000.0);

        BEAKER_HEAT_TRANSFER_COEFFICIENT = builder
                .comment("烧杯与热源（火/岩浆/篝火等）的换热系数，值越大升温越快", "推荐值 1.5 ~ 3.0")
                .defineInRange("beakerHeatTransferCoefficient", 2.0, 0.1, 20.0);

        AMBIENT_HEAT_TRANSFER_COEFFICIENT = builder
                .comment("烧杯与环境空气的自然换热系数（独立于热源强度，两者解耦）",
                        "值越小热惯量越大、温度越稳定：0.05 时约 20 秒向环境温度趋近",
                        "过强会导致烧杯瞬间弹回环境温度、留不住热")
                .defineInRange("ambientHeatTransferCoefficient", 0.05, 0.001, 10.0);

        MIN_HEAT_CAPACITY = builder
                .comment("容器允许的最小总热容（J/K），防止除零")
                .defineInRange("minHeatCapacity", 10.0, 0.1, 1000.0);

        DEFAULT_TEMPERATURE = builder
                .comment("容器的默认初始温度（开尔文 K），室温约 293K")
                .defineInRange("defaultTemperature", 293.0, 0.0, 10000.0);

        MAX_SAFE_TEMPERATURE = builder
                .comment("容器最高安全温度（开尔文 K），由容器材料决定，此处为默认值")
                .defineInRange("maxSafeTemperature", 1800.0, 0.0, 10000.0);

        CRITICAL_TEMPERATURE_RATIO = builder
                .comment("触发临界温度警告的阈值（占最高安全温度的百分比）")
                .defineInRange("criticalTemperatureRatio", 0.9, 0.0, 1.0);

        ENVIRONMENT_TEMPERATURE_BASE = builder
                .comment("主世界海平面环境温度（开尔文 K），实际由生物群系决定")
                .defineInRange("environmentTemperatureBase", 293.0, 0.0, 1000.0);

        ENVIRONMENT_TEMPERATURE_LAPSE_RATE = builder
                .comment("环境温度随高度递减率（K/100m）")
                .defineInRange("environmentTemperatureLapseRate", 0.6, 0.0, 10.0);

        builder.pop();

        // ============================================================
        // 八、热源温度
        // ============================================================
        builder.push("heat_sources");

        HEAT_SOURCE_FIRE = builder
                .comment("原版火（Fire）的温度（开尔文 K）")
                .defineInRange("fire", 1300.0, 0.0, 10000.0);

        HEAT_SOURCE_SOUL_FIRE = builder
                .comment("原版灵魂火（Soul Fire）的温度（开尔文 K）")
                .defineInRange("soul_fire", 1200.0, 0.0, 10000.0);

        HEAT_SOURCE_LAVA = builder
                .comment("原版岩浆（Lava）的温度（开尔文 K）")
                .defineInRange("lava", 1500.0, 0.0, 10000.0);

        HEAT_SOURCE_MAGMA = builder
                .comment("原版岩浆块（Magma Block）的温度（开尔文 K）")
                .defineInRange("magma", 1300.0, 0.0, 10000.0);

        HEAT_SOURCE_CAMPFIRE = builder
                .comment("原版营火（Campfire）的温度（开尔文 K）")
                .defineInRange("campfire", 800.0, 0.0, 10000.0);

        HEAT_SOURCE_SOUL_CAMPFIRE = builder
                .comment("原版灵魂营火（Soul Campfire）的温度（开尔文 K）")
                .defineInRange("soul_campfire", 750.0, 0.0, 10000.0);

        builder.pop();

        // ============================================================
        // 九、反应动力学默认值
        // ============================================================
        builder.push("reaction_defaults");

        DEFAULT_ACTIVATION_ENERGY = builder
                .comment("未指定活化能时的默认值（kJ/mol）")
                .defineInRange("defaultActivationEnergy", 50.0, 0.0, 10000.0);

        DEFAULT_PRE_EXPONENTIAL_FACTOR = builder
                .comment("未指定指前因子时的默认值（A）")
                .defineInRange("defaultPreExponentialFactor", 1e8, 0.0, 1e20);

        DEFAULT_EQUILIBRIUM_CONSTANT = builder
                .comment("未指定平衡常数时的默认值（K°）")
                .defineInRange("defaultEquilibriumConstant", 1.0, 0.0, 1e20);

        DEFAULT_MIN_TEMPERATURE = builder
                .comment("未指定最低触发温度时的默认值（K）")
                .defineInRange("defaultMinTemperature", 0.0, 0.0, 10000.0);

        builder.pop();

        // ============================================================
        // 十、反应优先级计算
        // ============================================================
        builder.push("priority");

        PRIORITY_EPSILON = builder
                .comment("优先级计算中防止除零的极小值")
                .defineInRange("priorityEpsilon", 0.1, 1e-9, 1.0);

        PRIORITY_TEMPERATURE_MODIFIER = builder
                .comment("优先级中温度修正系数，值越大温度对优先级影响越大")
                .defineInRange("priorityTemperatureModifier", 0.1, 0.0, 10.0);

        PRIORITY_TEMPERATURE_REFERENCE = builder
                .comment("优先级中温度修正的参考温度（K），用于归一化")
                .defineInRange("priorityTemperatureReference", 100.0, 0.0, 10000.0);

        builder.pop();

        // ============================================================
        // 十一、烧杯交互
        // ============================================================
        builder.push("beaker_interaction");

        BEAKER_DEFAULT_FLUID_ADD_AMOUNT = builder
                .comment("烧杯右键倒入流体时每次添加的摩尔数")
                .defineInRange("beakerDefaultFluidAddAmount", 1.0, 0.001, 1000.0);

        BEAKER_DEFAULT_SOLID_ADD_AMOUNT = builder
                .comment("烧杯右键放入固体时每次添加的摩尔数")
                .defineInRange("beakerDefaultSolidAddAmount", 1.0, 0.001, 1000.0);

        builder.pop();

        // ============================================================
        // 十二、环境模拟
        // ============================================================
        builder.push("environment");

        PRESSURE_STANDARD_ATMOSPHERE = builder
                .comment("标准大气压（Pa），用于压强单位换算")
                .defineInRange("standardAtmosphere", 101325.0, 1.0, 1e9);

        builder.pop();

        // ============================================================
        // 十三、电解（理论调试）
        // ============================================================
        builder.push("electrolysis");

        ELECTROLYZER_FREE_POWER = builder
                .comment("电解理论阶段：容器是否视为无限通电（不消耗电能、恒可电解）",
                        "true = 理论调试用，不依赖真实发电；接入能量系统后设为 false")
                .define("electrolyzerFreePower", true);

        builder.pop();

        // ============================================================
        // 十四、引擎调度（原 ReactionEngine 硬编码常量，蓝本 §14.3）
        // ============================================================
        builder.push("engine");

        ENGINE_POLLING_INTERVAL = builder
                .comment("Tick 轮询间隔（每 N Tick 执行一次完整轮询）")
                .defineInRange("pollingInterval", 5, 1, 100);

        ENGINE_MAX_RULES_PER_POLL = builder
                .comment("单次轮询最多执行的规则数量")
                .defineInRange("maxRulesPerPoll", 20, 1, 200);

        ENGINE_MAX_CHAIN_REACTIONS_PER_TICK = builder
                .comment("每 Tick 最多处理的连锁反应数量（防止栈溢出）")
                .defineInRange("maxChainReactionsPerTick", 10, 1, 100);

        ENGINE_EPSILON = builder
                .comment("反应进度截断阈值（防 Zeno 悖论）")
                .defineInRange("epsilon", 1e-6, 1e-12, 1.0);

        builder.pop();

//        COMMON_SPEC = builder.build();
        SERVER_SPEC = builder.build();
    }

    // ============================================================
    // 难度等级枚举
    // ============================================================
    public enum DifficultyLevel {
        EASY("简单"),
        NORMAL("普通"),
        HARD("困难"),
        CUSTOM("自定义");

        public final String displayName;

        DifficultyLevel(String displayName) {
            this.displayName = displayName;
        }
    }

    // ============================================================
    // 难度预设值工具
    // ============================================================
    public static class DifficultyPresets {

        private static final Map<DifficultyLevel, ConfigValues> PRESETS = new HashMap<>();

        static {
            // 严格映射（所有难度相同）
            double bucketToMol = 64.0;
            double ingotToMol = 1.0;
            double nuggetToMol = 1.0 / 9.0;
            double maxPerComp = 64.0;
            double maxTotal = 64.0;

            PRESETS.put(DifficultyLevel.EASY, new ConfigValues(
                    bucketToMol, ingotToMol, nuggetToMol, maxPerComp, maxTotal,
                    1.2, 0.8,  // 矿石有效/脉石乘数
                    0.5, 2.0,  // 污染扩散速度/毒性阈值
                    0.7, 1.5   // 能源消耗/发电乘数
            ));

            PRESETS.put(DifficultyLevel.NORMAL, new ConfigValues(
                    bucketToMol, ingotToMol, nuggetToMol, maxPerComp, maxTotal,
                    1.0, 1.0,
                    1.0, 1.0,
                    1.0, 1.0
            ));

            PRESETS.put(DifficultyLevel.HARD, new ConfigValues(
                    bucketToMol, ingotToMol, nuggetToMol, maxPerComp, maxTotal,
                    0.7, 1.3,
                    2.0, 0.5,
                    1.5, 0.7
            ));
        }

        public static ConfigValues get(DifficultyLevel difficulty) {
            return PRESETS.getOrDefault(difficulty, PRESETS.get(DifficultyLevel.NORMAL));
        }

        public static class ConfigValues {
            public final double bucketToMolWater;
            public final double solidIngotToMol;
            public final double solidNuggetToMol;
            public final double maxMolesPerComponent;
            public final double maxTotalMoles;
            public final double oreValuableMultiplier;
            public final double oreGangueMultiplier;
            public final double pollutionDiffusionSpeed;
            public final double pollutionToxicityThreshold;
            public final double energyConsumptionMultiplier;
            public final double energyGenerationMultiplier;

            public ConfigValues(double bucketToMolWater, double solidIngotToMol, double solidNuggetToMol,
                                double maxMolesPerComponent, double maxTotalMoles,
                                double oreValuableMultiplier, double oreGangueMultiplier,
                                double pollutionDiffusionSpeed, double pollutionToxicityThreshold,
                                double energyConsumptionMultiplier, double energyGenerationMultiplier) {
                this.bucketToMolWater = bucketToMolWater;
                this.solidIngotToMol = solidIngotToMol;
                this.solidNuggetToMol = solidNuggetToMol;
                this.maxMolesPerComponent = maxMolesPerComponent;
                this.maxTotalMoles = maxTotalMoles;
                this.oreValuableMultiplier = oreValuableMultiplier;
                this.oreGangueMultiplier = oreGangueMultiplier;
                this.pollutionDiffusionSpeed = pollutionDiffusionSpeed;
                this.pollutionToxicityThreshold = pollutionToxicityThreshold;
                this.energyConsumptionMultiplier = energyConsumptionMultiplier;
                this.energyGenerationMultiplier = energyGenerationMultiplier;
            }
        }
    }

    // ============================================================
    // 难度指标计算器
    // ============================================================
    public static class DifficultyScorer {

        /**
         * 计算当前配置的难度指标（0~100）
         * 数值越高代表游戏越困难
         */
        public static double calculateScore() {
            DifficultyLevel current = GAME_DIFFICULTY.get();

            // 获取当前有效的难度参数
            double valuableMultiplier = ORE_VALUABLE_RATIO_MULTIPLIER.get();
            double gangueMultiplier = ORE_GANGUE_RATIO_MULTIPLIER.get();
            double pollutionSpeed = POLLUTION_DIFFUSION_SPEED.get();
            double toxicityThreshold = POLLUTION_TOXICITY_THRESHOLD.get();
            double energyConsumption = ENERGY_CONSUMPTION_MULTIPLIER.get();
            double energyGeneration = ENERGY_GENERATION_MULTIPLIER.get();

            // 计算各维度得分 (0~1)
            // 1. 资源获取难度：有效成分越低/脉石越高 → 越困难
            double resourceScore = (1.0 - valuableMultiplier) * 0.5 + (gangueMultiplier - 0.5) * 0.5;
            resourceScore = Math.max(0, Math.min(1, resourceScore));

            // 2. 污染压力：扩散越快/毒性阈值越低 → 越困难
            double pollutionScore = (pollutionSpeed / 2.0) * 0.6 + (1.0 - Math.min(1.0, toxicityThreshold / 2.0)) * 0.4;
            pollutionScore = Math.max(0, Math.min(1, pollutionScore));

            // 3. 能源效率：耗电越高/发电越低 → 越困难
            double energyScore = (energyConsumption - 0.5) * 0.5 + (1.0 - energyGeneration) * 0.5;
            energyScore = Math.max(0, Math.min(1, energyScore));

            // 加权平均
            double totalWeight = WEIGHT_RESOURCE_GETTING.get() + WEIGHT_POLLUTION.get() + WEIGHT_ENERGY.get();
            if (totalWeight <= 0) return 0;

            double weightedScore = (resourceScore * WEIGHT_RESOURCE_GETTING.get()
                    + pollutionScore * WEIGHT_POLLUTION.get()
                    + energyScore * WEIGHT_ENERGY.get()) / totalWeight;

            // 映射到 0~100
            return weightedScore * 100;
        }
    }
}