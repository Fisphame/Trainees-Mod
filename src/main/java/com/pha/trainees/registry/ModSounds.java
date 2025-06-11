package com.pha.trainees.registry;

import com.pha.trainees.Main;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModSounds {
    public static final DeferredRegister<SoundEvent> SOUNDS =
            DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, Main.MODID);

    private static RegistryObject<SoundEvent> registerSound(String name) {
        return SOUNDS.register(name,
                () -> SoundEvent.createVariableRangeEvent(
                        new ResourceLocation(Main.MODID, name))
        );
    }

    public static final RegistryObject<SoundEvent> AHKUN_SOUND = registerSound("ahkun_sound");
    public static final RegistryObject<SoundEvent> MINING_SOUND_1 = registerSound("mining_sound_1");
    public static final RegistryObject<SoundEvent> MINING_SOUND_2 = registerSound("mining_sound_2");
    public static final RegistryObject<SoundEvent> MINING_SOUND_3 = registerSound("mining_sound_3");
    public static final RegistryObject<SoundEvent> MINING_SOUND_4 = registerSound("mining_sound_4");
    public static final RegistryObject<SoundEvent> MINING_SOUND_5 = registerSound("mining_sound_5");
    public static final RegistryObject<SoundEvent> MINING_SOUND_6 = registerSound("mining_sound_6");


}
