package com.strangesmell.melodymagic.message;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import static com.strangesmell.melodymagic.MelodyMagic.MODID;

public record SelectCount(Integer selectCount) implements CustomPacketPayload {
    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(MODID,"select_index");

    public static final Type<SelectCount> TYPE = new Type<>(ID);

    public static final StreamCodec<ByteBuf, SelectCount> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT,
            SelectCount::selectCount,
            SelectCount::new
    );

    public SelectCount(final FriendlyByteBuf buf){
        this(buf.readInt());
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
