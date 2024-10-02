package com.strangesmell.melodymagic.container;

import com.strangesmell.melodymagic.MelodyMagic;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.ItemContainerContents;

import static com.strangesmell.melodymagic.MelodyMagic.MODID;
import static net.minecraft.world.item.component.ItemContainerContents.EMPTY;

public class ChestConatiner extends AbstractContainerMenu {
    private static final int SLOTS_PER_ROW = 9;
    private final SimpleContainer container;
    private final int containerRows;
    private Player player;
    private ItemStack itemStack ;

    private ChestConatiner(MenuType<?> pType, int pContainerId, Inventory pPlayerInventory, int pRows,SimpleContainer pContainer) {
        this(pType, pContainerId, pPlayerInventory, pContainer, pRows);
    }

    public static ChestConatiner oneRow(int pContainerId, Inventory pPlayerInventory) {
        return new ChestConatiner(MelodyMagic.CHEST_ROW1.get(), pContainerId, pPlayerInventory, 1, new SimpleContainer(9 ));
    }

    public static ChestConatiner twoRows(int pContainerId, Inventory pPlayerInventory) {
        SimpleContainer pContainer = new SimpleContainer(9 * 2);
        Player player = pPlayerInventory.player;
        int selectCount = 0;
        ItemStack wand = player.getItemInHand(player.getUsedItemHand());
        CompoundTag compoundTag =wand.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        if (!compoundTag.contains(MODID + "select_index")) {
            compoundTag.putInt(MODID + "select_index", selectCount);
        } else {
            selectCount = compoundTag.getInt(MODID + "select_index");
        }
        ItemContainerContents itemContainerContents = wand.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY);
        ItemStack itemStack = itemContainerContents.getStackInSlot(selectCount);
        itemStack.getOrDefault(DataComponents.CONTAINER,EMPTY).copyInto(pContainer.getItems());
        return new ChestConatiner(MelodyMagic.CHEST_ROW2.get(), pContainerId, pPlayerInventory, 2,pContainer);
    }

    public static ChestConatiner threeRows(int pContainerId, Inventory pPlayerInventory) {
        SimpleContainer pContainer = new SimpleContainer(9 * 3);
        Player player = pPlayerInventory.player;
        int selectCount = 0;
        ItemStack wand = player.getItemInHand(player.getUsedItemHand());
        CompoundTag compoundTag =wand.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        if (!compoundTag.contains(MODID + "select_index")) {
            compoundTag.putInt(MODID + "select_index", selectCount);
        } else {
            selectCount = compoundTag.getInt(MODID + "select_index");
        }
        ItemContainerContents itemContainerContents = wand.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY);
        ItemStack itemStack = itemContainerContents.getStackInSlot(selectCount);
        itemStack.getOrDefault(DataComponents.CONTAINER,EMPTY).copyInto(pContainer.getItems());
        return new ChestConatiner(MelodyMagic.CHEST_ROW3.get(), pContainerId, pPlayerInventory, 3,pContainer);
    }

    public static ChestConatiner fourRows(int pContainerId, Inventory pPlayerInventory) {
        SimpleContainer pContainer = new SimpleContainer(9 * 4);
        Player player = pPlayerInventory.player;
        int selectCount = 0;
        ItemStack wand = player.getItemInHand(player.getUsedItemHand());
        CompoundTag compoundTag =wand.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        if (!compoundTag.contains(MODID + "select_index")) {
            compoundTag.putInt(MODID + "select_index", selectCount);
        } else {
            selectCount = compoundTag.getInt(MODID + "select_index");
        }
        ItemContainerContents itemContainerContents = wand.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY);
        ItemStack itemStack = itemContainerContents.getStackInSlot(selectCount);
        itemStack.getOrDefault(DataComponents.CONTAINER,EMPTY).copyInto(pContainer.getItems());
        return new ChestConatiner(MelodyMagic.CHEST_ROW4.get(), pContainerId, pPlayerInventory, 4,pContainer);
    }

    public static ChestConatiner fiveRows(int pContainerId, Inventory pPlayerInventory) {
        SimpleContainer pContainer = new SimpleContainer(9 * 5);
        Player player = pPlayerInventory.player;
        int selectCount = 0;
        ItemStack wand = player.getItemInHand(player.getUsedItemHand());
        CompoundTag compoundTag =wand.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        if (!compoundTag.contains(MODID + "select_index")) {
            compoundTag.putInt(MODID + "select_index", selectCount);
        } else {
            selectCount = compoundTag.getInt(MODID + "select_index");
        }
        ItemContainerContents itemContainerContents = wand.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY);
        ItemStack itemStack = itemContainerContents.getStackInSlot(selectCount);
        itemStack.getOrDefault(DataComponents.CONTAINER,EMPTY).copyInto(pContainer.getItems());
        return new ChestConatiner(MelodyMagic.CHEST_ROW5.get(), pContainerId, pPlayerInventory, 5,pContainer);
    }

    public static ChestConatiner sixRows(int pContainerId, Inventory pPlayerInventory) {
        return new ChestConatiner(MelodyMagic.CHEST_ROW6.get(), pContainerId, pPlayerInventory, 6,new SimpleContainer(9 ));
    }


    public ChestConatiner(MenuType<?> pType, int pContainerId, Inventory pPlayerInventory, SimpleContainer pContainer, int pRows) {
        super(pType, pContainerId);
        player = pPlayerInventory.player;
        int selectCount = 0;
        ItemStack wand = player.getItemInHand(player.getUsedItemHand());
        CompoundTag compoundTag =wand.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        if (!compoundTag.contains(MODID + "select_index")) {
            compoundTag.putInt(MODID + "select_index", selectCount);
        } else {
            selectCount = compoundTag.getInt(MODID + "select_index");
        }

        itemStack = wand.get(DataComponents.CONTAINER).getStackInSlot(selectCount);
        itemStack.getOrDefault(DataComponents.CONTAINER,EMPTY).copyInto(pContainer.getItems());
        this.container = pContainer;
        this.containerRows = pRows;

        pContainer.startOpen(player);
        int i = (this.containerRows - 4) * 18;

        for (int j = 0; j < this.containerRows; j++) {
            for (int k = 0; k < 9; k++) {
                this.addSlot(new Slot(pContainer, k + j * 9, 8 + k * 18, 18 + j * 18));
            }
        }

        for (int l = 0; l < 3; l++) {
            for (int j1 = 0; j1 < 9; j1++) {
                this.addSlot(new Slot(pPlayerInventory, j1 + l * 9 + 9, 8 + j1 * 18, 103 + l * 18 + i));
            }
        }

        for (int i1 = 0; i1 < 9; i1++) {
            this.addSlot(new Slot(pPlayerInventory, i1, 8 + i1 * 18, 161 + i));
        }

    }

    public Container getContainer() {
        return this.container;
    }

    public int getRowCount() {
        return this.containerRows;
    }

    @Override
    public void removed(Player pPlayer) {
        itemStack.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(container.getItems()));
        ItemStack wand = player.getItemInHand(player.getUsedItemHand());
        NonNullList<ItemStack> items = NonNullList.create();
        int selectCount = 0;
        CompoundTag compoundTag =wand.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        if (!compoundTag.contains(MODID + "select_index")) {
            compoundTag.putInt(MODID + "select_index", selectCount);
        } else {
            selectCount = compoundTag.getInt(MODID + "select_index");
        }
        for(int i= 0;i<wand.getOrDefault(DataComponents.CONTAINER,EMPTY).getSlots();i++){
            items.add(wand.getOrDefault(DataComponents.CONTAINER,EMPTY).getStackInSlot(i));
        }
        items.set(selectCount,itemStack);
        wand.set(DataComponents.CONTAINER,ItemContainerContents.fromItems(items));
        super.removed(pPlayer);
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        return this.container.stillValid(pPlayer);
    }

    @Override
    public ItemStack quickMoveStack(Player pPlayer, int pIndex) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(pIndex);
        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();
            if (pIndex < this.containerRows * 9) {
                if (!this.moveItemStackTo(itemstack1, this.containerRows * 9, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(itemstack1, 0, this.containerRows * 9, false)) {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }

        return itemstack;
    }
}
