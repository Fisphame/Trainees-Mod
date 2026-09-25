package com.pha.trainees.chemistry.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * 离子 id → 化学式显示名（§19.18.2 新增离子必须能自动显示，无需语言键）。
 * 这条测试同时锁住命名约定：价态用 plus/minus、原子数用下标、物态用 molten/gas 后缀。
 */
class IonDisplayTest {

    @Test
    void newChlorineSpecies_displayCorrectly() {
        assertEquals("ClO⁻", IonDisplay.format("clo_minus"));
        assertEquals("ClO₃⁻", IonDisplay.format("clo3_minus"), "数字应转下标，不能吃掉 3");
    }

    @Test
    void existingConventions_stillHold() {
        assertEquals("H⁺", IonDisplay.format("h_plus"));
        assertEquals("SO₄²⁻", IonDisplay.format("so4_2minus"));
        assertEquals("H₂O", IonDisplay.format("h2o"));
        assertEquals("CO₂", IonDisplay.format("co2"), "co 必须解析为 C+O，而非钴");
        assertEquals("NaOH(熔)", IonDisplay.format("naoh_molten"));
        assertEquals("HCl(aq)", IonDisplay.format("hcl_aq"));
    }
}
