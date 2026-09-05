package com.pha.trainees.chemistry.block;

import com.pha.trainees.chemistry.blockentity.BeakerBlockEntity;
import com.pha.trainees.chemistry.blockentity.ElectrolysisCellBlockEntity;
import com.pha.trainees.registry.ModChemistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
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
import org.jetbrains.annotations.Nullable;

/**
 * 电解槽（§19.10 电解槽设计）。
 * 双电极接入面（facing / opposite，方向可配置），双面供电才构成回路；
 * 实现 IChemicalContainer（继承烧杯引擎能力），电解仍由反应图驱动。
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
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(net.minecraft.world.level.Level level,
                                                                  BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide) return null;
        // 复用烧杯的容器 tick（引擎驱动 + 残渣清扫等），电解槽作为 IChemicalContainer 直接接入
        return createTickerHelper(type, ModChemistry.ModChemistryBlockEntities.ELECTROLYSIS_CELL.get(),
                BeakerBlockEntity::tick);
    }
}
