package nl.melonstudios.create.recipe.client;

import com.melonstudios.melonlib.recipe.IRecipeTypeClient;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import nl.melonstudios.create.recipe.DeployerRecipe;
import nl.melonstudios.create.recipe.server.DeployerRecipes;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@SideOnly(Side.CLIENT)
public class DeployingRecipesClient implements IRecipeTypeClient<DeployerRecipe, DeployerRecipes> {
    public static final DeployingRecipesClient instance = new DeployingRecipesClient();

    private DeployingRecipesClient() {

    }

    public final HashMap<String, DeployerRecipe> recipes = new HashMap<>();

    @Nullable
    @Override
    public DeployerRecipe getRecipe(@Nonnull String recipeID) {
        return this.recipes.get(recipeID);
    }

    @Override
    public boolean hasRecipe(@Nonnull String recipeID) {
        return this.recipes.containsKey(recipeID);
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

    @Override
    public void prepareForData() {
        this.recipes.clear();
    }

    @Override
    public void addFromLocal(@Nonnull DeployerRecipes local) {
        this.recipes.putAll(local.recipes);
    }

    @Override
    public void addFromRemote(@Nonnull String recipeID, @Nonnull DeployerRecipe recipe) {
        this.recipes.put(recipeID, recipe);
    }
}
