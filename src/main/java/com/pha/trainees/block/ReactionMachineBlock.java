package com.pha.trainees.block;

import com.pha.trainees.blockentity.ReactionMachineBlockEntity;
import com.pha.trainees.menu.ReactionMachineMenu;
import com.pha.trainees.util.interfaces.Chemistry;
import com.pha.trainees.util.interfaces.Machine;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class ReactionMachineBlock extends BaseEntityBlock implements EntityBlock, Chemistry, Machine {
    public ReactionMachineBlock(Properties properties) {
        super(properties);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ReactionMachineBlockEntity(pos, state);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!level.isClientSide) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof ReactionMachineBlockEntity) {
                player.openMenu(new MenuProvider() {
                    @Override
                    public Component getDisplayName() {
                        return Component.translatable("block.trainees.reaction_machine");
                    }

                    @Override
                    public @NotNull AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
                        return new ReactionMachineMenu(id, inventory, pos);
                    }
                });
            }
        }
        return InteractionResult.SUCCESS;
    }
}
