package nl.melonstudios.create.recipe.sequence;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class SequenceStep {
    public final String name;
    public final NBTTagCompound data;

    public SequenceStep(String name, NBTTagCompound data) {
        this.name = name;
        this.data = data;
    }
    public SequenceStep(String name) {
        this(name, new NBTTagCompound());
    }

    public static SequenceStep pressing() {
        return new SequenceStep("pressing");
    }
    public static SequenceStep deploying(ItemStack applied) {
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setTag("Applied", applied.writeToNBT(new NBTTagCompound()));
        return new SequenceStep("deploying", nbt);
    }
    public static SequenceStep cutting() {
        return new SequenceStep("cutting");
    }
}
