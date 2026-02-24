package nl.melonstudios.create.recipe.client;

import com.melonstudios.melonlib.recipe.IRecipeTypeClient;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import nl.melonstudios.create.recipe.PressingRecipe;
import nl.melonstudios.create.recipe.server.PressingRecipes;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

@SideOnly(Side.CLIENT)
public class PressingRecipesClient implements IRecipeTypeClient<PressingRecipe, PressingRecipes> {
    public static final PressingRecipesClient instance = new PressingRecipesClient();

    private PressingRecipesClient() {

    }

    private final Map<String, PressingRecipe> recipes = new HashMap<>();

    @Nonnull
    @Override
    public Map<String, PressingRecipe> getRecipeMap() {
        return this.recipes;
    }
}
