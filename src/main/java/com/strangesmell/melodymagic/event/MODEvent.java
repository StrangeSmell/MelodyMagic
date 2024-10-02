package com.strangesmell.melodymagic.event;

import com.strangesmell.melodymagic.MelodyMagic;
import com.strangesmell.melodymagic.container.ChestContainerScreen;
import com.strangesmell.melodymagic.container.WandScreen;
import com.strangesmell.melodymagic.entity.FriendlyVex;
import com.strangesmell.melodymagic.message.*;
import net.minecraft.core.component.DataComponents;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.items.ComponentItemHandler;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.DirectionalPayloadHandler;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

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
                ContinueSoundData.TYPE,
                ContinueSoundData.STREAM_CODEC,
                new DirectionalPayloadHandler<>(
                        ClientPayloadHandler::handleContinueData,
                        ServerPayloadHandler::handleContinueData
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
        registrar.playBidirectional(
                RecordData.TYPE,
                RecordData.STREAM_CODEC,
                new DirectionalPayloadHandler<>(
                        ClientPayloadHandler::handleRecordData,
                        ServerPayloadHandler::handleRecordData
                )
        );
/*        registrar.playBidirectional(
                ItemsData.TYPE,
                ItemsData.STREAM_CODEC,
                new DirectionalPayloadHandler<>(
                        ClientPayloadHandler::handleItemData,
                        ServerPayloadHandler::handleItemData
                )
        );*/
    }
    @SubscribeEvent
    public static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(MelodyMagic.WAND_MENU.get(), WandScreen::new);
        event.register(MelodyMagic.CHEST_ROW1.get(), ChestContainerScreen::new);
        event.register(MelodyMagic.CHEST_ROW2.get(), ChestContainerScreen::new);
        event.register(MelodyMagic.CHEST_ROW3.get(), ChestContainerScreen::new);
        event.register(MelodyMagic.CHEST_ROW4.get(), ChestContainerScreen::new);
        event.register(MelodyMagic.CHEST_ROW5.get(), ChestContainerScreen::new);
        event.register(MelodyMagic.CHEST_ROW6.get(), ChestContainerScreen::new);
    }

    @SubscribeEvent
    public static void registerScreens(EntityAttributeCreationEvent event) {
        event.put(MelodyMagic.FRIENDLY_VEX.get(), FriendlyVex.createAttributes().build());

    }

    @SubscribeEvent
    public static void registerCapabilitiesEvent(RegisterCapabilitiesEvent event)
    {
        event.registerItem(Capabilities.ItemHandler.ITEM, (itemStack, context) ->new ComponentItemHandler(itemStack, DataComponents.CONTAINER, 20), MelodyMagic.COLLECTION_ITEM.get());
    }


}
