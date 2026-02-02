package com.pha.trainees.util.game.structure;

import com.pha.trainees.Main;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.*;

/**
 * 多方块结构模式
 */
public class MultiblockPattern {

    // 结构尺寸
    private final int width;   // X方向
    private final int height;  // Y方向
    private final int depth;   // Z方向

    // 结构原点偏移（匹配位置 -> 结构原点）
    private final BlockPos originOffset;

    // 匹配位置（通常是结构中的关键方块）
    private final BlockPos matchPos;

    // 激活处理器
    @Nullable
    private final IActivationHandler activationHandler;

    // 方块条件映射：相对位置 -> 条件
    private final Map<BlockPos, ConditionEntry> conditions = new HashMap<>();

    // 忽略的位置（通常是空气）
    private final Set<BlockPos> ignoredPositions = new HashSet<>();

    /**
     * 结构匹配结果
     */
    public static class MatchResult {
        public final BlockPos matchPos;       // 匹配的位置（传递给激活处理器）
        public final BlockPos originPos;      // 结构原点位置
        public final Direction facing;        // 结构朝向


        public MatchResult(BlockPos matchPos, BlockPos originPos, Direction facing) {
            this.matchPos = matchPos;
            this.originPos = originPos;
            this.facing = facing;
        }

        /**
         * 获取结构中的绝对位置（考虑朝向）
         */
        public BlockPos getAbsolutePos(BlockPos relativePos) {
            return rotatePosition(originPos, relativePos, facing);
        }

        /**
         * 获取所有结构方块位置
         */
        public Set<BlockPos> getAllPositions() {
            Set<BlockPos> positions = new HashSet<>();
            // 这里可以根据需要实现
            return positions;
        }
    }

    public static class SafeMatchResult {
        public final MatchResult matchResult;
        public final String error;
        public final boolean success;

        public SafeMatchResult(MatchResult matchResult) {
            this.matchResult = matchResult;
            this.error = null;
            this.success = true;
        }

        public SafeMatchResult(String error) {
            this.matchResult = null;
            this.error = error;
            this.success = false;
        }
    }

    /**
     * 条件条目
     */
    private static class ConditionEntry {
        final IBlockPredicate predicate;
        final boolean required;
        final String description;

        ConditionEntry(IBlockPredicate predicate, boolean required, String description) {
            this.predicate = predicate;
            this.required = required;
            this.description = description;
        }
    }

    public MultiblockPattern(Builder builder) {
        this.width = builder.width;
        this.height = builder.height;
        this.depth = builder.depth;
        this.originOffset = builder.originOffset;
        this.matchPos = builder.matchPos;
        this.activationHandler = builder.activationHandler;
        this.conditions.putAll(builder.conditions);
        this.ignoredPositions.addAll(builder.ignoredPositions);
    }

    /**
     * 检查结构是否完整
     */
    @Nullable
    public MatchResult check(Level level, BlockPos checkPos) {
        // 计算结构原点
        BlockPos originPos = checkPos.offset(originOffset);

        // 检查所有方向
        List<Direction> validFacings = getValidFacings();

        for (Direction facing : validFacings) {
            if (checkAtFacing(level, originPos, facing)) {
                BlockPos actualMatchPos = rotatePosition(originPos, matchPos, facing);
                return new MatchResult(actualMatchPos, originPos, facing);
            }
        }

        return null;
    }

    /**
     * 安全地检查结构完整性，避免异常传播
     */
    @Nullable
    public SafeMatchResult checkSafe(Level level, BlockPos checkPos) {
        try {
            MatchResult result = check(level, checkPos);
            return result != null ? new SafeMatchResult(result) : null;
        } catch (Exception e) {
            Main.LOGGER.error("Error checking multiblock pattern at {}", checkPos, e);
            return new SafeMatchResult(e.getMessage());
        }
    }

    /**
     * 检查特定朝向的结构
     */
    private boolean checkAtFacing(Level level, BlockPos originPos, Direction facing) {

        int passed = 0;
        int total = conditions.size();

        for (Map.Entry<BlockPos, ConditionEntry> entry : conditions.entrySet()) {
            BlockPos relativePos = entry.getKey();
            ConditionEntry condition = entry.getValue();

            // 旋转位置（考虑朝向）
            BlockPos absolutePos = rotatePosition(originPos, relativePos, facing);
            BlockState state = level.getBlockState(absolutePos);

            // 记录当前位置信息
            String blockName = state.getBlock().getDescriptionId();

            // 执行条件检查
            boolean testResult = condition.predicate.test(level, absolutePos, state);

            if (testResult) {
                passed++;
            } else {
                if (condition.required) {
                    return false;
                }
            }
        }


        // 检查忽略的位置
        if (!ignoredPositions.isEmpty()) {
            for (BlockPos relativePos : ignoredPositions) {
                BlockPos absolutePos = rotatePosition(originPos, relativePos, facing);
                if (!level.isEmptyBlock(absolutePos)) {
                    Main.LOGGER.info("   ⚠ [Ignored] Position {} has block: {}",
                            absolutePos, level.getBlockState(absolutePos).getBlock().getDescriptionId());
                }
            }
        }

        return true;
    }

    /**
     * 获取有效的朝向
     */
    private List<Direction> getValidFacings() {
        // 默认只支持北朝向，可以根据需要扩展
        return Collections.singletonList(Direction.NORTH);
    }

    /**
     * 旋转位置（考虑朝向）
     */
    private static BlockPos rotatePosition(BlockPos origin, BlockPos relativePos, Direction facing) {
        int x = relativePos.getX();
        int y = relativePos.getY();
        int z = relativePos.getZ();

        // 根据朝向旋转
        return switch (facing) {
            case EAST -> origin.offset(-z, y, x);
            case SOUTH -> origin.offset(-x, y, -z);
            case WEST -> origin.offset(z, y, -x);
            default -> origin.offset(x, y, z);
        };
    }

    /**
     * 获取结构尺寸
     */
    public BlockPos getDimensions() {
        return new BlockPos(width, height, depth);
    }

    /**
     * 获取激活处理器
     */
    @Nullable
    public IActivationHandler getActivationHandler() {
        return activationHandler;
    }

    /**
     * 获取结构匹配位置（相对位置）
     */
    public BlockPos getMatchPos() {
        return matchPos;
    }

    /**
     * 获取结构原点偏移
     */
    public BlockPos getOriginOffset() {
        return originOffset;
    }

    /**
     * 构建器类
     */
    public static class Builder {
        private int width = 1;
        private int height = 1;
        private int depth = 1;
        private BlockPos originOffset = BlockPos.ZERO;
        private BlockPos matchPos = BlockPos.ZERO;
        @Nullable
        private IActivationHandler activationHandler = null;
        private final Map<BlockPos, ConditionEntry> conditions = new HashMap<>();
        private final Set<BlockPos> ignoredPositions = new HashSet<>();

        /**
         * 设置结构尺寸
         */
        public Builder dimensions(int width, int height, int depth) {
            this.width = width;
            this.height = height;
            this.depth = depth;
            return this;
        }

        /**
         * 设置原点偏移
         */
        public Builder originOffset(BlockPos offset) {
            this.originOffset = offset;
            return this;
        }

        /**
         * 设置匹配位置（相对位置）
         */
        public Builder matchPos(BlockPos relativePos) {
            this.matchPos = relativePos;
            return this;
        }

        /**
         * 设置激活处理器
         */
        public Builder activationHandler(IActivationHandler handler) {
            this.activationHandler = handler;
            return this;
        }

        /**
         * 添加方块条件
         */
        public Builder addCondition(BlockPos relativePos, IBlockPredicate predicate) {
            return addCondition(relativePos, predicate, true, "custom condition");
        }

        /**
         * 添加方块条件（带描述）
         */
        public Builder addCondition(BlockPos relativePos, IBlockPredicate predicate, boolean required, String description) {
            conditions.put(relativePos, new ConditionEntry(predicate, required, description));
            return this;
        }

        /**
         * 添加忽略位置（通常是空气位置）
         */
        public Builder addIgnoredPosition(BlockPos relativePos) {
            ignoredPositions.add(relativePos);
            return this;
        }

        /**
         * 构建结构模式
         */
        public MultiblockPattern build() {
            // 验证必要的设置
            if (width <= 0 || height <= 0 || depth <= 0) {
                throw new IllegalArgumentException("Dimensions must be positive");
            }

            if (conditions.isEmpty()) {
                Main.LOGGER.warn("Building MultiblockPattern with no conditions");
            }

            return new MultiblockPattern(this);
        }
    }
}