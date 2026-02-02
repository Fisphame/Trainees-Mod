package com.pha.trainees.item;

import com.pha.trainees.entity.GoldChickenEntity;
import com.pha.trainees.entity.KunAntiEntity;
import com.pha.trainees.entity.KunTraineesEntity;
import com.pha.trainees.registry.ModEntities;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrownEgg;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;

public class ThrownEggCourseItem {

    public static class ThrownKunEgg extends ThrownEgg {

        public ThrownKunEgg(Level level, Player player) {
            super(level, player);
        }

        @Override
        protected void onHit(HitResult HitResult) {
            super.onHit(HitResult);

            if (!this.level().isClientSide) {
                KunTraineesEntity chicken = ModEntities.KUN_TRAINEES.get().create(this.level());
                if (chicken != null) {
                    chicken.setAge(-24000); // 设置为幼年
                    chicken.moveTo(this.getX(), this.getY(), this.getZ(), this.getYRot(), 0.0F);
                    this.level().addFreshEntity(chicken);
                }

                this.level().broadcastEntityEvent(this, (byte)3);
                this.discard();
            }
        }
    }

    public static class ThrownBlackEgg extends ThrownEgg {

        public ThrownBlackEgg(Level level, Player player) {
            super(level, player);
        }

        @Override
        protected void onHit(HitResult HitResult) {
            super.onHit(HitResult);

            if (!this.level().isClientSide) {
                KunAntiEntity chicken = ModEntities.KUN_ANTI.get().create(this.level());
                if (chicken != null) {
                    chicken.setAge(-24000); // 设置为幼年
                    chicken.moveTo(this.getX(), this.getY(), this.getZ(),
                            this.getYRot(), 0.0F);
                    this.level().addFreshEntity(chicken);
                }

                this.level().broadcastEntityEvent(this, (byte)3);
                this.discard();
            }
        }
    }

    public static class ThrownGoldEgg extends ThrownEgg {

        public ThrownGoldEgg(Level level, Player player) {
            super(level, player);
        }

        @Override
        protected void onHit(HitResult HitResult) {
            super.onHit(HitResult);

            if (!this.level().isClientSide) {
                GoldChickenEntity chicken = ModEntities.GOLD_CHICKEN.get().create(this.level());
                if (chicken != null) {
                    chicken.setAge(-24000); // 设置为幼年
                    chicken.moveTo(this.getX(), this.getY(), this.getZ(), this.getYRot(), 0.0F);
                    this.level().addFreshEntity(chicken);
                }

                this.level().broadcastEntityEvent(this, (byte)3);
                this.discard();
            }
        }
    }
}
