package com.strangesmell.melodymagic.item;

import com.strangesmell.melodymagic.screen.RecordSoundBook;
import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import static com.strangesmell.melodymagic.MelodyMagic.MODID;


public class RecordBook extends Item {
    public RecordBook(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return true;
    }

    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        if(pLevel.isClientSide){
            function(pPlayer);
        }
        if(pPlayer.isShiftKeyDown()){
            pPlayer.getPersistentData().remove(MODID+"sound_kinds");
            pPlayer.getPersistentData().remove(MODID+"effect_kinds");
            pPlayer.getPersistentData().remove(MODID+"subtitle_kinds");
        }
        return InteractionResultHolder.success(pPlayer.getItemInHand(pUsedHand));
    }

    @OnlyIn(Dist.CLIENT)
    private void function(Player pPlayer){
        RecordSoundBook recodeScreen = new RecordSoundBook(pPlayer);
        Minecraft.getInstance().setScreen(recodeScreen);
    }

}
