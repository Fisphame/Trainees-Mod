package com.pha.trainees.way;

import com.pha.trainees.entity.CalledSwordEntity;
import com.pha.trainees.registry.ModEntities;

public class DiedCode {
//    private static final String startTime = "start_time";
//    private static final String requiredTime = "required_time";
//    private static final String waterStartTime = "water_start_time";
//    private static final String requiredDuration = "required_duration";

    // 2HBpO =光照= 2HBp + O2↑
    /*

    public static boolean HBpODecompose(ItemStack stack, ItemEntity entity){
        if (!entity.level().isClientSide) {
            Level level = entity.level();
            // 获取或创建实体标签来存储计时信息
            var tag = entity.getPersistentData();


            // 检查是否已经开始计时
            if (!tag.contains(startTime)) {
                // 记录开始存在时间和随机持续时间
                tag.putLong(startTime, entity.level().getGameTime());

                // 随机0-2s + 惩罚时间
                double randomTime = entity.level().random.nextInt(20);
                double punishTime = Ways.Use.punishmentTimeToSunlight(entity);
                // punishTime ∈ { [1,6.92] ∪ [9999,9999] }
                tag.putDouble(requiredTime, randomTime + punishTime);
                return false;
            }

            // 获取开始时间和要求的持续时间
            long start = tag.getLong(startTime);
            double require = tag.getDouble(ChemicalReaction.requiredTime);
            long currentTime = entity.level().getGameTime();

            // 检查是否已经达到要求的持续时间
            if (currentTime - start >= require) {
                int count = stack.getCount() / 2;
                entity.discard();
                if (count != 0) {
                    // 创建新的物品实体
                    CheWays.AddEntity(level, entity, ModChemistry.ModChemistryItems.CHE_HBP.get(), count * 2, true );

                    // 清除标签数据
                    tag.remove(startTime);
                    tag.remove(ChemicalReaction.requiredTime);

                    return true;
                }
            }
        }
        return false;
    }
*/


    // 2Ji + 2H2O == 2JiOH + H2↑
    /*
    public static boolean JiAndH2O(int number, ItemStack stack, ItemEntity entity) {
        if (!entity.level().isClientSide && entity.isInWater()) {
            Level level = entity.level();
            double x = entity.getX(), y = entity.getY(), z = entity.getZ();
            Ways.Use.DoTnt_center(level, x, y, z);

            //移除物品实体 创建物品 获取原物品堆叠个数 创建物品堆叠 创建掉落物实体 设置无敌 设置默认拾取延迟 生成实体
            entity.discard();
            var item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(Main.MODID, number==1 ? "che_jioh_nugget" : (number == 2 ?  "che_jioh" : "che_jioh_block" )));
            if (item != null) {
                int count = stack.getCount();
                ItemStack itemStack = new ItemStack(item, count);
                ItemEntity itemEntity = new ItemEntity(level, x, y, z, itemStack);
                itemEntity.setInvulnerable(true);
                itemEntity.setDefaultPickUpDelay();
                level.addFreshEntity(itemEntity);
            }


            return true;
        }
        return false;
    }

     */

    // Bp2 + H2O == HBp + HBpO
    /*
    public static boolean Bp2AndH2O(int number, ItemStack stack, ItemEntity entity, int simpleSubstance) {
        if (!entity.level().isClientSide && entity.isInWater()) {
            Level level = entity.level();
            double x = entity.getX(), y = entity.getY(), z = entity.getZ();
            // 获取或创建实体标签来存储计时信息
            var tag = entity.getPersistentData();

            // 检查是否已经开始计时
            if (!tag.contains(waterStartTime)) {
                // 第一次进入水中，记录开始时间和随机持续时间
                tag.putLong(waterStartTime, entity.level().getGameTime());
                // 随机5-10s 即100-200tick
                tag.putInt(requiredDuration, 100 + entity.level().random.nextInt(100));
                return false;
            }

            // 获取开始时间和要求的持续时间
            long startTime = tag.getLong(waterStartTime);
            int requiredDuration = tag.getInt(ChemicalReaction.requiredDuration);
            long currentTime = entity.level().getGameTime();

            // 检查是否已经达到要求的持续时间
            if (currentTime - startTime >= requiredDuration) {
                int count = stack.getCount() * number * simpleSubstance / 2;
                if (count != 0) {

                    entity.discard();
                    CheWays.AddEntity(level, entity, ModChemistry.ModChemistryItems.CHE_HBP.get(), count , true);
                    CheWays.AddEntity(level, entity, ModChemistry.ModChemistryItems.CHE_HBPO.get(), count , true);

//                    int rest = stack.getCount() * number * simpleSubstance % 2;
//                    if (rest != 0){
//                        CheWays.AddEntity(level, entity,ModItems.POWDER_ANTI.get(), rest, false );
//                    }

                    // 清除标签数据
                    tag.remove(waterStartTime);
                    tag.remove(ChemicalReaction.requiredDuration);

                    return true;
                }
            }
        } else {
            // 不在水中，清除计时数据
            var tag = entity.getPersistentData();
            if (tag.contains(waterStartTime)) {
                tag.remove(waterStartTime);
                tag.remove(requiredDuration);
            }
        }
        return false;
    }

     */

    /*
public class AbilityHandler {

        private static final int COOLDOWN_TICKS = 10 * 20;
        private static final int HURT_BREAK = 5;

        @SubscribeEvent
        public static void onRightClickItem(PlayerInteractEvent.RightClickItem event) {
            Level level = event.getLevel();
            Player player = event.getEntity();
            ItemStack stack = player.getMainHandItem();

            // 检查是否持有剑且附魔存在
            if (stack.getItem() instanceof SwordItem &&
                    stack.getEnchantmentLevel(ModEnchantments.ten_thousand_sword.get()) > 0) {
                Item item = stack.getItem();
                float basicDamage = ((SwordItem) stack.getItem()).getDamage();
                float enchantDamage = EnchantmentHelper.getDamageBonus(stack, MobType.UNDEFINED);
                if (Tools.isInstanceofScythe(item)){
                    basicDamage *= 2;
                }

                float damage = basicDamage + enchantDamage;
                if (player.getCooldowns().isOnCooldown(item)){
                    player.displayClientMessage(Component.translatable("msg.trainees.isOnCooldown"), true);
                    return;
                }
                level.playSound(null, new BlockPos(player.getBlockX(), player.getBlockY(), player.getBlockZ()),
                        SoundEvents.ITEM_BREAK, SoundSource.PLAYERS, 0.5F, 1.0F);
                // 触发技能
                int degree = stack.getEnchantmentLevel(ModEnchantments.ten_thousand_sword.get());
                activateAbility(player, damage, degree);

                if (!player.isCreative()) {
                    player.getCooldowns().addCooldown(item, COOLDOWN_TICKS);
                    stack.hurtAndBreak(HURT_BREAK, player, (e) -> e.broadcastBreakEvent(EquipmentSlot.MAINHAND));
                }
            }
        }

        private static void activateAbility(Player player, float damage, int degree) {
            spawnSwords(player, degree, damage);
        }


        private static void spawnSwords(Player player, int degree, float damage) {
            Direction direction = player.getDirection(); // 玩家朝向
            int totalSwords = degree * 4; // N=附魔等级×n
            double baseX = player.getX();
            double baseY = player.getY() + 1;
            double baseZ = player.getZ();



            // 计算伤害
//            float baseDamage = 0.0f;
//            Multimap<net.minecraft.world.entity.ai.attributes.Attribute, AttributeModifier> modifiers =
//                    stack.getAttributeModifiers(EquipmentSlot.MAINHAND);
//            for (Map.Entry<Attribute, AttributeModifier> entry : modifiers.entries()) {
//                if (entry.getKey() == Attributes.ATTACK_DAMAGE) {
//                    baseDamage = (float) entry.getValue().getAmount();
//                    break;
//                }
//            }
            float re_damage = damage * 2.0f;

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
/*
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
                swordEntity.setDamage(re_damage);
                swordEntity.setDirection(direction1);
                swordEntity.setPos(x, y, z);
                swordEntity.setOffPlayer(player);
                player.level().addFreshEntity(swordEntity);
}
        }
                }







 */
}
