package com.strangesmell.melodymagic.event;

import com.google.common.collect.Lists;
import com.strangesmell.melodymagic.api.Util;
import com.strangesmell.melodymagic.item.CollectionItem;
import com.strangesmell.melodymagic.item.SoundContainerItem;
import com.strangesmell.melodymagic.message.SelectCount;
import com.strangesmell.melodymagic.message.SoundData;
import net.minecraft.client.Minecraft;
import net.minecraft.ChatFormatting;
import net.minecraft.client.sounds.WeighedSoundEvents;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.List;

import static com.strangesmell.melodymagic.MelodyMagic.MODID;
import static com.strangesmell.melodymagic.hud.SelectHud.*;


@EventBusSubscriber(modid = MODID, bus = EventBusSubscriber.Bus.GAME)
public class GameEvent {
    @SubscribeEvent
    public static void registerPayloadHandlers( ItemTooltipEvent event)
    {
        if(event.getItemStack().getItem() instanceof SoundContainerItem){
            List<SoundEvent> subtitles = Lists.newArrayList();
            List<List<Double>> location = Lists.newArrayList();
            if(event.getItemStack().get(DataComponents.CUSTOM_DATA) ==null) return;
            Util.loadSoundDataToTag(event.getItemStack().get(DataComponents.CUSTOM_DATA).copyTag(),subtitles,location);
            List<SoundEvent> tooltip = Lists.newArrayList();
            List<Integer> num = Lists.newArrayList();
            for(int i=0;i<subtitles.size();i++){
                if(!Util.contain(tooltip,subtitles.get(i))){
                    tooltip.add(subtitles.get(i));
                    num.add(1);
                }else{
                    int index = Util.indexOf(tooltip,subtitles.get(i));
                    num.set(index,num.get(index)+1);
                }
            }
            for(int i=0;i<tooltip.size();i++){
                ResourceLocation resourceLocation = ResourceLocation.fromNamespaceAndPath(MODID,"subtitles."+tooltip.get(i).getLocation().getPath());
                TranslatableContents translatableContents = new TranslatableContents(resourceLocation.toString(),null,TranslatableContents.NO_ARGS);
                TranslatableContents translatableContents2 = new TranslatableContents(resourceLocation.toString(),null,TranslatableContents.NO_ARGS);

                event.getToolTip().add(Component.translatable("subtitles."+tooltip.get(i).getLocation().getPath()).append(" *"+num.get(i)).setStyle(Style.EMPTY.withColor(ChatFormatting.GOLD)));
            }
        }
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
            selectCount = selectCount + (int)event.getScrollDeltaY();
            if(selectCount<0) selectCount = selectCount + 9;
            if(selectCount>8) selectCount = selectCount - 9;

            compoundTag.putInt(MODID+"select_count",selectCount);

            itemStack.set(DataComponents.CUSTOM_DATA,CustomData.of(compoundTag));
            PacketDistributor.sendToServer(new SelectCount(selectCount));
            event.setCanceled(true);
        }

    }
}
