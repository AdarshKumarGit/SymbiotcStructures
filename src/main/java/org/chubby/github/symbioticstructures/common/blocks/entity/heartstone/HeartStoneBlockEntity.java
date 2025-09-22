package org.chubby.github.symbioticstructures.common.blocks.entity.heartstone;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.chubby.github.symbioticstructures.api.multiblock.MultiBlockController;
import org.chubby.github.symbioticstructures.api.multiblock.blockEntity.IMultiBlockEntity;
import org.chubby.github.symbioticstructures.common.blocks.ProceduralMultiBlockShape;
import org.chubby.github.symbioticstructures.common.menu.HeartstoneMenu;
import org.chubby.github.symbioticstructures.common.registry.SSBlockEntities;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class HeartStoneBlockEntity extends BlockEntity implements IMultiBlockEntity, MenuProvider {

    private static final int MAX_HEALTH = 100;
    private static final int MAX_HUNGER = 100;
    private static final int FEED_SLOT = 0;
    private static final int HUNGER_DECAY_RATE = 1;

    private final ItemStackHandler itemHandler = new ItemStackHandler(1) {
        @Override
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);
            setChanged();
        }
    };
    private LazyOptional<IItemHandler> lazyItemHandler;

    private MultiBlockController controller;
    private BlockPos masterPos;
    private final ProceduralMultiBlockShape proceduralShape;

    private int health = MAX_HEALTH;
    private int hunger = MAX_HUNGER;
    private int growthCooldown = 0;
    private int hungerTickCounter = 0;

    public HeartStoneBlockEntity(BlockPos pos, BlockState state) {
        super(SSBlockEntities.HEARTSTONE_BLOCK_ENTITY.get(), pos, state);
        this.proceduralShape = new ProceduralMultiBlockShape("heartstone_growth");
        this.controller = new MultiBlockController(proceduralShape);
    }

    @Override
    public MultiBlockController getController() {
        return controller;
    }

    @Override
    public void setController(MultiBlockController controller) {
        this.controller = controller;
    }

    @Override
    public BlockPos getMasterPos() {
        return masterPos != null ? masterPos : worldPosition;
    }

    @Override
    public void setMasterPos(BlockPos masterPos) {
        this.masterPos = masterPos;
    }

    @Override
    public void onMultiBlockBroken() {
        health = Math.max(0, health - 10);
        setChanged();
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap) {
        return cap == ForgeCapabilities.ITEM_HANDLER ? lazyItemHandler.cast() : LazyOptional.empty();
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        lazyItemHandler.invalidate();
    }

    @Override
    public void onLoad() {
        super.onLoad();
        lazyItemHandler = LazyOptional.of(() -> itemHandler);
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);

        tag.put("Inventory", itemHandler.serializeNBT());
        tag.putInt("Hunger", hunger);
        tag.putInt("HungerTickCounter", hungerTickCounter);
        tag.putInt("Health", health);
        tag.putInt("GrowthCooldown", growthCooldown);

        tag.put("MultiBlock", controller.saveToNBT());
        tag.put("MultiBlockShape", proceduralShape.saveToNBT());
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);

        itemHandler.deserializeNBT(tag.getCompound("Inventory"));
        hunger = tag.getInt("Hunger");
        hungerTickCounter = tag.getInt("HungerTickCounter");
        health = tag.getInt("Health");
        growthCooldown = tag.getInt("GrowthCooldown");

        controller.loadFromNBT(tag.getCompound("MultiBlock"), this.getLevel());
        proceduralShape.loadFromNBT(tag.getCompound("MultiBlockShape"), this.getLevel(), proceduralShape.getValidator());
    }

    public int getHunger() { return hunger; }
    public void setHunger(int hunger) { this.hunger = hunger; }

    public int getHealth() { return health; }
    public void setHealth(int health) { this.health = health; }

    public int getGrowthCooldown() { return growthCooldown; }
    public void setGrowthCooldown(int growthCooldown) { this.growthCooldown = growthCooldown; }

    public int getHungerTickCounter() { return hungerTickCounter; }
    public void setHungerTickCounter(int hungerTickCounter) { this.hungerTickCounter = hungerTickCounter; }

    private void rebuildMultiBlock() {
        if (level != null) {
            controller.breakMultiBlock(level);
            controller = new MultiBlockController(proceduralShape);
            controller.validateStructure(level, worldPosition);
        }
    }

    private void validateMultiBlock(Level level) {
        if (controller != null) {
            controller.validateStructure(level, worldPosition);
        }
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("gui.sms.heartstone");
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        return new HeartstoneMenu(id,inventory,this);
    }

    public static void tick (Level level,BlockPos pos,BlockState state,BlockEntity entity)
    {
        if(level.isClientSide()) return;

        if(!(entity instanceof HeartStoneBlockEntity hsEntity)) return;

        hsEntity.handleHungerDecay();

        if(hsEntity.growthCooldown > 0){
            hsEntity.growthCooldown--;
        }

        if(hsEntity.growthCooldown == 0 && !hsEntity.itemHandler.getStackInSlot(FEED_SLOT).isEmpty())
        {
            hsEntity.processFeed(hsEntity.itemHandler.getStackInSlot(FEED_SLOT));
        }

        if (hsEntity.hunger > 50 && hsEntity.health < MAX_HEALTH) {
            if (level.getGameTime() % 100 == 0) {
                hsEntity.health = Math.min(hsEntity.health + 1, MAX_HEALTH);
                hsEntity.setChanged();
            }
        }

        if(level.getGameTime() % 40 == 0)
        {
            hsEntity.validateMultiBlock(level);
        }
    }

    private void handleHungerDecay() {
        hungerTickCounter++;
        if (hungerTickCounter >= 200) {
            if (hunger > 0) {
                hunger = Math.max(0, hunger - HUNGER_DECAY_RATE);
                setChanged();
            }
            hungerTickCounter = 0;
        }
    }

    private void processFeed(ItemStack feedItem)
    {
        ProceduralMultiBlockShape.FeedType feedType = determineFeedType(feedItem.getItem());

        if (feedType != null) {
            int nutritionValue = getNutritionValue(feedItem.getItem());

            hunger = Math.min(hunger + nutritionValue, MAX_HUNGER);

            if (proceduralShape.canGrow() && hunger >= 20) {
                proceduralShape.growStructure(feedType, 1);
                hunger -= 10;
                growthCooldown = 60;

                rebuildMultiBlock();
            }

            feedItem.shrink(1);
            setChanged();
        }
    }

    //TODO::FIX THIS
    private ProceduralMultiBlockShape.FeedType determineFeedType(Item item) {
        if (item == Items.BONE || item == Items.BONE_MEAL ||
                item == Items.BEEF || item == Items.PORKCHOP || item == Items.CHICKEN) {
            return ProceduralMultiBlockShape.FeedType.STRUCTURAL;
        }

        if (item == Items.LEATHER || item == Items.WHITE_WOOL ||
                item == Items.FEATHER || item == Items.STRING) {
            return ProceduralMultiBlockShape.FeedType.MEMBRANE;
        }

        if (item == Items.GLOWSTONE_DUST || item == Items.GLOW_INK_SAC) {
            return ProceduralMultiBlockShape.FeedType.BIOLUMINESCENT;
        }

        if (item == Items.HONEYCOMB || item == Items.SLIME_BALL) {
            return ProceduralMultiBlockShape.FeedType.RESIN;
        }

        if (item == Items.AMETHYST_SHARD || item == Items.DIAMOND) {
            return ProceduralMultiBlockShape.FeedType.DEFENSIVE;
        }

        return null;
    }

    private int getNutritionValue(Item item) {
        if (item == Items.DIAMOND || item == Items.AMETHYST_SHARD) {
            return 30;
        }

        if (item == Items.GLOWSTONE_DUST || item == Items.HONEYCOMB) {
            return 20;
        }

        return 10;
    }
}
