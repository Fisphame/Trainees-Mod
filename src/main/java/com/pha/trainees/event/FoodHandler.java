package com.pha.trainees.event;

import com.pha.trainees.registry.ModItems;
import com.pha.trainees.registry.ModSounds;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class FoodHandler {
    @SubscribeEvent
    public static void onFoodEaten(LivingEntityUseItemEvent.Finish event) {
        // 仅处理玩家食用食物事件
        if (!(event.getEntity() instanceof Player player)) return;

        // 检查食用的物品是否为 ahkun_apple一类
        Item item = event.getItem().getItem();
        if (item == ModItems.AHKUN_APPLE.get() || item == ModItems.BIG_AHKUN_APPLE.get() || item == ModItems.REAL_APPLE.get()) {
            // 添加状态效果（服务器端执行）
            if (!player.level().isClientSide) {
                if (item == ModItems.AHKUN_APPLE.get()) {
                    player.addEffect(new MobEffectInstance(
                            MobEffects.DAMAGE_RESISTANCE, // 伤害吸收
                            7 * 60 * 20,                 // 持续时间：7分钟
                            0
                    ));
                    player.addEffect(new MobEffectInstance(
                            MobEffects.REGENERATION,      // 生命回复
                            15 * 20,                      // 15秒
                            1
                    ));
                    player.addEffect(new MobEffectInstance(
                            MobEffects.REGENERATION,      // 快速回血
                            2 * 20,
                            5
                    ));
                    player.addEffect(new MobEffectInstance(
                            MobEffects.SATURATION,       // 饱和
                            10 * 20,                // 10s（实际饱和效果会立即生效）
                            0
                    ));
                    player.addEffect(new MobEffectInstance(
                            MobEffects.HEALTH_BOOST,      // 生命提升
                            7 * 60 * 20,                 // 7分钟
                            2
                    ));
                    player.addEffect(new MobEffectInstance(
                            MobEffects.ABSORPTION,
                            7 * 60 * 20,
                            4
                    ));
                    player.addEffect(new MobEffectInstance(
                            MobEffects.FIRE_RESISTANCE,    //抗火
                            7 * 60 * 20,
                            0
                    ));
                    player.addEffect(new MobEffectInstance(
                            MobEffects.DAMAGE_BOOST,    //力量
                            3 * 60 * 20,
                            1
                    ));

                } else if (item == ModItems.BIG_AHKUN_APPLE.get()) {
                    player.addEffect(new MobEffectInstance(
                            MobEffects.DAMAGE_RESISTANCE, // 伤害吸收
                            10 * 60 * 20,                 // 持续时间：10分钟
                            2
                    ));
                    player.addEffect(new MobEffectInstance(
                            MobEffects.REGENERATION,      // 生命回复
                            20 * 20,                      // 20秒
                            1
                    ));
                    player.addEffect(new MobEffectInstance(
                            MobEffects.REGENERATION,      // 快速满血
                            2 * 20,
                            8
                    ));
                    player.addEffect(new MobEffectInstance(
                            MobEffects.SATURATION,       // 饱和
                            10 * 20,                // 10s（实际饱和效果会立即生效）
                            2
                    ));
                    player.addEffect(new MobEffectInstance(
                            MobEffects.HEALTH_BOOST,      // 生命提升
                            10 * 60 * 20,                 // 10分钟
                            4
                    ));
                    player.addEffect(new MobEffectInstance(
                            MobEffects.ABSORPTION,
                            10 * 60 * 20,
                            8
                    ));
                    player.addEffect(new MobEffectInstance(
                            MobEffects.FIRE_RESISTANCE,    //抗火
                            10 * 60 * 20,
                            0
                    ));
                    player.addEffect(new MobEffectInstance(
                            MobEffects.DAMAGE_BOOST,    //力量
                            10 * 60 * 20,
                            4
                    ));

                    player.level().playSound(
                            null,
                            player.getX(), player.getY(), player.getZ(),
                            ModSounds.AHKUN_SOUND.get(),
                            SoundSource.PLAYERS,
                            1.0f, 1.0f
                    );
                } else if (item == ModItems.REAL_APPLE.get()){
                    player.addEffect(new MobEffectInstance(
                            MobEffects.CONFUSION, //反胃
                            10 * 60 * 20,
                            4
                    ));
                    player.addEffect(new MobEffectInstance(
                            MobEffects.HUNGER,
                            2 * 60 * 20,
                            4
                    ));
                    player.addEffect(new MobEffectInstance(
                            MobEffects.POISON,
                            3 * 20,
                            4
                    ));
                    player.addEffect(new MobEffectInstance(
                            MobEffects.DAMAGE_BOOST,
                            30 * 20,
                            4
                    ));
                }
            }

            // 播放音效（客户端执行）

        }
    }
}
