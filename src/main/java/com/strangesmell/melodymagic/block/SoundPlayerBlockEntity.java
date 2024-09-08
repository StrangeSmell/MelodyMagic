package com.strangesmell.melodymagic.block;

import com.google.common.annotations.VisibleForTesting;
import com.strangesmell.melodymagic.MelodyMagic;
import com.strangesmell.melodymagic.item.SoundContainerItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Clearable;
import net.minecraft.world.Container;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.JukeboxSong;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.JukeboxBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.JukeboxBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.ticks.ContainerSingleItem;

public class SoundPlayerBlockEntity extends BlockEntity implements Clearable, ContainerSingleItem.BlockContainerSingleItem{
    private ItemStack item = ItemStack.EMPTY;
    private int cooldown = 0;
    public SoundPlayerBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(MelodyMagic.SOUND_PLAYER_BLOCK_ENTITY.get(), pPos, pBlockState);
    }

    public void popOutTheItem() {
        if (this.level != null && !this.level.isClientSide) {
            BlockPos blockpos = this.getBlockPos();
            ItemStack itemstack = this.getTheItem();
            if (!itemstack.isEmpty()) {
                this.removeTheItem();
                Vec3 vec3 = Vec3.atLowerCornerWithOffset(blockpos, 0.5, 1.01, 0.5).offsetRandom(this.level.random, 0.7F);
                ItemStack itemstack1 = itemstack.copy();
                ItemEntity itementity = new ItemEntity(this.level, vec3.x(), vec3.y(), vec3.z(), itemstack1);
                itementity.setDefaultPickUpDelay();
                this.level.addFreshEntity(itementity);
            }
        }
    }
    public void deCooldown() {
        if(cooldown > 0) {
            cooldown--;
        }
    }
    public int getCooldown() {
        return this.cooldown;
    }
    public void setCooldown(int cooldown) {
        this.cooldown=cooldown;
    }
    @Override
    public ItemStack splitTheItem(int pAmount) {
        ItemStack itemstack = this.item;
        this.setTheItem(ItemStack.EMPTY);
        return itemstack;
    }

    @Override
    protected void loadAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries) {
        super.loadAdditional(pTag, pRegistries);
        if (pTag.contains("sound_container_item")) {
            this.item = ItemStack.parse(pRegistries, pTag.getCompound("sound_container_item")).orElse(ItemStack.EMPTY);
        } else {
            this.item = ItemStack.EMPTY;
        }
    }

    @Override
    protected void saveAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries) {
        super.saveAdditional(pTag, pRegistries);
        if (!this.getTheItem().isEmpty()) {
            pTag.put("sound_container_item", this.getTheItem().save(pRegistries));
        }
    }


    @Override
    public BlockEntity getContainerBlockEntity() {
        return this;
    }

    @Override
    public ItemStack getTheItem() {
        return item;
    }

    @Override
    public void setTheItem(ItemStack pItem) {
        this.item = pItem;
        boolean flag = !this.item.isEmpty();
        this.notifyItemChangedInJukebox(flag);
        //todo play sound
    }

    public static void tick(Level p_273615_, BlockPos pLevel, BlockState pPos, SoundPlayerBlockEntity soundPlayerBlockEntity) {

    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }

    @Override
    public boolean canPlaceItem(int pSlot, ItemStack pStack) {
        return pStack.getItem() instanceof SoundContainerItem && this.getItem(pSlot).isEmpty();
    }

    @Override
    public boolean canTakeItem(Container pTarget, int pSlot, ItemStack pStack) {
        return pTarget.hasAnyMatching(ItemStack::isEmpty);
    }


    private void notifyItemChangedInJukebox(boolean p_350455_) {
        if (this.level != null && this.level.getBlockState(this.getBlockPos()) == this.getBlockState()) {
            this.level.setBlock(this.getBlockPos(), this.getBlockState().setValue(SoundPlayerBlock.HAS_RECORD, Boolean.valueOf(p_350455_)), 2);
            this.level.gameEvent(GameEvent.BLOCK_CHANGE, this.getBlockPos(), GameEvent.Context.of(this.getBlockState()));
        }
    }
}
