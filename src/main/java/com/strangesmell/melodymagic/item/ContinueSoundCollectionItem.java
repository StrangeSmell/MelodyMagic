package com.strangesmell.melodymagic.item;

import com.google.common.collect.Lists;
import com.strangesmell.melodymagic.MelodyMagic;
import com.strangesmell.melodymagic.api.ItemUtil;
import com.strangesmell.melodymagic.api.Util;
import com.strangesmell.melodymagic.hud.SelectHud;
import com.strangesmell.melodymagic.message.ContinueSoundData;
import com.strangesmell.melodymagic.message.SoundData;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.strangesmell.melodymagic.hud.SelectHud.*;
import static com.strangesmell.melodymagic.hud.SelectHud.peach;

public class ContinueSoundCollectionItem extends Item {

    private static List<SoundInstance> subtitles = Lists.newArrayList();
    private static Map<ResourceLocation,String> subtitles2 = new HashMap();
    private static List<Float> range = Lists.newArrayList();
    private static List<Float> volume = Lists.newArrayList();
    private static List<Float> peach = Lists.newArrayList();
    private static List<Integer> time = Lists.newArrayList();
    private static List<List<Double>> location = Lists.newArrayList();


    public ContinueSoundCollectionItem(Properties pProperties) {
        super(pProperties);
    }
    @Override
    public void onUseTick(Level pLevel, LivingEntity pLivingEntity, ItemStack pStack, int pRemainingUseDuration) {
        if(!pLevel.isClientSide) return;
        if(!SelectHud.subtitles.isEmpty()){
            for(int i=0;i<SelectHud.subtitles.size();++i){
                time.add(getUseDuration(pStack,pLivingEntity)-pRemainingUseDuration);
            }
            subtitles.addAll(SelectHud.subtitles);
            SelectHud.subtitles.clear();
            subtitles2.putAll(SelectHud.subtitles2);
            SelectHud.subtitles2.clear();
            range.addAll(SelectHud.range);
            SelectHud.range.clear();
            volume.addAll(SelectHud.volume);
            SelectHud.volume.clear();
            peach.addAll(SelectHud.peach);
            SelectHud.peach.clear();
            location.addAll(SelectHud.location);
            SelectHud.location.clear();
            SelectHud.subtitles2.clear();
        }
    }
    @Override
    public int getUseDuration(ItemStack pStack, LivingEntity p_345962_) {
        return 72000;
    }
    @Override
    public void releaseUsing(ItemStack pStack, Level pLevel, LivingEntity pEntityLiving, int pTimeLeft) {
        if(!pLevel.isClientSide) return;
        if (pEntityLiving instanceof Player player) {
            Boolean contain = ItemUtil.containItem(player.getInventory().items, Items.AMETHYST_SHARD);
            if(!contain) return;
            PacketDistributor.sendToServer(new ContinueSoundData(Util.saveSoundDataToTag(subtitles, location, subtitles2, range, volume, peach,time)));//发包给一个唱片
            subtitles.clear();
            subtitles2.clear();
            range.clear();
            volume.clear();
            peach.clear();
            time.clear();
        }
    }

    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        ItemStack itemstack = pPlayer.getItemInHand(pUsedHand);
        pPlayer.startUsingItem(pUsedHand);
        return InteractionResultHolder.consume(itemstack);
    }
}
