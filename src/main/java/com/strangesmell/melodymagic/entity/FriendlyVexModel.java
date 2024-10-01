package com.strangesmell.melodymagic.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.ArmedModel;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FriendlyVexModel  extends HierarchicalModel<FriendlyVex> implements ArmedModel {
    private final ModelPart root;
    private final ModelPart body;
    private final ModelPart rightArm;
    private final ModelPart leftArm;
    private final ModelPart rightWing;
    private final ModelPart leftWing;
    private final ModelPart head;

    public FriendlyVexModel(ModelPart pRoot) {
        super(RenderType::entityTranslucent);
        this.root = pRoot.getChild("root");
        this.body = this.root.getChild("body");
        this.rightArm = this.body.getChild("right_arm");
        this.leftArm = this.body.getChild("left_arm");
        this.rightWing = this.body.getChild("right_wing");
        this.leftWing = this.body.getChild("left_wing");
        this.head = this.root.getChild("head");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();
        PartDefinition partdefinition1 = partdefinition.addOrReplaceChild("root", CubeListBuilder.create(), PartPose.offset(0.0F, -2.5F, 0.0F));
        partdefinition1.addOrReplaceChild(
                "head",
                CubeListBuilder.create().texOffs(0, 0).addBox(-2.5F, -5.0F, -2.5F, 5.0F, 5.0F, 5.0F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, 20.0F, 0.0F)
        );
        PartDefinition partdefinition2 = partdefinition1.addOrReplaceChild(
                "body",
                CubeListBuilder.create()
                        .texOffs(0, 10)
                        .addBox(-1.5F, 0.0F, -1.0F, 3.0F, 4.0F, 2.0F, new CubeDeformation(0.0F))
                        .texOffs(0, 16)
                        .addBox(-1.5F, 1.0F, -1.0F, 3.0F, 5.0F, 2.0F, new CubeDeformation(-0.2F)),
                PartPose.offset(0.0F, 20.0F, 0.0F)
        );
        partdefinition2.addOrReplaceChild(
                "right_arm",
                CubeListBuilder.create().texOffs(23, 0).addBox(-1.25F, -0.5F, -1.0F, 2.0F, 4.0F, 2.0F, new CubeDeformation(-0.1F)),
                PartPose.offset(-1.75F, 0.25F, 0.0F)
        );
        partdefinition2.addOrReplaceChild(
                "left_arm",
                CubeListBuilder.create().texOffs(23, 6).addBox(-0.75F, -0.5F, -1.0F, 2.0F, 4.0F, 2.0F, new CubeDeformation(-0.1F)),
                PartPose.offset(1.75F, 0.25F, 0.0F)
        );
        partdefinition2.addOrReplaceChild(
                "left_wing",
                CubeListBuilder.create().texOffs(16, 14).mirror().addBox(0.0F, 0.0F, 0.0F, 0.0F, 5.0F, 8.0F, new CubeDeformation(0.0F)).mirror(false),
                PartPose.offset(0.5F, 1.0F, 1.0F)
        );
        partdefinition2.addOrReplaceChild(
                "right_wing",
                CubeListBuilder.create().texOffs(16, 14).addBox(0.0F, 0.0F, 0.0F, 0.0F, 5.0F, 8.0F, new CubeDeformation(0.0F)),
                PartPose.offset(-0.5F, 1.0F, 1.0F)
        );
        return LayerDefinition.create(meshdefinition, 32, 32);
    }

    /**
     * Sets this entity's model rotation angles
     */
    public void setupAnim(FriendlyVex pEntity, float pLimbSwing, float pLimbSwingAmount, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch) {
        this.root().getAllParts().forEach(ModelPart::resetPose);
        this.head.yRot = pNetHeadYaw * (float) (Math.PI / 180.0);
        this.head.xRot = pHeadPitch * (float) (Math.PI / 180.0);
        float f = Mth.cos(pAgeInTicks * 5.5F * (float) (Math.PI / 180.0)) * 0.1F;
        this.rightArm.zRot = (float) (Math.PI / 5) + f;
        this.leftArm.zRot = -((float) (Math.PI / 5) + f);
        if (pEntity.isCharging()) {
            this.body.xRot = 0.0F;
            this.setArmsCharging(pEntity.getMainHandItem(), pEntity.getOffhandItem(), f);
        } else {
            this.body.xRot = (float) (Math.PI / 20);
        }

        this.leftWing.yRot = 1.0995574F + Mth.cos(pAgeInTicks * 45.836624F * (float) (Math.PI / 180.0)) * (float) (Math.PI / 180.0) * 16.2F;
        this.rightWing.yRot = -this.leftWing.yRot;
        this.leftWing.xRot = 0.47123888F;
        this.leftWing.zRot = -0.47123888F;
        this.rightWing.xRot = 0.47123888F;
        this.rightWing.zRot = 0.47123888F;
    }

    private void setArmsCharging(ItemStack pRightHandItem, ItemStack pLeftHandItem, float p_265125_) {
        if (pRightHandItem.isEmpty() && pLeftHandItem.isEmpty()) {
            this.rightArm.xRot = -1.2217305F;
            this.rightArm.yRot = (float) (Math.PI / 12);
            this.rightArm.zRot = -0.47123888F - p_265125_;
            this.leftArm.xRot = -1.2217305F;
            this.leftArm.yRot = (float) (-Math.PI / 12);
            this.leftArm.zRot = 0.47123888F + p_265125_;
        } else {
            if (!pRightHandItem.isEmpty()) {
                this.rightArm.xRot = (float) (Math.PI * 7.0 / 6.0);
                this.rightArm.yRot = (float) (Math.PI / 12);
                this.rightArm.zRot = -0.47123888F - p_265125_;
            }

            if (!pLeftHandItem.isEmpty()) {
                this.leftArm.xRot = (float) (Math.PI * 7.0 / 6.0);
                this.leftArm.yRot = (float) (-Math.PI / 12);
                this.leftArm.zRot = 0.47123888F + p_265125_;
            }
        }
    }

    @Override
    public ModelPart root() {
        return this.root;
    }

    @Override
    public void translateToHand(HumanoidArm pSide, PoseStack pPoseStack) {
        boolean flag = pSide == HumanoidArm.RIGHT;
        ModelPart modelpart = flag ? this.rightArm : this.leftArm;
        this.root.translateAndRotate(pPoseStack);
        this.body.translateAndRotate(pPoseStack);
        modelpart.translateAndRotate(pPoseStack);
        pPoseStack.scale(0.55F, 0.55F, 0.55F);
        this.offsetStackPosition(pPoseStack, flag);
    }

    private void offsetStackPosition(PoseStack pPoseStack, boolean pRightSide) {
        if (pRightSide) {
            pPoseStack.translate(0.046875, -0.15625, 0.078125);
        } else {
            pPoseStack.translate(-0.046875, -0.15625, 0.078125);
        }
    }
}
