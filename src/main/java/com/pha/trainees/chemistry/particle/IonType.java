package com.pha.trainees.chemistry.particle;

import com.pha.trainees.util.math.MathT;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;

@SuppressWarnings({"deprecation", "removal"})
public class IonType {

    private final ResourceLocation id;
    private final Phase phase;
    private final double molarMass;          // g/mol
    private final double specificHeat;       // J/(mol·K)
    private final double formationEnthalpy;  // ΔHf° kJ/mol
    private final double formationGibbs;     // ΔGf° kJ/mol
    private final int toxicityLevel;         // 0-3
    private final int flameColor;            // ARGB 用于焰色/气体颜色
    private final Set<ResourceLocation> tags;

    // 可选：用于经验的温度范围或浓度阈值（后续可接入配置）
    private final double minStableTemp;       // 最低稳定温度（K），默认 0
    private final double maxStableTemp;       // 最高稳定温度（K），默认 9999

    private IonType(Builder builder) {
        this.id = builder.id;
        this.phase = builder.phase;
        this.molarMass = builder.molarMass;
        this.specificHeat = builder.specificHeat;
        this.formationEnthalpy = builder.formationEnthalpy;
        this.formationGibbs = builder.formationGibbs;
        this.toxicityLevel = builder.toxicityLevel;
        this.flameColor = builder.flameColor;
        this.tags = Set.copyOf(builder.tags);
        this.minStableTemp = builder.minStableTemp;
        this.maxStableTemp = builder.maxStableTemp;
    }

    // ---------- Getters ----------
    public ResourceLocation getId() { return id; }
    public Phase getPhase() { return phase; }
    public double getMolarMass() { return molarMass; }
    public double getSpecificHeat() { return specificHeat; }
    public double getFormationEnthalpy() { return formationEnthalpy; }
    public double getFormationGibbs() { return formationGibbs; }
    public int getToxicityLevel() { return toxicityLevel; }
    public int getFlameColor() { return flameColor; }
    public Set<ResourceLocation> getTags() { return tags; }

    public boolean isStableAt(double temperatureKelvin) {
        return MathT.isInInterval(temperatureKelvin, minStableTemp, maxStableTemp);
    }

    // 判断是否匹配标签（用于有机官能团匹配）
    public boolean hasTag(ResourceLocation tag) {
        return tags.contains(tag);
    }

    @Override
    public String toString() {
        return id.toString();
    }

    // ---------- Builder ----------
    public static class Builder {
        private final ResourceLocation id;
        private final Phase phase;
        private double molarMass = 0;
        private double specificHeat = 1.0; // 默认值
        private double formationEnthalpy = 0;
        private double formationGibbs = 0;
        private int toxicityLevel = 0;
        private int flameColor = 0xFFFFFF; // 白色默认
        private final Set<ResourceLocation> tags = new HashSet<>();
        private double minStableTemp = 0;
        private double maxStableTemp = 9999;

        public Builder(ResourceLocation id, Phase phase) {
            this.id = id;
            this.phase = phase;
        }

        public Builder molarMass(double val) { this.molarMass = val; return this; }
        public Builder specificHeat(double val) { this.specificHeat = val; return this; }
        public Builder formationEnthalpy(double val) { this.formationEnthalpy = val; return this; }
        public Builder formationGibbs(double val) { this.formationGibbs = val; return this; }
        public Builder toxicityLevel(int val) { this.toxicityLevel = val; return this; }
        public Builder flameColor(int val) { this.flameColor = val; return this; }
        public Builder tag(ResourceLocation tag) { this.tags.add(tag); return this; }
        public Builder stableRange(double minK, double maxK) { this.minStableTemp = minK; this.maxStableTemp = maxK; return this; }

        public IonType build() {
            return new IonType(this);
        }
    }
}