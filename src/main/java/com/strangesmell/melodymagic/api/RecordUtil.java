package com.strangesmell.melodymagic.api;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.List;

import static com.strangesmell.melodymagic.MelodyMagic.MODID;
import static com.strangesmell.melodymagic.api.Util.*;

public class RecordUtil {

    //save to player
    public static void saveSubKinds(Player player, List<String> subtitles) {
        CompoundTag compoundTag = new CompoundTag();
        if(player.getPersistentData().get(MODID+"subtitle_kinds")!=null){
            compoundTag=(CompoundTag) player.getPersistentData().get(MODID+"subtitle_kinds");
        }
        for(int i=0;i<subtitles.size();i++){
            compoundTag.putString("subtitle"+compoundTag.size(),subtitles.get(i));
        }
        player.getPersistentData().put(MODID+"subtitle_kinds",compoundTag);
    }

    //load from player
    public static void loadSubKinds(Player player,List<String> soundEventList) {
        CompoundTag compoundTag = new CompoundTag();
        if(player.getPersistentData().get(MODID+"subtitle_kinds")!=null){
            compoundTag=(CompoundTag) player.getPersistentData().get(MODID+"subtitle_kinds");
        }
        if(compoundTag==null) return;
        for(int i=0;i<compoundTag.size();i++){
            soundEventList.add(compoundTag.getString("subtitle"+i));
        }
    }

    public static CompoundTag loadSubKinds(Player player) {
        CompoundTag compoundTag = new CompoundTag();
        if(player.getPersistentData().get(MODID+"subtitle_kinds")!=null){
            compoundTag=(CompoundTag) player.getPersistentData().get(MODID+"subtitle_kinds");
        }
        if(compoundTag==null) return compoundTag;
        return compoundTag;
    }

    //save to player
    public static void saveSoundKinds(Player player, List<SoundEvent> subtitles) {
        CompoundTag compoundTag = new CompoundTag();
        if(player.getPersistentData().get(MODID+"sound_kinds")!=null){
            compoundTag=(CompoundTag) player.getPersistentData().get(MODID+"sound_kinds");
        }
        for(int i=0;i<subtitles.size();i++){
            putResToTag(compoundTag,subtitles.get(i).getLocation(),compoundTag.size()/2);
        }
        player.getPersistentData().put(MODID+"sound_kinds",compoundTag);
    }

    //save to player
    public static void saveSoundKindsAndSub(Player player, List<SoundEvent> soundEventList, List<String> subtitles) {
        CompoundTag sound_kinds = new CompoundTag();
        CompoundTag subtitle_kinds =loadSubKinds(player);
        List<String> tempSoundEventList = new ArrayList<>();

        if(player.getPersistentData().get(MODID+"sound_kinds")!=null){
            sound_kinds=(CompoundTag) player.getPersistentData().get(MODID+"sound_kinds");
        }
        loadSoundKindsFromTag(sound_kinds,tempSoundEventList);//把玩家数据存入临时数组
        for(int i=0;i<soundEventList.size();i++){
            if(!tempSoundEventList.contains(soundEventList.get(i).getLocation().toString())){
                tempSoundEventList.add(soundEventList.get(i).getLocation().toString());
                putResToTag(sound_kinds,soundEventList.get(i).getLocation(),sound_kinds.size()/2);
                subtitle_kinds.putString("subtitle"+(sound_kinds.size()/2-1),subtitles.get(i));
            }

        }
        player.getPersistentData().put(MODID+"sound_kinds",sound_kinds);
        player.getPersistentData().put(MODID+"subtitle_kinds",subtitle_kinds);
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

    public static void loadSoundKindsFromTag(CompoundTag compoundTag,List<String> soundEventList) {
        if(compoundTag==null) return;
        for(int i=0;i<compoundTag.size()/2;i++){
            soundEventList.add(getSoundEventFromTag(compoundTag,i).getLocation().toString());
        }
    }

    //load from player
    public static void loadSoundKindsString(Player player,List<String> soundEventList) {
        CompoundTag compoundTag = new CompoundTag();
        if(player.getPersistentData().get(MODID+"sound_kinds")!=null){
            compoundTag=(CompoundTag) player.getPersistentData().get(MODID+"sound_kinds");
        }
        if(compoundTag==null) return;
        for(int i=0;i<compoundTag.size()/2;i++){
            soundEventList.add(getResFromTag(compoundTag,i).toString());
        }
    }

    public static CompoundTag loadSoundKinds(Player player) {
        CompoundTag compoundTag = new CompoundTag();
        if(player.getPersistentData().get(MODID+"sound_kinds")!=null){
            compoundTag=(CompoundTag) player.getPersistentData().get(MODID+"sound_kinds");
        }
        if(compoundTag==null) return compoundTag;
        return compoundTag;
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
            compoundTag.putString("effect"+compoundTag.size(),effect.get(i));
        }
        player.getPersistentData().put(MODID+"effect_kinds",compoundTag);
    }

    //load from player
    public static void loadEffectKinds(Player player,List<String> effect) {
        CompoundTag compoundTag = new CompoundTag();
        if(player.getPersistentData().get(MODID+"effect_kinds")!=null){
            compoundTag=(CompoundTag) player.getPersistentData().get(MODID+"effect_kinds");
        }
        if(compoundTag==null) return;
        for(int i=0;i<compoundTag.size();i++){
            effect.add(compoundTag.getString("effect"+i));
        }
    }

    public static CompoundTag loadEffectKinds(Player player) {
        CompoundTag compoundTag = new CompoundTag();
        if(player.getPersistentData().get(MODID+"effect_kinds")!=null){
            compoundTag=(CompoundTag) player.getPersistentData().get(MODID+"effect_kinds");
        }
        if(compoundTag==null) return compoundTag;
        return compoundTag;
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
