package org.chubby.github.symbioticstructures.api.multiblock;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Mod.EventBusSubscriber
public class MultiBlockManager extends SavedData {
    private static final String DATA_NAME = "symbiotic_multiblocks";
    private static final String NBT_MULTIBLOCKS = "MultiBlocks";

    private static MultiBlockManager instance;
    private final Map<UUID, MultiBlockController> activeMultiBlocks = new ConcurrentHashMap<>();
    private final Map<BlockPos, UUID> positionToController = new ConcurrentHashMap<>();

    public MultiBlockManager() {
        super();
    }

    public static MultiBlockManager getInstance() {
        if (instance == null) {
            instance = new MultiBlockManager();
        }
        return instance;
    }

    public static MultiBlockManager get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(
                MultiBlockManager::load,
                MultiBlockManager::new,
                DATA_NAME
        );
    }

    public static MultiBlockManager load(CompoundTag tag) {
        MultiBlockManager manager = new MultiBlockManager();
        manager.loadFromNBT(tag);
        return manager;
    }

    public void registerMultiBlock(Level level, MultiBlockController controller) {
        if (level.isClientSide) return;
        UUID id = controller.getControllerId();
        activeMultiBlocks.put(id, controller);
        for (BlockPos pos : controller.getMultiBlockPositions()) {
            positionToController.put(pos, id);
        }
        setDirty();
    }

    public void unregisterMultiBlock(Level level, MultiBlockController controller) {
        if (level.isClientSide) return;
        UUID id = controller.getControllerId();
        activeMultiBlocks.remove(id);
        for (BlockPos pos : controller.getMultiBlockPositions()) {
            positionToController.remove(pos, id);
        }
        setDirty();
    }

    public Optional<MultiBlockController> getControllerAt(BlockPos pos) {
        UUID controllerId = positionToController.get(pos);
        if (controllerId != null) {
            return Optional.ofNullable(activeMultiBlocks.get(controllerId));
        }
        return Optional.empty();
    }

    public Collection<MultiBlockController> getAllControllers() {
        return activeMultiBlocks.values();
    }

    public void validateAllMultiBlocks(Level level) {
        if (level.isClientSide) return;
        List<UUID> invalidControllers = new ArrayList<>();
        for (Map.Entry<UUID, MultiBlockController> entry : activeMultiBlocks.entrySet()) {
            MultiBlockController controller = entry.getValue();
            if (controller.getMasterPos() != null) {
                if (!controller.validateStructure(level, controller.getMasterPos())) {
                    invalidControllers.add(entry.getKey());
                }
            } else {
                invalidControllers.add(entry.getKey());
            }
        }
        for (UUID id : invalidControllers) {
            MultiBlockController controller = activeMultiBlocks.remove(id);
            if (controller != null) {
                controller.breakMultiBlock(level);
            }
        }
        if (!invalidControllers.isEmpty()) {
            setDirty();
        }
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        ListTag multiBlocksList = new ListTag();
        for (MultiBlockController controller : activeMultiBlocks.values()) {
            if (controller.isFormed()) {
                multiBlocksList.add(controller.saveToNBT());
            }
        }
        tag.put(NBT_MULTIBLOCKS, multiBlocksList);
        return tag;
    }

    public void loadFromNBT(CompoundTag tag) {
        activeMultiBlocks.clear();
        positionToController.clear();
        if (tag.contains(NBT_MULTIBLOCKS)) {
            ListTag multiBlocksList = tag.getList(NBT_MULTIBLOCKS, Tag.TAG_COMPOUND);
            for (int i = 0; i < multiBlocksList.size(); i++) {
                CompoundTag controllerTag = multiBlocksList.getCompound(i);
                UUID controllerId = UUID.fromString(controllerTag.getString("ControllerId"));
                MultiBlockController controller = new MultiBlockController(null, controllerId);
                controller.loadFromNBT(controllerTag, null);
                activeMultiBlocks.put(controllerId, controller);
            }
        }
    }

    @SubscribeEvent
    public static void onWorldLoad(LevelEvent.Load event) {
        if (event.getLevel() instanceof ServerLevel serverLevel) {
            MultiBlockManager manager = get(serverLevel);
            serverLevel.getServer().execute(() -> {
                manager.validateAllMultiBlocks(serverLevel);
            });
        }
    }

    @SubscribeEvent
    public static void onWorldSave(LevelEvent.Save event) {
        if (event.getLevel() instanceof ServerLevel serverLevel) {
            MultiBlockManager manager = get(serverLevel);
            manager.setDirty();
        }
    }
}
