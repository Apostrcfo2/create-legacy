package nl.melonstudios.create.tileentity;

import com.google.common.collect.ImmutableList;
import com.melonstudios.melonlib.misc.StackUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidHandlerConcatenate;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import nl.melonstudios.create.recipe.MixingRecipe;
import nl.melonstudios.create.tileentity.marker.ITileEntityWithSubInteractions;
import nl.melonstudios.create.tileentity.marker.ITopOpenInventory;
import nl.melonstudios.create.util.SubInteractionBox;
import nl.melonstudios.create.util.filter.IItemFilter;
import nl.melonstudios.create.util.filter.ItemFilterExact;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;

public class TileEntityBasin extends TileEntityOptimizedBase implements ITileEntityWithSubInteractions, ITopOpenInventory, IItemHandler {
    public final FluidTank tank1 = new FluidTank(1000) {
        @Override
        public boolean canFillFluidType(FluidStack fluid) {
            FluidStack comparator = TileEntityBasin.this.tank2.getFluid();
            if (comparator == null || comparator.amount <= 0) return true;
            return !comparator.isFluidEqual(fluid);
        }

        @Override
        protected void onContentsChanged() {
            TileEntityBasin.this.sync();
        }
    };
    public final FluidTank tank2 = new FluidTank(1000) {
        @Override
        public boolean canFillFluidType(FluidStack fluid) {
            FluidStack comparator = TileEntityBasin.this.tank1.getFluid();
            if (comparator == null || comparator.amount <= 0) return false;
            return !comparator.isFluidEqual(fluid);
        }

        @Override
        public boolean canDrainFluidType(@Nullable FluidStack fluid) {
            return false;
        }
        @Override
        public boolean canDrain() {
            return false;
        }

        @Override
        protected void onContentsChanged() {
            TileEntityBasin.this.sync();
        }
    };
    public final FluidTank tank3 = new FluidTank(1000) {
        @Override
        public boolean canFillFluidType(FluidStack fluid) {
            return false;
        }

        @Override
        public boolean canFill() {
            return false;
        }

        @Override
        protected void onContentsChanged() {
            TileEntityBasin.this.sync();
        }
    };
    public final FluidHandlerConcatenate fluid = new FluidHandlerConcatenate(this.tank1, this.tank2, this.tank3);
    public final NonNullList<ItemStack> inventory = NonNullList.create();
    public IItemFilter recipeFilter = null;

    public TileEntityBasin() {
        this.setTickRateLazy(Integer.MAX_VALUE);

        this.subInteractionBoxes = ImmutableList.of(
                SubInteractionBox.Helper.createDefaultAt(0.0F, 0.75F, 0.5F, this::setRecipeFilter),
                SubInteractionBox.Helper.createDefaultAt(1.0F, 0.75F, 0.5F, this::setRecipeFilter),
                SubInteractionBox.Helper.createDefaultAt(0.5F, 0.75F, 1.0F, this::setRecipeFilter),
                SubInteractionBox.Helper.createDefaultAt(0.5F, 0.75F, 0.0F, this::setRecipeFilter)
        );
    }

    public boolean hasAnyFluid() {
        return this.tank1.getFluidAmount() > 0 || this.tank2.getFluidAmount() > 0 || this.tank3.getFluidAmount() > 0;
    }
    public boolean setRecipeFilter(@Nullable EntityPlayer player, boolean sneaking, ItemStack held) {
        if (held.isEmpty()) {
            this.recipeFilter = null;
        } else {
            this.recipeFilter = new ItemFilterExact(held);
        }
        this.sync();
        if (player != null) {
            if (held.isEmpty()) {
                player.playSound(SoundEvents.ENTITY_ITEMFRAME_REMOVE_ITEM, 1.0F, 1.0F);
                player.sendStatusMessage(new TextComponentString("Cleared recipe filter"), true);
            } else {
                player.playSound(SoundEvents.ENTITY_ITEMFRAME_ADD_ITEM, 1.0F, 1.0F);
                player.sendStatusMessage(new TextComponentString("Set recipe filter to " + held.getDisplayName()), true);
            }
        }
        return true;
    }

    @Override
    public void tick() {
        this.itemRotationOld = this.itemRotation;
        this.itemRotation += this.addedItemRotation;
        this.addedItemRotation = 0;
    }

    @Override
    public void tickLazy() {

    }

    public int getHeat() {
        return 0;
    }

    public int itemRotation = 0;
    public int itemRotationOld = 0;
    public int addedItemRotation = 0;

    //TODO: Add the output spout thingamajig
    public void dumpRecipeResults(@Nullable FluidStack[] fluids, ItemStack... items) {
        if (fluids != null) {
            if (fluids.length > 1)
                throw new IllegalArgumentException("More than 1 output fluid is currently unsupported");
            FluidStack fluid = fluids.length > 0 ? fluids[0] : null;
            if (fluid != null) {
                if (this.tank3.getFluidAmount() > 0) {
                    this.tank3.fillInternal(fluid, true);
                } else this.tank3.setFluid(fluid);
            }
        }
        loop:
        for (ItemStack stack : items) {
            for (ItemStack pre : this.inventory) {
                if (ItemStack.areItemsEqual(pre, stack) && ItemStack.areItemStackTagsEqual(pre, stack)) {
                    pre.grow(stack.getCount());
                    continue loop;
                }
            }
            this.inventory.add(stack.copy());
        }
        this.sync();
    }

    public void dumpRecipeResults(MixingRecipe recipe) {
        this.dumpRecipeResults(
                recipe.fluidOut != null ? new FluidStack[]{recipe.fluidOut.copy()} : null,
                recipe.resultItems.toArray(new ItemStack[0])
        );
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        this.optimizeInventory();
        super.writeToNBT(nbt);

        if (this.tank1.getFluidAmount() > 0) {
            NBTTagCompound tank1NBT = new NBTTagCompound();
            this.tank1.writeToNBT(tank1NBT);
            nbt.setTag("Tank1", tank1NBT);
        }

        if (this.tank2.getFluidAmount() > 0) {
            NBTTagCompound tank2NBT = new NBTTagCompound();
            this.tank2.writeToNBT(tank2NBT);
            nbt.setTag("Tank2", tank2NBT);
        }

        if (this.tank3.getFluidAmount() > 0) {
            NBTTagCompound tank3NBT = new NBTTagCompound();
            this.tank3.writeToNBT(tank3NBT);
            nbt.setTag("Tank3", tank3NBT);
        }

        if (!this.inventory.isEmpty()) {
            NBTTagList list = new NBTTagList();
            for (ItemStack stack : this.inventory) {
                list.appendTag(stack.writeToNBT(new NBTTagCompound()));
            }
            nbt.setTag("Inventory", list);
        }

        return nbt;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);

        if (nbt.hasKey("Tank1", 10)) {
            this.tank1.readFromNBT(nbt.getCompoundTag("Tank1"));
        } else this.tank1.setFluid(null);

        if (nbt.hasKey("Tank2", 10)) {
            this.tank2.readFromNBT(nbt.getCompoundTag("Tank2"));
        } else this.tank2.setFluid(null);

        if (nbt.hasKey("Tank3", 10)) {
            this.tank3.readFromNBT(nbt.getCompoundTag("Tank3"));
        } else this.tank3.setFluid(null);

        this.inventory.clear();
        if (nbt.hasKey("Inventory", 9)) {
            NBTTagList list = nbt.getTagList("Inventory", 10);
            for (int i = 0; i < list.tagCount(); i++) {
                this.inventory.add(new ItemStack(list.getCompoundTagAt(i)));
            }
        }
        this.optimizeInventory();
    }

    @Override
    public NBTTagCompound writePacket() {
        this.optimizeInventory();
        NBTTagCompound nbt = new NBTTagCompound();

        if (this.tank1.getFluidAmount() > 0) {
            NBTTagCompound tank1NBT = new NBTTagCompound();
            this.tank1.writeToNBT(tank1NBT);
            nbt.setTag("Tank1", tank1NBT);
        }

        if (this.tank2.getFluidAmount() > 0) {
            NBTTagCompound tank2NBT = new NBTTagCompound();
            this.tank2.writeToNBT(tank2NBT);
            nbt.setTag("Tank2", tank2NBT);
        }

        if (this.tank3.getFluidAmount() > 0) {
            NBTTagCompound tank3NBT = new NBTTagCompound();
            this.tank3.writeToNBT(tank3NBT);
            nbt.setTag("Tank3", tank3NBT);
        }

        if (!this.inventory.isEmpty()) {
            NBTTagList list = new NBTTagList();
            for (ItemStack stack : this.inventory) {
                list.appendTag(stack.writeToNBT(new NBTTagCompound()));
            }
            nbt.setTag("Inventory", list);
        }

        return nbt;
    }

    @Override
    public void readPacket(NBTTagCompound nbt) {
        if (nbt.hasKey("Tank1", 10)) {
            this.tank1.readFromNBT(nbt.getCompoundTag("Tank1"));
        } else this.tank1.setFluid(null);

        if (nbt.hasKey("Tank2", 10)) {
            this.tank2.readFromNBT(nbt.getCompoundTag("Tank2"));
        } else this.tank2.setFluid(null);

        if (nbt.hasKey("Tank3", 10)) {
            this.tank3.readFromNBT(nbt.getCompoundTag("Tank3"));
        } else this.tank3.setFluid(null);

        this.inventory.clear();
        if (nbt.hasKey("Inventory", 9)) {
            NBTTagList list = nbt.getTagList("Inventory", 10);
            for (int i = 0; i < list.tagCount(); i++) {
                this.inventory.add(new ItemStack(list.getCompoundTagAt(i)));
            }
        }
        this.optimizeInventory();
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
        return capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY || capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || super.hasCapability(capability, facing);
    }

    @Nullable
    @Override
    @SuppressWarnings("unchecked")
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
            return (T)this.fluid;
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
            return (T)this;
        return super.getCapability(capability, facing);
    }

    public void optimizeInventory() {
        this.inventory.removeIf(ItemStack::isEmpty);
    }

    @Override
    public void destroy() {
        StackUtil.dropItemsAt(this.world, this.pos, this.inventory.toArray(new ItemStack[0]));
    }

    private final List<SubInteractionBox> subInteractionBoxes;
    @Override
    public Collection<SubInteractionBox> getSubInteractionBoxes() {
        return this.subInteractionBoxes;
    }

    @Override
    public ItemStack tryInsertItem(ItemStack stack) {
        this.sync();
        for (int i = 0; i < this.inventory.size(); i++) {
            ItemStack prev = this.inventory.get(i);
            if (prev.isEmpty()) {
                this.inventory.set(i, stack.splitStack(16));
                return stack;
            }
            if (ItemStack.areItemsEqual(prev, stack) && ItemStack.areItemStackTagsEqual(prev, stack)) {
                int space = Math.min(prev.getMaxStackSize(), 16);
                prev.grow(space);
                stack.shrink(space);
            }
            if (stack.isEmpty()) return ItemStack.EMPTY;
        }
        this.inventory.add(stack.splitStack(16));
        return stack;
    }

    @Override
    public int getSlots() {
        return this.inventory.size()+1;
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        return slot == this.inventory.size() ? ItemStack.EMPTY : this.inventory.get(slot);
    }

    @Override
    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
        if (stack.isEmpty()) return ItemStack.EMPTY;
        ItemStack copy = stack.copy();
        if (slot == this.inventory.size()) {
            if (this.mayInsertNewItem(copy)) {
                if (simulate) {
                    copy.splitStack(this.getSlotLimit(slot));
                    return copy;
                }
                this.inventory.add(copy.splitStack(this.getSlotLimit(slot)));
                this.sync();
            }
            return copy;
        }
        ItemStack prev = this.inventory.get(slot);
        if (ItemStack.areItemsEqual(prev, copy) && ItemStack.areItemStackTagsEqual(prev, copy)) {
            int space = Math.min(prev.getMaxStackSize(), this.getSlotLimit(slot)) - prev.getCount();
            if (space > 0) {
                ItemStack ret = copy.splitStack(space);
                if (simulate) {
                    return copy;
                }
                prev.grow(ret.getCount());
                this.sync();
                return copy;
            }
        }
        return copy;
    }

    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (amount == 0 || slot == this.inventory.size()) return ItemStack.EMPTY;
        ItemStack prev = this.inventory.get(slot);
        if (simulate) {
            return prev.copy().splitStack(amount);
        }
        this.sync();
        return prev.splitStack(amount);
    }

    @Override
    public int getSlotLimit(int slot) {
        return 16;
    }

    private boolean mayInsertNewItem(ItemStack stack) {
        for (ItemStack prev : this.inventory) {
            if (ItemStack.areItemsEqual(prev, stack) && ItemStack.areItemStackTagsEqual(prev, stack)) {
                return false;
            }
        }
        return true;
    }
}
