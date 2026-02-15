package com.pha.trainees.item;

import com.pha.trainees.Main;
import com.pha.trainees.multiblock.TrainerAltarPattern;
import com.pha.trainees.registry.ModBlocks;
import com.pha.trainees.util.game.AchievementManager;
import com.pha.trainees.util.game.Booleanf;
import com.pha.trainees.util.game.structure.MultiblockStructure;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class StoneStickItem extends Item {
    public StoneStickItem(Properties p_41383_) {
        super(p_41383_);
    }

    public @NotNull InteractionResult useOn(UseOnContext context){
        Level level = context.getLevel();
        Player player = context.getPlayer();
        ItemStack itemStack = context.getItemInHand();
        BlockPos pos = context.getClickedPos();
        BlockState state = level.getBlockState(pos);

        if (player == null || !state.is(ModBlocks.ALTAR_CORE_BLOCK.get()) || level.isClientSide()) {
            return InteractionResult.FAIL;
        }
        // 重要：我们需要找到结构原点
        // 根据结构定义，原点在核心方块的下方一格 (0,0,0)
        // 所以检查位置应该是 (0,0,0) 的位置
        BlockPos checkPos = pos.below();  // 核心在 (0,1,0)，原点是下方一格

        // 尝试激活结构 - 使用原点位置进行检查
        Booleanf activated = MultiblockStructure.tryActivateStructure(
                level,
                checkPos,  // 使用原点位置
                TrainerAltarPattern.STRUCTURE_ID,
                player
        );

        if (activated.bool()) {
            Main.LOGGER.info("Trainer Altar activated success");
            player.displayClientMessage(
                    Component.literal("--[ - ]--").withStyle(ChatFormatting.GREEN),
                    true
            );
            if (player instanceof ServerPlayer serverPlayer) {
                AchievementManager.grantSpecificAchievement(
                        serverPlayer,
                        "trainees:structure/altar"  // altar.json
                );
            }

        } else {
            Main.LOGGER.info("Trainer Altar activation fail");
            player.displayClientMessage(
                    Component.literal(
                            switch (activated.num()) {
                                case 0 -> "--x--";
                                case 1 -> "--?--";
                                case 2 -> "···";
                                case 3 -> "-[-x-]-";
                                case 4 -> "--[ - ]--";
                                default -> "Unexpected";
                            }
                    ).withStyle(
                            switch (activated.num()) {
                                case 2, 4 -> ChatFormatting.WHITE;
                                default -> ChatFormatting.RED;
                            }
                    ),
                    true
            );
        }
        player.getCooldowns().addCooldown(itemStack.getItem(), 20);
        return InteractionResult.SUCCESS;
    }
}
