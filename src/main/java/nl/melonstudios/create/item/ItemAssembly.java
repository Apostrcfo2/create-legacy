package nl.melonstudios.create.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import nl.melonstudios.create.recipe.sequence.SequenceRecipe;
import nl.melonstudios.create.recipe.sequence.SequencedRecipes;

public class ItemAssembly extends Item {
    public ItemAssembly() {
        super();
        this.setRegistryName("assembly");
        this.setUnlocalizedName("create.assembly");
        this.setHasSubtypes(true);
        this.setMaxStackSize(1);
        this.setMaxDamage(0);
    }

    @Override
    public boolean showDurabilityBar(ItemStack stack) {
        return true;
    }

    @Override
    public double getDurabilityForDisplay(ItemStack stack) {
        NBTTagCompound data = stack.getSubCompound("SequencedAssembly");
        if (data == null) return 1.0;
        SequenceRecipe recipe = SequencedRecipes.instance.getRecipe(data.getString("id"));
        int step = data.getInteger("step");
        return 1.0 - this.progress(recipe, step);
    }
    private double progress(SequenceRecipe recipe, int step) {
        return (double)step / (recipe.steps.size() * recipe.repetitions);
    }

    @Override
    public int getRGBDurabilityForDisplay(ItemStack stack) {
        return 0x8888FF;
    }

    public static final String[] NAME_LOOKUP = {
            "precision_mechanism", "sturdy_sheet"
    };

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        return "item.create.assembly_" + NAME_LOOKUP[stack.getMetadata() & 1];
    }
}
