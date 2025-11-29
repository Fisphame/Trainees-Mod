package com.pha.trainees.way.chemistry;

import com.pha.trainees.Main;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.fml.common.Mod;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Mod.EventBusSubscriber(modid = Main.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ReactionSystem {

    // 反应条件接口
    @FunctionalInterface
    public interface IReactionCondition {
        boolean test(ItemStack stack, ItemEntity entity);
    }

    // 反应结果接口
    @FunctionalInterface
    public interface IReactionResult {
        void execute(Level level, ItemEntity entity, ItemStack stack);
    }

    // 时间管理反应接口
    public interface ITimedReaction {
        boolean shouldStartTimer(ItemStack stack, ItemEntity entity);
        boolean shouldReact(ItemStack stack, ItemEntity entity);
        void onTimerStart(ItemStack stack, ItemEntity entity);
        void onTimerReset(ItemStack stack, ItemEntity entity);
    }

    // 反应定义
    public static class ChemicalReaction {
        public final String id;
        public final IReactionCondition condition;
        public final IReactionResult result;
        public final ITimedReaction timedLogic;
        public final int priority;
        public final boolean isTimed;

        public ChemicalReaction(String id, IReactionCondition condition, IReactionResult result, int priority) {
            this.id = id;
            this.condition = condition;
            this.result = result;
            this.timedLogic = null;
            this.priority = priority;
            this.isTimed = false;
        }

        public ChemicalReaction(String id, ITimedReaction timedLogic, IReactionResult result, int priority) {
            this.id = id;
            this.condition = null;
            this.result = result;
            this.timedLogic = timedLogic;
            this.priority = priority;
            this.isTimed = true;
        }
    }

    // 反应注册表
    public static class ReactionRegistry {
        private static final Map<String, ChemicalReaction> REACTIONS = new ConcurrentHashMap<>();
        private static final List<ChemicalReaction> SORTED_REACTIONS = new ArrayList<>();
        private static boolean needsSorting = false;

        public static void registerReaction(String id, IReactionCondition condition, IReactionResult result) {
            registerReaction(id, condition, result, 0);
        }

        public static void registerReaction(String id, IReactionCondition condition, IReactionResult result, int priority) {
            REACTIONS.put(id, new ChemicalReaction(id, condition, result, priority));
            needsSorting = true;
        }

        public static void registerTimedReaction(String id, ITimedReaction timedLogic, IReactionResult result) {
            registerTimedReaction(id, timedLogic, result, 0);
        }

        public static void registerTimedReaction(String id, ITimedReaction timedLogic, IReactionResult result, int priority) {
            REACTIONS.put(id, new ChemicalReaction(id, timedLogic, result, priority));
            needsSorting = true;
        }

        public static boolean triggerReactions(ItemStack stack, ItemEntity entity) {
            if (needsSorting) {
                sortReactions();
            }

            for (ChemicalReaction reaction : SORTED_REACTIONS) {
                boolean shouldReact = false;

                if (reaction.isTimed && reaction.timedLogic != null) {
                    // 处理时间管理反应
                    if (reaction.timedLogic.shouldStartTimer(stack, entity)) {
                        reaction.timedLogic.onTimerStart(stack, entity);
                    }

                    if (reaction.timedLogic.shouldReact(stack, entity)) {
                        shouldReact = true;
                    } else {
                        // 如果不应该反应但条件不满足，重置计时器
                        reaction.timedLogic.onTimerReset(stack, entity);
                    }
                } else if (reaction.condition != null) {
                    // 处理即时反应
                    shouldReact = reaction.condition.test(stack, entity);
                }

                if (shouldReact) {
                    reaction.result.execute(entity.level(), entity, stack);
                    return true; // 一次只触发一个反应
                }
            }
            return false;
        }

        private static void sortReactions() {
            SORTED_REACTIONS.clear();
            SORTED_REACTIONS.addAll(REACTIONS.values());
            SORTED_REACTIONS.sort((a, b) -> Integer.compare(b.priority, a.priority));
            needsSorting = false;
        }

        public static Collection<ChemicalReaction> getAllReactions() {
            return REACTIONS.values();
        }
    }


    // 时间管理工具类（增强版）
    public static class TimeManager {
        private static final String START_TIME = "reaction_start_time";
        private static final String REQUIRED_TIME = "reaction_required_time";

        public static boolean shouldReact(ItemEntity entity, String reactionId, double requiredSeconds) {
            var tag = entity.getPersistentData();
            String startKey = START_TIME + "_" + reactionId;
            String requiredKey = REQUIRED_TIME + "_" + reactionId;

            if (!tag.contains(startKey)) {
                return false;
            }

            long start = tag.getLong(startKey);
            double requiredTicks = requiredSeconds * 20; // 秒转换为tick
            long current = entity.level().getGameTime();

            if (current - start >= requiredTicks) {
                tag.remove(startKey);
                tag.remove(requiredKey);
                return true;
            }
            return false;
        }

        public static void startTimer(ItemEntity entity, String reactionId, double requiredSeconds) {
            var tag = entity.getPersistentData();
            String startKey = START_TIME + "_" + reactionId;
            String requiredKey = REQUIRED_TIME + "_" + reactionId;

            tag.putLong(startKey, entity.level().getGameTime());
            tag.putDouble(requiredKey, requiredSeconds * 20); // 存储为tick
        }

        public static void resetTimer(ItemEntity entity, String reactionId) {
            var tag = entity.getPersistentData();
            String startKey = START_TIME + "_" + reactionId;
            String requiredKey = REQUIRED_TIME + "_" + reactionId;
            tag.remove(startKey);
            tag.remove(requiredKey);
        }

        public static boolean isTimerActive(ItemEntity entity, String reactionId) {
            var tag = entity.getPersistentData();
            String startKey = START_TIME + "_" + reactionId;
            return tag.contains(startKey);
        }
    }
}