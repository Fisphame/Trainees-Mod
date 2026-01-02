package com.pha.trainees.way.chemistry;

import com.pha.trainees.registry.ModBlocks;
import com.pha.trainees.registry.ModChemistry;
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
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;

import static com.pha.trainees.way.chemistry.ReactionConditions.*;
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


    public static void registerAllReactions() {
        ReactionSystem.ReactionRegistry.clearAllReactions();

        // ==================== HBpO 光照分解反应 ====================
        // 2HBpO =光照= 2HBp + O2↑
//        ModChemistry.ModChemistryEquations.HBPO_DECOMPOSITION.register(10);

        ReactionBuilder.create("hbpo_decompose")
                .withTimedCondition(
                        hbpoDecomposeCondition.get(), hbpoDurationProvider.get()
                )
                .withResult((level, entity, stack) -> {
                    if (stack.getCount() < 2) return;
                    int reactions = stack.getCount() / 2;
                    int remainder = stack.getCount() % 2;
                    int producedHBp = reactions * 2;

                    entity.discard();

                    if (producedHBp > 0) {
                        NumedItemEntities itemEntities = AddEntity(level, entity,
                                new ItemStack(ModChemistry.ModChemistryItems.CHE_HBP.get(), producedHBp), true);
                        AddNIEs(null, itemEntities, false);
                    }
                    if (remainder > 0) {
                        ItemStack remainStack = stack.copy();
                        remainStack.setCount(remainder);
                        ItemEntity remainderEntity = new ItemEntity(
                                level, entity.getX(), entity.getY(), entity.getZ(), remainStack
                        );
                        level.addFreshEntity(remainderEntity);
                    }
                })
                .withPriority(10)
                .register();

        // ==================== 黑单质（黑 Bp2）与水的反应 ====================
        // Bp2 + H2O == HBp + HBpO
//        ModChemistry.ModChemistryEquations.BP2_WATER_REACTION.register(5);

        ReactionBuilder.create("bp2_water_reaction")
                .withTimedCondition(
                        bp2AndWaterCondition.get(), random5to10
                )
                .withResult((level, entity, stack) -> {
                    int simpleSubstance = 2;
                    int multiplier = Tools.getPowderMultiplier(stack.getItem());
                    int count = stack.getCount() * multiplier * simpleSubstance / 2;

                    if (count > 0) {
                        entity.discard();
                        NumedItemEntities itemEntities_1 = AddEntity(level, entity,
                                new ItemStack(ModChemistry.ModChemistryItems.CHE_HBP.get(), count), true);
                        NumedItemEntities itemEntities_2 = AddEntity(level, entity,
                                new ItemStack(ModChemistry.ModChemistryItems.CHE_HBPO.get(), count), true);
                        AddNIEs(entity, itemEntities_1, false);
                        AddNIEs(entity, itemEntities_2, false);
                    }
                })
                .withPriority(5)
                .register();

        // ==================== Ji与水的反应 ====================
        // 2Ji + 2H2O == 2JiOH + H2↑
        ReactionBuilder.create("ji_water_reaction")
                .withCondition(
                        and(IS_JI.get(), or(IN_RAIN, IN_WATER))
                )
                .withResult((level, entity, stack) -> {
                    double x = entity.getX(), y = entity.getY(), z = entity.getZ();
                    Tools.DoTnt_center(level, x, y, z);

                    entity.discard();
                    Item item = Tools.getWhichJiOH(stack);
                    int count = stack.getCount();

                    NumedItemEntities itemEntities = AddEntity(level, entity, new ItemStack(item, count) , true);
                    AddNIEs(entity, itemEntities, true);
                })
                .withPriority(10)
                .register();
    }
}
