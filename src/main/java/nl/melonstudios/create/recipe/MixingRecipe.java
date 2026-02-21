package nl.melonstudios.create.recipe;

import com.google.common.collect.Lists;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import nl.melonstudios.create.tileentity.TileEntityBasin;
import nl.melonstudios.create.util.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MixingRecipe {
    public final String recipeID;
    public final FluidStack fluidIn1;
    public final FluidStack fluidIn2;
    public final List<ItemStack> requiredItems;
    public final FluidStack fluidOut;
    public final List<ItemStack> resultItems;
    public final int requiredHeat;
    public final int recipeTime;

    public MixingRecipe(String recipeID, FluidStack fluidIn1, FluidStack fluidIn2, List<ItemStack> requiredItems,
                        FluidStack fluidOut, List<ItemStack> resultItems, int requiredHeat, int recipeTime) {
        this.recipeID = recipeID;
        this.fluidIn1 = fluidIn1;
        this.fluidIn2 = fluidIn2;
        this.requiredItems = requiredItems;
        this.fluidOut = fluidOut;
        this.resultItems = resultItems;
        this.requiredHeat = requiredHeat;
        this.recipeTime = recipeTime;
    }

    public boolean matches(TileEntityBasin basin) {
        if (this.requiredHeat > basin.getHeat()) return false;
        FluidStack fluid1 = basin.tank1.getFluid();
        FluidStack fluid2 = basin.tank2.getFluid();
        boolean firstFluidFirst = true;
        if (this.fluidIn1 != null) {
            if (fluid1 == null) {
                if (fluid2 == null) return false;
                firstFluidFirst = false;
                if (!fluid2.containsFluid(this.fluidIn1)) return false;
            } else {
                if (!fluid1.containsFluid(this.fluidIn1)) {
                    firstFluidFirst = false;
                    if (fluid2 == null || !fluid2.containsFluid(this.fluidIn1)) return false;
                }
            }
        }
        if (this.fluidIn2 != null) {
            if (firstFluidFirst) {
                if (fluid2 == null) return false;
                if (!fluid2.containsFluid(this.fluidIn2)) return false;
            } else {
                if (fluid1 == null) return false;
                if (!fluid1.containsFluid(this.fluidIn2)) return false;
            }
        }
        if (!this.requiredItems.isEmpty()) {
            for (ItemStack require : this.requiredItems) {
                int c = require.getCount();
                for (ItemStack stack : basin.inventory) {
                    if (Utils.itemMatches(require, stack)) {
                        c -= stack.getCount();
                    }
                }
                if (c > 0) return false;
            }
        }
        return true;
    }
    public boolean checkOutputSpace(TileEntityBasin basin) {
        if (this.fluidOut != null) {
            FluidStack fluid = basin.tank3.getFluid();
            if (fluid != null && fluid.amount + this.fluidOut.amount > 1000) return false;
        }
        if (!this.resultItems.isEmpty()) {
            for (ItemStack stack : this.resultItems) {
                for (ItemStack inv : basin.inventory) {
                    if (ItemStack.areItemsEqual(stack, inv) && ItemStack.areItemStackTagsEqual(stack, inv)) {
                        if (stack.getCount() + inv.getCount() > 16) return false;
                    }
                }
            }
        }
        return true;
    }
    public boolean removeRequiredInput(TileEntityBasin basin) {
        if (this.requiredHeat > basin.getHeat()) return false;
        FluidStack fluid1 = basin.tank1.getFluid();
        FluidStack fluid2 = basin.tank2.getFluid();
        boolean firstFluidFirst = true;
        if (this.fluidIn1 != null) {
            if (fluid1 == null || !fluid1.containsFluid(this.fluidIn1)) {
                firstFluidFirst = false;
                basin.tank2.drainInternal(this.fluidIn1, true);
            } else {
                basin.tank1.drainInternal(this.fluidIn1, true);
            }
        }
        if (this.fluidIn2 != null) {
            if (firstFluidFirst) {
                basin.tank2.drainInternal(this.fluidIn2, true);
            } else {
                basin.tank1.drainInternal(this.fluidIn2, true);
            }
        }
        if (!this.requiredItems.isEmpty()) {
            for (ItemStack require : this.requiredItems) {
                int c = require.getCount();
                for (ItemStack stack : basin.inventory) {
                    if (Utils.itemMatches(require, stack)) {
                        int rem = stack.getCount();
                        stack.shrink(Math.min(c, rem));
                        c = Math.max(c - rem, 0);
                    }
                }
            }
        }
        basin.sync();
        return true;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private FluidStack fluidIn1, fluidIn2;
        private List<ItemStack> requiredItems = Collections.emptyList();
        private FluidStack fluidOut;
        private List<ItemStack> resultItems = Collections.emptyList();
        private int requiredHeat = 0;
        private int recipeTime = 5120;

        public MixingRecipe build(String recipeID) {
            return new MixingRecipe(recipeID, this.fluidIn1, this.fluidIn2, this.requiredItems, this.fluidOut, this.resultItems, this.requiredHeat, this.recipeTime);
        }

        public Builder setInputFluids(FluidStack... fluids) {
            if (fluids.length == 0) {
                this.fluidIn1 = null;
                this.fluidIn2 = null;
                return this;
            } else if (fluids.length == 1) {
                this.fluidIn1 = fluids[0];
                this.fluidIn2 = null;
                return this;
            } else {
                this.fluidIn1 = fluids[0];
                this.fluidIn2 = fluids[1];
                return this;
            }
        }
        public Builder setInputItems(ItemStack... items) {
            this.requiredItems = items.length > 0 ? Lists.newArrayList(items) : Collections.emptyList();
            return this;
        }
        public Builder setOutputFluid(FluidStack fluid) {
            this.fluidOut = fluid;
            return this;
        }
        public Builder setOutputItems(ItemStack... items) {
            this.resultItems = items.length > 0 ? Lists.newArrayList(items) : Collections.emptyList();
            return this;
        }
        public Builder setRequiredHeat(int heat) {
            this.requiredHeat = heat;
            return this;
        }
        public Builder setRecipeTime(int ticks) {
            this.recipeTime = ticks;
            return this;
        }
        public Builder setRecipeTime64RPM(int ticks) {
            this.recipeTime = ticks * 64;
            return this;
        }
    }
}
