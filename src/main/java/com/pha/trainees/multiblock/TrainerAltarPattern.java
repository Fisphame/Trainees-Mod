package com.pha.trainees.multiblock;

import com.pha.trainees.Main;
import com.pha.trainees.block.AbsorbBlock;
import com.pha.trainees.block.KunAltarBlock;
import com.pha.trainees.blockentity.AbsorbBlockEntity;
import com.pha.trainees.blockentity.KunAltarBlockEntity;
import com.pha.trainees.recipe.TrainerAltarRecipe;
import com.pha.trainees.registry.ModBlocks;
import com.pha.trainees.registry.ModRecipes;
import com.pha.trainees.util.game.enums.AbsorbWorkModel;
import com.pha.trainees.util.game.enums.KunAltarType;
import com.pha.trainees.util.game.Tools;
import com.pha.trainees.util.game.structure.*;
import com.pha.trainees.util.interfaces.Traversal;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.Objects;

/**
 * Trainer Altar 多方块结构
 */
public class TrainerAltarPattern {
    public static final String STRUCTURE_ID = "trainees:trainer_altar";
    // 1 - north  2 - east  3 - south  4 - west
    // 掉落位置允许的范围（相对于核心方块 matchPos）
    private static final int MIN_X = -4;
    private static final int MAX_X = 4;
    private static final int MIN_Y = -4;
    private static final int MAX_Y = 4;
    private static final int MIN_Z = -1;
    private static final int MAX_Z = 5;

    public static void register() {
        // 创建激活处理器
        IActivationHandler activationHandler = new TrainerAltarActivationHandler();

        // 关键：设置正确的偏移
        // 我们以 (0,0,0) 位置（two_half_ingot_block）作为结构原点
        BlockPos originOffset = new BlockPos(0, 0, 0);  // 检查位置就是原点位置
        BlockPos matchPos = new BlockPos(0, 1, 0);      // 核心方块在原点上方一格


        // 构建结构模式
        MultiblockPattern pattern = new MultiblockPattern.Builder()
                .dimensions(7, 3, 7)  // a×b×c 结构
                .originOffset(originOffset)
                .matchPos(matchPos)
                .activationHandler(activationHandler)

                // 基础方块 (0,0,0)
                .addCondition(new BlockPos(0, 0, 0),
                        (level, pos, state) ->
                                state.is(ModBlocks.TWO_HALF_INGOT_BLOCK.get()) || state.is(ModBlocks.WAXED_TWO_HALF_INGOT_BLOCK.get())
                )

                // 核心方块
                .addCondition(new BlockPos(0, 1, 0),
                        IBlockPredicate.block(ModBlocks.ALTAR_CORE_BLOCK.get())
                )

                // 顶部方块
                .addCondition(new BlockPos(0, 2, 0),
                        (level, pos, state) ->
                        state.is(ModBlocks.TWO_HALF_INGOT_BLOCK.get()) || state.is(ModBlocks.WAXED_TWO_HALF_INGOT_BLOCK.get())
                )

                // KunAltar 方块
                .addCondition(new BlockPos(-3, 0, 0),
                        createKunAltarCondition(KunAltarType.HALF)
                )
                .addCondition(new BlockPos(3, 0, 0),
                        createKunAltarCondition(KunAltarType.HALF)
                )
                .addCondition(new BlockPos(0, 0, -3),
                        createKunAltarCondition(KunAltarType.HALF)
                )
                .addCondition(new BlockPos(0, 0, 3),
                        createKunAltarCondition(KunAltarType.HALF)
                )

                // 添加一些忽略的位置（结构内部可以有空位）
//                .addIgnoredPosition(new BlockPos(-1, 0, 0))
//                .addIgnoredPosition(new BlockPos(1, 0, 0))
//                .addIgnoredPosition(new BlockPos(0, 0, -1))
//                .addIgnoredPosition(new BlockPos(0, 0, 1))
                .build();

        // 注册结构
        MultiblockStructure.registerStructure(STRUCTURE_ID, pattern);

        Main.LOGGER.info("[TrainerAltar] Structure registered success : {}", STRUCTURE_ID);
    }

    /**
     * 创建 KunAltar 条件检查器
     */
    private static IBlockPredicate createKunAltarCondition(KunAltarType requiredType) {
        return (level, pos, state) -> {
            try {
                // 1. 检查是否为 KunAltar 方块
                if (!state.is(ModBlocks.KUN_ALTAR.get())) {
                    return false;
                }

                // 2. 安全地检查方块实体
                if (level.getBlockEntity(pos) instanceof KunAltarBlockEntity altarEntity) {
                    KunAltarType actualType = altarEntity.getAltarType();
                    return actualType == requiredType;
                }

                // 3. 或者使用方块的方法（如果可用）
                if (state.getBlock() instanceof KunAltarBlock altarBlock) {
                    try {
                        KunAltarType actualType = KunAltarBlock.getKunAltarType(level, pos);
                        return actualType == requiredType;
                    } catch (Exception e) {
                        Main.LOGGER.error("Failed to get KunAltar type from block method", e);
                    }
                }

                return false;
            } catch (Exception e) {
                Main.LOGGER.error("Error checking KunAltar condition at {}", pos, e);
                return false;
            }
        };
    }

    /**
     * 激活处理器实现
     */
    private static class TrainerAltarActivationHandler implements IActivationHandler, Traversal {

        @Override
        public void onActivate(Level level, BlockPos matchPos) {
            if (level.isClientSide) return;
            Main.LOGGER.info("Trainer Altar activated at {}", matchPos);
            double x = matchPos.getX(); double y = matchPos.getY(); double z = matchPos.getZ();
            Vec3 center = Tools.BlockCourse.getCenter(matchPos);
            double cx = center.x; double cy = center.y; double cz = center.z;

            // 记录激活状态
            ActiveStructureManager manager = ActiveStructureManager.get(level);
            BlockPos defaultDrop = matchPos.above(2);
            manager.addActiveStructure(level, STRUCTURE_ID, matchPos, defaultDrop);

            level.playSound(null, matchPos, SoundEvents.BEACON_ACTIVATE, SoundSource.BLOCKS, 1.0F, 1.0F);
            Tools.Particle.send(level, ParticleTypes.SOUL_FIRE_FLAME, cx, cy, cz, 200, 0.5, 0, 0.5, 0.15);
            Tools.Particle.send(level, ParticleTypes.FLAME, cx, cy, cz, 200, 0.5, 0, 0.5, 0.15);



            saveActivationData(level, matchPos);
        }

        @Override
        public void onActivationFailed(Level level, BlockPos matchPos, String reason) {
            double x = matchPos.getX(); double y = matchPos.getY(); double z = matchPos.getZ();
            // 给玩家反馈
            Player nearestPlayer = level.getNearestPlayer(x, y, z, 10, false);
            if (nearestPlayer != null) {
                nearestPlayer.displayClientMessage(
                        Component.literal("---!---：" + reason).withStyle(ChatFormatting.RED), true
                );
            }
        }

        @Override
        public void onLogicTick(Level level, BlockPos matchPos, long activeTime) {
            if (level.isClientSide) return;

            BlockPos altar1Pos = getAltarNorth(matchPos); BlockPos altar2Pos = getAltarSouth(matchPos);
            BlockPos altar3Pos = getAltarWest(matchPos); BlockPos altar4Pos = getAltarEast(matchPos);
            BlockPos[] altarPos = {altar1Pos, altar2Pos, altar3Pos, altar4Pos};

            if (!(level.getBlockEntity(altar1Pos) instanceof KunAltarBlockEntity altar1)) return;
            if (!(level.getBlockEntity(altar2Pos) instanceof KunAltarBlockEntity altar2)) return;
            if (!(level.getBlockEntity(altar3Pos) instanceof KunAltarBlockEntity altar3)) return;
            if (!(level.getBlockEntity(altar4Pos) instanceof KunAltarBlockEntity altar4)) return;

            ItemStack stack1 = altar1.getStoredItem();ItemStack stack2 = altar2.getStoredItem();
            ItemStack stack3 = altar3.getStoredItem();ItemStack stack4 = altar4.getStoredItem();

            if (stack1.isEmpty() || stack2.isEmpty() || stack3.isEmpty() || stack4.isEmpty()) return;

            RecipeManager recipeManager = Objects.requireNonNull(level.getServer()).getRecipeManager();
            List<TrainerAltarRecipe> recipes = recipeManager.getAllRecipesFor(ModRecipes.TRAINER_ALTAR_TYPE.get())
                    .stream()
                    .toList();

            // 遍历配方
            for (TrainerAltarRecipe recipe : recipes) {
                if (recipe.matches(stack1, stack2, stack3, stack4)) {

                    ActiveStructureManager manager = ActiveStructureManager.get(level);
                    BlockPos dropPos = manager.getDropPos(level, matchPos);

                    // 1.掉落位置判断：位置为null或被其他方块占用，回退默认
                    if (dropPos == null) dropPos = matchPos.above(2);
                    BlockState dropState = level.getBlockState(dropPos);
                    boolean isAir = dropState.isAir();
                    boolean isAbsorb = dropState.getBlock() instanceof AbsorbBlock;
                    if (!isAir && !isAbsorb) {
                        dropPos = matchPos.above(2);
                        dropState = level.getBlockState(dropPos);
                        isAir = dropState.isAir();
                        isAbsorb = dropState.getBlock() instanceof AbsorbBlock;
                    }

                    // 2. 允许合成判断（根据工作模式）：若无法合成，直接返回，不清除祭坛物品
                    if (!canCraft(level, dropPos, isAbsorb, isAir)) return;
                    ItemStack resultItem = recipe.getResultItem().copy();

                    // 3. 操作
                    doCraft(level, matchPos, dropPos, altarPos, resultItem, isAbsorb, altar1, altar2, altar3, altar4);
                    break;
                }
            }
        }

        private void clearStoredItems(KunAltarBlockEntity... entities) {
            for (KunAltarBlockEntity entity : entities) {
                entity.clearStoredItem();
            }
        }

        private boolean canCraft(Level level, BlockPos dropPos, boolean isAbsorb, boolean isAir) {
            // 空气位置：总是允许
            if (isAbsorb) {
                BlockEntity be = level.getBlockEntity(dropPos);
                if (be instanceof AbsorbBlockEntity absorb) {
                    AbsorbWorkModel model = absorb.getModel();
                    ItemStack stored = absorb.getStoredItem();
                    if (model == AbsorbWorkModel.HINDERING) {
                        // HINDERING: 仅当存储为空时才允许合成
                        return stored.isEmpty();
                    } else return model == AbsorbWorkModel.DROPPING; // DROPPING: 总是允许合成
                }
            } else return isAir;
            return false;
        }

        private void doCraft(Level level, BlockPos matchPos, BlockPos dropPos, BlockPos[] altarPos,
                            ItemStack resultItem, boolean isAbsorb,
                             KunAltarBlockEntity... entities) {
            if (isAbsorb) {
                BlockEntity be = level.getBlockEntity(dropPos);
                if (be instanceof AbsorbBlockEntity absorb) {
                    AbsorbWorkModel model = absorb.getModel();
                    ItemStack stored = absorb.getStoredItem();
                    if (model == AbsorbWorkModel.HINDERING) {
                        absorb.setStoredItem(resultItem);
                    } else if (model == AbsorbWorkModel.DROPPING) {
                        if (!stored.isEmpty()) {
                            if (ItemStack.isSameItemSameTags(stored, resultItem)) {
                                int newCount = stored.getCount() + resultItem.getCount();
                                if (newCount < 0) newCount = Integer.MAX_VALUE;
                                ItemStack combined = stored.copy();
                                combined.setCount(newCount);
                                absorb.setStoredItem(combined);
                            } else {
                                absorb.dropStoredItem();
                                absorb.setStoredItem(resultItem);
                            }
                        } else {
                            absorb.setStoredItem(resultItem);
                        }
                    }
                    int[] random = {Tools.randomInRange(level, 1, 2), Tools.randomInRange(level, 1, 2),
                            Tools.randomInRange(level, 1, 2), Tools.randomInRange(level, 1, 2)};
                    for (int i = 0; i <= 3; i++) {
                        Tools.Particle.spawnArcParticle(level,
                                random[i] == 1 ? ParticleTypes.FLAME : ParticleTypes.SOUL_FIRE_FLAME,
                                Tools.BlockCourse.getCenter(altarPos[i]),
                                Tools.BlockCourse.getCenter(dropPos)
                        );
                    }
                }
            } else { // isAir
                Tools.EntityWay.spawnItemEntity(level, dropPos, resultItem);
                Vec3 center = Tools.BlockCourse.getCenter(matchPos);
                double cx = center.x; double cy = center.y; double cz = center.z;
                Tools.Particle.send(level, ParticleTypes.FLAME, cx, cy, cz, 25, 0.5, 0.5, 0.5, 0.1);
            }
            clearStoredItems(entities);
            level.playSound(null, matchPos, SoundEvents.PLAYER_LEVELUP, SoundSource.BLOCKS, 0.6F, 1.0F);
        }


        @Override
        public void onEffectTick(Level level, BlockPos matchPos, long activeTime) {
            if (level.isClientSide) return;
            Vec3 pos = Tools.BlockCourse.getCenter(matchPos);
            double x = pos.x; double y = pos.y; double z = pos.z;
            Tools.Particle.send(
                    level, ParticleTypes.SOUL_FIRE_FLAME, x, y, z, 5, 3, 1, 3, 0.01
            );
            Tools.Particle.send(
                    level, ParticleTypes.FLAME, x, y, z, 5, 3, 1, 3, 0.01
            );
        }

        @Override
        public int getLogicTickInterval() {
            return 1; // 每 tick 检测合成
        }

        @Override
        public int getEffectTickInterval() {
            return 40; // 每 40 tick 显示一次粒子
        }

        @Override
        public void onStructureBroken(Level level, BlockPos matchPos) {
            Main.LOGGER.info("Trainer Altar structure broken at {}", matchPos);

            // 播放破坏音效
            level.playSound(null, matchPos,
                    SoundEvents.BEACON_DEACTIVATE,
                    SoundSource.BLOCKS,
                    1.0F, 1.0F);

            // 通知玩家
            Player nearestPlayer = level.getNearestPlayer(matchPos.getX(), matchPos.getY(), matchPos.getZ(), 10, false);
            if (nearestPlayer != null) {
                nearestPlayer.displayClientMessage(
                        Component.literal("§k-------").withStyle(ChatFormatting.RED),
                        true
                );
            }
        }

        /**
         * 保存激活数据
         */
        private void saveActivationData(Level level, BlockPos matchPos) {
            // 可以使用持久化数据保存激活状态
            // 或者使用方块实体存储数据
            // 这里只是一个示例
            if (level.getBlockEntity(matchPos) != null) {
                // 设置方块实体的激活状态
            }
        }

        // 检查位置是否在允许范围内
        public static boolean isInRange(BlockPos matchPos, BlockPos pos) {
            int dx = pos.getX() - matchPos.getX();
            int dy = pos.getY() - matchPos.getY();
            int dz = pos.getZ() - matchPos.getZ();
            return dx >= MIN_X && dx <= MAX_X &&
                    dy >= MIN_Y && dy <= MAX_Y &&
                    dz >= MIN_Z && dz <= MAX_Z;
        }

        // 检查位置是否合法（空气或 AbsorbBlock）
        public static boolean isValidDropPos(Level level, BlockPos pos) {
            BlockState state = level.getBlockState(pos);
            return state.isAir() || state.getBlock() instanceof AbsorbBlock;
        }
    }

    /**
     * 设置指定祭坛的物品掉落位置
     * @param level 世界
     * @param matchPos 核心方块位置
     * @param newDropPos 新的掉落位置
     * @return 是否设置成功（位置必须在允许范围内且合法）
     */
    public static boolean setDropPosition(Level level, BlockPos matchPos, BlockPos newDropPos) {
        if (!TrainerAltarActivationHandler.isInRange(matchPos, newDropPos) ||
                !TrainerAltarActivationHandler.isValidDropPos(level, newDropPos)) {
            return false;
        }
        ActiveStructureManager manager = ActiveStructureManager.get(level);
        return manager.setDropPos(level, matchPos, newDropPos);
    }
}

