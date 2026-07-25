package com.pha.trainees.chemistry.item;

import com.pha.trainees.chemistry.block.BeakerBlock;
import com.pha.trainees.chemistry.blockentity.BeakerBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * 分析仪基类
 * 手持时右键烧杯可锁定目标，或准星对准时自动显示信息
 */
public class AnalyzerItem extends Item {

    // 用于在客户端记录当前锁定的烧杯位置（通过 ItemStack 的 NBT 或静态变量）
    // 这里我们采用静态变量 + 客户端同步的方式，由 HUD 读取

    public AnalyzerItem(Properties properties) {
        super(properties);
    }

    /**
     * 右键点击方块时触发
     * 用于“锁定”烧杯作为分析目标（可选功能）
     */
    public @NotNull InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        if (level.isClientSide) {
            // 客户端仅处理显示，不改变服务端状态
            return InteractionResult.SUCCESS;
        }

        BlockPos pos = context.getClickedPos();

        // 服务端：检查目标是否是烧杯
        BlockState state = level.getBlockState(pos);
        if (state.getBlock() instanceof BeakerBlock) {
            BlockEntity entity = level.getBlockEntity(pos);
            if (entity instanceof BeakerBlockEntity beaker) {
                // 可以在这里触发一些服务端行为，比如记录日志或同步数据
                // 我们暂时不做额外操作，由客户端 HUD 自动检测准星
                return InteractionResult.CONSUME;
            }
        }
        return InteractionResult.PASS;
    }

    /**
     * 获取要显示的信息（由子类重写）
     * @param beaker 目标烧杯
     * @return 信息组件列表
     */
    public List<Component> getAnalysisResult(BeakerBlockEntity beaker) {
        // 默认显示烧杯状态
        return List.of(
                Component.literal("烧杯分析结果:"),
                Component.literal("温度: " + String.format("%.1f", beaker.getTemperature()) + " K")
        );
    }

    /**
     * 判断是否为创造模式分析仪（子类重写）
     */
    public boolean isCreativeMode() {
        return false;
    }

    /**
     * 获取显示标题
     */
    public String getDisplayTitle() {
        return "分析仪";
    }
}