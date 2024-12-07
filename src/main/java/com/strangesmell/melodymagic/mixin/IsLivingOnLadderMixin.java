package com.strangesmell.melodymagic.mixin;
//record: 9.27 21:00 the first mixin of strange_smell

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.CommonHooks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

import static com.strangesmell.melodymagic.MelodyMagic.ENTITY_AGE;

@Mixin(CommonHooks.class)
public class IsLivingOnLadderMixin {

    @Inject(method = "isLivingOnLadder", at = @At("HEAD"), cancellable = true)
    private static void isLivingOnLadder(BlockState state, Level level, BlockPos pos, LivingEntity entity, CallbackInfoReturnable<Optional<BlockPos>> info) {
        boolean isSpectator = (entity instanceof Player && entity.isSpectator());
        if (isSpectator) info.setReturnValue(Optional.empty());
        if (entity instanceof Player player) {
            if (player.getData(ENTITY_AGE) > 0) {
                if (entity.horizontalCollision) {
                    info.setReturnValue(Optional.of(pos));
                }
            }
        }
    }
}
