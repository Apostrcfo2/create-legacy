package nl.melonstudios.create.tileentity.actor;

import net.minecraft.item.ItemStack;
import nl.melonstudios.create.tileentity.marker.IInventoryDebloated;

public class TileEntityStorageInterface extends TileEntityContraptionInterfaceBase implements IInventoryDebloated {
    public TileEntityStorageInterface() {
        super();
    }

    @Override
    public String getName() {
        return "Storage Interface";
    }

    @Override
    public int getSizeInventory() {
        return this.getInventory().getInventoryRepresentation().getSizeInventory();
    }

    @Override
    public boolean isEmpty() {
        return this.getInventory().getInventoryRepresentation().isEmpty();
    }

    @Override
    public ItemStack getStackInSlot(int index) {
        return this.getInventory().getInventoryRepresentation().getStackInSlot(index);
    }

    @Override
    public ItemStack decrStackSize(int index, int count) {
        ItemStack stack = this.getInventory().getInventoryRepresentation().decrStackSize(index, count);
        if (!stack.isEmpty()) this.setDisconnectionTimer(40);
        return stack;
    }

    @Override
    public ItemStack removeStackFromSlot(int index) {
        ItemStack stack = this.getInventory().getInventoryRepresentation().removeStackFromSlot(index);
        if (!stack.isEmpty()) this.setDisconnectionTimer(40);
        return stack;
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        this.setDisconnectionTimer(40);
        this.getInventory().getInventoryRepresentation().setInventorySlotContents(index, stack);
    }

    @Override
    public int getInventoryStackLimit() {
        return this.getInventory().getInventoryRepresentation().getInventoryStackLimit();
    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        return this.getInventory().getInventoryRepresentation().isItemValidForSlot(index, stack);
    }

    @Override
    public void clear() {
        this.getInventory().getInventoryRepresentation().clear();
        this.setDisconnectionTimer(40);
    }
}
