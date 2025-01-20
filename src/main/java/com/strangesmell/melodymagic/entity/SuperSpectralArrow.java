package com.strangesmell.melodymagic.entity;

import com.strangesmell.melodymagic.MelodyMagic;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.SpectralArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;

import javax.annotation.Nullable;
import java.util.List;

public class SuperSpectralArrow extends AbstractArrow {
    private int duration = 200;

    public SuperSpectralArrow(EntityType<? extends AbstractArrow> entityType, Level level) {
        super(entityType, level);
    }

    public SuperSpectralArrow(Level level, LivingEntity owner, ItemStack pickupItemStack, @Nullable ItemStack p_346408_) {
        super(MelodyMagic.SUPER_SPECTRAL_ARROW.get(), owner, level, pickupItemStack, p_346408_);
    }

    public SuperSpectralArrow(Level level, double x, double y, double z, ItemStack pickupItemStack, @Nullable ItemStack p_345907_) {
        super(MelodyMagic.SUPER_SPECTRAL_ARROW.get(), x, y, z, level, pickupItemStack, p_345907_);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide && !this.inGround) {
            this.level().addParticle(ParticleTypes.INSTANT_EFFECT, this.getX(), this.getY(), this.getZ(), 0.0, 0.0, 0.0);
        }
    }

    @Override
    protected void doPostHurtEffects(LivingEntity living) {
        super.doPostHurtEffects(living);
        MobEffectInstance mobeffectinstance = new MobEffectInstance(MobEffects.GLOWING, this.duration, 0);
        living.addEffect(mobeffectinstance, this.getEffectSource());
        if(!living.level().isClientSide){
            effect(living);
        }
    }
    @Override
    protected void onHitBlock(BlockHitResult result) {
        super.onHitBlock(result);
        if (this.level().isClientSide) return;
        TargetingConditions alertableTargeting = TargetingConditions.forNonCombat()
                .range(12.0)
                .ignoreLineOfSight()
                .selector(null);

        List<LivingEntity> list =this.level().getNearbyEntities(LivingEntity.class, alertableTargeting, null, this.getBoundingBox().inflate(8, 4, 8));
        for (LivingEntity livingEntity : list) {
            livingEntity.addEffect(new MobEffectInstance(MobEffects.GLOWING, 1000, 1));
        }
    }

    public void effect(LivingEntity living) {
        Level level = living.level();
        if (level.isClientSide) return;
        TargetingConditions alertableTargeting = TargetingConditions.forCombat()
                .range(12.0)
                .ignoreLineOfSight()
                .selector(null);

        List<LivingEntity> list = level.getNearbyEntities(LivingEntity.class, alertableTargeting, living, living.getBoundingBox().inflate(8, 4, 8));
        list.add(living);
        for (LivingEntity livingEntity : list) {
            livingEntity.addEffect(new MobEffectInstance(MobEffects.GLOWING, 1000, 1));
        }

    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("Duration")) {
            this.duration = compound.getInt("Duration");
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("Duration", this.duration);
    }

    @Override
    protected ItemStack getDefaultPickupItem() {
        return new ItemStack(MelodyMagic.RECON_BOLT_ITEM.get());
    }
}