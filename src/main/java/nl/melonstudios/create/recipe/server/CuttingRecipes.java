package nl.melonstudios.create.recipe.server;

import com.melonstudios.melonlib.recipe.*;
import io.netty.buffer.ByteBuf;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import nl.melonstudios.create.CreateLegacy;
import nl.melonstudios.create.init.RecipeInit;
import nl.melonstudios.create.recipe.CuttingRecipe;
import nl.melonstudios.create.util.filter.IItemFilter;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.util.*;

public class CuttingRecipes implements ISyncedRecipeType<CuttingRecipe> {
    public static final CuttingRecipes instance = new CuttingRecipes();

    private CuttingRecipes() {

    }

    public final HashMap<String, CuttingRecipe> recipes = new HashMap<>();

    @Override
    public void addRecipe(@Nonnull String recipeID, @Nonnull CuttingRecipe recipe) {
        this.recipes.put(recipeID, recipe);
    }

    public void addRecipe(@Nonnull String recipeID, @Nonnull Ingredient input, @Nonnull ItemStack result, int recipeTime) {
        this.addRecipe(recipeID, new CuttingRecipe(input, result, recipeTime));
    }

    @Override
    public final CuttingRecipe getRecipe(@Nonnull String recipeID) {
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
    public Collection<CuttingRecipe> getAllRecipes() {
        return this.recipes.values();
    }

    @Nonnull
    @Override
    public Map<String, CuttingRecipe> getRecipeMap() {
        return this.recipes;
    }

    @Nonnull
    @Override
    public CuttingRecipe convert(@Nonnull UniversalRecipe universal) throws RecipeException {
        try {
            Ingredient input = universal.itemInputs.get(0).get(0);
            ItemStack result = universal.itemOutputs.get(0).get(0);
            int time = universal.extraData.getInteger("processingTime");
            return new CuttingRecipe(input, result, time);
        } catch (Throwable e) {
            throw new RecipeException(e);
        }
    }

    public static String getRecipeForInput(ItemStack input, @Nullable IItemFilter recipeFilter) {
        IRecipeAccessor<CuttingRecipe> accessor = RecipeInit.getCuttingRecipes();
        List<String> candidates = new ArrayList<>();
        for (String recipeID : accessor.getAllRecipeIDs()) {
            CuttingRecipe recipe = accessor.getRecipe(recipeID);
            if (recipe == null) continue;
            if (recipe.input.matches(input)) {
                if (recipeFilter == null || recipeFilter.matches(recipe.result)) {
                    candidates.add(recipeID);
                }
            }
        }
        if (candidates.isEmpty()) return null;
        return candidates.get(CreateLegacy.rand.nextInt(candidates.size()));
    }

    @Override
    public void write(CuttingRecipe recipe, ByteBuf buf) throws IOException {
        recipe.input.serialize(buf);
        ItemStack result = recipe.result;
        buf.writeInt(Item.getIdFromItem(result.getItem()));
        buf.writeByte(result.getCount());
        buf.writeShort(result.getItemDamage());
        NBTTagCompound itemNBT = result.getTagCompound();
        if (itemNBT != null) {
            buf.writeBoolean(true);
            new PacketBuffer(buf).writeCompoundTag(itemNBT);
        } else {
            buf.writeBoolean(false);
        }
        buf.writeInt(recipe.processingTime);
    }

    @Override
    public CuttingRecipe read(String recipeID, ByteBuf buf) throws IOException {
        Ingredient input = Ingredient.read(buf);
        Item item = Item.getItemById(buf.readInt());
        int count = buf.readUnsignedByte();
        int damage = buf.readUnsignedShort();
        boolean hasNBT = buf.readBoolean();
        NBTTagCompound itemNBT = hasNBT ? new PacketBuffer(buf).readCompoundTag() : null;
        ItemStack result = new ItemStack(item, count, damage, itemNBT);
        int time = buf.readInt();
        return new CuttingRecipe(input, result, time);
    }
}
