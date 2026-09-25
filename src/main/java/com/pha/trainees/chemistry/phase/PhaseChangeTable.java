package com.pha.trainees.chemistry.phase;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * 相变数据表（§19.17.1 / §19.17.3）：每物质一行，生成 6 条有向边（s⇄l、l⇄g、s⇄g）。
 *
 * <p><b>参考值来源</b>（文献值，298 K 生成量 + 转变温度/转变焓）：</p>
 * <ul>
 *   <li>H₂O：T_m 273.15 K、T_b 373.15 K、L_f 6.01、L_v 40.65 kJ/mol；
 *       ΔHf°(冰) = −291.8（= ΔHf°(液) − L_f）、ΔGf°(冰) = −236.6
 *       （由 ΔG_fus(298 K) = ΔH − TΔS = −0.55 反推，与文献冰的 −236.6 一致）</li>
 *   <li>NaCl：T_m 1074 K、T_b 1738 K、L_f 28.0、L_v 170 kJ/mol；
 *       熔盐 ΔHf° = ΔHf°(固) + L_f = −383.1、ΔGf° = −384.1 + (L_f − TΔS) = −363.9
 *       （ΔS = L_f/T_m = 0.02607 kJ/(mol·K)）</li>
 * </ul>
 *
 * <p><b>为什么把熔盐的生成量一并改掉</b>：原先注册的熔盐生成量是为电解调参的，用物理 K 反推
 * 熔点会得到上万开尔文（与 1074 K 差一个数量级）。相变要走"物理 K 在转变温度穿过 1"这条路，
 * 生成量就必须与 T_m 自洽——修正后电解的 ΔG/W 也会随之小幅变化（更正确）。</p>
 */
public final class PhaseChangeTable {

    /** 关联系数 R (kJ/(mol·K)) */
    public static final double R_KJ = 0.008314;
    /** 生成量参考温度 (K) */
    public static final double REFERENCE_TEMPERATURE_K = 298.15;

    // ==================== 水的三相 ====================
    public static final PhaseSpecies ICE = new PhaseSpecies("h2o_solid", 18.02, -291.8, -236.6);
    public static final PhaseSpecies WATER = new PhaseSpecies("h2o", 18.02, -285.8, -237.1);
    public static final PhaseSpecies STEAM = new PhaseSpecies("h2o_gas", 18.02, -241.8, -228.6);

    // ==================== 食盐（熔盐电解的相变前置） ====================
    public static final PhaseSpecies SALT = new PhaseSpecies("nacl", 58.44, -411.1, -384.1);
    public static final PhaseSpecies MOLTEN_SALT = new PhaseSpecies("nacl_molten", 58.44, -383.1, -363.9);

    public static final PhaseChangeData WATER_SYSTEM =
            new PhaseChangeData("h2o", ICE, WATER, STEAM, 273.15, 373.15, 6.01, 40.65, 1.0);

    public static final PhaseChangeData SALT_SYSTEM =
            new PhaseChangeData("nacl", SALT, MOLTEN_SALT, null, 1074.0, 1738.0, 28.0, 170.0, 5.0);

    public static final List<PhaseChangeData> ALL = List.of(WATER_SYSTEM, SALT_SYSTEM);

    private PhaseChangeTable() {
    }

    // ==================== 边生成 ====================

    /** 生成某物质的全部有向相变边（三相 → 6 条；两相 → 2 条）。 */
    public static List<PhaseTransition> transitions(PhaseChangeData data) {
        List<PhaseTransition> edges = new ArrayList<>(6);
        PhaseSpecies solid = data.solid();
        PhaseSpecies liquid = data.liquid();
        PhaseSpecies gas = data.gas();
        double hysteresis = data.hysteresisK();

        if (solid != null && liquid != null) {
            double melting = data.meltingPointK();
            // 熔化：只在 T ≥ T_m 运行（吸热，靠潜热把温度"卡"在熔点 = 相变平台）
            edges.add(edge(data, PhaseTransition.Kind.FUSION, solid, liquid,
                    melting, Double.POSITIVE_INFINITY, 20.0, 1e9));
            // 凝固：只在 T ≤ T_m − 迟滞 运行；与熔化之间留死区，杜绝熔点附近来回抖动
            edges.add(edge(data, PhaseTransition.Kind.FREEZING, liquid, solid,
                    0.0, melting - hysteresis, 25.0, 1e8));
        }
        if (liquid != null && gas != null) {
            double boiling = data.boilingPointK();
            edges.add(edge(data, PhaseTransition.Kind.VAPORIZATION, liquid, gas,
                    boiling, Double.POSITIVE_INFINITY, 45.0, 1e9));
            edges.add(edge(data, PhaseTransition.Kind.CONDENSATION, gas, liquid,
                    0.0, boiling - hysteresis, 10.0, 1e10));
        }
        if (solid != null && gas != null) {
            // V1 不设温度门槛（方向交给物理 K），改用高 Ea + 极低指前因子压制：
            // 只有液相不可用或温度远超时才接管。V2 再引入"固体饱和蒸气压 > 环境分压"判据。
            edges.add(edge(data, PhaseTransition.Kind.SUBLIMATION, solid, gas,
                    0.0, Double.POSITIVE_INFINITY, 65.0, 1e3));
            edges.add(edge(data, PhaseTransition.Kind.DEPOSITION, gas, solid,
                    0.0, Double.POSITIVE_INFINITY, 60.0, 1e3));
        }
        return List.copyOf(edges);
    }

    /** 全表的有向边（注册用）。 */
    public static List<PhaseTransition> allTransitions() {
        List<PhaseTransition> all = new ArrayList<>(8);
        for (PhaseChangeData data : ALL) {
            all.addAll(transitions(data));
        }
        return List.copyOf(all);
    }

    private static PhaseTransition edge(PhaseChangeData data, PhaseTransition.Kind kind,
                                        PhaseSpecies from, PhaseSpecies to,
                                        double minKelvin, double maxKelvin,
                                        double activationEnergyKj, double preExponential) {
        String ruleId = "phase_" + data.key() + "_" + kind.name().toLowerCase(Locale.ROOT);
        return new PhaseTransition(ruleId, from.id(), to.id(), kind,
                minKelvin, maxKelvin, activationEnergyKj, preExponential);
    }

    // ==================== 热力学自洽性（测试与后续蒸馏共用） ====================

    /** 有向边的转变焓（kJ/mol）：ΔH = ΔHf°(to) − ΔHf°(from)。 */
    public static double transitionEnthalpy(PhaseSpecies from, PhaseSpecies to) {
        return to.formationEnthalpy() - from.formationEnthalpy();
    }

    /**
     * 由 298 K 生成量推出的转变温度：ΔS = (ΔH − ΔG)/298.15，T = ΔH/ΔS。
     * 物理 K = exp(−ΔG(T)/RT) 正是在这个温度穿过 1，因此它应当贴近文献 T_m/T_b。
     */
    public static double predictedTransitionTemperature(PhaseSpecies from, PhaseSpecies to) {
        double deltaH = transitionEnthalpy(from, to);
        double deltaG = to.formationGibbs() - from.formationGibbs();
        double deltaS = (deltaH - deltaG) / REFERENCE_TEMPERATURE_K;
        if (Math.abs(deltaS) < 1e-9) return Double.NaN;
        return deltaH / deltaS;
    }
}
