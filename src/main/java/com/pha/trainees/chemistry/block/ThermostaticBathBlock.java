package com.pha.trainees.chemistry.block;

import com.pha.trainees.chemistry.blockentity.ThermostaticBathBlockEntity;
import com.pha.trainees.chemistry.util.AnalyzerAccess;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

/**
 * 恒温浴方块（§19.24 ③-4）：放在容器正下方即成为"设定点型"热源。
 *
 * <p>交互（V1 无 GUI，与电解槽同一风格）：</p>
 * <ul>
 *   <li>空手右键：设定点 +5 K；潜行右键：−5 K（受当前介质区间夹取）</li>
 *   <li>持介质物品右键：更换介质——水桶=水浴、冰=冰盐浴、沙子=沙浴、蜂蜜块=油浴（占位，待有真油物品后替换）</li>
 *   <li>分析仪右键：诊断面板（与其他化学方块一致）</li>
 * </ul>
 */
public class ThermostaticBathBlock extends BaseEntityBlock {

    public ThermostaticBathBlock(Properties properties) {
        super(properties);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ThermostaticBathBlockEntity(pos, state);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player,
                                 InteractionHand hand, BlockHitResult hit) {
        if (level.isClientSide) return InteractionResult.SUCCESS;
        if (!(level.getBlockEntity(pos) instanceof ThermostaticBathBlockEntity bath)) {
            return InteractionResult.PASS;
        }

        ItemStack held = player.getItemInHand(hand);

        // 分析仪优先（与其他化学方块一致）
        InteractionResult analyzerResult = AnalyzerAccess.tryUse(level, pos, player, held);
        if (analyzerResult != InteractionResult.PASS) {
            return analyzerResult;
        }

        // 1. 介质物品 → 更换介质
        ThermostaticBathBlockEntity.Medium medium = mediumFor(held);
        if (medium != null && medium != bath.getMedium()) {
            bath.setMedium(medium);
            display(player, "message.trainees.bath.medium",
                    Component.translatable("message.trainees.bath.medium." + medium.id()),
                    format(bath.getSetpoint()));
            return InteractionResult.CONSUME;
        }

        // 2. 空手/其他：调整设定点（右键升、潜行右键降）
        if (!held.isEmpty() && medium == null) {
            // 手里拿着无关物品时不动设定点，避免误触
            return InteractionResult.CONSUME;
        }
        double setpoint = bath.adjustSetpoint(player.isShiftKeyDown()
                ? -ThermostaticBathBlockEntity.STEP_K
                : ThermostaticBathBlockEntity.STEP_K);
        display(player, "message.trainees.bath.set", format(setpoint), format(setpoint - 273.15));
        return InteractionResult.CONSUME;
    }

    /** 介质物品映射：全部使用原版物品占位，避免为 V1 新增资产。 */
    private static ThermostaticBathBlockEntity.Medium mediumFor(ItemStack stack) {
        if (stack.isEmpty()) return null;
        if (stack.is(Items.WATER_BUCKET)) return ThermostaticBathBlockEntity.Medium.WATER;
        if (stack.is(Items.ICE)) return ThermostaticBathBlockEntity.Medium.ICE_SALT;
        if (stack.is(Items.SAND)) return ThermostaticBathBlockEntity.Medium.SAND;
        if (stack.is(Items.HONEY_BLOCK)) return ThermostaticBathBlockEntity.Medium.OIL;
        return null;
    }

    private static void display(Player player, String key, Object... args) {
        player.displayClientMessage(Component.translatable(key, args), true);
    }

    private static String format(double value) {
        return String.format("%.1f", value);
    }
}
