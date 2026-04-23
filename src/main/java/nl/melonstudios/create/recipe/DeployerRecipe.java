package nl.melonstudios.create.recipe;

import com.melonstudios.melonlib.recipe.Ingredient;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import nl.melonstudios.create.CreateLegacy;

public class DeployerRecipe {
    public final Ingredient input;
    public final Ingredient applied;
    public final ItemStack result;
    public final InputType inputType;

    public DeployerRecipe(Ingredient input, Ingredient applied, ItemStack result, InputType inputType) {
        this.input = input;
        this.applied = applied;
        this.result = result;
        this.inputType = inputType;
    }

    public DeployerRecipe(NBTTagCompound nbt) {
        this.input = Ingredient.read(nbt.getCompoundTag("Input"));
        this.applied = Ingredient.read(nbt.getCompoundTag("Applied"));
        this.result = new ItemStack(nbt.getCompoundTag("Result"));
        this.inputType = InputType.valueOf(nbt.getString("inputType"));
    }

    public enum InputType {
        CONSUME, DAMAGE, KEEP;

        public static InputType get(String name) {
            if (name == null || name.isEmpty()) return CONSUME;
            if ("consume".equalsIgnoreCase(name)) return CONSUME;
            if ("damage".equalsIgnoreCase(name)) return DAMAGE;
            if ("keep".equalsIgnoreCase(name)) return KEEP;
            CreateLegacy.logger.warn("Invalid deployer recipe type: {} (defaulting to InputType.CONSUME)", name);
            return CONSUME;
        }

        private static final InputType[] LOOKUP = {CONSUME, DAMAGE, KEEP};
        public static InputType lookup(int id) {
            return LOOKUP[id];
        }
        public static int lookup(InputType value) {
            return value.ordinal();
        }
    }
}
