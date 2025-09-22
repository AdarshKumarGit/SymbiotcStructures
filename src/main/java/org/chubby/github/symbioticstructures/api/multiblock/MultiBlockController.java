package org.chubby.github.symbioticstructures.api.multiblock;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.chubby.github.symbioticstructures.api.multiblock.block.IMultiBlock;
import org.chubby.github.symbioticstructures.api.multiblock.blockEntity.IMultiBlockEntity;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class MultiBlockController {
    private static final String NBT_MULTIBLOCK_POSITIONS = "MultiBlockPositions";
    private static final String NBT_MASTER_POS = "MasterPos";
    private static final String NBT_IS_FORMED = "IsFormed";
    private static final String NBT_CONTROLLER_ID = "ControllerId";

    private final Set<BlockPos> multiBlockPositions = new HashSet<>();
    private BlockPos masterPos;
    private MultiBlockShape shape;
    private boolean isFormed = false;
    private final UUID controllerId;

    public MultiBlockController(MultiBlockShape shape) {
        this.shape = shape;
        this.controllerId = UUID.randomUUID();
    }

    public MultiBlockController(MultiBlockShape shape, UUID controllerId) {
        this.shape = shape;
        this.controllerId = controllerId != null ? controllerId : UUID.randomUUID();
    }

    /**
     * Attempt to validate and form the multiblock structure
     */
    public boolean validateStructure(Level level, BlockPos potentialMasterPos) {
        if (shape.getValidator().validate(level, potentialMasterPos, shape)) {
            formMultiBlock(level, potentialMasterPos);
            return true;
        } else {
            breakMultiBlock(level);
            return false;
        }
    }

    /**
     * Form the multiblock structure
     */
    private void formMultiBlock(Level level, BlockPos masterPos) {
        if (isFormed && this.masterPos.equals(masterPos)) {
            return;
        }

        if (isFormed) {
            breakMultiBlock(level);
        }

        this.masterPos = masterPos;
        this.isFormed = true;
        multiBlockPositions.clear();

        for (BlockPos relativePos : shape.getBlockPositions()) {
            BlockPos absolutePos = masterPos.offset(relativePos);
            multiBlockPositions.add(absolutePos);

            BlockEntity blockEntity = level.getBlockEntity(absolutePos);
            if (blockEntity instanceof IMultiBlockEntity multiBlockEntity) {
                multiBlockEntity.setController(this);
                multiBlockEntity.setMasterPos(masterPos);
                multiBlockEntity.onMultiBlockFormed();
            }

            if (level.getBlockState(absolutePos).getBlock() instanceof IMultiBlock multiBlock) {
                multiBlock.onMultiBlockFormed(level, absolutePos, masterPos);
            }
        }

        MultiBlockManager.getInstance().registerMultiBlock(level, this);
    }

    /**
     * Break the multiblock structure
     */
    public void breakMultiBlock(Level level) {
        if (!isFormed) return;

        for (BlockPos pos : multiBlockPositions) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof IMultiBlockEntity multiBlockEntity) {
                multiBlockEntity.setController(null);
                multiBlockEntity.setMasterPos(null);
                multiBlockEntity.onMultiBlockBroken();
            }

            if (level.getBlockState(pos).getBlock() instanceof IMultiBlock multiBlock) {
                multiBlock.onMultiBlockBroken(level, pos, masterPos);
            }
        }

        multiBlockPositions.clear();
        masterPos = null;
        isFormed = false;
    }

    public boolean restoreFromNBT(Level level, BlockPos masterPos) {
        if (shape.getValidator().validate(level, masterPos, shape)) {
            formMultiBlock(level, masterPos);
            return true;
        }
        return false;
    }

    public CompoundTag saveToNBT() {
        CompoundTag tag = new CompoundTag();

        tag.putString(NBT_CONTROLLER_ID, controllerId.toString());
        tag.putBoolean(NBT_IS_FORMED, isFormed);

        if (masterPos != null) {
            tag.put(NBT_MASTER_POS, NbtUtils.writeBlockPos(masterPos));
        }

        ListTag positionsList = new ListTag();
        for (BlockPos pos : multiBlockPositions) {
            positionsList.add(NbtUtils.writeBlockPos(pos));
        }
        tag.put(NBT_MULTIBLOCK_POSITIONS, positionsList);

        return tag;
    }

    public void loadFromNBT(CompoundTag tag, Level level) {
        if (tag.contains(NBT_CONTROLLER_ID)) {
        }

        isFormed = tag.getBoolean(NBT_IS_FORMED);

        if (tag.contains(NBT_MASTER_POS)) {
            masterPos = NbtUtils.readBlockPos(tag.getCompound(NBT_MASTER_POS));
        }

        multiBlockPositions.clear();
        if (tag.contains(NBT_MULTIBLOCK_POSITIONS)) {
            ListTag positionsList = tag.getList(NBT_MULTIBLOCK_POSITIONS, Tag.TAG_COMPOUND);
            for (int i = 0; i < positionsList.size(); i++) {
                BlockPos pos = NbtUtils.readBlockPos(positionsList.getCompound(i));
                multiBlockPositions.add(pos);
            }
        }

        if (isFormed && masterPos != null && level != null) {
            level.scheduleTick(masterPos, level.getBlockState(masterPos).getBlock(), 1);
        }
    }

    /**
     * Check if a position is part of this multiblock
     */
    public boolean isPartOfMultiBlock(BlockPos pos) {
        return multiBlockPositions.contains(pos);
    }

    public boolean isFormed() {
        return isFormed;
    }

    public BlockPos getMasterPos() {
        return masterPos;
    }

    public Set<BlockPos> getMultiBlockPositions() {
        return new HashSet<>(multiBlockPositions);
    }

    public UUID getControllerId() {
        return controllerId;
    }
}