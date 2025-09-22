package org.chubby.github.symbioticstructures.api.multiblock.blockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.chubby.github.symbioticstructures.api.multiblock.MultiBlockController;

public interface IMultiBlockEntity {
    /**
     * Get the controller for this multiblock entity
     */
    MultiBlockController getController();

    /**
     * Set the controller for this multiblock entity
     */
    void setController(MultiBlockController controller);

    /**
     * Get the master position of this multiblock
     */
    BlockPos getMasterPos();

    /**
     * Set the master position of this multiblock
     */
    void setMasterPos(BlockPos masterPos);

    /**
     * Whether this entity is the master of the multiblock
     */
    default boolean isMaster() {
        return getMasterPos() != null && getMasterPos().equals(getBlockPos());
    }

    /**
     * Get the position of this block entity
     */
    BlockPos getBlockPos();

    /**
     * Called when the multiblock is formed
     */
    default void onMultiBlockFormed() {}

    /**
     * Called when the multiblock is broken
     */
    default void onMultiBlockBroken() {}

    /**
     * Validate and attempt to form the multiblock
     */
    default void tryFormMultiBlock(Level level) {
        if (getController() != null) {
            getController().validateStructure(level, getBlockPos());
        }
    }
}
