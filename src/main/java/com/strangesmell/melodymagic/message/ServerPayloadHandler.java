package com.strangesmell.melodymagic.message;

import com.google.common.collect.Lists;
import com.strangesmell.melodymagic.MelodyMagic;
import com.strangesmell.melodymagic.api.Util;
import com.strangesmell.melodymagic.item.CollectionItem;
import com.strangesmell.melodymagic.item.SoundContainerItem;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.List;

import static com.strangesmell.melodymagic.MelodyMagic.MODID;

public class ServerPayloadHandler {
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

    public static void function(IPayloadContext context, CompoundTag compoundTag){
        Player player = context.player();
        ItemStack itemStack = new ItemStack(MelodyMagic.SOUND_CONTAINER_ITEM.get());

        //存入KEY
        //todo:这部分计算可以在客户端上实现
        List<SoundEvent> subtitles = Lists.newArrayList();
        List<List<Double>> location = Lists.newArrayList();
        Util.loadSoundDataToTag(compoundTag.copy(),subtitles,location);

        List<SoundEvent> soundEvents = Lists.newArrayList();
        List<Integer> num = Lists.newArrayList();
        List<String> strings = Util.getSoundEventNum2(compoundTag.copy(),soundEvents,num);

        List<String> listString = Util.getKeyFromSoundRes(strings,num);


        //结束存入KEY


        CustomData customData = CustomData.of(compoundTag);
        itemStack.set(DataComponents.CUSTOM_DATA,customData);

        Util.saveToCustomData(itemStack,listString);

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

    public static String getKey(String key){
        return MODID+key;
    }
}
