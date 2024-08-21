package com.strangesmell.melodymagic.container;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class WandScreen extends AbstractContainerScreen<WandMenu> {
    private final NonNullList<ItemStack> lastSlots = NonNullList.withSize(9,ItemStack.EMPTY);

    private static final ResourceLocation CONTAINER_LOCATION = ResourceLocation.withDefaultNamespace("textures/gui/container/dispenser.png");

    public WandScreen(WandMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        //lastSlots.addAll(pMenu.getItems());
    }
    @Override
    protected void init() {
        super.init();
        this.titleLabelX = (this.imageWidth - this.font.width(this.title)) / 2;
    }


    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        this.renderTooltip(pGuiGraphics, pMouseX, pMouseY);
        pGuiGraphics.pose().pushPose();
        long time = Minecraft.getInstance().level.getGameTime();
        float f1 = Mth.lerp(pPartialTick,time, time+1);
        PoseStack poseStack = pGuiGraphics.pose();
        poseStack.clear();
        Player player = Minecraft.getInstance().player;

        float z= (float) player.getZ();
        int k = 6;
        float r=68/k;
        int coefficent=1;
        poseStack.scale(3,3,3);
        if(player.getX()!=0.5){
            poseStack.rotateAround(Axis.XP.rotationDegrees(f1*coefficent),(float) this.width/k,(float)this.height/k-r,150);
        }
        if(player.getY()!=0){
            poseStack.rotateAround(Axis.YP.rotationDegrees(f1*coefficent),(float) this.width/k,(float)this.height/k-r,150);
        }
        if(player.getZ()!=0.5){
            poseStack.rotateAround(Axis.ZP.rotationDegrees(f1*coefficent),(float) this.width/k,(float)this.height/k-r,150);
        }



        pGuiGraphics.renderItem(new ItemStack(Items.AMETHYST_SHARD),  (int) (this.width/k-16*0.5), (int) (this.height/k-16*0.5-r));
        //pGuiGraphics.renderItem(new ItemStack(Items.AMETHYST_SHARD),  (int) pMouseX/k,  (int) pMouseY/k);


        pGuiGraphics.pose().popPose();

    }



    @Override
    protected void renderBg(GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2+40;

        pGuiGraphics.blit(CONTAINER_LOCATION, i, j+77, 0, 0, this.imageWidth, 5);
        pGuiGraphics.blit(CONTAINER_LOCATION, i, j+82, 0, 82, this.imageWidth, this.imageHeight);
    }
}
