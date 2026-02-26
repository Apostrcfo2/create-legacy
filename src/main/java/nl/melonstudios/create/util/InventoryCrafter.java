package nl.melonstudios.create.util;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;
import nl.melonstudios.create.tileentity.actor.TileEntityCrafter;

import javax.annotation.Nullable;

public class InventoryCrafter extends InventoryCrafting {
    private final TileEntityCrafter[][] crafters;
    private final int width, height;
    public InventoryCrafter(TileEntityCrafter[][] crafters) {
        super(null, crafters.length, crafters[0].length);
        this.crafters = crafters;
        this.width = crafters.length;
        this.height = crafters[0].length;
    }

    @Override
    public int getSizeInventory() {
        return this.width * this.height;
    }

    @Override
    public ItemStack getStackInSlot(int index) {
        if (index < 0 || index >= this.getSizeInventory()) return ItemStack.EMPTY;
        return this.getStackInRowAndColumn(index % this.width, index / this.width);
    }

    @Override
    public ItemStack getStackInRowAndColumn(int row, int column) {
        if (row >= 0 && row < this.width) {
            if (column >= 0 && column < this.height) {
                TileEntityCrafter crafter = this.crafters[row][column];
                return crafter != null ? crafter.containedItem : ItemStack.EMPTY;
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public int getInventoryStackLimit() {
        return 1;
    }

    @Override
    public int getHeight() {
        return this.height;
    }

    @Override
    public int getWidth() {
        return this.width;
    }

    @Nullable
    public IRecipe findMatching(World world) {
        return CraftingManager.findMatchingRecipe(this, world);
    }
}
