package nl.melonstudios.create.capability.fluid;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;

import javax.annotation.Nullable;
import java.util.*;

public class FluidHandlerBasin implements IFluidHandler {
    protected final List<FluidTank> handlers;

    public FluidHandlerBasin(List<FluidTank> handlers) {
        this.handlers = handlers;
    }
    public FluidHandlerBasin() {
        this.handlers = new LinkedList<>();
    }

    @Override
    public IFluidTankProperties[] getTankProperties() {
        if (this.handlers.isEmpty()) return new IFluidTankProperties[0];
        List<IFluidTankProperties> properties = new ArrayList<>();
        for (FluidTank tank : this.handlers) {
            Collections.addAll(properties, tank.getTankProperties());
        }
        return properties.toArray(new IFluidTankProperties[0]);
    }

    public List<FluidTank> getHandlers() {
        return this.handlers;
    }

    @Override
    public int fill(FluidStack resource, boolean doFill) {
        if (resource == null || resource.amount <= 0) return 0;
        FluidTank tank = null;
        synchronized (this.handlers) {
            for (FluidTank ft : this.handlers) {
                if (resource.isFluidEqual(ft.getFluid())) {
                    tank = ft;
                    break;
                }
            }
            if (tank == null) {
                tank = new FluidTankFiltered(null, 1000, resource.getFluid());
                this.handlers.add(tank);
            }
        }
        return tank.fill(resource, doFill);
    }

    @Nullable
    @Override
    public FluidStack drain(FluidStack resource, boolean doDrain) {
        if (resource == null || resource.amount <= 0) return null;
        synchronized (this.handlers) {
            for (FluidTank tank : this.handlers) {
                if (resource.isFluidEqual(tank.getFluid())) return tank.drain(resource, doDrain);
            }
        }
        return null;
    }

    @Nullable
    @Override
    public FluidStack drain(int maxDrain, boolean doDrain) {
        if (maxDrain <= 0) return null;
        synchronized (this.handlers) {
            for (FluidTank tank : this.handlers) {
                FluidStack drain = tank.drain(maxDrain, doDrain);
                if (drain != null) return drain;
            }
        }
        return null;
    }

    public void optimize() {
        if (this.handlers.isEmpty()) return;
        this.handlers.removeIf(tank -> tank.getFluidAmount() <= 0);
    }

    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        synchronized (this.handlers) {
            this.optimize();
            if (!this.handlers.isEmpty()) {
                NBTTagList tanksNBT = new NBTTagList();
                for (FluidTank tank : this.handlers) {
                    tanksNBT.appendTag(tank.writeToNBT(new NBTTagCompound()));
                }
                nbt.setTag("Tanks", tanksNBT);
            }
        }
        return nbt;
    }
    public FluidHandlerBasin readFromNBT(NBTTagCompound nbt) {
        synchronized (this.handlers) {
            this.handlers.clear();
            if (nbt.hasKey("Tanks", 9)) {
                NBTTagList tanksNBT = nbt.getTagList("Tanks", 10);
                for (int i = 0; i < tanksNBT.tagCount(); i++) {
                    NBTTagCompound tankNBT = tanksNBT.getCompoundTagAt(i);
                    FluidStack stack = FluidStack.loadFluidStackFromNBT(tankNBT);
                    if (stack == null || stack.amount <= 0) continue;
                    FluidTank tank = new FluidTankFiltered(stack, 1000, stack.getFluid());
                    this.handlers.add(tank);
                }
            }
        }
        return this;
    }

    public boolean containsFluid(FluidStack resource) {
        return this.handlers.stream().map(FluidTank::getFluid).anyMatch(resource::containsFluid);
    }
}
