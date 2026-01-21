package nl.melonstudios.create.recipe.jei;

import com.melonstudios.melonlib.misc.Localizer;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeCategory;
import net.minecraft.util.ResourceLocation;

public class DeployerRecipeCategory implements IRecipeCategory<JEIDeployerRecipe> {
    private static final ResourceLocation TEXTURES =
            new ResourceLocation("create", "textures/gui/jei/deploying.png");

    protected static final int input = 0;
    protected static final int applied = 1;
    protected static final int output = 2;

    private final IDrawable background;

    @Override
    public String getUid() {
        return "create.deploying";
    }

    @Override
    public String getTitle() {
        return Localizer.translate("recipe.create.deploying");
    }

    @Override
    public String getModName() {
        return "create";
    }

    @Override
    public IDrawable getBackground() {
        return this.background;
    }

    @Override
    public void setRecipe(IRecipeLayout layout, JEIDeployerRecipe recipe, IIngredients ingredients) {
        IGuiItemStackGroup stacks = layout.getItemStacks();
        stacks.init(input, true, 1, 7);
        stacks.init(applied, true, 1, 17);
        stacks.init(output, false, 45, 7);
        stacks.set(ingredients);
    }

    public DeployerRecipeCategory(IGuiHelper helper) {
        this.background = helper.drawableBuilder(TEXTURES, 0, 0, 64, 32).setTextureSize(64, 32).build();
    }
}
