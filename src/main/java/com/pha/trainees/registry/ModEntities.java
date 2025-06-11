package com.pha.trainees.registry;

import com.pha.trainees.Main;
import com.pha.trainees.entity.BasketballEntity;
import com.pha.trainees.entity.CalledSwordEntity;
//import com.pha.trainees.entity.KunTraineesEntity;
import com.pha.trainees.entity.KunTraineesEntity;
//import com.pha.trainees.entity.RopeEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEntities {
    // 创建 DeferredRegister 实例，管理实体注册
    public static final DeferredRegister<EntityType<?>> ENTITIES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, Main.MODID);
    // 注册自定义剑实体
    public static final RegistryObject<EntityType<CalledSwordEntity>> CALLED_SWORD =
            ENTITIES.register("called_sword", // 实体注册名（最终ID为 trainees:called_sword）
                    () -> EntityType.Builder.of(
                                    CalledSwordEntity::new, // 实体工厂方法
                                    MobCategory.MISC // 实体分类（不属于生物）
                            )
                            .sized(1.0F, 1.0F) // 碰撞箱大小（宽1.0，高1.0）
                            .clientTrackingRange(4) // 客户端跟踪范围（4区块）
                            .build("called_sword") // 内部名称
            );

    public static final RegistryObject<EntityType<KunTraineesEntity>> KUN_TRAINEES =
            ENTITIES.register("kun_trainees",
                    () -> EntityType.Builder.of(
                            KunTraineesEntity::new,
                            MobCategory.MONSTER
                    )
                            .sized(1.0F,1.0F)
                            .clientTrackingRange(16)
                            .build("kun_trainees")
            );

    public static final RegistryObject<EntityType<BasketballEntity>> BASKETBALL =
            ENTITIES.register("kun_basketball",
                    () -> EntityType.Builder.<BasketballEntity>of(
                                    BasketballEntity::new,
                                    MobCategory.MISC
                            )
                            .sized(0.5F, 0.5F)
                            .clientTrackingRange(4)
                            .build("kun_basketball")
            );

/*
    public static final RegistryObject<EntityType<RopeEntity>> ROPE =
            ENTITIES.register("rope",
                    () -> EntityType.Builder.of(
                            RopeEntity::new,
                            MobCategory.MISC
                    )
                            .sized(1.0F,1.0F)
                            .clientTrackingRange(3)
                            .build("rope")
                    );

*/

}

