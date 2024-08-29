package com.strangesmell.melodymagic.api;

import com.google.common.collect.Lists;
import com.strangesmell.melodymagic.MelodyMagic;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.*;

import static com.strangesmell.melodymagic.MelodyMagic.*;
import static net.minecraft.sounds.SoundEvent.createVariableRangeEvent;
import static net.minecraft.world.effect.MobEffects.*;

public class Util {
    public static List<Holder<MobEffect>> EFFECT_LIST = Arrays.asList(MOVEMENT_SPEED, MOVEMENT_SLOWDOWN, DIG_SPEED, DIG_SLOWDOWN, DAMAGE_BOOST, HEAL, HARM, JUMP, CONFUSION, REGENERATION
            , DAMAGE_RESISTANCE, FIRE_RESISTANCE, WATER_BREATHING, INVISIBILITY, BLINDNESS, NIGHT_VISION, HUNGER, WEAKNESS, POISON, WITHER, HEALTH_BOOST, ABSORPTION
            , SATURATION, GLOWING, LEVITATION, LUCK, UNLUCK, SLOW_FALLING, CONDUIT_POWER, DOLPHINS_GRACE, BAD_OMEN, HERO_OF_THE_VILLAGE, DARKNESS, TRIAL_OMEN, RAID_OMEN, WIND_CHARGED, WEAVING
            , OOZING, INFESTED);

    public static Holder<MobEffect> getRandomEffect() {
        Random random = new Random(net.minecraft.Util.getMillis());
        return EFFECT_LIST.get(random.nextInt(0, EFFECT_LIST.size()));
    }


    public static CompoundTag saveSoundDataToTag(List<SoundInstance> subtitles, List<List<Double>> location) {
        CompoundTag compoundTag = new CompoundTag();
        int size = subtitles.size();
        compoundTag.putInt("size", size);
        if (subtitles.isEmpty()) return compoundTag;
        for (int i = 0; i < size; i++) {

            compoundTag.putString("namespace" + i, subtitles.get(i).getLocation().getNamespace());
            compoundTag.putString("path" + i, subtitles.get(i).getLocation().getPath());
            compoundTag.putDouble("x" + i, location.get(i).get(0));
            compoundTag.putDouble("y" + i, location.get(i).get(0));
            compoundTag.putDouble("z" + i, location.get(i).get(0));
        }
        return compoundTag;
    }

    public static CompoundTag saveSoundDataToTag(List<List<Object>> subtitles) {
        CompoundTag compoundTag = new CompoundTag();
        int size = subtitles.size();
        compoundTag.putInt("size", size);
        if (subtitles.isEmpty()) return compoundTag;
        for (int i = 0; i < size; i++) {
            compoundTag.putString("namespace" + i, ((SoundEvent) subtitles.get(i).get(0)).getLocation().getNamespace());
            compoundTag.putString("path" + i, ((SoundEvent) subtitles.get(i).get(0)).getLocation().getPath());
            compoundTag.putDouble("distance" + i, (Double) subtitles.get(i).get(1));
        }
        return compoundTag;
    }

    public static CompoundTag saveSoundDataToTag(List<SoundInstance> subtitles, List<List<Double>> location,Map<ResourceLocation,String> subtitles2) {
        CompoundTag compoundTag = new CompoundTag();
        int size = subtitles.size();
        compoundTag.putInt("size", size);
        if (subtitles.isEmpty()) return compoundTag;
        for (int i = 0; i < size; i++) {
            compoundTag.putString("namespace" + i, subtitles.get(i).getLocation().getNamespace());
            compoundTag.putString("path" + i, subtitles.get(i).getLocation().getPath());
            compoundTag.putString("subtitle" + i, subtitles2.get(subtitles.get(i).getLocation()));
            compoundTag.putDouble("x" + i, location.get(i).get(0));
            compoundTag.putDouble("y" + i, location.get(i).get(0));
            compoundTag.putDouble("z" + i, location.get(i).get(0));
        }
        return compoundTag;
    }

    public static List<List<Object>> loadSoundDataToTag(CompoundTag tag) {
        List<List<Object>> subtitles = Lists.newArrayList();
        int size = tag.getInt("size");
        if (size == 0) {
            return subtitles;
        } else {
            for (int i = 0; i < size; i++) {
                List<Object> list = new ArrayList<>();
                list.add(createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(tag.getString("namespace" + i), tag.getString("path" + i))));
                list.add(tag.getDouble("distance" + i));
                subtitles.add(list);
            }
        }
        return subtitles;
    }

    public static void loadSoundDataToTag(CompoundTag tag, List<SoundEvent> list1, List<List<Double>> list2) {
        int size = tag.getInt("size");
        if (size != 0) {
            for (int i = 0; i < size; i++) {
                list1.add(createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(tag.getString("namespace" + i), tag.getString("path" + i))));
                List<Double> xyz = Lists.newArrayList();
                xyz.add(tag.getDouble("x" + i));
                xyz.add(tag.getDouble("y" + i));
                xyz.add(tag.getDouble("z" + i));
                list2.add(xyz);
            }
        }
    }

    public static void loadSoundDataToTag(CompoundTag tag, List<SoundEvent> list1, List<List<Double>> list2,List<String> subtitles2) {
        int size = tag.getInt("size");
        if (size != 0) {
            for (int i = 0; i < size; i++) {
                ResourceLocation res = ResourceLocation.fromNamespaceAndPath(tag.getString("namespace" + i), tag.getString("path" + i));
                SoundEvent soundEvent = createVariableRangeEvent(res);
                list1.add(soundEvent);
                subtitles2.add(tag.getString("subtitle" + i));
                List<Double> xyz = Lists.newArrayList();
                xyz.add(tag.getDouble("x" + i));
                xyz.add(tag.getDouble("y" + i));
                xyz.add(tag.getDouble("z" + i));
                list2.add(xyz);
            }
        }
    }

    public static Vec3 getSoundVec3(SoundInstance soundInstance) {
        return new Vec3(soundInstance.getX(), soundInstance.getY(), soundInstance.getZ());
    }


    public static boolean contain(List<SoundEvent> list1, SoundEvent soundEvent) {
        for (SoundEvent event : list1) {
            if (event.getLocation().equals(soundEvent.getLocation())) return true;
        }
        return false;
    }

    public static int indexOf(List<SoundEvent> list1, SoundEvent soundEvent) {
        for (int i = 0; i < list1.size(); i++) {
            if (list1.get(i).getLocation().equals(soundEvent.getLocation())) return i;
        }
        LOGGER.info("An error occurred while getting the index");
        LOGGER.info("The game doesn't crash but tooltip renders an error message");
        return 0;
    }

    public static void addSound2Key(HashSet<String> value, String key) {
        MelodyMagic.SOUND2KEY.put(value, key);
    }

    public static void addKey2Effect(String value, SoundEffect key) {
        MelodyMagic.KEY2EFFECT.put(value, key);
    }

    public static List<String> getKeyFromSound(List<SoundEvent> soundList, List<Integer> num) {
        HashSet<SoundEvent> soundHashSet = new HashSet<>(soundList);
        List<String> listString = new ArrayList<>();
        for (int i = 0; i < SOUND_LIST.size(); i++) {
            if(soundList.containsAll(SOUND_LIST.get(i))){
                listString.add(SOUND2KEY.get(SOUND_LIST.get(i)));
            }
        }
        return listString;
    }

    //这里判断是否满足条件，加入对应的key
    public static List<String> getKeyFromSoundRes(List<String> soundList, List<Integer> num) {
        List<String> listString = new ArrayList<>();
        for (int i = 0; i < SOUND_LIST.size(); i++) {
            if(soundList.containsAll(SOUND_LIST.get(i))){
                Boolean flag=true;
                List<String> strings=SOUND_LIST.get(i).stream().toList();//声音res组合
                CompoundTag compoundTag = CONDITION.get(SOUND2KEY.get(SOUND_LIST.get(i)));//条件集合
                for(int j=0;j<strings.size();j++){
                    if(compoundTag.contains(strings.get(j)+"num")){
                        if(num.get( soundList.indexOf(strings.get(j)))<compoundTag.getInt(strings.get(j)+"num")) flag=false;
                    }
                    //todo:别的条件
                }
                if(flag) listString.add(SOUND2KEY.get(SOUND_LIST.get(i)));
            }
        }
        return listString;
    }

    public static void getSoundEventNum(CompoundTag compoundTag, List<SoundEvent> soundEvents, List<Integer> num) {
        List<SoundEvent> subtitles = Lists.newArrayList();
        List<List<Double>> location = Lists.newArrayList();
        Util.loadSoundDataToTag(compoundTag.copy(), subtitles, location);
        for (int i = 0; i < subtitles.size(); i++) {
            if (!Util.contain(soundEvents, subtitles.get(i))) {
                soundEvents.add(subtitles.get(i));
                num.add(1);
            } else {
                int index = Util.indexOf(soundEvents, subtitles.get(i));
                num.set(index, num.get(index) + 1);
            }
        }
    }

    public static List<String> getSoundEventNum2(CompoundTag compoundTag, List<SoundEvent> soundEvents, List<Integer> num) {
        List<SoundEvent> subtitles = Lists.newArrayList();
        List<String> strings = Lists.newArrayList();
        List<List<Double>> location = Lists.newArrayList();
        Util.loadSoundDataToTag(compoundTag.copy(), subtitles, location);
        for (int i = 0; i < subtitles.size(); i++) {
            if (!Util.contain(soundEvents, subtitles.get(i))) {
                soundEvents.add(subtitles.get(i));
                strings.add(subtitles.get(i).getLocation().toString());
                num.add(1);
            } else {
                int index = Util.indexOf(soundEvents, subtitles.get(i));
                num.set(index, num.get(index) + 1);
            }
        }
        return strings;
    }

    public static void saveToCustomData(ItemStack itemStack,List<String> listString) {
        CompoundTag tag = new CompoundTag();
        tag.putInt("size",listString.size());
        for(int i=0;i<listString.size();i++){
            tag.putString("index"+i,listString.get(i));
        }

        CompoundTag compoundTag = itemStack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        compoundTag.put(MODID+"sound2key",tag);
        CustomData customData = CustomData.of(compoundTag);
        itemStack.set(DataComponents.CUSTOM_DATA,customData);
    }

    public static void saveToCustomData(ItemStack itemStack,List<String> listString,List<String> subtitles2 ) {
        CompoundTag tag = new CompoundTag();
        tag.putInt("size",listString.size());
        for(int i=0;i<listString.size();i++){
            tag.putString("index"+i,listString.get(i));
        }
        for(int i=0;i<subtitles2.size();i++){
            tag.putString("subtitles"+i,subtitles2.get(i));
        }

        CompoundTag compoundTag = itemStack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        compoundTag.put(MODID+"sound2key",tag);
        CustomData customData = CustomData.of(compoundTag);
        itemStack.set(DataComponents.CUSTOM_DATA,customData);
    }

    public static List<String> getFromCustomData(ItemStack itemStack) {
        CompoundTag compoundTag = itemStack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        CompoundTag tag =(CompoundTag) compoundTag.get(MODID+"sound2key");
        List<String> listString = new ArrayList<>();
        int size = tag.getInt("size");
        for(int i=0;i<size;i++){
            listString.add(tag.getString("index"+i));
        }
        return listString;
    }

    public static List<SoundEffect> getSoundEffect(ItemStack itemStack) {
        CompoundTag compoundTag = itemStack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        CompoundTag tag =(CompoundTag) compoundTag.get(MODID+"sound2key");
        List<SoundEffect> listEffect = new ArrayList<>();
        int size = tag.getInt("size");
        for(int i=0;i<size;i++){
            listEffect.add(KEY2EFFECT.get(tag.getString("index"+i)));
        }
        return listEffect;
    }

    public static List<SoundEffect> getSoundEffectWithCondition(ItemStack itemStack) {
        CompoundTag compoundTag = itemStack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        CompoundTag tag =(CompoundTag) compoundTag.get(MODID+"sound2key");
        CompoundTag condition ;
        List<SoundEffect> listEffect = new ArrayList<>();
        int size = tag.getInt("size");
        for(int i=0;i<size;i++){
            condition = CONDITION.get(tag.getString("index"+i));
            if(condition.contains("num")){

            }
            listEffect.add(KEY2EFFECT.get(tag.getString("index"+i)));
        }
        return listEffect;
    }

    public static void effect(List<SoundEvent> subtitles, List<List<Double>> distance, Level pLevel, Player pPlayer, InteractionHand pUsedHand){
        MobEffectInstance effectInstance = new MobEffectInstance(getRandomEffect(), 180, 1);
        pPlayer.addEffect(effectInstance);
    }

    public static void playSoundFromItem(ItemStack itemStack, Level level ,float x,float y,float z) {
        List<SoundEvent> subtitles = Lists.newArrayList();
        List<List<Double>> location = Lists.newArrayList();
        if(itemStack.get(DataComponents.CUSTOM_DATA)==null) return ;
        Util.loadSoundDataToTag(itemStack.get(DataComponents.CUSTOM_DATA).copyTag(),subtitles,location);
        //if(level instanceof ServerLevel) effect(subtitles,location, pLevel,  pPlayer,  pUsedHand);
        if(!subtitles.isEmpty()){
            for (int i =0;i<subtitles.size();i++) {
                level.playSound(null,location.get(i).get(0)+x,location.get(i).get(1)+y,location.get(i).get(2)+z,subtitles.get(i),SoundSource.MASTER );
            }
        }
    }
}
