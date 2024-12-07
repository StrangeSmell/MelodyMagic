package com.strangesmell.melodymagic.api;

import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.List;

import static com.strangesmell.melodymagic.MelodyMagic.MODID;
import static com.strangesmell.melodymagic.api.Util.putResToTag;

public class ItemUtil {

    public static Boolean containItem(NonNullList<ItemStack> list, Item item) {
        for(int i = 0; i < list.size(); ++i) {
            if(list.get(i).isEmpty()) {
                continue;
            }
            if(list.get(i).is(item)) return true;
        }
        return false;
    }

    public static Boolean remove1Item(Player player, Item item) {
        Inventory inventory = player.getInventory();
        if(player.getOffhandItem().is(item)) player.getOffhandItem().shrink(1);
        for(int i = 0; i < inventory.items.size(); ++i) {
            if(inventory.items.get(i).isEmpty()) {
                continue;
            }
            if(inventory.items.get(i).is(item)) {
                inventory.items.get(i).setCount(inventory.items.get(i).getCount()-1);
                return true;
            }
        }
        return false;
    }
}
