package nl.melonstudios.create.recipe;

import com.melonstudios.melonlib.misc.StackUtil;
import com.melonstudios.melonlib.recipe.FluidIngredient;
import com.melonstudios.melonlib.recipe.Ingredient;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.oredict.OreDictionary;
import nl.melonstudios.create.tileentity.TileEntityBasin;
import nl.melonstudios.create.util.filter.IItemFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MixingRecipe {
    public final List<Ingredient> itemInputs;
    public final List<FluidIngredient> fluidInputs;
    public final List<ItemStack> itemOutputs;
    public final List<FluidStack> fluidOutputs;
    public final int requiredHeat;
    public final int processingTime;

    public MixingRecipe(
            List<Ingredient> itemInputs,
            List<FluidIngredient> fluidInputs,
            List<ItemStack> itemOutputs,
            List<FluidStack> fluidOutputs,
            int requiredHeat,
            int processingTime
    ) {
        this.itemInputs = itemInputs;
        this.fluidInputs = fluidInputs;
        this.itemOutputs = itemOutputs;
        this.fluidOutputs = fluidOutputs;
        this.requiredHeat = requiredHeat;
        this.processingTime = processingTime;
    }

    private static void serializeFluid(FluidStack stack, ByteBuf buf) {
        String id = FluidRegistry.getFluidName(stack);
        int amount = stack.amount;
        NBTTagCompound tag = stack.tag;
        buf.writeInt(id.length());
        buf.writeCharSequence(id, StandardCharsets.UTF_8);
        buf.writeInt(amount);
        if (tag != null) {
            buf.writeBoolean(true);
            new PacketBuffer(buf).writeCompoundTag(tag);
        } else {
            buf.writeBoolean(false);
        }
    }
    private static FluidStack readFluid(ByteBuf buf) throws IOException {
        int len = buf.readInt();
        String id = buf.readCharSequence(len, StandardCharsets.UTF_8).toString();
        int amount = buf.readInt();
        NBTTagCompound nbt = buf.readBoolean() ? new PacketBuffer(buf).readCompoundTag() : null;
        return new FluidStack(FluidRegistry.getFluid(id), amount, nbt);
    }

    public void write(ByteBuf buf) {
        buf.writeInt(this.itemInputs.size());
        for (Ingredient input : this.itemInputs) input.serialize(buf);
        buf.writeInt(this.fluidInputs.size());
        for (FluidIngredient input : this.fluidInputs) input.serialize(buf);
        buf.writeInt(this.itemOutputs.size());
        for (ItemStack stack : this.itemOutputs) StackUtil.writeItemStack(stack, buf, true, true);
        buf.writeInt(this.fluidOutputs.size());
        for (FluidStack stack : this.fluidOutputs) serializeFluid(stack, buf);
        buf.writeByte(this.requiredHeat);
        buf.writeInt(this.processingTime);
    }
    public static MixingRecipe read(ByteBuf buf) throws IOException {
        int itemInputsLen = buf.readInt();
        List<Ingredient> itemInputs = new ArrayList<>(itemInputsLen);
        for (int i = 0; i < itemInputsLen; i++) {
            itemInputs.add(Ingredient.read(buf));
        }
        int fluidInputsLen = buf.readInt();
        List<FluidIngredient> fluidInputs = new ArrayList<>(fluidInputsLen);
        for (int i = 0; i < fluidInputsLen; i++) {
            fluidInputs.add(FluidIngredient.read(buf));
        }
        int itemOutputsLen = buf.readInt();
        List<ItemStack> itemOutputs = new ArrayList<>(itemOutputsLen);
        for (int i = 0; i < itemOutputsLen; i++) {
            itemOutputs.add(StackUtil.readItemStack(buf, true, true));
        }
        int fluidOutputsLen = buf.readInt();
        List<FluidStack> fluidOutputs = new ArrayList<>(fluidOutputsLen);
        for (int i = 0; i < fluidOutputsLen; i++) {
            fluidOutputs.add(readFluid(buf));
        }
        int requiredHeat = buf.readUnsignedByte();
        int processingTime = buf.readInt();
        return new MixingRecipe(itemInputs, fluidInputs, itemOutputs, fluidOutputs, requiredHeat, processingTime);
    }

    public boolean checkFilter(IItemFilter filter) {
        for (ItemStack stack : this.itemOutputs) {
            if (filter.matches(stack)) return true;
        }
        for (FluidStack stack : this.fluidOutputs) {
            if (filter.matches(stack)) return true;
        }
        return false;
    }

    public boolean matches(TileEntityBasin basin) {
        if (this.requiredHeat > basin.getHeat()) return false;
        if (basin.recipeFilter == null || this.checkFilter(basin.recipeFilter)) {
            if (!this.itemInputs.isEmpty()) {
                List<Ingredient> test = new ArrayList<>(this.itemInputs);
                loop:
                for (ItemStack stack : basin.inventory) {
                    while (!test.isEmpty()) {
                        Ingredient ingredient = test.remove(0);
                        if (ingredient.matches(stack)) {
                            continue loop;
                        }
                        test.add(ingredient);
                    }
                }
                if (!test.isEmpty()) return false;
            }
            if (!this.fluidInputs.isEmpty()) {
                List<FluidIngredient> test = new ArrayList<>(this.fluidInputs);
                loop:
                for (FluidTank tank : basin.fluid.getHandlers()) {
                    FluidStack stack = tank.getFluid();
                    if (stack == null) throw new IllegalStateException("How is the fluid stack null? Please optimize fluid pool!");
                    while (!test.isEmpty()) {
                        FluidIngredient ingredient = test.remove(0);
                        if (ingredient.matches(stack)) {
                            continue loop;
                        }
                        test.add(ingredient);
                    }
                }
                if (!test.isEmpty()) return false;
            }
            return true;
        }
        return false;
    }
    public boolean checkOutputSpace(TileEntityBasin basin) {
        if (basin == null) return false;
        TileEntityBasin copy = basin.copyForTesting();
        if (!this.removeRequiredInput(copy)) throw new IllegalStateException("Required input not available?");
        for (ItemStack stack : this.itemOutputs) {
            if (!basin.tryInsertItem(stack.copy()).isEmpty()) return false;
        }
        for (FluidStack stack : this.fluidOutputs) {
            int amount = basin.fluid.fill(stack, true);
            if (amount < stack.amount) return false;
        }
        return true;
    }
    public boolean removeRequiredInput(TileEntityBasin basin) {
        if (!this.itemInputs.isEmpty()) {
            List<Ingredient> test = new ArrayList<>(this.itemInputs);
            loop:
            for (ItemStack stack : basin.inventory) {
                while (!test.isEmpty()) {
                    Ingredient ingredient = test.remove(0);
                    if (ingredient.matches(stack)) {
                        stack.shrink(1);
                        continue loop;
                    }
                    test.add(ingredient);
                }
            }
            if (!test.isEmpty()) return false;
        }
        if (!this.fluidInputs.isEmpty()) {
            List<FluidIngredient> test = new ArrayList<>(this.fluidInputs);
            loop:
            for (FluidTank tank : basin.fluid.getHandlers()) {
                FluidStack stack = tank.getFluid();
                if (stack == null) throw new IllegalStateException("How is the fluid stack null? Please optimize fluid pool!");
                while (!test.isEmpty()) {
                    FluidIngredient ingredient = test.remove(0);
                    if (ingredient.matches(stack)) {
                        stack.amount -= ingredient.getDisplayFluids().get(0).amount;
                        continue loop;
                    }
                    test.add(ingredient);
                }
            }
            if (!test.isEmpty()) return false;
        }
        basin.optimizeInventory();
        return true;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private List<Ingredient> itemInputs = Collections.emptyList();
        private List<FluidIngredient> fluidInputs = Collections.emptyList();
        private List<ItemStack> itemOutputs = Collections.emptyList();
        private List<FluidStack> fluidOutputs = Collections.emptyList();
        private int requiredHeat = 0;
        private int processingTime = 5120;

        private Builder() {

        }

        public MixingRecipe build() {
            return new MixingRecipe(this.itemInputs, this.fluidInputs, this.itemOutputs, this.fluidOutputs, this.requiredHeat, this.processingTime);
        }

        public Builder setItemInputs(Object... inputs) {
            this.itemInputs = new ArrayList<>(inputs.length);
            for (Object param : inputs) {
                if (param instanceof Ingredient) {
                    this.itemInputs.add((Ingredient) param);
                } else if (param instanceof ItemStack) {
                    ItemStack input = (ItemStack) param;
                    if (input.isEmpty()) throw new IllegalArgumentException("ItemStack input cannot be empty");
                    for (int i = 0; i < input.getCount(); i++) {
                        this.itemInputs.add(Ingredient.of(input, false));
                    }
                } else if (param instanceof Item) {
                    this.itemInputs.add(Ingredient.of(new ItemStack((Item) param, 1, OreDictionary.WILDCARD_VALUE), false));
                } else if (param instanceof Block) {
                    this.itemInputs.add(Ingredient.of(new ItemStack((Block) param, 1, OreDictionary.WILDCARD_VALUE), false));
                } else if (param instanceof String) {
                    this.itemInputs.add(Ingredient.of((String) param));
                } else {
                    throw new IllegalArgumentException("Invalid item input: " + param + " (" + param.getClass().getSimpleName() + ")");
                }
            }
            return this;
        }
        public Builder setFluidInputs(Object... inputs) {
            this.fluidInputs = new ArrayList<>(inputs.length);
            for (Object param : inputs) {
                if (param instanceof FluidIngredient) {
                    this.fluidInputs.add((FluidIngredient) param);
                } else if (param instanceof FluidStack) {
                    this.fluidInputs.add(FluidIngredient.of((FluidStack) param));
                } else {
                    throw new IllegalArgumentException("Invalid fluid input: " + param + " (" + param.getClass().getSimpleName() + ")");
                }
            }
            return this;
        }
        public Builder setItemOutputs(Object... outputs) {
            this.itemOutputs = new ArrayList<>(outputs.length);
            for (Object param : outputs) {
                if (param instanceof ItemStack) {
                    this.itemOutputs.add((ItemStack) param);
                } else if (param instanceof Item) {
                    this.itemOutputs.add(new ItemStack((Item) param, 1, 0));
                } else if (param instanceof Block) {
                    this.itemOutputs.add(new ItemStack((Block) param, 1, 0));
                } else {
                    throw new IllegalArgumentException("Invalid item output: " + param + " (" + param.getClass().getSimpleName() + ")");
                }
            }
            return this;
        }
        public Builder setFluidOutputs(Object... outputs) {
            this.fluidOutputs = new ArrayList<>(outputs.length);
            for (Object param : outputs) {
                if (param instanceof FluidStack) {
                    this.fluidOutputs.add((FluidStack) param);
                } else {
                    throw new IllegalArgumentException("Invalid fluid output: " + param + " (" + param.getClass().getSimpleName() + ")");
                }
            }
            return this;
        }
        public Builder setRequiredHeat(int heat) {
            this.requiredHeat = heat;
            return this;
        }
        public Builder setProcessingTime(int ticks) {
            this.processingTime = ticks;
            return this;
        }
        public Builder setProcessingTime64RPM(int ticks) {
            this.processingTime = ticks * 64;
            return this;
        }
    }
}
