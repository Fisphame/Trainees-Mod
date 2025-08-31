package com.pha.trainees.block;

import com.pha.trainees.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class BasketballAntiBlock extends Block {
    public BasketballAntiBlock(Properties p_49795_) {
        super(p_49795_);
    }

    // 当玩家挖掘方块时掉落物品
    @Override
    public void playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        super.playerWillDestroy(level, pos, state, player);

        // 只有在服务器端并且不是创造模式时才掉落
        if (!level.isClientSide && !player.isCreative()) {
            // 创建篮球反制物品
            ItemStack itemStack = new ItemStack(ModItems.BASKETBALL_ANTI.get());

            // 掉落篮球反制物品
            popResource(level, pos, itemStack);
        }
    }

}
