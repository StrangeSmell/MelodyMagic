package com.strangesmell.melodymagic.block;

import com.strangesmell.melodymagic.MelodyMagic;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import static com.strangesmell.melodymagic.block.FakeNetherPortal.AXIS;

public class FakeNetherPortalBlockEntity extends BlockEntity {
    public int num=100;
    public FakeNetherPortalBlockEntity( BlockPos pPos, BlockState pBlockState) {
        super(MelodyMagic.FAKE_NETHER_PORTAL_BLOCK_ENTITY.get(), pPos, pBlockState);
    }

    public static void tick(Level level, BlockPos pos, BlockState blockState, FakeNetherPortalBlockEntity fakeNetherPortalBlockEntity) {
        fakeNetherPortalBlockEntity.num--;
        if(fakeNetherPortalBlockEntity.num<=0){
            //level.destroyBlock(blockPos, false);
            boolean isX=blockState.getValue(AXIS) == Direction.Axis.X;


            if(isX){
                for(int i=-2;i<3;i++){
                    destroyObAndPortal(level,pos.offset(1,i,0));
                    destroyObAndPortal(level, pos.offset(-1,i,0));
                    destroyObAndPortal(level, pos.offset(0,i,0));
                }
            }else{
                for(int i=-2;i<3;i++){
                    destroyObAndPortal(level,pos.offset(0,i,1));
                    destroyObAndPortal(level,pos.offset(0,i,-1));
                    destroyObAndPortal(level,pos.offset(0,i,0));
                }
            }


            level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
        }
    }

    public static void destroyObAndPortal(Level level, BlockPos pos) {
        if(level.getBlockState(pos).getBlock() == Blocks.OBSIDIAN){
            level.destroyBlock(pos, false);
        }
    }


}
