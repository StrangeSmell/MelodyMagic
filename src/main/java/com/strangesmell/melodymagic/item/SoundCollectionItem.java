package com.strangesmell.melodymagic.item;

import com.google.common.collect.Lists;
import com.strangesmell.melodymagic.api.ItemUtil;
import com.strangesmell.melodymagic.api.RecordUtil;
import com.strangesmell.melodymagic.api.Util;
import com.strangesmell.melodymagic.hud.RecordHud;
import com.strangesmell.melodymagic.message.SoundData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;
import oshi.util.tuples.Pair;

import java.util.ArrayList;
import java.util.List;

import static com.strangesmell.melodymagic.MelodyMagic.MODID;
import static com.strangesmell.melodymagic.api.Util.putResToTag;
import static com.strangesmell.melodymagic.hud.SelectHud.*;
import static com.strangesmell.melodymagic.hud.SelectHud.subtitles;

public class SoundCollectionItem extends Item {
    public SoundCollectionItem(Properties pProperties) {
        super(pProperties);
    }

    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        ItemStack itemStack = pPlayer.getItemInHand(pUsedHand);
        if (pLevel.isClientSide) {
            //同步到服务端中
            Boolean contain = ItemUtil.containItem(pPlayer.getInventory().items, Items.AMETHYST_SHARD);
            if((!contain)||subtitles.isEmpty()){
                return InteractionResultHolder.pass(pPlayer.getItemInHand(pUsedHand));
            }
            PacketDistributor.sendToServer(new SoundData(Util.saveSoundDataToTag(subtitles, location, subtitles2, range, volume, peach)));//发包给一个唱片

            //记录record
            //又算了一次
            List<String> soundEventList = new ArrayList<>();
            List<String> soundEffectList = new ArrayList<>();
            RecordUtil.loadSoundKindsString(pPlayer,soundEventList);
            RecordUtil.loadEffectKinds(pPlayer,soundEffectList);

            CompoundTag sound_kinds = RecordUtil.loadSoundKinds(pPlayer);
            CompoundTag effect_kinds = RecordUtil.loadEffectKinds(pPlayer);
            CompoundTag subtitle_kinds = RecordUtil.loadSubKinds(pPlayer);

            for(int i=0;i<subtitles.size();i++){
                if(!soundEventList.contains(subtitles.get(i).getLocation().toString())){
                    soundEventList.add(subtitles.get(i).getLocation().toString());
                    putResToTag(sound_kinds,subtitles.get(i).getLocation(),sound_kinds.size()/2);
                    subtitle_kinds.putString("subtitle"+(sound_kinds.size()/2-1),subtitles2.get(subtitles.get(i).getLocation()));
                    RecordHud.recordSubtitle.add(new Pair<>( subtitles2.get(subtitles.get(i).getLocation()),false));
                    RecordHud.recordSubtitleTime.add(1000);
                }
            }

            List<Integer> num = Lists.newArrayList();
            List<String> location = Util.getSoundEventNum3(subtitles,num);//location key
            List<String> effectKey = Util.getKeyFromSoundRes(location,num);//这里判断是否满足条件，加入对应的effect key

            for (String s : effectKey) {
                if (!soundEffectList.contains(s)) {
                    soundEffectList.add(s);
                    effect_kinds.putString("effect" + effect_kinds.size(), s);
                    RecordHud.recordSubtitle.add(new Pair<>(s,true));
                    RecordHud.recordSubtitleTime.add(1000);
                }
            }

            pPlayer.getPersistentData().put(MODID+"sound_kinds",sound_kinds);
            pPlayer.getPersistentData().put(MODID+"effect_kinds",effect_kinds);
            pPlayer.getPersistentData().put(MODID+"subtitle_kinds",subtitle_kinds);


            //record end
            for (SoundInstance soundInstance : subtitles) {
                Minecraft.getInstance().getSoundManager().stop(soundInstance);
            }
            for (int i = 0; i < subtitles.size(); i++) {
                Minecraft.getInstance().getSoundManager().stop(subtitles.get(i));
            }

        }
        return InteractionResultHolder.success(pPlayer.getItemInHand(pUsedHand));
    }
}
