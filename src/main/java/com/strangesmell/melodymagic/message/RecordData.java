package com.strangesmell.melodymagic.message;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import static com.strangesmell.melodymagic.MelodyMagic.MODID;

public record RecordData(CompoundTag tag) implements CustomPacketPayload {
    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(MODID,"record_data");

    public static final Type<RecordData> TYPE = new Type<>(ID);

    public static final StreamCodec<ByteBuf, RecordData> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.COMPOUND_TAG,
            RecordData::tag,
            RecordData::new
    );

    public RecordData(final FriendlyByteBuf buf){
        this(buf.readNbt());
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
