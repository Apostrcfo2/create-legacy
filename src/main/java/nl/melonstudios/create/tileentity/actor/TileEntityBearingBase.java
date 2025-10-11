package nl.melonstudios.create.tileentity.actor;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import nl.melonstudios.create.block.actor.BlockBearingBase;
import nl.melonstudios.create.kinetics.contraption.IContraptionHolder;
import nl.melonstudios.create.kinetics.contraption.ITileEntityWithContraption;
import nl.melonstudios.create.tileentity.TileEntityKinetic;

import java.util.List;

public abstract class TileEntityBearingBase extends TileEntityKinetic implements ITileEntityWithContraption, IContraptionHolder {
    public boolean tryAssemble() {
        return false;
    }
    public int getXOffset() {
        return 0;
    }
    public int getYOffset() {
        return 0;
    }
    public int getZOffset() {
        return 0;
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

    public float angle = 0.0F;
    @Override
    public void tick() {
        super.tick();

        if (this.isAssembled() && !this.overstressed) {
            if (this.useGeneratedSpeedForContraption()) {
                this.angle += this.getGeneratedSpeed();
            } else {
                this.angle += this.getSpeed();
            }
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
    public int getCombinedLight(BlockPos contraptionPos, int min) {
        return this.world.getCombinedLight(contraptionPos, min);
    }

    @Override
    public Biome getBiome() {
        return this.world.getBiome(this.pos);
    }

    @Override
    public void collectCollisions(AxisAlignedBB aabb, List<AxisAlignedBB> collisions) {

    }
}
