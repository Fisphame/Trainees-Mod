package com.pha.trainees.chemistry.container;

/**
 * 容器材料档位（§6.3 材料分级，③-3）。
 *
 * <p><b>为什么需要它</b>：在此之前烧杯的耐温上限只有一个全局配置值（默认 1800 K），
 * 于是"玻璃烧杯能不能炼钢"这种问题不存在——所有物质都能用同一个容器做到任意温度。
 * 材料分级把**耐温上限**变成科技树的第一道硬墙（§19.23）：炼钢要 1600 K+，玻璃撑不住，
 * 必须先有石英/陶瓷坩埚。</p>
 *
 * <p><b>现实依据</b>（长期安全工作温度，非熔点）：硼硅玻璃 ≈500 ℃；耐热钢 ≈1400 ℃；
 * 石英玻璃 ≈1600 ℃ 且热震稳定性极好；氧化铝陶瓷 ≈2000 ℃。三个坩埚方块共用烧杯引擎，
 * 仅材料不同——所以只有这一张表是真源，方块只是给它挂一个名字。</p>
 *
 * @param id                档位 id（调试/展示用）
 * @param maxSafeTemperatureK 容器的安全温度上限 (K)：超过后温度被钳制（材质撑不住）
 * @param capacityScale     热容倍率（乘在全局基准热容上；越厚重升降温越慢）
 * @param transferScale     换热倍率（乘在全局换热系数上；金属导热最快）
 */
public enum ContainerMaterial {

    /** 硼硅玻璃烧杯：耐温最低，但透明可视 */
    GLASS("glass", 773.0, 1.0, 1.0),
    /** 耐热钢坩埚：导热快、热容小，能到炼钢区间 */
    METAL("metal", 1673.0, 0.8, 1.5),
    /** 石英坩埚：耐温高、热震稳定性好 */
    QUARTZ("quartz", 1873.0, 1.3, 1.1),
    /** 陶瓷（氧化铝）坩埚：耐温最高，但厚重、升降温慢 */
    CERAMIC("ceramic", 2273.0, 1.6, 1.2);

    private final String id;
    private final double maxSafeTemperatureK;
    private final double capacityScale;
    private final double transferScale;

    ContainerMaterial(String id, double maxSafeTemperatureK, double capacityScale, double transferScale) {
        this.id = id;
        this.maxSafeTemperatureK = maxSafeTemperatureK;
        this.capacityScale = capacityScale;
        this.transferScale = transferScale;
    }

    public String id() {
        return id;
    }

    public double maxSafeTemperatureK() {
        return maxSafeTemperatureK;
    }

    public double capacityScale() {
        return capacityScale;
    }

    public double transferScale() {
        return transferScale;
    }

    /** 该材料在给定温度下是否已经超出安全范围（诊断/警告用）。 */
    public boolean isOverheated(double temperatureK) {
        return temperatureK > maxSafeTemperatureK;
    }
}
