package com.pha.trainees.multiblock;

import com.pha.trainees.Main;
import com.pha.trainees.block.KunAltarBlock;
import com.pha.trainees.blockentity.KunAltarBlockEntity;
import com.pha.trainees.registry.ModBlocks;
import com.pha.trainees.util.game.ItemPair4;
import com.pha.trainees.util.game.ItemPair4Manager;
import com.pha.trainees.util.game.KunAltarType;
import com.pha.trainees.util.game.Tools;
import com.pha.trainees.util.game.structure.*;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/**
 * Trainer Altar 多方块结构
 */
public class TrainerAltarPattern {
    public static final String STRUCTURE_ID = "trainees:trainer_altar";

    public static ActiveStructureManager manager;
    public static final ItemPair4Manager pair4Manager = new ItemPair4Manager();
    // 1 - north  2 - east  3 - south  4 - west
    public static KunAltarBlockEntity altarBlockEntity1;
    public static KunAltarBlockEntity altarBlockEntity2;
    public static KunAltarBlockEntity altarBlockEntity3;
    public static KunAltarBlockEntity altarBlockEntity4;


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
    private static class TrainerAltarActivationHandler implements IActivationHandler {

        @Override
        public boolean preActivateCheck(Level level, BlockPos matchPos) {
            // 这里可以添加额外的激活前检查
            // 例如：检查玩家是否持有正确物品、检查冷却时间等
            return true;
        }

        @Override
        public void onActivate(Level level, BlockPos matchPos) {
            Main.LOGGER.info("Trainer Altar activated at {}", matchPos);
            double x = matchPos.getX(); double y = matchPos.getY(); double z = matchPos.getZ();
            Vec3 center = Tools.BlockCourse.getCenter(matchPos);
            double cx = center.x; double cy = center.y; double cz = center.z;

            // 记录激活状态
            manager = ActiveStructureManager.get(level);
            manager.addActiveStructure(level, STRUCTURE_ID, matchPos);

            if (level.isClientSide) return;

            level.playSound(null, matchPos, SoundEvents.BEACON_ACTIVATE, SoundSource.BLOCKS, 1.0F, 1.0F);
            Tools.Particle.send(level, ParticleTypes.SOUL_FIRE_FLAME, cx, cy, cz, 200, 0.5, 0, 0.5, 0.15);
            Tools.Particle.send(level, ParticleTypes.FLAME, cx, cy, cz, 200, 0.5, 0, 0.5, 0.15);



            saveActivationData(level, matchPos);
            setAltarBlockEntities(level, matchPos);
            TrainerAltarRecipes.registerRecipes(pair4Manager);
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
        public void onActiveTick(Level level, BlockPos matchPos, long activeTime) {
            double x = matchPos.getX(); double y = matchPos.getY(); double z = matchPos.getZ();
            Vec3 center = Tools.BlockCourse.getCenter(matchPos);
            double cx = center.x; double cy = center.y; double cz = center.z;
            Tools.Particle.send(
                    level, ParticleTypes.SOUL_FIRE_FLAME, cx, cy, cz, 5, 3, 1, 3, 0.01
            );
            Tools.Particle.send(
                    level, ParticleTypes.FLAME, cx, cy, cz, 5, 3, 1, 3, 0.01
            );
            ItemPair4 nowPair = getStoredItem();

//            Main.LOGGER.info(nowPair.toString());
//            ItemPair4 pair = pair4Manager.findMatchingPair(nowPair);
//            if (pair != null) {
//                ItemEntity itemEntity = new ItemEntity(level, cx, y + 2.0, cz, pair.getResultItem());
//                Tools.EntityWay.spawnItemEntity(level, itemEntity);
//                clearStoredItem();
//            }
//            else {
//                Main.LOGGER.info("fuck pair is null fky");
//            }

        }

        @Override
        public int getParticleTickInterval() {
            return 40;
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
    }

//    /**
//     * 实用方法：从激活位置获取所有结构方块位置
//     */
//    public static void forEachStructureBlock(Level level, BlockPos corePos,
//                                             BiConsumer<BlockPos, BlockState> consumer) {
//        // 计算结构原点
//        BlockPos originPos = corePos.offset(-2, -1, -2);
//
//        // 遍历所有结构位置
//        BlockPos[] positions = {
//                // 基础方块
//                originPos.offset(2, 0, 2),
//                originPos.offset(2, 2, 2),
//                // 核心方块
//                originPos.offset(2, 1, 2),
//                // KunAltar 方块
//                originPos.offset(0, 0, 2),
//                originPos.offset(4, 0, 2),
//                originPos.offset(2, 0, 0),
//                originPos.offset(2, 0, 4)
//        };
//
//        for (BlockPos pos : positions) {
//            BlockState state = level.getBlockState(pos);
//            consumer.accept(pos, state);
//        }
//    }

    public static void setAltarBlockEntities(Level level, BlockPos corePos) {
        if (level.getBlockEntity(corePos.offset(0, -1, -3)) instanceof KunAltarBlockEntity blockEntity){
            altarBlockEntity1 = blockEntity;
        }
        if (level.getBlockEntity(corePos.offset(3, -1, 0)) instanceof KunAltarBlockEntity blockEntity){
            altarBlockEntity2 = blockEntity;
        }
        if (level.getBlockEntity(corePos.offset(0, -1, 3)) instanceof KunAltarBlockEntity blockEntity){
            altarBlockEntity3 = blockEntity;
        }
        if (level.getBlockEntity(corePos.offset(-3, -1, 0)) instanceof KunAltarBlockEntity blockEntity){
            altarBlockEntity4 = blockEntity;
        }
    }

    public static ItemPair4 getStoredItem() {
        return new ItemPair4(
                altarBlockEntity1.getStoredItem().getItem(),
                altarBlockEntity2.getStoredItem().getItem(),
                altarBlockEntity3.getStoredItem().getItem(),
                altarBlockEntity4.getStoredItem().getItem(),
                null
        );
    }

    public void setStoredItem(ItemPair4 itemPair4) {
        altarBlockEntity1.setStoredItem(new ItemStack(itemPair4.getItem1(), 1));
        altarBlockEntity2.setStoredItem(new ItemStack(itemPair4.getItem2(), 1));
        altarBlockEntity3.setStoredItem(new ItemStack(itemPair4.getItem3(), 1));
        altarBlockEntity4.setStoredItem(new ItemStack(itemPair4.getItem4(), 1));
    }

    public static void clearStoredItem() {
        altarBlockEntity1.clearStoredItem();
        altarBlockEntity2.clearStoredItem();
        altarBlockEntity3.clearStoredItem();
        altarBlockEntity4.clearStoredItem();
    }

    public boolean hasStoredItem() {
        return altarBlockEntity1.hasStoredItem() && altarBlockEntity2.hasStoredItem()
                && altarBlockEntity3.hasStoredItem() && altarBlockEntity4.hasStoredItem();
    }

    public void dropStoredItem(Level level) {
        if (level == null || level.isClientSide()) return;
        altarBlockEntity1.dropStoredItem();
        altarBlockEntity2.dropStoredItem();
        altarBlockEntity3.dropStoredItem();
        altarBlockEntity4.dropStoredItem();
    }
}

