package nl.melonstudios.create.recipe;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class SawingRecipe {
    public final String recipeID;
    public final ItemStack input;
    public final ItemStack result;
    public final int processingTime;

    public SawingRecipe(String recipeID, ItemStack input, ItemStack result, int processingTime) {
        this.recipeID = recipeID;
        this.input = input;
        this.result = result;
        this.processingTime = processingTime;
    }
    public SawingRecipe(String recipeID, NBTTagCompound nbt) {
        this.recipeID = recipeID;
        this.input = new ItemStack(nbt.getCompoundTag("Input"));
        this.result = new ItemStack(nbt.getCompoundTag("Result"));
        this.processingTime = nbt.getInteger("processingTime");
    }
}
