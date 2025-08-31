package com.pha.trainees.item;

import com.pha.trainees.registry.ModBlocks;
//import com.pha.trainees.util.AdvancementUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.List;

public class KunPickaxeFinal extends PickaxeItem {
    public KunPickaxeFinal(Tier p_42961_, int p_42962_, float p_42963_, Properties p_42964_) {
        super(p_42961_, p_42962_, p_42963_, p_42964_);
    }
//    private static final ResourceLocation MYBLOCK_ADVANCEMENT =
//            ResourceLocation.parse("trainees:items/myblock");
//    UseOnContext context;
//    ServerPlayer serverPlayer = (ServerPlayer) context.getPlayer();

    @Override
    public boolean isCorrectToolForDrops(ItemStack stack, BlockState state) {
        // 如果是myblock，则返回true（表示可以挖掘）
        if (state.is(ModBlocks.myblock.get())) {
            return true;
        }
        // 对于其他方块，使用默认的镐子挖掘逻辑
        return state.is(BlockTags.MINEABLE_WITH_PICKAXE);
    }

    @Override
    public float getDestroySpeed(ItemStack stack, BlockState state) {
        if (!state.is(ModBlocks.myblock.get())) {
            if (state.is(BlockTags.MINEABLE_WITH_PICKAXE)) {
                return super.getDestroySpeed(stack, state) * 4.0f;
            }
            return super.getDestroySpeed(stack, state);
        }
        return super.getDestroySpeed(stack, state);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level,
                                List<Component> tooltipComponents, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltipComponents, flag);

        // 添加本地化的工具提示
        tooltipComponents.add(Component.translatable("tooltip.trainees.kun_pickaxe_final_item"));
        tooltipComponents.add(Component.translatable("tooltip.trainees.kun_pickaxe_final_item.2"));
    }
}