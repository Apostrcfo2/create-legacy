package nl.melonstudios.create.recipe.sequence;

import it.unimi.dsi.fastutil.objects.Object2FloatArrayMap;
import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;


public class SequenceResult {
    public final ItemStack expected;
    public final float chance;
    public final Object2FloatMap<ItemStack> waste;

    public SequenceResult(ItemStack expected, float chance, Object2FloatMap<ItemStack> waste) {
        this.expected = expected;
        this.chance = chance;
        this.waste = waste;
    }
    public SequenceResult(ItemStack expected, float chance, Object... waste) {
        this.expected = expected;
        this.chance = chance;
        if ((waste.length & 1) != 0) throw new IllegalArgumentException("Waste products must be in item-chance pairs");
        int amount = waste.length / 2;
        this.waste = new Object2FloatArrayMap<>(amount);
        for (int i = 0; i < amount; i++) {
            ItemStack wasteItem = (ItemStack) waste[i*2];
            float wasteChance = (float) waste[i*2+1];
            this.waste.put(wasteItem, wasteChance);
        }
    }
    public SequenceResult(ItemStack result) {
        this.expected = result;
        this.chance = 1.0F;
        this.waste = null;
    }
    public SequenceResult(NBTTagCompound nbt) {
        this.expected = new ItemStack(nbt.getCompoundTag("Result"));
        this.chance = nbt.getFloat("chance");
        NBTTagList list = nbt.getTagList("Waste", 10);
        if (this.chance < 1.0F && list.tagCount() > 0) {
            this.waste = new Object2FloatArrayMap<>();
            for (int i = 0; i < list.tagCount(); i++) {
                NBTTagCompound wasteNBT = list.getCompoundTagAt(i);
                ItemStack stack = new ItemStack(wasteNBT.getCompoundTag("Item"));
                float chance = wasteNBT.getFloat("chance");
                this.waste.put(stack, chance);
            }
        } else {
            this.waste = null;
        }
    }
}
