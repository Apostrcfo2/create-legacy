package nl.melonstudios.create.recipe;

import net.minecraft.nbt.NBTTagCompound;

public interface NBTDecodableRecipeType {
    String getRecipeType();

    void decodeRecipe(String recipeId, NBTTagCompound nbt);
}
