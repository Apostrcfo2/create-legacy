package nl.melonstudios.create.tileentity;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidHandlerConcatenate;
import nl.melonstudios.create.tileentity.marker.IInventoryDebloated;
import nl.melonstudios.create.util.Utils;

import javax.annotation.Nullable;

public class TileEntityBasin extends TileEntityOptimizedBase implements IInventoryDebloated {
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

    public TileEntityBasin() {
        this.setTickRateLazy(Integer.MAX_VALUE);
    }

    public boolean hasAnyFluid() {
        return this.tank1.getFluidAmount() > 0 || this.tank2.getFluidAmount() > 0 || this.tank3.getFluidAmount() > 0;
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
}
