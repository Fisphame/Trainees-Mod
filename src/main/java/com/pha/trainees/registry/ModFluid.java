package com.pha.trainees.registry;

import com.pha.trainees.Main;
import com.pha.trainees.fluid.CheHbpFluid;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.material.*;
import net.minecraftforge.common.SoundActions;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModFluid {
    public static final DeferredRegister<FluidType> FLUID_TYPES =
            DeferredRegister.create(ForgeRegistries.Keys.FLUID_TYPES, Main.MODID);
    public static final DeferredRegister<Fluid> FLUIDS =
            DeferredRegister.create(ForgeRegistries.FLUIDS, Main.MODID);

    public static final RegistryObject<FluidType> CHE_HBP_FLUID_TYPE = FLUID_TYPES.register("che_hbp",
            () -> new FluidType(FluidType.Properties.create()
                    .density(1500)
                    .viscosity(2000)
                    .temperature(300)
                    .sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL)
                    .sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY)
            ));

//    public static final FlowingFluid FLOWING_JI = register("flowing_ji", new JiFluid.Flowing());
//    public static final FlowingFluid JI = register("ji_fluid", new JiFluid.Source());

//    static {
//        for(Fluid fluid : BuiltInRegistries.FLUID) {
//            for(FluidState fluidstate : fluid.getStateDefinition().getPossibleStates()) {
//                Fluid.FLUID_STATE_REGISTRY.add(fluidstate);
//            }
//        }
//
//    }

    private static <T extends Fluid> T register(String p_76198_, T p_76199_) {
        return Registry.register(BuiltInRegistries.FLUID, p_76198_, p_76199_);
    }



    public static final RegistryObject<FlowingFluid> SOURCE_CHE_HBP = FLUIDS.register("che_hbp_fluid",
            () -> new CheHbpFluid.Source(ModFluid.CHE_HBP_PROPERTIES));

    public static final RegistryObject<FlowingFluid> FLOWING_CHE_HBP = FLUIDS.register("flowing_che_hbp",
            () -> new CheHbpFluid.Flowing(ModFluid.CHE_HBP_PROPERTIES));

//    public static final RegistryObject<FlowingFluid> SOURCE_JI = FLUIDS.register("ji_fluid",
//            () -> new JiFluid());
//
//    public static final RegistryObject<FlowingFluid> FLOWING_JI = FLUIDS.register("flowing_ji",
//            () -> new JiFluid());


    public static final ForgeFlowingFluid.Properties CHE_HBP_PROPERTIES = new ForgeFlowingFluid.Properties(
            CHE_HBP_FLUID_TYPE,
            SOURCE_CHE_HBP,
            FLOWING_CHE_HBP)
            .slopeFindDistance(2)
            .levelDecreasePerBlock(2);
//            .block(ModBlocks.CHE_HBP_BLOCK)
//            .bucket(ModItems.CHE_HBP_BUCKET);
}