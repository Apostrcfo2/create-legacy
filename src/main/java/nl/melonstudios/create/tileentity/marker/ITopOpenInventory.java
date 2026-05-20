package nl.melonstudios.create.tileentity.marker;

import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;

import javax.annotation.Nullable;

public interface ITopOpenInventory {
    /**
     * Inserts an item into this inventory, in the sense of "sliding it on top".
     * @param stack The stack to insert.
     * @return A stack of what could not be inserted.
     */
    ItemStack tryInsertItem(ItemStack stack);

    boolean isInsertionSlotEmpty(ItemStack stack);

    /**
     * Inserts an item into this inventory from a specific side, in the sense of "sliding it on top".
     * Direction sensitive version. Defaults to null side.
     * @param stack The stack to insert.
     * @param side The side from where it is inserted.
     * @return A stack of what could not be inserted.
     */
    default ItemStack tryInsertItem(ItemStack stack, @Nullable EnumFacing side) {
        return this.tryInsertItem(stack);
    }
}
