package com.pha.trainees.way.chemistry;

import com.pha.trainees.registry.ModBlocks;
import com.pha.trainees.registry.ModChemistry;
import com.pha.trainees.registry.ModItems;
import com.pha.trainees.registry.ModTags;
import com.pha.trainees.way.game.NumedItemEntities;
import com.pha.trainees.way.game.Tools;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;

import static com.pha.trainees.way.game.Tools.*;

public class ChemicalReaction {

    // Ji + Bp == JiBp
    public static boolean JiBpCombination_1(UseOnContext context, int num){
        //检测端
        if (context.getLevel().isClientSide()) return false;

        //检测数量
        Player player = context.getPlayer();
        if (context.getItemInHand().getCount() < num && !player.isCreative()) return false;

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

        if (!isRight) return false;

        //检测篝火状态
        boolean isLit = false;
        if (belowState.hasProperty(net.minecraft.world.level.block.CampfireBlock.LIT)) {
            isLit = belowState.getValue(net.minecraft.world.level.block.CampfireBlock.LIT);
        }

        if (!isLit) return false;


        Tools.DoTnt_6(level, x, y, z, 6.0F, 6, 1);
        //实现逻辑
//        Block targetBlock = ForgeRegistries.BLOCKS.getValue(ModBlocks.BASKETBALL_ANTI_BLOCK_RGT.getId());
        BlockState blockState = ModChemistry.ModChemistryBlocks.CHE_JIBP_BLOCK_RGT.get().defaultBlockState();

        // 替换方块
        context.getLevel().setBlock(clickedPos, blockState, 3);
        if (player != null && !player.isCreative()) {
            context.getItemInHand().shrink(num);
        }
        context.getLevel().playSound(null, clickedPos, SoundEvents.STONE_PLACE, SoundSource.BLOCKS, 1.0F, 1.0F);

        return true;

    }


    // 初始化所有反应
    /* 格式

    1.即时反应
        ReactionSystem.ReactionRegistry.registerReaction(
                // 反应ID，唯一标识
                "reaction_id",

                // 条件判断：返回boolean
                (stack, entity) -> {
                    // 示例：检查物品类型和环境条件
                    return stack.getItem() == targetItem &&
                           entity.isInWater() &&
                           otherConditions;
                },

                // 结果执行：当条件满足时立即执行
                (level, entity, stack) -> {
                    // 示例：生成新物品、播放声音、产生爆炸等
                    entity.discard();
                    // 生成反应产物...
                    new ItemEntity = …………
                },

                // 优先级，数字越大越先执行（可选） int值
                priority
        );


    2.长时反应
        ReactionSystem.ReactionRegistry.registerTimedReaction(
                // 反应ID，唯一标识
                "reaction_id",

                // 新建一个时间控制器
                new ReactionSystem.ITimedReaction() {

                    // 何时开始计时：当条件满足且计时器未启动时
                    @Override
                    public boolean shouldStartTimer(ItemStack stack, ItemEntity entity) {

                        return conditionForStartingTimer(stack, entity) &&
                               !ReactionSystem.TimeManager.isTimerActive(entity, "reaction_id");
                    }

                    // 何时触发反应：条件持续满足且计时结束
                    @Override
                    public boolean shouldReact(ItemStack stack, ItemEntity entity) {

                        return conditionForReaction(stack, entity) &&
                               ReactionSystem.TimeManager.shouldReact(entity, "reaction_id", requiredSeconds);
                    }

                    // 开始计时时的操作
                    @Override
                    public void onTimerStart(ItemStack stack, ItemEntity entity) {

                        if (conditionForStartingTimer(stack, entity)) {
                            double reactionTime = calculateReactionTime(stack, entity); // 秒为单位
                            ReactionSystem.TimeManager.startTimer(entity, "reaction_id", reactionTime);
                        }
                    }

                    // 当条件不再满足时重置计时器
                    @Override
                    public void onTimerReset(ItemStack stack, ItemEntity entity) {

                        if (!conditionForReaction(stack, entity)) {
                            ReactionSystem.TimeManager.resetTimer(entity, "reaction_id");
                        }
                    }

                },

                // 结果执行：计时结束后执行
                (level, entity, stack) -> {
                    // 示例：生成新物品、播放声音、产生爆炸等
                    entity.discard();
                    // 生成反应产物...
                    new ItemEntity = …………
                },

                // 优先级，数字越大越先执行（可选） int值
                priority
        );

    */
    public static void registerAllReactions() {
        // HBpO 光照分解反应
        // 2HBpO =光照= 2HBp + O2↑
            ReactionSystem.ReactionRegistry.registerTimedReaction(
                    "hbp_decompose",
                    new ReactionSystem.ITimedReaction() {
                        @Override
                        public boolean shouldStartTimer(ItemStack stack, ItemEntity entity) {
                            return stack.getItem() == ModChemistry.ModChemistryItems.CHE_HBPO.get() &&
                                    Tools.isInSunlight(entity) &&
                                    !ReactionSystem.TimeManager.isTimerActive(entity, "hbp_decompose");
                        }

                        @Override
                        public boolean shouldReact(ItemStack stack, ItemEntity entity) {
                            if (stack.getItem() != ModChemistry.ModChemistryItems.CHE_HBPO.get() ||
                                    !Tools.isInSunlight(entity)) {
                                return false;
                            }

                            double punishTime = Tools.punishmentTimeToSunlight(entity);
                            if (punishTime >= 9999) return false; // 不在有效光照时间

                            double randomTime = randomInRange(entity.level(), 0.0, 0.5);
                            double totalTime = randomTime + punishTime;

                            return ReactionSystem.TimeManager.shouldReact(entity, "hbp_decompose", totalTime);
                        }

                        @Override
                        public void onTimerStart(ItemStack stack, ItemEntity entity) {
                            double punishTime = Tools.punishmentTimeToSunlight(entity);
                            if (punishTime < 9999) {
                                double randomTime = randomInRange(entity.level(), 0.0, 1.0);
                                double totalTime = randomTime + punishTime;
                                ReactionSystem.TimeManager.startTimer(entity, "hbp_decompose", totalTime);
                            }
                        }

                        @Override
                        public void onTimerReset(ItemStack stack, ItemEntity entity) {
                            if (stack.getItem() != ModChemistry.ModChemistryItems.CHE_HBPO.get() ||
                                    !Tools.isInSunlight(entity)) {
                                ReactionSystem.TimeManager.resetTimer(entity, "hbp_decompose");
                            }
                        }
                    },
                    // 结果
                    (level, entity, stack) -> {
                        int count = stack.getCount() / 2;
                        if (count > 0) {
                            entity.discard();

                            NumedItemEntities itemEntities = AddEntity(level, entity,
                                    new ItemStack(ModChemistry.ModChemistryItems.CHE_HBP.get(), count * 2) , true);
                            AddNIEs(entity, itemEntities, false);

                        }
                    },
                    10 // 高优先级
            );

        // 黑单质（黑 Bp2）与水的反应 - 长时
        // Bp2 + H2O == HBp + HBpO
            ReactionSystem.ReactionRegistry.registerTimedReaction(
                    "bp2_water_reaction",
                    new ReactionSystem.ITimedReaction() {
                        @Override
                        public boolean shouldStartTimer(ItemStack stack, ItemEntity entity) {
                            return isAntiPowder2(stack.getItem()) &&
                                    (entity.isInWater() || entity.level().isRaining() || entity.level().isThundering()) &&
                                    !ReactionSystem.TimeManager.isTimerActive(entity, "bp2_water_reaction");
                        }

                        @Override
                        public boolean shouldReact(ItemStack stack, ItemEntity entity) {
                            if (!isAntiPowder2(stack.getItem()) ||
                                    !(entity.isInWater() || entity.level().isRaining() || entity.level().isThundering())) {
                                return false;
                            }

                            // 使用固定的5-10秒反应时间
                            return ReactionSystem.TimeManager.shouldReact(entity, "bp2_water_reaction",
                                    5 + entity.level().random.nextInt(6));
                        }

                        @Override
                        public void onTimerStart(ItemStack stack, ItemEntity entity) {
                            if (isAntiPowder2(stack.getItem()) && entity.isInWater()) {
                                // 固定5-10秒反应时间
                                ReactionSystem.TimeManager.startTimer(entity, "bp2_water_reaction",
                                        5 + entity.level().random.nextInt(6));
                            }
                        }

                        @Override
                        public void onTimerReset(ItemStack stack, ItemEntity entity) {
                            if (!isAntiPowder2(stack.getItem()) || !entity.isInWater()) {
                                ReactionSystem.TimeManager.resetTimer(entity, "bp2_water_reaction");
                            }
                        }

                        private boolean isAntiPowder2(Item item) {
                            return Tools.isInstanceof.tag(item, ModTags.POWDER_ANTI_2);
                        }
                    },
                    // 结果：生成HBp和HBpO
                    (level, entity, stack) -> {
                        int simpleSubstance = 2;
                        int multiplier = getPowderMultiplier(stack.getItem());
                        int count = stack.getCount() * multiplier * simpleSubstance / 2;

                        if (count > 0) {
                            entity.discard();

                            NumedItemEntities itemEntities_1 = AddEntity(level, entity,
                                    new ItemStack(ModChemistry.ModChemistryItems.CHE_HBP.get(), count) , true);
                            NumedItemEntities itemEntities_2 = AddEntity(level, entity,
                                    new ItemStack(ModChemistry.ModChemistryItems.CHE_HBPO.get(), count) , true);
                            AddNIEs(entity, itemEntities_1, false);
                            AddNIEs(entity, itemEntities_2, false);
                        }
                    },
                    5
            );

        // Ji与水反应 - 即时
        // 2Ji + 2H2O == 2JiOH + H2↑
            ReactionSystem.ReactionRegistry.registerReaction(
                    "ji_water_reaction",
                    // 条件：是Ji物品且在水中
                    (stack, entity) ->
                            (stack.getItem() == ModItems.KUN_NUGGET.get() || stack.getItem() == ModItems.TWO_HALF_INGOT.get()
                                    || stack.getItem() == ModItems.TWO_HALF_INGOT_BLOCK_ITEM.get())
                                    && entity.isInWater(),
                    // 结果：生成对应的氢氧化物
                    (level, entity, stack) -> {
                        double x = entity.getX(), y = entity.getY(), z = entity.getZ();
                        Tools.DoTnt_center(level, x, y, z);

                        entity.discard();
                        Item item = stack.getItem() == ModItems.KUN_NUGGET.get() ?
                                ModChemistry.ModChemistryItems.CHE_JIOH_NUGGET.get() :
                                ( stack.getItem() == ModItems.TWO_HALF_INGOT.get() ?
                                        ModChemistry.ModChemistryItems.CHE_JIOH.get() :
                                        ModChemistry.ModChemistryBlockItems.CHE_JIOH_BLOCK_ITEM.get());
                        int count = stack.getCount();

                        NumedItemEntities itemEntities = AddEntity(level, entity, new ItemStack(item, count) , true);
                        AddNIEs(entity, itemEntities, true);
                    },
                    5
            );
    }

    // 辅助方法保持不变


}


