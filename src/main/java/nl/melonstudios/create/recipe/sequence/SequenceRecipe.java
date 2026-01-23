package nl.melonstudios.create.recipe.sequence;

import com.google.common.collect.ImmutableList;
import com.melonstudios.melonlib.misc.Localizer;
import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import nl.melonstudios.create.CreateLegacy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class SequenceRecipe {
    public final String recipeID;
    public final ItemStack input;
    public final ItemStack processing;
    public final List<SequenceStep> steps;
    public final int repetitions;
    public final SequenceResult result;

    public SequenceRecipe(String recipeID, ItemStack input, ItemStack processing, List<SequenceStep> steps, int repetitions, SequenceResult result) {
        this.recipeID = recipeID;
        this.input = input;
        this.processing = processing;
        this.steps = steps;
        this.repetitions = repetitions;
        this.result = result;
        this.validate();
    }

    public SequenceRecipe(String recipeID, NBTTagCompound nbt) {
        this.recipeID = recipeID;
        this.input = new ItemStack(nbt.getCompoundTag("Input"));
        this.processing = new ItemStack(nbt.getCompoundTag("Processing"));
        NBTTagList stepsNBT = nbt.getTagList("Steps", 10);
        List<SequenceStep> list = new ArrayList<>();
        for (int i = 0; i < stepsNBT.tagCount(); i++) {
            NBTTagCompound stepNBT = stepsNBT.getCompoundTagAt(i);
            list.add(new SequenceStep(stepNBT.getString("name"), stepNBT.getCompoundTag("Data")));
        }
        this.steps = ImmutableList.copyOf(list);
        this.repetitions = Math.min(nbt.getInteger("repetitions"), 1);
        this.result = new SequenceResult(nbt.getCompoundTag("Result"));
        this.validate();
    }

    private void validate() {
        if (this.steps.isEmpty()) throw new RuntimeException("Stepless sequence recipe");
        if (this.repetitions <= 0) throw new RuntimeException("Stepless sequence recipe");
    }
    public SequenceStep getFirstStep() {
        return this.steps.get(0);
    }
    public SequenceStep getStep(int index) {
        return this.steps.get(index % this.steps.size());
    }

    public static boolean isInSequence(ItemStack processing) {
        return processing.getSubCompound("SequencedAssembly") != null;
    }
    public static SequenceStep getNextStep(ItemStack processing) {
        NBTTagCompound data = processing.getOrCreateSubCompound("SequencedAssembly");
        String id = data.getString("id");
        int step = data.getInteger("step");
        SequenceRecipe recipe = SequencedRecipes.instance.getRecipe(id);
        return recipe.steps.get(step % recipe.steps.size());
    }
    public static void initialize(ItemStack processing, String id) {
        NBTTagCompound data = processing.getOrCreateSubCompound("SequencedAssembly");
        data.setString("id", id);
        data.setInteger("step", 0);
    }
    public static ItemStack advance(ItemStack processing) {
        NBTTagCompound data = processing.getOrCreateSubCompound("SequencedAssembly");
        String id = data.getString("id");
        int nextStep = data.getInteger("step") + 1;
        SequenceRecipe recipe = SequencedRecipes.instance.getRecipe(id);
        int max = recipe.steps.size() * recipe.repetitions;
        if (nextStep >= max) {
            Random rnd = CreateLegacy.rand;
            if (rnd.nextFloat() < recipe.result.chance) {
                return recipe.result.expected.copy();
            } else {
                Object2FloatMap<ItemStack> waste = recipe.result.waste;
                float weight = 0.0F;
                for (float f : waste.values()) {
                    weight += f;
                }
                float selector = rnd.nextFloat() * weight;
                for (Object2FloatMap.Entry<ItemStack> entry : waste.object2FloatEntrySet()) {
                    if (selector < entry.getFloatValue()) {
                        return entry.getKey().copy();
                    }
                    selector -= entry.getFloatValue();
                }
            }
        }
        data.setInteger("step", nextStep);
        return processing;
    }

    private static final HashMap<String, Format> FORMATTERS = new HashMap<>();
    public static void registerFormat(String id, Format format) {
        FORMATTERS.put(id, format);
    }
    public static Format getFormat(String id) {
        return FORMATTERS.getOrDefault(id, Format.DEFAULT);
    }
    public interface Format {
        Format DEFAULT = (step) -> step.name;

        String getDisplayName(SequenceStep step);
    }
    static {
        registerFormat("pressing", (step) -> Localizer.translate("sequence.pressing"));
        registerFormat("deploying", (step) -> {
            ItemStack applied = new ItemStack(step.data.getCompoundTag("Applied"));
            return Localizer.translate("sequence.deploying", applied.getDisplayName());
        });
        registerFormat("cutting", (step) -> Localizer.translate("sequence.cutting"));
    }
}
