package com.strangesmell.melodymagic.hud;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import com.strangesmell.melodymagic.MelodyMagic;
import com.strangesmell.melodymagic.item.CollectionItem;
import com.strangesmell.melodymagic.item.SoundContainerItem;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundEventListener;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.client.sounds.WeighedSoundEvents;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.minecraft.sounds.SoundSource;
import net.neoforged.api.distmarker.OnlyIn;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWScrollCallback;
import net.minecraft.world.item.ItemDisplayContext;

import java.util.List;

import static com.strangesmell.melodymagic.MelodyMagic.MODID;


@OnlyIn(Dist.CLIENT)
public class SelectHud implements LayeredDraw.Layer , SoundEventListener {
    public static List<SoundInstance> subtitles = Lists.newArrayList();
    public static List<Double> distance = Lists.newArrayList();
    public static List<List<Double>> location = Lists.newArrayList();
    private static final SelectHud hud = new SelectHud();
    private final ResourceLocation HUD = ResourceLocation.fromNamespaceAndPath(MODID,"textures/gui/select.png");
    private final ResourceLocation HUD2 = ResourceLocation.withDefaultNamespace("textures/item/bucket.png");
    private static GLFWScrollCallback scrollCallback;
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
                    location.remove(i);

                }
            }
        }


        if(!hasAltDown()) return;
        Player player = Minecraft.getInstance().player;
        ItemStack itemStack = player.getItemInHand(player.getUsedItemHand());
        if(itemStack.getItem() instanceof CollectionItem collectionItem){

             int selectCount=0;
             int oldselectCount=0;

            CompoundTag compoundTag =itemStack.getOrDefault(DataComponents.CUSTOM_DATA,CustomData.EMPTY).copyTag();
            if(!compoundTag.contains(MODID+"select_index")){
                compoundTag.putInt(MODID+"select_index",selectCount);
            }else{
                selectCount = compoundTag.getInt(MODID+"select_index");
            }

            if(!compoundTag.contains(MODID+"old_select_index")){
                compoundTag.putInt(MODID+"old_select_index",oldselectCount);
            }else{
                oldselectCount = compoundTag.getInt(MODID+"old_select_index");
            }

            RenderSystem.disableDepthTest();
            RenderSystem.depthMask(false);
            pGuiGraphics.setColor(1.0F, 1.0F, 1.0F, 1.0f);
            ItemContainerContents itemContainerContents = itemStack.get(DataComponents.CONTAINER);
            
            int r = pGuiGraphics.guiHeight()/4;
            int r2 = pGuiGraphics.guiHeight()/16;


            ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
            //double dx= Mth.lerp(p_348559_.getGameTimeDeltaPartialTick());
            for(int i=0;i<9;i++){
                //加入格子
                if(itemContainerContents==null || itemContainerContents.getSlots()<=i)return;
                if(selectCount==i){
                    pGuiGraphics.pose().scale(2,2,2);
                    pGuiGraphics.renderItem(itemContainerContents.getStackInSlot(i),(int)(pGuiGraphics.guiWidth()/2-8+Math.sin(2*Math.PI*i/9)*r)/2-4,(int)(pGuiGraphics.guiHeight()/2-8-Math.cos(2*Math.PI*i/9)*r)/2-4);
                    pGuiGraphics.pose().scale(0.5F,0.5F,0.5F);

                    itemRenderer.renderStatic(Items.AMETHYST_SHARD.getDefaultInstance(),ItemDisplayContext.FIXED,LevelRenderer.getLightColor(Minecraft.getInstance().level,Minecraft.getInstance().player.getOnPos()), OverlayTexture.NO_OVERLAY,pGuiGraphics.pose(),pGuiGraphics.bufferSource(),null,1);


                }else{
                    pGuiGraphics.renderItem(itemContainerContents.getStackInSlot(i),(int)(pGuiGraphics.guiWidth()/2-8+Math.sin(2*Math.PI*i/9)*r),(int)(pGuiGraphics.guiHeight()/2-8-Math.cos(2*Math.PI*i/9)*r));

                }

            }

            int k = 6;
            pGuiGraphics.pose().scale(3,3,3);
            pGuiGraphics.renderItem(Items.AMETHYST_SHARD.getDefaultInstance(), pGuiGraphics.guiWidth()/k-8, pGuiGraphics.guiHeight()/k-8);

            pGuiGraphics.blit(HUD2, 0, 0,  0.0F, 0.0F, 16, 16, 16,16);
            RenderSystem.depthMask(true);
            RenderSystem.enableDepthTest();
            pGuiGraphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
        }

    }

    @Override
    public void onPlaySound(SoundInstance pSound, WeighedSoundEvents pAccessor, float pRange) {
        if (pSound.getSource() != SoundSource.MASTER && isAudibleFrom(new Vec3(pSound.getX(), pSound.getY(), pSound.getZ()),pRange)) {
            if(Minecraft.getInstance().player==null) return;
            if(subtitles.contains(pSound)) return;
            subtitles.add(pSound);
            List<Double> temp_location = Lists.newArrayList();
            Player player = Minecraft.getInstance().player;
            temp_location.add(pSound.getX()-player.getX());
            temp_location.add(pSound.getY()-player.getY());
            temp_location.add(pSound.getZ()-player.getZ());
            location.add(temp_location);
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

    public static boolean hasControlDown() {
        return Minecraft.ON_OSX
                ? InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 343)
                || InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 347)
                : InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 341)
                || InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 345);
    }

    public static boolean hasAltDown() {
        return InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 342)
                || InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 346);
    }
}
