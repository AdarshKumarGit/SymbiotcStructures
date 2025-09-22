package org.chubby.github.symbioticstructures.common.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.chubby.github.symbioticstructures.api.multiblock.MultiBlockShape;
import org.chubby.github.symbioticstructures.api.multiblock.MultiBlockValidator;
import org.chubby.github.symbioticstructures.common.blocks.heartstone.HeartStoneStages;

import java.util.*;

public class ProceduralMultiBlockShape extends MultiBlockShape
{
    private static final int MAX_GROWTH_STAGE = HeartStoneStages.values().length;
    private final Map<BlockPos, BlockState> currentStructure;
    private final RandomSource random;
    private int growthStage;

    public ProceduralMultiBlockShape(String shapeId) {
        super(shapeId,new ArrayList<>(), new ArrayList<>(), MultiBlockValidator.createMultiBlockValidator());
        this.currentStructure = new HashMap<>();
        this.random = RandomSource.create();
        this.growthStage = 0;

        currentStructure.put(BlockPos.ZERO, Blocks.BEDROCK.defaultBlockState());
        updateLists();
    }

    public void growStructure(FeedType feedType, int amount)
    {
        if(this.growthStage > MAX_GROWTH_STAGE) return;

        Set<BlockPos> newGrowthPositions = calculateGrowthPositions(feedType, amount);

        for (BlockPos pos : newGrowthPositions) {
            if (!currentStructure.containsKey(pos)) {
                BlockState newBlock = determineBlockType(feedType, pos);
                currentStructure.put(pos, newBlock);
            }
        }

        growthStage++;
        updateLists();
    }

    private Set<BlockPos> calculateGrowthPositions(FeedType feedType, int amount) {
        Set<BlockPos> newPositions = new HashSet<>();
        List<BlockPos> growthFronts = findGrowthFronts();

        int blocksToGrow = Math.min(amount, 5);

        for (int i = 0; i < blocksToGrow && !growthFronts.isEmpty(); i++) {
            BlockPos growthPoint = growthFronts.get(random.nextInt(growthFronts.size()));
            BlockPos newPos = selectGrowthDirection(growthPoint, feedType);

            if (newPos != null && !currentStructure.containsKey(newPos)) {
                newPositions.add(newPos);
                growthFronts.add(newPos);
            }
        }

        return newPositions;
    }

    private List<BlockPos> findGrowthFronts() {
        List<BlockPos> fronts = new ArrayList<>();

        for (BlockPos pos : currentStructure.keySet()) {
            for (BlockPos adjacent : getAdjacentPositions(pos)) {
                if (!currentStructure.containsKey(adjacent)) {
                    fronts.add(pos);
                    break;
                }
            }
        }

        return fronts;
    }

    private BlockPos selectGrowthDirection(BlockPos from, FeedType feedType) {
        List<BlockPos> adjacentPositions = getAdjacentPositions(from);
        List<BlockPos> validPositions = new ArrayList<>();

        for (BlockPos pos : adjacentPositions) {
            if (!currentStructure.containsKey(pos)) {
                validPositions.add(pos);
            }
        }

        if (validPositions.isEmpty()) {
            return null;
        }

        switch (feedType) {
            case STRUCTURAL -> {
                validPositions.sort((a, b) -> Integer.compare(b.getY(), a.getY()));
            }
            case MEMBRANE -> {
                validPositions.sort((a, b) -> {
                    int aDist = Math.abs(a.getX()) + Math.abs(a.getZ());
                    int bDist = Math.abs(b.getX()) + Math.abs(b.getZ());
                    return Integer.compare(bDist, aDist);
                });
            }
            case DEFENSIVE -> {
                validPositions.sort((a, b) -> {
                    double aDist = Math.sqrt(a.getX()*a.getX() + a.getZ()*a.getZ());
                    double bDist = Math.sqrt(b.getX()*b.getX() + b.getZ()*b.getZ());
                    return Double.compare(bDist, aDist);
                });
            }
        }

        return validPositions.get(random.nextInt(Math.min(3, validPositions.size())));
    }

    private List<BlockPos> getAdjacentPositions(BlockPos pos) {
        return Arrays.asList(
                pos.above(),
                pos.below(),
                pos.north(),
                pos.south(),
                pos.east(),
                pos.west()
        );
    }

    private BlockState determineBlockType(FeedType feedType, BlockPos pos) {
        return switch (feedType) {
            case STRUCTURAL -> Blocks.BONE_BLOCK.defaultBlockState(); // Marrow-Struts
            case MEMBRANE -> Blocks.WHITE_STAINED_GLASS.defaultBlockState(); // Membrane-Walls
            case BIOLUMINESCENT -> Blocks.GLOWSTONE.defaultBlockState(); // Bioluminescent Nodules
            case RESIN -> Blocks.HONEY_BLOCK.defaultBlockState(); // Permeable Resin
            case DEFENSIVE -> Blocks.OBSIDIAN.defaultBlockState(); // Crystalline Plating
        };
    }

    private void updateLists() {
        List<BlockPos> positions = new ArrayList<>(currentStructure.keySet());
        List<BlockState> states = new ArrayList<>();

        for (BlockPos pos : positions) {
            states.add(currentStructure.get(pos));
        }

        getBlockPositions().clear();
        getBlockPositions().addAll(positions);
        getBlockStates().clear();
        getBlockStates().addAll(states);
    }

    public int getStructureSize() {
        return currentStructure.size();
    }

    public int getGrowthStage() {
        return growthStage;
    }

    public boolean canGrow() {
        return currentStructure.size() < MAX_GROWTH_STAGE;
    }

    public Map<BlockPos, BlockState> getCurrentStructure() {
        return new HashMap<>(currentStructure);
    }

    public enum FeedType {
        STRUCTURAL,     // Bone, Meat -> Marrow-Struts
        MEMBRANE,       // Leather, Wool -> Membrane-Walls
        BIOLUMINESCENT, // Glowstone -> Bioluminescent Nodules
        RESIN,          // Honey, Slime -> Permeable Resin
        DEFENSIVE       // Amethyst, Diamond -> Crystalline Plating
    }
}
