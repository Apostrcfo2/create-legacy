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
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidHandlerConcatenate;
import nl.melonstudios.create.tileentity.marker.IInventoryDebloated;
import nl.melonstudios.create.tileentity.marker.ITileEntityWithSubInteractions;
import nl.melonstudios.create.tileentity.marker.ITopOpenInventory;
import nl.melonstudios.create.util.SubInteractionBox;
import nl.melonstudios.create.util.Utils;
import nl.melonstudios.create.util.filter.IItemFilter;
import nl.melonstudios.create.util.filter.ItemFilterExact;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;

public class TileEntityBasin extends TileEntityOptimizedBase implements IInventoryDebloated, ITileEntityWithSubInteractions, ITopOpenInventory {
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

    }

    @Override
    public void tickLazy() {

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

        if (this.tank1.getFluidAmount() > 0) {
            NBTTagCompound tank2NBT = new NBTTagCompound();
            this.tank2.writeToNBT(tank2NBT);
            nbt.setTag("Tank2", tank2NBT);
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

        if (this.tank1.getFluidAmount() > 0) {
            NBTTagCompound tank2NBT = new NBTTagCompound();
            this.tank2.writeToNBT(tank2NBT);
            nbt.setTag("Tank2", tank2NBT);
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
        return capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY || super.hasCapability(capability, facing);
    }

    @Nullable
    @Override
    @SuppressWarnings("unchecked")
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
            return (T)this.fluid;
        return super.getCapability(capability, facing);
    }

    @Override
    public String getName() {
        return "Basin";
    }

    @Override
    public int getSizeInventory() {
        return this.inventory.size()+1;
    }

    @Override
    public boolean isEmpty() {
        return this.inventory.isEmpty();
    }

    @Override
    public ItemStack getStackInSlot(int index) {
        return index == this.inventory.size() ? ItemStack.EMPTY : this.inventory.get(index);
    }

    @Override
    public ItemStack decrStackSize(int index, int count) {
        if (index == this.inventory.size()) return ItemStack.EMPTY;
        this.sync();
        ItemStack stack = this.inventory.get(index).splitStack(count);
        this.optimizeInventory();
        return stack;
    }

    @Override
    public ItemStack removeStackFromSlot(int index) {
        if (index == this.inventory.size()) return ItemStack.EMPTY;
        this.sync();
        return this.inventory.remove(index);
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        if (index == this.inventory.size()) {
            this.inventory.add(stack);
            return;
        }
        this.sync();
        this.inventory.set(index, stack);
        this.optimizeInventory();
    }

    @Override
    public int getInventoryStackLimit() {
        return 16;
    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        for (int i = 0; i < this.inventory.size(); i++) {
            if (Utils.itemMatches(this.inventory.get(i), stack) && i != index) return false;
        }
        return true;
    }

    @Override
    public void clear() {
        this.sync();
        this.inventory.clear();
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
        for (int i = 0; i < this.getSizeInventory(); i++) {
            ItemStack prev = this.getStackInSlot(i);
            if (prev.isEmpty()) {
                this.setInventorySlotContents(i, stack.splitStack(16));
                return stack;
            }
            if (ItemStack.areItemsEqual(prev, stack) && ItemStack.areItemStackTagsEqual(prev, stack)) {
                int space = Math.min(prev.getMaxStackSize(), this.getInventoryStackLimit());
                prev.grow(space);
                stack.shrink(space);
            }
            if (stack.isEmpty()) return ItemStack.EMPTY;
        }
        this.sync();
        return stack;
    }
}
