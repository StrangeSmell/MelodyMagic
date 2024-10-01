package com.strangesmell.melodymagic.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Vex;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.EnumSet;

public class FriendlyVex extends Monster implements TraceableEntity {
    public static final float FLAP_DEGREES_PER_TICK = 45.836624F;
    public static final int TICKS_PER_FLAP = Mth.ceil((float) (Math.PI * 5.0 / 4.0));
    protected static final EntityDataAccessor<Byte> DATA_FLAGS_ID = SynchedEntityData.defineId(FriendlyVex.class, EntityDataSerializers.BYTE);
    private static final int FLAG_IS_CHARGING = 1;
    @Nullable
    Player owner;
    @Nullable
    private BlockPos boundOrigin;
    private boolean hasLimitedLife;
    private int limitedLifeTicks;

    public FriendlyVex(EntityType<? extends FriendlyVex> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.moveControl = new FriendlyVex.VexMoveControl(this);
        this.xpReward = 3;
    }

    @Override
    public boolean isFlapping() {
        return this.tickCount % TICKS_PER_FLAP == 0;
    }

    @Override
    public void move(MoverType pType, Vec3 pPos) {
        super.move(pType, pPos);
        this.checkInsideBlocks();
    }

    @Override
    public void tick() {
        this.noPhysics = true;
        super.tick();
        this.noPhysics = false;
        this.setNoGravity(true);
        if (this.hasLimitedLife && --this.limitedLifeTicks <= 0) {
            this.limitedLifeTicks = 20;
            this.hurt(this.damageSources().starve(), 1.0F);
        }
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(4, new FriendlyVex.VexChargeAttackGoal());
        this.goalSelector.addGoal(8, new FriendlyVex.VexRandomMoveGoal());
        this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 3.0F, 1.0F));
        this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Mob.class, 8.0F));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this, Raider.class).setAlertOthers());
        this.targetSelector.addGoal(2, new FriendlyVex.VexCopyOwnerTargetGoal(this));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 14.0).add(Attributes.ATTACK_DAMAGE, 4.0);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder pBuilder) {
        super.defineSynchedData(pBuilder);
        pBuilder.define(DATA_FLAGS_ID, (byte)0);
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    @Override
    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        if (pCompound.contains("BoundX")) {
            this.boundOrigin = new BlockPos(pCompound.getInt("BoundX"), pCompound.getInt("BoundY"), pCompound.getInt("BoundZ"));
        }

        if (pCompound.contains("LifeTicks")) {
            this.setLimitedLife(pCompound.getInt("LifeTicks"));
        }
    }

    /**
     * Prepares this entity in new dimension by copying NBT data from entity in old dimension
     */
    @Override
    public void restoreFrom(Entity pEntity) {
        super.restoreFrom(pEntity);
        if (pEntity instanceof FriendlyVex vex) {
            this.owner = vex.getOwner();
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        if (this.boundOrigin != null) {
            pCompound.putInt("BoundX", this.boundOrigin.getX());
            pCompound.putInt("BoundY", this.boundOrigin.getY());
            pCompound.putInt("BoundZ", this.boundOrigin.getZ());
        }

        if (this.hasLimitedLife) {
            pCompound.putInt("LifeTicks", this.limitedLifeTicks);
        }
    }

    @Nullable
    public Player getOwner() {
        return this.owner;
    }

    @Nullable
    public BlockPos getBoundOrigin() {
        return this.boundOrigin;
    }

    public void setBoundOrigin(@Nullable BlockPos pBoundOrigin) {
        this.boundOrigin = pBoundOrigin;
    }

    private boolean getVexFlag(int pMask) {
        int i = this.entityData.get(DATA_FLAGS_ID);
        return (i & pMask) != 0;
    }

    private void setVexFlag(int pMask, boolean pValue) {
        int i = this.entityData.get(DATA_FLAGS_ID);
        if (pValue) {
            i |= pMask;
        } else {
            i &= ~pMask;
        }

        this.entityData.set(DATA_FLAGS_ID, (byte)(i & 0xFF));
    }

    public boolean isCharging() {
        return this.getVexFlag(1);
    }

    public void setIsCharging(boolean pCharging) {
        this.setVexFlag(1, pCharging);
    }

    public void setOwner(Player pOwner) {
        this.owner = pOwner;
    }

    public void setLimitedLife(int pLimitedLifeTicks) {
        this.hasLimitedLife = true;
        this.limitedLifeTicks = pLimitedLifeTicks;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.VEX_AMBIENT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.VEX_DEATH;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource pDamageSource) {
        return SoundEvents.VEX_HURT;
    }

    @Override
    public float getLightLevelDependentMagicValue() {
        return 1.0F;
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty, MobSpawnType pSpawnType, @Nullable SpawnGroupData pSpawnGroupData) {
        RandomSource randomsource = pLevel.getRandom();
        this.populateDefaultEquipmentSlots(randomsource, pDifficulty);
        this.populateDefaultEquipmentEnchantments(pLevel, randomsource, pDifficulty);
        return super.finalizeSpawn(pLevel, pDifficulty, pSpawnType, pSpawnGroupData);
    }

    @Override
    protected void populateDefaultEquipmentSlots(RandomSource pRandom, DifficultyInstance pDifficulty) {
        this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.IRON_SWORD));
        this.setDropChance(EquipmentSlot.MAINHAND, 0.0F);
    }

    class VexChargeAttackGoal extends Goal {
        public VexChargeAttackGoal() {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            LivingEntity livingentity = FriendlyVex.this.getTarget();
            return livingentity != null
                    && livingentity.isAlive()
                    && !FriendlyVex.this.getMoveControl().hasWanted()
                    && FriendlyVex.this.random.nextInt(reducedTickDelay(7)) == 0 && FriendlyVex.this.distanceToSqr(livingentity) > 4.0;
        }

        @Override
        public boolean canContinueToUse() {
            return FriendlyVex.this.getMoveControl().hasWanted() && FriendlyVex.this.isCharging() && FriendlyVex.this.getTarget() != null && FriendlyVex.this.getTarget().isAlive();
        }

        @Override
        public void start() {
            LivingEntity livingentity = FriendlyVex.this.getTarget();
            if (livingentity != null) {
                Vec3 vec3 = livingentity.getEyePosition();
                FriendlyVex.this.moveControl.setWantedPosition(vec3.x, vec3.y, vec3.z, 1.0);
            }

            FriendlyVex.this.setIsCharging(true);
            FriendlyVex.this.playSound(SoundEvents.VEX_CHARGE, 1.0F, 1.0F);
        }

        @Override
        public void stop() {
            FriendlyVex.this.setIsCharging(false);
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }

        @Override
        public void tick() {
            LivingEntity livingentity = FriendlyVex.this.getTarget();
            if (livingentity != null) {
                if (FriendlyVex.this.getBoundingBox().intersects(livingentity.getBoundingBox())) {
                    FriendlyVex.this.doHurtTarget(livingentity);
                    FriendlyVex.this.setIsCharging(false);
                } else {
                    double d0 = FriendlyVex.this.distanceToSqr(livingentity);
                    if (d0 < 9.0) {
                        Vec3 vec3 = livingentity.getEyePosition();
                        FriendlyVex.this.moveControl.setWantedPosition(vec3.x, vec3.y, vec3.z, 1.0);
                    }
                }
            }
        }
    }

    class VexCopyOwnerTargetGoal extends TargetGoal {
        private final TargetingConditions copyOwnerTargeting = TargetingConditions.forNonCombat().ignoreLineOfSight().ignoreInvisibilityTesting();

        public VexCopyOwnerTargetGoal(PathfinderMob pMob) {
            super(pMob, false);
        }

        @Override
        public boolean canUse() {
            return FriendlyVex.this.owner != null && FriendlyVex.this.owner.getLastAttacker() != null && this.canAttack(FriendlyVex.this.owner.getLastAttacker(), this.copyOwnerTargeting);
        }

        @Override
        public void start() {
            FriendlyVex.this.setTarget(FriendlyVex.this.owner.getLastAttacker());
            super.start();
        }
    }

    class VexMoveControl extends MoveControl {
        public VexMoveControl(FriendlyVex pVex) {
            super(pVex);
        }

        @Override
        public void tick() {
            if (this.operation == MoveControl.Operation.MOVE_TO) {
                Vec3 vec3 = new Vec3(this.wantedX - FriendlyVex.this.getX(), this.wantedY - FriendlyVex.this.getY(), this.wantedZ - FriendlyVex.this.getZ());
                double d0 = vec3.length();
                if (d0 < FriendlyVex.this.getBoundingBox().getSize()) {
                    this.operation = MoveControl.Operation.WAIT;
                    FriendlyVex.this.setDeltaMovement(FriendlyVex.this.getDeltaMovement().scale(0.5));
                } else {
                    FriendlyVex.this.setDeltaMovement(FriendlyVex.this.getDeltaMovement().add(vec3.scale(this.speedModifier * 0.05 / d0)));
                    if (FriendlyVex.this.getTarget() == null) {
                        Vec3 vec31 = FriendlyVex.this.getDeltaMovement();
                        FriendlyVex.this.setYRot(-((float)Mth.atan2(vec31.x, vec31.z)) * (180.0F / (float)Math.PI));
                        FriendlyVex.this.yBodyRot = FriendlyVex.this.getYRot();
                    } else {
                        double d2 = FriendlyVex.this.getTarget().getX() - FriendlyVex.this.getX();
                        double d1 = FriendlyVex.this.getTarget().getZ() - FriendlyVex.this.getZ();
                        FriendlyVex.this.setYRot(-((float)Mth.atan2(d2, d1)) * (180.0F / (float)Math.PI));
                        FriendlyVex.this.yBodyRot = FriendlyVex.this.getYRot();
                    }
                }
            }
        }
    }

    class VexRandomMoveGoal extends Goal {
        public VexRandomMoveGoal() {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            return !FriendlyVex.this.getMoveControl().hasWanted() && FriendlyVex.this.random.nextInt(reducedTickDelay(7)) == 0;
        }

        @Override
        public boolean canContinueToUse() {
            return false;
        }

        @Override
        public void tick() {
            BlockPos blockpos = FriendlyVex.this.getBoundOrigin();
            if (blockpos == null) {
                blockpos = FriendlyVex.this.blockPosition();
            }

            for (int i = 0; i < 3; i++) {
                BlockPos blockpos1 = blockpos.offset(FriendlyVex.this.random.nextInt(15) - 7, FriendlyVex.this.random.nextInt(11) - 5, FriendlyVex.this.random.nextInt(15) - 7);
                if (FriendlyVex.this.level().isEmptyBlock(blockpos1)) {
                    FriendlyVex.this.moveControl
                            .setWantedPosition((double)blockpos1.getX() + 0.5, (double)blockpos1.getY() + 0.5, (double)blockpos1.getZ() + 0.5, 0.25);
                    if (FriendlyVex.this.getTarget() == null) {
                        FriendlyVex.this.getLookControl()
                                .setLookAt((double)blockpos1.getX() + 0.5, (double)blockpos1.getY() + 0.5, (double)blockpos1.getZ() + 0.5, 180.0F, 20.0F);
                    }
                    break;
                }
            }
        }
    }
}
