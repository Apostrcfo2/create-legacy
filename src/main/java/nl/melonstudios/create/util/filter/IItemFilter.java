package nl.melonstudios.create.util.filter;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public interface IItemFilter {
    boolean matches(ItemStack stack);
    NBTTagCompound serialize(NBTTagCompound nbt);
    ItemStack getRenderItem();

    static IItemFilter deserialize(NBTTagCompound nbt) {
        byte type = nbt.getByte("type");
        if (type == 0) {
            return new ItemFilterExact(new ItemStack(nbt.getCompoundTag("ExampleItem")));
        }
        return null;
    }
}
