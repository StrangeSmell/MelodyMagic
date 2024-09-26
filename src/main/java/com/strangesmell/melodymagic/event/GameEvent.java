package com.strangesmell.melodymagic.event;

import com.google.common.collect.Lists;
import com.strangesmell.melodymagic.api.Util;
import com.strangesmell.melodymagic.item.CollectionItem;
import com.strangesmell.melodymagic.item.ContinueSoundContainerItem;
import com.strangesmell.melodymagic.item.SoundContainerItem;
import com.strangesmell.melodymagic.message.ContinueSoundData;
import com.strangesmell.melodymagic.message.RecordData;
import com.strangesmell.melodymagic.message.SelectCount;
import net.minecraft.client.Minecraft;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.event.entity.living.LivingEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.List;

import static com.strangesmell.melodymagic.MelodyMagic.ENTITY_AGE;
import static com.strangesmell.melodymagic.MelodyMagic.MODID;
import static com.strangesmell.melodymagic.api.Util.getSoundEffectToString;
import static com.strangesmell.melodymagic.hud.SelectHud.*;


@EventBusSubscriber(modid = MODID, bus = EventBusSubscriber.Bus.GAME)
public class GameEvent {
    @SubscribeEvent
    public static void registerPayloadHandlers( ItemTooltipEvent event)
    {
        if(event.getItemStack().getItem() instanceof SoundContainerItem){
            List<SoundEvent> subtitles = Lists.newArrayList();
            List<String> subtitles2 = Lists.newArrayList();
            List<List<Double>> location = Lists.newArrayList();

            if(event.getItemStack().get(DataComponents.CUSTOM_DATA) ==null) return;
            Util.loadSoundDataFromTag(event.getItemStack().get(DataComponents.CUSTOM_DATA).copyTag(),subtitles,location,subtitles2);
            List<String> tooltip = Lists.newArrayList();
            List<Integer> num = Lists.newArrayList();

            List<String> effectList =getSoundEffectToString(event.getItemStack());
            for (int j=0;j<effectList.size();j++){
                event.getToolTip().add(Component.translatable(effectList.get(j)).setStyle(Style.EMPTY.withColor(ChatFormatting.RED)));
            }

            for(int i=0;i<subtitles2.size();i++){
                if(!tooltip.contains(subtitles2.get(i))){
                    tooltip.add(subtitles2.get(i));
                    num.add(1);
                }else{
                    int index = tooltip.indexOf(subtitles2.get(i));
                    num.set(index,num.get(index)+1);
                }
            }

            for(int i=0;i<tooltip.size();i++){
                if(subtitles2.get(i)!=null){
                    event.getToolTip().add(Component.translatable(tooltip.get(i)).append(" *"+num.get(i)).setStyle(Style.EMPTY.withColor(ChatFormatting.GOLD)));
                }

            }
        }else if(event.getItemStack().getItem() instanceof ContinueSoundContainerItem){
            List<SoundEvent> subtitles = Lists.newArrayList();
            List<String> subtitles2 = Lists.newArrayList();
            List<List<Double>> location = Lists.newArrayList();

            if(event.getItemStack().get(DataComponents.CUSTOM_DATA) ==null) return;
            Util.loadSoundDataFromTag(event.getItemStack().get(DataComponents.CUSTOM_DATA).copyTag(),subtitles,location,subtitles2);
            List<String> tooltip = Lists.newArrayList();
            List<Integer> num = Lists.newArrayList();

            List<String> effectList =getSoundEffectToString(event.getItemStack());
            for (int j=0;j<effectList.size();j++){
                event.getToolTip().add(Component.translatable(effectList.get(j)).setStyle(Style.EMPTY.withColor(ChatFormatting.RED)));
            }

            for(int i=0;i<subtitles2.size();i++){
                if(!tooltip.contains(subtitles2.get(i))){
                    tooltip.add(subtitles2.get(i));
                    num.add(1);
                }else{
                    int index = tooltip.indexOf(subtitles2.get(i));
                    num.set(index,num.get(index)+1);
                }
            }

            for(int i=0;i<tooltip.size();i++){
                if(subtitles2.get(i)!=null){
                    event.getToolTip().add(Component.translatable(tooltip.get(i)).append(" *"+num.get(i)).setStyle(Style.EMPTY.withColor(ChatFormatting.GOLD)));
                }

            }
        }
    }

    @SubscribeEvent
    public static void playerEvent(PlayerEvent.Clone event)
    {
        if(event.isWasDeath()){
            Player oldPlayer = event.getOriginal();
            Player newPlayer = event.getEntity();
            if(oldPlayer.getPersistentData().get(MODID+"sound_kinds")!=null) {
                newPlayer.getPersistentData().put(MODID+"sound_kinds",oldPlayer.getPersistentData().get(MODID+"sound_kinds"));

            }

            if(oldPlayer.getPersistentData().get(MODID+"effect_kinds")!=null) {
                newPlayer.getPersistentData().put(MODID+"effect_kinds",oldPlayer.getPersistentData().get(MODID+"effect_kinds"));

            }
            if(oldPlayer.getPersistentData().get(MODID+"subtitle_kinds")!=null) {
                newPlayer.getPersistentData().put(MODID+"subtitle_kinds",oldPlayer.getPersistentData().get(MODID+"subtitle_kinds"));

            }
        }
    }

    @SubscribeEvent
    public static void entityAge(EntityTickEvent.Pre event)
    {
        if(event.getEntity() instanceof Wolf wolf){
            if(wolf.hasData(ENTITY_AGE)){
                if( wolf.getData(ENTITY_AGE)>0){
                    wolf.setData(ENTITY_AGE,wolf.getData(ENTITY_AGE)-1);
                }else {
                    wolf.remove(Entity.RemovalReason.KILLED);
                    wolf.level().playSound(null,wolf.getOnPos(), SoundEvents.WOLF_HOWL, SoundSource.MASTER,(float) (wolf.level().random.nextFloat()*0.25),(float) (wolf.level().random.nextFloat()*0.5));

                }

            }
        }
    }

    @SubscribeEvent
    public static void playerLoggedInEvent(PlayerEvent.PlayerLoggedInEvent event)
    {
        if(event.getEntity().level().isClientSide) return;
        Player player = event.getEntity();
        CompoundTag compoundTag =new CompoundTag();
        compoundTag.put(MODID+"sound_kinds",player.getPersistentData().getCompound(MODID+"sound_kinds") );
        compoundTag.put(MODID+"subtitle_kinds",player.getPersistentData().getCompound(MODID+"subtitle_kinds") );
        compoundTag.put(MODID+"effect_kinds",player.getPersistentData().getCompound(MODID+"effect_kinds") );
        PacketDistributor.sendToPlayer((ServerPlayer) player,new RecordData(compoundTag));//发包给一个唱片

    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void mouseScrollingEvent(InputEvent.MouseScrollingEvent event){
        ItemStack itemStack = Minecraft.getInstance().player.getItemInHand(Minecraft.getInstance().player.getUsedItemHand());
        if(itemStack.getItem() instanceof CollectionItem&&hasAltDown()){
            int selectCount=0;
            CompoundTag compoundTag = new CompoundTag();
            compoundTag = itemStack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
            if(compoundTag.contains(MODID+"select_index")){
                selectCount = compoundTag.getInt(MODID+"select_index");
            }
            selectCount = selectCount - (int)event.getScrollDeltaY();
            if(selectCount<0) selectCount = selectCount + 9;
            if(selectCount>8) selectCount = selectCount - 9;

            compoundTag.putInt(MODID+"select_count",selectCount);

            itemStack.set(DataComponents.CUSTOM_DATA,CustomData.of(compoundTag));
            PacketDistributor.sendToServer(new SelectCount(selectCount));
            event.setCanceled(true);
        }

    }
}
