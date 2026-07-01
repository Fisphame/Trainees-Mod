package com.pha.trainees.blockentity;

import com.pha.trainees.menu.ReactionMachineMenu;
import com.pha.trainees.registry.ModBlocks;
import com.pha.trainees.registry.ModChemistry;
import com.pha.trainees.registry.ModRecipes;
import com.pha.trainees.util.game.chemistry.CatalystCondition;
import com.pha.trainees.util.game.chemistry.ChemicalComponent;
import com.pha.trainees.util.game.chemistry.MachineChemicalEquation;
import com.pha.trainees.util.game.chemistry.MachineCondition;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Collectors;

public class ReactionMachineBlockEntity extends BlockEntity implements MenuProvider {
    // 常量
    private static final int INPUT_SLOTS = 3;
    private static final int CATALYST_SLOT = 0;   // 催化剂槽位索引（在输入槽之后）
    private static final int OUTPUT_SLOTS = 3;
    private static final int TOTAL_SLOTS = INPUT_SLOTS + 1 + OUTPUT_SLOTS;

    private static final int REACTION_INTERVAL_TICKS = 2; // 2 tick 检测一次
    private static final double BASE_TEMPERATURE_K = 293.15; // 20°C
    private static final double HEATING_TEMPERATURE_K = 373.15; // 100°C

    // 物品处理器
    private final ItemStackHandler itemHandler = new ItemStackHandler(TOTAL_SLOTS) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };
    private LazyOptional<IItemHandler> itemHandlerCap = LazyOptional.of(() -> itemHandler);

    // 温度与反应状态
    private double temperatureK = BASE_TEMPERATURE_K;
    private int reactionRemainingTicks = 0;
    private MachineChemicalEquation currentEquation = null;
    private boolean waitingForOutput = false; // 等待输出空间

    // 计时器
    private int cooldown = 0;

    public ReactionMachineBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlocks.ModBlockEntities.REACTION_MACHINE.get(), pos, state);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        itemHandler.deserializeNBT(tag.getCompound("Inventory"));
        temperatureK = tag.getDouble("Temperature");
        reactionRemainingTicks = tag.getInt("ReactionRemainingTicks");
        waitingForOutput = tag.getBoolean("WaitingForOutput");
        // 不保存 currentEquation，因为反应结束后会重新匹配
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("Inventory", itemHandler.serializeNBT());
        tag.putDouble("Temperature", temperatureK);
        tag.putInt("ReactionRemainingTicks", reactionRemainingTicks);
        tag.putBoolean("WaitingForOutput", waitingForOutput);
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return itemHandlerCap.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        itemHandlerCap.invalidate();
    }

    // 每 tick 调用（由服务器调用，客户端可调用但不应修改逻辑）
    public static void tick(Level level, BlockPos pos, BlockState state, ReactionMachineBlockEntity be) {
        if (level.isClientSide) return;

        // 周期检测（每2 tick）
        if (be.cooldown > 0) {
            be.cooldown--;
            return;
        }
        be.cooldown = REACTION_INTERVAL_TICKS;

        // 更新温度（暂不实现变化，恒温）
        // be.updateTemperature();

        // 处理反应状态
        if (be.waitingForOutput) {
            be.tryOutput();
            return;
        }

        if (be.reactionRemainingTicks > 0) {
            be.reactionRemainingTicks--;
            if (be.reactionRemainingTicks == 0) {
                be.completeReaction();
            }
            return;
        }

        // 空闲状态，尝试匹配配方
        be.tryStartReaction();
    }

    // 尝试启动反应
    private void tryStartReaction() {
        // 查找可匹配的配方
        MachineChemicalEquation equation = findMatchingRecipe();
        if (equation == null) return;

        // 检查催化剂条件（包含催化剂物品和消耗概率）
        if (!checkCatalyst(equation)) return;

        // 检查其他条件（加热等，目前只检查温度条件，但加热未实现，先跳过）
        if (!checkOtherConditions(equation)) return;

        // 消耗反应物和催化剂
        if (!consumeReactants(equation)) return; // 理论上应该成功，但防错
        consumeCatalyst(equation);

        // 开始反应
        currentEquation = equation;
        reactionRemainingTicks = equation.getDuration();
        setChanged();
    }

    // 查找匹配的配方
    private MachineChemicalEquation findMatchingRecipe() {
        // 统计输入槽中每种物品的总数量（只考虑前 INPUT_SLOTS 个槽）
        java.util.Map<Item, Integer> inputMap = new java.util.HashMap<>();
        for (int i = 0; i < INPUT_SLOTS; i++) {
            ItemStack stack = itemHandler.getStackInSlot(i);
            if (!stack.isEmpty()) {
                inputMap.merge(stack.getItem(), stack.getCount(), Integer::sum);
            }
        }

        // 遍历所有注册的配方（静态列表）
        for (MachineChemicalEquation equation : ModRecipes.ModMachineRecipes.getAllRecipes()) {
            // 检查反应物匹配
            boolean match = true;
            for (ChemicalComponent comp : equation.getReactants()) {
                Integer available = inputMap.get(comp.getItem());
                if (available == null || available < comp.getCoefficient()) {
                    match = false;
                    break;
                }
            }
            if (!match) continue;

            // 检查是否有流体反应物（暂不支持，跳过）
            if (!equation.getFluidReactants().isEmpty()) continue;

            // 匹配成功
            return equation;
        }
        return null;
    }

    // 检查催化剂条件
    private boolean checkCatalyst(MachineChemicalEquation equation) {
        // 获取催化剂条件（如果有）
        for (MachineCondition condition : equation.getConditions()) {
            if (condition instanceof CatalystCondition catalyst) {
                // 检查催化剂槽是否有所需物品
                ItemStack catalystStack = itemHandler.getStackInSlot(INPUT_SLOTS + CATALYST_SLOT);
                // 简化：假设催化剂条件只要求单个物品
                List<ItemStack> required = catalyst.getRequiredCatalysts();
                if (required.isEmpty()) return true;
                ItemStack requiredStack = required.get(0);
                if (catalystStack.getItem() != requiredStack.getItem()) return false;
                if (catalystStack.getCount() < requiredStack.getCount()) return false;
                return true;
            }
        }
        return true; // 没有催化剂条件，直接通过
    }

    // 检查其他条件（加热、光照等）
    private boolean checkOtherConditions(MachineChemicalEquation equation) {
        for (MachineCondition condition : equation.getConditions()) {
            if (condition instanceof CatalystCondition) continue;
            // 其他条件，暂时未实现，直接返回 true
            // 未来可在此处检查温度等
            // if (condition instanceof HeatCondition) { ... }
        }
        return true;
    }

    // 消耗反应物
    private boolean consumeReactants(MachineChemicalEquation equation) {
        // 复制反应物要求，记录需要扣除的数量
        java.util.Map<Item, Integer> toConsume = new java.util.HashMap<>();
        for (ChemicalComponent comp : equation.getReactants()) {
            toConsume.merge(comp.getItem(), comp.getCoefficient(), Integer::sum);
        }

        // 从输入槽中按顺序扣除
        for (int i = 0; i < INPUT_SLOTS; i++) {
            ItemStack stack = itemHandler.getStackInSlot(i);
            if (stack.isEmpty()) continue;
            Item item = stack.getItem();
            Integer need = toConsume.get(item);
            if (need != null && need > 0) {
                int toRemove = Math.min(need, stack.getCount());
                stack.shrink(toRemove);
                need -= toRemove;
                if (need == 0) {
                    toConsume.remove(item);
                } else {
                    toConsume.put(item, need);
                }
                if (stack.isEmpty()) {
                    itemHandler.setStackInSlot(i, ItemStack.EMPTY);
                }
                if (toConsume.isEmpty()) break;
            }
        }

        return toConsume.isEmpty();
    }

    // 消耗催化剂（按概率）
    private void consumeCatalyst(MachineChemicalEquation equation) {
        for (MachineCondition condition : equation.getConditions()) {
            if (condition instanceof CatalystCondition catalyst) {
                ItemStack catalystStack = itemHandler.getStackInSlot(INPUT_SLOTS + CATALYST_SLOT);
                if (!catalystStack.isEmpty()) {
                    // 假设催化剂条件只要求一个物品
                    List<ItemStack> required = catalyst.getRequiredCatalysts();
                    if (!required.isEmpty()) {
                        ItemStack requiredStack = required.get(0);
                        if (catalystStack.getItem() == requiredStack.getItem()) {
                            // 按概率消耗一个单位（或指定数量？这里简化：消耗所需数量）
                            if (catalyst.getConsumeChance() > 0 && level.random.nextDouble() < catalyst.getConsumeChance()) {
                                catalystStack.shrink(requiredStack.getCount());
                                if (catalystStack.isEmpty()) {
                                    itemHandler.setStackInSlot(INPUT_SLOTS + CATALYST_SLOT, ItemStack.EMPTY);
                                }
                            }
                        }
                    }
                }
                break;
            }
        }
    }

    // 完成反应，尝试输出产物
    private void completeReaction() {
        if (currentEquation == null) return;

        // 生成产物列表
        List<ItemStack> products = currentEquation.getProducts().stream()
                .map(comp -> comp.createStack(1)) // 单次反应
                .collect(Collectors.toList());

        // 尝试放入输出槽
        if (tryOutputProducts(products)) {
            // 输出成功，清除当前反应状态
            currentEquation = null;
            waitingForOutput = false;
            setChanged();
        } else {
            // 输出空间不足，进入等待输出状态
            waitingForOutput = true;
            setChanged();
        }
    }

    // 尝试输出产物，返回是否全部成功放入
    private boolean tryOutputProducts(List<ItemStack> products) {
        // 复制一份，尝试放入
        ItemStackHandler tempHandler = new ItemStackHandler(OUTPUT_SLOTS);
        for (int i = 0; i < OUTPUT_SLOTS; i++) {
            tempHandler.setStackInSlot(i, itemHandler.getStackInSlot(INPUT_SLOTS + 1 + i).copy());
        }

        for (ItemStack product : products) {
            ItemStack remaining = product.copy();
            for (int i = 0; i < OUTPUT_SLOTS; i++) {
                if (remaining.isEmpty()) break;
                ItemStack slotStack = tempHandler.getStackInSlot(i);
                if (slotStack.isEmpty()) {
                    tempHandler.setStackInSlot(i, remaining);
                    remaining = ItemStack.EMPTY;
                } else if (ItemStack.isSameItemSameTags(slotStack, remaining) && slotStack.getCount() < slotStack.getMaxStackSize()) {
                    int canAdd = Math.min(remaining.getCount(), slotStack.getMaxStackSize() - slotStack.getCount());
                    slotStack.grow(canAdd);
                    remaining.shrink(canAdd);
                }
            }
            if (!remaining.isEmpty()) {
                return false; // 空间不足
            }
        }

        // 所有产物都有空间，更新实际槽位
        for (int i = 0; i < OUTPUT_SLOTS; i++) {
            itemHandler.setStackInSlot(INPUT_SLOTS + 1 + i, tempHandler.getStackInSlot(i));
        }
        return true;
    }

    // 等待输出时，每 tick 尝试输出
    private void tryOutput() {
        if (currentEquation == null) {
            waitingForOutput = false;
            return;
        }
        List<ItemStack> products = currentEquation.getProducts().stream()
                .map(comp -> comp.createStack(1))
                .collect(Collectors.toList());
        if (tryOutputProducts(products)) {
            currentEquation = null;
            waitingForOutput = false;
            setChanged();
        }
    }

    // 获取温度（开尔文）
    public double getTemperatureK() {
        return temperatureK;
    }

    // 获取摄氏度显示
    public int getTemperatureC() {
        return (int) (temperatureK - 273.15);
    }

    // 获取反应剩余 ticks
    public int getReactionRemainingTicks() {
        return reactionRemainingTicks;
    }

    // 是否正在反应中（包括等待输出）
    public boolean isReacting() {
        return reactionRemainingTicks > 0 || waitingForOutput;
    }

    // 获取当前反应进度（0-1）
    public float getProgress() {
        if (currentEquation == null || currentEquation.getDuration() <= 0) return 0;
        return 1.0f - (float) reactionRemainingTicks / currentEquation.getDuration();
    }

    // 获取状态文本
    public Component getStatusText() {
        if (waitingForOutput) {
            return Component.translatable("container.reaction_machine.status.waiting_output");
        } else if (reactionRemainingTicks > 0) {
            return Component.translatable("container.reaction_machine.status.reacting");
        } else {
            return Component.translatable("container.reaction_machine.status.idle");
        }
    }

    // ========== MenuProvider 实现 ==========
    @Override
    public Component getDisplayName() {
        return Component.translatable("block.trainees.reaction_machine");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        return new ReactionMachineMenu(id, inventory, worldPosition);
    }

    // 提供物品处理器访问（供菜单使用）
    public ItemStackHandler getItemHandler() {
        return itemHandler;
    }

}