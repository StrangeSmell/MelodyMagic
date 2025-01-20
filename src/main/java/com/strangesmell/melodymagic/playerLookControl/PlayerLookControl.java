package com.strangesmell.melodymagic.playerLookControl;

import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.control.Control;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.math3.geometry.euclidean.threed.Rotation;

import java.util.Optional;

public class PlayerLookControl implements Control {
    protected final LivingEntity livingEntity;
    protected float yMaxRotSpeed;
    protected float xMaxRotAngle;
    protected double wantedX;
    protected double wantedY;
    protected double wantedZ;

    public PlayerLookControl(LivingEntity livingEntity) {
        this.livingEntity = livingEntity;
    }

    /**
     * Sets the livingEntity's look vector
     */
    public void setLookAt(Vec3 lookVector) {
        this.setLookAt(lookVector.x, lookVector.y, lookVector.z);
    }

    /**
     * Sets the controlling mob's look vector to the provided entity's location
     */
    public void setLookAt(Entity entity) {
        this.setLookAt(entity.getX(), getWantedY(entity), entity.getZ());
    }

    /**
     * Sets position to look at using entity
     */
    public void setLookAt(Entity entity, float deltaYaw, float deltaPitch) {
        this.setLookAt(entity.getX(), getWantedY(entity), entity.getZ(), deltaYaw, deltaPitch);
    }

    public void setLookAt(double x, double y, double z) {
        this.setLookAt(x, y, z, 10, 40);
    }

    /**
     * Sets position to look at
     */
    public void setLookAt(double x, double y, double z, float deltaYaw, float deltaPitch) {
        this.wantedX = x;
        this.wantedY = y;
        this.wantedZ = z;
        this.yMaxRotSpeed = deltaYaw;
        this.xMaxRotAngle = deltaPitch;

    }

    public void tick() {
        //this.getYRotD().ifPresent(p_287447_ -> this.livingEntity.setYHeadRot(this.rotateTowards(this.livingEntity.yBodyRot, p_287447_, this.yMaxRotSpeed)));
        this.getYRotD().ifPresent(p_287447_ -> setRotation(this.livingEntity,this.rotateTowards(Minecraft.getInstance().cameraEntity.getYRot(), p_287447_, this.yMaxRotSpeed)));

        this.getXRotD().ifPresent(p_352768_ -> this.livingEntity.setXRot(this.rotateTowards(Minecraft.getInstance().cameraEntity.getXRot(), p_352768_, this.xMaxRotAngle)));

    }

    public static void setRotation(LivingEntity livingEntity,float f){
        livingEntity.setYBodyRot(f);
        livingEntity.setYHeadRot(f);
        Minecraft.getInstance().cameraEntity.setYRot(f);
    }

    public double getWantedX() {
        return this.wantedX;
    }

    public double getWantedY() {
        return this.wantedY;
    }

    public double getWantedZ() {
        return this.wantedZ;
    }

    protected Optional<Float> getXRotD() {
        double d0 = this.wantedX - this.livingEntity.getX();
        double d1 = this.wantedY - this.livingEntity.getEyeY();
        double d2 = this.wantedZ - this.livingEntity.getZ();
        double d3 = Math.sqrt(d0 * d0 + d2 * d2);
        return !(Math.abs(d1) > 1.0E-5F) && !(Math.abs(d3) > 1.0E-5F) ? Optional.empty() : Optional.of((float)(-(Mth.atan2(d1, d3) * 180.0F / (float)Math.PI)));
    }

    protected Optional<Float> getYRotD() {
        double d0 = this.wantedX - this.livingEntity.getX();
        double d1 = this.wantedZ - this.livingEntity.getZ();
        return !(Math.abs(d1) > 1.0E-5F) && !(Math.abs(d0) > 1.0E-5F)
                ? Optional.empty()
                : Optional.of((float)(Mth.atan2(d1, d0) * 180.0F / (float)Math.PI) - 90.0F);
    }

    /**
     * Rotate as much as possible from {@code from} to {@code to} within the bounds of {@code maxDelta}
     */
    protected float rotateTowards(float from, float to, float maxDelta) {
        float f = Mth.degreesDifference(from, to);
        float f1 = Mth.clamp(f, -maxDelta, maxDelta);
        return from + f1;
    }

    private static double getWantedY(Entity entity) {
        return entity instanceof LivingEntity ? entity.getEyeY() : (entity.getBoundingBox().minY + entity.getBoundingBox().maxY) / 2.0;
    }
}
