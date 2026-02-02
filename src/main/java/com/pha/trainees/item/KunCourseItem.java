package com.pha.trainees.item;

import com.pha.trainees.registry.ModBlocks;
import com.pha.trainees.util.game.chemistry.ReactionSystem;
import com.pha.trainees.util.game.Tools;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

public class KunCourseItem {

    public static boolean on(ItemStack stack, @NotNull ItemEntity entity) {
        if (!entity.level().isClientSide) {
            return ReactionSystem.ReactionRegistry.triggerReactions(stack, entity);
        }
        return false;
    }

    public static class KunNuggetItem extends Item {

        public KunNuggetItem(Properties properties) {
            super(properties);
        }

        @Override
        public boolean onEntityItemUpdate(ItemStack stack, ItemEntity entity) {
            return on(stack, entity);
        }

    }

    public static class TwoHalfIngotItem extends Item {

        public TwoHalfIngotItem(Properties properties) { super(properties); }

        @Override
        public boolean onEntityItemUpdate(ItemStack stack, ItemEntity entity) {
            return on(stack, entity);
        }
    }

    public static class TwoHalfIngotBlockItem extends BlockItem {

        public TwoHalfIngotBlockItem(Block p_40565_, Properties p_40566_) {
            super(p_40565_, p_40566_);
        }

        @Override
        public boolean onEntityItemUpdate(ItemStack stack, ItemEntity entity) {
            return on(stack, entity);
        }
    }

    public static class KunPickaxeFinal extends PickaxeItem {
        public KunPickaxeFinal(Tier p_42961_, int p_42962_, float p_42963_, Properties p_42964_) {
            super(p_42961_, p_42962_, p_42963_, p_42964_);
        }

        @Override
        public boolean isCorrectToolForDrops(@NotNull ItemStack stack, @NotNull BlockState state) {
            if (state.is(ModBlocks.MYBLOCK.get())) {
                return true;
            }

            return state.is(BlockTags.MINEABLE_WITH_PICKAXE);
        }

        @Override
        public float getDestroySpeed(ItemStack stack, BlockState state) {
            return super.getDestroySpeed(stack, state) * 4.0f;
        }

        @Override
        public boolean mineBlock(ItemStack stack, @NotNull Level level, BlockState state,
                                 BlockPos pos, LivingEntity miningEntity) {
            boolean result = super.mineBlock(stack, level, state, pos, miningEntity);

            if (!level.isClientSide()) {
                SoundEvent sound = Tools.getIndexSound(Tools.FINAL_MINING_SOUND, level);
                level.playSound(null, pos,
                        sound, SoundSource.BLOCKS,
                        0.8F,
                        1.0F + (level.getRandom().nextFloat() - 0.5F) * 0.2F
                );
            }

            return result;
        }

        @Override
        public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag flag) {
            super.appendHoverText(stack, level, tooltipComponents, flag);

            if (flag.isAdvanced()) {
                tooltipComponents.add(Component.translatable("tooltip.trainees.kun_pickaxe_final_item"));
                tooltipComponents.add(Component.translatable("tooltip.trainees.kun_pickaxe_final_item.2"));
            } else {
                tooltipComponents.add(Component.translatable("tooltip.trainees.item.press_shift"));
            }

        }
    }

    public static class KunSwordItem extends SwordItem{

        public KunSwordItem(Tier p_43269_, int p_43270_, float p_43271_, Properties p_43272_) {
            super(p_43269_, p_43270_, p_43271_, p_43272_);
        }
    }

    public static class KunDaggerItem extends KunSwordItem{

        public KunDaggerItem(Tier p_43269_, int p_43270_, float p_43271_, Properties p_43272_) {
            super(p_43269_, p_43270_, p_43271_, p_43272_);
        }

        public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand interaction) {
            Entity nearestEntity = Tools.EntityWay.getNearestEntityInFront(player, 2.0, 15.0f);
            Tools.Particle.visualizeSector(level, player, 2., 30.0f, 30);
            if (nearestEntity != null) {
                if (nearestEntity instanceof LivingEntity living) {
                    if (Tools.EntityWay.getYawDiffer(living, player) <= 30.0f){
                        living.hurt(player.damageSources().playerAttack(player), 20.0f);
                    }
                }
            }
            ItemStack stack = player.getItemInHand(interaction);
            player.getCooldowns().addCooldown(this, 15);
            if (!player.isCreative()) {
                stack.hurtAndBreak(1, player, (e) -> e.broadcastBreakEvent(EquipmentSlot.MAINHAND));
            }
            return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
        }
    }
}
