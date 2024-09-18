package com.strangesmell.melodymagic.api;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

public class EffectUtil {

    public static void shoot(
            ServerLevel level,
            LivingEntity player,
            InteractionHand hand,
            ItemStack bow,
            List<ItemStack> draw,
            float p_331007_,
            float p_331445_,
            boolean p_331107_,
            @Nullable LivingEntity livingEntity
    ) {
        float f = EnchantmentHelper.processProjectileSpread(level, bow, player, 0.0F);
        float f1 = draw.size() == 1 ? 0.0F : 2.0F * f / (float)(draw.size() - 1);
        float f2 = (float)((draw.size() - 1) % 2) * f1 / 2.0F;
        float f3 = 1.0F;

        for (int i = 0; i < draw.size(); i++) {
            ItemStack itemstack = draw.get(i);
            if (!itemstack.isEmpty()) {
                float f4 = f2 + f3 * (float)((i + 1) / 2) * f1;
                f3 = -f3;
                Projectile projectile = createProjectile(level, player, bow, itemstack, p_331107_);
                shootProjectile(player, projectile, i, p_331007_, p_331445_, f4, livingEntity);
                level.addFreshEntity(projectile);
                if (bow.isEmpty()) {
                    break;
                }
            }
        }
    }

    public static void shootProjectile(
            LivingEntity pShooter, Projectile pProjectile, int pIndex, float pVelocity, float pInaccuracy, float pAngle, @Nullable LivingEntity pTarget
    ) {
        pProjectile.shootFromRotation(pShooter, pShooter.getXRot(), pShooter.getYRot() + pAngle, 0.0F, pVelocity, pInaccuracy);
    }

    public static Projectile createProjectile(Level pLevel, LivingEntity pShooter, ItemStack pWeapon, ItemStack pAmmo, boolean pIsCrit) {
        ArrowItem arrowitem = pAmmo.getItem() instanceof ArrowItem arrowitem1 ? arrowitem1 : (ArrowItem)Items.ARROW;
        AbstractArrow abstractarrow = arrowitem.createArrow(pLevel, pAmmo, pShooter, pWeapon);
        if (pIsCrit) {
            abstractarrow.setCritArrow(true);
        }

        return customArrow(abstractarrow, pAmmo, pWeapon);
    }

    public static AbstractArrow customArrow(AbstractArrow arrow, ItemStack projectileStack, ItemStack weaponStack) {
        return arrow;
    }

    public static float getPowerForTime(int pCharge) {
        float f = (float)pCharge / 20.0F;
        f = (f * f + f * 2.0F) / 3.0F;
        if (f > 1.0F) {
            f = 1.0F;
        }

        return f;
    }
}
