package nl.melonstudios.create.mixins;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraft.util.EnumFacing;
import nl.melonstudios.create.tileentity.marker.ITopOpenInventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import javax.annotation.Nullable;

@SuppressWarnings("all")
@Mixin(TileEntityHopper.class)
public abstract class MixinTileEntityHopper implements ITopOpenInventory {
    @Shadow
    protected abstract boolean isFull();

    @Shadow
    public static ItemStack putStackInInventoryAllSlots(IInventory source, IInventory destination, ItemStack stack, @Nullable EnumFacing direction) {
        return null;
    }

    @Override
    public ItemStack tryInsertItem(ItemStack stack) {
        if (this.isFull() || stack.isEmpty()) return stack;
        ((TileEntity)(Object)this).markDirty();
        return this.putStackInInventoryAllSlots(null, (IInventory) this, stack.copy(), EnumFacing.UP);
    }
}
