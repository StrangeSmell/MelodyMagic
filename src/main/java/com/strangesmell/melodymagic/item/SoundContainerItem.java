package com.strangesmell.melodymagic.item;

import com.google.common.collect.Lists;
import com.strangesmell.melodymagic.api.Util;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.sounds.SoundSource;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLEnvironment;

import java.util.List;

import static com.strangesmell.melodymagic.MelodyMagic.KEY2EFFECT;
import static com.strangesmell.melodymagic.api.Util.getSoundEffectToString;
import static com.strangesmell.melodymagic.hud.SelectHud.subtitles;

public class SoundContainerItem extends Item {
    public SoundContainerItem(Properties pProperties) {
        super(pProperties);
    }


    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        ItemStack itemstack = pPlayer.getItemInHand(pUsedHand);
        FoodProperties foodproperties = itemstack.getFoodProperties(pPlayer);
        if(pLevel.isClientSide){
            //同步到服务端中
            ClientLevel clientLevel = (ClientLevel) pLevel;
            for(SoundInstance soundInstance : subtitles){
                Minecraft.getInstance().getSoundManager().stop(soundInstance);
            }

        }else{
            ServerLevel serverLevel = (ServerLevel) pLevel;

        }
        List<SoundEvent> subtitles = Lists.newArrayList();
        List<Float> volume = Lists.newArrayList();
        List<Float> peach = Lists.newArrayList();
        List<List<Double>> location = Lists.newArrayList();
        if(pPlayer.getItemInHand(pUsedHand).get(DataComponents.CUSTOM_DATA)==null) return InteractionResultHolder.consume(itemstack);
        Util.loadSoundDataFromTag(pPlayer.getItemInHand(pUsedHand).get(DataComponents.CUSTOM_DATA).copyTag(),subtitles,location,volume,peach);
        //if(pLevel instanceof  ServerLevel) effect(subtitles,location, pLevel,  pPlayer,  pUsedHand);
        if(!subtitles.isEmpty()){
            for (int i =0;i<subtitles.size();i++) {
                pLevel.playSound(null,location.get(i).get(0)+pPlayer.getX(),location.get(i).get(1)+pPlayer.getY(),location.get(i).get(2)+pPlayer.getZ(),subtitles.get(i),SoundSource.MASTER ,volume.get(i),peach.get(i));
            }
        }
        pPlayer.getCooldowns().addCooldown(this, 5*subtitles.size());
        return InteractionResultHolder.consume(itemstack);
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        List<SoundEvent> subtitles = Lists.newArrayList();
        List<String> subtitles2 = Lists.newArrayList();
        List<List<Double>> location = Lists.newArrayList();

        if(stack.get(DataComponents.CUSTOM_DATA) ==null) return;
        Util.loadSoundDataFromTag(stack.get(DataComponents.CUSTOM_DATA).copyTag(),subtitles,location,subtitles2);
        List<String> tooltip = Lists.newArrayList();
        List<Integer> num = Lists.newArrayList();

        List<String> effectList =getSoundEffectToString(stack);
        for (int j=0;j<effectList.size();j++){
            if(KEY2EFFECT.get(effectList.get(j))==null)  continue;
            if(FMLEnvironment.dist == Dist.CLIENT){
                tooltipComponents.add(KEY2EFFECT.get(effectList.get(j)).clientToolTip(null,stack));
            }else {
                tooltipComponents.add(KEY2EFFECT.get(effectList.get(j)).toolTip(null,null,null,stack));
            }
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
                tooltipComponents.add(Component.translatable(tooltip.get(i)).append(" *"+num.get(i)).setStyle(Style.EMPTY.withColor(ChatFormatting.GOLD)));
            }

        }
    }
}
