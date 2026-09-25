package com.pha.trainees.chemistry.spec;

import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.ToDoubleFunction;

import static com.pha.trainees.chemistry.spec.SpecResult.RuleResult.Kind;
import static org.junit.jupiter.api.Assertions.*;

/**
 * 规格匹配纯逻辑层测试（§19.18 Step ① 规格底座）。
 *
 * <p>验收锚点（含氯消毒液，按质量分数 w/w）：有效氯 5%~8% 为合格窗口，7%~8% 为优质窗口，
 * 游离氯禁用，游离酸 ≤ 0.1%。每批按 100 g 配料，水补足，保证 totalMass 恰为 100 g 便于手算。</p>
 */
class SpecMatcherTest {

    private static final double EPS = 1e-9;
    private static final double TOTAL_MASS = 100.0;

    private static final String NACLO = "trainees:naclo";
    private static final String CL2 = "trainees:cl2";
    private static final String HCL = "trainees:hcl";
    private static final String NACL = "trainees:nacl";
    private static final String H2O = "trainees:h2o";

    private static final double M_NACLO = 74.44;
    private static final double M_CL2 = 70.90;
    private static final double M_HCL = 36.46;
    private static final double M_NACL = 58.44;
    private static final double M_H2O = 18.02;

    /** 摩尔质量表：未登记的 id 返回 -1（模拟注册表查不到），由被测代码决定怎么处理。 */
    private static final ToDoubleFunction<String> MOLAR_MASS = id -> switch (id) {
        case NACLO -> M_NACLO;
        case CL2 -> M_CL2;
        case HCL -> M_HCL;
        case NACL -> M_NACL;
        case H2O -> M_H2O;
        default -> -1.0;
    };

    private static final ProductSpec SPEC = new ProductSpec(
            "trainees:sodium_hypochlorite",
            List.of(new FractionRule(NACLO, 0.05, 0.08)),          // 有效氯窗口
            List.of(new FractionRule(CL2, 0.0, 0.0),               // 禁用：游离氯
                    new FractionRule(HCL, 0.0, 0.001)),            // 痕量游离酸上限
            1.0,                                                    // 每瓶 1 mol
            "trainees:sodium_hypochlorite_good",
            "trainees:sodium_hypochlorite_premium",
            List.of(new FractionRule(NACLO, 0.07, 0.08)),          // 优质档：更窄子区间
            "",
            "");

    // ==================== 合格线 ====================

    @Test
    void pass_atSixPointFivePercent() {
        SpecResult result = match(SPEC, 1.0, NACLO, 0.065);

        assertEquals(Verdict.PASS, result.verdict());
        assertTrue(result.acceptable(), "PASS 应当可以出货");
        assertEquals(100.0, result.totalMass(), 1e-6, "水补足后总质量应为 100 g");
        assertEquals(4, result.details().size(), "required(1) + limits(2) + premium(1) 都应出现在 details 里");
        for (SpecResult.RuleResult detail : result.details()) {
            if (detail.kind() == Kind.PREMIUM) continue;   // 6.5% 不在优质窗口内，只影响是否 PREMIUM
            assertEquals(SpecResult.RuleResult.Status.OK, detail.status(), "不应有规则失败: " + detail);
        }
    }

    @Test
    void belowMin_atThreePointTwoPercent() {
        SpecResult result = match(SPEC, 1.0, NACLO, 0.032);

        assertEquals(Verdict.REJECTED, result.verdict());
        assertEquals(SpecResult.RuleResult.Status.BELOW_MIN, rule(result, NACLO, Kind.REQUIRED).status());
        assertEquals(0.032, rule(result, NACLO, Kind.REQUIRED).actualFraction(), 1e-6);
    }

    @Test
    void aboveMax_atTwelvePercent() {
        SpecResult result = match(SPEC, 1.0, NACLO, 0.12);

        assertEquals(Verdict.REJECTED, result.verdict());
        assertEquals(SpecResult.RuleResult.Status.ABOVE_MAX, rule(result, NACLO, Kind.REQUIRED).status());
    }

    @Test
    void premium_atEightPercent() {
        SpecResult result = match(SPEC, 1.0, NACLO, 0.08);

        assertEquals(Verdict.PREMIUM, result.verdict(), "8% 同时满足合格与优质窗口");
        assertEquals(SpecResult.RuleResult.Status.OK, rule(result, NACLO, Kind.PREMIUM).status());
    }

    @Test
    void pass_notPremium_whenOutsidePremiumWindow() {
        SpecResult result = match(SPEC, 1.0, NACLO, 0.06);

        assertEquals(Verdict.PASS, result.verdict());
        assertEquals(SpecResult.RuleResult.Status.BELOW_MIN, rule(result, NACLO, Kind.PREMIUM).status(),
                "优质档不达标只降级为 PASS，不得判 REJECTED");
    }

    // ==================== 限值 ====================

    @Test
    void forbiddenChlorine_rejects() {
        SpecResult result = match(SPEC, 1.0, NACLO, 0.065, CL2, 0.005);

        assertEquals(Verdict.REJECTED, result.verdict());
        assertEquals(SpecResult.RuleResult.Status.PRESENT_FORBIDDEN, rule(result, CL2, Kind.LIMIT).status(),
                "上限为 0 的禁用物质应单独给出 PRESENT_FORBIDDEN");
    }

    @Test
    void traceAcid_isOk() {
        SpecResult result = match(SPEC, 1.0, NACLO, 0.065, HCL, 1e-10);

        assertEquals(Verdict.PASS, result.verdict());
        assertEquals(SpecResult.RuleResult.Status.OK, rule(result, HCL, Kind.LIMIT).status());
    }

    @Test
    void absentLimitSpecies_isOk_notUnknown() {
        // 限值物种不在容器里 = 摩尔数 0，不是"无法解析"，不得误报 UNKNOWN_MASS
        SpecResult result = match(SPEC, 1.0, NACLO, 0.065);

        assertEquals(SpecResult.RuleResult.Status.OK, rule(result, CL2, Kind.LIMIT).status());
        assertEquals(0.0, rule(result, CL2, Kind.LIMIT).actualFraction(), EPS);
    }

    @Test
    void allRulesEvaluated_neverShortCircuits() {
        // 有效成分不足 + 禁用物同时存在：两条都要出现在 details 里（GUI 才能一次说清）
        SpecResult result = match(SPEC, 1.0, NACLO, 0.032, CL2, 0.005);

        assertEquals(Verdict.REJECTED, result.verdict());
        assertEquals(SpecResult.RuleResult.Status.BELOW_MIN, rule(result, NACLO, Kind.REQUIRED).status());
        assertEquals(SpecResult.RuleResult.Status.PRESENT_FORBIDDEN, rule(result, CL2, Kind.LIMIT).status());
    }

    // ==================== 未申报物种 / othersFraction ====================

    @Test
    void foreignSalt_doesNotChangeVerdict() {
        SpecResult withoutSalt = match(SPEC, 1.0, NACLO, 0.065);
        SpecResult withSalt = match(SPEC, 1.0, NACLO, 0.065, NACL, 0.30);

        assertEquals(withoutSalt.verdict(), withSalt.verdict(), "副产盐不参与规格判定");
        assertEquals(Verdict.PASS, withSalt.verdict());
    }

    @Test
    void othersFraction_countsSolventAndForeignSolutes() {
        SpecResult result = match(SPEC, 1.0, NACLO, 0.065, NACL, 0.30);

        // 未申报 = 副产盐 30% + 溶剂水 63.5% = 93.5%：分母是全容器总质量，水虽"无害"但确实未被声明
        assertEquals(0.935, result.othersFraction(), 1e-6);
        assertEquals(0.065, rule(result, NACLO, Kind.REQUIRED).actualFraction(), 1e-6);
    }

    @Test
    void othersFraction_isComplementOfDeclaredFractions() {
        SpecResult result = match(SPEC, 1.0, NACLO, 0.065, NACL, 0.30);

        double declared = result.details().stream()
                .filter(detail -> detail.kind() != Kind.PREMIUM)   // premium 与 required 同 id，不得重复计入
                .mapToDouble(SpecResult.RuleResult::actualFraction)
                .sum();
        assertEquals(1.0, declared + result.othersFraction(), 1e-9,
                "申报质量占比 + othersFraction 必须等于 1（分母是全容器总质量）");
    }

    // ==================== 瓶量 ====================

    @Test
    void notEnough_whenUsableMolesBelowBottle() {
        SpecResult result = match(SPEC, 0.5, NACLO, 0.065);

        assertEquals(Verdict.NOT_ENOUGH, result.verdict());
        assertEquals(4, result.details().size(), "量不够也要把成分细节算完，GUI 需要展示");
    }

    @Test
    void exactlyOneBottle_passes() {
        assertEquals(Verdict.PASS, match(SPEC, 1.0, NACLO, 0.065).verdict());
    }

    @Test
    void rejectedBeatsNotEnough() {
        // 成分不合格 + 量不够：verdict 取 REJECTED（质量优先于数量），但细节仍在
        SpecResult result = match(SPEC, 0.5, NACLO, 0.12);

        assertEquals(Verdict.REJECTED, result.verdict());
    }

    // ==================== 空容器与非法输入 ====================

    @Test
    void emptyContainer_rejectedWithoutCrash() {
        SpecResult result = SpecMatcher.evaluate(SPEC, Map.of(), MOLAR_MASS, 1.0);

        assertEquals(Verdict.REJECTED, result.verdict());
        assertEquals(0.0, result.totalMass(), EPS);
        assertEquals(0.0, result.othersFraction(), EPS);
        assertTrue(result.details().isEmpty(), "空容器不进入规则评估，避免 0/0");
    }

    @Test
    void zeroMolesContainer_rejected() {
        Map<String, Double> map = new LinkedHashMap<>();
        map.put(NACLO, 0.0);
        map.put(H2O, 0.0);

        SpecResult result = SpecMatcher.evaluate(SPEC, map, MOLAR_MASS, 1.0);

        assertEquals(Verdict.REJECTED, result.verdict());
        assertEquals(0.0, result.totalMass(), EPS);
    }

    @Test
    void badMoles_throw() {
        Map<String, Double> nan = new LinkedHashMap<>();
        nan.put(NACLO, Double.NaN);
        assertThrows(IllegalArgumentException.class, () -> SpecMatcher.evaluate(SPEC, nan, MOLAR_MASS, 1.0));

        Map<String, Double> negative = new LinkedHashMap<>();
        negative.put(NACLO, -1.0);
        assertThrows(IllegalArgumentException.class, () -> SpecMatcher.evaluate(SPEC, negative, MOLAR_MASS, 1.0));
    }

    @Test
    void unknownMolarMassOfPresentSpecies_throws() {
        // 事实侧（容器里真的有）取不到摩尔质量 = 注册表/调用侧错误，抛而不是静默
        Map<String, Double> map = new LinkedHashMap<>();
        map.put("trainees:unregistered", 1.0);

        assertThrows(IllegalArgumentException.class, () -> SpecMatcher.evaluate(SPEC, map, MOLAR_MASS, 1.0));
    }

    @Test
    void badArguments_throw() {
        assertThrows(IllegalArgumentException.class,
                () -> SpecMatcher.evaluate(null, Map.of(), MOLAR_MASS, 1.0));
        assertThrows(IllegalArgumentException.class,
                () -> SpecMatcher.evaluate(SPEC, null, MOLAR_MASS, 1.0));
        assertThrows(IllegalArgumentException.class,
                () -> SpecMatcher.evaluate(SPEC, Map.of(), null, 1.0));
        assertThrows(IllegalArgumentException.class,
                () -> SpecMatcher.evaluate(SPEC, Map.of(), MOLAR_MASS, -0.1));
    }

    // ==================== 数据错误：失败关闭 ====================

    @Test
    void typoIdInLimits_rejectsInsteadOfSilentlyPassing() {
        ProductSpec typo = new ProductSpec(
                "trainees:typo_spec",
                List.of(new FractionRule(NACLO, 0.05, 0.08)),
                List.of(new FractionRule("trainees:cl2_typo", 0.0, 0.0)),   // 限值写错 id
                1.0, "trainees:good", "", List.of(), "", "");

        SpecResult result = match(typo, 1.0, NACLO, 0.065);

        assertEquals(Verdict.REJECTED, result.verdict(), "写错 id 的限值绝不能静默通过");
        assertEquals(SpecResult.RuleResult.Status.UNKNOWN_MASS, rule(result, "trainees:cl2_typo", Kind.LIMIT).status());
    }

    @Test
    void namespaceConventionMismatch_rejects() {
        // 规格用完整 id，而容器内容物用了不带命名空间的 key：属于集成错误，失败关闭
        Map<String, Double> loose = new LinkedHashMap<>();
        loose.put("naclo", SpecMatcher.molesFor(0.065, TOTAL_MASS, M_NACLO));
        loose.put("h2o", SpecMatcher.molesFor(0.935, TOTAL_MASS, M_H2O));
        ToDoubleFunction<String> looseMasses =
                id -> MOLAR_MASS.applyAsDouble(id.startsWith("trainees:") ? id : "trainees:" + id);

        SpecResult result = SpecMatcher.evaluate(SPEC, loose, looseMasses, 1.0);

        assertEquals(Verdict.REJECTED, result.verdict());
        assertEquals(SpecResult.RuleResult.Status.UNKNOWN_MASS, rule(result, NACLO, Kind.REQUIRED).status(),
                "id 写法不一致必须暴露为 UNKNOWN_MASS，而不是当成 0");
    }

    // ==================== 规格结构校验 ====================

    @Test
    void validate_cleanSpecHasNoErrors() {
        assertTrue(SpecMatcher.validate(SPEC).isEmpty(), () -> "合法规格不应报错: " + SpecMatcher.validate(SPEC));
    }

    @Test
    void validate_catchesStructuralProblems() {
        ProductSpec broken = new ProductSpec(
                " ",
                List.of(new FractionRule(NACLO, 0.09, 0.05),      // 窗口倒置
                        new FractionRule(NACLO, 0.0, 0.1)),        // 同清单内重复 id
                List.of(),
                0.0,                                               // bottleMoles 非正
                "",                                                // goodItem 为空
                "",
                List.of(new FractionRule(NACLO, 0.01, 0.20)),      // premium 比 required 还宽（且 required 已空）
                "",
                "");

        List<String> errors = SpecMatcher.validate(broken);

        assertTrue(errors.stream().anyMatch(e -> e.contains("spec.id")), errors.toString());
        assertTrue(errors.stream().anyMatch(e -> e.contains("goodItem")), errors.toString());
        assertTrue(errors.stream().anyMatch(e -> e.contains("bottleMoles")), errors.toString());
        assertTrue(errors.stream().anyMatch(e -> e.contains("重复")), errors.toString());
        assertTrue(errors.stream().anyMatch(e -> e.contains("窗口倒置")), errors.toString());
        assertTrue(errors.stream().anyMatch(e -> e.contains("premiumItem")), errors.toString());
    }

    @Test
    void validate_requiresAtLeastOneTooth() {
        ProductSpec toothless = new ProductSpec(
                "trainees:toothless", List.of(), List.of(), 1.0,
                "trainees:good", "", List.of(), "", "");

        assertTrue(SpecMatcher.validate(toothless).stream().anyMatch(e -> e.contains("必须有牙齿")),
                "required 与 limits 同时为空的规格没有意义");
    }

    @Test
    void validate_allowsTradeOnlyProductWithLimitsOnly() {
        // §19.18 方案 C：贸易品可以只有安全门禁，没有有效成分窗口
        ProductSpec tradeOnly = new ProductSpec(
                "trainees:trade_only", List.of(), List.of(new FractionRule(CL2, 0.0, 0.0)), 1.0,
                "trainees:good", "", List.of(), "", "");

        assertTrue(SpecMatcher.validate(tradeOnly).isEmpty(), () -> SpecMatcher.validate(tradeOnly).toString());
    }

    @Test
    void validate_handlesNullListsAndNullSpec() {
        assertFalse(SpecMatcher.validate(null).isEmpty());
        ProductSpec nullLists = new ProductSpec("trainees:x", null, null, 1.0,
                "trainees:good", "", null, "", "");
        List<String> errors = SpecMatcher.validate(nullLists);
        assertTrue(errors.stream().anyMatch(e -> e.contains("required 为 null")), errors.toString());
        assertTrue(errors.stream().anyMatch(e -> e.contains("limits 为 null")), errors.toString());
    }

    // ==================== 辅助 ====================

    /** 按质量分数配一批 100 g 溶液并判定；水补足剩余质量。 */
    private static SpecResult match(ProductSpec spec, double usableMoles, Object... idFractionPairs) {
        return SpecMatcher.evaluate(spec, topUpWithWater(pairs(idFractionPairs)), MOLAR_MASS, usableMoles);
    }

    private static Map<String, Double> pairs(Object... idFractionPairs) {
        Map<String, Double> map = new LinkedHashMap<>();
        for (int i = 0; i < idFractionPairs.length; i += 2) {
            String id = (String) idFractionPairs[i];
            double fraction = ((Number) idFractionPairs[i + 1]).doubleValue();
            if (fraction == 0.0) continue;
            map.put(id, SpecMatcher.molesFor(fraction, TOTAL_MASS, MOLAR_MASS.applyAsDouble(id)));
        }
        return map;
    }

    private static Map<String, Double> topUpWithWater(Map<String, Double> map) {
        double declaredMass = 0.0;
        for (Map.Entry<String, Double> entry : map.entrySet()) {
            declaredMass += entry.getValue() * MOLAR_MASS.applyAsDouble(entry.getKey());
        }
        double waterFraction = (TOTAL_MASS - declaredMass) / TOTAL_MASS;
        if (waterFraction > 1e-9) {
            map.put(H2O, SpecMatcher.molesFor(waterFraction, TOTAL_MASS, M_H2O));
        }
        return map;
    }

    private static SpecResult.RuleResult rule(SpecResult result, String ionId, SpecResult.RuleResult.Kind kind) {
        return result.details().stream()
                .filter(detail -> detail.ionId().equals(ionId) && detail.kind() == kind)
                .findFirst()
                .orElseThrow(() -> new AssertionError("details 缺少规则结果: " + kind + " " + ionId));
    }
}
