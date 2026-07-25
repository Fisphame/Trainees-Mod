package com.pha.trainees.chemistry.reaction;

import com.pha.trainees.chemistry.container.IChemicalContainer;
import com.pha.trainees.chemistry.particle.IonType;
import com.pha.trainees.chemistry.particle.Phase;
import com.pha.trainees.config.ChemConfig;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 反应规则对象
 * 包含完整化学方程式、热力学参数、动力学参数和前置条件
 */
public class ReactionRule {

    private final ResourceLocation id;
    private final Map<IonType, Integer> reactants;    // 反应物及计量数（精确匹配）
    private final Map<IonType, Integer> products;     // 生成物及计量数
    private final Map<IonType, Integer> preconditions; // 前置条件（催化剂等，不消耗）
    private final double deltaH;          // 反应焓变 (kJ/mol)
    private final double deltaG;          // 标准吉布斯自由能变 (kJ/mol)
    private final double equilibriumConstant; // 298K 平衡常数
    private final double activationEnergy; // 活化能 (kJ/mol)
    private final double preExponentialFactor; // 指前因子 A
    private final double gamePriorityBias; // 游戏性优先级偏置（默认1.0）
    private final double minTemperature;  // 最低触发温度 (K)
    private final boolean isSelfLoop;     // 是否为自环（分解反应）

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
        this.isSelfLoop = builder.isSelfLoop;
    }

    // ==================== Getters ====================
    public ResourceLocation getId() { return id; }
    public Map<IonType, Integer> getReactants() { return reactants; }
    public Map<IonType, Integer> getProducts() { return products; }
    public Map<IonType, Integer> getPreconditions() { return preconditions; }
    public double getDeltaH() { return deltaH; }
    public double getDeltaG() { return deltaG; }
    public double getEquilibriumConstant() { return equilibriumConstant; }
    public double getActivationEnergy() { return activationEnergy; }
    public double getPreExponentialFactor() { return preExponentialFactor; }
    public double getGamePriorityBias() { return gamePriorityBias; }
    public double getMinTemperature() { return minTemperature; }
    public boolean isSelfLoop() { return isSelfLoop; }

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
     * k = A * exp(-Ea / (R * T))
     */
    public double calculateRateConstant(double temperatureKelvin) {
        if (temperatureKelvin <= 0) return 0;
        double R = 8.314; // J/(mol·K)
        return preExponentialFactor * Math.exp(-activationEnergy * 1000 / (R * temperatureKelvin));
    }

    /**
     * 计算当前温度下的实际平衡常数 K
     * 使用范特霍夫方程：ln(K/K°) = -ΔH/R * (1/T - 1/298)
     */
    public double calculateEquilibriumConstant(double temperatureKelvin) {
        if (temperatureKelvin <= 0) return 0;
        double R = 8.314;
        double exponent = -deltaH * 1000 / R * (1.0 / temperatureKelvin - 1.0 / 298.0);
        return equilibriumConstant * Math.exp(exponent);
    }

    /**
     * 计算反应商 Q
     * Q = ∏[生成物]^ν / ∏[反应物]^ν
     */
    public double calculateReactionQuotient(IChemicalContainer container) {
        double numerator = 1.0;
        double denominator = 1.0;
        // 生成物
        for (Map.Entry<IonType, Integer> entry : products.entrySet()) {
            double conc = container.getAmount(entry.getKey());
            numerator *= Math.pow(conc, entry.getValue());
        }
        // 反应物
        for (Map.Entry<IonType, Integer> entry : reactants.entrySet()) {
            double conc = container.getAmount(entry.getKey());
            denominator *= Math.pow(conc, entry.getValue());
        }
        if (denominator < 1e-9) return Double.MAX_VALUE;
        return numerator / denominator;
    }

    /**
     * 计算该反应在当前容器状态下的“优先级评分”
     * Score = (-ΔG / Ea) * gamePriorityBias
     * 用于竞争反应排序
     */
    public double calculatePriority(IChemicalContainer container) {
        double temp = container.getTemperature();
        if (temp < minTemperature) return 0;
        // 基础评分：热力学驱动力 / 动力学壁垒
        double baseScore = (-deltaG) / (activationEnergy + ChemConfig.PRIORITY_EPSILON.get());
        // 温度修正：温度越高，动力学因素权重越大
        double tempModifier = 1.0 +
                ChemConfig.PRIORITY_TEMPERATURE_MODIFIER.get() * (temp - 298.0) / ChemConfig.PRIORITY_TEMPERATURE_REFERENCE.get();
        return baseScore * tempModifier * gamePriorityBias;
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
        private double deltaH = 0;
        private double deltaG = 0;
        private double equilibriumConstant = ChemConfig.DEFAULT_EQUILIBRIUM_CONSTANT.get();
        private double activationEnergy = ChemConfig.DEFAULT_ACTIVATION_ENERGY.get();
        private double preExponentialFactor = ChemConfig.DEFAULT_PRE_EXPONENTIAL_FACTOR.get();
        private double gamePriorityBias = 1.0;
        private double minTemperature = ChemConfig.DEFAULT_MIN_TEMPERATURE.get();
        private boolean isSelfLoop = false;

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

        public Builder deltaH(double val) { this.deltaH = val; return this; }
        public Builder deltaG(double val) { this.deltaG = val; return this; }
        public Builder equilibriumConstant(double val) { this.equilibriumConstant = val; return this; }
        public Builder activationEnergy(double val) { this.activationEnergy = val; return this; }
        public Builder preExponentialFactor(double val) { this.preExponentialFactor = val; return this; }
        public Builder gamePriorityBias(double val) { this.gamePriorityBias = val; return this; }
        public Builder minTemperature(double val) { this.minTemperature = val; return this; }
        public Builder selfLoop(boolean val) { this.isSelfLoop = val; return this; }

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