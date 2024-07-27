package com.strangesmell.melodymagic.event;

import com.google.common.collect.Lists;
import com.strangesmell.melodymagic.api.Util;
import com.strangesmell.melodymagic.item.SoundContainerItem;
import net.minecraft.client.sounds.WeighedSoundEvents;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

import java.util.List;

import static com.strangesmell.melodymagic.MelodyMagic.MODID;


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
                //if(!tooltip.contains(subtitles.get(i))){
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


                event.getToolTip().add(Component.translatable("subtitles."+tooltip.get(i).getLocation().getPath()).append(" X"+num.get(i)));
            }
        }



    }
}
