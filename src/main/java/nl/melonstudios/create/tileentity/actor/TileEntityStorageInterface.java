package nl.melonstudios.create.tileentity.actor;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

public class TileEntityStorageInterface extends TileEntityContraptionInterfaceBase implements IItemHandler {
    public TileEntityStorageInterface() {
        super();
    }

    @Override
    public int getSlots() {
        return this.getInventory().getInventoryRepresentation().getSlots();
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        return this.getInventory().getInventoryRepresentation().getStackInSlot(slot);
    }

    @Override
    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
        return this.getInventory().getInventoryRepresentation().insertItem(slot, stack, simulate);
    }

    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        return this.getInventory().getInventoryRepresentation().extractItem(slot, amount, simulate);
    }

    @Override
    public int getSlotLimit(int slot) {
        return this.getInventory().getInventoryRepresentation().getSlotLimit(slot);
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack) {
        return this.getInventory().getInventoryRepresentation().isItemValid(slot, stack);
    }
}
