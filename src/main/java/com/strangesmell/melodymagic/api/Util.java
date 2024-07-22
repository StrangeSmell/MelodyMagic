package com.strangesmell.melodymagic.api;

import com.google.common.collect.Lists;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.phys.Vec3;

import java.util.*;

import static net.minecraft.sounds.SoundEvent.createVariableRangeEvent;
import static net.minecraft.world.effect.MobEffects.*;

public class Util {
    public static List<Holder<MobEffect>> EFFECT_LIST =  Arrays.asList(MOVEMENT_SPEED,MOVEMENT_SLOWDOWN,DIG_SPEED,DIG_SLOWDOWN,DAMAGE_BOOST,HEAL,HARM,JUMP,CONFUSION,REGENERATION
            ,DAMAGE_RESISTANCE,FIRE_RESISTANCE,WATER_BREATHING,INVISIBILITY,BLINDNESS,NIGHT_VISION,HUNGER,WEAKNESS,POISON,WITHER,HEALTH_BOOST,ABSORPTION
            ,SATURATION,GLOWING,LEVITATION,LUCK,UNLUCK,SLOW_FALLING,CONDUIT_POWER,DOLPHINS_GRACE,BAD_OMEN,HERO_OF_THE_VILLAGE,DARKNESS,TRIAL_OMEN,RAID_OMEN,WIND_CHARGED,WEAVING
            ,OOZING,INFESTED);
    public static Holder<MobEffect> getRandomEffect(){
        Random random = new Random(net.minecraft.Util.getMillis());
        return EFFECT_LIST.get(random.nextInt(0,EFFECT_LIST.size()));
    }
    public static CompoundTag saveSoundDataToTag(List<SoundInstance> subtitles,List<Double> distance){
        CompoundTag compoundTag = new CompoundTag();
        int size = subtitles.size();
        compoundTag.putInt("size",size);
        if(subtitles.isEmpty())  return compoundTag;
        for(int i = 0;i<size;i++){
            compoundTag.putString("namespace"+i,subtitles.get(i).getLocation().getNamespace());
            compoundTag.putString("path"+i,subtitles.get(i).getLocation().getPath());
            compoundTag.putDouble("distance"+i,distance.get(i));
        }
        return compoundTag;
    }

    public static CompoundTag saveSoundDataToTag(List<List<Object>> subtitles){
        CompoundTag compoundTag = new CompoundTag();
        int size = subtitles.size();
        compoundTag.putInt("size",size);
        if(subtitles.isEmpty())  return compoundTag;
        for(int i = 0;i<size;i++){
            compoundTag.putString("namespace"+i,((SoundEvent)subtitles.get(i).get(0)).getLocation().getNamespace());
            compoundTag.putString("path"+i,((SoundEvent)subtitles.get(i).get(0)).getLocation().getPath());
            compoundTag.putDouble("distance"+i,(Double) subtitles.get(i).get(1));
        }
        return compoundTag;
    }

    public static List<List<Object>> loadSoundDataToTag(CompoundTag tag){
        List<List<Object>> subtitles = Lists.newArrayList();
        int size = tag.getInt("size");
        if(size==0){
            return subtitles;
        }else{
            for(int i =0;i<size;i++){
                List<Object> list = new ArrayList<>();
                list.add(createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(tag.getString("namespace"+i),tag.getString("path"+i))));
                list.add(tag.getDouble("distance"+i));
                subtitles.add(list);
            }
        }
        return subtitles;
    }

    public static void loadSoundDataToTag(CompoundTag tag,List<SoundEvent> list1,List<Double> list2){
        int size = tag.getInt("size");
        if(size!=0){
            for(int i =0;i<size;i++){
                list1.add(createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(tag.getString("namespace"+i),tag.getString("path"+i))));
                list2.add(tag.getDouble("distance"+i));
            }
        }
    }

    public static Vec3 getSoundVec3(SoundInstance soundInstance){
        return new Vec3(soundInstance.getX(),soundInstance.getY(),soundInstance.getZ());
    }
}
