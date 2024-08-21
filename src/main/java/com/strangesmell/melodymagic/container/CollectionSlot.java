package com.strangesmell.melodymagic.container;

import com.strangesmell.melodymagic.item.SoundContainerItem;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class CollectionSlot extends Slot {

    public CollectionSlot(Container pFurnaceContainer, int pSlot, int pXPosition, int pYPosition) {
        super(pFurnaceContainer, pSlot, pXPosition, pYPosition);
    }

    /**
     * Check if the stack is allowed to be placed in this slot, used for armor slots as well as furnace fuel.
     */
    @Override
    public boolean mayPlace(ItemStack pStack) {
        return pStack.getItem() instanceof SoundContainerItem;
    }
}
