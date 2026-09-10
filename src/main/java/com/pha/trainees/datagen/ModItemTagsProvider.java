package com.pha.trainees.datagen;

import com.pha.trainees.Main;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.BlockTagsProvider;

import java.util.concurrent.CompletableFuture;

/**
 * 物品公共标签（§19.21）：矿/储存块用 {@code copy} 从方块标签同步，
 * 锭与粒直接登记，使外部 mod 的加工链能识别我们的材料（锭→粉→板…）。
 */
public class ModItemTagsProvider extends ItemTagsProvider {

    private static final TagKey<Item> ORES = forgeItem("ores");
    private static final TagKey<Item> ORES_IN_GROUND_STONE = forgeItem("ores_in_ground/stone");
    private static final TagKey<Item> ORES_IN_GROUND_DEEPSLATE = forgeItem("ores_in_ground/deepslate");
    private static final TagKey<Item> STORAGE_BLOCKS = forgeItem("storage_blocks");
    private static final TagKey<Item> INGOTS = forgeItem("ingots");
    private static final TagKey<Item> NUGGETS = forgeItem("nuggets");

    public ModItemTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider,
                               BlockTagsProvider blockTagsProvider) {
        super(output, lookupProvider, blockTagsProvider.contentsGetter());
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        // 矿与储存块：直接复制方块标签（BlockItem 映射由 copy 处理）
        copy(ModBlockTagsProvider.ORES, ORES);
        copy(ModBlockTagsProvider.ORES_IN_GROUND_STONE, ORES_IN_GROUND_STONE);
        copy(ModBlockTagsProvider.ORES_IN_GROUND_DEEPSLATE, ORES_IN_GROUND_DEEPSLATE);
        copy(ModBlockTagsProvider.STORAGE_BLOCKS, STORAGE_BLOCKS);

        for (Material material : Material.ALL) {
            // 按材料细分的矿（仅当该材料确实有矿时才存在对应方块标签）
            if (material.hasOre()) {
                copy(TagKey.create(Registries.BLOCK, new ResourceLocation("forge", "ores/" + material.id())),
                        forgeItem("ores/" + material.id()));
            }
            // 按材料细分的储存块
            copy(TagKey.create(Registries.BLOCK, new ResourceLocation("forge", "storage_blocks/" + material.id())),
                    forgeItem("storage_blocks/" + material.id()));

            // 锭（通用 + 细分）
            tag(INGOTS).add(material.ingot().get());
            tag(forgeItem("ingots/" + material.id())).add(material.ingot().get());

            // 粒（通用 + 细分）
            if (material.hasNugget()) {
                tag(NUGGETS).add(material.nugget().get());
                tag(forgeItem("nuggets/" + material.id())).add(material.nugget().get());
            }
        }
    }

    private static TagKey<Item> forgeItem(String path) {
        return TagKey.create(Registries.ITEM, new ResourceLocation("forge", path));
    }
}
