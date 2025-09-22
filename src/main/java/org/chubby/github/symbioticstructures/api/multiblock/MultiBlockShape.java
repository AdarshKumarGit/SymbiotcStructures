package org.chubby.github.symbioticstructures.api.multiblock;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;

public class MultiBlockShape {
    private final List<BlockPos> blockPositions;
    private final List<BlockState> blockStates;
    private final MultiBlockValidator validator;
    private final String shapeId;

    public MultiBlockShape(String shapeId, List<BlockPos> blockPositions, List<BlockState> blockStates, MultiBlockValidator validator) {
        this.shapeId = shapeId;
        this.blockPositions = blockPositions;
        this.blockStates = blockStates;
        this.validator = validator;
    }

    public List<BlockPos> getBlockPositions() {
        return blockPositions;
    }

    public List<BlockState> getBlockStates() {
        return blockStates;
    }

    public MultiBlockValidator getValidator() {
        return validator;
    }

    public String getShapeId() {
        return shapeId;
    }

    public CompoundTag saveToNBT() {
        CompoundTag tag = new CompoundTag();

        tag.putString("ShapeId", shapeId);

        ListTag positionsList = new ListTag();
        for (BlockPos pos : blockPositions) {
            positionsList.add(NbtUtils.writeBlockPos(pos));
        }
        tag.put("BlockPositions", positionsList);

        ListTag statesList = new ListTag();
        for (BlockState state : blockStates) {
            statesList.add(NbtUtils.writeBlockState(state));
        }
        tag.put("BlockStates", statesList);

        return tag;
    }

    public MultiBlockShape loadFromNBT(CompoundTag tag, Level level, MultiBlockValidator validator) {
        String shapeId = tag.getString("ShapeId");

        List<BlockPos> positions = new ArrayList<>();
        if (tag.contains("BlockPositions")) {
            ListTag positionsList = tag.getList("BlockPositions", Tag.TAG_COMPOUND);
            for (int i = 0; i < positionsList.size(); i++) {
                positions.add(NbtUtils.readBlockPos(positionsList.getCompound(i)));
            }
        }

        List<BlockState> states = new ArrayList<>();
        if (tag.contains("BlockStates")) {
            ListTag statesList = tag.getList("BlockStates", Tag.TAG_COMPOUND);
            for (int i = 0; i < statesList.size(); i++) {
                states.add(NbtUtils.readBlockState(level.holderLookup(Registries.BLOCK),
                        statesList.getCompound(i)));
            }
        }

        return new MultiBlockShape(shapeId, positions, states, validator);
    }

}
