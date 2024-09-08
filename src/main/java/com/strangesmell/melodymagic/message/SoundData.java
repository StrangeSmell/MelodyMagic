package com.strangesmell.melodymagic.message;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;


import static com.strangesmell.melodymagic.MelodyMagic.MODID;

public record SoundData(CompoundTag tag) implements CustomPacketPayload {
    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(MODID,"sound_message");

    public static final Type<SoundData> TYPE = new Type<>(ID);

    public static final StreamCodec<ByteBuf, SoundData> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.COMPOUND_TAG,
            SoundData::tag,
            SoundData::new
    );

    public SoundData(final FriendlyByteBuf buf){
        this(buf.readNbt());
    }



    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
