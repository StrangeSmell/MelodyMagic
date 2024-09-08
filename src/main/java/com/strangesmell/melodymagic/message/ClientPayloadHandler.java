package com.strangesmell.melodymagic.message;

import com.strangesmell.melodymagic.MelodyMagic;
import com.strangesmell.melodymagic.item.CollectionItem;
import com.strangesmell.melodymagic.screen.RecordSoundBook;
import net.minecraft.client.Minecraft;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import static com.strangesmell.melodymagic.MelodyMagic.MODID;
import static com.strangesmell.melodymagic.message.ServerPayloadHandler.getKey;

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
    public static void handleData(final SelectCount data, final IPayloadContext context) {
        // Do something with the data, on the network thread
        //blah(data.name());

        // Do something with the data, on the main thread
        context.enqueueWork(() -> {

                    function2(context,data.selectCount());
                })
                .exceptionally(e -> {
                    // Handle exception
                    context.disconnect(Component.translatable("melodymagic.networking.failed", e.getMessage()));
                    return null;
                });
    }

    public static void handleRecordData(final RecordData data, final IPayloadContext context) {
        context.enqueueWork(() -> {
                    function3(context,data.tag());
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

    public static void function2(IPayloadContext context,int selectCount){
        Player player = context.player();
        ItemStack itemStack = player.getItemInHand(player.getUsedItemHand());
        if(itemStack.getItem() instanceof CollectionItem){
            CompoundTag compoundTag = itemStack.getOrDefault(DataComponents.CUSTOM_DATA,CustomData.EMPTY).copyTag();
            compoundTag.putInt(getKey("old_select_index"),compoundTag.getInt(getKey("select_index")));
            compoundTag.putInt(getKey("select_index"),selectCount);
            itemStack.set(DataComponents.CUSTOM_DATA,CustomData.of(compoundTag));
        }

    }
    public static void function3(IPayloadContext context, CompoundTag compoundTag){
        Player player = context.player();
        if(compoundTag.get(MODID+"sound_kinds")!=null){
            player.getPersistentData().put(MODID+"sound_kinds",compoundTag.get(MODID+"sound_kinds"));
        }
        if(compoundTag.get(MODID+"effect_kinds")!=null){
            player.getPersistentData().put(MODID+"effect_kinds",compoundTag.get(MODID+"effect_kinds"));
        }
        if(compoundTag.get(MODID+"subtitle_kinds")!=null){
            player.getPersistentData().put(MODID+"subtitle_kinds",compoundTag.get(MODID+"subtitle_kinds"));
        }
    }

}
