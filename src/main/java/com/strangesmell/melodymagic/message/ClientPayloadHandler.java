package com.strangesmell.melodymagic.message;

import com.strangesmell.melodymagic.MelodyMagic;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class ClientPayloadHandler {
    private static final ClientPayloadHandler INSTANCE = new ClientPayloadHandler();

    public static ClientPayloadHandler getInstance() {
        return INSTANCE;
    }

    public static void handleData(final SoundData data, final IPayloadContext context) {
        // Do something with the data, on the network thread
        //blah(data.name());

        // Do something with the data, on the main thread
        context.enqueueWork(() -> {

                    function(context,data.tag());
                })
                .exceptionally(e -> {
                    // Handle exception
                    context.disconnect(Component.translatable("melodymagic.networking.failed", e.getMessage()));
                    return null;
                });
    }

    public static void function(IPayloadContext context, CompoundTag compoundTag){
        Player player = context.player();
        ItemStack itemStack = new ItemStack(MelodyMagic.SOUND_CONTAINER_ITEM.get());
        CustomData customData = CustomData.of(compoundTag);
        itemStack.set(DataComponents.CUSTOM_DATA,customData);
        player.getInventory().add(itemStack);
    }

}
