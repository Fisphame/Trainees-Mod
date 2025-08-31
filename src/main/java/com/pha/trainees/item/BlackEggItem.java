package com.pha.trainees.item;

import com.pha.trainees.registry.ModEntities;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrownEgg;
import net.minecraft.world.item.EggItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

// 导入自定义实体
import com.pha.trainees.entity.KunAntiEntity;
import com.pha.trainees.Main;
import net.minecraft.world.phys.HitResult;

import javax.annotation.Nullable;
import java.util.List;

public class BlackEggItem extends EggItem {

    public BlackEggItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.EGG_THROW, SoundSource.PLAYERS,
                0.5F, 0.4F / (level.getRandom().nextFloat() * 0.4F + 0.8F));

        if (!level.isClientSide) {
            // 创建自定义的投掷黑蛋实体
            ThrownBlackEgg thrownBlackEgg = new ThrownBlackEgg(level, player);
            thrownBlackEgg.setItem(itemstack);
            thrownBlackEgg.shootFromRotation(player, player.getXRot(), player.getYRot(),
                    0.0F, 1.5F, 1.0F);
            level.addFreshEntity(thrownBlackEgg);
        }

        player.awardStat(Stats.ITEM_USED.get(this));
        if (!player.getAbilities().instabuild) {
            itemstack.shrink(1);
        }

        return InteractionResultHolder.sidedSuccess(itemstack, level.isClientSide());
    }

    // 自定义投掷黑蛋实体类
    public static class ThrownBlackEgg extends ThrownEgg {

        public ThrownBlackEgg(Level level, Player player) {
            super(level, player);
        }

        protected void onHit(HitResult HitResult) {
            super.onHit(HitResult);

            if (!this.level().isClientSide) {
                // 生成幼年KunAntiEntity而不是原版鸡
                KunAntiEntity kunAnti = ModEntities.KUN_ANTI.get().create(this.level());
                if (kunAnti != null) {
                    kunAnti.setAge(-24000); // 设置为幼年
                    kunAnti.moveTo(this.getX(), this.getY(), this.getZ(),
                            this.getYRot(), 0.0F);
                    this.level().addFreshEntity(kunAnti);
                }

                this.level().broadcastEntityEvent(this, (byte)3);
                this.discard();
            }
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level,
                                List<Component> tooltipComponents, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltipComponents, flag);

        // 添加本地化的工具提示
        tooltipComponents.add(Component.translatable("tooltip.trainees.black_egg_item"));
        tooltipComponents.add(Component.translatable("tooltip.trainees.black_egg_item.2"));
    }
}