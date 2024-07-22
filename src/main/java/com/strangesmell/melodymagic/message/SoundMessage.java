package com.strangesmell.melodymagic.message;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public class SoundMessage implements CustomPacketPayload {
    @Override
    public Type<? extends CustomPacketPayload> type() {
        return null;
    }
}
