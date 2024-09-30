package com.strangesmell.melodymagic.event;

import com.mojang.blaze3d.platform.InputConstants;
import com.strangesmell.melodymagic.MelodyMagic;
import com.strangesmell.melodymagic.item.SoundContainerBakedModel;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.jarjar.nio.util.Lazy;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import net.neoforged.neoforge.client.settings.KeyModifier;
import org.lwjgl.glfw.GLFW;

import java.util.Map;

import static com.strangesmell.melodymagic.MelodyMagic.MODID;

@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(modid = MODID, bus = EventBusSubscriber.Bus.MOD,value = Dist.CLIENT)
public class ClientModEvent {

    public static final Lazy<KeyMapping> ALT = Lazy.of(() -> new KeyMapping(
            "key.melodymagic.hud", // 将使用该翻译键进行本地化
            KeyConflictContext.IN_GAME, // 映射只能在当一个屏幕打开时使用
            InputConstants.Type.KEYSYM, // 在键盘上的默认映射
            GLFW.GLFW_KEY_LEFT_ALT, // 左alt
            "key.categories.melodymagic" // 映射将在杂项（misc）类别中
    ));

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

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void key(RegisterKeyMappingsEvent event) {
        event.register(ALT.get());
    }

}
