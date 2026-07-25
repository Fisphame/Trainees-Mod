package com.pha.trainees.chemistry.blockentity;

import com.pha.trainees.Main;
import com.pha.trainees.chemistry.container.IChemicalContainer;
import com.pha.trainees.chemistry.particle.IonType;
import com.pha.trainees.config.ChemConfig;
import com.pha.trainees.registry.ModChemistry;
import com.pha.trainees.registry.ModChemistry.ModIons;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class BeakerBlockEntity extends BlockEntity implements IChemicalContainer {

    private static final double DEFAULT_TEMPERATURE = ChemConfig.DEFAULT_TEMPERATURE.get();
    private static final double DEFAULT_VOLUME = ChemConfig.DEFAULT_VOLUME.get();
    private static final double MAX_SAFE_TEMPERATURE = ChemConfig.MAX_SAFE_TEMPERATURE.get();
    private static final double HEAT_TRANSFER_COEFFICIENT = ChemConfig.HEAT_TRANSFER_COEFFICIENT.get();

    private static final Map<Block, Double> HEAT_SOURCE_TEMPS = new HashMap<>();
    static {
        HEAT_SOURCE_TEMPS.put(Blocks.FIRE, ChemConfig.HEAT_SOURCE_FIRE.get());
        HEAT_SOURCE_TEMPS.put(Blocks.SOUL_FIRE, ChemConfig.HEAT_SOURCE_SOUL_FIRE.get());
        HEAT_SOURCE_TEMPS.put(Blocks.LAVA, ChemConfig.HEAT_SOURCE_LAVA.get());
//        HEAT_SOURCE_TEMPS.put(Blocks.FLOWING_LAVA, 1500.0);
        HEAT_SOURCE_TEMPS.put(Blocks.MAGMA_BLOCK, ChemConfig.HEAT_SOURCE_MAGMA.get());
        HEAT_SOURCE_TEMPS.put(Blocks.CAMPFIRE, ChemConfig.HEAT_SOURCE_CAMPFIRE.get());
        HEAT_SOURCE_TEMPS.put(Blocks.SOUL_CAMPFIRE, ChemConfig.HEAT_SOURCE_SOUL_CAMPFIRE.get());
    }

    private final Map<IonType, Double> contents = new ConcurrentHashMap<>();
    private double temperature = DEFAULT_TEMPERATURE;
    private double volume = DEFAULT_VOLUME;
    private double totalHeatCapacity = ChemConfig.BEAKER_GLASS_HEAT_CAPACITY.get();

    public BeakerBlockEntity(BlockPos pos, BlockState state) {
        super(ModChemistry.ModChemistryBlockEntities.BEAKER.get(), pos, state);
    }

    // ==================== IChemicalContainer 实现 ====================

    @Override
    public Map<IonType, Double> getContents() {
        return Map.copyOf(contents);
    }

    @Override
    public double getAmount(IonType ion) {
        return contents.getOrDefault(ion, 0.0);
    }

    @Override
    public synchronized double addIon(IonType ion, double moles) {
        if (moles <= 0 || ion == null) return 0;
        double current = contents.getOrDefault(ion, 0.0);
        double newAmount = current + moles;
        contents.put(ion, newAmount);
        recalcHeatCapacity();
        setChanged();
        syncToClient();
        return moles;
    }

    @Override
    public synchronized double removeIon(IonType ion, double moles) {
        if (moles <= 0 || ion == null) return 0;
        double current = contents.getOrDefault(ion, 0.0);
        double removed = Math.min(current, moles);
        if (removed <= 0) return 0;
        double newAmount = current - removed;
        if (newAmount < 1e-9) {
            contents.remove(ion);
        } else {
            contents.put(ion, newAmount);
        }
        recalcHeatCapacity();
        setChanged();
        syncToClient();
        return removed;
    }

    @Override
    public boolean contains(IonType ion, double minMoles) {
        return contents.getOrDefault(ion, 0.0) >= minMoles - 1e-9;
    }

    @Override
    public Set<IonType> getPresentIons() {
        return contents.keySet();
    }

    @Override
    public double getTemperature() {
        return temperature;
    }

    @Override
    public void setTemperature(double temperatureKelvin) {
        this.temperature = Math.max(0, temperatureKelvin);
        setChanged();
        syncToClient();
    }

    @Override
    public double getVolume() {
        return volume;
    }

    @Override
    public void setVolume(double volumeLiters) {
        if (volumeLiters > 0) {
            this.volume = volumeLiters;
            setChanged();
            syncToClient();
        }
    }

    @Override
    public double getPressure() {
        double totalMoles = contents.values().stream().mapToDouble(Double::doubleValue).sum();
        if (totalMoles < 1e-9 || volume < 1e-9) return 0;
        double R = 8.314;
        double pressurePa = (totalMoles * R * temperature) / volume;
        return pressurePa / ChemConfig.PRESSURE_STANDARD_ATMOSPHERE.get();
    }

    @Override
    public double getTotalHeatCapacity() {
        return totalHeatCapacity;
    }

    @Override
    public void addThermalEnergy(double joules) {
        if (totalHeatCapacity < 1e-9) return;
        double deltaT = joules / totalHeatCapacity;
        double newTemp = temperature + deltaT;
        this.temperature = Math.min(Math.max(0, newTemp), MAX_SAFE_TEMPERATURE);
        setChanged();
        syncToClient();
    }

    @Override
    public Level getLevel() {
        return super.getLevel();
    }

    @Override
    public BlockPos getBlockPos() {
        return super.getBlockPos();
    }

    @Override
    public boolean isRemoved() {
        return super.isRemoved();
    }

    // ==================== 内部方法 ====================

    private void recalcHeatCapacity() {
        double sum = 0;
        for (Map.Entry<IonType, Double> entry : contents.entrySet()) {
            IonType ion = entry.getKey();
            double moles = entry.getValue();
            sum += moles * ion.getSpecificHeat();
        }
        this.totalHeatCapacity = sum + ChemConfig.BEAKER_GLASS_HEAT_CAPACITY.get();
        if (this.totalHeatCapacity < ChemConfig.MIN_HEAT_CAPACITY.get()) {
            this.totalHeatCapacity = ChemConfig.MIN_HEAT_CAPACITY.get();
        }
    }

    private void syncToClient() {
        if (getLevel() != null && !getLevel().isClientSide) {
            getLevel().sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
        }
    }

    // ==================== 网络同步（核心修复） ====================

    @Override
    public CompoundTag getUpdateTag() {
        // 客户端在加载时调用的更新标签
        CompoundTag tag = super.getUpdateTag();
        saveAdditional(tag);
        return tag;
    }

    @Override
    public void handleUpdateTag(CompoundTag tag) {
        // 客户端接收更新标签时调用，直接加载数据
        load(tag);
    }

    @Nullable
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        // 服务端发送更新包
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        // 客户端接收更新包时调用
        if (pkt.getTag() != null) {
            handleUpdateTag(pkt.getTag());
        }
    }

    // ==================== Tick 逻辑 ====================

    public static void tick(Level level, BlockPos pos, BlockState state, BeakerBlockEntity beaker) {
        if (level.isClientSide) return;

        beaker.detectAndApplyHeat(level, pos);

        double envTemp = beaker.getEnvironmentTemperature(level, pos);
        if (beaker.temperature > envTemp) {
            double heatLoss = HEAT_TRANSFER_COEFFICIENT * (beaker.temperature - envTemp) * 0.05;
            beaker.addThermalEnergy(-heatLoss);
        }

        // 后续接入 ReactionEngine
        // beaker.reactionEngineTick();

        if (beaker.temperature >= ChemConfig.MAX_SAFE_TEMPERATURE.get() * ChemConfig.CRITICAL_TEMPERATURE_RATIO.get()) {
            Main.LOGGER.warn("[Beaker] Beaker at {} is reaching critical temperature! {:.1f}K",
                    pos, beaker.temperature);
        }
    }

    private void detectAndApplyHeat(Level level, BlockPos pos) {
        BlockPos below = pos.below();
        BlockState belowState = level.getBlockState(below);
        Block belowBlock = belowState.getBlock();

        Double heatSourceTemp = HEAT_SOURCE_TEMPS.get(belowBlock);
        if (heatSourceTemp == null || heatSourceTemp <= temperature) return;

        double deltaT = HEAT_TRANSFER_COEFFICIENT * (heatSourceTemp - temperature) * 0.05;
        double heatEnergy = deltaT * totalHeatCapacity;
        addThermalEnergy(heatEnergy);
    }

    private double getEnvironmentTemperature(Level level, BlockPos pos) {
        double base = ChemConfig.ENVIRONMENT_TEMPERATURE_BASE.get();
        double yFactor = Math.max(0, (pos.getY() - 64) / 100.0) * ChemConfig.ENVIRONMENT_TEMPERATURE_LAPSE_RATE.get();
        return base - yFactor;
    }

    // ==================== NBT ====================

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);

        tag.putDouble("temperature", temperature);
        tag.putDouble("volume", volume);

        ListTag contentsList = new ListTag();
        for (Map.Entry<IonType, Double> entry : contents.entrySet()) {
            CompoundTag entryTag = new CompoundTag();
            entryTag.putString("id", entry.getKey().getId().toString());
            entryTag.putDouble("moles", entry.getValue());
            contentsList.add(entryTag);
        }
        tag.put("contents", contentsList);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);

        temperature = tag.getDouble("temperature");
        if (temperature <= 0) temperature = DEFAULT_TEMPERATURE;

        volume = tag.getDouble("volume");
        if (volume <= 0) volume = DEFAULT_VOLUME;

        contents.clear();
        ListTag contentsList = tag.getList("contents", Tag.TAG_COMPOUND);
        for (int i = 0; i < contentsList.size(); i++) {
            CompoundTag entryTag = contentsList.getCompound(i);
            String idStr = entryTag.getString("id");
            double moles = entryTag.getDouble("moles");
            if (moles > 1e-9) {
                ResourceLocation id = ResourceLocation.tryParse(idStr);
                if (id != null) {
                    IonType ion = ModIons.getById(id);
                    if (ion != null) {
                        contents.put(ion, moles);
                    }
                }
            }
        }

        recalcHeatCapacity();
    }
}