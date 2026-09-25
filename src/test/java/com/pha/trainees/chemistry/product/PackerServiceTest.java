package com.pha.trainees.chemistry.product;

import com.pha.trainees.chemistry.spec.ProductSpec;
import com.pha.trainees.chemistry.spec.SpecMatcher;
import com.pha.trainees.chemistry.spec.Verdict;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.ToDoubleFunction;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 打包机判定服务测试（④ 纯逻辑层）。
 *
 * <p>关键点：规格口径是**有效氯**（1 mol ClO⁻ ≡ 1 mol Cl₂），所以"次氯酸根质量分数 × 70.90/51.45"
 * 才是判定用的数字——测试同时锁住这个折算关系，避免以后有人把两者搞混。</p>
 */
class PackerServiceTest {

    private static final double TOTAL_MASS = 100.0;

    private static final String CLO = "trainees:clo_minus";
    private static final String CL2 = "trainees:cl2";
    private static final String H_PLUS = "trainees:h_plus";
    private static final String OH_MINUS = "trainees:oh_minus";
    private static final String CLO3 = "trainees:clo3_minus";
    private static final String H2O = "trainees:h2o";

    private static final double M_CLO = 51.45;
    private static final double M_CL2 = 70.90;
    private static final double M_H_PLUS = 1.008;
    private static final double M_OH_MINUS = 17.01;
    private static final double M_CLO3 = 83.45;
    private static final double M_H2O = 18.02;

    private static final ToDoubleFunction<String> MOLAR_MASS = id -> switch (id) {
        case CLO -> M_CLO;
        case CL2 -> M_CL2;
        case H_PLUS -> M_H_PLUS;
        case OH_MINUS -> M_OH_MINUS;
        case CLO3 -> M_CLO3;
        case H2O -> M_H2O;
        default -> -1.0;
    };

    private static final SpecBasisAdapter BLEACH_BASIS = SpecBasisAdapter.availableChlorine();

    // ==================== 合格 / 优质 ====================

    @Test
    void premium_atFivePercentHypochlorite() {
        // 5.0% ClO⁻ → 有效氯 5.0×70.90/51.45 = 6.89% → 落在优质窗口 6.0~7.5%
        PackOutcome outcome = auto(batch(CLO, 0.05), 1.0);

        assertEquals(Verdict.PREMIUM, outcome.result().verdict());
        assertEquals("trainees:bleach_premium", outcome.productItemId());
        assertEquals(PackOutcome.DefectKind.NONE, outcome.defect());
        assertFalse(outcome.isDefective());
    }

    @Test
    void pass_atFourPercentHypochlorite() {
        // 4.0% ClO⁻ → 有效氯 5.51% → 合格但不达优质
        PackOutcome outcome = auto(batch(CLO, 0.04), 1.0);

        assertEquals(Verdict.PASS, outcome.result().verdict());
        assertEquals("trainees:bleach_good", outcome.productItemId());
    }

    @Test
    void availableChlorineConversion_isTheJudgedNumber() {
        double basisFraction = SpecBasisAdapter.availableChlorineFraction(0.05);
        assertEquals(0.0689, basisFraction, 5e-4, "5% ClO⁻ 应折算为约 6.89% 有效氯");
        assertEquals(1.378, SpecBasisAdapter.EQUIVALENT_MOLAR_MASS / M_CLO, 1e-3,
                "有效氯/次氯酸根的当量比应约为 1.378（70.90/51.45）");
    }

    // ==================== 不合格：自动拒收 / 强制灌装出次品 ====================

    @Test
    void tooWeak_neverProduces_evenWhenForced() {
        Map<String, Double> weak = batch(CLO, 0.03);   // 有效氯 4.13% < 5%

        PackOutcome auto = auto(weak, 1.0);
        assertEquals(Verdict.REJECTED, auto.result().verdict());
        assertFalse(auto.producesItem(), "自动模式不合格不得产出");
        assertEquals(PackOutcome.DefectKind.OFF_SPEC, auto.defect());

        // §19.18 决策：强制灌装**只为危险失败**产出次品；浓度不符既不危险也无教学价值 → 任何模式都不灌装
        PackOutcome forced = forced(weak, 1.0);
        assertFalse(forced.producesItem(), "浓度不符不得产出（次品只能是危险品）");
        assertFalse(forced.isDefective());
        assertEquals(PackOutcome.DefectKind.OFF_SPEC, forced.defect());
        assertEquals("message.trainees.packer.off_spec_no_fill", forced.reasonKey());
    }

    @Test
    void tooStrong_autoRejects() {
        Map<String, Double> strong = batch(CLO, 0.06);   // 有效氯 8.27% > 8%
        PackOutcome outcome = auto(strong, 1.0);

        assertEquals(Verdict.REJECTED, outcome.result().verdict());
        assertFalse(outcome.producesItem());
        assertEquals(PackOutcome.DefectKind.OFF_SPEC, outcome.defect());

        // 浓度"过高"同样属于浓度不符：不引入"太浓"次品
        assertFalse(forced(strong, 1.0).producesItem());
    }

    @Test
    void freeChlorine_isHazardousDefectiveNotJustOffSpec() {
        Map<String, Double> gassed = batch(CLO, 0.05, CL2, 0.005);

        PackOutcome outcome = forced(gassed, 1.0);

        assertEquals(Verdict.REJECTED, outcome.result().verdict());
        assertEquals(PackOutcome.DefectKind.CHLORINE, outcome.defect(), "游离氯超标必须归为危险次品");
        assertEquals("trainees:defective_chlorine", outcome.productItemId());
    }

    @Test
    void freeAcid_isAcidDefective() {
        PackOutcome outcome = forced(batch(CLO, 0.05, H_PLUS, 0.001), 1.0);

        assertEquals(PackOutcome.DefectKind.ACID, outcome.defect());
        assertEquals("trainees:defective_acid", outcome.productItemId());
    }

    @Test
    void excessAlkali_isAlkaliDefective() {
        PackOutcome outcome = forced(batch(CLO, 0.05, OH_MINUS, 0.02), 1.0);

        assertEquals(PackOutcome.DefectKind.ALKALI, outcome.defect());
        assertEquals("trainees:defective_alkali", outcome.productItemId());
    }

    @Test
    void chlorateImpurity_isChlorateDefective() {
        PackOutcome outcome = forced(batch(CLO, 0.05, CLO3, 0.02), 1.0);

        assertEquals(PackOutcome.DefectKind.CHLORATE, outcome.defect(),
                "氯酸盐是危险杂质（氧化性/毒性），单独一类次品");
        assertEquals("trainees:defective_chlorate", outcome.productItemId());
    }

    @Test
    void hazardWinsOverOffSpec_regardlessOfRuleOrder() {
        // 有效氯不足 + 游离氯超标同时存在：必须报"氯"，因为玩家先要知道会放毒
        PackOutcome outcome = forced(batch(CLO, 0.03, CL2, 0.005), 1.0);

        assertEquals(PackOutcome.DefectKind.CHLORINE, outcome.defect());
    }

    @Test
    void traceAcid_isStillPremium() {
        PackOutcome outcome = auto(batch(CLO, 0.05, H_PLUS, 1e-10), 1.0);

        assertEquals(Verdict.PREMIUM, outcome.result().verdict(), "痕量酸不应否决产品");
        assertEquals("trainees:bleach_premium", outcome.productItemId());
    }

    // ==================== 量不足 / 空容器 ====================

    @Test
    void notEnough_neverProducesEvenWhenForced() {
        Map<String, Double> good = batch(CLO, 0.05);

        PackOutcome auto = auto(good, 0.5);
        assertEquals(Verdict.NOT_ENOUGH, auto.result().verdict());
        assertFalse(auto.producesItem());

        PackOutcome forced = forced(good, 0.5);
        assertFalse(forced.producesItem(), "灌不满瓶时谈不上合格品或次品");
        assertEquals(PackOutcome.DefectKind.NONE, forced.defect());
    }

    @Test
    void emptyContainer_rejectedWithoutProduct() {
        PackOutcome outcome = auto(Map.of(), 1.0);

        assertEquals(Verdict.REJECTED, outcome.result().verdict());
        assertFalse(outcome.producesItem());
        assertEquals(PackOutcome.DefectKind.NONE, outcome.defect());
    }

    // ==================== 口径适配器 ====================

    @Test
    void adapter_movesHypochloriteOntoAvailableChlorine() {
        Map<String, Double> contents = batch(CLO, 0.05, CL2, 0.001);

        Map<String, Double> basis = BLEACH_BASIS.toSpecBasis(contents);

        assertFalse(basis.containsKey(CLO), "折算后不应再有游离的 ClO⁻ 条目");
        assertEquals(contents.get(CLO), basis.get(SpecBasisAdapter.AVAILABLE_CHLORINE_ID), 1e-12);
        assertEquals(contents.get(CL2), basis.get(CL2), 1e-12, "其余物种必须原样保留，限值才生效");
        assertEquals(SpecBasisAdapter.EQUIVALENT_MOLAR_MASS,
                BLEACH_BASIS.molarMassOf(MOLAR_MASS).applyAsDouble(SpecBasisAdapter.AVAILABLE_CHLORINE_ID), 1e-9);
        assertEquals(M_CL2, BLEACH_BASIS.molarMassOf(MOLAR_MASS).applyAsDouble(CL2), 1e-9);
    }

    @Test
    void identityAdapter_keepsSpeciesUntouched() {
        Map<String, Double> contents = batch(CLO, 0.05);
        Map<String, Double> basis = SpecBasisAdapter.identity().toSpecBasis(contents);

        assertEquals(contents, basis);
        assertSame(MOLAR_MASS, SpecBasisAdapter.identity().molarMassOf(MOLAR_MASS));
    }

    // ==================== 规格表自检 ====================

    @Test
    void allShippedSpecs_areStructurallyValid() {
        for (ProductSpec spec : ProductSpecs.ALL) {
            assertTrue(SpecMatcher.validate(spec).isEmpty(),
                    () -> "规格 " + spec.id() + " 结构非法: " + SpecMatcher.validate(spec));
        }
    }

    @Test
    void specIndex_resolvesBothIdForms() {
        assertNotNull(ProductSpecs.byId("trainees:bleach"));
        assertNotNull(ProductSpecs.byId("bleach"));
        assertNull(ProductSpecs.byId("trainees:nonexistent"));
        assertEquals(ProductSpecs.BLEACH, ProductSpecs.byId("bleach"));
        assertSame(BLEACH_BASIS.getClass(), ProductSpecs.adapterFor("bleach").getClass());
    }

    @Test
    void convenienceEntry_matchesExplicitAdapterCall() {
        Map<String, Double> contents = batch(CLO, 0.05);
        PackOutcome viaId = PackerService.evaluateAuto("trainees:bleach", contents, MOLAR_MASS, 1.0);
        PackOutcome explicit = PackerService.evaluateAuto(
                ProductSpecs.BLEACH, BLEACH_BASIS, contents, MOLAR_MASS, 1.0);

        assertEquals(explicit.productItemId(), viaId.productItemId());
        assertEquals(explicit.result().verdict(), viaId.result().verdict());
    }

    // ==================== 辅助 ====================

    private static PackOutcome auto(Map<String, Double> contents, double usableMoles) {
        return PackerService.evaluateAuto(ProductSpecs.BLEACH, BLEACH_BASIS, contents, MOLAR_MASS, usableMoles);
    }

    private static PackOutcome forced(Map<String, Double> contents, double usableMoles) {
        return PackerService.evaluateForced(ProductSpecs.BLEACH, BLEACH_BASIS, contents, MOLAR_MASS, usableMoles);
    }

    /** 按质量分数配一批 100 g 溶液（水补足），保证总质量恰为 100 g 便于手算。 */
    private static Map<String, Double> batch(Object... idFractionPairs) {
        Map<String, Double> map = new LinkedHashMap<>();
        double used = 0.0;
        for (int i = 0; i < idFractionPairs.length; i += 2) {
            String id = (String) idFractionPairs[i];
            double fraction = ((Number) idFractionPairs[i + 1]).doubleValue();
            if (fraction == 0.0) continue;
            map.put(id, SpecMatcher.molesFor(fraction, TOTAL_MASS, MOLAR_MASS.applyAsDouble(id)));
            used += fraction;
        }
        double waterFraction = 1.0 - used;
        if (waterFraction > 1e-9) {
            map.put(H2O, SpecMatcher.molesFor(waterFraction, TOTAL_MASS, M_H2O));
        }
        return map;
    }
}
