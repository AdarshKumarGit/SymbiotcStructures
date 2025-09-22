package org.chubby.github.symbioticstructures.common.menu;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.chubby.github.symbioticstructures.common.blocks.entity.heartstone.HeartStoneBlockEntity;
import org.chubby.github.symbioticstructures.common.registry.SSMenus;
import org.jetbrains.annotations.Nullable;

public class HeartstoneMenu extends AbstractContainerMenu {

    private final HeartStoneBlockEntity blockEntity;
    private final Level level;

    public HeartstoneMenu(int pContainerId, Inventory inventory, FriendlyByteBuf buf) {
        this(pContainerId, inventory, (HeartStoneBlockEntity) inventory.player.level().getBlockEntity(buf.readBlockPos()));
    }

    public HeartstoneMenu(int pContainerId, Inventory inventory, BlockEntity be) {
        super(SSMenus.HEARTSTONE_MENU.get(), pContainerId);
        this.blockEntity = (HeartStoneBlockEntity) be;
        this.level = be.getLevel();
        checkContainerSize(inventory,2);

        this.addSlot(new Slot(inventory,0,80,32));
        addPlayerInventorySlots(8,84,inventory);

    }

    protected void addPlayerInventorySlots(int x, int y, Inventory playerInventory)
    {
        for(int j = 0; j < 3; j++)
        {
            for(int i = 0; i < 9; i++)
            {
                int slotIndex = i + j * 9 + 9;
                int slotX = x + i * 18;
                int slotY = y + j * 18;
                this.addSlot(new Slot(playerInventory, slotIndex, slotX, slotY));
            }
        }
        for(int i = 0; i < 9; i++)
        {
            int slotX = x + i * 18;
            int slotY = y + 58;
            this.addSlot(new Slot(playerInventory, i, slotX, slotY));
        }
    }

    @Override
    public ItemStack quickMoveStack(Player player, int i) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        return false;
    }
}
