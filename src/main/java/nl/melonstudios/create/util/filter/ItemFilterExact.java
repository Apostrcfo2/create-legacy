package nl.melonstudios.create.util.filter;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.oredict.OreDictionary;

public class ItemFilterExact implements IItemFilter {
    private final ItemStack example;

    public ItemFilterExact(ItemStack example) {
        this.example = example.copy();
        this.example.setCount(1);
    }

    @Override
    public boolean matches(ItemStack stack) {
        return OreDictionary.itemMatches(this.example, stack, false);
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

    @Override
    public boolean matches(FluidStack stack) {
        IFluidHandlerItem handler = FluidUtil.getFluidHandler(this.example);
        if (handler == null) return false;
        return handler.drain(stack, false) != null;
    }
}
