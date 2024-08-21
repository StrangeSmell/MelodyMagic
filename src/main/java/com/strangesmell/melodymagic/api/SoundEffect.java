package com.strangesmell.melodymagic.api;

import com.strangesmell.melodymagic.item.CollectionItem;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public abstract class SoundEffect {
    public  void effect(Player player, Level level, InteractionHand pUsedHand, ItemStack itemStack){}
    public  String text(Player player, Level level, InteractionHand pUsedHand, CollectionItem collectionItem){
        return null;
    }
}
