package com.strangesmell.melodymagic.hud;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import com.strangesmell.melodymagic.api.RenderUtil;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static com.strangesmell.melodymagic.MelodyMagic.*;

@OnlyIn(Dist.CLIENT)
public class DanceHud implements LayeredDraw.Layer {
    private static final ResourceLocation PAGE_FORWARD_HIGHLIGHTED_SPRITE = ResourceLocation.withDefaultNamespace("widget/page_forward_highlighted");
    private static final ResourceLocation PAGE_FORWARD_SPRITE = ResourceLocation.withDefaultNamespace("widget/page_forward");
    private static final ResourceLocation PAGE_BACKWARD_HIGHLIGHTED_SPRITE = ResourceLocation.withDefaultNamespace("widget/page_backward_highlighted");
    private static final ResourceLocation PAGE_BACKWARD_SPRITE = ResourceLocation.withDefaultNamespace("widget/page_backward");

    private static final ResourceLocation UP = ResourceLocation.fromNamespaceAndPath(MODID,"textures/hud/up.png");
    private static final ResourceLocation DOWN = ResourceLocation.fromNamespaceAndPath(MODID,"textures/hud/down.png");
    private static final ResourceLocation LEFT = ResourceLocation.fromNamespaceAndPath(MODID,"textures/hud/left.png");
    private static final ResourceLocation RIGHT = ResourceLocation.fromNamespaceAndPath(MODID,"textures/hud/right.png");
    public List<SoundEvent> subtitles = Lists.newArrayList();
    public List<Float> volume = Lists.newArrayList();
    public List<Float> peach = Lists.newArrayList();
    public List<List<Double>> location = Lists.newArrayList();
    public List<Integer> time = Lists.newArrayList();
    public List<Integer> random_time = Lists.newArrayList();
    public int useTime = 0;
    private static final DanceHud hud = new DanceHud();
    private long fftTime;
    public static DanceHud getInstance() {
        return hud;
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, @NotNull DeltaTracker deltaTracker) {
/*        Player player = Minecraft.getInstance().player;
        Level level = Minecraft.getInstance().level;
        if(player != null) {
            if(!player.isUsingItem()) return;
            guiGraphics.pose().pushPose();
            float partialTick = deltaTracker.getGameTimeDeltaPartialTick(false);

            for(int i=0;i<time.size();i++) {
                if(time.get(i) -useTime> 0) {

                    int move_time = time.get(i)-useTime;

                    ResourceLocation res=UP;

                    int intOfString = random_time.get(i);

                    res = switch (intOfString  % 4) {
                        case 0 -> UP;
                        case 1 -> DOWN;
                        case 2 -> LEFT;
                        case 3 -> RIGHT;
                        default -> res;
                    };

                    int leftOrRight = time.get(i) % 2;
                    if(leftOrRight == 0) {
                        leftOrRight=-1;
                    }else if(leftOrRight == 1){
                        leftOrRight = 1;
                    }
                    if(move_time>40){
                        RenderSystem.enableBlend();
                        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, Math.min(1f,40/(float)move_time));
                    }

                    if(res.equals(UP)) {
                        RenderUtil.blit(res, ((float) guiGraphics.guiWidth()) /2 + leftOrRight*Mth.lerp(partialTick,move_time,move_time-1)*5 , ((float) guiGraphics.guiHeight() )/2-16,16,16,0f,0f,16f,16f,16f,16f,guiGraphics);
                    }else if(res.equals(DOWN)) {
                        RenderUtil.blit(res, ((float) guiGraphics.guiWidth()) /2 + leftOrRight*Mth.lerp(partialTick,move_time,move_time-1)*5 , ((float) guiGraphics.guiHeight() )/2+16,16,16,0f,0f,16f,16f,16f,16f,guiGraphics);
                    }else if(res.equals(LEFT)) {
                        RenderUtil.blit(res, ((float) guiGraphics.guiWidth()) /2 + leftOrRight*Mth.lerp(partialTick,move_time,move_time-1)*5 , ((float) guiGraphics.guiHeight() )/2-32,16,16,0f,0f,16f,16f,16f,16f,guiGraphics);
                    }else if(res.equals(RIGHT)) {
                        RenderUtil.blit(res, ((float) guiGraphics.guiWidth()) /2 + leftOrRight*Mth.lerp(partialTick,move_time,move_time-1)*5 , ((float) guiGraphics.guiHeight() )/2+32,16,16,0f,0f,16f,16f,16f,16f,guiGraphics);
                    }

                    if(move_time>20){
                        RenderSystem.disableBlend();
                        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1);
                    }

                }
            }
            guiGraphics.pose().popPose();


        }*/

    }

}
