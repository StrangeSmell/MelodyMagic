package com.strangesmell.melodymagic.block;

import com.mojang.serialization.MapCodec;
import com.strangesmell.melodymagic.MelodyMagic;
import com.strangesmell.melodymagic.item.SoundContainerItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.stats.Stats;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.JukeboxPlayable;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;

import javax.annotation.Nullable;

import static com.strangesmell.melodymagic.api.Util.playSoundFromItem;

public class SoundPlayerBlock extends BaseEntityBlock {
    public static final MapCodec<SoundPlayerBlock> CODEC = simpleCodec(SoundPlayerBlock::new);
    public static final BooleanProperty HAS_RECORD = BlockStateProperties.HAS_RECORD;
    public static final BooleanProperty TRIGGERED = BlockStateProperties.TRIGGERED;

    @Override
    public MapCodec<SoundPlayerBlock> codec() {
        return CODEC;
    }

    public SoundPlayerBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(HAS_RECORD, Boolean.valueOf(false)).setValue(TRIGGERED, Boolean.valueOf(false)));
    }

    @Override
    protected void neighborChanged(BlockState pState, Level pLevel, BlockPos pPos, Block pBlock, BlockPos pFromPos, boolean pIsMoving) {
        boolean flag = pLevel.hasNeighborSignal(pPos);
        boolean flag1 = pState.getValue(TRIGGERED);
        if (flag && !flag1) {
            pLevel.scheduleTick(pPos, this, 4);
            pLevel.setBlock(pPos, pState.setValue(TRIGGERED, Boolean.valueOf(true)), 2);
        } else if (!flag && flag1) {
            pLevel.setBlock(pPos, pState.setValue(TRIGGERED, Boolean.valueOf(false)), 2);
        }
    }

    @Override
    protected void tick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRandom) {
        SoundPlayerBlockEntity soundPlayerBlockEntity = pLevel.getBlockEntity(pPos, MelodyMagic.SOUND_PLAYER_BLOCK_ENTITY.get()).orElse(null);
        ItemStack itemStack = soundPlayerBlockEntity.getTheItem();
        if(itemStack.isEmpty()){
        }else {
            playSoundFromItem(soundPlayerBlockEntity.getTheItem(),pLevel,pPos.getX(),pPos.getY(),pPos.getZ());
        }
    }


    @Override
    public void setPlacedBy(Level pLevel, BlockPos pPos, BlockState pState, @Nullable LivingEntity pPlacer, ItemStack pStack) {
        super.setPlacedBy(pLevel, pPos, pState, pPlacer, pStack);
        CustomData customdata = pStack.getOrDefault(DataComponents.BLOCK_ENTITY_DATA, CustomData.EMPTY);
        if (customdata.contains("sound_container_item")) {
            pLevel.setBlock(pPos, pState.setValue(HAS_RECORD, Boolean.valueOf(true)), 2);
        }
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, BlockHitResult pHitResult) {
        if (pState.getValue(HAS_RECORD) && pLevel.getBlockEntity(pPos) instanceof SoundPlayerBlockEntity soundPlayerBlockEntity) {
            soundPlayerBlockEntity.popOutTheItem();
            return InteractionResult.sidedSuccess(pLevel.isClientSide);
        } else {
            return InteractionResult.PASS;
        }
    }

    @Override
    protected ItemInteractionResult useItemOn(
            ItemStack p_350348_, BlockState p_351033_, Level p_350995_, BlockPos p_350838_, Player p_350684_, InteractionHand p_350461_, BlockHitResult p_351025_
    ) {
        if (p_351033_.getValue(HAS_RECORD)) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        } else {
            ItemStack itemstack = p_350684_.getItemInHand(p_350461_);
            ItemInteractionResult iteminteractionresult = tryInsertIntoJukebox(p_350995_, p_350838_, itemstack, p_350684_);
            return !iteminteractionresult.consumesAction() ? ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION : iteminteractionresult;
        }
    }

    @Override
    protected void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
        if (!pState.is(pNewState.getBlock())) {
            if (pLevel.getBlockEntity(pPos) instanceof SoundPlayerBlockEntity soundPlayerBlockEntity) {
                soundPlayerBlockEntity.popOutTheItem();
            }

            super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
        }
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new SoundPlayerBlockEntity(pPos, pState);
    }

    /**
     * The type of render function called. MODEL for mixed tesr and static model, MODELBLOCK_ANIMATED for TESR-only, LIQUID for vanilla liquids, INVISIBLE to skip all rendering
     * @deprecated call via {@link net.minecraft.world.level.block.state.BlockBehaviour.BlockStateBase#getRenderShape} whenever possible. Implementing/overriding is fine.
     */
    @Override
    protected RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(HAS_RECORD).add(TRIGGERED);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        return pState.getValue(HAS_RECORD) ? createTickerHelper(pBlockEntityType, MelodyMagic.SOUND_PLAYER_BLOCK_ENTITY.get(), SoundPlayerBlockEntity::tick) : null;
    }


    public static ItemInteractionResult tryInsertIntoJukebox(Level p_350560_, BlockPos p_350567_, ItemStack p_350531_, Player p_350807_) {
        if(p_350531_.getItem() instanceof SoundContainerItem){
            BlockState blockstate = p_350560_.getBlockState(p_350567_);
            if (blockstate.is(MelodyMagic.SOUND_PLAYER_BLOCK) && !blockstate.getValue(SoundPlayerBlock.HAS_RECORD)) {
                if (!p_350560_.isClientSide) {
                    ItemStack itemstack = p_350531_.consumeAndReturn(1, p_350807_);
                    if (p_350560_.getBlockEntity(p_350567_) instanceof SoundPlayerBlockEntity soundPlayerBlockEntity) {
                        soundPlayerBlockEntity.setTheItem(itemstack);
                        p_350560_.gameEvent(GameEvent.BLOCK_CHANGE, p_350567_, GameEvent.Context.of(p_350807_, blockstate));
                    }
                }
                return ItemInteractionResult.sidedSuccess(p_350560_.isClientSide);
            } else {
                return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
            }
        }else{
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
    }
}