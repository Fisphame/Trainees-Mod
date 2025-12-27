package com.pha.trainees.thetwice;

import com.pha.trainees.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

public class SiJian extends SwordItem {

    public SiJian(Tier p_43269_, int p_43270_, float p_43271_, Properties p_43272_) {
        super(p_43269_, p_43270_, p_43271_, p_43272_);
    }
    static boolean IsInAirAttact=false;
    static Player playe1;

    @Override
    public boolean hurtEnemy(@NotNull ItemStack stack, @NotNull LivingEntity target, @NotNull LivingEntity attacker) {

        boolean result = super.hurtEnemy(stack, target, attacker);
        Level level = attacker.level();
        if (!level.isClientSide) {
            if ((attacker instanceof Player player)) {
//                if (Textview.PlayerIsOnground(player) &&!Textview.getPlayerUnderBlockstate ( player,level,1 ).isAir ()&&!IsInAirAttact){
                    IsInAirAttact=true;
                    playe1=player;
//                }
                if (Textview.SiHittime <= 4) {
                    Textview.SiHittime++;
                }
                Textview.seedMsg(String.valueOf(Textview.SiHittime), player);
            }
        }

        return result;
    }

    public @NotNull InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos blockpos = context.getClickedPos();
        BlockState blockstate = level.getBlockState(blockpos);
        Block block = blockstate.getBlock();
        if (block == ModBlocks.MYBLOCK.get()) {
            Player player = context.getPlayer();
            assert player != null;
            player.setDeltaMovement(player.getDeltaMovement().x, 1.5, player.getDeltaMovement().z);
            player.getCooldowns().addCooldown(this, 20);
        }
        return super.useOn(context);
    }

    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, Player player, @NotNull InteractionHand p_41130_) {
        ItemStack itemstack = player.getItemInHand(p_41130_);
        Vec3 look = player.getLookAngle();
        look = new Vec3(look.x, 0, look.z).normalize();
        player.setDeltaMovement(look);
        Textview.seedMsg("C",player);
        player.awardStat(Stats.ITEM_USED.get(this));
        player.getCooldowns().addCooldown(this, 12);
        return InteractionResultHolder.sidedSuccess(itemstack, level.isClientSide());
    }

    @Override
    public boolean onDroppedByPlayer(ItemStack stack, Player player) {
        if (Textview.SiHittime == 5) {
            player.setHealth(player.getHealth() + 6);
            Textview.SiHittime = 0;
        }
        stack.shrink(1);
        Objects.requireNonNull(player.level().getServer()).execute(() -> addSijianToInventory(player));
        if (player instanceof ServerPlayer serverPlayer) {
            serverPlayer.inventoryMenu.broadcastChanges();
            serverPlayer.containerMenu.broadcastFullState();
        }
        return false;
    }

    public void inventoryTick(@NotNull ItemStack p_41404_, @NotNull Level p_41405_, @NotNull Entity p_41406_, int p_41407_, boolean p_41408_) {
        if (IsInAirAttact){
            Player player = this.playe1;
            player.setDeltaMovement(player.getDeltaMovement().x, 1.5, player.getDeltaMovement().z);
            IsInAirAttact=true;
        }
    }

    private void addSijianToInventory(Player player) {
        Item sijianItem = Object.SIJIAN.get();
        ItemStack sijianStack = new ItemStack(sijianItem);
        Inventory inventory = player.getInventory();
        int originalSlot = player.getInventory().selected;
        inventory.setItem(originalSlot, sijianStack);
    }


    public static boolean hasEntityInRange(Level level, BlockPos centerPos) {
        double range = 3.0; // 半径
        double centerX = centerPos.getX() + 0.5;
        double centerY = centerPos.getY() + 0.5;
        double centerZ = centerPos.getZ() + 0.5;

        AABB searchArea = new net.minecraft.world.phys.AABB(
                centerX - range, centerY - range, centerZ - range,
                centerX + range, centerY + range, centerZ + range
        );

        List<Entity> entities = level.getEntities(null, searchArea);

        for (Entity entity : entities) {
            if (!(entity instanceof Player)) {
                return true; // 找到
            }
        }
        return false;

    }


    public static Entity rayTraceEntities(Level world, Player player, double maxDistance) {
        // 1. 计算视线起点（眼睛位置）
        Vec3 eyePosition = player.getEyePosition(0.5F);

        // 2. 计算视线方向向量
        float pitchRad = (float) Math.toRadians(player.getXRot()); // 垂直角度
        float yawRad = (float) Math.toRadians(-player.getYRot()); // 水平角度（注意取反）
        float cosPitch = Mth.cos(pitchRad);
        float sinPitch = Mth.sin(pitchRad);
        float cosYaw = Mth.cos(yawRad);
        float sinYaw = Mth.sin(yawRad);

        Vec3 lookVec = new Vec3(sinYaw * cosPitch, -sinPitch, -cosYaw * cosPitch).normalize();

        // 3. 计算视线终点
        Vec3 endPosition = eyePosition.add(lookVec.scale(maxDistance));

        // 4. 执行实体射线检测
        EntityHitResult result = ProjectileUtil.getEntityHitResult(
                world,
                player,
                eyePosition,
                endPosition,
                new AABB(eyePosition, endPosition).inflate(1.0D), // 扩大检测范围
                (entity) -> !entity.isSpectator() && entity.isPickable() // 过滤条件：非旁观者且可被选取
        );

        if (result != null) return result.getEntity();
        return null;
    }
}
