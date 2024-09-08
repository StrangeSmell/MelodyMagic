package com.strangesmell.melodymagic.api;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.*;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.network.codec.ByteBufCodecs;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.strangesmell.melodymagic.MelodyMagic.*;

public class MMCriterionTrigger extends SimpleCriterionTrigger<MMCriterionTrigger.MMTriggerInstance>   {
    // This method is unique for each trigger and is as such not a method to override
    //ItemSubPredicate.Type<ItemCustomDataPredicate>
    public void trigger(ServerPlayer player, ItemStack stack) {
        this.trigger(player,
                // The condition checker method within the SimpleCriterionTrigger.SimpleInstance subclass
                triggerInstance -> triggerInstance.matches(stack)
        );
    }

    @Override
    public Codec<MMTriggerInstance> codec() {
        return MMCriterionTrigger.MMTriggerInstance.CODEC;
    }

    public record MMTriggerInstance(Optional<ContextAwarePredicate> player, ItemCustomDataPredicate customDataPredicate )
            implements SimpleCriterionTrigger.SimpleInstance {

        public static final Codec<MMTriggerInstance> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(MMTriggerInstance::player),
                ItemCustomDataPredicate.CODEC.fieldOf("customDataPredicate").forGetter(MMTriggerInstance::customDataPredicate)
        ).apply(instance,MMTriggerInstance::new));

        public static Criterion<MMTriggerInstance> instance( ItemCustomDataPredicate customDataPredicate) {
            return MM_TRIGGER.get().createCriterion(new MMTriggerInstance(Optional.empty(), customDataPredicate));
        }

        public boolean matches(ItemStack stack) {
            // Since ItemPredicate matches a stack, we use a stack as the input here.
            //如果触发的Itemstack和在datagen放入的item相同，则成功触发
            return containKey(stack,customDataPredicate);
        }
    }

    public static boolean containKey(ItemStack itemStack,ItemCustomDataPredicate customDataPredicate) {

        CompoundTag compoundTag = itemStack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        CompoundTag tag =(CompoundTag) compoundTag.get(MODID+"sound2key");
        if(tag==null) return false;
        int size = tag.getInt("effect_size");
        List<String> listEffect = new ArrayList<>();
        if(size==0) return false;
        for(int i=0;i<size;i++){
            listEffect.add(tag.getString("index"+i));
        }
        return listEffect.contains(customDataPredicate.value().tag().getString("adv"));
    }
}

