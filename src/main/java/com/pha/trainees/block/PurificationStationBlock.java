package com.pha.trainees.block;

import com.pha.trainees.blockentity.PurificationStationBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;

public class PurificationStationBlock extends Block implements EntityBlock {
    public PurificationStationBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    // 右键打开GUI的核心逻辑
    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos,
                                 Player player, InteractionHand hand, BlockHitResult hit) {
        if (!level.isClientSide() &&
                level.getBlockEntity(pos) instanceof PurificationStationBlockEntity blockEntity) {
            NetworkHooks.openScreen((ServerPlayer) player, blockEntity, pos);
            return InteractionResult.CONSUME;
        }
        return InteractionResult.SUCCESS;
    }

    // 创建关联的方块实体
    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new PurificationStationBlockEntity(pos, state);
    }

//    @Override
//    public MenuProvider getMenuProvider(BlockState state, Level level, BlockPos pos) {
//        BlockEntity blockEntity = level.getBlockEntity(pos);
//        return blockEntity instanceof MenuProvider menuProvider ? menuProvider : null;
//    }
}
