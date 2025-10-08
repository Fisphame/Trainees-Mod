package com.pha.trainees.item;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class BombItem extends Item {
    public BombItem(Properties p_41383_) {
        super(p_41383_);
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity entity, InteractionHand hand) {
        Level level = player.level();
        //检测
        if (level.isClientSide()) return InteractionResult.FAIL;

        BlockPos blockPos = entity.getOnPos();
        int x = blockPos.getX(), y = blockPos.getY(), z = blockPos.getZ() ;
        if(entity instanceof Sheep){
            DoTnt(level, x, y, z, 16.0F, 6, 1);
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }

    public static void DoTnt(Level level, int x, int y, int z, float power, int surface, int distance){
        int O = 0;
        level.playSound(null, x, y, z, SoundEvents.TNT_PRIMED, SoundSource.BLOCKS, 1.0F, 1.0F);

        PrimedTnt[] tnts = new PrimedTnt[]{
                new PrimedTnt(level, x, y+distance, z, null) {
                    @Override
                    protected void explode() {
                        level.explode(this, this.getX(), this.getY(0.0625D), this.getZ(),
                                power, Level.ExplosionInteraction.TNT);
                    }
                },
                new PrimedTnt(level, x, y-distance, z, null) {
                    @Override
                    protected void explode() {
                        level.explode(this, this.getX(), this.getY(0.0625D), this.getZ(),
                                power, Level.ExplosionInteraction.TNT);
                    }
                },
                new PrimedTnt(level, x+distance, y, z, null) {
                    @Override
                    protected void explode() {
                        level.explode(this, this.getX(), this.getY(0.0625D), this.getZ(),
                                power, Level.ExplosionInteraction.TNT);
                    }
                },
                new PrimedTnt(level, x-distance, y, z, null) {
                    @Override
                    protected void explode() {
                        level.explode(this, this.getX(), this.getY(0.0625D), this.getZ(),
                                power, Level.ExplosionInteraction.TNT);
                    }
                },
                new PrimedTnt(level, x, y, z+distance, null) {
                    @Override
                    protected void explode() {
                        level.explode(this, this.getX(), this.getY(0.0625D), this.getZ(),
                                power, Level.ExplosionInteraction.TNT);
                    }
                },
                new PrimedTnt(level, x, y, z-distance, null) {
                    @Override
                    protected void explode() {
                        level.explode(this, this.getX(), this.getY(0.0625D), this.getZ(),
                                power, Level.ExplosionInteraction.TNT);
                    }
                },
        };


        //欸嘿^v^
        for (int i = O; !(i >= surface) ; i -= -1) {
            tnts[i].setFuse(O);
            level.addFreshEntity(tnts[i]);
        }
    }
}
