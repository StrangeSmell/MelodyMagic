package com.strangesmell.melodymagic.container;

import com.strangesmell.melodymagic.item.ContinueSoundContainerItem;
import com.strangesmell.melodymagic.item.SoundContainerItem;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class CollectionSlot extends Slot {

    public CollectionSlot(Container pFurnaceContainer, int pSlot, int pXPosition, int pYPosition) {
        super(pFurnaceContainer, pSlot, pXPosition, pYPosition);
    }

    @Override
    public boolean mayPlace(ItemStack pStack) {
        if(pStack.getItem() instanceof SoundContainerItem) return true;
        return pStack.getItem() instanceof ContinueSoundContainerItem;
    }
}
