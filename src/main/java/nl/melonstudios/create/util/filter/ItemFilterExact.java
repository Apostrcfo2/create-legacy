package nl.melonstudios.create.util.filter;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class ItemFilterExact implements IItemFilter {
    private final ItemStack example;

    public ItemFilterExact(ItemStack example) {
        this.example = example;
    }

    @Override
    public boolean matches(ItemStack stack) {
        return this.example.isItemEqualIgnoreDurability(stack);
    }

    @Override
    public NBTTagCompound serialize(NBTTagCompound nbt) {
        nbt.setByte("type", (byte)0);
        nbt.setTag("ExampleItem", this.example.writeToNBT(new NBTTagCompound()));
        return nbt;
    }

    @Override
    public ItemStack getRenderItem() {
        return this.example;
    }
}
