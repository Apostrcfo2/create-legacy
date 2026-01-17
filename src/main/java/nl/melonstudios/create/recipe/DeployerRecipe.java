package nl.melonstudios.create.recipe;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import nl.melonstudios.create.CreateLegacy;

public class DeployerRecipe {
    public final String recipeID;
    public final ItemStack input;
    public final ItemStack applied;
    public final ItemStack result;
    public final InputType inputType;

    public DeployerRecipe(String recipeID, ItemStack input, ItemStack applied, ItemStack result, InputType inputType) {
        this.recipeID = recipeID;
        this.input = input;
        this.applied = applied;
        this.result = result;
        this.inputType = inputType;
    }

    public DeployerRecipe(String recipeID, NBTTagCompound nbt) {
        this.recipeID = recipeID;
        this.input = new ItemStack(nbt.getCompoundTag("Input"));
        this.applied = new ItemStack(nbt.getCompoundTag("Applied"));
        this.result = new ItemStack(nbt.getCompoundTag("Result"));
        this.inputType = InputType.valueOf(nbt.getString("inputType"));
    }

    public enum InputType {
        CONSUME, DAMAGE, KEEP;

        public static InputType get(String name) {
            if (name.isEmpty()) return CONSUME;
            if ("consume".equalsIgnoreCase(name)) return CONSUME;
            if ("damage".equalsIgnoreCase(name)) return DAMAGE;
            if ("keep".equalsIgnoreCase(name)) return KEEP;
            CreateLegacy.logger.warn("Invalid deployer recipe type: {} (defaulting to InputType.CONSUME)", name);
            return CONSUME;
        }
    }
}
