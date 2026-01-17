package nl.melonstudios.create.kinetics.contraption;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import nl.melonstudios.create.tileentity.marker.IInventoryDebloated;
import nl.melonstudios.create.util.filter.IItemFilter;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class ContraptionInventory {
    private final List<IInventory> inventories = new ArrayList<>();
    private IInventory inventoryRepresentation = new InventoryRepresentation(this.inventories);

    public void reindex(Contraption contraption) {
        this.inventories.clear();
        for (TileEntity te : contraption.tileEntities.values()) {
            if (te instanceof IInventory) {
                IInventory inventory = (IInventory) te;
                if (Contraption.isValidInventory(inventory)) {
                    this.inventories.add(inventory);
                }
            }
        }
        this.inventoryRepresentation = new InventoryRepresentation(this.inventories);
    }

    public ItemStack insertItem(ItemStack stack) {
        if (stack.isEmpty()) return ItemStack.EMPTY;
        for (IInventory inventory : this.inventories) {
            for (int i = 0; i < inventory.getSizeInventory(); i++) {
                if (inventory.isItemValidForSlot(i, stack)) {
                    ItemStack pre = inventory.getStackInSlot(i);
                    if (pre.isEmpty()) {
                        inventory.setInventorySlotContents(i, stack);
                        return ItemStack.EMPTY;
                    }
                    int space = Math.min(pre.getMaxStackSize() - pre.getCount(), stack.getCount());
                    if (space <= 0) continue;
                    if (ItemStack.areItemsEqual(pre, stack) && ItemStack.areItemStackTagsEqual(pre, stack)) {
                        stack.shrink(space);
                        pre.grow(space);
                        if (stack.isEmpty()) return ItemStack.EMPTY;
                    }
                }
            }
        }
        return stack;
    }
    public ItemStack retrieveItem(@Nullable Item item, int metadata) {
        boolean ignoreDamage = item == null || !item.getHasSubtypes() || metadata == -1;
        for (IInventory inventory : this.inventories) {
            if (inventory.isEmpty()) continue;
            for (int i = 0; i < inventory.getSizeInventory(); i++) {
                ItemStack stack = inventory.getStackInSlot(i);
                if (stack.isEmpty()) continue;
                if (item == null) return inventory.removeStackFromSlot(i);
                if (stack.getItem() == item && (ignoreDamage || stack.getMetadata() == metadata)) {
                    return inventory.removeStackFromSlot(i);
                }
            }
        }
        return ItemStack.EMPTY;
    }
    public ItemStack retrieveItem(@Nullable IItemFilter filter) {
        for (IInventory inventory : this.inventories) {
            if (inventory.isEmpty()) continue;
            for (int i = 0; i < inventory.getSizeInventory(); i++) {
                ItemStack stack = inventory.getStackInSlot(i);
                if (stack.isEmpty()) continue;
                if (filter == null) return inventory.removeStackFromSlot(i);
                if (filter.matches(stack)) {
                    return inventory.removeStackFromSlot(i);
                }
            }
        }
        return ItemStack.EMPTY;
    }
    public boolean hasNoInventories() {
        return this.inventories.isEmpty();
    }

    public IInventory getInventoryRepresentation() {
        return this.inventoryRepresentation;
    }

    public static ContraptionInventory empty() {
        return Empty.INSTANCE;
    }

    private static class Empty extends ContraptionInventory {
        private static final ContraptionInventory INSTANCE = new Empty();

        private Empty() {

        }

        @Override
        public void reindex(Contraption contraption) {

        }

        @Override
        public ItemStack insertItem(ItemStack stack) {
            return stack;
        }

        @Override
        public ItemStack retrieveItem(@Nullable Item item, int metadata) {
            return ItemStack.EMPTY;
        }

        @Override
        public boolean hasNoInventories() {
            return true;
        }

        @Override
        public IInventory getInventoryRepresentation() {
            return Inventory.INSTANCE;
        }

        private static class Inventory implements IInventoryDebloated {
            private static final IInventory INSTANCE = new Inventory();

            @Override
            public String getName() {
                return "";
            }

            @Override
            public ITextComponent getDisplayName() {
                return new TextComponentString(this.getName());
            }

            @Override
            public int getSizeInventory() {
                return 0;
            }

            @Override
            public boolean isEmpty() {
                return true;
            }

            @Override
            public ItemStack getStackInSlot(int index) {
                throw new ArrayIndexOutOfBoundsException(index);
            }

            @Override
            public ItemStack decrStackSize(int index, int count) {
                throw new ArrayIndexOutOfBoundsException(index);
            }

            @Override
            public ItemStack removeStackFromSlot(int index) {
                throw new ArrayIndexOutOfBoundsException(index);
            }

            @Override
            public void setInventorySlotContents(int index, ItemStack stack) {
                throw new ArrayIndexOutOfBoundsException(index);
            }

            @Override
            public int getInventoryStackLimit() {
                return 0;
            }

            @Override
            public void markDirty() {

            }

            @Override
            public boolean isItemValidForSlot(int index, ItemStack stack) {
                return false;
            }

            @Override
            public void clear() {

            }
        }
    }

    private static class InventoryRepresentation implements IInventory {
        private final List<IInventory> inventories;
        private final int size;
        private InventoryRepresentation(List<IInventory> inventories) {
            this.inventories = inventories;
            int _size = 0;
            for (IInventory inventory : inventories) {
                _size += inventory.getSizeInventory();
            }
            this.size = _size;
        }

        @Override
        public int getSizeInventory() {
            return this.size;
        }

        @Override
        public boolean isEmpty() {
            return this.size == 0;
        }

        @Override
        public ItemStack getStackInSlot(int index) {
            for (IInventory inventory : this.inventories) {
                int s = inventory.getSizeInventory();
                if (index < s) return inventory.getStackInSlot(index);
                index -= s;
            }
            throw new ArrayIndexOutOfBoundsException(index + " out of bounds for " + this.size);
        }

        @Override
        public ItemStack decrStackSize(int index, int count) {
            return this.getStackInSlot(index).splitStack(count);
        }

        @Override
        public ItemStack removeStackFromSlot(int index) {
            ItemStack stack = this.getStackInSlot(index);
            this.setInventorySlotContents(index, ItemStack.EMPTY);
            return stack;
        }

        @Override
        public void setInventorySlotContents(int index, ItemStack stack) {
            for (IInventory inventory : this.inventories) {
                int s = inventory.getSizeInventory();
                if (index < s) {
                    inventory.setInventorySlotContents(index, stack);
                    return;
                }
                index -= s;
            }
            throw new ArrayIndexOutOfBoundsException(index + " out of bounds for " + this.size);
        }

        @Override
        public int getInventoryStackLimit() {
            return 64;
        }

        @Override
        public void markDirty() {

        }

        @Override
        public boolean isUsableByPlayer(EntityPlayer player) {
            return false;
        }

        @Override
        public void openInventory(EntityPlayer player) {

        }

        @Override
        public void closeInventory(EntityPlayer player) {

        }

        @Override
        public boolean isItemValidForSlot(int index, ItemStack stack) {
            for (IInventory inventory : this.inventories) {
                int s = inventory.getSizeInventory();
                if (index < s) return inventory.isItemValidForSlot(index, stack);
                index -= s;
            }
            return false;
        }

        @Override
        public int getField(int id) {
            return 0;
        }

        @Override
        public void setField(int id, int value) {

        }

        @Override
        public int getFieldCount() {
            return 0;
        }

        @Override
        public void clear() {

        }

        @Override
        public String getName() {
            return "ContraptionInventory";
        }

        @Override
        public boolean hasCustomName() {
            return false;
        }

        @Override
        public ITextComponent getDisplayName() {
            return new TextComponentString(this.getName());
        }
    }
}
