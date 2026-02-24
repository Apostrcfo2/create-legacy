package nl.melonstudios.create.recipe;

import com.melonstudios.melonlib.recipe.Ingredient;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class CuttingRecipe {
    public final Ingredient input;
    public final ItemStack result;
    public final int processingTime;

    public CuttingRecipe(Ingredient input, ItemStack result, int processingTime) {
        this.input = input;
        this.result = result;
        this.processingTime = processingTime;
    }
    public CuttingRecipe(NBTTagCompound nbt) {
        this.input = Ingredient.read(nbt.getCompoundTag("Input"));
        this.result = new ItemStack(nbt.getCompoundTag("Result"));
        this.processingTime = nbt.getInteger("processingTime");
    }
}
