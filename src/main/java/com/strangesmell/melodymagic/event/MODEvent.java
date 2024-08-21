package com.strangesmell.melodymagic.event;

import com.strangesmell.melodymagic.MelodyMagic;
import com.strangesmell.melodymagic.container.WandScreen;
import com.strangesmell.melodymagic.item.CollectionItem;
import com.strangesmell.melodymagic.item.SoundContainerBakedModel;
import com.strangesmell.melodymagic.message.ClientPayloadHandler;
import com.strangesmell.melodymagic.message.SelectCount;
import com.strangesmell.melodymagic.message.ServerPayloadHandler;
import com.strangesmell.melodymagic.message.SoundData;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.items.ComponentItemHandler;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.DirectionalPayloadHandler;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

import java.util.Map;

import static com.strangesmell.melodymagic.MelodyMagic.MODID;

@EventBusSubscriber(modid = MODID, bus = EventBusSubscriber.Bus.MOD)
public class MODEvent {
    @SubscribeEvent
    public static void registerPayloadHandlers(final RegisterPayloadHandlersEvent event)
    {
        final PayloadRegistrar registrar = event.registrar("1");
        registrar.playBidirectional(
                SoundData.TYPE,
                SoundData.STREAM_CODEC,
                new DirectionalPayloadHandler<>(
                        ClientPayloadHandler::handleData,
                        ServerPayloadHandler::handleData
                )
        );
        registrar.playBidirectional(
                SelectCount.TYPE,
                SelectCount.STREAM_CODEC,
                new DirectionalPayloadHandler<>(
                        ClientPayloadHandler::handleData,
                        ServerPayloadHandler::handleData
                )
        );
    }
    @SubscribeEvent
    public static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(MelodyMagic.WAND_MENU.get(), WandScreen::new);
    }

    @SubscribeEvent
    public static void registerCapabilitiesEvent(RegisterCapabilitiesEvent event)
    {
        event.registerItem(Capabilities.ItemHandler.ITEM, (itemStack, context) ->new ComponentItemHandler(itemStack, DataComponents.CONTAINER, 20), MelodyMagic.COLLECTION_ITEM.get());
    }


}
