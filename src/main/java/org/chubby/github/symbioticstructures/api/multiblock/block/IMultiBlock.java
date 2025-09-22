package org.chubby.github.symbioticstructures.api.multiblock.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.chubby.github.symbioticstructures.api.multiblock.MultiBlockShape;

public interface IMultiBlock {
    /**
     * Get the multiblock shape definition for this block type
     */
    MultiBlockShape getMultiBlockShape();

    /**
     * Called when this block becomes part of a formed multiblock
     */
    default void onMultiBlockFormed(Level level, BlockPos pos, BlockPos masterPos) {}

    /**
     * Called when this block is removed from a multiblock
     */
    default void onMultiBlockBroken(Level level, BlockPos pos, BlockPos masterPos) {}

    /**
     * Whether this block can be the master block of the multiblock
     */
    default boolean canBeMaster() { return true; }
}