package nl.melonstudios.create.tileentity.marker;

import net.minecraft.item.ItemStack;

public interface ITopOpenInventory {
    ItemStack tryInsertItem(ItemStack stack);
}
