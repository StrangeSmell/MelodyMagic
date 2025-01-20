package com.strangesmell.melodymagic.block;

import com.mojang.serialization.MapCodec;
import com.strangesmell.melodymagic.MelodyMagic;
import com.strangesmell.melodymagic.container.WandMenu;
import com.strangesmell.melodymagic.item.ContinueSoundContainerItem;
import com.strangesmell.melodymagic.item.SoundContainer;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.*;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;

public class CompositionWorkbenchBlock extends BaseEntityBlock {
    public static final MapCodec<CompositionWorkbenchBlock> CODEC = simpleCodec(CompositionWorkbenchBlock::new);
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    public static final BooleanProperty HAS_BOOK = BlockStateProperties.HAS_BOOK;
    public static final VoxelShape SHAPE_BASE = Block.box(1.0, 0.0, 1.0, 15.0, 2.0, 15.0);
    public static final VoxelShape SHAPE_POST = Block.box(4.0, 2.0, 4.0, 12.0, 14.0, 12.0);
    public static final VoxelShape SHAPE_COMMON = Shapes.or(SHAPE_BASE, SHAPE_POST);
    public static final VoxelShape SHAPE_TOP_PLATE = Block.box(0.0, 15.0, 0.0, 16.0, 15.0, 16.0);
    public static final VoxelShape SHAPE_COLLISION = Shapes.or(SHAPE_COMMON, SHAPE_TOP_PLATE);
    public static final VoxelShape SHAPE_WEST = Shapes.or(
            Block.box(1.0, 10.0, 0.0, 5.333333, 14.0, 16.0),
            Block.box(5.333333, 12.0, 0.0, 9.666667, 16.0, 16.0),
            Block.box(9.666667, 14.0, 0.0, 14.0, 18.0, 16.0),
            SHAPE_COMMON
    );
    public static final VoxelShape SHAPE_NORTH = Shapes.or(
            Block.box(0.0, 10.0, 1.0, 16.0, 14.0, 5.333333),
            Block.box(0.0, 12.0, 5.333333, 16.0, 16.0, 9.666667),
            Block.box(0.0, 14.0, 9.666667, 16.0, 18.0, 14.0),
            SHAPE_COMMON
    );
    public static final VoxelShape SHAPE_EAST = Shapes.or(
            Block.box(10.666667, 10.0, 0.0, 15.0, 14.0, 16.0),
            Block.box(6.333333, 12.0, 0.0, 10.666667, 16.0, 16.0),
            Block.box(2.0, 14.0, 0.0, 6.333333, 18.0, 16.0),
            SHAPE_COMMON
    );
    public static final VoxelShape SHAPE_SOUTH = Shapes.or(
            Block.box(0.0, 10.0, 10.666667, 16.0, 14.0, 15.0),
            Block.box(0.0, 12.0, 6.333333, 16.0, 16.0, 10.666667),
            Block.box(0.0, 14.0, 2.0, 16.0, 18.0, 6.333333),
            SHAPE_COMMON
    );
    private static final int PAGE_CHANGE_IMPULSE_TICKS = 2;

    @Override
    public MapCodec<CompositionWorkbenchBlock> codec() {
        return CODEC;
    }

    public CompositionWorkbenchBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(
                this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(POWERED, Boolean.valueOf(false)).setValue(HAS_BOOK, Boolean.valueOf(false))
        );
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    protected VoxelShape getOcclusionShape(BlockState state, BlockGetter level, BlockPos pos) {
        return SHAPE_COMMON;
    }

    @Override
    protected boolean useShapeForLightOcclusion(BlockState state) {
        return true;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Level level = context.getLevel();
        ItemStack itemstack = context.getItemInHand();
        Player player = context.getPlayer();
        boolean flag = false;
        if (!level.isClientSide && player != null && player.canUseGameMasterBlocks()) {
            CustomData customdata = itemstack.getOrDefault(DataComponents.BLOCK_ENTITY_DATA, CustomData.EMPTY);
            if (customdata.contains("Book")) {
                flag = true;
            }
        }

        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite()).setValue(HAS_BOOK, Boolean.valueOf(flag));
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE_COLLISION;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        switch ((Direction)state.getValue(FACING)) {
            case NORTH:
                return SHAPE_NORTH;
            case SOUTH:
                return SHAPE_SOUTH;
            case EAST:
                return SHAPE_EAST;
            case WEST:
                return SHAPE_WEST;
            default:
                return SHAPE_COMMON;
        }
    }


    @Override
    protected BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    protected BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, POWERED, HAS_BOOK);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new CompositionWorkbenchBlockEntity(pos, state);
    }

    public static boolean tryPlaceBook(@Nullable LivingEntity p_347473_, Level p_270604_, BlockPos p_270276_, BlockState p_270445_, ItemStack p_270458_) {
        if (!p_270445_.getValue(HAS_BOOK)) {
            if (!p_270604_.isClientSide) {
                placeBook(p_347473_, p_270604_, p_270276_, p_270445_, p_270458_);
            }

            return true;
        } else {
            return false;
        }
    }
    //todo
    private static void placeBook(@Nullable LivingEntity p_347484_, Level p_270065_, BlockPos p_270155_, BlockState p_270753_, ItemStack p_270173_) {
        if (p_270065_.getBlockEntity(p_270155_) instanceof CompositionWorkbenchBlockEntity compositionWorkbenchBlockEntity) {
            compositionWorkbenchBlockEntity.setBook(p_270173_.consumeAndReturn(1, p_347484_));
            resetBookState(p_347484_, p_270065_, p_270155_, p_270753_, true);
            p_270065_.playSound(null, p_270155_, SoundEvents.BOOK_PUT, SoundSource.BLOCKS, 1.0F, 1.0F);
        }
    }

    public static void resetBookState(@Nullable Entity entity, Level level, BlockPos pos, BlockState state, boolean hasBook) {
        BlockState blockstate = state.setValue(POWERED, Boolean.valueOf(false)).setValue(HAS_BOOK, Boolean.valueOf(hasBook));
        level.setBlock(pos, blockstate, 3);
        level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(entity, blockstate));
        updateBelow(level, pos, state);
    }

    public static void signalPageChange(Level level, BlockPos pos, BlockState state) {
        changePowered(level, pos, state, true);
        level.scheduleTick(pos, state.getBlock(), 2);
        level.levelEvent(1043, pos, 0);
    }

    private static void changePowered(Level level, BlockPos pos, BlockState state, boolean powered) {
        level.setBlock(pos, state.setValue(POWERED, Boolean.valueOf(powered)), 3);
        updateBelow(level, pos, state);
    }

    private static void updateBelow(Level level, BlockPos pos, BlockState state) {
        level.updateNeighborsAt(pos.below(), state.getBlock());
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        changePowered(level, pos, state, false);
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            if (state.getValue(HAS_BOOK)) {
                this.popBook(state, level, pos);
            }

            super.onRemove(state, level, pos, newState, isMoving);
            if (state.getValue(POWERED)) {
                level.updateNeighborsAt(pos.below(), this);
            }
        }
    }

    private void popBook(BlockState state, Level level, BlockPos pos) {
        if (level.getBlockEntity(pos) instanceof LecternBlockEntity lecternblockentity) {
            Direction direction = state.getValue(FACING);
            ItemStack itemstack = lecternblockentity.getBook().copy();
            float f = 0.25F * (float)direction.getStepX();
            float f1 = 0.25F * (float)direction.getStepZ();
            ItemEntity itementity = new ItemEntity(
                    level, (double)pos.getX() + 0.5 + (double)f, (double)(pos.getY() + 1), (double)pos.getZ() + 0.5 + (double)f1, itemstack
            );
            itementity.setDefaultPickUpDelay();
            level.addFreshEntity(itementity);
            lecternblockentity.clearContent();
        }
    }


    @Override
    protected boolean isSignalSource(BlockState state) {
        return true;
    }

    /**
     * Returns the signal this block emits in the given direction.
     *
     * <p>
     * NOTE: directions in redstone signal related methods are backwards, so this method
     * checks for the signal emitted in the <i>opposite</i> direction of the one given.
     *
     * @deprecated call via {@link net.minecraft.world.level.block.state.BlockBehaviour.BlockStateBase#getSignal} whenever possible. Implementing/overriding is fine.
     */
    @Override
    protected int getSignal(BlockState blockState, BlockGetter blockAccess, BlockPos pos, Direction side) {
        return blockState.getValue(POWERED) ? 15 : 0;
    }

    /**
     * Returns the direct signal this block emits in the given direction.
     *
     * <p>
     * NOTE: directions in redstone signal related methods are backwards, so this method
     * checks for the signal emitted in the <i>opposite</i> direction of the one given.
     *
     * @deprecated call via {@link net.minecraft.world.level.block.state.BlockBehaviour.BlockStateBase#getDirectSignal} whenever possible. Implementing/overriding is fine.
     */
    @Override
    protected int getDirectSignal(BlockState blockState, BlockGetter blockAccess, BlockPos pos, Direction side) {
        return side == Direction.UP && blockState.getValue(POWERED) ? 15 : 0;
    }

    /**
     * @deprecated call via {@link net.minecraft.world.level.block.state.BlockBehaviour.BlockStateBase#hasAnalogOutputSignal} whenever possible. Implementing/overriding is fine.
     */
    @Override
    protected boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    /**
     * Returns the analog signal this block emits. This is the signal a comparator can read from it.
     *
     * @deprecated call via {@link net.minecraft.world.level.block.state.BlockBehaviour.BlockStateBase#getAnalogOutputSignal} whenever possible. Implementing/overriding is fine.
     */
    @Override
    protected int getAnalogOutputSignal(BlockState blockState, Level level, BlockPos pos) {
        if (blockState.getValue(HAS_BOOK)) {
            BlockEntity blockentity = level.getBlockEntity(pos);
            if (blockentity instanceof CompositionWorkbenchBlockEntity) {
                return ((CompositionWorkbenchBlockEntity)blockentity).getRedstoneSignal();
            }
        }

        return 0;
    }

    @Override
    protected ItemInteractionResult useItemOn(
            ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult
    ) {
        if (state.getValue(HAS_BOOK)) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        } else if (stack.is(MelodyMagic.CONTINUE_SOUND_CONTAINER_ITEM.get())) {
            return tryPlaceBook(player, level, pos, state, stack)
                    ? ItemInteractionResult.sidedSuccess(level.isClientSide)
                    : ItemInteractionResult.SKIP_DEFAULT_BLOCK_INTERACTION;
        } else {
            return stack.isEmpty() && hand == InteractionHand.MAIN_HAND
                    ? ItemInteractionResult.SKIP_DEFAULT_BLOCK_INTERACTION
                    : ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (state.getValue(HAS_BOOK)) {
            if (level.isClientSide) {

                //Minecraft.getInstance().setScreen(effectBook);

            }

            return InteractionResult.sidedSuccess(level.isClientSide);
        } else {
            return InteractionResult.CONSUME;
        }
    }



    @Override
    protected boolean isPathfindable(BlockState state, PathComputationType pathComputationType) {
        return false;
    }
}
