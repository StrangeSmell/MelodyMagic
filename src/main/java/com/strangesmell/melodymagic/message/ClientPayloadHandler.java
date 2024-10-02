package com.strangesmell.melodymagic.message;

import com.google.common.collect.Lists;
import com.strangesmell.melodymagic.MelodyMagic;
import com.strangesmell.melodymagic.api.ItemUtil;
import com.strangesmell.melodymagic.api.RecordUtil;
import com.strangesmell.melodymagic.api.Util;
import com.strangesmell.melodymagic.item.CollectionItem;
import com.strangesmell.melodymagic.screen.RecordSoundBook;
import net.minecraft.client.Minecraft;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.List;

import static com.strangesmell.melodymagic.MelodyMagic.MM_TRIGGER;
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
    public static void handleContinueData(final ContinueSoundData data, final IPayloadContext context) {
        // Do something with the data, on the network thread
        //blah(data.name());

        // Do something with the data, on the main thread
        context.enqueueWork(() -> {

                    handleContinueSoundContainer(context,data.tag());
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
/*    public static void handleRecordData(final ItemsData data, final IPayloadContext context) {
        context.enqueueWork(() -> {
                    function4(context,data.tag());
                })
                .exceptionally(e -> {
                    // Handle exception
                    context.disconnect(Component.translatable("melodymagic.networking.failed", e.getMessage()));
                    return null;
                });
    }*/

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

/*    public static void function4(IPayloadContext context, CompoundTag compoundTag){
        Player player = context.player();
        int selectCount = 0;
        ItemStack wand = player.getItemInHand(player.getUsedItemHand());
        CompoundTag wandNbt =wand.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        if (!wandNbt.contains(MODID + "select_index")) {
            wandNbt.putInt(MODID + "select_index", selectCount);
        } else {
            selectCount = wandNbt.getInt(MODID + "select_index");
        }
        NonNullList<ItemStack> nonNullList = NonNullList.create();
        ContainerHelper.loadAllItems(compoundTag,nonNullList,player.registryAccess());
        ItemStack ite= player.getItemInHand(player.getUsedItemHand()).get(DataComponents.CONTAINER).getStackInSlot(selectCount);
        ItemContainerContents containerData =ite.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY);
        containerData.copyInto(nonNullList);
        ite.set(DataComponents.CONTAINER, containerData);
    }*/

    public static void handleContinueSoundContainer(IPayloadContext context, CompoundTag compoundTag){
        Player player = context.player();
        ItemStack itemStack = new ItemStack(MelodyMagic.CONTINUE_SOUND_CONTAINER_ITEM.get());
        //存入KEY
        //todo:这部分计算可以在客户端上实现
        List<SoundEvent> soundEvents = Lists.newArrayList();
        List<Integer> num = Lists.newArrayList();

        List<String> subtitle = Lists.newArrayList();

        List<String> strings = Util.getSoundEventNum2(compoundTag.copy(),soundEvents,num,subtitle);//location key

        List<String> listString = Util.getKeyFromSoundRes(strings,num);//这里判断是否满足条件，加入对应的effect key

        //结束存入KEY
        //记录玩家搜集的声音种类
        RecordUtil.saveSoundKindsAndSub(player,soundEvents,subtitle);
        //RecordUtil.saveSubKinds(player,subtitle);
        RecordUtil.saveEffectKinds(player,listString);

        //结束


        CustomData customData = CustomData.of(compoundTag);
        itemStack.set(DataComponents.CUSTOM_DATA,customData);

        Util.saveToCustomData(itemStack,listString);

        MM_TRIGGER.get().trigger((ServerPlayer) player,itemStack);
        ItemUtil.remove1Item(player.getInventory(), Items.AMETHYST_SHARD);
        player.getInventory().add(itemStack);
    }

}
