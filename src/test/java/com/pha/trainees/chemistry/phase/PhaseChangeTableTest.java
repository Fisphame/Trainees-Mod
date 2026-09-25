package com.pha.trainees.chemistry.phase;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 相变数据表的一致性测试（§19.17.1）。
 *
 * <p>这层测试守的是整条相变链路的**物理自洽**：相变走"物理 K 在转变温度穿过 1"这条路，
 * 所以"由生成量推出的转变温度"必须贴近文献熔点/沸点；一旦有人改了某个相的生成量而没改另一个，
 * 这里会立刻红。同时锁定迟滞死区（熔化/凝固门槛不得重叠）与升华的动力学压制。</p>
 */
class PhaseChangeTableTest {

    /** 由生成量推出的转变温度与文献值允许的偏差（5%）。 */
    private static final double TEMPERATURE_TOLERANCE = 0.05;
    /** 转变焓与文献 L 的允许偏差（kJ/mol）。 */
    private static final double ENTHALPY_TOLERANCE = 0.5;

    @Test
    void fusionEnthalpy_matchesFormationValues() {
        for (PhaseChangeData data : PhaseChangeTable.ALL) {
            if (data.solid() == null || data.liquid() == null) continue;
            double derived = PhaseChangeTable.transitionEnthalpy(data.solid(), data.liquid());
            assertEquals(data.fusionEnthalpyKj(), derived, ENTHALPY_TOLERANCE,
                    data.key() + " 的熔化焓应等于两相生成焓之差");
        }
    }

    @Test
    void meltingTemperature_matchesLiterature() {
        for (PhaseChangeData data : PhaseChangeTable.ALL) {
            if (data.solid() == null || data.liquid() == null) continue;
            double predicted = PhaseChangeTable.predictedTransitionTemperature(data.solid(), data.liquid());
            double error = Math.abs(predicted - data.meltingPointK()) / data.meltingPointK();
            assertTrue(error <= TEMPERATURE_TOLERANCE, String.format(
                    "%s 熔点：物理 K 穿过 1 的温度 %.1f K 与文献 %.1f K 偏差 %.1f%%（生成量不自洽）",
                    data.key(), predicted, data.meltingPointK(), error * 100));
        }
    }

    @Test
    void boilingTemperature_matchesLiterature() {
        for (PhaseChangeData data : PhaseChangeTable.ALL) {
            if (data.liquid() == null || data.gas() == null) continue;
            double predicted = PhaseChangeTable.predictedTransitionTemperature(data.liquid(), data.gas());
            double error = Math.abs(predicted - data.boilingPointK()) / data.boilingPointK();
            assertTrue(error <= TEMPERATURE_TOLERANCE, String.format(
                    "%s 沸点：物理 K 穿过 1 的温度 %.1f K 与文献 %.1f K 偏差 %.1f%%",
                    data.key(), predicted, data.boilingPointK(), error * 100));
        }
    }

    @Test
    void hysteresis_keepsForwardAndReverseWindowsApart() {
        for (PhaseChangeData data : PhaseChangeTable.ALL) {
            List<PhaseTransition> edges = PhaseChangeTable.transitions(data);
            for (PhaseTransition.Kind forward : List.of(PhaseTransition.Kind.FUSION, PhaseTransition.Kind.VAPORIZATION)) {
                PhaseTransition forwardEdge = find(edges, forward);
                if (forwardEdge == null) continue;
                PhaseTransition reverseEdge = find(edges, forward == PhaseTransition.Kind.FUSION
                        ? PhaseTransition.Kind.FREEZING : PhaseTransition.Kind.CONDENSATION);
                assertNotNull(reverseEdge, data.key() + " 缺少 " + forward + " 的反向边");
                assertTrue(reverseEdge.maxTemperatureK() < forwardEdge.minTemperatureK(),
                        data.key() + " 的死区没留出来：反向上限 " + reverseEdge.maxTemperatureK()
                                + " 应严格低于正向下限 " + forwardEdge.minTemperatureK());
                assertEquals(data.hysteresisK(),
                        forwardEdge.minTemperatureK() - reverseEdge.maxTemperatureK(), 1e-9,
                        data.key() + " 的死区宽度应等于表中的迟滞");
            }
        }
    }

    @Test
    void edgeCounts_matchPhaseCount() {
        assertEquals(6, PhaseChangeTable.transitions(PhaseChangeTable.WATER_SYSTEM).size(),
                "水的三相应生成 6 条有向边（s⇄l、l⇄g、s⇄g）");
        assertEquals(2, PhaseChangeTable.transitions(PhaseChangeTable.SALT_SYSTEM).size(),
                "只有固液两相的物质应生成 2 条边");
        assertEquals(8, PhaseChangeTable.allTransitions().size());
    }

    @Test
    void sublimation_isKineticSuppressedAndUngated() {
        for (PhaseChangeData data : PhaseChangeTable.ALL) {
            for (PhaseTransition edge : PhaseChangeTable.transitions(data)) {
                if (edge.kind() != PhaseTransition.Kind.SUBLIMATION
                        && edge.kind() != PhaseTransition.Kind.DEPOSITION) continue;
                assertFalse(edge.hasLowerBound(), data.key() + " 的升华/凝华不应设温度下限（方向由物理 K 决定）");
                assertFalse(edge.hasUpperBound(), data.key() + " 的升华/凝华不应设温度上限");
                assertTrue(edge.preExponentialFactor() <= 1e4,
                        data.key() + " 的升华/凝华必须被动力学压制，当前 A=" + edge.preExponentialFactor());
            }
        }
    }

    @Test
    void ruleIdsAreUniqueAndWellFormed() {
        Set<String> seen = new HashSet<>();
        for (PhaseTransition edge : PhaseChangeTable.allTransitions()) {
            assertTrue(edge.ruleId().startsWith("phase_"), "相变规则 id 应以 phase_ 开头: " + edge.ruleId());
            assertTrue(seen.add(edge.ruleId()), "相变规则 id 重复: " + edge.ruleId());
            assertNotEquals(edge.fromId(), edge.toId(), "相变两端不应是同一物种");
        }
    }

    @Test
    void phasesOfSameSubstance_shareMolarMass() {
        for (PhaseChangeData data : PhaseChangeTable.ALL) {
            List<PhaseSpecies> phases = java.util.stream.Stream.of(data.solid(), data.liquid(), data.gas())
                    .filter(java.util.Objects::nonNull).toList();
            double first = phases.get(0).molarMass();
            for (PhaseSpecies phase : phases) {
                assertEquals(first, phase.molarMass(), 1e-9,
                        data.key() + " 各相摩尔质量应一致: " + phase.id());
            }
        }
    }

    private static PhaseTransition find(List<PhaseTransition> edges, PhaseTransition.Kind kind) {
        return edges.stream().filter(edge -> edge.kind() == kind).findFirst().orElse(null);
    }
}
