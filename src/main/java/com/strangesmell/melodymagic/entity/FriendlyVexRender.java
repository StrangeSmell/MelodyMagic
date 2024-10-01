package com.strangesmell.melodymagic.entity;

import net.minecraft.client.model.VexModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Vex;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FriendlyVexRender extends MobRenderer<FriendlyVex, FriendlyVexModel> {
    public static final ResourceLocation VEX_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/illager/vex.png");
    public static final ResourceLocation VEX_CHARGING_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/illager/vex_charging.png");

    public FriendlyVexRender(EntityRendererProvider.Context p_174435_) {
        super(p_174435_, new FriendlyVexModel(p_174435_.bakeLayer(ModelLayers.VEX)), 0.3F);
        this.addLayer(new ItemInHandLayer<>(this, p_174435_.getItemInHandRenderer()));
    }

    protected int getBlockLightLevel(Vex pEntity, BlockPos pPos) {
        return 15;
    }

    /**
     * Returns the location of an entity's texture.
     */
    public ResourceLocation getTextureLocation(FriendlyVex pEntity) {
        return pEntity.isCharging() ? VEX_CHARGING_LOCATION : VEX_LOCATION;
    }
}