package com.strangesmell.melodymagic.item;

import com.google.common.collect.Lists;
import com.strangesmell.melodymagic.api.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.sounds.SoundSource;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;

import java.util.List;
import java.util.function.Consumer;

import static com.strangesmell.melodymagic.hud.SelectHud.subtitles;
import static com.strangesmell.melodymagic.api.Util.getRandomEffect;

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
        List<List<Double>> location = Lists.newArrayList();
        if(pPlayer.getItemInHand(pUsedHand).get(DataComponents.CUSTOM_DATA)==null) return InteractionResultHolder.consume(itemstack);
        Util.loadSoundDataToTag(pPlayer.getItemInHand(pUsedHand).get(DataComponents.CUSTOM_DATA).copyTag(),subtitles,location);
        if(pLevel instanceof  ServerLevel) effect(subtitles,location, pLevel,  pPlayer,  pUsedHand);
        if(!subtitles.isEmpty()){
            for (int i =0;i<subtitles.size();i++) {
                pLevel.playSound(null,location.get(i).get(0)+pPlayer.getX(),location.get(i).get(1)+pPlayer.getY(),location.get(i).get(2)+pPlayer.getZ(),subtitles.get(i),SoundSource.MASTER );

            }
        }
        pPlayer.getCooldowns().addCooldown(this, 5*subtitles.size());
        return InteractionResultHolder.consume(itemstack);
    }
    public void effect(List<SoundEvent> subtitles,List<List<Double>> distance,Level pLevel, Player pPlayer, InteractionHand pUsedHand){

        MobEffectInstance effectInstance = new MobEffectInstance(getRandomEffect(), 180, 1);
        pPlayer.addEffect(effectInstance);
    }


}
