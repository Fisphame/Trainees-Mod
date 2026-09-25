package com.pha.trainees.chemistry.heat;

/**
 * 设定点型热源（§19.24 热源分级）。
 *
 * <p><b>与"燃料型热源"的区别</b>：火/岩浆/营火只能**单向把容器烤热**，且温度由燃料天然决定
 * （语义是"最高能给到多热"）；而恒温浴、加热套、低温槽、冷冻混合物这类设备，语义是
 * **设定点（setpoint）**——它同时具备加热与吸热能力，容器会稳定在设定点附近，
 * 而不是一路冲向热源自身的温度。现实实验室的精确控温（水浴/油浴/沙浴/加热套/恒温循环槽）
 * 全部属于后者。</p>
 *
 * <p>实现方应为方块实体（BlockEntity）。烧杯每 tick 检查**正下方**的方块是否实现本接口：
 * 若实现且处于工作状态，则按设定点做双向换热；否则回落到燃料型热源表。</p>
 */
public interface IHeatSource {

    /** 目标温度（K）。容器会向它趋近：高于容器温度则加热，低于则吸热冷却。 */
    double getSourceTemperature();

    /** 是否处于工作状态（关机、缺少介质时应返回 false，容器按"无热源"处理）。 */
    default boolean isHeatSourceActive() {
        return true;
    }

    /** 目标温度是否可被玩家/命令设定（恒温设备为 true，被动热源为 false）。 */
    default boolean isSettable() {
        return false;
    }

    /**
     * 设定目标温度。
     *
     * @return 是否成功设定；不可设定的实现直接返回 false
     */
    default boolean setSourceTemperature(double kelvin) {
        return false;
    }
}
