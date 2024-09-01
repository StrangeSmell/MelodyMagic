package com.strangesmell.melodymagic.api;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.player.Player;

import java.util.List;

import static com.strangesmell.melodymagic.MelodyMagic.MODID;
import static com.strangesmell.melodymagic.api.Util.*;

public class RecodeUtil {

    //save to player
    public static void saveSoundKinds(Player player, List<SoundEvent> subtitles) {
        CompoundTag compoundTag = new CompoundTag();
        if(player.getPersistentData().get(MODID+"sound_kinds")!=null){
            compoundTag=(CompoundTag) player.getPersistentData().get(MODID+"sound_kinds");
        }
        for(int i=0;i<subtitles.size();i++){
            putResToTag(compoundTag,subtitles.get(i).getLocation(),i);
        }
    }

    //load from player
    public static void loadSoundKinds(Player player,List<SoundEvent> soundEventList) {
        CompoundTag compoundTag = new CompoundTag();
        if(player.getPersistentData().get(MODID+"sound_kinds")!=null){
            compoundTag=(CompoundTag) player.getPersistentData().get(MODID+"sound_kinds");
        }
        if(compoundTag==null) return;
        for(int i=0;i<compoundTag.size()/2;i++){
            soundEventList.add(getSoundEventFromTag(compoundTag,i));
        }
    }

    //get the size of sound kind
    public static int getSoundKindsNum(Player player) {
        CompoundTag compoundTag = new CompoundTag();
        if(player.getPersistentData().get(MODID+"sound_kinds")!=null){
            compoundTag=(CompoundTag) player.getPersistentData().get(MODID+"sound_kinds");
        }
        if(compoundTag==null) return 0;
        return compoundTag.size()/2;
    }

    //save to player
    public static void saveEffectKinds(Player player,List<String> effect) {
        CompoundTag compoundTag = new CompoundTag();
        if(player.getPersistentData().get(MODID+"effect_kinds")!=null){
            compoundTag=(CompoundTag) player.getPersistentData().get(MODID+"effect_kinds");
        }
        for(int i=0;i<effect.size();i++){
            compoundTag.putString("effect"+i,effect.get(i));
        }
    }

    //load from player
    public static void loadEffectKinds(Player player,List<String> effect) {
        CompoundTag compoundTag = new CompoundTag();
        if(player.getPersistentData().get(MODID+"effect_kinds")!=null){
            compoundTag=(CompoundTag) player.getPersistentData().get(MODID+"effect_kinds");
        }
        if(compoundTag==null) return;
        for(int i=0;i<compoundTag.size()/2;i++){
            effect.add(compoundTag.getString("effect"+i));
        }
    }

    //get the size of effect kind
    public static int getEffectKindsNum(Player player) {
        CompoundTag compoundTag = new CompoundTag();
        if(player.getPersistentData().get(MODID+"sound_kinds")!=null){
            compoundTag=(CompoundTag) player.getPersistentData().get(MODID+"sound_kinds");
        }
        if(compoundTag==null) return 0;
        return compoundTag.size();
    }
}
