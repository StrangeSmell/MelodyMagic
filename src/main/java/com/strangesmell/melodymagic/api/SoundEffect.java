package com.strangesmell.melodymagic.api;

import com.strangesmell.melodymagic.MelodyMagic;
import com.strangesmell.melodymagic.item.CollectionItem;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;

import static com.strangesmell.melodymagic.MelodyMagic.MODID;

public abstract class SoundEffect {
    public static ResourceLocation DEFALUTRES = ResourceLocation.fromNamespaceAndPath(MODID,"textures/effect_icon/img.png");

    public  void effect(Player player, Level level, InteractionHand pUsedHand, ItemStack itemStack){}

    public  String text(@Nullable Player player,@Nullable  Level level, @Nullable InteractionHand pUsedHand, @Nullable CollectionItem collectionItem){
        return null;
    };

    public  Item displayTex(){
        return MelodyMagic.SOUND_CONTAINER_ITEM.get();
    }


    public abstract  String name(@Nullable Player player,@Nullable  Level level, @Nullable InteractionHand pUsedHand, @Nullable CollectionItem collectionItem);

    public ResourceLocation getRes(){
        return DEFALUTRES;
    }

    public int getWeight(){
        return 16;
    }
    public int getHeight(){
        return 16;
    }

    //public abstract MutableComponent getTranslation(Player player, Level level, InteractionHand pUsedHand, CollectionItem collectionItem);

    //public abstract MutableComponent getTranslation(Player player, Level level, InteractionHand pUsedHand, CollectionItem collectionItem);
}
