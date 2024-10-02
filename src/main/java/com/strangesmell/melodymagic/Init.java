package com.strangesmell.melodymagic;

import com.strangesmell.melodymagic.api.EffectUtil;
import com.strangesmell.melodymagic.api.SoundEffect;
import com.strangesmell.melodymagic.block.FakeNetherPortal;
import com.strangesmell.melodymagic.block.FakeNetherPortalBlockEntity;
import com.strangesmell.melodymagic.container.ChestConatiner;
import com.strangesmell.melodymagic.entity.FriendlyVex;
import com.strangesmell.melodymagic.item.CollectionItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.npc.Npc;
import net.minecraft.world.entity.npc.WanderingTrader;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.*;
import net.minecraft.world.entity.projectile.windcharge.BreezeWindCharge;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.PlayerEnderChestContainer;
import net.minecraft.world.item.BoneMealItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.timers.TimerCallback;
import net.minecraft.world.level.timers.TimerQueue;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.common.EffectCures;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static com.strangesmell.melodymagic.MelodyMagic.*;
import static com.strangesmell.melodymagic.api.RecordUtil.getSoundEventSize;
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

        initAll("wither", new HashSet<>(List.of(SoundEvents.WITHER_DEATH.getLocation().toString())), compoundTag, new SoundEffect() {
            @Override
            public void effect(Player player, Level level, InteractionHand pUsedHand, ItemStack itemStack) {
                if (level.isClientSide) return;
                HitResult hitResult = getHitResult(level, player, 1);
                Vec3 eye = player.getEyePosition();
                Vec3 vec31 = hitResult.getLocation().subtract(eye).normalize();
                WitherSkull witherSkull = EntityType.WITHER_SKULL.create(level);
                witherSkull.setOwner(player);
                witherSkull.setPos(player.getX(), player.getY() + 1, player.getZ());
                witherSkull.setDeltaMovement(vec31.x, vec31.y, vec31.z);
                level.addFreshEntity(witherSkull);

            }

            @Override
            public String name(@Nullable Player player, @Nullable Level level, @Nullable InteractionHand pUsedHand, @Nullable CollectionItem collectionItem) {
                return "wither";
            }
        }, List.of(20, DEFALUTRES));
        initAll("breeze", new HashSet<>(List.of(SoundEvents.BREEZE_CHARGE.getLocation().toString())), compoundTag, new SoundEffect() {
            @Override
            public void effect(Player player, Level level, InteractionHand pUsedHand, ItemStack itemStack) {
                if (level.isClientSide) return;
                HitResult hitResult = getHitResult(level, player, 1);
                Vec3 eye = player.getEyePosition();
                Vec3 vec31 = hitResult.getLocation().subtract(eye).normalize();
                for (int i = 0; i < 3; i++) {
                    BreezeWindCharge breezeWindCharge = EntityType.BREEZE_WIND_CHARGE.create(level);
                    breezeWindCharge.setOwner(player);
                    breezeWindCharge.setPos(player.getX(), player.getEyeY() - 0.1, player.getZ());
                    breezeWindCharge.setDeltaMovement(vec31.x, vec31.y, vec31.z);
                    level.addFreshEntity(breezeWindCharge);
                }
            }

            @Override
            public String name(@Nullable Player player, @Nullable Level level, @Nullable InteractionHand pUsedHand, @Nullable CollectionItem collectionItem) {
                return "breeze";
            }
        }, List.of(20, DEFALUTRES));
        initAll("bow", new HashSet<>(List.of(SoundEvents.ARROW_SHOOT.getLocation().toString())), compoundTag, new SoundEffect() {
            @Override
            public void effect(Player player, Level level, InteractionHand pUsedHand, ItemStack itemStack) {
                if (level.isClientSide) return;
                List<ItemStack> draw = new ArrayList<>();
                draw.add(Items.APPLE.getDefaultInstance());
                EffectUtil.shoot((ServerLevel) level, player, pUsedHand, Items.BOW.getDefaultInstance(), draw, 3, 1, true, null);
            }

            @Override
            public String name(@Nullable Player player, @Nullable Level level, @Nullable InteractionHand pUsedHand, @Nullable CollectionItem collectionItem) {
                return "bow";
            }
        }, List.of(20, DEFALUTRES));
        initAll("dragon_breath", new HashSet<>(List.of(SoundEvents.ENDER_DRAGON_SHOOT.getLocation().toString())), compoundTag, new SoundEffect() {
            @Override
            public void effect(Player player, Level level, InteractionHand pUsedHand, ItemStack itemStack) {
                if (level.isClientSide) return;
                HitResult hitResult = getHitResult(level, player, 1);
                Vec3 eye = player.getEyePosition();
                Vec3 vec31 = hitResult.getLocation().subtract(eye).normalize();
                DragonFireball dragonFireball = EntityType.DRAGON_FIREBALL.create(level);
                dragonFireball.setOwner(player);
                dragonFireball.setPos(player.getX(), player.getY(), player.getZ());
                dragonFireball.setDeltaMovement(vec31.x, vec31.y, vec31.z);
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
        initAll("blaze", new HashSet<>(List.of(SoundEvents.BLAZE_SHOOT.getLocation().toString())), compoundTag, new SoundEffect() {
            @Override
            public void effect(Player player, Level level, InteractionHand pUsedHand, ItemStack itemStack) {
                if (level.isClientSide) return;
                HitResult hitResult = getHitResult(level, player, 1);
                Vec3 eye = player.getEyePosition();
                Vec3 vec31 = hitResult.getLocation().subtract(eye).normalize();
                for (int i = 0; i < 3; i++) {
                    SmallFireball smallFireball = EntityType.SMALL_FIREBALL.create(level);
                    smallFireball.setOwner(player);
                    smallFireball.setPos(player.getX(), player.getEyeY() - 0.1, player.getZ());
                    smallFireball.setDeltaMovement(vec31.x, vec31.y, vec31.z);
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
                if (level.isClientSide) return;
                HitResult hitResult = getHitResult(level, player, 1);
                Vec3 eye = player.getEyePosition();
                Vec3 vec31 = hitResult.getLocation().subtract(eye).normalize();
                LargeFireball largeFireball = EntityType.FIREBALL.create(level);
                largeFireball.setOwner(player);
                largeFireball.setPos(player.getX(), player.getEyeY() - 0.1, player.getZ());
                largeFireball.setDeltaMovement(vec31.x, vec31.y, vec31.z);
                level.addFreshEntity(largeFireball);

            }

            @Override
            public String name(@Nullable Player player, @Nullable Level level, @Nullable InteractionHand pUsedHand, @Nullable CollectionItem collectionItem) {
                return "ghast";
            }
        }, List.of(10, DEFALUTRES));
        initAll("ender_pearl", new HashSet<>(List.of(SoundEvents.ENDER_PEARL_THROW.getLocation().toString())), compoundTag, new SoundEffect() {
            @Override
            public void effect(Player player, Level level, InteractionHand pUsedHand, ItemStack itemStack) {
                if (level.isClientSide) return;
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
                if (level.isClientSide) return;
                if (player.isFallFlying()) {
                    FireworkRocketEntity fireworkrocketentity = new FireworkRocketEntity(level, Items.FIREWORK_ROCKET.getDefaultInstance(), player);
                    level.addFreshEntity(fireworkrocketentity);
                }
            }

            @Override
            public String name(@Nullable Player player, @Nullable Level level, @Nullable InteractionHand pUsedHand, @Nullable CollectionItem collectionItem) {
                return "firework_rocket_launch";
            }
        }, List.of(20, DEFALUTRES));
        initAll("snowball", new HashSet<>(List.of(SoundEvents.SNOWBALL_THROW.getLocation().toString())), compoundTag, new SoundEffect() {
            @Override
            public void effect(Player player, Level level, InteractionHand pUsedHand, ItemStack itemStack) {
                if (level.isClientSide) return;
                HitResult hitResult = getHitResult(level, player, 1);
                Vec3 eye = player.getEyePosition();
                Vec3 vec31 = hitResult.getLocation().subtract(eye).normalize();
                Snowball snowball = EntityType.SNOWBALL.create(level);
                snowball.setOwner(player);
                snowball.setPos(player.getX(), player.getEyeY() - 0.1, player.getZ());
                snowball.setDeltaMovement(vec31.x, vec31.y, vec31.z);
                level.addFreshEntity(snowball);
            }

            @Override
            public String name(@Nullable Player player, @Nullable Level level, @Nullable InteractionHand pUsedHand, @Nullable CollectionItem collectionItem) {
                return "snowball";
            }
        }, List.of(20, DEFALUTRES));
        initAll("chicken_egg", new HashSet<>(List.of(SoundEvents.CHICKEN_EGG.getLocation().toString())), compoundTag, new SoundEffect() {
            @Override
            public void effect(Player player, Level level, InteractionHand pUsedHand, ItemStack itemStack) {
                if (level.isClientSide) return;
                HitResult hitResult = getHitResult(level, player, 1);
                Vec3 eye = player.getEyePosition();
                Vec3 vec31 = hitResult.getLocation().subtract(eye).normalize();
                ThrownEgg thrownEgg = EntityType.EGG.create(level);
                thrownEgg.setOwner(player);
                thrownEgg.setPos(player.getX(), player.getEyeY() - 0.1, player.getZ());
                thrownEgg.setDeltaMovement(vec31.x, vec31.y, vec31.z);
                level.addFreshEntity(thrownEgg);

            }

            @Override
            public String name(@Nullable Player player, @Nullable Level level, @Nullable InteractionHand pUsedHand, @Nullable CollectionItem collectionItem) {
                return "chicken_egg";
            }
        }, List.of(20, DEFALUTRES));
        initAll("shulker", new HashSet<>(List.of(SoundEvents.SHULKER_SHOOT.getLocation().toString())), compoundTag, new SoundEffect() {
            @Override
            public void effect(Player player, Level level, InteractionHand pUsedHand, ItemStack itemStack) {
                if (level.isClientSide) return;
                HitResult hitResult = getHitResult(level, player, 20);
                ShulkerBullet shulkerBullet;
                if(hitResult instanceof EntityHitResult entityHitResult){
                    shulkerBullet = new ShulkerBullet(level, player, entityHitResult.getEntity(), Direction.Axis.Y);
                }else{
                    shulkerBullet = EntityType.SHULKER_BULLET.create(level);
                    Vec3 eye = player.getEyePosition();
                    Vec3 vec31 = hitResult.getLocation().subtract(eye).normalize();
                    shulkerBullet.setPos(player.getX(), player.getEyeY() - 0.1, player.getZ());
                    shulkerBullet.setDeltaMovement(vec31.x, vec31.y, vec31.z);
                }
                shulkerBullet.setOwner(player);
                level.addFreshEntity(shulkerBullet);

            }
            @Override
            public String name(@Nullable Player player, @Nullable Level level, @Nullable InteractionHand pUsedHand, @Nullable CollectionItem collectionItem) {
                return "shulker";
            }
        }, List.of(20, DEFALUTRES));

        initAll("evoker", new HashSet<>(List.of(SoundEvents.EVOKER_FANGS_ATTACK.getLocation().toString())), compoundTag, new SoundEffect() {
            @Override
            public void effect(Player player, Level level, InteractionHand pUsedHand, ItemStack itemStack) {
                if (level.isClientSide) return;
                HitResult hitResult = getHitResult(level, player, 20);

                if(hitResult instanceof EntityHitResult entityHitResult){
                    performSpellCasting(player, entityHitResult.getEntity().getOnPos());
                }else if(hitResult instanceof BlockHitResult entityHitResult){
                    performSpellCasting(player, entityHitResult.getBlockPos());
                }
            }
            private void performSpellCasting(LivingEntity player, BlockPos livingentity) {
                double d0 = Math.min(livingentity.getY(), player.getY());
                double d1 = Math.max(livingentity.getY(), player.getY()) + 1.0;
                float f = (float)Mth.atan2(livingentity.getZ() -player.getZ(), livingentity.getX() - player.getX());
                if (player.distanceToSqr(livingentity.getX(),livingentity.getY(),livingentity.getZ()) < 9.0) {
                    for (int i = 0; i < 5; i++) {
                        float f1 = f + (float)i * (float) Math.PI * 0.4F;
                        createSpellEntity(player.getX() + (double)Mth.cos(f1) * 1.5, player.getZ() + (double)Mth.sin(f1) * 1.5, d0, d1, f1, 0,player);
                    }

                    for (int k = 0; k < 8; k++) {
                        float f2 = f + (float)k * (float) Math.PI * 2.0F / 8.0F + (float) (Math.PI * 2.0 / 5.0);
                        createSpellEntity(player.getX() + (double)Mth.cos(f2) * 2.5, player.getZ() + (double)Mth.sin(f2) * 2.5, d0, d1, f2, 3,player);
                    }
                } else {
                    for (int l = 0; l < 16; l++) {
                        double d2 = 1.25 * (double)(l + 1);
                        int j = 1 * l;
                        this.createSpellEntity(player.getX() + (double)Mth.cos(f) * d2, player.getZ() + (double)Mth.sin(f) * d2, d0, d1, f, j,player);
                    }
                }
            }
            private void createSpellEntity(double pX, double pZ, double pMinY, double pMaxY, float pYRot, int pWarmupDelay,LivingEntity livingEntity) {
                BlockPos blockpos = BlockPos.containing(pX, pMaxY, pZ);
                boolean flag = false;
                double d0 = 0.0;

                do {
                    BlockPos blockpos1 = blockpos.below();
                    BlockState blockstate = livingEntity.level().getBlockState(blockpos1);
                    if (blockstate.isFaceSturdy(livingEntity.level(), blockpos1, Direction.UP)) {
                        if (!livingEntity.level().isEmptyBlock(blockpos)) {
                            BlockState blockstate1 =livingEntity.level().getBlockState(blockpos);
                            VoxelShape voxelshape = blockstate1.getCollisionShape(livingEntity.level(), blockpos);
                            if (!voxelshape.isEmpty()) {
                                d0 = voxelshape.max(Direction.Axis.Y);
                            }
                        }

                        flag = true;
                        break;
                    }

                    blockpos = blockpos.below();
                } while (blockpos.getY() >= Mth.floor(pMinY) - 1);

                if (flag) {
                    livingEntity.level()
                            .addFreshEntity(new EvokerFangs(livingEntity.level(), pX, (double)blockpos.getY() + d0, pZ, pYRot, pWarmupDelay, livingEntity));
                    livingEntity.level()
                            .gameEvent(GameEvent.ENTITY_PLACE, new Vec3(pX, (double)blockpos.getY() + d0, pZ), GameEvent.Context.of(livingEntity));
                }
            }
            @Override
            public String name(@Nullable Player player, @Nullable Level level, @Nullable InteractionHand pUsedHand, @Nullable CollectionItem collectionItem) {
                return "evoker";
            }
        }, List.of(20, DEFALUTRES));
        //11
        initAll("lightning_bolt_thunder", new HashSet<>(List.of(SoundEvents.LIGHTNING_BOLT_THUNDER.getLocation().toString())), compoundTag, new SoundEffect() {
            @Override
            public void effect(Player player, Level level, InteractionHand pUsedHand, ItemStack itemStack) {
                if (level.isClientSide) return;
                int size=getSoundEventSize(itemStack,SoundEvents.LIGHTNING_BOLT_THUNDER);
                HitResult hitResult = getHitResult(level, player, 20 + size);
                LightningBolt bolt = EntityType.LIGHTNING_BOLT.create(level);
                assert bolt != null;

                bolt.addTag(MODID);
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
                if (level.isClientSide) return;
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
        initAll("wolf", new HashSet<>(List.of(SoundEvents.WOLF_AMBIENT.getLocation().toString())), compoundTag, new SoundEffect() {
            @Override
            public void effect(Player player, Level level, InteractionHand pUsedHand, ItemStack itemStack) {
                if (level.isClientSide) return;
                int size=getSoundEventSize(itemStack,SoundEvents.WOLF_AMBIENT);
                for (int i = 0; i < size; i++) {
                    final int a = i;
                    Wolf wolf = EntityType.WOLF.create(level);
                    if (wolf != null) {
                        wolf.finalizeSpawn((ServerLevel) level, level.getCurrentDifficultyAt(player.getOnPos()), MobSpawnType.NATURAL, null);
                        wolf.tame(player);
                        wolf.setData(ENTITY_AGE, 200 + size * 10);
                        wolf.setPos(player.getX() + level.random.nextInt(-3, 4), player.getY(), player.getZ() + level.random.nextInt(-3, 4));
                        if (level.getRandom().nextInt(0, 11) < 3) {
                            wolf.setAge(-24000);
                        } else {
                            wolf.setAge(0);
                        }

                        TimerQueue<MinecraftServer> timerqueue = ((ServerLevel) level).getServer().getWorldData().overworldData().getScheduledEvents();
                        TimerCallback<MinecraftServer> myCallback = (obj, timerQueue, gameTime) -> {
                            level.addFreshEntity(wolf);
                            level.playSound(null, player.getOnPos(), SoundEvents.WOLF_HOWL, SoundSource.MASTER, (float) (level.random.nextFloat() * 0.5), (float) (level.random.nextFloat() * 0.5 + 0.5));
                        };
                        timerqueue.schedule("wolf" + a, level.getGameTime() + level.random.nextInt(0, 101), myCallback);


                    }
                }
            }

            @Override
            public String name(@Nullable Player player, @Nullable Level level, @Nullable InteractionHand pUsedHand, @Nullable CollectionItem collectionItem) {
                return "wolf";
            }
        }, List.of(20, DEFALUTRES));
        initAll("vex", new HashSet<>(List.of(SoundEvents.VEX_AMBIENT.getLocation().toString())), compoundTag, new SoundEffect() {
            @Override
            public void effect(Player player, Level level, InteractionHand pUsedHand, ItemStack itemStack) {
                if (level.isClientSide) return;
                int size=getSoundEventSize(itemStack,SoundEvents.VEX_AMBIENT);
                for (int i = 0; i < size; i++) {
                    final int a = i;
                    FriendlyVex vex = FRIENDLY_VEX.get().create(level);
                    if (vex != null) {
                        vex.finalizeSpawn((ServerLevel) level, level.getCurrentDifficultyAt(player.getOnPos()), MobSpawnType.NATURAL, null);
                        vex.setData(ENTITY_AGE, 200 + size * 10);
                        vex.setPos(player.getX() + level.random.nextInt(-3, 4), player.getY(), player.getZ() + level.random.nextInt(-3, 4));
                        vex.setOwner(player);
                        TimerQueue<MinecraftServer> timerqueue = ((ServerLevel) level).getServer().getWorldData().overworldData().getScheduledEvents();
                        TimerCallback<MinecraftServer> myCallback = (obj, timerQueue, gameTime) -> {
                            level.addFreshEntity(vex);
                        };
                        timerqueue.schedule("vex" + a, level.getGameTime() + level.random.nextInt(0, 101), myCallback);
                    }
                }
            }
            @Override
            public String name(@Nullable Player player, @Nullable Level level, @Nullable InteractionHand pUsedHand, @Nullable CollectionItem collectionItem) {
                return "vex";
            }
        }, List.of(20, DEFALUTRES));
        initAll("witch", new HashSet<>(List.of(SoundEvents.WITCH_DRINK.getLocation().toString(), SoundEvents.WITCH_THROW.getLocation().toString())), compoundTag, new SoundEffect() {
            @Override
            public void effect(Player player, Level level, InteractionHand pUsedHand, ItemStack itemStack) {
                HitResult hitResult = getHitResult(level, player, 20);
                if (hitResult instanceof EntityHitResult entityHitResult) {
                    if (entityHitResult.getEntity() instanceof LivingEntity pTarget) {
                        Vec3 vec3 = pTarget.getDeltaMovement();
                        double d0 = pTarget.getX() + vec3.x - player.getX();
                        double d1 = pTarget.getEyeY() - 1.1F - player.getY();
                        double d2 = pTarget.getZ() + vec3.z - player.getZ();
                        double d3 = Math.sqrt(d0 * d0 + d2 * d2);
                        Holder<Potion> holder = Potions.HARMING;
                        if (pTarget instanceof Npc) {
                            if (pTarget.getHealth() <= 4.0F) {
                                holder = Potions.HEALING;
                            } else {
                                holder = Potions.REGENERATION;
                            }
                        } else if (d3 >= 8.0 && !pTarget.hasEffect(MobEffects.MOVEMENT_SLOWDOWN)) {
                            holder = Potions.SLOWNESS;
                        } else if (pTarget.getHealth() >= 8.0F && !pTarget.hasEffect(MobEffects.POISON)) {
                            holder = Potions.POISON;
                        } else if (d3 <= 3.0 && !pTarget.hasEffect(MobEffects.WEAKNESS) && level.random.nextFloat() < 0.25F) {
                            holder = Potions.WEAKNESS;
                        }

                        ThrownPotion thrownpotion = new ThrownPotion(level, player);
                        thrownpotion.setItem(PotionContents.createItemStack(Items.SPLASH_POTION, holder));
                        thrownpotion.setXRot(thrownpotion.getXRot() - -20.0F);
                        thrownpotion.shoot(d0, d1 + d3 * 0.2, d2, 0.75F, 8.0F);
                        if (!player.isSilent()) {
                            level.playSound(
                                    null,
                                    player.getX(),
                                    player.getY(),
                                    player.getZ(),
                                    SoundEvents.WITCH_THROW,
                                    player.getSoundSource(),
                                    1.0F,
                                    0.8F + level.random.nextFloat() * 0.4F
                            );
                        }
                        level.addFreshEntity(thrownpotion);
                    }

                }else{
                    Holder<Potion> holder = null;
                    //level.random.nextFloat() < 0.15F &&
                    if (player.isEyeInFluid(FluidTags.WATER) && !player.hasEffect(MobEffects.WATER_BREATHING)) {
                        holder = Potions.WATER_BREATHING;
                    } else if ((player.isOnFire() || player.getLastDamageSource() != null && player.getLastDamageSource().is(DamageTypeTags.IS_FIRE))
                            && !player.hasEffect(MobEffects.FIRE_RESISTANCE)) {
                        holder = Potions.FIRE_RESISTANCE;
                    } else if (player.getHealth() < player.getMaxHealth()) {
                        holder = Potions.HEALING;
                    } else if (!player.hasEffect(MobEffects.MOVEMENT_SPEED)) {
                        holder = Potions.SWIFTNESS;
                    }
                    if (holder != null) {
                        ItemStack potion = PotionContents.createItemStack(Items.POTION, holder);
                        potion.use(level, player, player.getUsedItemHand());

                        PotionContents potioncontents = potion.get(DataComponents.POTION_CONTENTS);
                        if (potion.is(Items.POTION) && potioncontents != null) {
                            potioncontents.forEachEffect(player::addEffect);
                        }
                        player.gameEvent(GameEvent.DRINK);
                    }
                }


            }

            @Override
            public String name(@Nullable Player player, @Nullable Level level, @Nullable InteractionHand pUsedHand, @Nullable CollectionItem collectionItem) {
                return "witch";
            }
        }, List.of(20, DEFALUTRES));

        initAll("eat", new HashSet<>(List.of(SoundEvents.PLAYER_BURP.getLocation().toString())), compoundTag, new SoundEffect() {
            @Override
            public void effect(Player player, Level level, InteractionHand pUsedHand, ItemStack itemStack) {
                if (level.isClientSide) return;
                int size=getSoundEventSize(itemStack,".eat");
                for (int i = 0; i < size; i++) {
                    player.eat(level, Items.POTATO.getDefaultInstance(), Objects.requireNonNull(Items.POTATO.getDefaultInstance().getFoodProperties(player)));
                }
            }

            @Override
            public String name(@Nullable Player player, @Nullable Level level, @Nullable InteractionHand pUsedHand, @Nullable CollectionItem collectionItem) {
                return "eat";
            }
        }, List.of(10, DEFALUTRES));
        initAll("bone_meal", new HashSet<>(List.of(SoundEvents.BONE_MEAL_USE.getLocation().toString())), compoundTag, new SoundEffect() {
            @Override
            public void effect(Player player, Level level, InteractionHand pUsedHand, ItemStack itemStack) {
                if (level.isClientSide) return;
                HitResult hitResult = getHitResult(level, player, player.entityInteractionRange());
                if (hitResult instanceof BlockHitResult blockHitResult) {
                    if (!level.getBlockState(blockHitResult.getBlockPos()).getTags().anyMatch(tag -> tag.equals(BlockTags.FLOWERS))) {
                        UseOnContext useOnContext = new UseOnContext(player, pUsedHand, blockHitResult);
                        BoneMealItem.applyBonemeal(Items.BONE_MEAL.getDefaultInstance(), level, blockHitResult.getBlockPos(), player);
                        level.playSound(null, blockHitResult.getBlockPos(), SoundEvents.BONE_MEAL_USE, SoundSource.MASTER);
                        return;
                    } else {
                        Block flower = level.getBlockState(blockHitResult.getBlockPos()).getBlock();
                        BlockPos blockPos = blockHitResult.getBlockPos();


                        for (int i = 0; i < 200; i++) {
                            int dx = level.random.nextInt(-12, 13);
                            int dy = level.random.nextInt(-3, 4);
                            int dz = level.random.nextInt(-12, 13);
                            if (level.getBlockState(blockPos.offset(dx, dy, dz)).getTags().anyMatch(tag -> tag.equals(BlockTags.DIRT)) && level.getBlockState(blockPos.offset(dx, dy + 1, dz)).getBlock() instanceof AirBlock) {
                                if (level.random.nextInt(0, 100) < 90) {
                                    level.setBlock(blockPos.offset(dx, dy + 1, dz), flower.defaultBlockState(), 2);
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
                if (level.isClientSide) return;
                Cat cat = EntityType.CAT.create(level);
                if (cat != null) {
                    cat.finalizeSpawn((ServerLevel) level, level.getCurrentDifficultyAt(player.getOnPos()), MobSpawnType.NATURAL, null);
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
        initAll("nether_portal", new HashSet<>(List.of(SoundEvents.PORTAL_AMBIENT.getLocation().toString())), compoundTag, new SoundEffect() {
            @Override
            public void effect(Player player, Level level, InteractionHand pUsedHand, ItemStack itemStack) {
                int size=getSoundEventSize(itemStack,SoundEvents.PORTAL_AMBIENT);
                HitResult hitResult = getHitResult(level, player, 10);
                Vec3 eye = player.getEyePosition();
                Vec3 vec31 = hitResult.getLocation().subtract(eye).normalize();
                BlockState blockState = FAKE_NETHER_PORTAL.get().defaultBlockState();
                BlockState blockState2 = Blocks.OBSIDIAN.defaultBlockState();
                boolean x = true;
                if (Math.abs(vec31.x) > Math.abs(vec31.z)) {
                    blockState = blockState.setValue(FakeNetherPortal.AXIS, Direction.Axis.Z);
                    x = false;
                }
                if (hitResult instanceof BlockHitResult blockHitResult) {
                    BlockPos pos = blockHitResult.getBlockPos();

                    setBlock(level, pos.offset(0, 1, 0), blockState2);
                    setBlock(level, pos.offset(0, 4, 0), blockState2);
                    if (x) {
                        for (int i = 1; i < 5; i++) {
                            setBlock(level, pos.offset(1, i, 0), blockState2);
                            setBlock(level, pos.offset(-1, i, 0), blockState2);
                        }

                    } else {
                        for (int i = 1; i < 5; i++) {
                            setBlock(level, pos.offset(0, i, 1), blockState2);
                            setBlock(level, pos.offset(0, i, -1), blockState2);
                        }
                    }

                    setBlock(level, pos.offset(0, 2, 0), blockState);
                    setBlock(level, pos.offset(0, 3, 0), blockState);
                    if (level.getBlockEntity(pos.offset(0, 2, 0)) instanceof FakeNetherPortalBlockEntity fakeNetherPortalBlockEntity) {
                        fakeNetherPortalBlockEntity.num = 100 * size;
                    }
                    if (level.getBlockEntity(pos.offset(0, 3, 0)) instanceof FakeNetherPortalBlockEntity fakeNetherPortalBlockEntity) {
                        fakeNetherPortalBlockEntity.num = 100 * size;
                    }

                }

            }

            @Override
            public String name(@Nullable Player player, @Nullable Level level, @Nullable InteractionHand pUsedHand, @Nullable CollectionItem collectionItem) {
                return "nether_portal";
            }
        }, List.of(10, DEFALUTRES));

        initAll("ender_dragon_death", new HashSet<>(List.of(SoundEvents.FIREWORK_ROCKET_LAUNCH.getLocation().toString())), compoundTag, new SoundEffect() {
            @Override
            public void effect(Player player, Level level, InteractionHand pUsedHand, ItemStack itemStack) {
                if (level.isClientSide) return;
                level.playSound(null, player.getOnPos(), SoundEvents.ENDER_DRAGON_DEATH, SoundSource.MASTER, 5.0F, 1);
            }

            @Override
            public String name(@Nullable Player player, @Nullable Level level, @Nullable InteractionHand pUsedHand, @Nullable CollectionItem collectionItem) {
                return "ender_dragon_death";
            }
        }, List.of(20, DEFALUTRES));
        initAll("anvil", new HashSet<>(List.of(SoundEvents.ANVIL_USE.getLocation().toString())), compoundTag, new SoundEffect() {
            @Override
            public void effect(Player player, Level level, InteractionHand pUsedHand, ItemStack itemStack) {
                if (level.isClientSide) return;
                int size=getSoundEventSize(itemStack,SoundEvents.ANVIL_USE);
                ItemStack tool = player.getItemInHand(InteractionHand.OFF_HAND);
                if (tool.getDamageValue() - size * 10 >= 0) {
                    tool.setDamageValue(tool.getDamageValue() - size * 10);
                } else {
                    tool.setDamageValue(0);
                }
            }

            @Override
            public String name(@Nullable Player player, @Nullable Level level, @Nullable InteractionHand pUsedHand, @Nullable CollectionItem collectionItem) {
                return "anvil";
            }
        }, List.of(20, DEFALUTRES));
        //10
        initAll("spider", new HashSet<>(List.of(SoundEvents.SPIDER_AMBIENT.getLocation().toString())), compoundTag, new SoundEffect() {
            @Override
            public void effect(Player player, Level level, InteractionHand pUsedHand, ItemStack itemStack) {
                int size=getSoundEventSize(itemStack,SoundEvents.SPIDER_AMBIENT);
                player.setData(ENTITY_AGE, 200 + size * 10);
            }

            @Override
            public String name(@Nullable Player player, @Nullable Level level, @Nullable InteractionHand pUsedHand, @Nullable CollectionItem collectionItem) {
                return "spider";
            }
        }, List.of(20, DEFALUTRES));
        initAll("glow_squid", new HashSet<>(List.of(SoundEvents.GLOW_SQUID_AMBIENT.getLocation().toString())), compoundTag, new SoundEffect() {
            @Override
            public void effect(Player player, Level level, InteractionHand pUsedHand, ItemStack itemStack) {
                HitResult hitResult = getHitResult(level, player, 20);
                if(hitResult instanceof EntityHitResult entityHitResult) {
                    for(int i=20;i>0;i--){
                        level.addParticle(ParticleTypes.GLOW, entityHitResult.getEntity().getRandomX(0.6), entityHitResult.getEntity().getRandomY(), entityHitResult.getEntity().getRandomZ(0.6), 0.0, 0.0, 0.0);
                    }
                }else{
                    for(int i=20;i>0;i--){
                        level.addParticle(ParticleTypes.GLOW, hitResult.getLocation().x + level.random.nextFloat()-0.5, hitResult.getLocation().y+ level.random.nextFloat()-0.5, hitResult.getLocation().z+ level.random.nextFloat()-0.5, 0.0, 0.0, 0.0);
                    }
                }

            }
            @Override
            public String name(@Nullable Player player, @Nullable Level level, @Nullable InteractionHand pUsedHand, @Nullable CollectionItem collectionItem) {
                return "glow_squid";
            }
        }, List.of(20, DEFALUTRES));
        initAll("wandering_trader", new HashSet<>(List.of(SoundEvents.WANDERING_TRADER_YES.getLocation().toString())), compoundTag, new SoundEffect() {
            @Override
            public void effect(Player player, Level level, InteractionHand pUsedHand, ItemStack itemStack) {
                if (level.isClientSide) return;
                int size=getSoundEventSize(itemStack,SoundEvents.WANDERING_TRADER_YES);
                WanderingTrader wanderingTrader = EntityType.WANDERING_TRADER.create(level);
                if (wanderingTrader != null) {
                    wanderingTrader.finalizeSpawn((ServerLevel) level, level.getCurrentDifficultyAt(player.getOnPos()), MobSpawnType.NATURAL, null);
                    wanderingTrader.setData(ENTITY_AGE, 1200 + size * 10);
                    wanderingTrader.setPos(player.getX() + level.random.nextInt(-3, 4), player.getY(), player.getZ() + level.random.nextInt(-3, 4));
                }
            }

            @Override
            public String name(@Nullable Player player, @Nullable Level level, @Nullable InteractionHand pUsedHand, @Nullable CollectionItem collectionItem) {
                return "wandering_trader";
            }
        }, List.of(20, DEFALUTRES));
        initAll("ender_chest", new HashSet<>(List.of(SoundEvents.ENDER_CHEST_OPEN.getLocation().toString())), compoundTag, new SoundEffect() {
            @Override
            public void effect(Player player, Level level, InteractionHand pUsedHand, ItemStack itemStack) {
                if (level.isClientSide) return;
                PlayerEnderChestContainer playerenderchestcontainer = player.getEnderChestInventory();
                if(playerenderchestcontainer != null){
                    player.openMenu(
                            new SimpleMenuProvider(
                                    (p_53124_, p_53125_, p_53126_) -> ChestMenu.threeRows(p_53124_, p_53125_, playerenderchestcontainer), Component.translatable("container.enderchest"))
                    );
                }
            }

            @Override
            public String name(@Nullable Player player, @Nullable Level level, @Nullable InteractionHand pUsedHand, @Nullable CollectionItem collectionItem) {
                return "ender_chest";
            }
        }, List.of(20, DEFALUTRES));
        initAll("chest", new HashSet<>(List.of(SoundEvents.CHEST_OPEN.getLocation().toString())), compoundTag, new SoundEffect() {
            @Override
            public void effect(Player player, Level level, InteractionHand pUsedHand, ItemStack itemStack) {

                if (level.isClientSide) {
                    return;
                }
                int size=getSoundEventSize(itemStack,SoundEvents.CHEST_OPEN,6);

                SimpleMenuProvider simpleMenuProvider = null;
                switch (size) {
                    case 1 : simpleMenuProvider = new SimpleMenuProvider(
                            (containerId, playerInventory,player1) ->  ChestConatiner.oneRow(containerId, playerInventory),Component.translatable("chest_menu")
                    );break;
                    case 2 :simpleMenuProvider = new SimpleMenuProvider(
                            (containerId, playerInventory,player1) ->  ChestConatiner.twoRows(containerId, playerInventory),Component.translatable("chest_menu")
                    );break;
                    case 3 :simpleMenuProvider = new SimpleMenuProvider(
                            (containerId, playerInventory,player1) ->  ChestConatiner.threeRows(containerId, playerInventory),Component.translatable("chest_menu")
                    );break;
                    case 4 :simpleMenuProvider = new SimpleMenuProvider(
                            (containerId, playerInventory,player1) ->  ChestConatiner.fourRows(containerId, playerInventory),Component.translatable("chest_menu")
                    );break;
                    case 5 :simpleMenuProvider = new SimpleMenuProvider(
                            (containerId, playerInventory,player1) ->  ChestConatiner.fiveRows(containerId, playerInventory),Component.translatable("chest_menu")
                    );break;
                    case 6 :simpleMenuProvider = new SimpleMenuProvider(
                            (containerId, playerInventory,player1) ->  ChestConatiner.sixRows(containerId, playerInventory),Component.translatable("chest_menu")
                    );break;
                }
                if(simpleMenuProvider!=null){
                    player.openMenu(simpleMenuProvider);
                }

            }
            @Override
            public String name(@Nullable Player player, @Nullable Level level, @Nullable InteractionHand pUsedHand, @Nullable CollectionItem collectionItem) {
                return "chest";
            }
        }, List.of(20, DEFALUTRES));


        compoundTag.putInt(SoundEvents.COW_AMBIENT.getLocation() + "num", 9);
        initAll("nine_cow", new HashSet<>(List.of(SoundEvents.COW_AMBIENT.getLocation().toString())), compoundTag, new SoundEffect() {
            @Override
            public void effect(Player player, Level level, InteractionHand pUsedHand, ItemStack itemStack) {
                if (level.isClientSide) return;
                player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 200, 1));
            }

            @Override
            public String name(@Nullable Player player, @Nullable Level level, @Nullable InteractionHand pUsedHand, @Nullable CollectionItem collectionItem) {
                return "nine_cow";
            }
        }, List.of(20, DEFALUTRES));
        compoundTag.putInt(SoundEvents.PIG_AMBIENT.getLocation() + "num", 9);
        initAll("nine_pig", new HashSet<>(List.of(SoundEvents.PIG_AMBIENT.getLocation().toString())), compoundTag, new SoundEffect() {
            @Override
            public void effect(Player player, Level level, InteractionHand pUsedHand, ItemStack itemStack) {
                if (level.isClientSide) return;
                player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 200, 1));
                player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 200, 1));
            }

            @Override
            public String name(@Nullable Player player, @Nullable Level level, @Nullable InteractionHand pUsedHand, @Nullable CollectionItem collectionItem) {
                return "nine_pig";
            }
        }, List.of(20, DEFALUTRES));
        initAll("water_breath", new HashSet<>(List.of(SoundEvents.WATER_AMBIENT.getLocation().toString())), compoundTag, new SoundEffect() {
            @Override
            public void effect(Player player, Level level, InteractionHand pUsedHand, ItemStack itemStack) {
                if (level.isClientSide) return;
                player.addEffect(new MobEffectInstance(MobEffects.WATER_BREATHING, 200, 1));
            }

            @Override
            public String name(@Nullable Player player, @Nullable Level level, @Nullable InteractionHand pUsedHand, @Nullable CollectionItem collectionItem) {
                return "water_breath";
            }
        }, List.of(5, DEFALUTRES));

        initAll("village_reputation", new HashSet<>(List.of(SoundEvents.VILLAGER_YES.getLocation().toString())), compoundTag, new SoundEffect() {
            @Override
            public void effect(Player player, Level level, InteractionHand pUsedHand, ItemStack itemStack) {
                if (level.isClientSide) return;
                int size=getSoundEventSize(itemStack,SoundEvents.VILLAGER_YES);
                player.addEffect(new MobEffectInstance(MobEffects.HERO_OF_THE_VILLAGE, 200, size));
            }

            @Override
            public String name(@Nullable Player player, @Nullable Level level, @Nullable InteractionHand pUsedHand, @Nullable CollectionItem collectionItem) {
                return "village_reputation";
            }
        }, List.of(10, DEFALUTRES));
        initAll("bat", new HashSet<>(List.of(SoundEvents.BAT_AMBIENT.getLocation().toString())), compoundTag, new SoundEffect() {
            @Override
            public void effect(Player player, Level level, InteractionHand pUsedHand, ItemStack itemStack) {
                if (level.isClientSide) return;
                TargetingConditions alertableTargeting = TargetingConditions.forCombat()
                        .range(12.0)
                        .ignoreLineOfSight()
                        .selector(null);

                List<LivingEntity> list = level.getNearbyEntities(LivingEntity.class, alertableTargeting, player, player.getBoundingBox().inflate(4, 3, 4));
                for (LivingEntity livingEntity : list) {
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
                if (level.isClientSide) return;
                player.addEffect(new MobEffectInstance(MobEffects.DOLPHINS_GRACE, 1000, 1));
            }

            @Override
            public String name(@Nullable Player player, @Nullable Level level, @Nullable InteractionHand pUsedHand, @Nullable CollectionItem collectionItem) {
                return "dolphin";
            }
        }, List.of(10, DEFALUTRES));
        initAll("iron_golem", new HashSet<>(List.of(SoundEvents.IRON_GOLEM_REPAIR.getLocation().toString())), compoundTag, new SoundEffect() {
            @Override
            public void effect(Player player, Level level, InteractionHand pUsedHand, ItemStack itemStack) {
                if (level.isClientSide) return;
                player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 400, 0));
                player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 400, 0));
            }

            @Override
            public String name(@Nullable Player player, @Nullable Level level, @Nullable InteractionHand pUsedHand, @Nullable CollectionItem collectionItem) {
                return "iron_golem";
            }
        }, List.of(10, DEFALUTRES));
        initAll("iron_golem_summon", new HashSet<>(List.of(SoundEvents.IRON_GOLEM_DEATH.getLocation().toString())), compoundTag, new SoundEffect() {
            @Override
            public void effect(Player player, Level level, InteractionHand pUsedHand, ItemStack itemStack) {
                if (level.isClientSide) return;
                int size=getSoundEventSize(itemStack,SoundEvents.IRON_GOLEM_DEATH);
                IronGolem ironGolem = EntityType.IRON_GOLEM.create(level);
                if (ironGolem != null) {
                    ironGolem.finalizeSpawn((ServerLevel) level, level.getCurrentDifficultyAt(player.getOnPos()), MobSpawnType.NATURAL, null);
                    ironGolem.setData(ENTITY_AGE, 200 + size * 10);
                    ironGolem.setPos(player.getX() + level.random.nextInt(-3, 4), player.getY(), player.getZ() + level.random.nextInt(-3, 4));
                    TimerQueue<MinecraftServer> timerqueue = ((ServerLevel) level).getServer().getWorldData().overworldData().getScheduledEvents();
                    TimerCallback<MinecraftServer> myCallback = (obj, timerQueue, gameTime) -> {
                        level.addFreshEntity(ironGolem);
                    };
                    timerqueue.schedule("iron_golem_summon", level.getGameTime() + level.random.nextInt(0, 50), myCallback);


                }
            }

            @Override
            public String name(@Nullable Player player, @Nullable Level level, @Nullable InteractionHand pUsedHand, @Nullable CollectionItem collectionItem) {
                return "iron_golem_summon";
            }
        }, List.of(10, DEFALUTRES));
        initAll("turtle", new HashSet<>(List.of(SoundEvents.TURTLE_AMBIENT_LAND.getLocation().toString())), compoundTag, new SoundEffect() {
            @Override
            public void effect(Player player, Level level, InteractionHand pUsedHand, ItemStack itemStack) {
                if (level.isClientSide) return;
                player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 200, 2));
            }

            @Override
            public String name(@Nullable Player player, @Nullable Level level, @Nullable InteractionHand pUsedHand, @Nullable CollectionItem collectionItem) {
                return "turtle";
            }
        }, List.of(10, DEFALUTRES));
        initAll("rabbit", new HashSet<>(List.of(SoundEvents.RABBIT_JUMP.getLocation().toString())), compoundTag, new SoundEffect() {
            @Override
            public void effect(Player player, Level level, InteractionHand pUsedHand, ItemStack itemStack) {
                if (level.isClientSide) return;
                int size=getSoundEventSize(itemStack,SoundEvents.RABBIT_JUMP);
                player.addEffect(new MobEffectInstance(MobEffects.JUMP, 200 + size * 5, size - 1));
            }

            @Override
            public String name(@Nullable Player player, @Nullable Level level, @Nullable InteractionHand pUsedHand, @Nullable CollectionItem collectionItem) {
                return "rabbit";
            }
        }, List.of(20, DEFALUTRES));
        initAll("totem", new HashSet<>(List.of(SoundEvents.TOTEM_USE.getLocation().toString())), compoundTag, new SoundEffect() {
            @Override
            public void effect(Player player, Level level, InteractionHand pUsedHand, ItemStack itemStack) {
                if (level.isClientSide) return;
                level.playSound(null,player.getOnPos(),SoundEvents.TOTEM_USE,SoundSource.MASTER,1,1);
                player.removeEffectsCuredBy(EffectCures.PROTECTED_BY_TOTEM);
                player.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 100, 1));
                player.addEffect(new MobEffectInstance(MobEffects.HEAL, 100, 1));
            }

            @Override
            public String name(@Nullable Player player, @Nullable Level level, @Nullable InteractionHand pUsedHand, @Nullable CollectionItem collectionItem) {
                return "totem";
            }
        }, List.of(20, DEFALUTRES));
        //12

    }

    private void build() {

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
        SOUND_INF.put(SoundEvents.CHICKEN_EGG.getLocation().toString(), 10);
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
