package nl.melonstudios.create.recipe;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class FlatteningRecipe {
    public final String recipeID;
    public final ItemStack input;
    public final ItemStack result;

    public FlatteningRecipe(String recipeID, ItemStack input, ItemStack result) {
        this.recipeID = recipeID;
        this.input = input;
        this.result = result;
    }
    public FlatteningRecipe(String recipeID, NBTTagCompound nbt) {
        this.recipeID = recipeID;
        this.input = new ItemStack(nbt.getCompoundTag("Input"));
        this.result = new ItemStack(nbt.getCompoundTag("Result"));
    }
}
