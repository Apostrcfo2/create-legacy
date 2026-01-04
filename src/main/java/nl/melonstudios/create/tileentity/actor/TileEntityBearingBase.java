package nl.melonstudios.create.tileentity.actor;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import nl.melonstudios.create.block.actor.BlockBearingBase;
import nl.melonstudios.create.entity.EntityContraptionBearing;
import nl.melonstudios.create.init.SoundInit;
import nl.melonstudios.create.kinetics.contraption.ITileEntityWithContraption;
import nl.melonstudios.create.tileentity.TileEntityKinetic;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public abstract class TileEntityBearingBase extends TileEntityKinetic implements ITileEntityWithContraption {
    public boolean tryAssemble() {
        if (this.isAssembled()) {
            return this.disassemble();
        } else if (this.getSpeed() != 0.0F) {
            return this.assemble();
        }
        return false;
    }

    public boolean isFlipped() {
        return false;
    }
    protected boolean mightAssemble = false;
    protected boolean mightDisassemble = false;

    @Nullable
    protected final EntityContraptionBearing getAttachedContraption() {
        List<EntityContraptionBearing> bearings = this.world.getEntities(
                EntityContraptionBearing.class,
                (e) -> e.bearing == this
        );
        if (bearings.isEmpty()) return null;
        return bearings.get(0);
    }

    public boolean disassemble() {
        if (this.assemblyChanged) return false;
        this.assemblyChanged = true;
        List<EntityContraptionBearing> bearings = this.world.getEntities(
                EntityContraptionBearing.class,
                (e) -> e.bearing.getPos().equals(this.pos)
        );
        for (EntityContraptionBearing bearing : bearings) {
            this.world.removeEntityDangerously(bearing);
        }
        this.preventNextRemoval();
        this.world.setBlockState(this.pos, this.getState().withProperty(BlockBearingBase.ASSEMBLED, false));
        this.validate();
        this.world.setTileEntity(this.pos, this);
        this.angle = 0.0F;
        this.sync();
        this.world.playSound(null, this.pos, SoundInit.contraption_disassemble, SoundCategory.BLOCKS, 1.0F, 1.0F);
        return true;
    }
    protected boolean assemble() {
        if (this.assemblyChanged) return false;
        this.assemblyChanged = true;
        EntityContraptionBearing bearing = new EntityContraptionBearing(this, null, this.pos);
        if (bearing.contraption == null) return false;
        if (bearing.contraption.tileEntities.containsValue(this)) return false; //Prevent bearing picking up itself
        if (bearing.contraption.blocks.containsKey(this.pos)) return false; //Ditto
        if (!this.world.isRemote) this.world.spawnEntity(bearing);
        this.preventNextRemoval();
        this.world.setBlockState(this.pos, this.getState().withProperty(BlockBearingBase.ASSEMBLED, true));
        this.validate();
        this.world.setTileEntity(this.pos, this);
        this.angle = 0.0F;
        this.sync();
        this.world.playSound(null, this.pos, SoundInit.contraption_assemble, SoundCategory.BLOCKS, 1.0F, 1.0F);
        this.world.playSound(null, this.pos, SoundInit.contraption_assemble_compound, SoundCategory.BLOCKS, 0.25F, 1.1F);
        return true;
    }

    public EnumFacing getFacing() {
        return this.getState().getValue(BlockBearingBase.FACING);
    }
    public boolean isAssembled() {
        return this.getState().getValue(BlockBearingBase.ASSEMBLED);
    }

    protected boolean useGeneratedSpeedForContraption() {
        return false;
    }

    public boolean assemblyChanged = false;
    public float angleOld = 0.0F;
    public float angle = 0.0F;
    @Override
    public void tick() {
        this.assemblyChanged = false;
        this.angleOld = this.angle;
        super.tick();

        if (this.mightAssemble && this.getSpeed() != 0.0F && !this.isAssembled()) {
            this.assemble();
        }
        if (this.mightDisassemble && this.getSpeed() == 0.0F && this.isAssembled()) {
            this.disassemble();
        }

        this.mightAssemble = this.mightDisassemble = false;

        if (this.isAssembled() && !this.overstressed) {
            if (this.useGeneratedSpeedForContraption()) {
                this.angle += this.getGeneratedSpeed() * 0.3F;
            } else {
                this.angle += this.getSpeed() * 0.3F;
            }

            if (this.angleOld > 360.0F && this.angle > 360.0F) {
                this.angleOld %= 360.0F;
                this.angle %= 360.0F;
            } else if (this.angleOld < -360.0F && this.angle < -360.0F) {
                this.angleOld %= 360.0F;
                this.angle %= 360.0F;
            }
            EntityContraptionBearing entity = this.getAttachedContraption();
            if (entity != null) {
                entity.cachedAngle = this.angle;
            }
            this.markDirty();
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        NBTTagCompound nbt = super.writeToNBT(compound);

        nbt.setFloat("angle", this.angle);

        return nbt;
    }

    @Override
    public NBTTagCompound writePacket() {
        NBTTagCompound nbt = super.writePacket();

        if (this.angle != 0.0F) nbt.setFloat("angle", this.angle);

        return nbt;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);

        this.angle = nbt.getFloat("angle");
    }

    @Override
    public void readPacket(NBTTagCompound nbt) {
        super.readPacket(nbt);

        this.angle = nbt.getFloat("angle");
    }

    @Override
    public void collectCollisions(AxisAlignedBB aabb, List<AxisAlignedBB> collisions) {

    }
}
