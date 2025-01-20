package com.strangesmell.melodymagic.item;

import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;


import java.util.List;


public class Note extends Item {
    public Note(Properties properties) {
        super(properties);
    }
    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {

        CompoundTag component = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        if(component.contains("subtitle")) {
            tooltipComponents.add(Component.literal(component.getString("subtitle")).setStyle(Style.EMPTY.withColor(ChatFormatting.DARK_BLUE)));
            tooltipComponents.add(Component.translatable("tooltip.melodymagic.note.volume").append(String.valueOf(component.getFloat("volume"))).setStyle(Style.EMPTY.withColor(ChatFormatting.DARK_BLUE)));
            tooltipComponents.add(Component.translatable("tooltip.melodymagic.note.peach").append(String.valueOf(component.getFloat("peach"))).setStyle(Style.EMPTY.withColor(ChatFormatting.DARK_BLUE)));
            tooltipComponents.add(Component.translatable("tooltip.melodymagic.note.location").append(" "+component.getDouble("x")+" "+component.getDouble("y")+" "+component.getDouble("z")).setStyle(Style.EMPTY.withColor(ChatFormatting.DARK_BLUE)));

        }
    }

}
