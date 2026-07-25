package com.pha.trainees.chemistry.item;

import com.pha.trainees.chemistry.blockentity.BeakerBlockEntity;
import com.pha.trainees.chemistry.particle.IonType;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 普通分析仪
 * 显示简化信息：温度、主要成分（前3种）
 */
public class AnalyzerItemNormal extends AnalyzerItem {

    private static final DecimalFormat DF = new DecimalFormat("#0.00");

    public AnalyzerItemNormal(Properties properties) {
        super(properties);
    }

    @Override
    public List<Component> getAnalysisResult(BeakerBlockEntity beaker) {
        List<Component> lines = new ArrayList<>();

        // 标题行
        lines.add(Component.literal("§6§l=== 烧杯分析报告 ==="));

        // 温度
        double tempK = beaker.getTemperature();
        double tempC = tempK - 273.15;
        lines.add(Component.literal("§e温度: §f" + DF.format(tempK) + " K (§7" + DF.format(tempC) + "°C§f)"));

        // 体积
        lines.add(Component.literal("§e体积: §f" + DF.format(beaker.getVolume()) + " L"));

        // 内容物（主要成分，取前3种）
        Map<IonType, Double> contents = beaker.getContents();
        if (contents.isEmpty()) {
            lines.add(Component.literal("§7内容物: 空"));
        } else {
            lines.add(Component.literal("§e内容物:"));
            // 按物质的量从大到小排序，取前3
            List<Map.Entry<IonType, Double>> sorted = contents.entrySet().stream()
                    .sorted((a, b) -> Double.compare(b.getValue(), a.getValue()))
                    .limit(3)
                    .toList();
            for (Map.Entry<IonType, Double> entry : sorted) {
                IonType ion = entry.getKey();
                double moles = entry.getValue();
                String ionName = ion.getId().getPath();
                lines.add(Component.literal("  §7" + ionName + ": §f" + DF.format(moles) + " mol"));
            }
            if (contents.size() > 3) {
                lines.add(Component.literal("  §7... 还有 " + (contents.size() - 3) + " 种成分"));
            }
        }

        // 压力（如果有气体）
        double pressure = beaker.getPressure();
        if (pressure > 0.01) {
            lines.add(Component.literal("§e压力: §f" + DF.format(pressure) + " atm"));
        }

        lines.add(Component.literal("§8[右键烧杯可锁定目标]"));

        return lines;
    }

    @Override
    public String getDisplayTitle() {
        return "分析仪";
    }
}