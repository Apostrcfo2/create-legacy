package nl.melonstudios.create.kinetics.contraption;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import nl.melonstudios.create.util.filter.IItemFilter;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class ContraptionInventory {
    private final List<IInventory> inventories = new ArrayList<>();

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
                        pre.shrink(space);
                        stack.grow(space);
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

    public static ContraptionInventory empty() {
        return Empty.INSTANCE;
    }

    private static class Empty extends ContraptionInventory {
        private static final ContraptionInventory INSTANCE = new Empty();

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
    }
}
