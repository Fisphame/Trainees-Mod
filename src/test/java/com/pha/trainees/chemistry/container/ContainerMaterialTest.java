package com.pha.trainees.chemistry.container;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 容器材料档位测试（§6.3 / ③-3）。
 *
 * <p>守两件事：① 档位顺序不得倒挂（玻璃 &lt; 金属 &lt; 石英 &lt; 陶瓷），
 * ② 关键工艺点落在预期的容器上——熔盐（1074 K）玻璃做不到、石英/陶瓷可以；
 * 炼钢区间（1600 K+）必须换坩埚。这是科技树"材料硬墙"的回归保护。</p>
 */
class ContainerMaterialTest {

    private static final double SALT_MELTING_K = 1074.0;
    private static final double STEELMAKING_K = 1600.0;

    @Test
    void tiersAreStrictlyIncreasing() {
        assertTrue(ContainerMaterial.GLASS.maxSafeTemperatureK() < ContainerMaterial.METAL.maxSafeTemperatureK(),
                "金属坩埚应比玻璃烧杯耐温更高");
        assertTrue(ContainerMaterial.METAL.maxSafeTemperatureK() < ContainerMaterial.QUARTZ.maxSafeTemperatureK(),
                "石英应比金属耐温更高");
        assertTrue(ContainerMaterial.QUARTZ.maxSafeTemperatureK() < ContainerMaterial.CERAMIC.maxSafeTemperatureK(),
                "陶瓷应是最高档");
    }

    @Test
    void glassCannotHoldMoltenSalt_butQuartzCan() {
        assertTrue(ContainerMaterial.GLASS.isOverheated(SALT_MELTING_K),
                "玻璃烧杯不得承受熔盐温度（" + SALT_MELTING_K + " K）");
        assertFalse(ContainerMaterial.QUARTZ.isOverheated(SALT_MELTING_K),
                "石英坩埚应能熔化食盐");
        assertFalse(ContainerMaterial.CERAMIC.isOverheated(SALT_MELTING_K),
                "陶瓷坩埚应能熔化食盐");
    }

    @Test
    void steelmakingRequiresACrucible() {
        assertTrue(ContainerMaterial.GLASS.isOverheated(STEELMAKING_K), "玻璃烧杯不得用于炼钢温度");
        assertFalse(ContainerMaterial.CERAMIC.isOverheated(STEELMAKING_K), "陶瓷坩埚应覆盖炼钢区间");
    }

    @Test
    void glassIsTheBaseline_noBehaviourChange() {
        assertEquals(1.0, ContainerMaterial.GLASS.capacityScale(), 1e-9,
                "玻璃档热容倍率必须为 1.0，否则会改变既有烧杯手感");
        assertEquals(1.0, ContainerMaterial.GLASS.transferScale(), 1e-9,
                "玻璃档换热倍率必须为 1.0，否则会改变既有烧杯升温速度");
    }

    @Test
    void allTiersAreSane() {
        for (ContainerMaterial material : ContainerMaterial.values()) {
            assertTrue(material.maxSafeTemperatureK() > 0, material.id() + " 的耐温上限必须为正");
            assertTrue(material.capacityScale() > 0, material.id() + " 的热容倍率必须为正");
            assertTrue(material.transferScale() > 0, material.id() + " 的换热倍率必须为正");
            assertFalse(material.id().isBlank(), "档位 id 不得为空");
        }
        // 边界：恰好等于上限不算过热，超过才算
        ContainerMaterial glass = ContainerMaterial.GLASS;
        assertFalse(glass.isOverheated(glass.maxSafeTemperatureK()));
        assertTrue(glass.isOverheated(glass.maxSafeTemperatureK() + 1e-6));
        assertNotEquals(ContainerMaterial.GLASS, ContainerMaterial.CERAMIC);
    }
}
