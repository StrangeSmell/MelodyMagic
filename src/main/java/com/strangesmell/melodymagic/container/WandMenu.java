package com.strangesmell.melodymagic.container;

import com.strangesmell.melodymagic.MelodyMagic;
import com.strangesmell.melodymagic.item.CollectionItem;
import com.strangesmell.melodymagic.item.SoundContainerItem;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;

import javax.annotation.Nullable;

import static net.minecraft.world.item.component.ItemContainerContents.EMPTY;


public class WandMenu extends AbstractContainerMenu {
    private final SimpleContainer container;
    private ItemStack itemStack ;

    private Player player;
    public WandMenu(int pContainerId, Inventory pPlayerInventory) {
        this(pContainerId, pPlayerInventory, new SimpleContainer(9));

    }


    public WandMenu(int pContainerId, Inventory pPlayerInventory, SimpleContainer pContainer) {
        super(MelodyMagic.WAND_MENU.get(), pContainerId);
        player = pPlayerInventory.player;
        itemStack = player.getItemInHand(player.getUsedItemHand());
        itemStack.getOrDefault(DataComponents.CONTAINER,EMPTY).copyInto(pContainer.getItems());
        this.container = pContainer;
        //加入格子
        int r = 50;
        for(int i = 0;i<9;i++){
            if(i==3){
                this.addSlot(new CollectionSlot(pContainer,i,(int)(80+Math.sin(2*Math.PI*i/9)*r),(int)(40-Math.cos(2*Math.PI*i/9)*r+0.00001)));
            }else{
                this.addSlot(new CollectionSlot(pContainer,i,(int)(80+Math.sin(2*Math.PI*i/9)*r),(int)(40-Math.cos(2*Math.PI*i/9)*r)));

            }
        }

        for (int k = 0; k < 3; k++) {
            for (int i1 = 0; i1 < 9; i1++) {
                this.addSlot(new Slot(pPlayerInventory, i1 + k * 9 + 9, 8 + i1 * 18, 84+40 + k * 18));
            }
        }

        for (int l = 0; l < 9; l++) {
            this.addSlot(new Slot(pPlayerInventory, l, 8 + l * 18, 142+40));
        }
    }



    @Override
    public ItemStack quickMoveStack(Player pPlayer, int pIndex) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(pIndex);
        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();
            if (pIndex < 9) {
                if (!this.moveItemStackTo(itemstack1, 9, 45, true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(itemstack1, 0, 9, false)) {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (itemstack1.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(pPlayer, itemstack1);
        }
        return itemstack;
    }
    @Override
    public void removed(Player pPlayer) {
        itemStack.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(container.getItems()));
        super.removed(pPlayer);
    }
    @Override
    public boolean stillValid(Player pPlayer) {
        return container.stillValid(pPlayer);
    }

    @Override
    public void broadcastChanges() {
        super.broadcastChanges();
    }

    /**
     * Puts an ItemStack in a slot.
     */
/*    public void setItem(int pSlotId, int pStateId, ItemStack pStack) {
        this.getSlot(pSlotId).set(pStack);
        this.stateId = pStateId;
    }*/


    public boolean canDragTo(Slot pSlot) {
        return  (this.getCarried().getItem() instanceof SoundContainerItem);
    }

}
