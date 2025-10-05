package com.pha.trainees.item;


import com.pha.trainees.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SplashPotionItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.List;

public class BasketballAntiItem extends SplashPotionItem {

    public BasketballAntiItem(Properties properties) {
        super(properties);
        this.setDefaultPotionEffect();
    }

    // 设置默认药水效果
    private void setDefaultPotionEffect() {
        // 这个方法会在构造函数中调用，确保物品有正确的NBT数据
    }

    // 重写药水类型，返回伤害药水
    public Potion getPotion(ItemStack stack) {
        return Potions.HARMING;
    }

    // 确保新创建的物品栈有正确的NBT数据
    @Override
    public ItemStack getDefaultInstance() {
        ItemStack stack = super.getDefaultInstance();
        // 设置药水效果为伤害药水
        PotionUtils.setPotion(stack, Potions.HARMING);
        return stack;
    }

    // 重写使用物品的方法
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        // 确保物品栈有正确的药水效果
        if (!PotionUtils.getPotion(itemstack).equals(Potions.HARMING)) {
            PotionUtils.setPotion(itemstack, Potions.HARMING);
        }

        // 检查玩家是否在潜行（按下Shift键）
        if (player.isShiftKeyDown()) {
            // 获取玩家视线指向的方块位置
            BlockHitResult blockhitresult = getPlayerPOVHitResult(level, player, ClipContext.Fluid.NONE);

            // 如果玩家视线指向了某个方块
            if (blockhitresult.getType() == HitResult.Type.BLOCK) {
                // 尝试放置方块
                InteractionResult placementResult = this.tryPlaceBlock(new BlockPlaceContext(
                        new UseOnContext(player, hand, blockhitresult)
                ));

                if (placementResult.consumesAction()) {
                    // 放置成功，消耗物品
                    if (!player.getAbilities().instabuild) {
                        itemstack.shrink(1);
                    }
                    return InteractionResultHolder.success(itemstack);
                }
            }

            // 放置失败，返回传递
            return InteractionResultHolder.pass(itemstack);
        } else {
            // 正常投掷药水，但不消耗物品
            this.throwPotionWithoutConsuming(level, player, hand);
            return InteractionResultHolder.success(itemstack);
        }
    }

    // 获取玩家视线指向的方块
    public static BlockHitResult getPlayerPOVHitResult(Level level, Player player, ClipContext.Fluid fluidMode) {
        // 获取玩家视线方向
        float f = player.getXRot();
        float f1 = player.getYRot();
        Vec3 vec3 = player.getEyePosition();
        float f2 = Mth.cos(-f1 * ((float)Math.PI / 180F) - (float)Math.PI);
        float f3 = Mth.sin(-f1 * ((float)Math.PI / 180F) - (float)Math.PI);
        float f4 = -Mth.cos(-f * ((float)Math.PI / 180F));
        float f5 = Mth.sin(-f * ((float)Math.PI / 180F));
        float f6 = f3 * f4;
        float f7 = f2 * f4;
        double d0 = player.getBlockReach();
        Vec3 vec31 = vec3.add((double)f6 * d0, (double)f5 * d0, (double)f7 * d0);

        // 返回视线与方块的碰撞结果
        return level.clip(new ClipContext(vec3, vec31, ClipContext.Block.OUTLINE, fluidMode, player));
    }

    // 尝试放置方块
    // 在tryPlaceBlock方法中添加保存NBT数据的逻辑
    private InteractionResult tryPlaceBlock(BlockPlaceContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        ItemStack itemStack = context.getItemInHand();

        // 检查是否可以放置方块
        if (context.canPlace()) {
            BlockState stateToPlace = ModBlocks.BASKETBALL_ANTI_BLOCK.get().getStateForPlacement(context);

            if (stateToPlace != null && level.setBlock(pos, stateToPlace, 11)) {
                // 方块放置成功
                level.playSound(null, pos, SoundEvents.WOOL_PLACE, SoundSource.BLOCKS, 1.0F, 1.0F);
                return InteractionResult.SUCCESS;
            }
        }

        // 放置失败
        return InteractionResult.FAIL;
    }

    // 投掷药水但不消耗物品
    private void throwPotionWithoutConsuming(Level level, Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);

        // 确保物品栈有正确的药水效果
        ItemStack throwStack = itemstack.copy();
        if (!PotionUtils.getPotion(throwStack).equals(Potions.HARMING)) {
            PotionUtils.setPotion(throwStack, Potions.HARMING);
        }
        if (!level.isClientSide) {
            // 创建喷溅药水实体（使用原版逻辑）
            ThrownPotion thrownpotion = new ThrownPotion(level, player);

            thrownpotion.setItem(throwStack);
            thrownpotion.shootFromRotation(player, player.getXRot(), player.getYRot(), -20.0F, 0.5F, 1.0F);
            level.addFreshEntity(thrownpotion);
        }

        // 不消耗物品 - 这是关键，我们移除了原版的shrink调用
        player.awardStat(Stats.ITEM_USED.get(this));
        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.SPLASH_POTION_THROW, SoundSource.PLAYERS,
                0.5F, 0.4F / (level.getRandom().nextFloat() * 0.4F + 0.8F));
    }

    // 重写右键点击方块的行为
    @Override
    public InteractionResult useOn(UseOnContext context) {
        // 当潜行时，允许在方块上放置
        if (context.getPlayer() != null && context.getPlayer().isShiftKeyDown()) {
            InteractionResult result = this.tryPlaceBlock(new BlockPlaceContext(context));
            if (result.consumesAction()) {
                // 放置成功，消耗物品
                if (!context.getPlayer().getAbilities().instabuild) {
                    context.getItemInHand().shrink(1);
                }
                return result;
            }
        }

        // 默认行为（传递）
        return InteractionResult.PASS;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltipComponents, flag);

        tooltipComponents.add(Component.translatable("tooltip.trainees.basketball_anti_item"));
        tooltipComponents.add(Component.translatable("tooltip.trainees.basketball_anti_item.2"));
    }
}