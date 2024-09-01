package com.strangesmell.melodymagic.item;

import com.google.common.base.Suppliers;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.strangesmell.melodymagic.MelodyMagic;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;

import java.util.function.Supplier;

@OnlyIn(Dist.CLIENT)
public class SoundContainerItemStackRenderer extends BlockEntityWithoutLevelRenderer {
    private static int degree = 0;

    public SoundContainerItemStackRenderer() {
        super(null, null);
    }

    @Override
    public void renderByItem(ItemStack pStack, ItemDisplayContext ctx, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, int pPackedOverlay) {
        int k = 5;
        if (degree == 360 * k) {
            degree = 0;
        }
        degree++;

        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();

        pPoseStack.pushPose();
        pPoseStack.translate(0.5F, 0.5F, 0.5F);
        float xOffset = -1 / 32f;
        float zOffset = 0;

        if (ctx == ItemDisplayContext.GUI || ctx == ItemDisplayContext.FIXED) {

        } else {
            pPoseStack.translate(-xOffset, 0, -zOffset);
            pPoseStack.mulPose(Axis.YP.rotationDegrees(90));
            pPoseStack.translate(xOffset, 0, zOffset);
        }

        if (ctx == ItemDisplayContext.FIRST_PERSON_RIGHT_HAND || ctx == ItemDisplayContext.FIRST_PERSON_LEFT_HAND) {
            pPoseStack.translate(0.15F, 0.15F, 0);
        }

        itemRenderer.renderStatic(MelodyMagic.COLLECTION_DISPLAY_ITEM.toStack(), ItemDisplayContext.NONE, pPackedLight, pPackedOverlay, pPoseStack, pBuffer, null, 1);
        pPoseStack.popPose();

        //紫水晶

        pPoseStack.pushPose();
        pPoseStack.translate(0.5F, 0.5F, 0.5F);

        if (ctx != ItemDisplayContext.FIRST_PERSON_LEFT_HAND && ctx != ItemDisplayContext.FIRST_PERSON_RIGHT_HAND) {
            pPoseStack.popPose();
            return;
        } else {
            pPoseStack.translate(-xOffset, 0, -zOffset);
            pPoseStack.mulPose(Axis.YP.rotationDegrees(90));
            pPoseStack.translate(xOffset, 0, zOffset);
        }

        pPoseStack.translate(0.29, 0.29, 0);
        pPoseStack.translate(0.15, 0.15, 0);
        pPoseStack.mulPose(Axis.YP.rotationDegrees(degree / k));
        pPoseStack.mulPose(Axis.XP.rotationDegrees(degree / k));
        pPoseStack.mulPose(Axis.ZP.rotationDegrees(degree / k));
        pPoseStack.translate(-0.29, -0.29, 0);
        pPoseStack.translate(-0.15, -0.15, 0);


        pPoseStack.translate(0.29F, 0.29F, 0);
        pPoseStack.scale(0.1F, 0.1F, 0.1F);

        pPoseStack.translate(1.5, 1.5, 0);
        itemRenderer.renderStatic(Items.AMETHYST_SHARD.getDefaultInstance(), ItemDisplayContext.NONE, pPackedLight, pPackedOverlay, pPoseStack, pBuffer, null, 1);
        pPoseStack.popPose();


    }
}
