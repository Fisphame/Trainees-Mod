package com.pha.trainees.datagen;

import com.pha.trainees.Main;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

/**
 * 方块公共标签（§19.21 已定）：把我们的矿/深层矿/金属块接入 Forge 通用约定标签，
 * 使 GT / Mekanism / Create / 各类矿机与加工机可以直接识别并处理我们的材料。
 *
 * <p>只生成 {@code forge:} 命名空间的标签；**不触碰**已手写的
 * {@code minecraft:mineable/pickaxe} 等文件，避免同一逻辑路径出现两份文件。</p>
 */
public class ModBlockTagsProvider extends BlockTagsProvider {

    // ---- forge: 通用标签（用 TagKey 直接构造，避免依赖 Forge 常量命名差异） ----
    public static final TagKey<Block> ORES = forgeBlock("ores");
    public static final TagKey<Block> ORES_IN_GROUND_STONE = forgeBlock("ores_in_ground/stone");
    public static final TagKey<Block> ORES_IN_GROUND_DEEPSLATE = forgeBlock("ores_in_ground/deepslate");
    public static final TagKey<Block> STORAGE_BLOCKS = forgeBlock("storage_blocks");

    public ModBlockTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider,
                                @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, Main.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        for (Material material : Material.ALL) {
            // 金属块 → 通用储存块 + 按材料细分
            tag(STORAGE_BLOCKS).add(material.storageBlock().get());
            tag(forgeBlock("storage_blocks/" + material.id())).add(material.storageBlock().get());

            // 矿（石质 + 深层）→ 通用矿 + 按材料细分 + 按赋存岩层
            if (material.hasOre()) {
                tag(ORES).add(material.ore().get(), material.deepslateOre().get());
                tag(forgeBlock("ores/" + material.id()))
                        .add(material.ore().get(), material.deepslateOre().get());
                tag(ORES_IN_GROUND_STONE).add(material.ore().get());
                tag(ORES_IN_GROUND_DEEPSLATE).add(material.deepslateOre().get());
            }
        }
    }

    private static TagKey<Block> forgeBlock(String path) {
        return TagKey.create(Registries.BLOCK, new ResourceLocation("forge", path));
    }
}
