package com.pha.trainees.chemistry.util;

import com.pha.trainees.chemistry.particle.IonType;
import com.pha.trainees.chemistry.reaction.ReactionMath;
import com.pha.trainees.chemistry.reaction.ReactionRule;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 反应的条件 / 规划参考文本（§19.16 条件区定稿），供 JEI 分类与将来的分析仪共用。
 *
 * <p>分两组：</p>
 * <ul>
 *   <li><b>最低可进行条件（门槛）</b>：最低温度、前置/催化剂（含所需摩尔数）、通电要求；</li>
 *   <li><b>规划参考（非门槛）</b>：ΔH（放热/吸热）、Ea、K(298 K)——给玩家自己权衡"多快、要不要强冷"。</li>
 * </ul>
 *
 * <p>只读、纯展示：不读取玩家容器状态（JEI 是静态百科；"你现在够不够"由分析仪回答）。</p>
 */
public final class ReactionInfo {

    private ReactionInfo() {}

    /** 条件区全部文本行（已本地化、含颜色） */
    public static List<Component> conditionLines(ReactionRule rule) {
        List<Component> lines = new ArrayList<>();

        // ---- 最低可进行条件 ----
        lines.add(Component.translatable("jei.trainees.reaction.cond.header")
                .withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD));
        lines.add(Component.translatable("jei.trainees.reaction.cond.min_temp",
                fmt(rule.getMinTemperature(), 0)));
        for (Map.Entry<IonType, Integer> e : rule.getPreconditions().entrySet()) {
            lines.add(Component.translatable("jei.trainees.reaction.cond.precondition",
                    IonDisplay.format(e.getKey().getId().getPath()), String.valueOf(e.getValue())));
        }
        if (rule.getElectricalWorkPerMol() > 0) {
            lines.add(Component.translatable("jei.trainees.reaction.cond.power",
                    fmt(rule.getElectricalWorkPerMol(), 1)));
        }

        // ---- 规划参考（非门槛） ----
        lines.add(Component.translatable("jei.trainees.reaction.cond.header2")
                .withStyle(ChatFormatting.DARK_GRAY));
        double deltaH = rule.getDeltaH();
        String thermal = Component.translatable(deltaH < 0
                ? "jei.trainees.reaction.cond.exothermic"
                : "jei.trainees.reaction.cond.endothermic").getString();
        lines.add(Component.translatable("jei.trainees.reaction.cond.delta_h",
                fmt(deltaH, 1), thermal));
        lines.add(Component.translatable("jei.trainees.reaction.cond.ea",
                fmt(rule.getActivationEnergy(), 1)));
        lines.add(Component.translatable("jei.trainees.reaction.cond.k",
                sci(equilibriumAt298(rule))));
        return lines;
    }

    /**
     * 298 K 平衡常数（纯数学层计算，不依赖容器）：
     * 电解规则按"理论电功"折算；显式覆盖走范特霍夫；否则物理公式。
     */
    public static double equilibriumAt298(ReactionRule rule) {
        double t = ReactionMath.T0;
        if (rule.getElectricalWorkPerMol() > 0) {
            return ReactionMath.equilibriumConstantElectrolysis(
                    rule.getDeltaG(), rule.getElectricalWorkPerMol(), t);
        }
        if (rule.isEquilibriumOverridden()) {
            return ReactionMath.equilibriumConstantVanHoff(
                    rule.getEquilibriumConstant(), rule.getDeltaH(), t);
        }
        return ReactionMath.equilibriumConstantPhysical(rule.getDeltaH(), rule.getDeltaG(), t);
    }

    private static String fmt(double v, int decimals) {
        return String.format("%." + decimals + "f", v);
    }

    private static String sci(double v) {
        if (!Double.isFinite(v)) return "∞";
        return String.format("%.2e", v);
    }
}
