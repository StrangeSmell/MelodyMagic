package com.strangesmell.melodymagic.item;

import com.google.common.collect.Lists;
import com.strangesmell.melodymagic.api.ItemUtil;
import com.strangesmell.melodymagic.api.SoundEffect;
import com.strangesmell.melodymagic.api.Util;
import com.strangesmell.melodymagic.container.WandMenu;
import com.strangesmell.melodymagic.hud.SelectHud;
import com.strangesmell.melodymagic.message.SoundData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.*;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Consumer;

import static com.strangesmell.melodymagic.MelodyMagic.MODID;
import static com.strangesmell.melodymagic.hud.SelectHud.location;


public class CollectionItem extends Item implements MenuProvider {
    public CollectionItem(Properties pProperties) {
        super(pProperties);
    }

    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        ItemStack itemStack = pPlayer.getItemInHand(pUsedHand);
        if (!pLevel.isClientSide) {
            if (pPlayer.isShiftKeyDown()) {
                pPlayer.openMenu(new SimpleMenuProvider(
                        (containerId, playerInventory, player) -> new WandMenu(containerId, playerInventory),
                        Component.translatable("wand_menu")
                ));
                return InteractionResultHolder.success(pPlayer.getItemInHand(pUsedHand));
            }

        } else {
            if (pPlayer.isShiftKeyDown()) {
                return InteractionResultHolder.success(pPlayer.getItemInHand(pUsedHand));

            }
        }
        int selectCount = 0;
        CompoundTag compoundTag = itemStack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        if (!compoundTag.contains(MODID + "select_index")) {
            compoundTag.putInt(MODID + "select_index", selectCount);
        } else {
            selectCount = compoundTag.getInt(MODID + "select_index");
        }
        //使用

        ItemContainerContents itemContainerContents = itemStack.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY);
        if (itemContainerContents.getSlots() <= selectCount)
            return InteractionResultHolder.pass(pPlayer.getItemInHand(pUsedHand));
        ItemStack soundContainer = itemContainerContents.getStackInSlot(selectCount);
        if (soundContainer.getItem() instanceof SoundContainerItem) {
            List<SoundEffect> listEffect = Util.getSoundEffect(soundContainer);
            if (listEffect.isEmpty() && pLevel.isClientSide) {
                pPlayer.sendSystemMessage(Component.translatable(MODID + ".sound_container.empty"));
            }
            for (int i = 0; i < listEffect.size(); i++) {
                listEffect.get(i).effect(pPlayer, pLevel, pUsedHand, soundContainer);
                for(int j=0;j<5;j++){
                    pLevel.addParticle(
                            ParticleTypes.NOTE, pPlayer.getRandomX(1.5) , pPlayer.getRandomY(), pPlayer.getRandomZ(1.5) , listEffect.size() / 10.0, 0.0, 0.0
                    );
                }

            }
            pPlayer.getCooldowns().addCooldown(this, soundContainer.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag().getInt("cooldown"));
        }else if(soundContainer.getItem() instanceof ContinueSoundContainerItem){
            //持续施法
            pPlayer.startUsingItem(pUsedHand);
            initLists(itemStack);
            return InteractionResultHolder.consume(itemStack);
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
        return new WandMenu(pContainerId, pPlayerInventory);
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

    @Override
    public int getUseDuration(ItemStack pStack, LivingEntity p_345962_) {
        return 72000;
    }

    public void initLists(ItemStack itemStack) {


    }

    @Override
    public void onUseTick(Level pLevel, LivingEntity pLivingEntity, ItemStack itemStack, int pRemainingUseDuration) {
        int useTime = getUseDuration(itemStack, pLivingEntity) - pRemainingUseDuration;
        int selectCount = 0;
        CompoundTag compoundTag = itemStack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        if (!compoundTag.contains(MODID + "select_index")) {
            compoundTag.putInt(MODID + "select_index", selectCount);
        } else {
            selectCount = compoundTag.getInt(MODID + "select_index");
        }
        ItemContainerContents itemContainerContents = itemStack.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY);
        if (itemContainerContents.getSlots() <= selectCount) return;
        ItemStack soundContainer = itemContainerContents.getStackInSlot(selectCount);
        if (soundContainer.getItem() instanceof ContinueSoundContainerItem) {
            List<SoundEvent> subtitles = Lists.newArrayList();
            List<Float> volume = Lists.newArrayList();
            List<Float> peach = Lists.newArrayList();
            List<List<Double>> location = Lists.newArrayList();
            List<Integer> time = Lists.newArrayList();
            Util.loadSoundDataFromTag(soundContainer.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag(), subtitles, location, volume, peach, time);
            for (int i = 0; i < time.size(); i++) {
                if (useTime == time.get(i)) {
                    pLevel.playSound((Player) pLivingEntity,pLivingEntity.getX()+ location.get(i).get(0),pLivingEntity.getY()+location.get(i).get(1),pLivingEntity.getZ()+ location.get(i).get(2), subtitles.get(i), SoundSource.MASTER,volume.get(i),peach.get(i));
                    for(int j=0;j<5;j++){
                        pLevel.addParticle(
                                ParticleTypes.NOTE, pLivingEntity.getRandomX(1.5) , pLivingEntity.getRandomY(), pLivingEntity.getRandomZ(1.5) , 0.1, 0.0, 0.0
                        );
                    }
                    if(getUseDuration(soundContainer,pLivingEntity)-pRemainingUseDuration==time.get(time.size()-1)){
                        releaseUsing(soundContainer,pLevel,pLivingEntity,0);
                    }
                }
            }

        }


    }

    @Override
    public void releaseUsing(ItemStack soundContainer, Level pLevel, LivingEntity pEntityLiving, int pTimeLeft) {
        if(pTimeLeft!=0)return;
        if(pEntityLiving instanceof Player player){
            if (soundContainer.getItem() instanceof ContinueSoundContainerItem) {
                List<SoundEffect> listEffect = Util.getSoundEffect(soundContainer.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag());
                if (listEffect.isEmpty() && pLevel.isClientSide) {
                    player.sendSystemMessage(Component.translatable(MODID + ".sound_container.empty"));
                }
                for (int i = 0; i < listEffect.size(); i++) {
                    listEffect.get(i).effect(player, pLevel, player.getUsedItemHand(), soundContainer);
                }
                player.getCooldowns().addCooldown(this, soundContainer.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag().getInt("cooldown"));

            }
        }

    }
}
