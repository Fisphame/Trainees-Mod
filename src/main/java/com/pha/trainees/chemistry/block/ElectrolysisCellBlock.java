package com.pha.trainees.chemistry.block;

import com.pha.trainees.chemistry.blockentity.ElectrolysisCellBlockEntity;
import com.pha.trainees.chemistry.particle.IonType;
import com.pha.trainees.chemistry.particle.Phase;
import com.pha.trainees.chemistry.util.FluidIonMapper;
import com.pha.trainees.config.ChemConfig;
import com.pha.trainees.registry.ModChemistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import org.jetbrains.annotations.Nullable;

/**
 * 电解槽（§19.10 电解槽设计）。
 * 双电极接入面（facing=阳极 / opposite=阴极，方向可配置），双面供电才构成回路；
 * 实现 IChemicalContainer（继承烧杯引擎能力），电解仍由反应图驱动。
 * V1 无 GUI：右键倒流体/放膜、空手看状态、潜行空手取产物。
 */
public class ElectrolysisCellBlock extends BaseEntityBlock {

    public static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;

    public ElectrolysisCellBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos p_153215_, BlockState p_153216_) {
        return new ElectrolysisCellBlockEntity(p_153215_, p_153216_);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide) return null;
        // 电解槽自己的 tick：引擎 + 产物打包 + 槽满停产（不沿用烧杯的 tick 签名）
        return createTickerHelper(type, ModChemistry.ModChemistryBlockEntities.ELECTROLYSIS_CELL.get(),
                ElectrolysisCellBlockEntity::tick);
    }

    // ==================== 交互（V1 无 GUI，右键 + 聊天提示） ====================

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player,
                                 InteractionHand hand, BlockHitResult hit) {
        if (level.isClientSide) return InteractionResult.SUCCESS;
        if (!(level.getBlockEntity(pos) instanceof ElectrolysisCellBlockEntity cell)) {
            return InteractionResult.PASS;
        }
        ItemStack held = player.getItemInHand(hand);

        // 1. 流体桶倒入（映射离子入 contents，按严格映射 64mol/桶）
        if (held.getItem() instanceof BucketItem) {
            FluidStack fs = FluidUtil.getFluidContained(held).orElse(null);
            if (fs != null && fs.getFluid() != Fluids.EMPTY) {
                IonType ion = FluidIonMapper.getIonForFluid(fs.getFluid());
                if (ion != null) {
                    cell.addIon(ion, ChemConfig.BUCKET_TO_MOL_WATER.get());
                    if (!player.isCreative()) {
                        player.setItemInHand(hand, new ItemStack(Items.BUCKET));
                    }
                    player.displayClientMessage(Component.literal(
                            "§a已倒入 " + ion.getId().getPath() + "（" + ChemConfig.BUCKET_TO_MOL_WATER.get() + " mol）"), true);
                    return InteractionResult.CONSUME;
                }
            }
            player.displayClientMessage(Component.literal("§c该流体无法被识别为离子"), true);
            return InteractionResult.FAIL;
        }

        // 2. 空桶：抽取最多的液态离子成桶
        if (held.getItem() == Items.BUCKET) {
            IonType best = null;
            double bestMoles = 0;
            for (var e : cell.getContents().entrySet()) {
                if (e.getKey().getPhase() == Phase.LIQUID && e.getValue() > bestMoles) {
                    best = e.getKey();
                    bestMoles = e.getValue();
                }
            }
            if (best != null && bestMoles >= 1.0) {
                Fluid f = FluidIonMapper.getFluidForIon(best);
                if (f != null && f != Fluids.EMPTY) {
                    cell.removeIon(best, 1.0);
                    ItemStack bucketStack = f.getFluidType().getBucket(new FluidStack(f, 1000));
                    if (!bucketStack.isEmpty()) {
                        player.setItemInHand(hand, bucketStack);
                        return InteractionResult.CONSUME;
                    }
                }
            }
            player.displayClientMessage(Component.literal("§7电解槽中没有可盛装的液态物质"), true);
            return InteractionResult.FAIL;
        }

        // 3. 离子交换膜 → 装入/更换膜槽
        if (ModChemistry.ModChemistryItems.ION_MEMBRANE.isPresent()
                && held.getItem() == ModChemistry.ModChemistryItems.ION_MEMBRANE.get()) {
            if (!cell.hasMembrane()) {
                cell.setMembraneStack(held.copyWithCount(1));
                held.shrink(1);
                player.displayClientMessage(Component.literal("§a已装入离子交换膜：产物将分侧纯化"), true);
            } else {
                player.getInventory().add(cell.getMembraneStack());
                cell.setMembraneStack(held.copyWithCount(1));
                held.shrink(1);
                player.displayClientMessage(Component.literal("§a已更换离子交换膜"), true);
            }
            return InteractionResult.CONSUME;
        }

        // 4. 潜行空手：取出输出槽产物
        if (player.isShiftKeyDown() && held.isEmpty()) {
            popOutputs(player, cell);
            return InteractionResult.CONSUME;
        }
        // 5. 空手：状态报告
        if (held.isEmpty()) {
            reportState(player, cell);
            return InteractionResult.CONSUME;
        }

        player.displayClientMessage(
                Component.literal("§7右键：倒流体/放膜 | 空手：状态 | 潜行空手：取产物"), true);
        return InteractionResult.PASS;
    }

    private static void popOutputs(Player player, ElectrolysisCellBlockEntity cell) {
        var inv = cell.getInventory();
        boolean any = false;
        for (int slot : new int[]{ElectrolysisCellBlockEntity.SLOT_OUT_A, ElectrolysisCellBlockEntity.SLOT_OUT_B}) {
            ItemStack s = inv.getStackInSlot(slot);
            if (!s.isEmpty()) {
                if (!player.getInventory().add(s)) {
                    player.drop(s, false);
                }
                inv.setStackInSlot(slot, ItemStack.EMPTY);
                any = true;
            }
        }
        player.displayClientMessage(Component.literal(any ? "§a已取出输出槽产物" : "§7输出槽为空"), true);
        reportState(player, cell);
    }

    private static void reportState(Player player, ElectrolysisCellBlockEntity cell) {
        Direction facing = cell.getBlockState().getValue(FACING);
        player.sendSystemMessage(Component.literal("§6=== 电解槽状态 ==="));
        player.sendSystemMessage(Component.literal(
                "  电极方向：阳极 " + facing.getName() + " / 阴极 " + facing.getOpposite().getName()));
        player.sendSystemMessage(Component.literal(
                "  隔膜：" + (cell.hasMembrane() ? "§a已装入（产物分侧纯化）" : "§7无（产物混合输出）")));
        player.sendSystemMessage(Component.literal(
                "  电极储能：阳极 " + cell.getAnodeEnergy() + " FE / 阴极 " + cell.getCathodeEnergy() + " FE"));
        player.sendSystemMessage(Component.literal(
                "  供电：" + (cell.isElectricallyPowered() ? "§aOK（回路接通/无限电）" : "§c未构成回路（需双面供电）")
                        + " | 无限电配置 = " + ChemConfig.ELECTROLYZER_FREE_POWER.get()));
        player.sendSystemMessage(Component.literal(
                "  温度 " + String.format("%.0f", cell.getTemperature()) + "K | 内容物 "
                        + cell.getContents().size() + " 种 | 总 "
                        + String.format("%.2f", cell.getTotalMoles()) + " mol"));
    }
}
