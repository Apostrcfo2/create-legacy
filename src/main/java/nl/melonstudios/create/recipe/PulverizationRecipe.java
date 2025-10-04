package nl.melonstudios.create.recipe;

import com.google.common.collect.ImmutableList;
import com.melonstudios.melonlib.misc.MetaItem;
import com.melonstudios.melonlib.predicates.StackPredicate;
import com.melonstudios.melonlib.predicates.StackPredicateMetaItem;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;

public final class PulverizationRecipe {
    public final String recipeID;
    public final StackPredicate input;
    public final List<Tuple<ItemStack, Float>> results;
    public final int processingTime;

    public PulverizationRecipe(String recipeID, StackPredicate input, List<Tuple<ItemStack, Float>> results, int processingTime) {
        this.recipeID = recipeID;
        this.input = input;
        this.results = results;
        this.processingTime = processingTime;
    }

    PulverizationRecipe(String recipeID, NBTTagCompound nbt) {
        this.recipeID = recipeID;
        this.input = new StackPredicateMetaItem(
                MetaItem.of(ForgeRegistries.ITEMS.getValue(
                                new ResourceLocation(nbt.getString("inputID"))),
                        nbt.getInteger("inputMeta")
                )
        );
        NBTTagList resultList = nbt.getTagList("Results", 10);
        ArrayList<Tuple<ItemStack, Float>> results = new ArrayList<>();
        for (int i = 0; i < resultList.tagCount(); i++) {
            NBTTagCompound data = resultList.getCompoundTagAt(i);
            float chance = data.hasKey("chance") ? data.getFloat("chance") : 1.0F;
            ItemStack stack = new ItemStack(data.getCompoundTag("Item"));
            results.add(new Tuple<>(stack, chance));
        }
        this.results = ImmutableList.copyOf(results);
        this.processingTime = nbt.hasKey("processingTime") ? nbt.getInteger("processingTime") : 100;
    }
}
