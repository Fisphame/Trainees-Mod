package com.pha.trainees.chemistry.reaction;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 化学引擎纯数学层测试（§19.8 单元测试策略）。
 * 覆盖引擎核心计算的回归保护：物理平衡常数温度依赖（高温不塌缩）、范特霍夫、
 * 阿伦尼乌斯、电解有效 K、反应商、净速率、Δξ。
 */
class ReactionMathTest {

    private static final double EPS = 1e-9;

    // ==================== 物理平衡常数 ====================

    @Test
    void physicalK_strongExothermic_remainsHugeAtHighTemperature() {
        // 燃烧 C + O₂ → CO₂：ΔH≈ΔG < 0（kJ/mol）
        double deltaH = -393.5, deltaG = -394.4;
        double k298 = ReactionMath.equilibriumConstantPhysical(deltaH, deltaG, 298);
        double k1300 = ReactionMath.equilibriumConstantPhysical(deltaH, deltaG, 1300);
        assertTrue(k298 > 1e10, "强放热反应 298K 应有巨大 K，实际 " + k298);
        assertTrue(k1300 > 1e10, "强放热反应 1300K 高温 K 不应塌缩，实际 " + k1300);
    }

    @Test
    void physicalK_endothermic_risesWithTemperature() {
        // 石灰煅烧 CaCO₃ → CaO + CO₂：强吸热、常温不自发
        double deltaH = 178.0, deltaG = 130.0;
        double k298 = ReactionMath.equilibriumConstantPhysical(deltaH, deltaG, 298);
        double k1300 = ReactionMath.equilibriumConstantPhysical(deltaH, deltaG, 1300);
        assertTrue(k298 < 1e-5, "吸热反应常温 K 应冻结，实际 " + k298);
        assertTrue(k1300 > 1, "吸热反应高温 K 应增大（石灰煅烧可行），实际 " + k1300);
    }

    @Test
    void physicalK_neutralization_isHuge() {
        // 中和 H⁺ + OH⁻ → H₂O：强放热自发
        double deltaH = -55.8, deltaG = -79.9;
        double k = ReactionMath.equilibriumConstantPhysical(deltaH, deltaG, 298);
        assertTrue(k > 1e10, "中和反应应有巨大 K，实际 " + k);
    }

    @Test
    void physicalK_negativeTemperature_returnsZero() {
        assertEquals(0, ReactionMath.equilibriumConstantPhysical(-10, -10, 0));
    }

    // ==================== 范特霍夫（显式覆盖） ====================

    @Test
    void vanHoff_n2o4_matchesBlueprintValue() {
        // N₂O₄ ⇌ 2NO₂：K°=1.0，ΔH=+57.2 kJ/mol；蓝本注释：约 293K 时 K≈0.676
        double k293 = ReactionMath.equilibriumConstantVanHoff(1.0, 57.2, 293);
        assertEquals(0.676, k293, 0.05, "293K 时 K 应约 0.676，实际 " + k293);
    }

    @Test
    void vanHoff_reverseIsInverseOfForward() {
        // 正反应 K 与逆反应 K（同 ΔH 反号、K° 取倒数）互为倒数
        double kFwd = ReactionMath.equilibriumConstantVanHoff(10.0, -92.2, 400);
        double kRev = ReactionMath.equilibriumConstantVanHoff(0.1, 92.2, 400);
        assertEquals(1.0, kFwd * kRev, 1e-9, "正逆 K 应互为倒数");
    }

    // ==================== 阿伦尼乌斯 ====================

    @Test
    void arrhenius_rateConstant_increasesWithTemperature() {
        double k300 = ReactionMath.rateConstant(50.0, 1e8, 300);
        double k500 = ReactionMath.rateConstant(50.0, 1e8, 500);
        assertTrue(k500 > k300, "温度升高 k 应增大");
        assertEquals(0, ReactionMath.rateConstant(50.0, 1e8, 0), "T≤0 返回 0");
    }

    @Test
    void arrhenius_higherActivationEnergy_lowerRate() {
        double kLowEa = ReactionMath.rateConstant(30.0, 1e8, 300);
        double kHighEa = ReactionMath.rateConstant(75.0, 1e8, 300);
        assertTrue(kLowEa > kHighEa, "活化能越高 k 越小（低温更明显）");
    }

    // ==================== 电解 ====================

    @Test
    void electrolysis_workEqualsDeltaG_givesK1() {
        // 水电解 ΔG=+474.2 kJ/mol，W=ΔG 时 ΔG_eff=0 → K=1（理论最小电功）
        double k = ReactionMath.equilibriumConstantElectrolysis(474.2, 474.2, 298);
        assertEquals(1.0, k, 1e-9, "W=ΔG 时 K 应=1");
    }

    @Test
    void electrolysis_overwork_pushesKAbove1() {
        double k = ReactionMath.equilibriumConstantElectrolysis(474.2, 600.0, 298);
        assertTrue(k > 1, "电功超过 ΔG 应把平衡推向产物（K>1）");
    }

    // ==================== 反应商 Q ====================

    @Test
    void reactionQuotient_basic() {
        // Q = [C]^2 / ([A] * [B])：[C]=2(ν=2)，[A]=1，[B]=3 → 4/3
        double q = ReactionMath.reactionQuotient(
                Map.of(2.0, 2),          // [C]=2, ν=2 → 分子 2²=4
                Map.of(1.0, 1, 3.0, 1)   // [A]=1, [B]=3 → 分母 3
        );
        assertEquals(4.0 / 3.0, q, EPS);
    }

    @Test
    void reactionQuotient_zeroDenominator_maxValue() {
        double q = ReactionMath.reactionQuotient(Map.of(2.0, 2), Map.of(0.0, 1));
        assertEquals(Double.MAX_VALUE, q, "反应物浓度为零时 Q 应为 MAX_VALUE");
    }

    // ==================== 净速率 ====================

    @Test
    void netRate_belowEquilibrium_positive() {
        assertEquals(0.5, ReactionMath.netRate(1.0, 0.5, 1.0), EPS); // v_f=1, Q=0.5, K=1 → 0.5
    }

    @Test
    void netRate_atEquilibrium_zero() {
        assertEquals(0.0, ReactionMath.netRate(1.0, 1.0, 1.0), EPS);
    }

    @Test
    void netRate_aboveEquilibrium_negative() {
        assertTrue(ReactionMath.netRate(1.0, 2.0, 1.0) < 0, "Q>K 时净速率为负（逆向方向）");
    }

    @Test
    void netRate_zeroK_returnsZero() {
        assertEquals(0.0, ReactionMath.netRate(1.0, 0.5, 0.0), EPS);
    }

    // ==================== Δξ ====================

    @Test
    void deltaXi_scalesWithTime() {
        assertEquals(0.05, ReactionMath.deltaXi(1.0, 0.05), EPS); // 1 tick = 0.05s
        assertEquals(0.0, ReactionMath.deltaXi(0.0, 0.05), EPS);
    }
}
