package com.pha.trainees.chemistry.item;

import com.pha.trainees.chemistry.material.IMaterial;
import com.pha.trainees.chemistry.material.SimpleMaterial;
import com.pha.trainees.chemistry.material.SubstanceBlueprint;
import com.pha.trainees.chemistry.material.SubstanceBlueprintRegistry;
import com.pha.trainees.chemistry.particle.IonType;
import com.pha.trainees.config.ChemConfig;
import com.pha.trainees.registry.ModChemistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 物质基类物品
 * 支持蓝图引用（静态配方）和动态混合物两种存储模式
 */
public class SubstanceItem extends Item {

    private static final DecimalFormat DF = new DecimalFormat("#0.000");

    public SubstanceItem(Properties properties) {
        super(properties);
    }

    // ==================== 静态工厂方法 ====================

    /**
     * 从蓝图创建物品堆（蓝图模式）。
     * NBT 仅存 blueprint ID + units，成分在读取时实时计算，
     * 因此难度变更后旧物品会自动反映新难度（蓝本 §8.3）。
     */
    public static ItemStack fromBlueprint(ResourceLocation blueprintId, double units) {
        SubstanceBlueprint blueprint = SubstanceBlueprintRegistry.get(blueprintId);
        if (blueprint == null) return ItemStack.EMPTY;

        ItemStack stack = new ItemStack(ModChemistry.ModChemistryItems.SUBSTANCE.get());
        CompoundTag tag = stack.getOrCreateTag();
        tag.putString("blueprint", blueprintId.toString());
        tag.putDouble("units", units > 0 ? units : 1.0);
        return stack;
    }

    /**
     * 从成分映射创建物品堆（动态混合物）
     */
    public static ItemStack fromComposition(Map<IonType, Double> composition) {
        ItemStack stack = new ItemStack(ModChemistry.ModChemistryItems.SUBSTANCE.get());
        CompoundTag tag = stack.getOrCreateTag();
        ListTag compositionList = new ListTag();
        for (Map.Entry<IonType, Double> entry : composition.entrySet()) {
            CompoundTag entryTag = new CompoundTag();
            entryTag.putString("ion", entry.getKey().getId().toString());
            entryTag.putDouble("moles", entry.getValue());
            compositionList.add(entryTag);
        }
        tag.put("composition", compositionList);
        return stack;
    }

    /**
     * 创建纯净物（快捷方法）
     */
    public static ItemStack ofPure(IonType ion, double moles) {
        Map<IonType, Double> comp = new HashMap<>();
        comp.put(ion, moles);
        return fromComposition(comp);
    }

    // ==================== 数据提取 ====================

    /**
     * 从物品堆中提取成分映射
     */
    public static Map<IonType, Double> getComposition(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag == null) return Map.of();

        // 蓝图模式：读取时实时解析蓝图并套用当前难度乘数
        if (tag.contains("blueprint")) {
            ResourceLocation id = ResourceLocation.tryParse(tag.getString("blueprint"));
            if (id != null) {
                SubstanceBlueprint blueprint = SubstanceBlueprintRegistry.get(id);
                if (blueprint != null) {
                    double units = tag.getDouble("units");
                    if (units <= 0) units = 1.0;
                    return blueprint.createComposition(units, getValuableMultiplier(), getGangueMultiplier());
                }
            }
            return Map.of();
        }

        // 动态混合物模式
        if (tag.contains("composition")) {
            ListTag list = tag.getList("composition", Tag.TAG_COMPOUND);
            Map<IonType, Double> result = new HashMap<>();
            for (int i = 0; i < list.size(); i++) {
                CompoundTag entry = list.getCompound(i);
                String idStr = entry.getString("ion");
                double moles = entry.getDouble("moles");
                if (moles > 1e-9) {
                    ResourceLocation id = ResourceLocation.tryParse(idStr);
                    if (id != null) {
                        IonType ion = ModChemistry.ModIons.getById(id);
                        if (ion != null) {
                            result.put(ion, moles);
                        }
                    }
                }
            }
            return Map.copyOf(result);
        }

        return Map.of();
    }

    /**
     * 从物品堆中提取总摩尔数
     */
    public static double getTotalMoles(ItemStack stack) {
        return getComposition(stack).values().stream().mapToDouble(Double::doubleValue).sum();
    }

    /**
     * 从物品堆中创建 IMaterial 实例
     */
    public static IMaterial getMaterial(ItemStack stack) {
        Map<IonType, Double> comp = getComposition(stack);
        if (comp.isEmpty()) {
            return null;
        }
        return new SimpleMaterial(comp);
    }

    /**
     * 检查是否为蓝图模式
     */
    public static boolean isBlueprint(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        return tag != null && tag.contains("blueprint");
    }

    // ==================== 难度乘数解析 ====================

    /**
     * 根据当前难度档位获取有效成分乘数。
     * CUSTOM 档直接读配置字段；预设档读取预设表。
     */
    private static double getValuableMultiplier() {
        ChemConfig.DifficultyLevel difficulty = ChemConfig.GAME_DIFFICULTY.get();
        if (difficulty == ChemConfig.DifficultyLevel.CUSTOM) {
            return ChemConfig.ORE_VALUABLE_RATIO_MULTIPLIER.get();
        }
        return ChemConfig.DifficultyPresets.get(difficulty).oreValuableMultiplier;
    }

    /**
     * 根据当前难度档位获取脉石乘数。
     */
    private static double getGangueMultiplier() {
        ChemConfig.DifficultyLevel difficulty = ChemConfig.GAME_DIFFICULTY.get();
        if (difficulty == ChemConfig.DifficultyLevel.CUSTOM) {
            return ChemConfig.ORE_GANGUE_RATIO_MULTIPLIER.get();
        }
        return ChemConfig.DifficultyPresets.get(difficulty).oreGangueMultiplier;
    }
    // ==================== 显示相关 ====================

    @Override
    public Component getName(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains("blueprint")) {
            ResourceLocation id = ResourceLocation.tryParse(tag.getString("blueprint"));
            if (id != null) {
                SubstanceBlueprint blueprint = SubstanceBlueprintRegistry.get(id);
                if (blueprint != null) {
                    return Component.literal(blueprint.getDisplayName());
                }
            }
        }
        return super.getName(stack);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);

        Map<IonType, Double> comp = getComposition(stack);
        if (comp.isEmpty()) {
            tooltip.add(Component.literal("§7空物质"));
            return;
        }

        double total = comp.values().stream().mapToDouble(Double::doubleValue).sum();
        tooltip.add(Component.literal("§7总摩尔数: §f" + DF.format(total) + " mol"));

        // 显示成分（按摩尔数从大到小排序）
        tooltip.add(Component.literal("§7成分:"));
        comp.entrySet().stream()
                .sorted((a, b) -> Double.compare(b.getValue(), a.getValue()))
                .forEach(entry -> {
                    String ionName = entry.getKey().getId().getPath();
                    double moles = entry.getValue();
                    double percentage = total > 0 ? moles / total * 100 : 0;
                    tooltip.add(Component.literal("  §8- §f" + ionName + " §7" + DF.format(moles) +
                            " mol (§8" + DF.format(percentage) + "%§7)"));
                });

        // 显示模式标识
        if (isBlueprint(stack)) {
            tooltip.add(Component.literal("§8[蓝图模式]"));
        }
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        // 蓝图模式的物品显示附魔光泽
        return isBlueprint(stack);
    }
}