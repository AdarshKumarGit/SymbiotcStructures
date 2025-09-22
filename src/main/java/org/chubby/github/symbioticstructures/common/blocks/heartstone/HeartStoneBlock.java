package org.chubby.github.symbioticstructures.common.blocks.heartstone;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import org.chubby.github.symbioticstructures.api.multiblock.MultiBlockShape;
import org.chubby.github.symbioticstructures.api.multiblock.block.IMultiBlock;
import org.chubby.github.symbioticstructures.common.blocks.entity.heartstone.HeartStoneBlockEntity;
import org.chubby.github.symbioticstructures.common.registry.SSBlockEntities;
import org.jetbrains.annotations.Nullable;

public class HeartStoneBlock extends BaseEntityBlock implements IMultiBlock
{
    private State state;
    public HeartStoneBlock(Properties pProperties) {
        super(pProperties);
        this.state = State.OFF;
    }

    @Override
    public void onPlace(BlockState pState, Level pLevel, BlockPos pPos, BlockState pOldState, boolean pMovedByPiston)
    {
        if(pLevel.isClientSide()) return;
        setState(State.ON);

        BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
        if (blockEntity instanceof HeartStoneBlockEntity heartstone) {
            heartstone.tryFormMultiBlock(pLevel);
        }
    }

    @Override
    public boolean onDestroyedByPlayer(BlockState state, Level level, BlockPos pos, Player player, boolean willHarvest, FluidState fluid) {
        return super.onDestroyedByPlayer(state, level, pos, player, willHarvest, fluid);
    }

    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return super.getRenderShape(pState);
    }

    @Override
    public boolean canHarvestBlock(BlockState state, BlockGetter level, BlockPos pos, Player player) {
        return super.canHarvestBlock(state, level, pos, player);
    }

    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pMovedByPiston) {
    }

    @Override
    public void animateTick(BlockState pState, Level pLevel, BlockPos pPos, RandomSource pRandom) {
        super.animateTick(pState, pLevel, pPos, pRandom);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new HeartStoneBlockEntity(blockPos,blockState);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        return createTickerHelper(pBlockEntityType,SSBlockEntities.HEARTSTONE_BLOCK_ENTITY.get(),HeartStoneBlockEntity::tick);
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    @Override
    public MultiBlockShape getMultiBlockShape() {
        return null;
    }

    @Override
    public boolean canBeMaster() {
        return true;
    }

    public enum State
    {
        ON, OFF;
    }
}
