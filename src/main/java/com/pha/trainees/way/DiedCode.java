package com.pha.trainees.way;

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

}
