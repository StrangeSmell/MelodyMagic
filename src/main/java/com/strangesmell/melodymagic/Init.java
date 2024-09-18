package com.strangesmell.melodymagic;

import com.strangesmell.melodymagic.api.EffectUtil;
import com.strangesmell.melodymagic.api.SoundEffect;
import com.strangesmell.melodymagic.api.Util;
import com.strangesmell.melodymagic.block.FakeNetherPortal;
import com.strangesmell.melodymagic.block.FakeNetherPortalBlockEntity;
import com.strangesmell.melodymagic.item.CollectionItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.animal.Fox;
import net.minecraft.world.entity.npc.CatSpawner;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.*;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BoneMealItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static com.strangesmell.melodymagic.MelodyMagic.*;
import static com.strangesmell.melodymagic.api.Util.setBlock;
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
                if(level.isClientSide)return;
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
                if(level.isClientSide)return;
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
                if(level.isClientSide)return;
                List<Integer> num = new ArrayList<>();
                List<String> res = new ArrayList<>();
                Util.loadSoundDataFromTag(num,res,itemStack);
                int size=0;
                if(res.contains(SoundEvents.LIGHTNING_BOLT_THUNDER.getLocation().toString())) {
                    size=num.get(res.indexOf(SoundEvents.LIGHTNING_BOLT_THUNDER.getLocation().toString()));
                }

                HitResult hitResult = getHitResult(level, player, 20+size);
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
                if(level.isClientSide)return;
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
        initAll("village_reputation", new HashSet<>(List.of(SoundEvents.VILLAGER_YES.getLocation().toString())), compoundTag, new SoundEffect() {
            @Override
            public void effect(Player player, Level level, InteractionHand pUsedHand, ItemStack itemStack) {
                if(level.isClientSide)return;
                List<Integer> num = new ArrayList<>();
                List<String> res = new ArrayList<>();
                Util.loadSoundDataFromTag(num,res,itemStack);
                int size=0;
                if(res.contains(SoundEvents.VILLAGER_YES.getLocation().toString())) {
                    size=num.get(res.indexOf(SoundEvents.VILLAGER_YES.getLocation().toString()));
                }
                player.addEffect(new MobEffectInstance(MobEffects.HERO_OF_THE_VILLAGE, 200,size));
            }

            @Override
            public String name(@Nullable Player player, @Nullable Level level, @Nullable InteractionHand pUsedHand, @Nullable CollectionItem collectionItem) {
                return "village_reputation";
            }
        }, List.of(10, DEFALUTRES));
        initAll("eat", new HashSet<>(List.of(SoundEvents.PLAYER_BURP.getLocation().toString())), compoundTag, new SoundEffect() {
            @Override
            public void effect(Player player, Level level, InteractionHand pUsedHand, ItemStack itemStack) {
                if(level.isClientSide)return;
                List<Integer> num = new ArrayList<>();
                List<String> res = new ArrayList<>();
                Util.loadSoundDataFromTag(num,res,itemStack);
                int size=0;
                for(int i =0;i<res.size();i++) {
                    if(res.get(i).contains(".eat")) {
                        size=num.get(i)+size;
                    }
                }
                for(int i=0;i<size;i++){
                    player.eat(level,Items.POTATO.getDefaultInstance(), Objects.requireNonNull(Items.POTATO.getDefaultInstance().getFoodProperties(player)));
                }
        }

            @Override
            public String name(@Nullable Player player, @Nullable Level level, @Nullable InteractionHand pUsedHand, @Nullable CollectionItem collectionItem) {
                return "eat";
            }
        }, List.of(10, DEFALUTRES));
        initAll("bow", new HashSet<>(List.of(SoundEvents.ARROW_SHOOT.getLocation().toString())), compoundTag, new SoundEffect() {
            @Override
            public void effect(Player player, Level level, InteractionHand pUsedHand, ItemStack itemStack) {
                if(level.isClientSide)return;
                List<Integer> num = new ArrayList<>();
                List<String> res = new ArrayList<>();
                Util.loadSoundDataFromTag(num,res,itemStack);
                int size=0;
                for(int i =0;i<res.size();i++) {
                    if(res.get(i).contains(".shoot")) {
                        size=num.get(i)+size;
                    }
                }
                List<ItemStack> draw = new ArrayList<>();
                draw.add(Items.APPLE.getDefaultInstance());
                EffectUtil.shoot((ServerLevel) level,player,pUsedHand,Items.BOW.getDefaultInstance(),draw,3,1,true,null);
            }

            @Override
            public String name(@Nullable Player player, @Nullable Level level, @Nullable InteractionHand pUsedHand, @Nullable CollectionItem collectionItem) {
                return "bow";
            }
        }, List.of(20, DEFALUTRES));
        initAll("bone_meal", new HashSet<>(List.of(SoundEvents.BONE_MEAL_USE.getLocation().toString())), compoundTag, new SoundEffect() {
            @Override
            public void effect(Player player, Level level, InteractionHand pUsedHand, ItemStack itemStack) {
                if(level.isClientSide)return;
                HitResult hitResult = getHitResult(level, player, player.entityInteractionRange());
                if(hitResult instanceof BlockHitResult blockHitResult) {
                    if(!level.getBlockState(blockHitResult.getBlockPos()).getTags().anyMatch(tag->tag.equals(BlockTags.FLOWERS))){
                        UseOnContext useOnContext = new UseOnContext(player,pUsedHand,blockHitResult);
                        BoneMealItem.applyBonemeal(Items.BONE_MEAL.getDefaultInstance(),level,blockHitResult.getBlockPos(),player);
                        level.playSound(null,blockHitResult.getBlockPos(),SoundEvents.BONE_MEAL_USE,SoundSource.MASTER);
                        return;
                    }else{
                        Block flower = level.getBlockState(blockHitResult.getBlockPos()).getBlock();
                        BlockPos blockPos = blockHitResult.getBlockPos();


                        for(int i=0;i<200;i++){
                            int dx =level.random.nextInt(-12,13);
                            int dy =level.random.nextInt(-3,4);
                            int dz =level.random.nextInt(-12,13);
                            if(level.getBlockState(blockPos.offset(dx,dy,dz)).getTags().anyMatch(tag->tag.equals(BlockTags.DIRT))&&level.getBlockState(blockPos.offset(dx,dy+1,dz)).getBlock() instanceof AirBlock){
                                if(level.random.nextInt(0,100)<90){
                                    level.setBlock(blockPos.offset(dx,dy+1,dz),flower.defaultBlockState(),2);
                                }
                            }
                        }
                    }

                }
            }

            @Override
            public String name(@Nullable Player player, @Nullable Level level, @Nullable InteractionHand pUsedHand, @Nullable CollectionItem collectionItem) {
                return "bone_meal";
            }
        }, List.of(10, DEFALUTRES));
        initAll("cat", new HashSet<>(List.of(SoundEvents.CAT_PURR.getLocation().toString())), compoundTag, new SoundEffect() {
            @Override
            public void effect(Player player, Level level, InteractionHand pUsedHand, ItemStack itemStack) {
                if(level.isClientSide)return;
                Cat cat = EntityType.CAT.create(level);
                if(cat != null) {
                    cat.finalizeSpawn((ServerLevel)level,level.getCurrentDifficultyAt(player.getOnPos()), MobSpawnType.NATURAL, null);
                    cat.tame(player);
                    cat.setAge(-24000);
                    level.addFreshEntity(cat);
                }
            }

            @Override
            public String name(@Nullable Player player, @Nullable Level level, @Nullable InteractionHand pUsedHand, @Nullable CollectionItem collectionItem) {
                return "cat";
            }
        }, List.of(100, DEFALUTRES));
        initAll("bat", new HashSet<>(List.of(SoundEvents.BAT_AMBIENT.getLocation().toString())), compoundTag, new SoundEffect() {
            @Override
            public void effect(Player player, Level level, InteractionHand pUsedHand, ItemStack itemStack) {
                if(level.isClientSide)return;
                TargetingConditions alertableTargeting = TargetingConditions.forCombat()
                        .range(12.0)
                        .ignoreLineOfSight()
                        .selector(null);

                List<LivingEntity> list = level.getNearbyEntities(LivingEntity.class,alertableTargeting , player, player.getBoundingBox().inflate(4,3,4));
                for(LivingEntity livingEntity : list) {
                    livingEntity.addEffect(new MobEffectInstance(MobEffects.GLOWING, 1000, 1));
                }

            }

            @Override
            public String name(@Nullable Player player, @Nullable Level level, @Nullable InteractionHand pUsedHand, @Nullable CollectionItem collectionItem) {
                return "bat";
            }
        }, List.of(10, DEFALUTRES));
        initAll("dolphin", new HashSet<>(List.of(SoundEvents.DOLPHIN_AMBIENT_WATER.getLocation().toString())), compoundTag, new SoundEffect() {
            @Override
            public void effect(Player player, Level level, InteractionHand pUsedHand, ItemStack itemStack) {
                if(level.isClientSide)return;
                player.addEffect(new MobEffectInstance(MobEffects.DOLPHINS_GRACE, 1000, 1));
            }

            @Override
            public String name(@Nullable Player player, @Nullable Level level, @Nullable InteractionHand pUsedHand, @Nullable CollectionItem collectionItem) {
                return "dolphin";
            }
        }, List.of(10, DEFALUTRES));
        initAll("dragon_breath", new HashSet<>(List.of(SoundEvents.ENDER_DRAGON_SHOOT.getLocation().toString())), compoundTag, new SoundEffect() {
            @Override
            public void effect(Player player, Level level, InteractionHand pUsedHand, ItemStack itemStack) {
                if(level.isClientSide)return;
                HitResult hitResult = getHitResult(level, player, 1);
                Vec3 eye = player.getEyePosition();
                Vec3 vec31 = hitResult.getLocation().subtract(eye).normalize();
                DragonFireball dragonFireball = EntityType.DRAGON_FIREBALL.create(level);
                dragonFireball.setOwner(player);
                dragonFireball.setPos(player.getX(), player.getY(), player.getZ());
                dragonFireball.setDeltaMovement(vec31.x,vec31.y,vec31.z);
                level.addFreshEntity(dragonFireball);
            }

            @Override
            public Item displayTex() {
                return Items.DRAGON_HEAD;
            }

            @Override
            public String name(@Nullable Player player, @Nullable Level level, @Nullable InteractionHand pUsedHand, @Nullable CollectionItem collectionItem) {
                return "dragon_breath";
            }
        }, List.of(10, DEFALUTRES));
        initAll("nether_portal", new HashSet<>(List.of(SoundEvents.PORTAL_AMBIENT.getLocation().toString())), compoundTag, new SoundEffect() {
            @Override
            public void effect(Player player, Level level, InteractionHand pUsedHand, ItemStack itemStack) {

                List<Integer> num = new ArrayList<>();
                List<String> res = new ArrayList<>();
                Util.loadSoundDataFromTag(num,res,itemStack);
                int size=0;
                for(int i =0;i<res.size();i++) {
                    if(res.get(i).contains(SoundEvents.PORTAL_AMBIENT.getLocation().toString())) {
                        size=num.get(i)+size;
                    }
                }
                HitResult hitResult = getHitResult(level, player, 10);
                Vec3 eye = player.getEyePosition();
                Vec3 vec31 = hitResult.getLocation().subtract(eye).normalize();
                BlockState blockState = FAKE_NETHER_PORTAL.get().defaultBlockState();
                BlockState blockState2 = Blocks.OBSIDIAN.defaultBlockState();
                boolean x=true;
                if(Math.abs(vec31.x)>Math.abs(vec31.z)){
                    blockState = blockState.setValue(FakeNetherPortal.AXIS, Direction.Axis.Z);
                    x=false;
                }
                if(hitResult instanceof BlockHitResult blockHitResult) {
                    BlockPos pos = blockHitResult.getBlockPos();

                    setBlock(level,pos.offset(0,1,0), blockState2);
                    setBlock(level,pos.offset(0,4,0), blockState2);
                    if(x){
                        for(int i=1;i<5;i++){
                            setBlock(level,pos.offset(1,i,0), blockState2);
                            setBlock(level,pos.offset(-1,i,0), blockState2);
                        }

                    }else{
                        for(int i=1;i<5;i++){
                            setBlock(level,pos.offset(0,i,1), blockState2);
                            setBlock(level,pos.offset(0,i,-1), blockState2);
                        }
                    }

                    setBlock(level,pos.offset(0,2,0), blockState);
                    setBlock(level,pos.offset(0,3,0), blockState);
                    if(level.getBlockEntity(pos.offset(0,2,0)) instanceof FakeNetherPortalBlockEntity fakeNetherPortalBlockEntity){
                        fakeNetherPortalBlockEntity.num= 100*size;
                    }
                    if(level.getBlockEntity(pos.offset(0,3,0)) instanceof FakeNetherPortalBlockEntity fakeNetherPortalBlockEntity){
                        fakeNetherPortalBlockEntity.num= 100*size;
                    }

                }

            }

            @Override
            public String name(@Nullable Player player, @Nullable Level level, @Nullable InteractionHand pUsedHand, @Nullable CollectionItem collectionItem) {
                return "nether_portal";
            }
        }, List.of(10, DEFALUTRES));
        initAll("blaze", new HashSet<>(List.of(SoundEvents.BLAZE_SHOOT.getLocation().toString())), compoundTag, new SoundEffect() {
            @Override
            public void effect(Player player, Level level, InteractionHand pUsedHand, ItemStack itemStack) {
                if(level.isClientSide)return;
                HitResult hitResult = getHitResult(level, player, 1);
                Vec3 eye = player.getEyePosition();
                Vec3 vec31 = hitResult.getLocation().subtract(eye).normalize();
                for(int i=0;i<3;i++){
                    SmallFireball smallFireball = EntityType.SMALL_FIREBALL.create(level);
                    smallFireball.setOwner(player);
                    smallFireball.setPos(player.getX(), player.getY(), player.getZ());
                    smallFireball.setDeltaMovement(vec31.x,vec31.y,vec31.z);
                    level.addFreshEntity(smallFireball);
                }
            }

            @Override
            public String name(@Nullable Player player, @Nullable Level level, @Nullable InteractionHand pUsedHand, @Nullable CollectionItem collectionItem) {
                return "blaze";
            }
        }, List.of(10, DEFALUTRES));
        initAll("ghast", new HashSet<>(List.of(SoundEvents.GHAST_SHOOT.getLocation().toString())), compoundTag, new SoundEffect() {
            @Override
            public void effect(Player player, Level level, InteractionHand pUsedHand, ItemStack itemStack) {
                if(level.isClientSide)return;
                HitResult hitResult = getHitResult(level, player, 1);
                Vec3 eye = player.getEyePosition();
                Vec3 vec31 = hitResult.getLocation().subtract(eye).normalize();
                LargeFireball largeFireball = EntityType.FIREBALL.create(level);
                largeFireball.setOwner(player);
                largeFireball.setPos(player.getX(), player.getY(), player.getZ());
                largeFireball.setDeltaMovement(vec31.x,vec31.y,vec31.z);
                level.addFreshEntity(largeFireball);

            }

            @Override
            public String name(@Nullable Player player, @Nullable Level level, @Nullable InteractionHand pUsedHand, @Nullable CollectionItem collectionItem) {
                return "ghast";
            }
        }, List.of(10, DEFALUTRES));
        initAll("iron_golem", new HashSet<>(List.of(SoundEvents.IRON_GOLEM_REPAIR.getLocation().toString())), compoundTag, new SoundEffect() {
            @Override
            public void effect(Player player, Level level, InteractionHand pUsedHand, ItemStack itemStack) {
                if(level.isClientSide)return;
                player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 400, 0));
                player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 400, 0));
            }

            @Override
            public String name(@Nullable Player player, @Nullable Level level, @Nullable InteractionHand pUsedHand, @Nullable CollectionItem collectionItem) {
                return "iron_golem";
            }
        }, List.of(10, DEFALUTRES));
        initAll("turtle", new HashSet<>(List.of(SoundEvents.TURTLE_AMBIENT_LAND.getLocation().toString())), compoundTag, new SoundEffect() {
            @Override
            public void effect(Player player, Level level, InteractionHand pUsedHand, ItemStack itemStack) {
                if(level.isClientSide)return;
                player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 200, 2));
            }

            @Override
            public String name(@Nullable Player player, @Nullable Level level, @Nullable InteractionHand pUsedHand, @Nullable CollectionItem collectionItem) {
                return "turtle";
            }
        }, List.of(10, DEFALUTRES));
        initAll("ender_pearl", new HashSet<>(List.of(SoundEvents.ENDER_PEARL_THROW.getLocation().toString())), compoundTag, new SoundEffect() {
            @Override
            public void effect(Player player, Level level, InteractionHand pUsedHand, ItemStack itemStack) {
                if(level.isClientSide)return;
                ThrownEnderpearl thrownenderpearl = new ThrownEnderpearl(level, player);
                thrownenderpearl.setItem(Items.ENDER_PEARL.getDefaultInstance());
                thrownenderpearl.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 1.5F, 1.0F);
                level.addFreshEntity(thrownenderpearl);
            }

            @Override
            public String name(@Nullable Player player, @Nullable Level level, @Nullable InteractionHand pUsedHand, @Nullable CollectionItem collectionItem) {
                return "ender_pearl";
            }
        }, List.of(20, DEFALUTRES));
        initAll("firework_rocket_launch", new HashSet<>(List.of(SoundEvents.FIREWORK_ROCKET_LAUNCH.getLocation().toString())), compoundTag, new SoundEffect() {
            @Override
            public void effect(Player player, Level level, InteractionHand pUsedHand, ItemStack itemStack) {
                if(level.isClientSide)return;

                if (player.isFallFlying()) {

                    FireworkRocketEntity fireworkrocketentity = new FireworkRocketEntity(level,  Items.FIREWORK_ROCKET.getDefaultInstance(), player);
                    level.addFreshEntity(fireworkrocketentity);

                }
            }

            @Override
            public String name(@Nullable Player player, @Nullable Level level, @Nullable InteractionHand pUsedHand, @Nullable CollectionItem collectionItem) {
                return "firework_rocket_launch";
            }
        }, List.of(20, DEFALUTRES));
        initAll("ender_dragon_death", new HashSet<>(List.of(SoundEvents.FIREWORK_ROCKET_LAUNCH.getLocation().toString())), compoundTag, new SoundEffect() {
            @Override
            public void effect(Player player, Level level, InteractionHand pUsedHand, ItemStack itemStack) {
                if(level.isClientSide)return;
                level.playSound(null,player.getOnPos(),SoundEvents.ENDER_DRAGON_DEATH,SoundSource.MASTER,5.0F,1);
            }
            @Override
            public String name(@Nullable Player player, @Nullable Level level, @Nullable InteractionHand pUsedHand, @Nullable CollectionItem collectionItem) {
                return "ender_dragon_death";
            }
        }, List.of(20, DEFALUTRES));
        initAll("wither", new HashSet<>(List.of(SoundEvents.WITHER_DEATH.getLocation().toString())), compoundTag, new SoundEffect() {
            @Override
            public void effect(Player player, Level level, InteractionHand pUsedHand, ItemStack itemStack) {
                if(level.isClientSide)return;
                HitResult hitResult = getHitResult(level, player, 1);
                Vec3 eye = player.getEyePosition();
                Vec3 vec31 = hitResult.getLocation().subtract(eye).normalize();
                WitherSkull witherSkull = EntityType.WITHER_SKULL.create(level);
                witherSkull.setOwner(player);
                witherSkull.setPos(player.getX(), player.getY(), player.getZ());
                witherSkull.setDeltaMovement(vec31.x,vec31.y,vec31.z);
                level.addFreshEntity(witherSkull);

            }
            @Override
            public String name(@Nullable Player player, @Nullable Level level, @Nullable InteractionHand pUsedHand, @Nullable CollectionItem collectionItem) {
                return "wither";
            }
        }, List.of(20, DEFALUTRES));

    }

    private void build(){

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
        SOUND_INF.put(SoundEvents.ENDER_PEARL_THROW.getLocation().toString(), 20);
        SOUND_INF.put(SoundEvents.CAT_PURR.getLocation().toString(), 1200);
        SOUND_INF.put(SoundEvents.TURTLE_AMBIENT_LAND.getLocation().toString(), 200);
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
