package org.chubby.github.symbioticstructures.api.multiblock;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.chubby.github.symbioticstructures.api.multiblock.block.IMultiBlock;

import java.util.List;
import java.util.function.BiPredicate;

public class MultiBlockValidator {
    private final BiPredicate<BlockState, BlockState> blockMatcher;

    public MultiBlockValidator() {
        this(BlockState::equals);
    }

    public MultiBlockValidator(BiPredicate<BlockState, BlockState> blockMatcher) {
        this.blockMatcher = blockMatcher;
    }

    /**
     * Validate if the structure matches at the given position
     */
    public boolean validate(Level level, BlockPos masterPos, MultiBlockShape shape) {
        List<BlockPos> positions = shape.getBlockPositions();
        List<BlockState> expectedStates = shape.getBlockStates();

        if (positions.size() != expectedStates.size()) {
            return false;
        }

        for (int i = 0; i < positions.size(); i++) {
            BlockPos checkPos = masterPos.offset(positions.get(i));
            BlockState actualState = level.getBlockState(checkPos);
            BlockState expectedState = expectedStates.get(i);

            if (!blockMatcher.test(actualState, expectedState)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Custom validator that checks if blocks implement IMultiBlock
     */
    public static MultiBlockValidator createMultiBlockValidator() {
        return new MultiBlockValidator((actual, expected) -> {
            boolean actualIsMultiBlock = actual.getBlock() instanceof IMultiBlock;
            boolean expectedIsMultiBlock = expected.getBlock() instanceof IMultiBlock;

            if (actualIsMultiBlock && expectedIsMultiBlock) {
                IMultiBlock actualMultiBlock = (IMultiBlock) actual.getBlock();
                IMultiBlock expectedMultiBlock = (IMultiBlock) expected.getBlock();
                return actualMultiBlock.getClass().equals(expectedMultiBlock.getClass());
            }

            return actual.equals(expected);
        });
    }
}