package com.strangesmell.melodymagic.event;

import com.mojang.blaze3d.platform.InputConstants;
import com.strangesmell.melodymagic.MelodyMagic;
import com.strangesmell.melodymagic.item.CollectionItem;
import com.strangesmell.melodymagic.item.SoundContainerBakedModel;
import cpw.mods.util.Lazy;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import org.lwjgl.glfw.GLFW;

import javax.xml.crypto.Data;
import java.util.Map;

import static com.strangesmell.melodymagic.MelodyMagic.MODID;

@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(modid = MODID, bus = EventBusSubscriber.Bus.MOD,value = Dist.CLIENT)
public class ClitenModEvent {

    @SubscribeEvent
    public static void onModelBaked(ModelEvent.ModifyBakingResult event){
        // wrench item model
        Map<ModelResourceLocation, BakedModel> modelRegistry = event.getModels();
        ModelResourceLocation location = new ModelResourceLocation(BuiltInRegistries.ITEM.getKey(MelodyMagic.COLLECTION_ITEM.get()), "inventory");
        BakedModel existingModel = modelRegistry.get(location);
        if (existingModel == null) {
            throw new RuntimeException("Did not find COLLECTION_ITEM in registry");
        } else if (existingModel instanceof SoundContainerBakedModel) {
            throw new RuntimeException("Tried to replace COLLECTION_ITEM twice");
        } else {
            SoundContainerBakedModel model = new SoundContainerBakedModel(existingModel);
            event.getModels().put(location, model);
        }
    }


}
