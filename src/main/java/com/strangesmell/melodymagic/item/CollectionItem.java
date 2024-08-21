package com.strangesmell.melodymagic.item;

import com.strangesmell.melodymagic.api.SoundEffect;
import com.strangesmell.melodymagic.api.Util;
import com.strangesmell.melodymagic.container.WandMenu;
import com.strangesmell.melodymagic.message.SoundData;
import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.datafix.fixes.OptionsKeyLwjgl3Fix;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.ICapabilityProvider;
import net.neoforged.neoforge.capabilities.ItemCapability;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

import static com.strangesmell.melodymagic.MelodyMagic.MODID;
import static com.strangesmell.melodymagic.hud.SelectHud.*;


public class CollectionItem extends Item implements  MenuProvider  {

    public CollectionItem(Properties pProperties) {
        super(pProperties);
    }

    public static final ItemCapability<IItemHandler, Void> ITEMS =
            ItemCapability.createVoid(
                    // Provide a name to uniquely identify the capability.
                    ResourceLocation.fromNamespaceAndPath(MODID, "wand_item_handler"),
                    // Provide the queried type. Here, we want to look up `IItemHandler` instances.
                    IItemHandler.class);

    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        ItemStack itemStack = pPlayer.getItemInHand(pUsedHand);
        if(pLevel.isClientSide){
            //同步到服务端中
            ClientLevel clientLevel = (ClientLevel) pLevel;
            if(pPlayer.isShiftKeyDown()){
                PacketDistributor.sendToServer(new SoundData(Util.saveSoundDataToTag(subtitles,location)));
                for(SoundInstance soundInstance : subtitles){
                    Minecraft.getInstance().getSoundManager().stop(soundInstance);
                }
            }
        }else{
            ServerLevel serverLevel = (ServerLevel) pLevel;
            if(pPlayer.isShiftKeyDown()){
                pPlayer.openMenu(new SimpleMenuProvider(
                        (containerId, playerInventory, player) -> new WandMenu(containerId, playerInventory),
                        Component.translatable("wand_menu")
                ));
            }else{

                int selectCount=0;
                CompoundTag compoundTag =itemStack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
                if(!compoundTag.contains(MODID+"select_index")){
                    compoundTag.putInt(MODID+"select_index",selectCount);
                }else{
                    selectCount = compoundTag.getInt(MODID+"select_index");
                }
                ItemContainerContents itemContainerContents = itemStack.get(DataComponents.CONTAINER);
                ItemStack soundContainer = itemContainerContents.getStackInSlot(selectCount);
                if(soundContainer.getItem() instanceof SoundContainerItem){
                    List<SoundEffect> listEffect = Util.getSoundEffect(soundContainer);
                    for(int i=0;i<listEffect.size();i++){
                        listEffect.get(i).effect(pPlayer,pLevel,pUsedHand,soundContainer);
                    }
                }
            }


        }
        return InteractionResultHolder.pass(pPlayer.getItemInHand(pUsedHand));
    }


    @Override
    public Component getDisplayName() {
        return Component.translatable("wand_menu").withColor(165155);
    }

    @Nullable
    @Override
    public WandMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
        return new WandMenu(pContainerId,pPlayerInventory);
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(
                new IClientItemExtensions() {

                    @Override
                    public BlockEntityWithoutLevelRenderer getCustomRenderer() {

                        return new SoundContainerItemStackRenderer();
                    }
                }
        );
    }
}
