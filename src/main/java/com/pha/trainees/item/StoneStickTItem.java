package com.pha.trainees.item;

import com.pha.trainees.Main;
import com.pha.trainees.block.AbsorbBlock;
import com.pha.trainees.multiblock.TrainerAltarPattern;
import com.pha.trainees.registry.ModBlocks;
import com.pha.trainees.util.game.Tools;
import com.pha.trainees.util.game.structure.ActiveStructureManager;
import com.pha.trainees.util.interfaces.HoverText;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

public class StoneStickTItem extends Item implements HoverText {

    public StoneStickTItem(Properties p_41383_) {
        super(p_41383_);
    }

    public @NotNull InteractionResult useOn(UseOnContext context){
        Level level = context.getLevel();
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }
        Player player = context.getPlayer();
        if (player == null) return InteractionResult.PASS;
        BlockPos clickedPos = context.getClickedPos();
        ItemStack itemStack = context.getItemInHand();

        Vector3f color = new Vector3f(0, 255, 0);
        DustParticleOptions options = new DustParticleOptions(color, 1.0f);
        Tools.Particle.sendSurfaces(level, options, clickedPos, 15, 0.25, 0.25, 0.25, 0.0);

        // 获取玩家持久数据中用于配置的标签
        String configKey = Main.MODID + "_config";
        CompoundTag persistentData = player.getPersistentData();
        CompoundTag configData = persistentData.getCompound(configKey);

        boolean settingMode = configData.getBoolean("SettingMode");
        long settingAltarPosLong = configData.getLong("SettingAltarPos");

        if (settingMode) {
            // 玩家处于设置模式，处理掉落位置的选择
            BlockPos altarPos = BlockPos.of(settingAltarPosLong);
            ActiveStructureManager manager = ActiveStructureManager.get(level);

            // 验证祭坛是否仍然激活
            if (manager.isPositionActive(level, TrainerAltarPattern.STRUCTURE_ID, altarPos)) {
                // 确定候选位置
                BlockPos candidate = null;
                BlockState state = level.getBlockState(clickedPos);
                if (state.getBlock() instanceof AbsorbBlock) {
                    candidate = clickedPos;
                } else {
                    BlockPos above = clickedPos.above();
                    if (level.isEmptyBlock(above)) {
                        candidate = above;
                    }
                }

                if (candidate != null) {
                    // 尝试设置掉落位置
                    boolean success = TrainerAltarPattern.setDropPosition(level, altarPos, candidate);
                    if (success) {
                        // 设置成功，清除设置模式
                        configData.remove("SettingMode");
                        configData.remove("SettingAltarPos");
                        persistentData.put(configKey, configData);
                        player.displayClientMessage(SuccessC,true);
                    } else {
                        player.displayClientMessage(tr("hint.invalid_drop_pos_out_of_range_or_block_unavailable")
                                .withStyle(ChatFormatting.RED),true);

//                                Component.literal("无效的掉落位置（超出范围或方块不可用）");
                    }
                } else {
                    player.displayClientMessage(tr("hint.target_block_must_absorb_block_or_air_above")
                            .withStyle(ChatFormatting.RED), true);


//                            Component.literal("目标方块必须是吸收块或上方为空气"), true);
                }
            } else {
                // 祭坛不再激活，自动退出配置模式
                configData.remove("SettingMode");
                configData.remove("SettingAltarPos");
                persistentData.put(configKey, configData);
                player.displayClientMessage(tr("hint.altar_invalid_setting_canceled").withStyle(ChatFormatting.RED), true);

//                        Component.literal("祭坛已失效，配置取消"), true);
            }
            return InteractionResult.SUCCESS;
        } else {
            // 未处于设置模式，检查点击的是否为核心方块且祭坛已激活
            if (level.getBlockState(clickedPos).is(ModBlocks.ALTAR_CORE_BLOCK.get())) {
                ActiveStructureManager manager = ActiveStructureManager.get(level);
                if (manager.isPositionActive(level, TrainerAltarPattern.STRUCTURE_ID, clickedPos)) {
                    // 进入设置模式
                    configData.putBoolean("SettingMode", true);
                    configData.putLong("SettingAltarPos", clickedPos.asLong());
                    persistentData.put(configKey, configData);
                    player.getCooldowns().addCooldown(itemStack.getItem(), 5);
                    player.displayClientMessage(tr("hint.enter_drop_pos_setting_right_click_block_set_pos"), true);

//                            Component.literal("进入掉落位置配置模式，右键方块设置位置"), true);
                    return InteractionResult.SUCCESS;
                }
            }
            return InteractionResult.PASS;
        }
    }
}
