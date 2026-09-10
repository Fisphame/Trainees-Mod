package com.pha.trainees.chemistry.reaction;

import com.pha.trainees.Main;
import com.pha.trainees.chemistry.container.IChemicalContainer;
import com.pha.trainees.chemistry.particle.IonType;
import com.pha.trainees.chemistry.particle.Phase;
import com.pha.trainees.config.ChemConfig;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * 反应规则对象
 * 包含完整化学方程式、热力学参数、动力学参数和前置条件
 */
public class ReactionRule {

    private final ResourceLocation id;
    private final Map<IonType, Integer> reactants;    // 反应物及计量数（精确匹配）
    private final Map<IonType, Integer> products;     // 生成物及计量数
    private final Map<IonType, Integer> preconditions; // 前置条件（催化剂等，不消耗）
    // 使用 Supplier 支持懒求值与热加载（配置变更即时生效）
    private final Supplier<Double> deltaH;              // 反应焓变 (kJ/mol)
    private final Supplier<Double> deltaG;              // 标准吉布斯自由能变 (kJ/mol)
    private final Supplier<Double> equilibriumConstant; // 298K 平衡常数
    private final Supplier<Double> activationEnergy;    // 活化能 (kJ/mol)
    private final Supplier<Double> preExponentialFactor;// 指前因子 A
    private final Supplier<Double> gamePriorityBias;    // 游戏性优先级偏置（默认1.0）
    private final Supplier<Double> minTemperature;      // 最低触发温度 (K)
    // 电解所需电功（kJ/mol，蓝本 §17）：>0 表示该反应为电解反应，通电时 ΔG_eff = ΔG - W
    private final Supplier<Double> electricalWorkPerMol;
    // 动态优先级函数（蓝本 §5.2）：以容器状态为自变量的优先级偏置，为空则用 gamePriorityBias
    private final Function<IChemicalContainer, Double> dynamicPriorityBias;
    private final boolean isSelfLoop;                   // 是否为自环（分解反应）
    // 是否显式覆盖平衡常数（手设 K° × 范特霍夫）。默认 false → 物理公式 K = exp(-ΔG(T)/(R·T))。
    private final boolean equilibriumOverridden;

    private ReactionRule(Builder builder) {
        this.id = builder.id;
        this.reactants = Map.copyOf(builder.reactants);
        this.products = Map.copyOf(builder.products);
        this.preconditions = Map.copyOf(builder.preconditions);

        this.deltaH = builder.deltaH;
        this.deltaG = builder.deltaG;
        this.equilibriumConstant = builder.equilibriumConstant;
        this.activationEnergy = builder.activationEnergy;
        this.preExponentialFactor = builder.preExponentialFactor;
        this.gamePriorityBias = builder.gamePriorityBias;
        this.minTemperature = builder.minTemperature;
        this.electricalWorkPerMol = builder.electricalWorkPerMol;
        this.dynamicPriorityBias = builder.dynamicPriorityBias;
        this.isSelfLoop = builder.isSelfLoop;
        this.equilibriumOverridden = builder.equilibriumOverridden;
    }

    // ==================== Getters ====================
    public ResourceLocation getId() { return id; }
    public Map<IonType, Integer> getReactants() { return reactants; }
    public Map<IonType, Integer> getProducts() { return products; }
    public Map<IonType, Integer> getPreconditions() { return preconditions; }
    public double getDeltaH() { return deltaH.get(); }
    public double getDeltaG() { return deltaG.get(); }
    public double getEquilibriumConstant() { return equilibriumConstant.get(); }
    public double getActivationEnergy() { return activationEnergy.get(); }
    public double getPreExponentialFactor() { return preExponentialFactor.get(); }
    public double getGamePriorityBias() { return gamePriorityBias.get(); }
    public double getMinTemperature() { return minTemperature.get(); }
    public boolean isSelfLoop() { return isSelfLoop; }

    /** 是否显式覆盖了平衡常数（可逆玩法；JEI 等只读展示需要区分算法，§19.16） */
    public boolean isEquilibriumOverridden() { return equilibriumOverridden; }

    /**
     * 检查容器是否满足前置条件（催化剂等）
     */
    public boolean checkPreconditions(IChemicalContainer container) {
        for (Map.Entry<IonType, Integer> entry : preconditions.entrySet()) {
            if (container.getAmount(entry.getKey()) < entry.getValue()) {
                return false;
            }
        }
        return true;
    }

    /**
     * 计算当前温度下的实际速率常数 k
     * k = A * exp(-Ea / (R * T))（纯数学，见 ReactionMath.rateConstant）
     */
    public double calculateRateConstant(double temperatureKelvin) {
        return ReactionMath.rateConstant(activationEnergy.get(), preExponentialFactor.get(), temperatureKelvin);
    }

    /**
     * 计算当前温度下的实际平衡常数 K。
     * 电解反应（W>0）通电时：K 由 ΔG_eff 决定（电功等效于把平衡推向产物）。
     * 显式覆盖（equilibriumOverridden）：K = K° × exp(-ΔH/R * (1/T - 1/298))（范特霍夫）。
     * 默认（物理公式）：K = exp(-ΔG(T)/(R·T))，ΔG(T) = ΔH - T·ΔS，ΔS 由 298K 数据反推。
     * 计算细节委托纯数学层 ReactionMath（可单元测试）。
     */
    public double calculateEquilibriumConstant(double temperatureKelvin, IChemicalContainer container) {
        double work = electricalWorkPerMol.get();
        if (work > 0 && container.isElectricallyPowered()) {
            return ReactionMath.equilibriumConstantElectrolysis(deltaG.get(), work, temperatureKelvin);
        }

        if (temperatureKelvin <= 0) {
            Main.LOGGER.warn("[Engine] calculateEquilibriumConstant: temperature {} <= 0, returning 0",
                    temperatureKelvin);
            return 0;
        }

        if (equilibriumOverridden) {
            // 显式覆盖（游戏性可逆）：K° × 范特霍夫
            return ReactionMath.equilibriumConstantVanHoff(equilibriumConstant.get(), deltaH.get(), temperatureKelvin);
        }

        // 物理平衡常数：K = exp(-ΔG(T)/(R·T))，ΔG(T) = ΔH - T·ΔS（由生成数据推出）
        return ReactionMath.equilibriumConstantPhysical(deltaH.get(), deltaG.get(), temperatureKelvin);
    }

    /**
     * 电解所需电功（kJ/mol），>0 表示该反应为电解反应
     */
    public double getElectricalWorkPerMol() { return electricalWorkPerMol.get(); }

    /**
     * 有效自由能变（蓝本 §17 电解）：ΔG_eff = ΔG - W。
     * 电解反应（W>0）且容器通电时返回抵消后的值；否则返回原 ΔG。
     */
    public double getEffectiveDeltaG(IChemicalContainer container) {
        double work = electricalWorkPerMol.get();
        if (work <= 0 || !container.isElectricallyPowered()) return deltaG.get();
        return deltaG.get() - work;
    }

    /**
     * 计算该反应在当前容器状态下的“优先级评分”
     * Score = (-ΔG_eff / Ea) * 温度修正 * 偏置
     * 电解反应（W>0）未通电时不可运行（返回 0）
     */
    public double calculatePriority(IChemicalContainer container) {
        double temp = container.getTemperature();
        if (temp < minTemperature.get()) return 0;
        // 电解反应未通电时不可运行
        if (electricalWorkPerMol.get() > 0 && !container.isElectricallyPowered()) return 0;
        // 基础评分：热力学驱动力（有效自由能）/ 动力学壁垒
        double baseScore = (-getEffectiveDeltaG(container)) / (activationEnergy.get() + ChemConfig.PRIORITY_EPSILON.get());
        // 温度修正：温度越高，动力学因素权重越大
        double tempModifier = 1.0 +
                ChemConfig.PRIORITY_TEMPERATURE_MODIFIER.get() * (temp - 298.0) / ChemConfig.PRIORITY_TEMPERATURE_REFERENCE.get();
        double bias = dynamicPriorityBias != null ? dynamicPriorityBias.apply(container) : gamePriorityBias.get();
        return baseScore * tempModifier * bias;
    }

    @Override
    public String toString() {
        return "ReactionRule{" + id + "}";
    }

    // ==================== Builder ====================
    public static class Builder {
        private final ResourceLocation id;
        private final Map<IonType, Integer> reactants = new HashMap<>();
        private final Map<IonType, Integer> products = new HashMap<>();
        private final Map<IonType, Integer> preconditions = new HashMap<>();
        private Supplier<Double> deltaH = () -> 0.0;
        private Supplier<Double> deltaG = () -> 0.0;
        private Supplier<Double> equilibriumConstant = ChemConfig.DEFAULT_EQUILIBRIUM_CONSTANT;
        private Supplier<Double> activationEnergy = ChemConfig.DEFAULT_ACTIVATION_ENERGY;
        private Supplier<Double> preExponentialFactor = ChemConfig.DEFAULT_PRE_EXPONENTIAL_FACTOR;
        private Supplier<Double> gamePriorityBias = () -> 1.0;
        private Supplier<Double> minTemperature = ChemConfig.DEFAULT_MIN_TEMPERATURE;
        private Supplier<Double> electricalWorkPerMol = () -> 0.0;
        private Function<IChemicalContainer, Double> dynamicPriorityBias = null;
        private boolean isSelfLoop = false;
        private boolean equilibriumOverridden = false;

        public Builder(ResourceLocation id) {
            this.id = id;
        }

        public Builder reactant(IonType ion, int stoichiometry) {
            reactants.put(ion, stoichiometry);
            return this;
        }

        public Builder product(IonType ion, int stoichiometry) {
            products.put(ion, stoichiometry);
            return this;
        }

        public Builder precondition(IonType ion, int amount) {
            preconditions.put(ion, amount);
            return this;
        }

        public Builder deltaH(double val) { this.deltaH = () -> val; return this; }
        public Builder deltaG(double val) { this.deltaG = () -> val; return this; }
        public Builder equilibriumConstant(double val) { this.equilibriumConstant = () -> val; return this; }

        /**
         * 显式覆盖平衡常数（可逆玩法）：以手设 K° × 范特霍夫计算 K。
         * 不调用则走物理公式 K = exp(-ΔG(T)/(R·T))，ΔG(T) = ΔH - T·ΔS（由生成数据推出）。
         * 供 N₂O₄、SO₂/SO₃ 接触法、哈伯等作者手调平衡点的可逆反应使用。
         */
        public Builder equilibriumOverridden() {
            this.equilibriumOverridden = true;
            return this;
        }
        public Builder activationEnergy(double val) { this.activationEnergy = () -> val; return this; }
        public Builder preExponentialFactor(double val) { this.preExponentialFactor = () -> val; return this; }
        public Builder gamePriorityBias(double val) { this.gamePriorityBias = () -> val; return this; }
        public Builder minTemperature(double val) { this.minTemperature = () -> val; return this; }

        /**
         * 设置动态优先级函数（蓝本 §5.2 浓度依赖反应）。
         * 以容器当前状态为自变量返回偏置系数，用于竞争反应按浓度选择主导路径。
         */
        public Builder dynamicPriorityBias(Function<IChemicalContainer, Double> fn) {
            this.dynamicPriorityBias = fn;
            return this;
        }

        /**
         * 设置电解所需电功（kJ/mol，蓝本 §17）。>0 表示该反应为电解反应，
         * 通电时引擎以 ΔG_eff = ΔG - W 参与优先级与平衡计算，并按 Δξ 消耗电功。
         */
        public Builder electricalWorkPerMol(double val) {
            this.electricalWorkPerMol = () -> val;
            return this;
        }

        /**
         * 自动以反应自由能作为理论最小电功（W = ΔG）。
         * 适用于电解等非自发反应：通电后 ΔG_eff = 0（临界可驱动）。
         */
        public Builder electricalWorkComputed() {
            this.electricalWorkPerMol = () -> deltaGOf(products) - deltaGOf(reactants);
            return this;
        }

        /**
         * 按能量守恒自动计算反应焓变：ΔH = ΣΔHf°(产物) - ΣΔHf°(反应物)
         * 数据来源为 IonType 的生成焓，符合蓝本 §8.3 硬约束（禁止手填经验值）
         */
        public Builder deltaHComputed() {
            this.deltaH = () -> deltaHOf(products) - deltaHOf(reactants);
            return this;
        }

        /**
         * 按能量守恒自动计算标准自由能变：ΔG = ΣΔGf°(产物) - ΣΔGf°(反应物)
         */
        public Builder deltaGComputed() {
            this.deltaG = () -> deltaGOf(products) - deltaGOf(reactants);
            return this;
        }

        private static double deltaHOf(Map<IonType, Integer> map) {
            double sum = 0;
            for (Map.Entry<IonType, Integer> e : map.entrySet()) {
                sum += e.getKey().getFormationEnthalpy() * e.getValue();
            }
            return sum;
        }

        private static double deltaGOf(Map<IonType, Integer> map) {
            double sum = 0;
            for (Map.Entry<IonType, Integer> e : map.entrySet()) {
                sum += e.getKey().getFormationGibbs() * e.getValue();
            }
            return sum;
        }

        public Builder selfLoop(boolean val) { this.isSelfLoop = val; return this; }

        // ====== 从配置读取（懒求值） ======
        public Builder deltaHFromConfig(ForgeConfigSpec.DoubleValue config) {
            this.deltaH = config::get;
            return this;
        }
        public Builder deltaGFromConfig(ForgeConfigSpec.DoubleValue config) {
            this.deltaG = config::get;
            return this;
        }
        public Builder equilibriumConstantFromConfig(ForgeConfigSpec.DoubleValue config) {
            this.equilibriumConstant = config::get;
            return this;
        }
        public Builder activationEnergyFromConfig(ForgeConfigSpec.DoubleValue config) {
            this.activationEnergy = config::get;
            return this;
        }
        public Builder preExponentialFactorFromConfig(ForgeConfigSpec.DoubleValue config) {
            this.preExponentialFactor = config::get;
            return this;
        }
        public Builder gamePriorityBiasFromConfig(ForgeConfigSpec.DoubleValue config) {
            this.gamePriorityBias = config::get;
            return this;
        }
        public Builder minTemperatureFromConfig(ForgeConfigSpec.DoubleValue config) {
            this.minTemperature = config::get;
            return this;
        }

        public ReactionRule build() {
            if (reactants.isEmpty() || products.isEmpty()) {
                throw new IllegalStateException("Reaction rule must have at least one reactant and one product");
            }
            // 自动检测是否为自环
            if (reactants.size() == 1 && products.size() == 1) {
                IonType reactant = reactants.keySet().iterator().next();
                IonType product = products.keySet().iterator().next();
                if (reactant.equals(product)) {
                    this.isSelfLoop = true;
                }
            }
            return new ReactionRule(this);
        }
    }
}