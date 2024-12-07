package com.strangesmell.melodymagic.api;

import com.strangesmell.melodymagic.MelodyMagic;
import com.strangesmell.melodymagic.item.CollectionItem;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MinecartItem;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import javax.annotation.Nullable;

import static com.strangesmell.melodymagic.MelodyMagic.MODID;

public abstract class SoundEffect {
    public static ResourceLocation DEFALUTRES = ResourceLocation.fromNamespaceAndPath(MODID,"textures/effect_icon/img.png");

    public  void effect(Player player, Level level, InteractionHand pUsedHand, ItemStack itemStack){}

    public  String text(@Nullable Player player,@Nullable  Level level, @Nullable InteractionHand pUsedHand, @Nullable ItemStack collectionItem){
        return null;
    };

    public  Item displayTex(){
        return MelodyMagic.SOUND_CONTAINER_ITEM.get();
    }

    public abstract  String name(@Nullable Player player,@Nullable  Level level, @Nullable InteractionHand pUsedHand, @Nullable ItemStack collectionItem);

    public Component toolTip(@Nullable Player player, @Nullable  Level level, @Nullable InteractionHand pUsedHand, @Nullable ItemStack itemStack){
        return Component.literal(name(player, level, pUsedHand, itemStack)).setStyle(Style.EMPTY.withColor(ChatFormatting.RED));
    }

    @OnlyIn(Dist.CLIENT)
    public Component clientToolTip( @Nullable InteractionHand pUsedHand, @Nullable ItemStack itemStack){
        return Component.literal(name(Minecraft.getInstance().player, Minecraft.getInstance().player.level(), pUsedHand, itemStack)).setStyle(Style.EMPTY.withColor(ChatFormatting.RED));
    }

    public ResourceLocation getRes(){
        return DEFALUTRES;
    }

    public void saveAdditionData(IPayloadContext context, CompoundTag compoundTag,ItemStack itemStack){
        return ;
    }

    public int getWeight(){
        return 16;
    }
    public int getHeight(){
        return 16;
    }

}
