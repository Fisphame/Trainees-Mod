package com.pha.trainees.entity;

import com.pha.trainees.Main;
import com.pha.trainees.registry.ModEntities;
import com.pha.trainees.registry.ModItems;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.damagesource.DamageSource;



public class KunAntiEntity extends Chicken {
    // 定义新的食物列表
    private static final Ingredient FOOD_ITEMS = Ingredient.of(ModItems.POWDER_ANTI.get());

    public KunAntiEntity(EntityType<? extends Chicken> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Chicken.createAttributes()
                .add(Attributes.MAX_HEALTH, 18.0)
                .add(Attributes.MOVEMENT_SPEED, 0.25);
    }

    @Override
    protected ResourceLocation getDefaultLootTable() {
        return new ResourceLocation(Main.MODID, "entities/kun_anti");
    }

    // 重写aiStep方法以修改下蛋行为
    @Override
    public void aiStep() {
        super.aiStep();

        // 复制父类逻辑但修改下蛋部分
        if (!this.level().isClientSide && this.isAlive() && !this.isBaby() && !this.isChickenJockey() && --this.eggTime <= 0) {
            this.playSound(SoundEvents.CHICKEN_EGG, 1.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
            //@ 黑蛋
            this.spawnAtLocation(ModItems.BLACK_EGG.get());
            this.gameEvent(GameEvent.ENTITY_PLACE);
            this.eggTime = this.random.nextInt(6000) + 6000;
        }
    }

    // 重写食物检测方法
    @Override
    public boolean isFood(ItemStack stack) {
        return FOOD_ITEMS.test(stack);
    }

    // 重写环境音效方法（需要替换为自定义音效）
    @Override
    protected SoundEvent getAmbientSound() {
        // 返回自定义环境音效，需要先创建
        // return ModSounds.KUN_ANTI_AMBIENT.get();
        return SoundEvents.CHICKEN_AMBIENT;
    }

    // 重写受伤音效方法（需要替换为自定义音效）
    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        // 返回自定义受伤音效，需要先创建
        // return ModSounds.KUN_ANTI_HURT.get();
        return SoundEvents.CHICKEN_HURT;
    }

    // 重写死亡音效方法（需要替换为自定义音效）
    @Override
    protected SoundEvent getDeathSound() {
        // 返回自定义死亡音效，需要先创建
        // return ModSounds.KUN_ANTI_DEATH.get();
        return SoundEvents.CHICKEN_DEATH;
    }

    // 重写踏步音效方法（需要替换为自定义音效）
    @Override
    protected void playStepSound(BlockPos pos, BlockState blockState) {
        // 播放自定义踏步音效，需要先创建
        // this.playSound(ModSounds.KUN_ANTI_STEP.get(), 0.15F, 1.0F);
        this.playSound(SoundEvents.CHICKEN_STEP, 0.15F, 1.0F);
    }

    // 重写繁殖方法，生成同类型实体
    @Override
    public KunAntiEntity getBreedOffspring(ServerLevel level, AgeableMob otherParent) {
        // 假设你已经注册了KUN_ANTI实体类型
        return ModEntities.KUN_ANTI.get().create(level);
    }

    // 重写经验值获取方法
    @Override
    public int getExperienceReward() {
        return 25;
    }
}