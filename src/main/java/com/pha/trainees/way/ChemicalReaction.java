package com.pha.trainees.way;

import com.pha.trainees.Main;
import com.pha.trainees.registry.ModBlocks;
import com.pha.trainees.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;

// 聚集所有的反应，全部反应此化学反应文件
public class ChemicalReaction {

    private static final String startTime = "start_time";
    private static final String requiredTime = "required_time";
    private static final String waterStartTime = "water_start_time";
    private static final String requiredDuration = "required_duration";

    //
    public static boolean HBpODecompose(ItemStack stack, ItemEntity entity) {
        if (!entity.level().isClientSide) {
            Level level = entity.level();
            double x = entity.getX(), y = entity.getY(), z = entity.getZ();
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
                    Item HBP = ModItems.CHE_HBP.get();
                    ItemStack HBPStack = new ItemStack(HBP, count * 2);
                    ItemEntity HBPEntity = new ItemEntity(level, x, y, z, HBPStack);
                    HBPEntity.setDefaultPickUpDelay();
                    HBPEntity.setDeltaMovement(entity.getDeltaMovement().x, 0.5, entity.getDeltaMovement().z);

                    entity.level().addFreshEntity(HBPEntity);

                    // 清除标签数据
                    tag.remove(startTime);
                    tag.remove(ChemicalReaction.requiredTime);

                    return true;
                }
            }
        }
        return false;
    }

    // Ji + Bp == JiBp
    public static InteractionResult JiBpCombination_1(UseOnContext context, int num){
        //检测端
        if (context.getLevel().isClientSide()) return InteractionResult.FAIL;

        //检测数量
        Player player = context.getPlayer();
        if (context.getItemInHand().getCount() < num && !player.isCreative()) return InteractionResult.FAIL;

        //检测方块
        Level level = context.getLevel();
        BlockPos clickedPos = context.getClickedPos();
        BlockState clickedState = context.getLevel().getBlockState(clickedPos);
        ResourceLocation clickedBlockId = ForgeRegistries.BLOCKS.getKey(clickedState.getBlock());
        BlockPos belowPos = clickedPos.below();
        BlockState belowState = level.getBlockState(belowPos);
        ResourceLocation belowBlockId = ForgeRegistries.BLOCKS.getKey(belowState.getBlock());
        double x = clickedPos.getX(), y = clickedPos.getY(), z = clickedPos.getZ();
        boolean isRight = clickedBlockId != null && clickedBlockId == ModBlocks.TWO_HALF_INGOT_BLOCK.getId()
                && belowBlockId != null && belowBlockId.toString().equals("minecraft:campfire");

        if (!isRight) return InteractionResult.FAIL;

        //检测篝火状态
        boolean isLit = false;
        if (belowState.hasProperty(net.minecraft.world.level.block.CampfireBlock.LIT)) {
            isLit = belowState.getValue(net.minecraft.world.level.block.CampfireBlock.LIT);
        }
        if (!isLit) return InteractionResult.FAIL;



        //实现逻辑
        Block targetBlock = ForgeRegistries.BLOCKS.getValue(ModBlocks.BASKETBALL_ANTI_BLOCK_RGT.getId());

        if (targetBlock != null) {
            // 替换方块
            context.getLevel().setBlock(clickedPos, targetBlock.defaultBlockState(), 3);
            if (player != null && !player.isCreative()) {
                context.getItemInHand().shrink(num);
            }
            context.getLevel().playSound(null, clickedPos, SoundEvents.STONE_PLACE, SoundSource.BLOCKS, 1.0F, 1.0F);
            Ways.Use.DoTnt_6(level, x, y, z, 6.0F, 6, 1);

            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    //
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

    // 2Bp + H2O == HBp + HBpO
    public static boolean Bp2AndH2O(int number, ItemStack stack, ItemEntity entity) {
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
                int count = stack.getCount() * number / 2;
                if (count != 0) {

                    // 创建新的物品实体
                    Item HBP = ModItems.CHE_HBP.get();
                    ItemStack HBPStack = new ItemStack(HBP, count);
                    ItemEntity HBPEntity = new ItemEntity(level, x, y, z, HBPStack);
                    HBPEntity.setDefaultPickUpDelay();

                    Item HBPO = ModItems.CHE_HBPO.get();
                    ItemStack HBPOStack = new ItemStack(HBPO, count);
                    ItemEntity HBPOEntity = new ItemEntity(level, x, y, z, HBPOStack);
                    HBPOEntity.setDefaultPickUpDelay();


                    entity.discard();
                    entity.level().addFreshEntity(HBPEntity);
                    entity.level().addFreshEntity(HBPOEntity);

                    int rest = stack.getCount() * number % 2;
                    if (rest != 0){
                        Item BP = ModItems.POWDER_ANTI.get();
                        ItemStack BPStack = new ItemStack(BP, rest);
                        ItemEntity BPEntity = new ItemEntity(level, x, y, z, BPStack);
                        BPEntity.setDefaultPickUpDelay();

                        entity.level().addFreshEntity(BPEntity);
                    }

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


}
