package nl.melonstudios.create.capability.fluid;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;

import javax.annotation.Nullable;

public class FluidTankFiltered extends FluidTank {
    protected final Fluid type;

    public FluidTankFiltered(int capacity, Fluid type) {
        super(capacity);
        this.type = type;
    }

    public FluidTankFiltered(@Nullable FluidStack fluidStack, int capacity, Fluid type) {
        super(fluidStack, capacity);
        this.type = type;
    }

    public FluidTankFiltered(Fluid fluid, int amount, int capacity, Fluid type) {
        super(fluid, amount, capacity);
        this.type = type;
    }

    public FluidTankFiltered(NBTTagCompound nbt) {
        this(FluidStack.loadFluidStackFromNBT(nbt), nbt.getInteger("capacity"), FluidRegistry.getFluid(nbt.getString("FluidName")));
    }
    public FluidTankFiltered(NBTTagCompound nbt, int capacity) {
        this(FluidStack.loadFluidStackFromNBT(nbt), capacity, FluidRegistry.getFluid(nbt.getString("FluidName")));
    }

    @Override
    public boolean canFillFluidType(FluidStack fluid) {
        return fluid != null && fluid.getFluid() == this.type;
    }
}
