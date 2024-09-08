package com.strangesmell.melodymagic.api;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.NeoForgeMod;

import java.util.Objects;

import static net.minecraft.world.entity.ai.attributes.Attributes.BLOCK_INTERACTION_RANGE;

public class ViewUtil {

    public static HitResult getHitResult(Level level, Player player,double range) {
        AABB aabb;
        //double range = Objects.requireNonNull(player.getAttribute(BLOCK_INTERACTION_RANGE)).getValue();
        Vec3 eyePosition = player.getEyePosition();
        Vec3 viewVector = player.getViewVector(1.0f);
        Vec3 endPosition = eyePosition.add(viewVector.x * range, viewVector.y * range, viewVector.z * range);
        //may false
        HitResult hitResult = ProjectileUtil.getEntityHitResult((Level)level, (Entity)player, (Vec3)eyePosition, (Vec3)endPosition, (AABB)(aabb = player.getBoundingBox().expandTowards(viewVector.scale(range)).inflate(1.0, 1.0, 1.0)), entity -> !entity.isSpectator() );
        if (hitResult == null) {
            hitResult = level.clip(new ClipContext(eyePosition, endPosition, ClipContext.Block.OUTLINE, ClipContext.Fluid.SOURCE_ONLY, (Entity)player));
        }
        return hitResult;
    }
}
