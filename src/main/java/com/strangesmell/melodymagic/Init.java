package com.strangesmell.melodymagic;

import com.strangesmell.melodymagic.api.SoundEffect;
import com.strangesmell.melodymagic.item.CollectionItem;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityAttachment;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.strangesmell.melodymagic.MelodyMagic.*;
import static com.strangesmell.melodymagic.api.ViewUtil.getHitResult;

public class Init {
    public static ResourceLocation DEFALUTRES = ResourceLocation.fromNamespaceAndPath(MODID, "textures/effect_icon/img.png");

    //水下呼吸没有数量条件
    //雷击没有数量条件
    //warden没有数量条件
    //todo: 传入数量，依据数量造成伤害
    public void init() {
        initMaps();
        initSoundInf();
    }

    private static void initMaps() {
        CompoundTag compoundTag = new CompoundTag();

        compoundTag.putInt(SoundEvents.COW_AMBIENT.getLocation() + "num", 9);
        initAll("nine_cow", new HashSet<>(List.of(SoundEvents.COW_AMBIENT.getLocation().toString())), compoundTag, new SoundEffect() {
            @Override
            public void effect(Player player, Level level, InteractionHand pUsedHand, ItemStack itemStack) {
                player.addEffect(new MobEffectInstance(MobEffects.WATER_BREATHING, 200, 1));
            }

            @Override
            public String name(@Nullable Player player, @Nullable Level level, @Nullable InteractionHand pUsedHand, @Nullable CollectionItem collectionItem) {
                return "water_breath";
            }
        }, List.of(20, DEFALUTRES));

        initAll("water_breath", new HashSet<>(List.of(SoundEvents.WATER_AMBIENT.getLocation().toString())), compoundTag, new SoundEffect() {
            @Override
            public void effect(Player player, Level level, InteractionHand pUsedHand, ItemStack itemStack) {
                player.addEffect(new MobEffectInstance(MobEffects.WATER_BREATHING, 200, 1));
            }

            @Override
            public String name(@Nullable Player player, @Nullable Level level, @Nullable InteractionHand pUsedHand, @Nullable CollectionItem collectionItem) {
                return "water_breath";
            }
        }, List.of(5, DEFALUTRES));

        initAll("lightning_bolt_thunder", new HashSet<>(List.of(SoundEvents.LIGHTNING_BOLT_THUNDER.getLocation().toString())), compoundTag, new SoundEffect() {
            @Override
            public void effect(Player player, Level level, InteractionHand pUsedHand, ItemStack itemStack) {
                HitResult hitResult = getHitResult(level, player, 20);
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
        }, List.of(10, DEFALUTRES));

        initAll("warden_sonic_boon", new HashSet<>(List.of(SoundEvents.WARDEN_SONIC_BOOM.getLocation().toString())), compoundTag, new SoundEffect() {
            @Override
            public void effect(Player player, Level level, InteractionHand pUsedHand, ItemStack itemStack) {
                HitResult hitResult = getHitResult(level, player, 20);
                Vec3 vec3 = player.position().add(player.getAttachments().get(EntityAttachment.WARDEN_CHEST, 0, player.getYRot()));
                Vec3 vec31 = hitResult.getLocation().subtract(vec3);
                Vec3 vec32 = vec31.normalize();
                int i = Mth.floor(vec31.length()) + 7;
                ServerLevel serverLevel = (ServerLevel) level;
                for (int j = 1; j < i; j++) {
                    Vec3 vec33 = vec3.add(vec32.scale((double) j));
                    serverLevel.sendParticles(ParticleTypes.SONIC_BOOM, vec33.x, vec33.y, vec33.z, 1, 0.0, 0.0, 0.0, 0.0);
                }
                level.playSound(null, player.getOnPos(), SoundEvents.WARDEN_SONIC_BOOM, SoundSource.MASTER, 3.0F, 1.0F);

                if (hitResult instanceof EntityHitResult entityHitResult) {
                    if (entityHitResult.getEntity() instanceof LivingEntity livingEntity) {

                        if (livingEntity.hurt(serverLevel.damageSources().sonicBoom(player), 10.0F)) {
                            double d1 = 0.5 * (1.0 - livingEntity.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE));
                            double d0 = 2.5 * (1.0 - livingEntity.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE));
                            livingEntity.push(vec32.x() * d0, vec32.y() * d1, vec32.z() * d0);
                        }
                    }
                }

            }

            @Override
            public String name(@Nullable Player player, @Nullable Level level, @Nullable InteractionHand pUsedHand, @Nullable CollectionItem collectionItem) {
                return "warden_sonic_boon";
            }
        }, List.of(10, DEFALUTRES));

        initAll("village_reputation", new HashSet<>(List.of(SoundEvents.VILLAGER_YES.getLocation().toString()
   )), compoundTag, new SoundEffect() {
            @Override
            public void effect(Player player, Level level, InteractionHand pUsedHand, ItemStack itemStack) {
                player.addEffect(new MobEffectInstance(MobEffects.HERO_OF_THE_VILLAGE, 200, 1));
            }

            @Override
            public String name(@Nullable Player player, @Nullable Level level, @Nullable InteractionHand pUsedHand, @Nullable CollectionItem collectionItem) {
                return "village_reputation";
            }
        }, List.of(10, DEFALUTRES));


    }


    public static void initAll(String effectId, HashSet<String> soundEventSet, CompoundTag condition, SoundEffect soundEffect, List<Object> inf) {
        SOUND2KEY.put(soundEventSet, effectId);
        SOUND_LIST.add(soundEventSet);
        CONDITION.put(effectId, condition);
        Set<String> set = condition.getAllKeys();
        for (String s : set) {
            condition.remove(s);
        }
        KEY2EFFECT.put(effectId, soundEffect);
        initEffectInf(effectId, inf);
    }

    public void initSoundInf() {
        SOUND_INF.put(SoundEvents.LIGHTNING_BOLT_THUNDER.getLocation().toString(), 20);
    }

    //priority int,icon res
    public static void initEffectInf(String effectName, List<Object> list) {
        EFFECT_INF.put(effectName, list);

    }

    public static ResourceLocation res(String name, String path) {
        return ResourceLocation.fromNamespaceAndPath(name, path);

    }

    public static ResourceLocation defaultRes(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }


}
