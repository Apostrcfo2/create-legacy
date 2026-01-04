package nl.melonstudios.create.entity;

import io.netty.buffer.ByteBuf;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MoverType;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import nl.melonstudios.create.CreateLegacy;
import nl.melonstudios.create.kinetics.contraption.Contraption;
import nl.melonstudios.create.kinetics.contraption.ContraptionRendering;
import nl.melonstudios.create.kinetics.contraption.GluedSurface;
import nl.melonstudios.create.kinetics.contraption.IContraptionHolder;
import nl.melonstudios.create.tileentity.TileEntityKinetic;
import nl.melonstudios.create.tileentity.actor.TileEntityBearingBase;
import nl.melonstudios.create.util.BlockRotationHelper;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class EntityContraptionBearing extends EntityContraptionBase implements IContraptionHolder, IEntityAdditionalSpawnData {
    public EntityContraptionBearing(World worldIn) {
        super(worldIn);

        this.setSize(1.0F, 1.0F);
    }

    @Override
    protected RotationPossibility getRotationPossibility() {
        if (this.bearing == null) return RotationPossibility.ALL;
        switch (this.bearing.getFacing().getAxis()) {
            case X: return RotationPossibility.X;
            case Y: return RotationPossibility.Y;
            case Z: return RotationPossibility.Z;
            default:return RotationPossibility.ALL;
        }
    }

    public EntityContraptionBearing(TileEntityBearingBase bearing, @Nullable Contraption contraption, @Nullable BlockPos exclude) {
        this(bearing.getWorld());

        BlockPos pos = bearing.getPos().offset(bearing.getFacing());
        this.setPosition(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);

        this.bearing = bearing;
        this.contraption = contraption;
        if (this.contraption == null) {
            this.contraption = Contraption.assemble(this, pos, exclude);
        }
        this.cachedAxis = this.bearing.getFacing().getAxis();
        this.cachedAngle = this.bearing.angle;
    }

    public BlockPos bearingPos;
    public TileEntityBearingBase bearing;
    public Contraption contraption;
    public float cachedAngle;
    public EnumFacing.Axis cachedAxis;

    @Override
    public Contraption attachedContraption() {
        return this.contraption;
    }

    @Override
    public void onUpdate() {
        this.setFire(0);
        if (this.bearing == null) {
            if (this.bearingPos == null) {
                this.setDead();
                return;
            }
            TileEntity te = this.world.getTileEntity(this.bearingPos);
            if (te instanceof TileEntityBearingBase) {
                this.bearing = (TileEntityBearingBase) te;
                this.cachedAxis = ((TileEntityBearingBase) te).getFacing().getAxis();
                this.resetBB();
            } else {
                this.setDead();
                return;
            }
        }

        if (this.bearing.isInvalid()) this.bearing = null;
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound compound) {
        this.contraption = new Contraption(this);
        this.contraption.loadNBT(compound.getCompoundTag("Contraption"));

        this.bearingPos = NBTUtil.getPosFromTag(compound.getCompoundTag("BearingPos"));
    }
    @Override
    protected void writeEntityToNBT(NBTTagCompound compound) {
        compound.setTag("Contraption", this.contraption.saveNBT(new NBTTagCompound()));
        compound.setTag("BearingPos", NBTUtil.createPosTag(this.bearing.getPos()));
    }

    @Override
    public World getWorld() {
        return this.world;
    }
    @Override
    public int getCombinedLight(BlockPos contraptionPos, int min) {
        return this.world.getCombinedLight(this.getPosition(), min);
    }
    @Override
    public Biome getBiome() {
        return this.world.getBiome(this.getPosition());
    }

    @Override
    public void setDead() {
        super.setDead();

        if (!this.world.isRemote) {
            BlockPos self = this.getPosition();
            Rotation rotation = BlockRotationHelper.getRotationForAngle(this.bearing != null ? this.bearing.angle : this.cachedAngle);
            EnumFacing.Axis axis = this.bearing != null ? this.bearing.getFacing().getAxis() : this.cachedAxis;
            for (Map.Entry<BlockPos, IBlockState> entry : this.contraption.blocks.entrySet()) {
                BlockPos pos = BlockRotationHelper.transform(self, axis, rotation, entry.getKey()); //self.add(entry.getKey());
                this.world.setBlockState(pos, BlockRotationHelper.rotate(entry.getValue(), axis, rotation));
                TileEntity te = this.contraption.tileEntities.get(entry.getKey());
                if (te != null) {
                    te.setPos(pos);
                    te.validate();
                    if (te instanceof TileEntityKinetic) {
                        ((TileEntityKinetic)te).wasMoved = true;
                    }
                    this.world.setTileEntity(pos, te);
                }
            }
            for (GluedSurface surface : this.contraption.gluedSurfaces) {
                BlockPos pos = BlockRotationHelper.transform(self, axis, rotation, surface.pos); // self.add(surface.pos);
                EntityGlue entityGlue = new EntityGlue(this.world, new GluedSurface(pos, BlockRotationHelper.rotate(surface.side, axis, rotation)));
                entityGlue.wasCovered = true;
                this.world.spawnEntity(entityGlue);
            }
        } else {
            ContraptionRendering.contraptionFinalized(this.contraption);
        }
    }

    @Override
    public void writeSpawnData(ByteBuf buffer) {
        buffer.writeLong(this.bearing.getPos().toLong());

        new PacketBuffer(buffer).writeCompoundTag(this.contraption.saveNBT(new NBTTagCompound()));
    }

    @Override
    public void readSpawnData(ByteBuf additionalData) {
        try {
            this.bearingPos = BlockPos.fromLong(additionalData.readLong());

            this.contraption = new Contraption(this);
            this.contraption.loadNBT(Objects.requireNonNull(new PacketBuffer(additionalData).readCompoundTag()));
        } catch (IOException e) {
            throw new RuntimeException("Failed to read contraption spawn data", e);
        }
    }
}
