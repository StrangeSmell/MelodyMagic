package com.strangesmell.melodymagic.hud;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import com.strangesmell.melodymagic.MelodyMagic;
import com.strangesmell.melodymagic.item.CollectionItem;
import com.strangesmell.melodymagic.item.SoundContainerItem;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.client.gui.components.SubtitleOverlay;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundEventListener;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.client.sounds.WeighedSoundEvents;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.minecraft.sounds.SoundSource;
import net.neoforged.api.distmarker.OnlyIn;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWScrollCallback;

import javax.annotation.Nullable;
import java.util.*;

import static com.strangesmell.melodymagic.MelodyMagic.MODID;
import static com.strangesmell.melodymagic.api.Util.getSoundEffectToString;

@OnlyIn(Dist.CLIENT)
public class SelectHud implements LayeredDraw.Layer , SoundEventListener {
    public static List<SoundInstance> subtitles = Lists.newArrayList();
    public static Map<ResourceLocation,String> subtitles2 = new HashMap();
    public static List<Float> range = Lists.newArrayList();
    public static List<Float> volume = Lists.newArrayList();
    public static List<Float> peach = Lists.newArrayList();
    public static List<List<Double>> location = Lists.newArrayList();
    private static final SelectHud hud = new SelectHud();
    private final ResourceLocation HUD = ResourceLocation.fromNamespaceAndPath(MODID,"textures/gui/select.png");
    private final ResourceLocation HUD2 = ResourceLocation.withDefaultNamespace("textures/item/bucket.png");
    private static GLFWScrollCallback scrollCallback;
    public static SelectHud getInstance(){
        return hud;
    }
    private static final ResourceLocation SELECT = ResourceLocation.fromNamespaceAndPath(MelodyMagic.MODID,"textures/gui/select.png");

    @Override
    public void render(GuiGraphics pGuiGraphics, DeltaTracker p_348559_) {
        if(!subtitles.isEmpty()){
            SoundManager soundManager = Minecraft.getInstance().getSoundManager();
            for(int i=0;i<subtitles.size();i++){
                if(!soundManager.isActive(subtitles.get(i))){
                    //subtitles2.remove(subtitles.get(i).getLocation());
                    subtitles.remove(i);
                    location.remove(i);
                    range.remove(i);
                    volume.remove(i);
                    peach.remove(i);

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
            float k1 =1.25f;
            float k2 =0.8f;
            for(int i=0;i<9;i++){
                if(selectCount==i){
                    pGuiGraphics.pose().scale(k1,k1,k1);
                    pGuiGraphics.blit(SELECT,(int) (pGuiGraphics.guiWidth()/2/k1-12+Math.sin(2*Math.PI*i/9)*r/k1), (int) (pGuiGraphics.guiHeight()/2/k1-12-Math.cos(2*Math.PI*i/9)*r/k1),0,0,0,24,24,24,24);
                    pGuiGraphics.pose().scale(k2,k2,k2);
                }else{
                    pGuiGraphics.blit(SELECT,(int) (pGuiGraphics.guiWidth()/2-12+Math.sin(2*Math.PI*i/9)*r), (int) (pGuiGraphics.guiHeight()/2-12-Math.cos(2*Math.PI*i/9)*r),0,0,0,24,24,24,24);

                }
            }
            //加入格子
            for(int i=0;i<9;i++){
                if(itemContainerContents==null || itemContainerContents.getSlots()<=i)return;
                if(selectCount==i){
                    pGuiGraphics.pose().scale(k1,k1,k1);
                    pGuiGraphics.renderItem(itemContainerContents.getStackInSlot(i),(int) (pGuiGraphics.guiWidth()/2/k1-8+Math.sin(2*Math.PI*i/9)*r/k1), (int) (pGuiGraphics.guiHeight()/2/k1-8-Math.cos(2*Math.PI*i/9)*r/k1));

                    pGuiGraphics.pose().scale(k2,k2,k2);
                    if(!(itemContainerContents.getStackInSlot(i).getItem() == Items.AIR)){

                        pGuiGraphics.renderComponentTooltip(Minecraft.getInstance().font,itemContainerContents.getStackInSlot(i).getTooltipLines(Item.TooltipContext.EMPTY,null, TooltipFlag.NORMAL),(int)(pGuiGraphics.guiWidth()/2-8+Math.sin(2*Math.PI*i/9)*r)+20,(int)(pGuiGraphics.guiHeight()/2-8-Math.cos(2*Math.PI*i/9)*r)+8);
                    }

                    itemRenderer.renderStatic(Items.AMETHYST_SHARD.getDefaultInstance(), ItemDisplayContext.FIXED, LevelRenderer.getLightColor(Minecraft.getInstance().level, Minecraft.getInstance().player.getOnPos()), OverlayTexture.NO_OVERLAY, pGuiGraphics.pose(), pGuiGraphics.bufferSource(), null, 1);


                }else{
                    pGuiGraphics.renderItem(itemContainerContents.getStackInSlot(i),(int)(pGuiGraphics.guiWidth()/2-8+Math.sin(2*Math.PI*i/9)*r),(int)(pGuiGraphics.guiHeight()/2-8-Math.cos(2*Math.PI*i/9)*r));

                }

            }

            int k = 6;
            pGuiGraphics.pose().scale(3,3,3);//渲染紫水晶
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
            if(pAccessor.getSubtitle()==null){
                subtitles2.put(pSound.getLocation(),"untranslated_sound");
            }else{
                subtitles2.put(pSound.getLocation(),pAccessor.getSubtitle().getString());
            }


            List<Double> temp_location = Lists.newArrayList();
            Player player = Minecraft.getInstance().player;
            temp_location.add(pSound.getX()-player.getX());
            temp_location.add(pSound.getY()-player.getY());
            temp_location.add(pSound.getZ()-player.getZ());
            location.add(temp_location);
            range.add(pRange);
            volume.add(pSound.getVolume());
            peach.add(pSound.getPitch());
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

    @OnlyIn(Dist.CLIENT)
    static record SoundPlayedAt(Vec3 location, long time) {
    }

    @OnlyIn(Dist.CLIENT)
    static class Subtitle {
        private final Component text;
        private final float range;
        private final List<SelectHud.SoundPlayedAt> playedAt = new ArrayList<>();

        public Subtitle(Component pText, float pRange, Vec3 pLocation) {
            this.text = pText;
            this.range = pRange;
            this.playedAt.add(new SelectHud.SoundPlayedAt(pLocation, Util.getMillis()));
        }

        public Component getText() {
            return this.text;
        }

        @Nullable
        public SelectHud.SoundPlayedAt getClosest(Vec3 p_347452_) {
            if (this.playedAt.isEmpty()) {
                return null;
            } else {
                return this.playedAt.size() == 1
                        ? this.playedAt.getFirst()
                        : this.playedAt.stream().min(Comparator.comparingDouble(p_347541_ -> p_347541_.location().distanceTo(p_347452_))).orElse(null);
            }
        }

        public void refresh(Vec3 pLocation) {
            this.playedAt.removeIf(p_347631_ -> pLocation.equals(p_347631_.location()));
            this.playedAt.add(new SelectHud.SoundPlayedAt(pLocation, Util.getMillis()));
        }

        public boolean isAudibleFrom(Vec3 pLocation) {
            if (Float.isInfinite(this.range)) {
                return true;
            } else if (this.playedAt.isEmpty()) {
                return false;
            } else {
                SelectHud.SoundPlayedAt subtitleoverlay$soundplayedat = this.getClosest(pLocation);
                return subtitleoverlay$soundplayedat != null && pLocation.closerThan(subtitleoverlay$soundplayedat.location, (double) this.range);
            }
        }

        public void purgeOldInstances(double p_347730_) {
            long i = Util.getMillis();
            this.playedAt.removeIf(p_347590_ -> (double)(i - p_347590_.time()) > p_347730_);
        }

        public boolean isStillActive() {
            return !this.playedAt.isEmpty();
        }
    }

}
