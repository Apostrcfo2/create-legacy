package nl.melonstudios.create.recipe;

import net.minecraft.nbt.NBTTagCompound;

@Deprecated //Use the MelonLib equivalent instead
public interface NBTDecodableRecipeType {
    String getRecipeType();

    void decodeRecipe(String recipeId, NBTTagCompound nbt);
}
