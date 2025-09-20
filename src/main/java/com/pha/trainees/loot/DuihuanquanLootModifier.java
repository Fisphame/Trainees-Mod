//package com.pha.trainees.loot;
//
//import com.google.gson.JsonObject;
//import com.mojang.serialization.Codec;
//import com.mojang.serialization.codecs.RecordCodecBuilder;
//import com.pha.trainees.registry.ModItems;
//import it.unimi.dsi.fastutil.objects.ObjectArrayList;
//import net.minecraft.resources.ResourceLocation;
//import net.minecraft.world.item.ItemStack;
//import net.minecraft.world.level.storage.loot.LootContext;
//import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
//import net.minecraftforge.common.loot.IGlobalLootModifier;
//import net.minecraftforge.common.loot.LootModifier;
//import org.jetbrains.annotations.NotNull;
//
//public class DuihuanquanLootModifier extends LootModifier {
//    private final float chance;
//
//    public static final Codec<DuihuanquanLootModifier> CODEC = RecordCodecBuilder.create(inst ->
//            LootModifier.codecStart(inst).and(
//                    Codec.FLOAT.fieldOf("chance").forGetter(m -> m.chance)
//            ).apply(inst, DuihuanquanLootModifier::new)
//    );
//
//    public DuihuanquanLootModifier(LootItemCondition[] conditionsIn, float chance) {
//        super(conditionsIn);
//        this.chance = chance;
//    }
//
//    @Override
//    protected @NotNull ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
//        // 只在主世界生成
//        if (!context.getLevel().dimension().location().equals(new ResourceLocation("minecraft:overworld"))) {
//            return generatedLoot;
//        }
//
//        // 随机几率检查
//        if (context.getRandom().nextFloat() < chance) {
//            generatedLoot.add(new ItemStack(ModItems.duihuanquan.get()));
//        }
//
//        return generatedLoot;
//    }
//
//    @Override
//    public Codec<? extends IGlobalLootModifier> codec() {
//        return CODEC;
//    }
//
//    // 简单的序列化器实现
//    public static class Serializer extends GlobalLootModifierSerializer<DuihuanquanLootModifier> {
//        @Override
//        public DuihuanquanLootModifier read(ResourceLocation location, JsonObject object, LootItemCondition[] conditions) {
//            float chance = object.get("chance").getAsFloat();
//            return new DuihuanquanLootModifier(conditions, chance);
//        }
//
//        @Override
//        public JsonObject write(DuihuanquanLootModifier instance) {
//            JsonObject json = makeConditions(instance.conditions);
//            json.addProperty("chance", instance.chance);
//            return json;
//        }
//    }
//}