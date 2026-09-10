package com.pha.trainees.chemistry.util;

import com.pha.trainees.chemistry.container.IChemicalContainer;
import com.pha.trainees.chemistry.item.AnalyzerItem;
import com.pha.trainees.chemistry.report.AnalyzerReport;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/**
 * 分析仪与化学容器的统一交互入口（泛化到所有 {@link IChemicalContainer}：烧杯、电解槽、未来机器）。
 *
 * <p>行为（§19.6/19.7 方案 a + b）：</p>
 * <ul>
 *   <li>手持分析仪右键容器 → 服务端组装报告并推包，客户端打开诊断 GUI；</li>
 *   <li>潜行右键 → 保持原有聊天简报（快速查看 / 留档）。</li>
 * </ul>
 *
 * <p>容器方块只需在自己的 {@code use} 里调用本方法，无需各自实现分析逻辑。</p>
 */
public final class AnalyzerAccess {

    private AnalyzerAccess() {}

    /**
     * @return {@link InteractionResult#PASS} 表示"不是分析仪或目标不是化学容器"，调用方继续走自己的交互；
     *         其余结果表示本方法已处理该次右键。
     */
    public static InteractionResult tryUse(Level level, BlockPos pos, Player player, ItemStack held) {
        if (held == null || !(held.getItem() instanceof AnalyzerItem)) return InteractionResult.PASS;
        if (level == null || !(level.getBlockEntity(pos) instanceof IChemicalContainer container)) {
            return InteractionResult.PASS;
        }
        // 客户端不参与：GUI 由服务端的报告包打开
        if (level.isClientSide) return InteractionResult.SUCCESS;

        if (player instanceof ServerPlayer serverPlayer) {
            if (player.isShiftKeyDown()) {
                AnalyzerReport.sendChatBrief(serverPlayer, container);
            } else {
                AnalyzerReport.sendToClient(serverPlayer, container);
            }
        }
        return InteractionResult.SUCCESS;
    }
}
