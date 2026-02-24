package nl.melonstudios.create.util.filter;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidStack;

public interface IItemFilter {
    boolean matches(ItemStack stack);
    NBTTagCompound serialize(NBTTagCompound nbt);
    ItemStack getRenderItem();

    static IItemFilter deserialize(NBTTagCompound nbt) {
        if (!nbt.hasKey("type")) return null;
        byte type = nbt.getByte("type");
        if (type == 0) {
            return new ItemFilterExact(new ItemStack(nbt.getCompoundTag("ExampleItem")));
        }
        return null;
    }

    default boolean matches(FluidStack stack) {
        return false;
    }
}
