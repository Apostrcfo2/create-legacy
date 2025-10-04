package nl.melonstudios.create.recipe.jei;

import com.melonstudios.melonlib.misc.Localizer;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeCategory;
import net.minecraft.util.ResourceLocation;

public class SandingRecipeCategory implements IRecipeCategory<SandingRecipe> {
    private static final ResourceLocation TEXTURES =
            new ResourceLocation("create", "textures/gui/jei/sandpaper.png");

    protected static final int input = 0;
    protected static final int output = 1;

    private final IDrawable background;

    @Override
    public String getUid() {
        return "create.sanding";
    }

    @Override
    public String getTitle() {
        return Localizer.translate("recipe.create.sanding");
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
    public void setRecipe(IRecipeLayout iRecipeLayout, SandingRecipe sandingRecipe, IIngredients iIngredients) {
        IGuiItemStackGroup stacks = iRecipeLayout.getItemStacks();
        stacks.init(input, true, 1, 7);
        stacks.init(output, false, 45, 7);
        stacks.set(iIngredients);
    }

    public SandingRecipeCategory(IGuiHelper helper) {
        this.background = helper.drawableBuilder(TEXTURES, 0, 0, 64, 32).setTextureSize(64, 32).build();
    }
}
