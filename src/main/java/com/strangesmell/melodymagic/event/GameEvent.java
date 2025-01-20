package com.strangesmell.melodymagic.event;

import com.strangesmell.melodymagic.MelodyMagic;
import com.strangesmell.melodymagic.entity.FriendlyVex;
import com.strangesmell.melodymagic.item.CollectionItem;
import com.strangesmell.melodymagic.message.RecordData;
import com.strangesmell.melodymagic.message.SelectCount;
import com.strangesmell.melodymagic.playerLookControl.CameraLookControl;
import com.strangesmell.melodymagic.playerLookControl.PlayerLookControl;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.npc.WanderingTrader;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.block.TntBlock;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.level.ExplosionEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import static com.strangesmell.melodymagic.MelodyMagic.*;
import static java.lang.Math.atan2;


@EventBusSubscriber(modid = MODID, bus = EventBusSubscriber.Bus.GAME)
public class GameEvent {
    @SubscribeEvent
    public static void playerEvent(PlayerEvent.Clone event)
    {
        if(event.isWasDeath()){
            Player oldPlayer = event.getOriginal();
            Player newPlayer = event.getEntity();
            if(oldPlayer.getPersistentData().get(MODID+"sound_kinds")!=null) {
                newPlayer.getPersistentData().put(MODID+"sound_kinds",oldPlayer.getPersistentData().get(MODID+"sound_kinds"));

            }

            if(oldPlayer.getPersistentData().get(MODID+"effect_kinds")!=null) {
                newPlayer.getPersistentData().put(MODID+"effect_kinds",oldPlayer.getPersistentData().get(MODID+"effect_kinds"));

            }
            if(oldPlayer.getPersistentData().get(MODID+"subtitle_kinds")!=null) {
                newPlayer.getPersistentData().put(MODID+"subtitle_kinds",oldPlayer.getPersistentData().get(MODID+"subtitle_kinds"));

            }
        }
    }

    @SubscribeEvent
    public static void entityAge(EntityTickEvent.Pre event)
    {
        if(event.getEntity() instanceof Wolf wolf){
            if(wolf.hasData(ENTITY_AGE)){
                if( wolf.getData(ENTITY_AGE)>0){
                    wolf.setData(ENTITY_AGE,wolf.getData(ENTITY_AGE)-1);
                }else {
                    wolf.remove(Entity.RemovalReason.KILLED);
                    wolf.level().playSound(null,wolf.getOnPos(), SoundEvents.WOLF_HOWL, SoundSource.MASTER,(float) (wolf.level().random.nextFloat()*0.25),(float) (wolf.level().random.nextFloat()*0.5));

                }

            }
        }else if(event.getEntity() instanceof Player player){
            if(player.hasData(ENTITY_AGE)){
                if( player.getData(ENTITY_AGE)>0){
                    player.setData(ENTITY_AGE,player.getData(ENTITY_AGE)-1);
                }
            }
        }else if(event.getEntity() instanceof WanderingTrader wanderingTrader){
            if(wanderingTrader.hasData(ENTITY_AGE)){
                if( wanderingTrader.getData(ENTITY_AGE)>0){
                    wanderingTrader.setData(ENTITY_AGE,wanderingTrader.getData(ENTITY_AGE)-1);
                }else{
                    wanderingTrader.remove(Entity.RemovalReason.KILLED);
                }
            }
        }else if(event.getEntity() instanceof IronGolem entity){
            if(entity.hasData(ENTITY_AGE)){
                if( entity.getData(ENTITY_AGE)>0){
                    entity.setData(ENTITY_AGE,entity.getData(ENTITY_AGE)-1);
                } else entity.remove(Entity.RemovalReason.KILLED);
            }
        }else if(event.getEntity() instanceof FriendlyVex entity){
            if(entity.hasData(ENTITY_AGE)){
                if( entity.getData(ENTITY_AGE)>0){
                    entity.setData(ENTITY_AGE,entity.getData(ENTITY_AGE)-1);
                } else entity.remove(Entity.RemovalReason.KILLED);

            }
        }
    }

    @SubscribeEvent
    public static void playerLoggedInEvent(PlayerEvent.PlayerLoggedInEvent event)
    {
        if(event.getEntity().level().isClientSide) return;
        Player player = event.getEntity();
        CompoundTag compoundTag =new CompoundTag();
        compoundTag.put(MODID+"sound_kinds",player.getPersistentData().getCompound(MODID+"sound_kinds") );
        compoundTag.put(MODID+"subtitle_kinds",player.getPersistentData().getCompound(MODID+"subtitle_kinds") );
        compoundTag.put(MODID+"effect_kinds",player.getPersistentData().getCompound(MODID+"effect_kinds") );
        PacketDistributor.sendToPlayer((ServerPlayer) player,new RecordData(compoundTag));//发包给一个唱片

    }

    @SubscribeEvent
    public static void playerLoggedInEvent(PlayerEvent.Clone event)
    {
        Player old = event.getOriginal();
        Player newPlayer = event.getEntity();
        CompoundTag oldCompoundTag = old.getPersistentData();
        newPlayer.saveWithoutId(oldCompoundTag);
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void mouseScrollingEvent(InputEvent.MouseScrollingEvent event){
        ItemStack itemStack = Minecraft.getInstance().player.getItemInHand(Minecraft.getInstance().player.getUsedItemHand());
        if(itemStack.getItem() instanceof CollectionItem&&ClientModEvent.ALT.get().isDown()){
            int selectCount=0;
            CompoundTag compoundTag = new CompoundTag();
            compoundTag = itemStack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
            if(compoundTag.contains(MODID+"select_index")){
                selectCount = compoundTag.getInt(MODID+"select_index");
            }
            selectCount = selectCount - (int)event.getScrollDeltaY();
            if(selectCount<0) selectCount = selectCount + 9;
            if(selectCount>8) selectCount = selectCount - 9;

            compoundTag.putInt(MODID+"select_count",selectCount);

            itemStack.set(DataComponents.CUSTOM_DATA,CustomData.of(compoundTag));
            PacketDistributor.sendToServer(new SelectCount(selectCount));
            event.setCanceled(true);
        }

    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void rightClickEmpty(PlayerInteractEvent.RightClickEmpty event){
        if(event.getEntity().level().isClientSide){
            if(MelodyMagic.lock) {
                lock=false;
                locked_entity=null;
            }

        }

    }
/*    @SubscribeEvent
    public static void explosionEvent(ExplosionEvent.Detonate event)
    {
        for(int i =0;i<event.getAffectedBlocks().size();){
            BlockPos pos = event.getAffectedBlocks().get(i);
            if( event.getLevel().getBlockState(pos).getBlock() instanceof TntBlock){
                i++;
            }else{
                event.getAffectedBlocks().remove(i);
            }

        }

    }*/

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void rightClickBlock(PlayerInteractEvent.RightClickBlock event){

    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void clientTickEvent(ClientTickEvent.Post event) {

    }
}
