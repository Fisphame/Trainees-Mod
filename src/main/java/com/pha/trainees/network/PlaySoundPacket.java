package com.pha.trainees.network;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

public class PlaySoundPacket {
    private final SoundEvent sound;
    private final double x, y, z;

    public PlaySoundPacket(SoundEvent sound, double x, double y, double z) {
        this.sound = sound;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public static void encode(PlaySoundPacket msg, FriendlyByteBuf buffer) {
        buffer.writeRegistryId(ForgeRegistries.SOUND_EVENTS, msg.sound);
        buffer.writeDouble(msg.x);
        buffer.writeDouble(msg.y);
        buffer.writeDouble(msg.z);
    }

    public static PlaySoundPacket decode(FriendlyByteBuf buffer) {
        SoundEvent sound = buffer.readRegistryId();
        double x = buffer.readDouble();
        double y = buffer.readDouble();
        double z = buffer.readDouble();
        return new PlaySoundPacket(sound, x, y, z);
    }

    public static void handle(PlaySoundPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            // 在客户端播放音效
            Minecraft.getInstance().level.playLocalSound(
                    msg.x, msg.y, msg.z,
                    msg.sound, SoundSource.PLAYERS,
                    1.0f, 1.0f, false
            );
        });
        ctx.get().setPacketHandled(true);
    }
}