package com.strangesmell.melodymagic.item;

import com.strangesmell.melodymagic.api.Util;
import com.strangesmell.melodymagic.message.SoundData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.resources.sounds.Sound;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;

import static com.strangesmell.melodymagic.Hud.SelectHud.distance;
import static com.strangesmell.melodymagic.Hud.SelectHud.subtitles;
import static com.strangesmell.melodymagic.MelodyMagic.MODID;
import static com.strangesmell.melodymagic.api.Util.saveSoundDataToTag;


public class CollectionItem extends Item {

    public CollectionItem(Properties pProperties) {
        super(pProperties);
    }

    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        ItemStack itemstack = pPlayer.getItemInHand(pUsedHand);
        FoodProperties foodproperties = itemstack.getFoodProperties(pPlayer);
        if(pLevel.isClientSide){
            //同步到服务端中
            ClientLevel clientLevel = (ClientLevel) pLevel;

            PacketDistributor.sendToServer(new SoundData(Util.saveSoundDataToTag(subtitles,distance)));

            for(SoundInstance soundInstance : subtitles){
                Minecraft.getInstance().getSoundManager().stop(soundInstance);
            }
        }else{
            ServerLevel serverLevel = (ServerLevel) pLevel;


        }
        if (foodproperties != null) {
            if (pPlayer.canEat(foodproperties.canAlwaysEat())) {
                pPlayer.startUsingItem(pUsedHand);
                return InteractionResultHolder.consume(itemstack);
            } else {
                return InteractionResultHolder.fail(itemstack);
            }
        } else {
            return InteractionResultHolder.pass(pPlayer.getItemInHand(pUsedHand));
        }
    }


}
