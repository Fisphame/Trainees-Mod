package com.pha.trainees.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.pha.trainees.Main;
import com.pha.trainees.config.ChemConfig;
import com.pha.trainees.chemistry.block.BeakerBlock;
import com.pha.trainees.chemistry.blockentity.BeakerBlockEntity;
import com.pha.trainees.chemistry.engine.ReactionEngine;
import com.pha.trainees.chemistry.particle.IonType;
import com.pha.trainees.chemistry.reaction.ReactionEdge;
import com.pha.trainees.chemistry.reaction.ReactionGraph;
import com.pha.trainees.network.ModNetwork;
import com.pha.trainees.network.OpenGraphPacket;
import com.pha.trainees.registry.ModChemistry.ModIons;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.network.PacketDistributor;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DebugCommand {

    private static final DecimalFormat DF = new DecimalFormat("#0.000");
    private static final double REACH_DISTANCE = 8.0;

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("chemtester")
                .requires(source -> source.hasPermission(2))
                .then(Commands.literal("add")
                        .then(Commands.argument("ionId", StringArgumentType.string())
                                .then(Commands.argument("moles", DoubleArgumentType.doubleArg(0.001, 99999))
                                        .executes(DebugCommand::addIon)
                                )
                        )
                )
                .then(Commands.literal("clear")
                        .executes(DebugCommand::clearBeaker)
                )
                .then(Commands.literal("info")
                        .executes(DebugCommand::printInfo)
                )
                .then(Commands.literal("power")
                        .then(Commands.argument("onoff", BoolArgumentType.bool())
                                .executes(DebugCommand::setPower)
                        )
                )
                .then(Commands.literal("last")
                        .executes(DebugCommand::printRecentReactions)
                )
                .then(Commands.literal("graph")
                        .executes(DebugCommand::openGraph)
                )
                .then(Commands.literal("fluid")
                        .then(Commands.literal("fill")
                                .then(Commands.argument("mb", IntegerArgumentType.integer(1, 100000))
                                        .executes(ctx -> fluidTest(ctx, true))
                                )
                        )
                        .then(Commands.literal("drain")
                                .then(Commands.argument("mb", IntegerArgumentType.integer(1, 100000))
                                        .executes(ctx -> fluidTest(ctx, false))
                                )
                        )
                )
        );
    }

    private static int addIon(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Player player = context.getSource().getPlayerOrException();
        String ionIdStr = StringArgumentType.getString(context, "ionId");
        double moles = DoubleArgumentType.getDouble(context, "moles");

        // 解析离子 ID
        ResourceLocation ionId = ResourceLocation.tryParse(ionIdStr);
        if (ionId == null) {
            context.getSource().sendFailure(Component.literal("§c无效的离子 ID 格式，请使用 'trainees:h_plus' 格式"));
            return 0;
        }

        IonType ion = ModIons.getById(ionId);
        if (ion == null) {
            context.getSource().sendFailure(Component.literal("§c未找到离子: " + ionIdStr));
            return 0;
        }

        // 获取准星对准的方块（使用 player.pick()）
        Level level = player.level();
        BlockHitResult hitResult = getTargetBlock(player);
        if (hitResult == null) {
            context.getSource().sendFailure(Component.literal("§c请将准星对准一个烧杯"));
            return 0;
        }

        BlockPos pos = hitResult.getBlockPos();
        BlockState state = level.getBlockState(pos);
        if (!(state.getBlock() instanceof BeakerBlock)) {
            context.getSource().sendFailure(Component.literal("§c目标方块不是烧杯！"));
            return 0;
        }

        BlockEntity entity = level.getBlockEntity(pos);
        if (!(entity instanceof BeakerBlockEntity beaker)) {
            context.getSource().sendFailure(Component.literal("§c烧杯方块实体异常！"));
            return 0;
        }

        double added = beaker.addIon(ion, moles);
        if (added > 0) {
            context.getSource().sendSuccess(
                    () -> Component.literal("§a成功添加 " + DF.format(added) + " mol 的 " + ion.getId().getPath() + " 到烧杯"),
                    true
            );
            Main.LOGGER.info("[Debug] Added {} mol of {} to beaker at {}", added, ion.getId().getPath(), pos);
//            ReactionEngine.trigger(beaker, ion);
            return 1;
        } else {
            context.getSource().sendFailure(Component.literal("§c添加失败，请检查烧杯状态"));
            return 0;
        }
    }

    private static int clearBeaker(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Player player = context.getSource().getPlayerOrException();

        BlockHitResult hitResult = getTargetBlock(player);
        if (hitResult == null) {
            context.getSource().sendFailure(Component.literal("§c请将准星对准一个烧杯"));
            return 0;
        }

        BlockPos pos = hitResult.getBlockPos();
        BlockState state = player.level().getBlockState(pos);
        if (!(state.getBlock() instanceof BeakerBlock)) {
            context.getSource().sendFailure(Component.literal("§c目标方块不是烧杯！"));
            return 0;
        }

        BlockEntity entity = player.level().getBlockEntity(pos);
        if (!(entity instanceof BeakerBlockEntity beaker)) {
            context.getSource().sendFailure(Component.literal("§c烧杯方块实体异常！"));
            return 0;
        }

        int count = beaker.getContents().size();
        for (IonType ion : beaker.getContents().keySet()) {
            beaker.removeIon(ion, beaker.getAmount(ion));
        }

        context.getSource().sendSuccess(
                () -> Component.literal("§a已清空烧杯，移除了 " + count + " 种成分"),
                true
        );
        return 1;
    }

    private static int printInfo(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Player player = context.getSource().getPlayerOrException();

        BlockHitResult hitResult = getTargetBlock(player);
        if (hitResult == null) {
            context.getSource().sendFailure(Component.literal("§c请将准星对准一个烧杯"));
            return 0;
        }

        BlockPos pos = hitResult.getBlockPos();
        BlockState state = player.level().getBlockState(pos);
        if (!(state.getBlock() instanceof BeakerBlock)) {
            context.getSource().sendFailure(Component.literal("§c目标方块不是烧杯！"));
            return 0;
        }

        BlockEntity entity = player.level().getBlockEntity(pos);
        if (!(entity instanceof BeakerBlockEntity beaker)) {
            context.getSource().sendFailure(Component.literal("§c烧杯方块实体异常！"));
            return 0;
        }

        context.getSource().sendSuccess(() -> Component.literal("§6=== 烧杯调试信息 ==="), false);
        context.getSource().sendSuccess(() -> Component.literal("§7温度: §f" + DF.format(beaker.getTemperature()) + " K"), false);
        context.getSource().sendSuccess(() -> Component.literal("§7体积: §f" + DF.format(beaker.getVolume()) + " L"), false);
        context.getSource().sendSuccess(() -> Component.literal("§7总摩尔数: §f" + DF.format(beaker.getTotalMoles()) + " mol"), false);
        context.getSource().sendSuccess(() -> Component.literal("§7内容物:"), false);

        if (beaker.getContents().isEmpty()) {
            context.getSource().sendSuccess(() -> Component.literal("  §7(空)"), false);
        } else {
            for (var entry : beaker.getContents().entrySet()) {
                context.getSource().sendSuccess(() ->
                                Component.literal("  §f" + entry.getKey().getDisplayName() + "§7: §f" + DF.format(entry.getValue()) + " mol"),
                        false
                );
            }
        }

        return 1;
    }

    /**
     * 切换电解电源（蓝本 §17 理论调试：无限电开关）。
     * true = 容器视为无限通电，恒可电解；false = 电解反应被门控拦截。
     */
    private static int setPower(CommandContext<CommandSourceStack> context) {
        boolean on = BoolArgumentType.getBool(context, "onoff");
        ChemConfig.ELECTROLYZER_FREE_POWER.set(on);
        context.getSource().sendSuccess(
                () -> Component.literal("§a电解电源: " + (on ? "开（无限电）" : "关")), true);
        return 1;
    }

    /**
     * 打印最近反应记录（环形缓冲区，蓝本 §14.2）
     */
    private static int printRecentReactions(CommandContext<CommandSourceStack> context) {
        List<ReactionEngine.ReactionRecord> records = ReactionEngine.getRecentReactions();
        context.getSource().sendSuccess(() -> Component.literal("§6=== 最近反应记录（最多20条） ==="), false);
        if (records.isEmpty()) {
            context.getSource().sendSuccess(() -> Component.literal("  §7(暂无记录)"), false);
        } else {
            for (ReactionEngine.ReactionRecord r : records) {
                context.getSource().sendSuccess(() -> Component.literal(
                        "  §ftick " + r.gameTime() + "§7 | §f" + r.ruleId()
                                + "§7 | Δξ=" + String.format("%.4f", r.deltaXi())
                                + " | heat=" + String.format("%.1f", r.heatKj()) + "kJ"), false);
            }
        }
        return 1;
    }

    /**
     * 把反应图发送到客户端打开可视化界面（趣味调试功能）
     */
    private static int openGraph(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Player player = context.getSource().getPlayerOrException();
        if (!(player instanceof ServerPlayer serverPlayer)) return 0;

        List<ReactionEdge> edges = ReactionGraph.getInstance().getAllEdges();
        Map<String, String> nodePhase = new HashMap<>();
        List<String> edgeStrs = new ArrayList<>();
        for (ReactionEdge e : edges) {
            String src = e.getSource().getId().getPath();
            String tgt = e.getTarget().getId().getPath();
            nodePhase.putIfAbsent(src, e.getSource().getPhase().name());
            nodePhase.putIfAbsent(tgt, e.getTarget().getPhase().name());
            edgeStrs.add(src + ">" + tgt + ">" + e.getRule().getId().getPath());
        }
        List<String> nodeStrs = new ArrayList<>(nodePhase.size());
        nodePhase.forEach((id, phase) -> nodeStrs.add(id + ">" + phase));

        ModNetwork.get().send(PacketDistributor.PLAYER.with(() -> serverPlayer),
                new OpenGraphPacket(nodeStrs, edgeStrs));
        context.getSource().sendSuccess(
                () -> Component.literal("§a反应图已发送（" + edges.size() + " 条边）"), true);
        return 1;
    }

    /**
     * 通过 IFluidHandler 能力测试烧杯流体桥接（fill/drain 水，开发计划 §17.2）
     */
    private static int fluidTest(CommandContext<CommandSourceStack> context, boolean fill) throws CommandSyntaxException {
        Player player = context.getSource().getPlayerOrException();
        BlockHitResult hit = getTargetBlock(player);
        if (hit == null || !(player.level().getBlockEntity(hit.getBlockPos()) instanceof BeakerBlockEntity beaker)) {
            context.getSource().sendFailure(Component.literal("§c请将准星对准一个烧杯"));
            return 0;
        }
        int mb = IntegerArgumentType.getInteger(context, "mb");
        IFluidHandler handler = beaker.getCapability(ForgeCapabilities.FLUID_HANDLER, null).orElse(null);
        if (handler == null) {
            context.getSource().sendFailure(Component.literal("§c烧杯没有流体能力"));
            return 0;
        }
        double mbPerMol = 1000.0 / ChemConfig.BUCKET_TO_MOL_WATER.get();
        if (fill) {
            int filled = handler.fill(new FluidStack(Fluids.WATER, mb), IFluidHandler.FluidAction.EXECUTE);
            context.getSource().sendSuccess(() -> Component.literal("§a灌入水 " + filled + " mB（"
                    + String.format("%.3f", filled / mbPerMol) + " mol H₂O）"), true);
        } else {
            FluidStack drained = handler.drain(mb, IFluidHandler.FluidAction.EXECUTE);
            context.getSource().sendSuccess(() -> Component.literal("§a抽出水 " + drained.getAmount() + " mB（"
                    + String.format("%.3f", drained.getAmount() / mbPerMol) + " mol）"), true);
        }
        return 1;
    }

    /**
     * 获取玩家准星对准的方块 HitResult
     */
    private static BlockHitResult getTargetBlock(Player player) {
        // 使用 player.pick() 获取准星目标
        // 参数：到达距离、部分Tick（这里用0）、是否忽略流体
        HitResult hit = player.pick(REACH_DISTANCE, 0.0F, false);
        if (hit.getType() == HitResult.Type.BLOCK) {
            return (BlockHitResult) hit;
        }
        return null;
    }
}