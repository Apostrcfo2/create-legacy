package nl.melonstudios.create.tileentity.actor;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import nl.melonstudios.create.block.actor.BlockContraptionInterface;
import nl.melonstudios.create.kinetics.contraption.ContraptionInventory;
import nl.melonstudios.create.kinetics.contraption.IContraptionActor;
import nl.melonstudios.create.kinetics.contraption.accessor.IContraptionAccessor;
import nl.melonstudios.create.tileentity.TileEntityOptimizedBase;
import nl.melonstudios.create.util.Utils;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public abstract class TileEntityContraptionInterfaceBase extends TileEntityOptimizedBase implements IContraptionActor {
    public TileEntityContraptionInterfaceBase() {
        super();
    }

    @Override
    public void tick() {
        this.wasConnected = this.isConnected();
    }

    @Override
    public void tickLazy() {

    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        return super.writeToNBT(compound);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
    }

    @Override
    public NBTTagCompound writePacket() {
        NBTTagCompound nbt = new NBTTagCompound();
        if (this.disconnectionTimer > 0) nbt.setInteger("disconnect", this.disconnectionTimer);
        return nbt;
    }

    @Override
    public void readPacket(NBTTagCompound nbt) {
        this.disconnectionTimer = nbt.getInteger("disconnect");
    }

    public EnumFacing getFacing() {
        return this.getState().getValue(BlockContraptionInterface.FACING);
    }
    public ContraptionInventory getInventory() {
        return this.connectedInv != null ? this.connectedInv : ContraptionInventory.empty();
    }

    public boolean wasConnected() {
        return this.wasConnected;
    }
    public boolean isConnected() {
        return this.onContraption ? this.target != null : this.disconnectionTimer > 0;
    }
    public float getConnectorOffset() {
        return 0.5F;
    }

    private boolean wasConnected = false;
    private ContraptionInventory connectedInv;
    private TileEntityContraptionInterfaceBase target;
    protected int disconnectionTimer = 0;
    private BlockPos lastConnection = BlockPos.ORIGIN;
    private boolean onContraption = false;
    private final Vector3f contraptionFacing = new Vector3f();

    public void setDisconnectionTimer(int ticks) {
        this.disconnectionTimer = ticks;
        this.sync();
    }
    private boolean isValidRotation() {
        return (Math.abs(this.contraptionFacing.x) > 0.75F || Math.abs(this.contraptionFacing.y) > 0.75F || Math.abs(this.contraptionFacing.z) > 0.75F);
    }

    @Override
    public void setOnContraption(boolean onContraption) {
        this.onContraption = onContraption;
    }

    @Override
    public boolean isOnContraption() {
        return this.onContraption;
    }

    @Override
    public void contraptionTick(IContraptionAccessor contraption, World world, Vector3fc position, BlockPos blockPosition, boolean moved, Vector3fc movement) {
        this.wasConnected = this.isConnected();
        EnumFacing facing = this.getFacing();
        contraption.getNormal(facing, this.contraptionFacing);
        if (this.isValidRotation()) {
            BlockPos target = contraption.getWorldPos(this.pos.offset(facing, 2));
            EnumFacing globalFacing = EnumFacing.getFacingFromVector(this.contraptionFacing.x, this.contraptionFacing.y, this.contraptionFacing.z);
            IBlockState targetState = world.getBlockState(target);
            if (targetState.getBlock() instanceof BlockContraptionInterface) {
                if (targetState.getValue(BlockContraptionInterface.FACING) == globalFacing.getOpposite()) {
                    TileEntityContraptionInterfaceBase targetTE = Utils.cast(world.getTileEntity(target), this.getClass());
                    if (this.target != targetTE) {
                        if (!this.lastConnection.equals(target)) {
                            this.target = targetTE;
                            if (this.target != null) {
                                this.target.connectedInv = contraption.getInventory();
                                this.target.disconnectionTimer = 10;
                                contraption.pauseContraption();
                            }
                        } else this.resetTarget();
                    } else if (this.target != null && !this.target.isInvalid()) {
                        if (this.target.disconnectionTimer-- > 0) {
                            contraption.pauseContraption();
                            this.target.connectedInv = contraption.getInventory();
                        } else this.resetTarget();
                    }
                } else this.resetTarget();
            } else this.resetTarget();
            this.lastConnection = target.toImmutable();
        } else this.resetTarget();
    }

    private void resetTarget() {
        if (this.target != null) {
            this.target.connectedInv = null;
            this.target.disconnectionTimer = 0;
            this.target.target = null;
            this.target = null;
        }
    }
}
