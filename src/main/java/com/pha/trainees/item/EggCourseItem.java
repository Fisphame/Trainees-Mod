package com.pha.trainees.item;

import com.pha.trainees.item.ThrownEggCourseItem.*;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.EggItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;


import javax.annotation.Nullable;
import java.util.List;

public class EggCourseItem {
    public static class KunEggItem extends EggItem {

        public KunEggItem(Properties properties) {
            super(properties);
        }

        @Override
        public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
            ItemStack itemstack = player.getItemInHand(hand);
            level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.EGG_THROW, SoundSource.PLAYERS,
                    0.5F, 0.4F / (level.getRandom().nextFloat() * 0.4F + 0.8F));

            if (!level.isClientSide) {
                // 创建自定义的投掷坤蛋实体
                ThrownKunEgg thrownEgg = new ThrownKunEgg(level, player);
                thrownEgg.setItem(itemstack);
                thrownEgg.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 1.5F, 1.0F);
                level.addFreshEntity(thrownEgg);
            }

            player.awardStat(Stats.ITEM_USED.get(this));
            if (!player.getAbilities().instabuild) {
                itemstack.shrink(1);
            }

            return InteractionResultHolder.sidedSuccess(itemstack, level.isClientSide());
        }


        @Override
        public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag flag) {
            super.appendHoverText(stack, level, tooltipComponents, flag);

            if (flag.isAdvanced()) {
                tooltipComponents.add(Component.translatable("tooltip.trainees.kun_egg_item"));
                tooltipComponents.add(Component.translatable("tooltip.trainees.kun_egg_item.2"));
            } else {
                tooltipComponents.add(Component.translatable("tooltip.trainees.item.press_shift"));
            }
        }
    }

    public static class BlackEggItem extends EggItem {

        public BlackEggItem(Properties properties) {super(properties);}

        @Override
        public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
            ItemStack itemstack = player.getItemInHand(hand);
            level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.EGG_THROW, SoundSource.PLAYERS,
                    0.5F, 0.4F / (level.getRandom().nextFloat() * 0.4F + 0.8F));

            if (!level.isClientSide) {
                // 创建自定义的投掷黑蛋实体
                ThrownBlackEgg thrownEgg = new ThrownBlackEgg(level, player);
                thrownEgg.setItem(itemstack);
                thrownEgg.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 1.5F, 1.0F);
                level.addFreshEntity(thrownEgg);
            }

            player.awardStat(Stats.ITEM_USED.get(this));
            if (!player.getAbilities().instabuild) {
                itemstack.shrink(1);
            }

            return InteractionResultHolder.sidedSuccess(itemstack, level.isClientSide());
        }



        @Override
        public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag flag) {
            super.appendHoverText(stack, level, tooltipComponents, flag);

            if (flag.isAdvanced()) {
                tooltipComponents.add(Component.translatable("tooltip.trainees.black_egg_item"));
                tooltipComponents.add(Component.translatable("tooltip.trainees.black_egg_item.2"));
            } else {
                tooltipComponents.add(Component.translatable("tooltip.trainees.item.press_shift"));
            }
        }
    }

    public static class GoldEggItem extends EggItem {

        public GoldEggItem(Properties properties) {
            super(properties);
        }

        @Override
        public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
            ItemStack itemstack = player.getItemInHand(hand);
            level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.EGG_THROW, SoundSource.PLAYERS,
                    0.5F, 0.4F / (level.getRandom().nextFloat() * 0.4F + 0.8F));

            if (!level.isClientSide) {
                // 创建自定义的投掷坤蛋实体
                ThrownGoldEgg thrownEgg = new ThrownGoldEgg(level, player);
                thrownEgg.setItem(itemstack);
                thrownEgg.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 1.5F, 1.0F);
                level.addFreshEntity(thrownEgg);
            }

            player.awardStat(Stats.ITEM_USED.get(this));
            if (!player.getAbilities().instabuild) {
                itemstack.shrink(1);
            }

            return InteractionResultHolder.sidedSuccess(itemstack, level.isClientSide());
        }



        @Override
        public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag flag) {
            super.appendHoverText(stack, level, tooltipComponents, flag);

            if (flag.isAdvanced()) {
                tooltipComponents.add(Component.translatable("tooltip.trainees.gold_egg_item"));
                tooltipComponents.add(Component.translatable("tooltip.trainees.gold_egg_item.2"));
            } else {
                tooltipComponents.add(Component.translatable("tooltip.trainees.item.press_shift"));
            }
        }
    }
}
