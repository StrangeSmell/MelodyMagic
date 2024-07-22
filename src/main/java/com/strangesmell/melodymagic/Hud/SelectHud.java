package com.strangesmell.melodymagic.Hud;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundEventListener;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.client.sounds.WeighedSoundEvents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.List;

import static com.strangesmell.melodymagic.MelodyMagic.MODID;
import static com.strangesmell.melodymagic.api.Util.getSoundVec3;

@OnlyIn(Dist.CLIENT)
public class SelectHud implements LayeredDraw.Layer , SoundEventListener {
    public static List<SoundInstance> subtitles = Lists.newArrayList();
    public static List<Double> distance = Lists.newArrayList();
    private static final SelectHud hud = new SelectHud();
    private final ResourceLocation HUD = ResourceLocation.fromNamespaceAndPath(MODID,"textures/gui/select.png");
    private final ResourceLocation HUD2 = ResourceLocation.withDefaultNamespace("textures/item/bucket.png");

    public static SelectHud getInstance(){
        return hud;
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, DeltaTracker p_348559_) {
        if(!subtitles.isEmpty()){
            SoundManager soundManager = Minecraft.getInstance().getSoundManager();
            for(int i=0;i<subtitles.size();i++){
                if(!soundManager.isActive(subtitles.get(i))){
                    subtitles.remove(i);
                    distance.remove(i);

                }
            }
        }

        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        pGuiGraphics.setColor(1.0F, 1.0F, 1.0F, 1.0f);
        pGuiGraphics.blit(HUD2, 0, 0,  0.0F, 0.0F, 16, 16, 16,16);
        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();
        pGuiGraphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
    }

    @Override
    public void onPlaySound(SoundInstance pSound, WeighedSoundEvents pAccessor, float pRange) {
        if (isAudibleFrom(new Vec3(pSound.getX(), pSound.getY(), pSound.getZ()),pRange)) {
            if(Minecraft.getInstance().player==null) return;
            if(subtitles.contains(pSound)) return;
            subtitles.add(pSound);
            distance.add(getSoundVec3(pSound).distanceTo(Minecraft.getInstance().player.position()));
            Minecraft.getInstance().getSoundManager().stop(pSound);
        }

    }

    public boolean isAudibleFrom(Vec3 pLocation, float pRange) {
        if (Float.isInfinite(pRange)) {
            return true;
        } else {
            if(Minecraft.getInstance().player==null) return false;
            return !(Minecraft.getInstance().player.position().distanceTo(pLocation) > pRange);
        }
    }
}
