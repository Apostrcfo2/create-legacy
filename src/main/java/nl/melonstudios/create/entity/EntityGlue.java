package nl.melonstudios.create.entity;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import nl.melonstudios.create.kinetics.contraption.GluedSurface;

public class EntityGlue extends Entity implements IEntityAdditionalSpawnData {
    private GluedSurface surface;

    public EntityGlue(World worldIn) {
        super(worldIn);

        this.setSize(1.0F, 1.0F);
    }

    public EntityGlue(World world, GluedSurface surface) {
        this(world);

        this.setPosition(surface.pos.getX() + 0.5F, surface.pos.getY(), surface.pos.getZ() + 0.5F);
        this.surface = surface;
    }

    public GluedSurface getSurface() {
        return this.surface;
    }

    @Override
    public void onUpdate() {
        this.setFire(0);

        if (this.world.getTotalWorldTime() % 10 == 0) {
            this.validateGlueness();
        }
    }

    private void validateGlueness() {
        BlockPos otherPos = this.surface.pos.offset(this.surface.side);
        if (this.world.getBlockState(this.surface.pos).getBlock().isReplaceable(this.world, this.surface.pos)) {
            if (this.world.getBlockState(otherPos).getBlock().isReplaceable(this.world, otherPos)) {
                this.setDead();
            }
        }
    }

    @Override
    protected void entityInit() {

    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound compound) {
        BlockPos pos = BlockPos.fromLong(compound.getLong("gluedPos"));
        EnumFacing side = EnumFacing.VALUES[Byte.toUnsignedInt(compound.getByte("gluedSide"))];

        this.surface = new GluedSurface(pos, side);
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound compound) {
        compound.setLong("gluedPos", this.surface.pos.toLong());
        compound.setByte("gluedSide", (byte)this.surface.side.getIndex());
    }

    @Override
    public void writeSpawnData(ByteBuf buffer) {
        buffer.writeLong(this.surface.pos.toLong());
        buffer.writeByte(this.surface.side.getIndex());
    }

    @Override
    public void readSpawnData(ByteBuf additionalData) {
        this.surface = new GluedSurface(
                BlockPos.fromLong(additionalData.readLong()),
                EnumFacing.VALUES[additionalData.readByte()]
        );
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int getBrightnessForRender() {
        BlockPos otherPos = this.surface.pos.offset(this.surface.side);
        return Math.max(this.world.getCombinedLight(this.surface.pos, 0), this.world.getCombinedLight(otherPos, 0));
    }
}
