package com.pha.trainees.event;

import com.google.common.collect.Multimap;
import com.pha.trainees.entity.CalledSwordEntity;
import com.pha.trainees.registry.ModEnchantments;
import com.pha.trainees.registry.ModEntities;
import com.pha.trainees.util.CooldownTracker;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.Map;


public class AbilityHandler {

        private static final int COOLDOWN_TICKS = 0 * 20; // 冷却

        @SubscribeEvent
        public static void onRightClickItem(PlayerInteractEvent.RightClickItem event) {
            Player player = event.getEntity();
            ItemStack stack = player.getMainHandItem();

            // 检查是否持有剑且附魔存在
            if (stack.getItem() instanceof SwordItem &&
                    stack.getEnchantmentLevel(ModEnchantments.ten_thousand_sword.get()) > 0) {

                // 检查冷却状态
                if (CooldownTracker.isOnCooldown(player)) {
                    player.displayClientMessage(Component.literal("技能冷却中！"), true);
                    return;
                }

                // 触发技能
                int level = stack.getEnchantmentLevel(ModEnchantments.ten_thousand_sword.get());
                activateAbility(player, stack, level);

                // 设置冷却
                CooldownTracker.setCooldown(player, COOLDOWN_TICKS);
                // 消耗耐久
                stack.hurtAndBreak(20, player, (e) -> e.broadcastBreakEvent(EquipmentSlot.MAINHAND));
            }
        }

        private static void activateAbility(Player player, ItemStack sword, int level) {
            // 召唤剑实体逻辑
                spawnSwords(player, level, sword);
        }


        private static void spawnSwords(Player player, int level, ItemStack sword) {
            Direction direction = player.getDirection(); // 玩家朝向
            int totalSwords = level * 4; // N=附魔等级×n
            double baseX = player.getX();
            double baseY = player.getY() + 1;
            double baseZ = player.getZ();






            // 计算伤害
            float baseDamage = 0.0f;
            Multimap<net.minecraft.world.entity.ai.attributes.Attribute, AttributeModifier> modifiers = sword.getAttributeModifiers(EquipmentSlot.MAINHAND);
            for (Map.Entry<Attribute, AttributeModifier> entry : modifiers.entries()) {
                if (entry.getKey() == Attributes.ATTACK_DAMAGE) {
                    baseDamage = (float) entry.getValue().getAmount();
                    break;
                }
            }
            float enchantDamage = EnchantmentHelper.getDamageBonus(sword, MobType.UNDEFINED);
            float damage = (baseDamage+enchantDamage) * 1.5f;

            //计算方向
            Direction direction1=player.getDirection();
//            float pitch = player.getXRot();
//            if( -30.0F < pitch && pitch < 30.0F){
//
//            }

            //计算坐标
            for (int i = 0; i < totalSwords; i++) {
                // 计算每对剑的偏移量
                int pairIndex = i / 2;
                //double offset = ( (pairIndex % 8) + 1 ) * 0.5; // 每对距离递增0.5，超过4.0后重置
                //double yOffset = (pairIndex / 8) * 1; // 每8对Y轴增加1

                double offset = pairIndex * 0.5; // 每对距离递增n，无上限
                double yOffset = 0; // Y轴不增加
                ///注：弧形其实是两条等边

/*
                offset:x z的共同偏移
                要使最终效果如下：
                   ·      ·
                  ·        ·
                 ·          ·
                ·            ·
                    player

                东 +x  西-x
                南 +z  北-z
 */

                // 根据朝向计算初始位置
                // 左右分布
                double sideOffset = (i % 2 == 0) ? -offset : offset; //弧形
                sideOffset = sideOffset * 1.25;
                //当剑[i]为奇数数，向正方向；偶数，向负方向
                //double sideOffset = (i % 2 == 0) ? -1 : 1;//直线
                double x, y, z;
                float constIndex = 1; //额定偏移

                switch (direction) {
                    case NORTH -> { // 朝北（-Z方向）
                        x = baseX + sideOffset;
                        z = baseZ - constIndex + offset / 2;
                    }
                    case SOUTH -> { // 朝南（+Z方向）
                        x = baseX + sideOffset;
                        //x = baseX * sideOffset;
                        z = baseZ + constIndex - offset / 2;
                    }
                    case EAST -> { // 朝东（+X方向）
                        x = baseX + constIndex - offset / 2;
                        z = baseZ + sideOffset;
                        //z = baseX * sideOffset;
                    }
                    case WEST -> { // 朝西（-X方向）
                        x = baseX - constIndex + offset / 2;
                        z = baseZ + sideOffset;
                        //z = baseX * sideOffset;
                    }
                    default -> { // 默认朝南
                        x = baseX;
                        z = baseZ;
                    }
                }

                y = baseY + yOffset;



                // 生成剑实体
                CalledSwordEntity swordEntity = new CalledSwordEntity(
                        ModEntities.CALLED_SWORD.get(),
                        player.level()

                );

                //传参
                swordEntity.setDamage(damage);
                swordEntity.setDirection(direction1);
                swordEntity.setPos(x, y, z);
                player.level().addFreshEntity(swordEntity);
            }
        }
    }




