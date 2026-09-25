package com.pha.trainees.chemistry.blockentity;

import com.pha.trainees.chemistry.heat.IHeatSource;
import com.pha.trainees.registry.ModChemistry;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/**
 * 恒温浴（§19.24 热源分级 ③-4）：真正的"设定点型"热源设备。
 *
 * <p>与创造热源的区别是**介质决定可达区间**——这正是现实实验室的做法（§19.24 现实依据表）：
 * 水浴被水的沸腾钉在 ≤100 ℃、油浴（硅油）≤250 ℃、沙浴可到 ≈600 ℃、冰盐冷冻混合物 −20~0 ℃。
 * 玩家不能"把火烧小一点"来控温，只能换介质 + 设设定点。</p>
 *
 * <p>它让中温精细工艺第一次变得可操作：含氯消毒液的 60~80 ℃ 窗口、结晶/重结晶等。</p>
 */
public class ThermostaticBathBlockEntity extends BlockEntity implements IHeatSource {

    /** 介质：决定设定点的可达区间（现实依据见类注释）。 */
    public enum Medium {
        /** 冰 + 食盐冷冻混合物（现实 −10 ~ −23 ℃） */
        ICE_SALT("ice_salt", 250.0, 273.15),
        /** 水浴（受沸腾限制，天然 ≤100 ℃） */
        WATER("water", 273.15, 373.15),
        /** 油浴（硅油，现实 ≤250 ℃） */
        OIL("oil", 293.15, 523.15),
        /** 沙浴（现实可到 ≈600 ℃） */
        SAND("sand", 293.15, 873.15);

        private final String id;
        private final double minSetpointK;
        private final double maxSetpointK;

        Medium(String id, double minSetpointK, double maxSetpointK) {
            this.id = id;
            this.minSetpointK = minSetpointK;
            this.maxSetpointK = maxSetpointK;
        }

        public String id() {
            return id;
        }

        public double minSetpointK() {
            return minSetpointK;
        }

        public double maxSetpointK() {
            return maxSetpointK;
        }

        public double clamp(double kelvin) {
            return Mth.clamp(kelvin, minSetpointK, maxSetpointK);
        }
    }

    /** 设定点调整步长（K）：右键升、潜行右键降 */
    public static final double STEP_K = 5.0;

    private Medium medium = Medium.WATER;
    private double setpoint = 333.15;

    public ThermostaticBathBlockEntity(BlockPos pos, BlockState state) {
        super(ModChemistry.ModChemistryBlockEntities.THERMOSTATIC_BATH.get(), pos, state);
    }

    // ==================== IHeatSource（设定点语义：双向换热） ====================

    @Override
    public double getSourceTemperature() {
        return setpoint;
    }

    @Override
    public boolean isSettable() {
        return true;
    }

    @Override
    public boolean setSourceTemperature(double kelvin) {
        setSetpoint(kelvin);
        return true;
    }

    // ==================== 状态 ====================

    public Medium getMedium() {
        return medium;
    }

    public double getSetpoint() {
        return setpoint;
    }

    /** 设定设定点（按当前介质区间夹取），返回实际生效值。 */
    public double setSetpoint(double kelvin) {
        double clamped = medium.clamp(kelvin);
        if (Math.abs(clamped - setpoint) > 1e-6) {
            setpoint = clamped;
            markDirtyAndSync();
        }
        return setpoint;
    }

    /** 按步长调整设定点。 */
    public double adjustSetpoint(double deltaK) {
        return setSetpoint(setpoint + deltaK);
    }

    /**
     * 更换介质：设定点会被夹进新介质的区间（例如从沙浴 600 ℃ 换回水浴会掉到 100 ℃）。
     */
    public void setMedium(Medium newMedium) {
        if (newMedium == null || newMedium == medium) return;
        medium = newMedium;
        setpoint = medium.clamp(setpoint);
        markDirtyAndSync();
    }

    private void markDirtyAndSync() {
        setChanged();
        if (level != null && !level.isClientSide) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    // ==================== 持久化 ====================

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putString("Medium", medium.id());
        tag.putDouble("Setpoint", setpoint);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains("Medium")) {
            for (Medium candidate : Medium.values()) {
                if (candidate.id().equals(tag.getString("Medium"))) {
                    medium = candidate;
                    break;
                }
            }
        }
        if (tag.contains("Setpoint")) {
            setpoint = medium.clamp(tag.getDouble("Setpoint"));
        }
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        tag.putString("Medium", medium.id());
        tag.putDouble("Setpoint", setpoint);
        return tag;
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}
