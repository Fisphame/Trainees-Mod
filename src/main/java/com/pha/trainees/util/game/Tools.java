package com.pha.trainees.util.game;

import com.pha.trainees.enchantments.*;
import com.pha.trainees.item.AuriversiteRapierItem;
import com.pha.trainees.item.KunCourseItem;
import com.pha.trainees.item.ScytheCourseItem;
import com.pha.trainees.item.interfaces.KineticWeapon;
import com.pha.trainees.registry.*;
import com.pha.trainees.util.math.LogarithmicFunc;
import com.pha.trainees.util.math.MAth;
import com.pha.trainees.util.math.Pair;
import com.pha.trainees.util.math.QuadraticFuncVertexT;
import com.pha.trainees.util.physics.KineticData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.*;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import org.checkerframework.checker.units.qual.A;
import org.joml.Vector3f;

import javax.annotation.Nullable;
import java.awt.*;
import java.time.Year;
import java.util.*;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static com.pha.trainees.registry.ModSounds.*;
import static java.lang.Math.max;
import static java.lang.Math.min;

// 方法类
public class Tools {



    public static Pair AFireflyGleamAgainstTheMoon(double a, double b){
        if (a >= b) return new Pair(b, a);
        else return new Pair(a, b);
    }

    // 随机数 （注：游戏内的随机数）
    public static double randomInRange(Level level, double min, double max) {
        Pair pair = AFireflyGleamAgainstTheMoon(min, max);
        min = pair.x1(); max = pair.x2();
        // 使用 nextDouble() 生成 [0,1) 的随机数，然后映射到 [min, max] 区间
        return min + (max - min) * level.random.nextDouble();
    }
    public static double randomInRange(RandomSource random, double min, double max) {
        Pair pair = AFireflyGleamAgainstTheMoon(min, max);
        min = pair.x1(); max = pair.x2();
        return min + (max - min) * random.nextDouble();
    }
    public static int randomInRange(Level level, int min, int max) {
        Pair pair = AFireflyGleamAgainstTheMoon(min, max);
        min = (int) pair.x1(); max = (int) pair.x2();
        // 使用 nextDouble() 生成 [0,1) 的随机数，然后映射到 [min, max] 区间
        return min + level.random.nextInt(max + 1);
    }
    public static int randomInRange(RandomSource random, int min, int max) {
        Pair pair = AFireflyGleamAgainstTheMoon(min, max);
        min = (int) pair.x1(); max = (int) pair.x2();
        return min + random.nextInt(max + 1);
    }
    public static boolean chance(Level level, double probability) {
        // 快速边界检查
        if (probability <= 0.0) return false;
        if (probability >= 1.0) return true;

        return level.random.nextDouble() < probability;
    }
    /**
     * 使用指定随机源的概率判断
     */
    public static boolean chance(RandomSource random, double probability) {
        // 参数检查
        if (probability <= 0.0) return false;
        if (probability >= 1.0) return true;

        return random.nextDouble() < probability;
    }
    /**
     * 带偏见的概率（非均匀分布）
     * @param bias 偏置因子 (0.5=均匀, <0.5=更常false, >0.5=更常true)
     */
    public static boolean biasedChance(RandomSource random, double probability, double bias) {
        if (probability <= 0.0) return false;
        if (probability >= 1.0) return true;

        // 应用偏置
        double biased = Math.pow(probability, MAth.log(bias, 0.5));
        return random.nextDouble() < biased;
    }

    /*
    参考

        效果描述	x 值范围	y 值范围	z 值范围	使用场景
        轻微散开	[-0.1, 0.1]	[0.0, 0.2]	[-0.1, 0.1]	物品轻微分散
        中等散开	[-0.3, 0.3]	[0.1, 0.4]	[-0.3, 0.3]	物品明显分散
        强力散开	[-0.8, 0.8]	[0.3, 0.8]	[-0.8, 0.8]	爆炸效果
        水平散开	[-0.5, 0.5]	[0.0, 0.1]	[-0.5, 0.5]	地面滚动
        向上抛射	[-0.2, 0.2]	[0.5, 1.0]	[-0.2, 0.2]	投掷效果
        向下掉落	[-0.1, 0.1]	[-0.5, -0.1]	[-0.1, 0.1]	重力效果
     */

    public static NumedItemEntities AddEntity(Level level, ItemEntity entity, ItemStack itemStack, int move){
        double x = entity.getX(), y = entity.getY(), z = entity.getZ();
        int count = itemStack.getCount();
        Item item = itemStack.getItem();
        // 分组计算
        int groups = calculateSafeGroups(count);
        // 数组大小和索引处理
        ItemEntity[] itemEntities = new ItemEntity[groups];
        int baseCount = count / groups;
        int remainder = count % groups;

        for (int i = 0; i < groups; i++){
            int now_count = baseCount + (i < remainder ? 1 : 0);
            if (now_count > 0) {
                ItemEntity itemEntity = new ItemEntity(level, x, y, z, new ItemStack(item, now_count));
                if (move == 1){
                    // 根据物品数量调整散开强度
                    double spreadIntensity = min(randomInRange(level, 0.42, 0.005 * count + 0.1), 0.8);
                    double verticalIntensity = min(randomInRange(level, 0.164, 0.001 * count + 0.1), 1.0);

                    itemEntity.setDeltaMovement(
                            randomInRange(level, -spreadIntensity, spreadIntensity),
                            randomInRange(level, verticalIntensity, verticalIntensity),
                            randomInRange(level, -spreadIntensity, spreadIntensity)
                    );
                } else if (move == 2) {
                    double spreadIntensity = min(randomInRange(level, 0.20, 0.005 * count + 0.1), 0.4);
                    double verticalIntensity = min(randomInRange(level, 0.08, 0.001 * count + 0.1), 0.5);

                    itemEntity.setDeltaMovement(
                            randomInRange(level, -spreadIntensity, spreadIntensity),
                            randomInRange(level, verticalIntensity, verticalIntensity),
                            randomInRange(level, -spreadIntensity, spreadIntensity)
                    );
                } else if (move == 3){
                    itemEntity.setDeltaMovement(
                            entity.getDeltaMovement().x,
                            entity.getDeltaMovement().y + 0.1,
                            entity.getDeltaMovement().z
                    );
                }

                itemEntity.setDefaultPickUpDelay();
                itemEntities[i] = itemEntity;
            }
        }
        return new NumedItemEntities(itemEntities, groups);
    }

    // 新增辅助方法：安全计算分组数量
    private static int calculateSafeGroups(int count) {
        if (count <= 0) return 1;

        // 对于小数量物品，使用较小的分组
        if (count <= 8) {
            return min(count, 3);
        }

        // 对于中等数量，使用对数分组但限制范围
        LogarithmicFunc func = new LogarithmicFunc(3);
        int groups = (int) Math.ceil(func.getY(count));
        groups = max(1, min(groups, 64)); // 限制在1-N之间

        return groups;
    }

    public static void AddNIEs(ItemEntity entity, NumedItemEntities itemEntities, boolean Invulnerable){
        for (int i = 0; i < itemEntities.num(); i++){
            ItemEntity itemEntity = itemEntities.itemEntities()[i];

            itemEntity.setInvulnerable(Invulnerable);
            entity.level().addFreshEntity(itemEntity);
        }
    }

    public static int getPowderMultiplier(Item item) {
        if (item == ModItems.POWDER_ANTI.get()) return MAth.POW[0];
        if (item == ModItems.POWDER_ANTI_4.get()) return 4;
        if (item == ModItems.POWDER_ANTI_9.get()) return MAth.POW[1];
        if (item == Something.PrankItems.POWDER_ANTI_92.get()) return MAth.POW[2];
        if (item == Something.PrankItems.POWDER_ANTI_93.get()) return MAth.POW[3];
        if (item == Something.PrankItems.POWDER_ANTI_94.get()) return MAth.POW[4];
        if (item == Something.PrankItems.POWDER_ANTI_95.get()) return MAth.POW[5];
        if (item == Something.PrankItems.POWDER_ANTI_96.get()) return MAth.POW[6];
        if (item == Something.PrankItems.POWDER_ANTI_97.get()) return MAth.POW[7];
        if (item == Something.PrankItems.POWDER_ANTI_98.get()) return MAth.POW[8];

        return 1;
    }

    public static Boolean isJi(Item item) {
        return item == ModItems.KUN_NUGGET.get() ||
                item == ModItems.TWO_HALF_INGOT.get() ||
                item == ModItems.TWO_HALF_INGOT_BLOCK_ITEM.get();
    }

    public static Boolean isJi(ItemStack stack) {
        return isJi(stack.getItem());
    }

    public static Boolean isElementary(Item item){
        return isJi(item) || isInstanceof.tag(item, ModTags.POWDER_ANTI_2);
    }

    public static Boolean isElementary(ItemStack stack){
        return  isElementary(stack.getItem());
    }

    public static Item[] JiItems = new Item[]{
            ModChemistry.ModChemistryItems.IMPERFECTION.get(),

            ModChemistry.ModChemistryItems.CHE_JIOH_NUGGET.get(),
            ModChemistry.ModChemistryItems.CHE_JIOH_INGOT.get(),
            ModChemistry.ModChemistryBlockItems.CHE_JIOH_BLOCK_ITEM.get(),

            ModChemistry.ModChemistryItems.CHE_JI2O_NUGGET.get(),
            ModChemistry.ModChemistryItems.CHE_JI2O_INGOT.get(),
            ModChemistry.ModChemistryBlockItems.CHE_JI2O_BLOCK_ITEM.get(),

            ModChemistry.ModChemistryItems.CHE_JI2O2_NUGGET.get(),
            ModChemistry.ModChemistryItems.CHE_JI2O2_INGOT.get(),
            ModChemistry.ModChemistryBlockItems.CHE_JI2O2_BLOCK_ITEM.get(),
    };


    /**
     * @param n <p></p>
     *          =1     OH-
     *          <p>
     *          =2     O(-2)
     *          <p>
     *          =3     O(-1)
     */
    public static Item getWhichJiProduct(Item item, int n) {
        return item == ModItems.KUN_NUGGET.get() ?
                JiItems[ (n - 1) * 3 + 1] :
                ( item == ModItems.TWO_HALF_INGOT.get() ?
                        JiItems[(n - 1) * 3 + 2] :
                        (item == ModItems.TWO_HALF_INGOT_BLOCK_ITEM.get() ?
                                JiItems[(n - 1) * 3 + 3] :
                                JiItems[0]
                        )
                );
    }

    public static Item getWhichJiProduct(ItemStack stack, int n) {
        return getWhichJiProduct(stack.getItem(), n);
    }

    public static void DoTnt_center(Level level, double x, double y, double z, float power){
        new DoTnt.Builder(level, x, y, z)
                .setCenter(true)
                .setFuse(0)
                .setPower(power)
                .spawn();
    }
    public static void DoTnt_center(Level level, double x, double y, double z) {
        DoTnt_center(level, x, y, z, 4.0f);
    }
    public static void DoTnt_center(Level level, BlockPos pos, float power) {
        DoTnt_center(level, pos.getX(), pos.getY(), pos.getZ(), power);
    }
    public static void DoTnt_center(Level level, BlockPos pos) {
        DoTnt_center(level, pos.getX(), pos.getY(), pos.getZ(), 4.0f);
    }
    public static void DoTnt_6(Level level, double x, double y, double z, float power, int surfaces, int diffusion){
        new DoTnt.Builder(level, x, y, z)
                .setSurfaces(surfaces, diffusion)
                .setFuse(0)
                .setPower(power)
                .spawn();
    }

    public static double punishmentTimeToSunlight(ItemEntity entity) {
        if ( isInSunlight(entity)) {
            Level level = entity.level();
            long daytime = level.getDayTime() % 24000;

            return new QuadraticFuncVertexT(8 * 10e-8, 6000, 0.5).getY(daytime >= 23000 ? daytime - 24000 : daytime);
        }

        return 9999;
    }

    public static boolean isInSunlight(ItemEntity entity) {
        Level level = entity.level();
        long daytime = level.getDayTime() % 24000;

        // 检查是否为白天雨
        return (daytime >= 0 && daytime <= 13000) || (daytime >= 23000) //是否为白天
                && level.canSeeSky(entity.blockPosition())  //是否看到天空
                && !level.isRaining()
                && !level.isThundering()
//                && level.getBrightness(LightLayer.SKY, entity.blockPosition()) == 15 //天空光照等级
                ;

    }

//    public static void setDeltaMovement(ItemEntity entity, int number) {
//        // 安全地获取配置值
//        int movementMax = TraineesConfig.ENTITY_RANDOM_MOVEMENT_MAX.get();
//
//        if (number >= 1) {
//            entity.setDeltaMovement(entity.getDeltaMovement().x, 0.5, entity.getDeltaMovement().z);
//        } else {
//            entity.setDeltaMovement(
//                    entity.level().random.nextDouble() * 2 - 1.0,
//                    entity.level().random.nextDouble() - 0.5,
//                    entity.level().random.nextDouble() * 2 - 1.0
//            );
//        }
//    }

    public static final List<SoundEvent> MINING_SOUNDS = List.of(
            MINING_SOUND_1.get(), MINING_SOUND_2.get(), MINING_SOUND_3.get(),
            MINING_SOUND_4.get(), MINING_SOUND_5.get(), MINING_SOUND_6.get()
    );

    public static final List<SoundEvent> RELEASING_SWORD_WIND_SOUNDS = List.of(
            RELEASING_SWORD_WIND_1.get(), RELEASING_SWORD_WIND_2.get(), RELEASING_SWORD_WIND_3.get(),
            RELEASING_SWORD_WIND_4.get()
    );

    public static final List<SoundEvent> FINAL_MINING_SOUND = List.of(
            FINAL_MINING_SOUND_1.get(), FINAL_MINING_SOUND_2.get(), FINAL_MINING_SOUND_3.get(),
            FINAL_MINING_SOUND_4.get(), FINAL_MINING_SOUND_5.get()
    );

    public static SoundEvent getIndexSound(List<SoundEvent> soundEventList, Player player){
        int index = player.getRandom().nextInt(soundEventList.size());
        return soundEventList.get(index);
    }
    public static SoundEvent getIndexSound(List<SoundEvent> soundEventList, Level level){
        int index = level.getRandom().nextInt(soundEventList.size());
        return soundEventList.get(index);
    }
    public static class isInstanceof{
        public static Boolean scythe(Item item){
            return item instanceof ScytheCourseItem.ScytheItem || item instanceof ScytheCourseItem.CompoundScytheItem;
        }
        public static Boolean repair(Item item){
            return item instanceof AuriversiteRapierItem;
        }
        public static Boolean kunSword(Item item){
            return item instanceof KunCourseItem.KunSwordItem;
        }
        public static Boolean tag(Item item, TagKey<Item> tag) {
            return item.getDefaultInstance().is(tag);
        }

    }

    public static class BlockCourse{
        public static Boolean canBreak(Block block){
            if (block == Blocks.AIR) return false;
            if (block == Blocks.BEDROCK) return false;
            if (    block instanceof CommandBlock ||
                    block == Blocks.STRUCTURE_BLOCK ||
                    block == Blocks.STRUCTURE_VOID ||
                    block == Blocks.JIGSAW ||
                    block == Blocks.BARRIER ||
                    block == Blocks.LIGHT ||
                    block == Blocks.PLAYER_HEAD ||
                    block == Blocks.PLAYER_WALL_HEAD ||
                    block instanceof SpawnerBlock) return false;
            if (block == ModBlocks.MYBLOCK.get()) return false;
            return true;
        }

        public static boolean isInFire(Level level, BlockPos pos) {

            return level.getBlockState(pos).getBlock() == Blocks.FIRE ||
                    level.getBlockState(pos).getBlock() == Blocks.TORCH ||
                    level.getBlockState(pos).getBlock() == Blocks.WALL_TORCH ||
                    level.getBlockState(pos).getBlock() == Blocks.SOUL_TORCH ||
                    level.getBlockState(pos).getBlock() == Blocks.SOUL_WALL_TORCH ||
                    level.getBlockState(pos).getBlock() == Blocks.CAMPFIRE ||
                    level.getBlockState(pos).getBlock() == Blocks.SOUL_CAMPFIRE;
        }
    }





    public static class Command {
        public static boolean getEnchantBrokenBlockDrop(Level level) {
            if (level == null) return false;
            GameRules rules = level.getGameRules();
            return rules.getBoolean(ModCommand.ENCHANT_BROKEN_BLOCK_DROP);
        }
        public static boolean getEnchantBreakBlock(Level level) {
            if (level == null) return false;
            GameRules rules = level.getGameRules();
            return rules.getBoolean(ModCommand.ENCHANT_BREAK_BLOCK);
        }
        public static boolean isReactionExplode(Level level) {
            if (level == null) return false;
            GameRules rules = level.getGameRules();
            return rules.getBoolean(ModCommand.ALLOW_REACTION_EXPLODE);
        }

        // 设置规则值（需要服务器权限）
        public static void setEnchantBrokenBlockDrop(Level level, boolean value) {
            if (level == null || level.isClientSide()) return;
            GameRules rules = level.getGameRules();
            rules.getRule(ModCommand.ENCHANT_BROKEN_BLOCK_DROP).set(value, level.getServer());
        }
        public static void setEnchantBreakBlock(Level level, boolean value) {
            if (level == null || level.isClientSide()) return;
            GameRules rules = level.getGameRules();
            rules.getRule(ModCommand.ENCHANT_BREAK_BLOCK).set(value, level.getServer());
        }
        public static void setAllowReactionExplode(Level level, boolean value) {
            if (level == null || level.isClientSide()) return;
            GameRules rules = level.getGameRules();
            rules.getRule(ModCommand.ALLOW_REACTION_EXPLODE).set(value, level.getServer());
        }
    }

    public static class Burning {

        /**
         * 检测实体是否在燃烧（有火焰动画）
         */
        public static boolean isBurning(ItemEntity entity) {
            return entity.isOnFire();
            // && entity.displayFireAnimation()
        }
        /**
         * 检测实体是否即将因燃烧（剩余燃烧时间小于tick）而消失
         * @param tick 刻
         * <p>
         *  掉落物燃烧消失的逻辑：
         *  1. 必须正在燃烧
         *  2. 剩余时间很少（通常<10 tick）
         *  3. 不是刚被点燃
         */
        public static boolean isAboutToVanishFromFire(ItemEntity entity, int tick) {
            return MAth.isInInterval(getAboutToVanishFromFire(entity), 0, tick, false, true);
        }
        public static boolean isAboutToVanishFromFire(ItemEntity entity) {
            return isAboutToVanishFromFire(entity, 10);
        }
        /**
         * 获取实体剩余燃烧tick
         */
        public static int getAboutToVanishFromFire(ItemEntity entity) {
            if (!isBurning(entity)) return -114;
            // 获取剩余燃烧时间
            return entity.getRemainingFireTicks();
        }


//        /**
//         * 检测实体是否在燃烧中（忽略刚开始燃烧的阶段）
//         * 可以设置一个最小燃烧时间阈值
//         */
//        public static boolean isBurningForAWhile(ItemEntity entity, int minTicks) {
//            if (!entity.isOnFire()) return false;
//
//            // 获取实体的最大燃烧时间
//            int maxFireTicks = entity.getEntityData().get(Entity.DATA_MAX_FIRE_TICKS);
//            int currentTicks = maxFireTicks - entity.getRemainingFireTicks();
//
//            return currentTicks >= minTicks;
//        }
//
//        /**
//         * 获取燃烧进度（0.0-1.0）
//         * 0 = 刚开始燃烧，1 = 即将消失
//         */
//        public static float getBurnProgress(ItemEntity entity) {
//            if (!entity.isOnFire()) return 0.0f;
//
//            // 获取实体的最大燃烧时间
//            Integer maxFireTicks = entity.getEntityData().get(Entity);
//            if (maxFireTicks == null || maxFireTicks <= 0) return 0.0f;
//
//            int remainingTicks = entity.getRemainingFireTicks();
//            return 1.0f - ((float) remainingTicks / maxFireTicks);
//        }
    }

    public static class Particle {
        /**
         * 生成一个基础粒子
         */
        public static void spawn(Level level, ParticleOptions particleType,
                                              double x, double y, double z,
                                              double vx, double vy, double vz) {
            if (level.isClientSide()) {
                level.addParticle(particleType, x, y, z, vx, vy, vz);
            }
        }
        /**
         * 生成自定义颜色的灰尘粒子
         * @param size 粒子大小
         *             <p>
         *             r, g, b 颜色分量 (0.0-1.0)
         */
        public static void spawnColoredDust(Level level, double x, double y, double z,
                                            float r, float g, float b, float size) {
            if (level.isClientSide()) {
                Vector3f color = new Vector3f(r, g, b);
                DustParticleOptions options = new DustParticleOptions(color, size);
                level.addParticle(options, x, y, z, 0, 0, 0);
            }
        }
        public static void spawnColoredDust(Level level, double x, double y, double z, double vx, double vy, double vz,
                                            float r, float g, float b, float size) {
            if (level.isClientSide()) {
                Vector3f color = new Vector3f(r, g, b);
                DustParticleOptions options = new DustParticleOptions(color, size);
                level.addParticle(options, x, y, z, vx, vy, vz);
            }
        }
        /**
         * 生成颜色渐变的灰尘粒子
         * @param level 世界实例
         * fromR, fromG, fromB 起始颜色
         * toR, toG, toB 结束颜色
         * @param size 粒子大小
         */
        public static void spawnGradientDust(Level level, double x, double y, double z,
                                             float fromR, float fromG, float fromB,
                                             float toR, float toG, float toB,
                                             float size) {
            if (level.isClientSide()) {
                Vector3f fromColor = new Vector3f(fromR, fromG, fromB);
                Vector3f toColor = new Vector3f(toR, toG, toB);
                DustColorTransitionOptions options =
                        new DustColorTransitionOptions(fromColor, toColor, size);
                level.addParticle(options, x, y, z, 0, 0, 0);
            }
        }
        public static void spawnGradientDust(Level level, double x, double y, double z, double vx, double vy, double vz,
                                             float fromR, float fromG, float fromB,
                                             float toR, float toG, float toB,
                                             float size) {
            if (level.isClientSide()) {
                Vector3f fromColor = new Vector3f(fromR, fromG, fromB);
                Vector3f toColor = new Vector3f(toR, toG, toB);
                DustColorTransitionOptions options =
                        new DustColorTransitionOptions(fromColor, toColor, size);
                level.addParticle(options, x, y, z, vx, vy, vz);
            }
        }
        /**
         * 生成圆形分布的粒子
         * @param level 世界实例
         * @param particleType 粒子类型
         * <p>
         * centerX, centerY, centerZ 圆心坐标
         * @param count 粒子数量
         * @param radius 圆半径
         * @param speed 粒子向外扩散的速度
         */
        public static void spawnCircle(Level level, ParticleOptions particleType,
                                                double centerX, double centerY, double centerZ,
                                                int count, double radius, double speed) {
            if (level.isClientSide()) {
                for (int i = 0; i < count; i++) {
                    double angle = (2 * Math.PI * i) / count;
                    double x = centerX + Math.cos(angle) * radius;
                    double z = centerZ + Math.sin(angle) * radius;

                    double vx = Math.cos(angle) * speed;
                    double vz = Math.sin(angle) * speed;

                    level.addParticle(particleType, x, centerY, z, vx, 0, vz);
                }
            }
        }
        /**
         * 生成球形分布的粒子
         * @param level 世界实例
         * @param particleType 粒子类型
         * <P></P>
         * centerX, centerY, centerZ 球心坐标
         * @param count 粒子数量
         * @param radius 球半径
         * @param speed 粒子向外扩散的速度
         */
        public static void spawnSphere(Level level, ParticleOptions particleType,
                                                double centerX, double centerY, double centerZ,
                                                int count, double radius, double speed) {
            if (level.isClientSide()) {
                for (int i = 0; i < count; i++) {
                    // 球面坐标
                    double theta = 2 * Math.PI * Math.random();
                    double phi = Math.acos(2 * Math.random() - 1);

                    double x = centerX + radius * Math.sin(phi) * Math.cos(theta);
                    double y = centerY + radius * Math.cos(phi);
                    double z = centerZ + radius * Math.sin(phi) * Math.sin(theta);

                    // 从中心向外扩散的速度
                    double vx = Math.sin(phi) * Math.cos(theta) * speed;
                    double vy = Math.cos(phi) * speed;
                    double vz = Math.sin(phi) * Math.sin(theta) * speed;

                    level.addParticle(particleType, x, y, z, vx, vy, vz);
                }
            }
        }
        /**
         * 生成连接两点的线条状粒子
         * @param level 世界实例
         * @param particleType 粒子类型
         * <p></p>
         * startX, startY, startZ 起点坐标
         * endX, endY, endZ 终点坐标
         * @param count 粒子数量（粒子数量 = 线条段数 + 1）
         * @param speed 粒子沿线条移动的速度
         */
        public static void spawnLine(Level level, ParticleOptions particleType,
                                              double startX, double startY, double startZ,
                                              double endX, double endY, double endZ,
                                              int count, double speed) {
            if (level.isClientSide()) {
                int segments = count - 1;
                double dx = (endX - startX) / segments;
                double dy = (endY - startY) / segments;
                double dz = (endZ - startZ) / segments;

                for (int i = 0; i < segments; i++) {
                    double x = startX + dx * i;
                    double y = startY + dy * i;
                    double z = startZ + dz * i;

                    level.addParticle(particleType, x, y, z, dx * speed, dy * speed, dz * speed);
                }
            }
        }
        /**
         * 生成方块破坏粒子
         * @param state 方块状态
         */
        public static void spawnBlockBreak(Level level, double x, double y, double z,
                                                    BlockState state) {
            if (level.isClientSide()) {
                level.addParticle(
                        new BlockParticleOption(ParticleTypes.BLOCK, state),
                        x, y, z,
                        0, 0, 0
                );
            }
        }

        /**
         * 生成物品掉落粒子
         * @param stack 物品堆
         */
        public static void spawnItemDrop(Level level, double x, double y, double z,
                                              ItemStack stack) {
            if (level.isClientSide()) {
                level.addParticle(
                        new ItemParticleOption(ParticleTypes.ITEM, stack),
                        x, y, z,
                        0, 0, 0
                );
            }
        }
        /**
         * 可视化扇形区域（生成粒子效果）
         */
        public static void visualizeSector(Level level, Player player, double maxDistance, float angleDegrees,
                                           int particleCount) {
            if (level.isClientSide()) {
                // 在扇形区域边缘生成粒子
                float halfAngle = angleDegrees / 2.0f;

                for (int i = 0; i < particleCount; i++) {
                    // 计算当前粒子的角度
                    float currentAngle = -halfAngle + (halfAngle * 2 * i / (float) particleCount);

                    // 将角度转换为弧度
                    double angleRad = Math.toRadians(- currentAngle + player.getYRot());

                    // 计算粒子位置
                    double x = player.getX() + Math.sin(angleRad) * maxDistance;
                    double z = player.getZ() + Math.cos(angleRad) * maxDistance;
                    double y = player.getY() + 0.5; // 玩家高度

                    // 生成粒子
                    level.addParticle(net.minecraft.core.particles.ParticleTypes.FLAME,
                            x, y, z, 0, 0, 0);
                }
            }
        }

        /**
         * 快捷生成一些粒子
         */
        public static void spawnParticles(Level level, BlockPos pos) {
            if (level == null || !level.isClientSide()) return;

            double centerX = pos.getX() + 0.5;
            double centerY = pos.getY() + 1.0;
            double centerZ = pos.getZ() + 0.5;

            for (int i = 0; i < 10; i++) {
                double offsetX = (level.random.nextDouble() - 0.5) * 1.5;
                double offsetY = level.random.nextDouble() * 1.5;
                double offsetZ = (level.random.nextDouble() - 0.5) * 1.5;

                spawn(level, ParticleTypes.ENCHANT,
                        centerX + offsetX, centerY + offsetY, centerZ + offsetZ,
                        0, 0, 0
                );
            }
        }
        /**
         * 服务器发送粒子给所有玩家
         * @param level 世界实例
         * @param particleType 粒子类型
         * <p>
         * x, y, z 中心位置
         * @param count 粒子数量
         * <p>
         * dx, dy, dz 随机偏移范围
         * @param speed 基础速度
         */
        public static void send(Level level, ParticleOptions particleType,
                                              double x, double y, double z,
                                              int count, double dx, double dy, double dz,
                                              double speed) {
            if (!level.isClientSide() && level instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(
                        particleType,
                        x, y, z,
                        count,
                        dx, dy, dz,
                        speed
                );
            }
        }
        public static void sendGradientDust(Level level, double x, double y, double z, double dx, double dy, double dz,
                                                                int count,
                                                                float fromR, float fromG, float fromB,
                                                                float toR, float toG, float toB,
                                                                float size, double speed) {
            if (!level.isClientSide() && level instanceof ServerLevel serverLevel) {
                Vector3f fromColor = new Vector3f(fromR, fromG, fromB);
                Vector3f toColor = new Vector3f(toR, toG, toB);
                DustColorTransitionOptions options =
                        new DustColorTransitionOptions(fromColor, toColor, size);
                serverLevel.sendParticles(
                        options,
                        x, y, z,
                        count,
                        dx, dy, dz,
                        speed
                );
            }
        }
        /**
         * 服务器发送粒子给特定玩家
         */
        public static void sendToPlayer(
                                         ServerPlayer player,
                                         ParticleOptions particleType,
                                         double x, double y, double z,
                                         int count, double dx, double dy, double dz,
                                         double speed, boolean force) {
            player.connection.send(
                    new ClientboundLevelParticlesPacket(
                            particleType,
                            force,  // 是否强制显示
                            x, y, z,
                            (float)dx, (float)dy, (float)dz,
                            (float)speed,
                            count
                    )
            );
        }
        /**
         * 爆炸效果
         */
        public static void spawnExplosionEffect(Level level, double x, double y, double z,
                                                float size) {
            // 爆炸核心
            send(level, ParticleTypes.EXPLOSION, x, y, z, 1, 0, 0, 0, 0);

            // 爆炸烟雾
            send(level, ParticleTypes.EXPLOSION_EMITTER, x, y, z,
                    (int)(size * 20), size, size, size, 0.5);

            // 烟雾扩散
            spawnSphere(level, ParticleTypes.SMOKE, x, y, z,
                    (int)(size * 50), size * 2, 0.1);
        }
        /**
         * 火焰效果
         */
        public static void spawnFireEffect(Level level, double x, double y, double z,
                                           float intensity) {
            int count = (int)(intensity * 20);

            // 火焰粒子
            for (int i = 0; i < count; i++) {
                double offsetX = (Math.random() - 0.5) * intensity;
                double offsetY = Math.random() * intensity * 2;
                double offsetZ = (Math.random() - 0.5) * intensity;

                double vx = (Math.random() - 0.5) * 0.02;
                double vy = Math.random() * 0.05 + 0.02;
                double vz = (Math.random() - 0.5) * 0.02;

                level.addParticle(ParticleTypes.FLAME,
                        x + offsetX, y + offsetY, z + offsetZ,
                        vx, vy, vz);
            }

            // 烟雾
            for (int i = 0; i < count / 2; i++) {
                double offsetX = (Math.random() - 0.5) * intensity * 1.5;
                double offsetY = Math.random() * intensity * 2.5;
                double offsetZ = (Math.random() - 0.5) * intensity * 1.5;

                level.addParticle(ParticleTypes.SMOKE,
                        x + offsetX, y + offsetY, z + offsetZ,
                        0, 0.01, 0);
            }
        }
        /**
         * 魔法效果（适合附魔、技能等）
         */
        public static void spawnMagicEffect(Level level, double x, double y, double z,
                                            float r, float g, float b) {
            if (level.isClientSide()) {
                // 中心闪烁
                spawnColoredDust(level, x, y, z, r, g, b, 2.0f);

                // 圆形扩散
                spawnCircle(level, ParticleTypes.ENCHANT,
                        x, y, z, 16, 1.0, 0.05);

                // 上升粒子
                for (int i = 0; i < 10; i++) {
                    double offsetX = (Math.random() - 0.5) * 0.5;
                    double offsetZ = (Math.random() - 0.5) * 0.5;

                    Vector3f color = new Vector3f(r, g, b);
                    level.addParticle(
                            new DustParticleOptions(color, 1.0f),
                            x + offsetX, y, z + offsetZ,
                            0, 0.1 + Math.random() * 0.1, 0
                    );
                }
            }
        }
        /**
         * 轨迹效果（如飞行物轨迹）
         */
        public static void spawnTrailEffect(Level level, Vec3 start, Vec3 end,
                                            ParticleOptions particleType,
                                            int density, double spacing) {
            if (level.isClientSide()) {
                Vec3 direction = end.subtract(start).normalize();
                double distance = start.distanceTo(end);
                int steps = (int)(distance / spacing);

                for (int i = 0; i <= steps; i++) {
                    double t = (double)i / steps;
                    Vec3 pos = start.add(direction.scale(t * distance));

                    // 在轨迹上生成粒子
                    for (int j = 0; j < density; j++) {
                        double offsetX = (Math.random() - 0.5) * 0.2;
                        double offsetY = (Math.random() - 0.5) * 0.2;
                        double offsetZ = (Math.random() - 0.5) * 0.2;

                        double vx = (Math.random() - 0.5) * 0.01;
                        double vy = (Math.random() - 0.5) * 0.01;
                        double vz = (Math.random() - 0.5) * 0.01;

                        level.addParticle(particleType,
                                pos.x + offsetX, pos.y + offsetY, pos.z + offsetZ,
                                vx, vy, vz);
                    }
                }
            }
        }
        /**
         * 获取随机颜色
         */
        public static Vector3f getRandomColor() {
            return new Vector3f(
                    (float)Math.random(),
                    (float)Math.random(),
                    (float)Math.random()
            );
        }
        /**
         * 获取彩虹颜色（根据时间变化）
         */
        public static Vector3f getRainbowColor(long time) {
            float hue = (System.currentTimeMillis() % 6000) / 6000.0f;
            Color color = Color.getHSBColor(hue, 1.0f, 1.0f);
            return new Vector3f(
                    color.getRed() / 255.0f,
                    color.getGreen() / 255.0f,
                    color.getBlue() / 255.0f
            );
        }
    }

    public static class EntityWay {

        // ==================== 核心方法 ====================

        /**
         * 获取玩家前方扇形区域内最近的实体
         *
         * @param player 玩家实体
         * @param maxDistance 最大距离（米/方块）
         * @param angleDegrees 角度范围（度数），例如 30 表示玩家前方左右各30度
         * @return 符合条件的最近实体，如果没有则返回 null
         */
        @Nullable
        public static Entity getNearestEntityInFront(Player player, double maxDistance, float angleDegrees) {
            return getNearestEntityInFront(player, maxDistance, angleDegrees, null);
        }

        /**
         * 获取玩家前方扇形区域内最近的实体（带过滤条件）
         *
         * @param player 玩家实体
         * @param maxDistance 最大距离
         * @param angleDegrees 角度范围
         * @param filter 实体过滤器，可以按类型、队伍等过滤
         * @return 符合条件的最近实体
         */
        @Nullable
        public static Entity getNearestEntityInFront(Player player, double maxDistance, float angleDegrees,
                                                     @Nullable Predicate<Entity> filter) {
            if (player == null) {
                return null;
            }

            // 构建扇形区域AABB（性能优化）
            AABB searchArea = createSectorAABB(player, maxDistance);

            // 收集区域内的所有实体
            List<Entity> entities = player.level().getEntities(player, searchArea, getDefaultFilter(player, filter));

            // 如果没有实体，直接返回null
            if (entities.isEmpty()) {
                return null;
            }

            // 如果有多个实体，按规则排序选择
            return selectBestEntity(player, entities, maxDistance, angleDegrees);
        }

        // ==================== 辅助方法 ====================

        /**
         * 创建扇形区域的AABB（用于快速筛选）
         */
        private static AABB createSectorAABB(Player player, double maxDistance) {
            Vec3 playerPos = player.position();
            double radius = maxDistance;

            // 创建一个以玩家为中心，半径为maxDistance的立方体区域
            // 这是第一次粗略筛选，后续会进行精确的扇形和距离判断
            return new AABB(
                    playerPos.x - radius, playerPos.y - radius, playerPos.z - radius,
                    playerPos.x + radius, playerPos.y + radius, playerPos.z + radius
            );
        }

        /**
         * 获取默认过滤器（排除玩家自己）
         */
        private static Predicate<Entity> getDefaultFilter(Player player, @Nullable Predicate<Entity> customFilter) {
            Predicate<Entity> baseFilter = entity ->
                    entity != null &&
                            entity != player &&
                            entity.isAlive() &&
                            !entity.isSpectator();

            if (customFilter != null) {
                return baseFilter.and(customFilter);
            }

            return baseFilter;
        }

        /**
         * 从实体列表中选择最佳实体
         */
        @Nullable
        private static Entity selectBestEntity(Player player, List<Entity> entities,
                                               double maxDistance, float angleDegrees) {
            // 转换为流，进行筛选和排序
            Optional<Entity> bestEntity = entities.stream()
                    // 筛选在扇形区域内的实体
                    .filter(entity -> isInSector(player, entity, maxDistance, angleDegrees))
                    // 按照规则排序
                    .min(createEntityComparator(player));

            return bestEntity.orElse(null);
        }

        /**
         * 判断实体是否在玩家前方的扇形区域内
         */
        private static boolean isInSector(Player player, Entity target, double maxDistance, float angleDegrees) {
            // 1. 检查距离
            double distance = player.distanceTo(target);
            if (distance > maxDistance) {
                return false;
            }

            // 2. 检查角度
            float angle = calculateAngleBetween(player, target);
            return angle <= angleDegrees / 2.0f; // 除以2是因为参数是总角度
        }

        /**
         * 计算玩家看向与目标方向的夹角（度数）
         */
        private static float calculateAngleBetween(Player player, Entity target) {
            // 玩家视线方向向量（水平方向）
            Vec3 lookVec = player.getLookAngle();
            Vec3 lookVecHorizontal = new Vec3(lookVec.x, 0, lookVec.z).normalize();

            // 玩家到目标的向量（水平方向）
            Vec3 toTarget = target.position().subtract(player.position());
            Vec3 toTargetHorizontal = new Vec3(toTarget.x, 0, toTarget.z).normalize();

            // 如果目标就在玩家位置，返回0度
            if (toTargetHorizontal.lengthSqr() < 1e-6 || lookVecHorizontal.lengthSqr() < 1e-6) {
                return 0.0f;
            }

            // 计算点积并限制在[-1, 1]范围内
            double dot = lookVecHorizontal.dot(toTargetHorizontal);
            dot = Math.max(-1.0, Math.min(1.0, dot));

            // 计算夹角（弧度转度数）
            return (float) Math.toDegrees(Math.acos(dot));
        }

        /**
         * 创建实体比较器（按距离和角度排序）
         */
        private static Comparator<Object> createEntityComparator(Player player) {
            return Comparator
                    // 首先按距离排序
                    .comparingDouble(entity -> player.distanceToSqr((Entity) entity))
                    // 如果距离相等（在很小误差范围内），按角度排序
                    .thenComparingDouble(entity -> calculateAngleBetween(player, (Entity) entity));
        }

        // ==================== 高级功能 ====================

        /**
         * 获取玩家前方最近的生物实体（排除其他实体类型）
         */
        @Nullable
        public static LivingEntity getNearestLivingEntityInFront(Player player, double maxDistance, float angleDegrees) {
            return getNearestLivingEntityInFront(player, maxDistance, angleDegrees, null);
        }

        /**
         * 获取玩家前方最近的生物实体（带过滤条件）
         */
        @Nullable
        public static LivingEntity getNearestLivingEntityInFront(Player player, double maxDistance, float angleDegrees,
                                                                 @Nullable Predicate<LivingEntity> filter) {
            Predicate<Entity> entityFilter = entity -> {
                if (!(entity instanceof LivingEntity living)) {
                    return false;
                }
                return filter == null || filter.test(living);
            };

            Entity result = getNearestEntityInFront(player, maxDistance, angleDegrees, entityFilter);
            return result instanceof LivingEntity ? (LivingEntity) result : null;
        }

        /**
         * 获取玩家前方最近的敌对生物
         */
        @Nullable
        public static LivingEntity getNearestHostileInFront(Player player, double maxDistance, float angleDegrees) {
            return getNearestLivingEntityInFront(player, maxDistance, angleDegrees,
                    living -> !living.isAlliedTo(player) && living.canBeSeenByAnyone());
        }

        /**
         * 获取玩家前方最近的友好生物
         */
        @Nullable
        public static LivingEntity getNearestFriendlyInFront(Player player, double maxDistance, float angleDegrees) {
            return getNearestLivingEntityInFront(player, maxDistance, angleDegrees,
                    living -> living.isAlliedTo(player));
        }

        // ==================== 调试和可视化 ====================

        /**
         * 调试：获取扇形区域内的所有实体列表（按优先级排序）
         */
        public static List<Entity> getAllEntitiesInFront(Player player, double maxDistance, float angleDegrees) {
            if (player == null || player.level() == null) {
                return List.of();
            }

            AABB searchArea = createSectorAABB(player, maxDistance);
            List<Entity> entities = player.level().getEntities(player, searchArea,
                    entity -> entity != null && entity != player && entity.isAlive());

            return entities.stream()
                    .filter(entity -> isInSector(player, entity, maxDistance, angleDegrees))
                    .sorted(createEntityComparator(player))
                    .toList();
        }

        /**
         * 获取实体的详细信息（用于调试）
         */
        public static String getEntityInfo(Player player, Entity target) {
            if (player == null || target == null) {
                return "Invalid entities";
            }

            double distance = player.distanceTo(target);
            float angle = calculateAngleBetween(player, target);
            String type = target.getType().getDescription().getString();

            return String.format("%s - 距离: %.2f, 角度: %.1f°, 位置: (%.1f, %.1f, %.1f)",
                    type, distance, angle,
                    target.getX(), target.getY(), target.getZ());
        }



        /**
         *  获取两实体的偏航角差值
         */
        public static float getYawDiffer(Entity entity1, Entity entity2){
            return Math.abs(getYaw(entity1) - getYaw(entity2));
        }

        /**
         *  获取两实体的俯仰角差值
         */
        public static float getPitchDiffer(Entity entity1, Entity entity2){
            return Math.abs(getPitch(entity1) - getPitch(entity2));
        }

        /**
         * 获取实体偏航角（规范化到0-360）
         */
        public static float getYaw(Entity entity) {
            return Mth.wrapDegrees(entity.getYRot());
        }

        /**
         * 获取实体俯仰角（规范化到-90到90）
         */
        public static float getPitch(Entity entity) {
            return Mth.clamp(entity.getXRot(), -90.0f, 90.0f);
        }

        /**
         * 获取旋转角度（返回数组：[yaw, pitch]）
         */
        public static float[] getRotation(Entity entity) {
            return new float[] {
                    getYaw(entity),
                    getPitch(entity)
            };
        }

        /**
         * 获取视线方向向量（3D）
         */
        public static Vec3 getLookVector(Entity entity) {
            return entity.getLookAngle();
        }

        /**
         * 获取水平方向向量（2D）
         */
        public static Vec3 getHorizontalLookVector(Entity entity) {
            Vec3 lookVec = entity.getLookAngle();
            return new Vec3(lookVec.x, 0, lookVec.z).normalize();
        }

        /**
         * 获取背后方向向量
         */
        public static Vec3 getBackwardVector(Entity entity) {
            return getHorizontalLookVector(entity).reverse();
        }

        /**
         * 获取右侧方向向量
         */
        public static Vec3 getRightVector(Entity entity) {
            Vec3 forward = getHorizontalLookVector(entity);
            // 叉积：forward × up = right
            return forward.cross(new Vec3(0, 1, 0)).normalize();
        }

        /**
         * 获取左侧方向向量
         */
        public static Vec3 getLeftVector(Entity entity) {
            return getRightVector(entity).reverse();
        }

        // ==================== 方向枚举 ====================

        /**
         * 获取实体朝向的Direction（4方向）
         */
        public static Direction getCardinalDirection(Entity entity) {
            float yaw = getYaw(entity);

            if (yaw >= 315 || yaw < 45) return Direction.SOUTH;
            if (yaw >= 45 && yaw < 135) return Direction.WEST;
            if (yaw >= 135 && yaw < 225) return Direction.NORTH;
            return Direction.EAST; // 225-315
        }

        /**
         * 获取实体朝向的Direction（8方向）
         */
        public static Direction getOrdinalDirection(Entity entity) {
            float yaw = getYaw(entity) + 22.5f;

            if (yaw < 0) yaw += 360;
            int index = (int)(yaw / 45.0f) % 8;

            return Direction.from2DDataValue(index);
        }

        /**
         * 判断实体是否朝向上/下
         */
        public static boolean isLookingUp(Entity entity) {
            return entity.getXRot() < -45.0f;
        }

        public static boolean isLookingDown(Entity entity) {
            return entity.getXRot() > 45.0f;
        }

        public static boolean isLookingHorizontal(Entity entity) {
            float pitch = Math.abs(entity.getXRot());
            return pitch <= 30.0f;
        }

        // ==================== 方向判断 ====================

        /**
         * 判断实体是否朝向某个方向（容忍角度）
         * @param tolerance 容忍角度（度）
         */
        public static boolean isFacingDirection(Entity entity, Direction targetDir, float tolerance) {
            Direction currentDir = getCardinalDirection(entity);

            if (currentDir == targetDir) return true;

            // 检查相邻方向
            float yaw = getYaw(entity);
            float targetYaw = getYawFromDirection(targetDir);

            float diff = Math.abs(Mth.wrapDegrees(yaw - targetYaw));
            return diff <= tolerance;
        }

        /**
         * 判断实体是否看向某个位置
         */
        public static boolean isLookingAt(Entity entity, Vec3 targetPos, float toleranceDegrees) {
            Vec3 eyePos = entity.getEyePosition();
            Vec3 toTarget = targetPos.subtract(eyePos).normalize();
            Vec3 lookVec = entity.getLookAngle();

            double dot = lookVec.dot(toTarget);
            double angleRad = Math.acos(dot);
            double angleDeg = Math.toDegrees(angleRad);

            return angleDeg <= toleranceDegrees;
        }

        // ==================== 方向计算 ====================

        /**
         * 计算从实体位置到目标位置的方向向量
         */
        public static Vec3 getDirectionTo(Entity entity, Vec3 targetPos) {
            Vec3 entityPos = entity.position();
            return targetPos.subtract(entityPos).normalize();
        }

        /**
         * 计算从实体到目标的yaw角度
         */
        public static float getYawTo(Entity entity, Vec3 targetPos) {
            Vec3 entityPos = entity.position();
            double dx = targetPos.x - entityPos.x;
            double dz = targetPos.z - entityPos.z;

            // Minecraft的角度系统：0=南，90=西，180=北，270=东
            double yaw = Math.toDegrees(Math.atan2(dx, dz));

            // 转换为Minecraft的yaw系统
            return (float)Mth.wrapDegrees(yaw);
        }

        /**
         * 计算从实体到目标的pitch角度
         */
        public static float getPitchTo(Entity entity, Vec3 targetPos) {
            Vec3 entityPos = entity.position();
            double dx = targetPos.x - entityPos.x;
            double dy = targetPos.y - (entityPos.y + entity.getEyeHeight());
            double dz = targetPos.z - entityPos.z;

            double horizontalDistance = Math.sqrt(dx * dx + dz * dz);
            double pitch = -Math.toDegrees(Math.atan2(dy, horizontalDistance));

            return (float)Mth.clamp(pitch, -90.0, 90.0);
        }

        // ==================== 方向转换 ====================

        /**
         * 将Direction转换为yaw角度
         */
        public static float getYawFromDirection(Direction direction) {
            return switch (direction) {
                case SOUTH -> 0.0f;
                case WEST -> 90.0f;
                case NORTH -> 180.0f;
                case EAST -> 270.0f;
                default -> 0.0f;
            };
        }

        /**
         * 将yaw转换为方向字符串
         */
        public static String getDirectionName(float yaw) {
            Direction dir = getCardinalDirectionFromYaw(yaw);

            return switch (dir) {
                case SOUTH -> "南";
                case WEST -> "西";
                case NORTH -> "北";
                case EAST -> "东";
                default -> "未知";
            };
        }

        private static Direction getCardinalDirectionFromYaw(float yaw) {
            float normalized = Mth.wrapDegrees(yaw);

            if (normalized >= 315 || normalized < 45) return Direction.SOUTH;
            if (normalized >= 45 && normalized < 135) return Direction.WEST;
            if (normalized >= 135 && normalized < 225) return Direction.NORTH;
            return Direction.EAST;
        }

        // ==================== 运动方向 ====================

        /**
         * 获取实体运动方向
         */
        public static Vec3 getMovementDirection(Entity entity) {
            Vec3 motion = entity.getDeltaMovement();
            if (motion.lengthSqr() < 0.0001) {
                return Vec3.ZERO;
            }
            return motion.normalize();
        }

        /**
         * 判断实体是否在移动
         */
        public static boolean isMoving(Entity entity) {
            return entity.getDeltaMovement().lengthSqr() > 0.001;
        }

        /**
         * 获取实体移动速度
         */
        public static double getMovementSpeed(Entity entity) {
            return entity.getDeltaMovement().length();
        }

        // ==================== 应用场景 ====================

        /**
         * 使实体看向目标位置
         */
        public static void lookAt(Entity entity, Vec3 targetPos) {
            float yaw = getYawTo(entity, targetPos);
            float pitch = getPitchTo(entity, targetPos);

            entity.setYRot(yaw);
            entity.setXRot(pitch);
            entity.yRotO = yaw;
            entity.xRotO = pitch;
        }

        /**
         * 使实体看向另一个实体
         */
        public static void lookAtEntity(Entity source, Entity target) {
            lookAt(source, target.position());
        }

        /**
         * 平滑转向（插值）
         */
        public static void smoothLookAt(Entity entity, Vec3 targetPos, float delta) {
            float currentYaw = entity.getYRot();
            float currentPitch = entity.getXRot();

            float targetYaw = getYawTo(entity, targetPos);
            float targetPitch = getPitchTo(entity, targetPos);

            // 插值计算
            float newYaw = Mth.rotLerp(delta, currentYaw, targetYaw);
            float newPitch = Mth.rotLerp(delta, currentPitch, targetPitch);

            entity.setYRot(newYaw);
            entity.setXRot(newPitch);
        }

        public static void spawnItemEntity(Level level, BlockPos pos, ItemStack itemStack){
            spawnItemEntity(level, pos, itemStack, 0.1);
        }
        public static void spawnItemEntity(Level level, BlockPos pos, ItemStack itemStack, double spread){
            ItemEntity itemEntity = new ItemEntity(level, pos.getX(), pos.getY(), pos.getZ(), itemStack);
            spawnItemEntity(level, itemEntity, spread);
        }
        public static void spawnItemEntity(Level level, ItemEntity itemEntity){
            spawnItemEntity(level, itemEntity, 0.1);
        }
        public static void spawnItemEntity(Level level, ItemEntity itemEntity, double spread){
            itemEntity.setDefaultPickUpDelay();
            itemEntity.setDeltaMovement(
                    (level.random.nextDouble() - 0.5) * spread,
                    randomInRange(level, 0.2, 0.3),
                    (level.random.nextDouble() - 0.5) * spread
            );
            level.addFreshEntity(itemEntity);
        }

        /**
         * 检查两个实体是否接触（边界框相交）
         */
        public static boolean areEntitiesColliding(Entity entity1, Entity entity2) {
            return entity1.getBoundingBox().intersects(entity2.getBoundingBox());
        }

        /**
         * 检查两个实体是否在指定距离内
         */
        public static boolean areEntitiesWithinDistance(Entity entity1, Entity entity2, double distance) {
            return entity1.distanceTo(entity2) <= distance;
        }

        /**
         * 检查实体是否与另一个实体的膨胀边界框相交
         */
        public static boolean isEntityTouchingWithMargin(Entity entity1, Entity entity2, double margin) {
            AABB expandedBox1 = entity1.getBoundingBox().inflate(margin);
            AABB expandedBox2 = entity2.getBoundingBox().inflate(margin);
            return expandedBox1.intersects(expandedBox2);
        }
    }

    /*
    {
    Brain: {memories: {}}, HurtByTimestamp: 0, Attributes: [
    {Base: 0.08d, Name: "forge:entity_gravity"}, {Base: 0.0d, Name: "minecraft:generic.armor"},
    {Base: 0.0d, Name: "minecraft:generic.armor_toughness"},
    {Base: 0.0d, Name: "minecraft:generic.knockback_resistance"},
    {Base: 0.0d, Name: "forge:step_height_addition"},
    {Base: 0.699999988079071d, Name: "minecraft:generic.movement_speed"}],
    Invulnerable: 0b, FallFlying: 0b, ShowArms: 0b, PortalCooldown: 0, AbsorptionAmount: 0.0f,
    FallDistance: 0.0f, DisabledSlots: 0, CanUpdate: 1b, DeathTime: 0s, Pose: {}, Invisible: 0b,
    UUID: [I; -345649566, -227851149, -1952624807, 399656419], Motion: [0.0d, -0.0784000015258789d, 0.0d],
    Small: 0b, Health: 20.0f, Air: 300s, OnGround: 1b, Rotation: [0.0f, 0.0f], HandItems: [{}, {}],
    Pos: [5.5d, -60.0d, 23.5d], Fire: -1s,
    ArmorItems: [
    {id: "minecraft:netherite_boots", Count: 1b, tag: {
    Damage: 0, Trim: {material: "minecraft:quartz", pattern: "minecraft:snout"}
    }},
    {id: "minecraft:iron_leggings", Count: 1b, tag: {
    Damage: 0, Trim: {material: "minecraft:iron", pattern: "minecraft:vex"}
    }},
    {id: "minecraft:leather_chestplate", Count: 1b, tag: {
    Damage: 0, Trim: {material: "minecraft:quartz", pattern: "minecraft:tide"},
    display: {color: 1908001}
    }},
    {id: "minecraft:leather_helmet", Count: 1b, tag: {
    Damage: 0, Trim: {material: "minecraft:netherite", pattern: "minecraft:sentry"},
    display: {color: 10329495}
    }}
    ],
    NoBasePlate: 0b, HurtTime: 0s
    }
     */

    public static class ArmorChecker {

        /**
         * 检查玩家是否穿着符合条件的完整套装
         */
        public static boolean isWearingFullArmorSet(Player player) {
            return checkHelmet(player) &&
                    checkChestplate(player) &&
                    checkLeggings(player) &&
                    checkBoots(player);
        }


        /**
         * 检查头盔：皮革头盔，哨兵纹饰，下界合金质升级，淡灰色(#9D9D97)
         */
        private static boolean checkHelmet(Player player) {
            ItemStack helmet = player.getItemBySlot(EquipmentSlot.HEAD);

            // 检查是否为皮革头盔
            if (!helmet.is(Items.LEATHER_HELMET)) return false;

            CompoundTag tag = helmet.getTag();
            if (tag == null) return false;

            // 检查颜色（淡灰色 #9D9D97 = 10329495）
            if (tag.contains("display")) {
                CompoundTag display = tag.getCompound("display");
                if (display.contains("color")) {
                    int color = display.getInt("color");
                    if (color != 10329495) return false; // #9D9D97
                } else {
                    return false;
                }
            } else {
                return false;
            }

            // 检查纹饰
            if (tag.contains("Trim")) {
                CompoundTag trim = tag.getCompound("Trim");
                String pattern = trim.getString("pattern");
                String material = trim.getString("material");

                // 哨兵纹饰，下界合金质
                return "minecraft:sentry".equals(pattern) &&
                        "minecraft:netherite".equals(material);
            }

            return false;
        }

        /**
         * 检查胸甲：皮革胸甲，潮汐纹饰，石英质升级，黑色(#1D1D21)
         */
        private static boolean checkChestplate(Player player) {
            ItemStack chestplate = player.getItemBySlot(EquipmentSlot.CHEST);

            // 检查是否为皮革胸甲
            if (!chestplate.is(Items.LEATHER_CHESTPLATE)) return false;

            CompoundTag tag = chestplate.getTag();
            if (tag == null) return false;

            // 检查颜色（黑色 #1D1D21 = 1908001）
            if (tag.contains("display")) {
                CompoundTag display = tag.getCompound("display");
                if (display.contains("color")) {
                    int color = display.getInt("color");
                    if (color != 1908001) return false; // #1D1D21
                } else {
                    return false;
                }
            } else {
                return false;
            }

            // 检查纹饰
            if (tag.contains("Trim")) {
                CompoundTag trim = tag.getCompound("Trim");
                String pattern = trim.getString("pattern");
                String material = trim.getString("material");

                // 潮汐纹饰，石英质
                return "minecraft:tide".equals(pattern) &&
                        "minecraft:quartz".equals(material);
            }

            return false;
        }

        /**
         * 检查护腿：铁护腿，恼鬼纹饰，铁质升级
         */
        private static boolean checkLeggings(Player player) {
            ItemStack leggings = player.getItemBySlot(EquipmentSlot.LEGS);

            // 检查是否为铁护腿
            if (!leggings.is(Items.IRON_LEGGINGS)) return false;

            CompoundTag tag = leggings.getTag();
            if (tag == null) return false;

            // 检查纹饰
            if (tag.contains("Trim")) {
                CompoundTag trim = tag.getCompound("Trim");
                String pattern = trim.getString("pattern");
                String material = trim.getString("material");

                // 恼鬼纹饰，铁质
                return "minecraft:vex".equals(pattern) &&
                        "minecraft:iron".equals(material);
            }

            return false;
        }

        /**
         * 检查靴子：下界合金靴子，猪鼻纹饰，石英质升级
         */
        private static boolean checkBoots(Player player) {
            ItemStack boots = player.getItemBySlot(EquipmentSlot.FEET);

            // 检查是否为下界合金靴子
            if (!boots.is(Items.NETHERITE_BOOTS)) return false;

            CompoundTag tag = boots.getTag();
            if (tag == null) return false;

            // 检查纹饰
            if (tag.contains("Trim")) {
                CompoundTag trim = tag.getCompound("Trim");
                String pattern = trim.getString("pattern");
                String material = trim.getString("material");

                // 猪鼻纹饰，石英质
                return "minecraft:snout".equals(pattern) &&
                        "minecraft:quartz".equals(material);
            }

            return false;
        }

        /**
         * 调试方法：打印盔甲详细信息
         */
        public static void debugArmor(Player player) {
            for (EquipmentSlot slot : new EquipmentSlot[]{
                    EquipmentSlot.HEAD, EquipmentSlot.CHEST,
                    EquipmentSlot.LEGS, EquipmentSlot.FEET
            }) {
                ItemStack stack = player.getItemBySlot(slot);
                System.out.println(slot.getName() + ": " + stack);
                if (stack.hasTag()) {
                    System.out.println("  NBT: " + stack.getTag());
                }
            }
        }
    }

    public static class Enchantment {

        /**
     * 根据物品堆栈计算实际应用的线性转换系数
     * @param stack 物品堆栈
     * @param baseFactor 基础系数（来自KineticData.LINEAR_CONVERSION_FACTOR）
     * @return 应用附魔加成后的系数
     */
    public static float getModifiedLinearFactor(ItemStack stack, float baseFactor) {
        if (!(stack.getItem() instanceof KineticWeapon)) {
            return baseFactor;
        }

        // 获取Sprint附魔的等级
        int sprintLevel = EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.SPRINT.get(), stack);
        if (sprintLevel > 0) {
            float bonus = SprintEnchantment.getLinearFactorBonus(sprintLevel);
            return baseFactor + bonus;
        }

        return baseFactor;
    }

        /**
         * 根据物品堆栈计算实际应用的重力转换系数
         * @param stack 物品堆栈
         * @param baseFactor 基础系数（来自KineticData.GRAVITY_CONVERSION_FACTOR）
         * @return 应用附魔加成后的系数
         */
        public static float getModifiedGravityFactor(ItemStack stack, float baseFactor) {
            if (!(stack.getItem() instanceof KineticWeapon)) {
                return baseFactor;
            }

            // 获取Advent附魔的等级
            int adventLevel = EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.ADVENT.get(), stack);
            if (adventLevel > 0) {
                float bonus = AdventEnchantment.getGravityFactorBonus(adventLevel);
                return baseFactor + bonus;
            }

            return baseFactor;
        }

        /**
         * 根据物品堆栈计算实际应用的动能衰减速率
         * @param stack 物品堆栈
         * @param baseDecayRate 基础衰减速率（来自KineticData.KINETIC_DECAY_RATE）
         * @return 应用附魔加成后的衰减速率
         */
        public static float getModifiedDecayRate(ItemStack stack, float baseDecayRate) {
            if (!(stack.getItem() instanceof KineticWeapon)) {
                return baseDecayRate;
            }

            // 获取Balance附魔的等级
            int balanceLevel = EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.BALANCE.get(), stack);
            if (balanceLevel > 0) {
                float reduction = BalanceEnchantment.getKineticDecayRate(balanceLevel);
                return Math.max(0, baseDecayRate - reduction);
            }

            return baseDecayRate;
        }

        /**
         * 根据物品堆栈计算实际应用的伤害转化系数
         * @param stack 物品堆栈
         * @param baseFactor 基础系数（来自KineticData.DAMAGE_CONVERSION_FACTOR）
         * @return 应用附魔加成后的系数
         */
        public static float getModifiedDamageFactor(ItemStack stack, float baseFactor) {
            if (!(stack.getItem() instanceof KineticWeapon)) {
                return baseFactor;
            }

            // 获取Conversion附魔的等级
            int conversionLevel = EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.CONVERSION.get(), stack);
            if (conversionLevel > 0) {
                float bonus = ConversionEnchantment.getDamageFactorBonus(conversionLevel);
                return baseFactor + bonus;
            }

            return baseFactor;
        }

        /**
         * 根据物品堆栈计算实际应用的最大动能值
         * @param stack 物品堆栈
         * @param baseMaxEnergy 基础最大动能值（来自KineticData.MAX_KINETIC_ENERGY）
         * @return 应用附魔加成后的最大动能值
         */
        public static float getModifiedMaxKineticEnergy(ItemStack stack, float baseMaxEnergy) {
            if (!(stack.getItem() instanceof KineticWeapon)) {
                return baseMaxEnergy;
            }

            // 获取Exceeded附魔的等级
            int exceededLevel = EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.EXCEEDED.get(), stack);
            if (exceededLevel > 0) {
                float bonus = ExceededEnchantment.getKineticEnergyBonus(exceededLevel);
                return baseMaxEnergy + bonus;
            }

            return baseMaxEnergy;
        }

        /**
         * 获取物品上所有动能相关的附魔信息
         */
        public static String getKineticEnchantmentInfo(ItemStack stack) {
            if (!(stack.getItem() instanceof KineticWeapon)) {
                return "无动能附魔";
            }

            StringBuilder info = new StringBuilder();

            // 检查Sprint附魔
            int sprintLevel = EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.SPRINT.get(), stack);
            if (sprintLevel > 0) {
                info.append("§b疾跑 §7(").append(sprintLevel).append("级)§r\n");
                info.append("  线性动能效率 +").append(String.format("%.5f",
                        SprintEnchantment.getLinearFactorBonus(sprintLevel))).append("\n");
            }

            // 检查Advent附魔
            int adventLevel = EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.ADVENT.get(), stack);
            if (adventLevel > 0) {
                info.append("§c冒险 §7(").append(adventLevel).append("级)§r\n");
                info.append("  重力动能效率 +").append(String.format("%.2f",
                        AdventEnchantment.getGravityFactorBonus(adventLevel))).append("\n");
            }

            // 检查Balance附魔
            int balanceLevel = EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.BALANCE.get(), stack);
            if (balanceLevel > 0) {
                info.append("§a平衡 §7(").append(balanceLevel).append("级)§r\n");
                info.append("  动能衰减 -").append(String.format("%.2f%%",
                        BalanceEnchantment.getKineticDecayRate(balanceLevel))).append("\n");
            }

            // 检查Conversion附魔
            int conversionLevel = EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.CONVERSION.get(), stack);
            if (conversionLevel > 0) {
                info.append("§d转化 §7(").append(conversionLevel).append("级)§r\n");
                info.append("  伤害转化 +").append(String.format("%.1f",
                        ConversionEnchantment.getDamageFactorBonus(conversionLevel))).append("\n");
            }

            // 检查Exceeded附魔
            int exceededLevel = EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.EXCEEDED.get(), stack);
            if (exceededLevel > 0) {
                info.append("§6超越 §7(").append(exceededLevel).append("级)§r\n");
                info.append("  最大动能 +").append(String.format("%.0f",
                        ExceededEnchantment.getKineticEnergyBonus(exceededLevel))).append("\n");
            }

            if (info.isEmpty()) {
                return "§7无动能附魔§r";
            }

            return info.toString();
        }

        /**
         * 获取附魔加成后的线性转换系数（简写方法）
         */
        public static float getEffectiveLinearFactor(ItemStack stack) {
            return getModifiedLinearFactor(stack, KineticData.LINEAR_CONVERSION_FACTOR);
        }

        /**
         * 获取附魔加成后的重力转换系数（简写方法）
         */
        public static float getEffectiveGravityFactor(ItemStack stack) {
            return getModifiedGravityFactor(stack, KineticData.GRAVITY_CONVERSION_FACTOR);
        }

        /**
         * 获取附魔加成后的动能衰减速率（简写方法）
         */
        public static float getEffectiveDecayRate(ItemStack stack) {
            return getModifiedDecayRate(stack, KineticData.KINETIC_DECAY_RATE);
        }

        /**
         * 获取附魔加成后的伤害转化系数（简写方法）
         */
        public static float getEffectiveDamageFactor(ItemStack stack) {
            return getModifiedDamageFactor(stack, KineticData.DAMAGE_CONVERSION_FACTOR);
        }

        /**
         * 获取附魔加成后的最大动能值（简写方法）
         */
        public static float getEffectiveMaxKineticEnergy(ItemStack stack) {
            return getModifiedMaxKineticEnergy(stack, KineticData.MAX_KINETIC_ENERGY);
        }
    }

    /**
     * 模组兼容性工具类 - 用于安全地获取其他模组的注册对象
     */
    public static class ModGet {

        // 缓存以提高性能
        private static final Map<ResourceLocation, Optional<Item>> ITEM_CACHE = new ConcurrentHashMap<>();
        private static final Map<ResourceLocation, Optional<Block>> BLOCK_CACHE = new ConcurrentHashMap<>();
        private static final Map<String, Boolean> MOD_LOADED_CACHE = new ConcurrentHashMap<>();

        /**
         * 检查模组是否已加载
         * @param modId 模组ID
         * @return 是否已加载
         */
        public static boolean isModLoaded(String modId) {
            return MOD_LOADED_CACHE.computeIfAbsent(modId, ModList.get()::isLoaded);
        }

        /**
         * 安全地获取物品
         * @param modId 模组ID
         * @param itemId 物品ID
         * @return Optional包裹的物品
         */
        public static Optional<Item> getItem(String modId, String itemId) {
            ResourceLocation rl = new ResourceLocation(modId, itemId);
            return ITEM_CACHE.computeIfAbsent(rl, key ->
                    Optional.ofNullable(ForgeRegistries.ITEMS.getValue(key))
            );
        }

        public static Item getItem(Optional<Item> optional) {
            return optional.orElse(Items.AIR);
        }
        public static Block getBlock(Optional<Block> optional) {
            return optional.orElse(Blocks.AIR);
        }

        /**
         * 安全地获取方块
         * @param modId 模组ID
         * @param blockId 方块ID
         * @return Optional包裹的方块
         */
        public static Optional<Block> getBlock(String modId, String blockId) {
            ResourceLocation rl = new ResourceLocation(modId, blockId);
            return BLOCK_CACHE.computeIfAbsent(rl, key ->
                    Optional.ofNullable(ForgeRegistries.BLOCKS.getValue(key))
            );
        }
        /**
         * 获取方块状态
         * @param modId 模组ID
         * @param blockId 方块ID
         * @return Optional包裹的默认方块状态
         */
        public static Optional<BlockState> getBlockState(String modId, String blockId) {
            return getBlock(modId, blockId).map(Block::defaultBlockState);
        }
        /**
         * 通用方法：从Forge注册表获取对象
         * @param registry Forge注册表
         * @param modId 模组ID
         * @param id 对象ID
         * @param <T> 对象类型
         * @return Optional包裹的对象
         */
        public static <T> Optional<T> getFromForgeRegistry(IForgeRegistry<T> registry, String modId, String id) {
            if (registry == null) {
                return Optional.empty();
            }
            ResourceLocation rl = new ResourceLocation(modId, id);
            return Optional.ofNullable(registry.getValue(rl));
        }
        /**
         * 获取实体类型
         * @param modId 模组ID
         * @param entityId 实体ID
         * @return Optional包裹的实体类型
         */
        public static Optional<EntityType<?>> getEntityType(String modId, String entityId) {
            ResourceLocation rl = new ResourceLocation(modId, entityId);
            return Optional.ofNullable(ForgeRegistries.ENTITY_TYPES.getValue(rl));
        }

        /**
         * 获取药水效果
         * @param modId 模组ID
         * @param effectId 效果ID
         * @return Optional包裹的药水效果
         */
        public static Optional<MobEffect> getMobEffect(String modId, String effectId) {
            ResourceLocation rl = new ResourceLocation(modId, effectId);
            return Optional.ofNullable(ForgeRegistries.MOB_EFFECTS.getValue(rl));
        }

        /**
         * 延迟获取物品（用于静态字段初始化）
         * @param modId 模组ID
         * @param itemId 物品ID
         * @return Supplier包裹的物品
         */
        public static Supplier<Item> lazyItem(String modId, String itemId) {
            return () -> getItem(modId, itemId).orElse(null);
        }

        /**
         * 延迟获取方块（用于静态字段初始化）
         * @param modId 模组ID
         * @param blockId 方块ID
         * @return Supplier包裹的方块
         */
        public static Supplier<Block> lazyBlock(String modId, String blockId) {
            return () -> getBlock(modId, blockId).orElse(null);
        }

        /**
         * 检查物品是否存在
         * @param modId 模组ID
         * @param itemId 物品ID
         * @return 是否存在
         */
        public static boolean doesItemExist(String modId, String itemId) {
            return getItem(modId, itemId).isPresent();
        }

        /**
         * 检查方块是否存在
         * @param modId 模组ID
         * @param blockId 方块ID
         * @return 是否存在
         */
        public static boolean doesBlockExist(String modId, String blockId) {
            return getBlock(modId, blockId).isPresent();
        }

        /**
         * 获取所有注册物品的ResourceLocation
         * @param modId 模组ID（null表示所有模组）
         * @return ResourceLocation列表
         */
        public static List<ResourceLocation> getAllItemLocations(@Nullable String modId) {
            List<ResourceLocation> result = new ArrayList<>();
            for (Map.Entry<ResourceKey<Item>, Item> entry : BuiltInRegistries.ITEM.entrySet()) {
                ResourceLocation location = entry.getKey().location();
                if (modId == null || modId.equals(location.getNamespace())) {
                    result.add(location);
                }
            }
            return result;
        }

        /**
         * 清除缓存（谨慎使用，通常不需要）
         */
        public static void clearCache() {
            ITEM_CACHE.clear();
            BLOCK_CACHE.clear();
            MOD_LOADED_CACHE.clear();
        }

        /**
         * 预加载常用物品到缓存
         */
        public static void preloadCommonItems() {

        }
    }

}
