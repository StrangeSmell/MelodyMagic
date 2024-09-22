package com.strangesmell.melodymagic.api;

import com.google.common.collect.Lists;
import com.strangesmell.melodymagic.MelodyMagic;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.*;

import static com.strangesmell.melodymagic.MelodyMagic.*;
import static net.minecraft.sounds.SoundEvent.createVariableRangeEvent;

public class Util {
    public static Holder<MobEffect> getRandomEffect() {
        Random random = new Random();
        int id = random.nextInt(0, BuiltInRegistries.MOB_EFFECT.size());
        return BuiltInRegistries.MOB_EFFECT.getHolder(id).get();
    }

    public static CompoundTag saveSoundDataToTag(List<SoundInstance> subtitles, List<List<Double>> location,Map<ResourceLocation,String> subtitles2,List<Float> range,List<Float> volume,List<Float> peach) {
        CompoundTag compoundTag = new CompoundTag();
        int size = subtitles.size();
        compoundTag.putInt("creat_size", size);
        if (subtitles.isEmpty()) return compoundTag;
        List<Integer> num = new ArrayList<>();
        List<ResourceLocation> soundResList = new ArrayList<>();
        List<String> soundSubitlesList = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            if(!soundResList.contains(subtitles.get(i).getLocation())) {
                soundResList.add(subtitles.get(i).getLocation());
                num.add(1);
                soundSubitlesList.add(subtitles2.get(subtitles.get(i).getLocation()));
            }else{
                int ind= soundResList.indexOf(subtitles.get(i).getLocation());
                num.set(ind,num.get(ind)+1);
            }
            compoundTag.putString("namespace" + i, subtitles.get(i).getLocation().getNamespace());
            compoundTag.putString("path" + i, subtitles.get(i).getLocation().getPath());
            compoundTag.putString("subtitle" + i, subtitles2.get(subtitles.get(i).getLocation()));
            compoundTag.putDouble("x" + i, location.get(i).get(0));
            compoundTag.putDouble("y" + i, location.get(i).get(1));
            compoundTag.putDouble("z" + i, location.get(i).get(2));
            compoundTag.putFloat("range" + i, range.get(i));
            compoundTag.putFloat("volume" + i, volume.get(i));
            compoundTag.putFloat("peach" + i, peach.get(i));
        }
        int cooldown=0;//又算了一次
        for (int i = 0; i < num.size(); i++) {//聚合过后的数据
            if(SOUND_INF.get(soundResList.get(i).toString())==null){
                cooldown = cooldown +5*num.get(i);
            }else {
                cooldown = cooldown+SOUND_INF.get(soundResList.get(i).toString())*num.get(i);
            }
            compoundTag.putString("gather_sound_namespace" + i, soundResList.get(i).getNamespace());
            compoundTag.putString("gather_sound_path" + i, soundResList.get(i).getPath());
            compoundTag.putString("gather_sound_subtitles" + i, soundSubitlesList.get(i));
            compoundTag.putInt("gather_sound_num" + i, num.get(i));
        }
        compoundTag.putInt("gather_size", num.size());
        compoundTag.putInt("cooldown", cooldown);
        return compoundTag;
    }

    //只哪gather data
    public static void loadSoundDataFromTag(List<Integer> num,List<String> res,ItemStack itemStack) {
        CompoundTag tag = itemStack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        int size = tag.getInt("gather_size");
        if (size != 0) {
            for (int i = 0; i < size; i++) {
                num.add(tag.getInt("gather_sound_num" + i));
                res.add(ResourceLocation.fromNamespaceAndPath(tag.getString("gather_sound_namespace" + i), tag.getString("gather_sound_path" + i)).toString());
            }
        }
    }

    public static List<List<Object>> loadSoundDataFromTag(CompoundTag tag) {
        List<List<Object>> subtitles = Lists.newArrayList();
        int size = tag.getInt("creat_size");
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

    public static void loadSoundDataFromTag(CompoundTag tag, List<SoundEvent> list1, List<List<Double>> list2) {
        int size = tag.getInt("creat_size");
        if (size != 0) {
            for (int i = 0; i < size; i++) {
                ResourceLocation resourceLocation =ResourceLocation.fromNamespaceAndPath(tag.getString("namespace" + i),tag.getString("path" + i));
                SoundEvent soundEvent =SoundEvent.createFixedRangeEvent(resourceLocation,tag.getFloat("range" + i));
                list1.add(soundEvent);
                List<Double> xyz = Lists.newArrayList();
                xyz.add(tag.getDouble("x" + i));
                xyz.add(tag.getDouble("y" + i));
                xyz.add(tag.getDouble("z" + i));
                list2.add(xyz);
            }
        }
    }



    public static void loadSoundDataFromTag(CompoundTag tag, List<SoundEvent> list1, List<List<Double>> list2, List<String> subtitles2) {
        int size = tag.getInt("creat_size");
        if (size != 0) {
            for (int i = 0; i < size; i++) {
                ResourceLocation resourceLocation =ResourceLocation.fromNamespaceAndPath(tag.getString("namespace" + i),tag.getString("path" + i));
                SoundEvent soundEvent =SoundEvent.createFixedRangeEvent(resourceLocation,tag.getFloat("range" + i));
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

    public static void loadSoundDataFromTag(CompoundTag tag, List<SoundEvent> list1, List<List<Double>> list2, List<Float> volume, List<Float> peach) {
        int size = tag.getInt("creat_size");
        if (size != 0) {
            for (int i = 0; i < size; i++) {
                ResourceLocation resourceLocation =ResourceLocation.fromNamespaceAndPath(tag.getString("namespace" + i),tag.getString("path" + i));
                SoundEvent soundEvent =SoundEvent.createFixedRangeEvent(resourceLocation,tag.getFloat("range" + i));
                list1.add(soundEvent);
                List<Double> xyz = Lists.newArrayList();
                xyz.add(tag.getDouble("x" + i));
                xyz.add(tag.getDouble("y" + i));
                xyz.add(tag.getDouble("z" + i));
                list2.add(xyz);
                volume.add(tag.getFloat("volume" + i));
                peach.add(tag.getFloat("peach" + i));
            }
        }
    }

    public static void loadSoundDataFromTag(CompoundTag tag, List<SoundEvent> list1, List<List<Double>> list2,List<String> subtitles2,List<Float> range,List<Float> volume,List<Float> peach) {
        int size = tag.getInt("creat_size") ;
        if (size != 0) {
            for (int i = 0; i < size; i++) {
                ResourceLocation resourceLocation =ResourceLocation.fromNamespaceAndPath(tag.getString("namespace" + i),tag.getString("path" + i));
                SoundEvent soundEvent =SoundEvent.createFixedRangeEvent(resourceLocation,tag.getFloat("range" + i));
                list1.add(soundEvent);
                subtitles2.add(tag.getString("subtitle" + i));
                List<Double> xyz = Lists.newArrayList();
                xyz.add(tag.getDouble("x" + i));
                xyz.add(tag.getDouble("y" + i));
                xyz.add(tag.getDouble("z" + i));
                list2.add(xyz);
                range.add(tag.getFloat("range" + i));
                volume.add(tag.getFloat("volume" + i));
                peach.add(tag.getFloat("peach" + i));
            }
        }
    }

    public static int getNumOfUntranslate(ItemStack itemStack) {
        CompoundTag compoundTag = itemStack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();

        int size = compoundTag.getInt("untranslate_size");
        int num=0;
        if (size != 0) {
            for (int i = 0; i < size; i++) {
                if(compoundTag.getString("subtitle" + i).equals("untranslated_sound")) num++;
            }
        }
        return num;
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

    public static boolean contain2(List<SoundInstance> list1, SoundInstance soundInstance) {
        if(list1.isEmpty()) return false;
        for (SoundInstance event : list1) {
            if (event.getLocation().equals(soundInstance.getLocation())) return true;
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

    public static int indexOf2(List<SoundInstance> list1, SoundInstance soundEvent) {
        for (int i = 0; i < list1.size(); i++) {
            if (list1.get(i).getLocation().equals(soundEvent.getLocation())) return i;
        }
        LOGGER.info("An error occurred while getting the index");
        LOGGER.info("The game doesn't crash but tooltip renders an error message");
        return 0;
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
                    if(compoundTag ==null) break;
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

    public static void getSoundEventNum(CompoundTag compoundTag, List<SoundEvent> soundEvents, List<Integer> num,List<Float> volume,List<Float> peach) {
        List<SoundEvent> subtitles = Lists.newArrayList();
        List<List<Double>> location = Lists.newArrayList();
        Util.loadSoundDataFromTag(compoundTag.copy(), subtitles, location,volume,peach);
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

    public static List<String> getSoundEventNum2(CompoundTag compoundTag, List<SoundEvent> soundEvents, List<Integer> num,List<String> subtitle) {
        List<SoundEvent> subtitles = Lists.newArrayList();
        List<String> strings = Lists.newArrayList();
        List<String> sub = Lists.newArrayList();
        List<List<Double>> location = Lists.newArrayList();
        Util.loadSoundDataFromTag(compoundTag.copy(), subtitles, location,sub);
        for (int i = 0; i < subtitles.size(); i++) {
            if (!Util.contain(soundEvents, subtitles.get(i))) {
                soundEvents.add(subtitles.get(i));
                strings.add(subtitles.get(i).getLocation().toString());
                subtitle.add(sub.get(i));
                num.add(1);
            } else {
                int index = Util.indexOf(soundEvents, subtitles.get(i));
                num.set(index, num.get(index) + 1);
            }
        }
        return strings;
    }

    public static List<String> getSoundEventNum3(List<SoundInstance> soundInstanceList, List<Integer> num) {
        List<SoundInstance> soundInstances = Lists.newArrayList();
        List<String> location = Lists.newArrayList();
        for (int i = 0; i < soundInstanceList.size(); i++) {
            if (!Util.contain2(soundInstances, soundInstanceList.get(i))) {
                soundInstances.add(soundInstanceList.get(i));
                num.add(1);
                location.add(soundInstanceList.get(i).getLocation().toString());
            } else {
                int index = Util.indexOf2(soundInstances, soundInstanceList.get(i));
                num.set(index, num.get(index) + 1);
            }
        }
        return location;
    }


    public static void saveToCustomData(ItemStack itemStack,List<String> listString) {
        CompoundTag tag = new CompoundTag();
        tag.putInt("effect_size",listString.size());
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
        tag.putInt("effect_size",listString.size());
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
        int size = tag.getInt("effect_size");
        for(int i=0;i<size;i++){
            listString.add(tag.getString("index"+i));
        }
        return listString;
    }

    public static List<SoundEffect> getSoundEffect(ItemStack itemStack) {
        CompoundTag compoundTag = itemStack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        CompoundTag tag =(CompoundTag) compoundTag.get(MODID+"sound2key");
        if(tag ==null) tag = new CompoundTag();
        List<SoundEffect> listEffect = new ArrayList<>();
        int size =0;
        if(tag.contains("effect_size")){size=tag.getInt("effect_size");}
        for(int i=0;i<size;i++){
            listEffect.add(KEY2EFFECT.get(tag.getString("index"+i)));
        }
        return listEffect;
    }

    public static List<String> getSoundEffectToString(ItemStack itemStack) {
        CompoundTag compoundTag = itemStack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        CompoundTag tag =(CompoundTag) compoundTag.get(MODID+"sound2key");
        List<String> listEffectString = new ArrayList<>();
        int size = tag.getInt("effect_size");
        for(int i=0;i<size;i++){
            listEffectString.add(tag.getString("index"+i));
        }
        return listEffectString;
    }

    public static void effect(List<SoundEvent> subtitles, List<List<Double>> distance, Level pLevel, Player pPlayer, InteractionHand pUsedHand){
        MobEffectInstance effectInstance = new MobEffectInstance(getRandomEffect(), 180, 1);
        pPlayer.addEffect(effectInstance);
    }

    public static void playSoundFromItem(ItemStack itemStack, Level level ,float x,float y,float z) {
        List<SoundEvent> subtitles = Lists.newArrayList();
        List<List<Double>> location = Lists.newArrayList();
        if(itemStack.get(DataComponents.CUSTOM_DATA)==null) return ;
        Util.loadSoundDataFromTag(itemStack.get(DataComponents.CUSTOM_DATA).copyTag(),subtitles,location);
        //if(level instanceof ServerLevel) effect(subtitles,location, pLevel,  pPlayer,  pUsedHand);
        if(!subtitles.isEmpty()){
            for (int i =0;i<subtitles.size();i++) {
                level.playSound(null,location.get(i).get(0)+x,location.get(i).get(1)+y,location.get(i).get(2)+z,subtitles.get(i),SoundSource.MASTER );
            }
        }
    }

    public static void putResToTag(CompoundTag tag, ResourceLocation res,int i) {
        tag.putString("namespace" + i,res.getNamespace());
        tag.putString("path" + i,res.getPath());
    }

    public static ResourceLocation getResFromTag(CompoundTag tag,int i) {
        return ResourceLocation.fromNamespaceAndPath(tag.getString("namespace" + i),tag.getString("path" + i));
    }

    public static SoundEvent getSoundEventFromTag(CompoundTag tag,int i) {
        ResourceLocation resourceLocation =ResourceLocation.fromNamespaceAndPath(tag.getString("namespace" + i),tag.getString("path" + i));
        return SoundEvent.createFixedRangeEvent(resourceLocation,tag.getFloat("range" + i));
    }

    public static void  setBlock(Level level , BlockPos pos, BlockState blockState) {
        if(!level.getBlockState(pos).getBlock().getDescriptionId().equals(Blocks.BEDROCK.getDescriptionId())&&!level.getBlockState(pos).getBlock().getDescriptionId().equals(FAKE_NETHER_PORTAL.get().getDescriptionId())) level.setBlock(pos, blockState, 2);
    }
}
