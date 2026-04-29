package nl.melonstudios.create.recipe.server;

import com.melonstudios.melonlib.misc.StackUtil;
import com.melonstudios.melonlib.recipe.ISyncedRecipeType;
import com.melonstudios.melonlib.recipe.Ingredient;
import com.melonstudios.melonlib.recipe.RecipeException;
import com.melonstudios.melonlib.recipe.UniversalRecipe;
import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import nl.melonstudios.create.recipe.DeployerRecipe;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DeployerRecipes implements ISyncedRecipeType<DeployerRecipe> {
    public static final DeployerRecipes instance = new DeployerRecipes();

    private DeployerRecipes() {

    }

    public final HashMap<String, DeployerRecipe> recipes = new HashMap<>();

    public final DeployerRecipe getRecipeForInput(ItemStack depotItem, ItemStack deployerItem) {
        if (depotItem.isEmpty() || deployerItem.isEmpty()) return null; //failsafe
        for (DeployerRecipe recipe : this.recipes.values()) {
            if (recipe.input.matches(depotItem)) {
                if (recipe.applied.matches(deployerItem)) {
                    return recipe;
                }
            }
        }
        return null;
    }

    @Override
    public void write(DeployerRecipe recipe, ByteBuf buf) throws IOException {
        recipe.input.serialize(buf);
        recipe.applied.serialize(buf);
        StackUtil.writeItemStack(recipe.result, buf, true, true);
        buf.writeByte(DeployerRecipe.InputType.lookup(recipe.inputType));
    }

    @Override
    public DeployerRecipe read(String recipeID, ByteBuf buf) throws IOException {
        Ingredient input = Ingredient.read(buf);
        Ingredient applied = Ingredient.read(buf);
        ItemStack result = StackUtil.readItemStack(buf, true, true);
        DeployerRecipe.InputType inputType = DeployerRecipe.InputType.lookup(buf.readByte());
        return new DeployerRecipe(input, applied, result, inputType);
    }

    @Override
    public void addRecipe(@Nonnull String recipeID, @Nonnull DeployerRecipe recipe) {
        this.recipes.put(recipeID, recipe);
    }

    public void addRecipe(@Nonnull String recipeID, @Nonnull Ingredient input, @Nonnull Ingredient applied, @Nonnull ItemStack result, DeployerRecipe.InputType inputType) {
        this.addRecipe(recipeID, new DeployerRecipe(input, applied, result, inputType));
    }
    public void addRecipe(@Nonnull String recipeID, @Nonnull Ingredient input, @Nonnull Ingredient applied, @Nonnull ItemStack result) {
        this.addRecipe(recipeID, input, applied, result, DeployerRecipe.InputType.CONSUME);
    }

    @Nullable
    @Override
    public DeployerRecipe getRecipe(@Nonnull String recipeID) {
        return this.recipes.get(recipeID);
    }

    @Override
    public boolean hasRecipe(@Nonnull String recipeID) {
        return this.recipes.containsKey(recipeID);
    }

    @Override
    public void removeRecipe(@Nonnull String recipeID) {
        this.recipes.remove(recipeID);
    }

    @Nonnull
    @Override
    public Collection<String> getAllRecipeIDs() {
        return this.recipes.keySet();
    }

    @Nonnull
    @Override
    public Collection<DeployerRecipe> getAllRecipes() {
        return this.recipes.values();
    }

    @Nonnull
    @Override
    public Map<String, DeployerRecipe> getRecipeMap() {
        return this.recipes;
    }

    @Nonnull
    @Override
    public DeployerRecipe convert(@Nonnull UniversalRecipe universal) throws RecipeException {
        try {
            List<Ingredient> in = universal.itemInputs.get(0);
            Ingredient depot = in.get(0);
            Ingredient held = in.get(1);
            ItemStack result = universal.itemOutputs.get(0).get(0);
            return new DeployerRecipe(depot, held, result, DeployerRecipe.InputType.get(universal.extraData.getString("inputType")));
        } catch (Throwable e) {
            throw new RecipeException(e);
        }
    }
}
