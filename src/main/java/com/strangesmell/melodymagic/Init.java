package com.strangesmell.melodymagic;

import com.strangesmell.melodymagic.api.SoundEffect;
import com.strangesmell.melodymagic.item.CollectionItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.List;

import static com.strangesmell.melodymagic.MelodyMagic.*;
import static com.strangesmell.melodymagic.api.ViewUtil.getHitResult;

public class Init {
    public static ResourceLocation DEFALUTRES = ResourceLocation.fromNamespaceAndPath(MODID,"textures/effect_icon/img.png");

    public void init() {
        initMaps();
        initSoundInf();
    }

    private static void initMaps(){
        SOUND2KEY.put(new HashSet<>(List.of(SoundEvents.COW_AMBIENT.getLocation().toString())),"nine_cow");
        SOUND2KEY.put(new HashSet<>(List.of(SoundEvents.WATER_AMBIENT.getLocation().toString())),"water_breath");
        SOUND2KEY.put(new HashSet<>(List.of(SoundEvents.LIGHTNING_BOLT_THUNDER.getLocation().toString())),"lightning_bolt_thunder");

        SOUND_LIST.add(new HashSet<>(List.of(SoundEvents.COW_AMBIENT.getLocation().toString())));
        SOUND_LIST.add(new HashSet<>(List.of(SoundEvents.WATER_AMBIENT.getLocation().toString())));
        SOUND_LIST.add(new HashSet<>(List.of(SoundEvents.LIGHTNING_BOLT_THUNDER.getLocation().toString())));

        CompoundTag compoundTag =new CompoundTag();
        compoundTag.putInt(SoundEvents.COW_AMBIENT.getLocation()+"num",9);
        CONDITION.put("nine_cow",compoundTag);//数量
        //水下呼吸没有数量条件
        //雷击没有数量条件


        KEY2EFFECT.put("nine_cow", new SoundEffect() {
            @Override
            public void effect(Player player, Level level, InteractionHand pUsedHand, ItemStack itemStack) {
                player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 200, 1));
            }
            @Override
            public String name(@Nullable Player player, @Nullable Level level, @Nullable InteractionHand pUsedHand, @Nullable CollectionItem collectionItem) {
                return "nine_cow_text";
            }
        });
        initEffectInf("nine_cow",List.of(5, DEFALUTRES));


        KEY2EFFECT.put("water_breath", new SoundEffect() {
            @Override
            public void effect(Player player, Level level, InteractionHand pUsedHand, ItemStack itemStack) {
                player.addEffect(new MobEffectInstance(MobEffects.WATER_BREATHING, 200, 1));
            }
            @Override
            public String name(@Nullable Player player, @Nullable Level level, @Nullable InteractionHand pUsedHand, @Nullable CollectionItem collectionItem) {
                return "water_breath";
            }
        });
        initEffectInf("water_breath",List.of(5, DEFALUTRES));


        //todo: 传入数量，依据数量造成伤害
        KEY2EFFECT.put("lightning_bolt_thunder", new SoundEffect() {
            @Override
            public void effect(Player player, Level level, InteractionHand pUsedHand, ItemStack itemStack) {
                HitResult hitResult = getHitResult(level,player,20);
                LightningBolt bolt = EntityType.LIGHTNING_BOLT.create(level);
                assert bolt != null;
                bolt.addTag(MelodyMagic.MODID);
                bolt.moveTo(hitResult.getLocation());
                bolt.setCause((ServerPlayer) player);
                bolt.setDamage(5);
                level.addFreshEntity(bolt);
            }
            @Override
            public String name(@Nullable Player player, @Nullable Level level, @Nullable InteractionHand pUsedHand, @Nullable CollectionItem collectionItem) {
                return "lightning_bolt_thunder";
            }
        });
        initEffectInf("lightning_bolt_thunder",List.of(20, DEFALUTRES));
    }








    public void initSoundInf() {
        SOUND_INF.put(SoundEvents.LIGHTNING_BOLT_THUNDER.getLocation().toString(),20);

    }

    //priority int,icon res
    public static void initEffectInf(String effectName, List<Object> list) {
        EFFECT_INF.put(effectName,list);

    }
    public static ResourceLocation res(String name,String path) {
        return ResourceLocation.fromNamespaceAndPath(name,path);

    }
    public static ResourceLocation defaultRes(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID,path);
    }
}
