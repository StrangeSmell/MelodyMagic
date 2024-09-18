package com.strangesmell.melodymagic.item;

import com.strangesmell.melodymagic.api.SoundEffect;
import com.strangesmell.melodymagic.api.Util;
import com.strangesmell.melodymagic.container.WandMenu;
import com.strangesmell.melodymagic.message.SoundData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.*;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Random;
import java.util.function.Consumer;

import static com.strangesmell.melodymagic.MelodyMagic.MODID;
import static com.strangesmell.melodymagic.api.Util.getNumOfUntranslate;
import static com.strangesmell.melodymagic.hud.SelectHud.*;


public class CollectionItem extends Item implements  MenuProvider  {

    public CollectionItem(Properties pProperties) {
        super(pProperties);
    }

    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        ItemStack itemStack = pPlayer.getItemInHand(pUsedHand);
        if(!pLevel.isClientSide){
            if(pPlayer.isShiftKeyDown()){
                pPlayer.openMenu(new SimpleMenuProvider(
                        (containerId, playerInventory, player) -> new WandMenu(containerId, playerInventory),
                        Component.translatable("wand_menu")
                ));
                return InteractionResultHolder.success(pPlayer.getItemInHand(pUsedHand));
            }

        }
        int selectCount=0;
        CompoundTag compoundTag =itemStack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        if(!compoundTag.contains(MODID+"select_index")){
            compoundTag.putInt(MODID+"select_index",selectCount);
        }else{
            selectCount = compoundTag.getInt(MODID+"select_index");
        }
        //使用

        ItemContainerContents itemContainerContents = itemStack.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY);
        if(itemContainerContents.getSlots()<= selectCount) return InteractionResultHolder.pass(pPlayer.getItemInHand(pUsedHand));
        ItemStack soundContainer = itemContainerContents.getStackInSlot(selectCount);
        if(soundContainer.getItem() instanceof SoundContainerItem){
            List<SoundEffect> listEffect = Util.getSoundEffect(soundContainer);
            for(int i=0;i<listEffect.size();i++){
                listEffect.get(i).effect(pPlayer,pLevel,pUsedHand,soundContainer);
            }
            pPlayer.getCooldowns().addCooldown(this, soundContainer.getOrDefault(DataComponents.CUSTOM_DATA,CustomData.EMPTY).copyTag().getInt("cooldown"));
        }
        return InteractionResultHolder.success(pPlayer.getItemInHand(pUsedHand));
    }


    @Override
    public Component getDisplayName() {
        return Component.empty();
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
/*                    int num= getNumOfUntranslate(soundContainer);
                    Random random = new Random();
                    int id = random.nextInt(0,BuiltInRegistries.MOB_EFFECT.size());
                    for(int i=0;i<num;i++){
                        pPlayer.addEffect(new MobEffectInstance(BuiltInRegistries.MOB_EFFECT.getHolder(id).get(), random.nextInt(50,400), 1));

                    }*/