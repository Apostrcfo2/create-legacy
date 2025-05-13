package nl.melonstudios.create.recipe.jei;

import mcp.MethodsReturnNonnullByDefault;
import mezz.jei.api.*;
import mezz.jei.api.ingredients.IIngredientRegistry;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import mezz.jei.api.recipe.transfer.IRecipeTransferRegistry;
import net.minecraft.item.ItemStack;
import nl.melonstudios.create.init.ItemInit;

import javax.annotation.ParametersAreNonnullByDefault;

@JEIPlugin
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public final class JEICompat implements IModPlugin {
    @Override
    public void registerCategories(IRecipeCategoryRegistration registry) {
        final IJeiHelpers helpers = registry.getJeiHelpers();
        final IGuiHelper gui = helpers.getGuiHelper();

        registry.addRecipeCategories(new SandingRecipeCategory(gui));
    }

    @Override
    public void register(IModRegistry registry) {
        final IIngredientRegistry ingredientRegistry = registry.getIngredientRegistry();
        final IJeiHelpers jeiHelpers = registry.getJeiHelpers();

        IRecipeTransferRegistry recipeTransfer = registry.getRecipeTransferRegistry();

        registry.addRecipes(RecipeMaker.getSandingRecipes(jeiHelpers), "create.sanding");
        registry.addRecipeCatalyst(new ItemStack(ItemInit.SANDPAPER), "create.sanding");
    }
}
