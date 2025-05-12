package nl.melonstudios.create.tileentity;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import nl.melonstudios.create.cfg.ClientConfig;
import nl.melonstudios.create.cfg.CommonConfig;
import nl.melonstudios.create.kinetics.BlockStressValues;
import nl.melonstudios.create.kinetics.KNManager;
import nl.melonstudios.create.kinetics.KineticNetwork;
import nl.melonstudios.create.kinetics.KineticPropagator;
import nl.melonstudios.create.tesr.TESRKineticBase;
import nl.melonstudios.create.util.Utils;
import nl.melonstudios.create.util.interfaces.ICogwheel;
import nl.melonstudios.create.util.interfaces.IRotate;

import javax.annotation.Nullable;
import java.util.LinkedList;

public class TileEntityKinetic extends TileEntityOptimizedBase {
    public @Nullable Long networkID = null;
    public @Nullable BlockPos source = null;
    public boolean networkDirty;
    public int networkSize;

    public boolean updateSpeed = true;
    protected int validationCountdown = 60;
    protected int flickerTally = 0;
    public int preventSpeedUpdate = 0;

    public float speed;
    public boolean overstressed;

    public float capacity, stress;

    public float lastCapacityProvided, lastStressApplied;

    public boolean wasMoved;

    @Override
    public void initialize() {
        if (this.hasNetwork() && !this.world.isRemote) {
            KineticNetwork network = this.getOrCreateNetwork();
            if (!network.initialized) network.initFromTE(this.capacity, this.stress, this.networkSize);
            network.addSilently(this, this.lastCapacityProvided, this.lastStressApplied);
        }
    }
    @Override
    public void tick() {
        if (!this.world.isRemote && this.updateSpeed) this.attachKinetics();

        this.preventSpeedUpdate = 0;

        if (this.validationCountdown-- <= 0) {
            this.validationCountdown = 60;
            this.validateKinetics();
        }

        if (this.flickerTally > 0) this.flickerTally--;

        if (this.networkDirty) {
            if (this.hasNetwork()) this.getOrCreateNetwork().updateNetwork();
            this.networkDirty = false;
        }
    }

    private void validateKinetics() {
        if (this.hasSource()) {
            if (!this.hasNetwork()) {
                this.removeSource();
                return;
            }

            if (!this.world.isBlockLoaded(this.source)) return;

            TileEntity te = this.world.getTileEntity(this.source);
            TileEntityKinetic srcTE = te instanceof TileEntityKinetic ? (TileEntityKinetic) te : null;
            if (srcTE == null || srcTE.speed == 0) {
                this.removeSource();
                this.detachKinetics();
                return;
            }

            return;
        }

        if (this.speed != 0) {
            if (this.getGeneratedSpeed() == 0) this.speed = 0;
        }
    }

    @Override
    public void tickLazy() {

    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);

        compound.setFloat("speed", this.speed);
        compound.setBoolean("updateSpeed", this.updateSpeed);
        if (this.hasSource()) compound.setLong("source", this.source.toLong());

        if (this.hasNetwork()) {
            NBTTagCompound networkNBT = new NBTTagCompound();
            networkNBT.setLong("id", this.networkID);
            networkNBT.setFloat("stress", this.stress);
            networkNBT.setFloat("capacity", this.capacity);
            networkNBT.setInteger("size", this.networkSize);

            networkNBT.setFloat("addedStress", this.lastStressApplied);
            networkNBT.setFloat("addedCapacity", this.lastCapacityProvided);

            compound.setTag("Network", networkNBT);
        }

        return compound;
    }
    @Override
    public void readFromNBT(NBTTagCompound compound) {
        boolean overstressedBefore = this.overstressed;
        this.clearKineticInformation();

        if (this.wasMoved) {
            super.readFromNBT(compound);
            return;
        }

        this.speed = compound.getFloat("speed");
        if (compound.hasKey("source")) this.source = BlockPos.fromLong(compound.getLong("source"));

        if (compound.hasKey("Network", 10)) {
            NBTTagCompound networkNBT = compound.getCompoundTag("Network");
            this.networkID = networkNBT.getLong("id");
            this.stress = networkNBT.getFloat("stress");
            this.capacity = networkNBT.getFloat("capacity");
            this.networkSize = networkNBT.getInteger("size");
            this.lastStressApplied = networkNBT.getFloat("addedStress");
            this.lastCapacityProvided = networkNBT.getFloat("addedCapacity");
            this.overstressed = this.capacity < this.stress;
        }

        super.readFromNBT(compound);
    }

    @Override
    public NBTTagCompound writePacket() {
        NBTTagCompound nbt = new NBTTagCompound();

        nbt.setFloat("speed", this.speed);
        if (this.source != null) nbt.setLong("source", this.source.toLong());
        if (this.hasNetwork()) {
            nbt.setBoolean("hasNetwork", true);

            nbt.setLong("networkID", this.networkID);
            nbt.setFloat("stress", this.stress);
            nbt.setFloat("capacity", this.capacity);
            nbt.setInteger("size", this.networkSize);

            if (this.lastStressApplied != 0.0F) nbt.setFloat("addedStress", this.lastStressApplied);
            if (this.lastCapacityProvided != 0.0F) nbt.setFloat("addedCapacity", this.lastCapacityProvided);
        }

        return nbt;
    }
    @Override
    public void readPacket(NBTTagCompound nbt) {
        this.clearKineticInformation();

        this.speed = nbt.getFloat("speed");
        if (nbt.hasKey("source")) this.source = BlockPos.fromLong(nbt.getLong("source"));

        if (nbt.getBoolean("hasNetwork")) {
            this.networkID = nbt.getLong("networkID");
            this.stress = nbt.getFloat("stress");
            this.capacity = nbt.getFloat("capacity");
            this.networkSize = nbt.getInteger("size");

            this.lastStressApplied = nbt.getFloat("addedStress");
            this.lastCapacityProvided = nbt.getFloat("addedCapacity");

            this.overstressed = this.capacity < this.stress;
        }
    }

    public float getSpeed() {
        return this.overstressed ? 0.0F : this.getTheoreticalSpeed();
    }
    public float getTheoreticalSpeed() {
        return this.speed;
    }

    public float getGeneratedSpeed() {
        return 0.0F;
    }
    public boolean isSource() {
        return this.getGeneratedSpeed() != 0.0F;
    }

    public boolean hasSource() {
        return this.source != null;
    }
    public void setSource(BlockPos source) {
        this.source = source;
        if (this.world == null || this.world.isRemote) return;

        TileEntity te = this.world.getTileEntity(source);
        if (!(te instanceof TileEntityKinetic)) {
            this.removeSource();
            return;
        }

        this.setNetwork(((TileEntityKinetic)te).networkID);
    }
    public void removeSource() {
        float lastSpeed = this.getSpeed();

        this.speed = 0.0F;
        this.source = null;
        this.setNetwork(null);

        this.onSpeedChanged(lastSpeed);
    }

    public float calculateCapacity() {
        return this.lastCapacityProvided = BlockStressValues.getStressCapacity(this.blockType);
    }
    public float calculateImpact() {
        return this.lastStressApplied = BlockStressValues.getStressImpact(this.blockType);
    }

    public void updateFromNetwork(float capacity, float stress, int networkSize) {
        this.networkDirty = false;
        this.capacity = capacity;
        this.stress = stress;
        this.networkSize = networkSize;

        boolean overstressed = capacity < stress;
        this.markDirty();

        if (overstressed != this.overstressed) {
            float lastSpeed = this.getSpeed();
            this.overstressed = overstressed;
            this.onSpeedChanged(lastSpeed);
            this.sync();
        }
    }

    public void onSpeedChanged(float lastSpeed) {
        boolean fromOrToZero = (lastSpeed == 0) != (this.getSpeed() == 0);
        boolean directionSwap = !fromOrToZero && Math.signum(lastSpeed) != Math.signum(this.getSpeed());
        if (fromOrToZero || directionSwap) {
            if (CommonConfig.enableFlickerTally) this.flickerTally += 5;
        }
        this.markDirty();
    }

    @Override
    public void remove() {
        if (!this.world.isRemote) {
            if (this.hasNetwork()) {
                this.getOrCreateNetwork().remove(this);
            }
            this.detachKinetics();
        }
    }

    public boolean hasNetwork() {
        return this.networkID != null;
    }

    public void setNetwork(@Nullable Long network) {
        if (this.networkID == network) return;
        if (this.networkID != null) {
            this.getOrCreateNetwork().remove(this);
        }

        this.networkID = network;
        this.markDirty();

        if (network == null) return;
        this.networkID = network;

        KineticNetwork net = this.getOrCreateNetwork();
        net.initialized = true;
        net.add(this);
    }

    public KineticNetwork getOrCreateNetwork() {
        return KNManager.getOrCreateNetworkFor(this);
    }

    public void attachKinetics() {
        this.updateSpeed = false;
        KineticPropagator.handleAdded(this.world, this.pos, this);
    }
    public void detachKinetics() {
        KineticPropagator.handleRemoved(this.world, this.pos, this);
    }

    public void clearKineticInformation() {
        this.speed = 0;
        this.source = null;
        this.networkID = null;
        this.overstressed = false;
        this.stress = 0;
        this.capacity = 0;
        this.lastStressApplied = 0;
        this.lastCapacityProvided = 0;
    }


    public float propagateRotationTo(TileEntityKinetic target, IBlockState stateFrom, IBlockState stateTo, BlockPos diff,
                                     boolean connectedViaAxes, boolean connectedViaCogs) {
        return 0.0F;
    }

    public LinkedList<BlockPos> addPropagationLocations(IRotate block, IBlockState state, LinkedList<BlockPos> neighbours) {
        if (!this.canPropagateDiagonally(block, state)) return neighbours;

        EnumFacing.Axis axis = block.getRotationAxis(state);
        BlockPos.getAllInBox(-1, -1, -1, 1, 1, 1).forEach(offset -> {
            if (Utils.axis_choose(axis, offset.getX(), offset.getY(), offset.getZ()) != 0) return;
            if (offset.distanceSq(BlockPos.ORIGIN) != 2) return;
            neighbours.add(this.pos.add(offset));
        });
        return neighbours;
    }

    public boolean isCustomConnection(TileEntityKinetic other, IBlockState state, IBlockState otherState) {
        return false;
    }

    protected boolean canPropagateDiagonally(IRotate block, IBlockState state) {
        return ICogwheel.isSmallCog(state) || ICogwheel.isLargeCog(state);
    }

    protected boolean isNoisy() {
        return true;
    }

    public int getFlickerScore() {
        return this.flickerTally;
    }

    public float getAxisShift(EnumFacing.Axis axis) {
        return TESRKineticBase.isAxisShifted(this.pos, axis) ? 22.5F : 0.0F;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public double getMaxRenderDistanceSquared() {
        return ClientConfig.kineticRenderDistance;
    }

    public static float convertToDirection(float axisSpeed, EnumFacing d) {
        return d.getAxisDirection() == EnumFacing.AxisDirection.POSITIVE ? axisSpeed : -axisSpeed;
    }
    public static float convertToLinear(float speed) {
        return speed / 512.0F;
    }
    public static float convertToAngular(float speed) {
        return speed * 3 / 10.0F;
    }
}
