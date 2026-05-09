package nl.melonstudios.create.entity;

import com.melonstudios.melonlib.tileentity.ISyncedTE;
import io.netty.buffer.ByteBuf;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import nl.melonstudios.create.kinetics.contraption.*;
import nl.melonstudios.create.kinetics.contraption.accessor.CAccessorBearing;
import nl.melonstudios.create.kinetics.contraption.accessor.IContraptionAccessor;
import nl.melonstudios.create.tileentity.TileEntityKinetic;
import nl.melonstudios.create.tileentity.actor.TileEntityBearingBase;
import nl.melonstudios.create.util.BlockRotationHelper;
import org.joml.Matrix4d;
import org.joml.Vector3f;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class EntityContraptionBearing extends EntityContraptionBase implements IContraptionHolder {
    public final IContraptionAccessor contraptionAccessor;
    public EntityContraptionBearing(World worldIn) {
        super(worldIn);

        this.setSize(1.0F, 1.0F);

        this.contraptionAccessor = new CAccessorBearing(this);
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
        this.setPositionAndUpdateInternal(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);

        this.bearing = bearing;
        this.bearingPos = bearing.getPos();
        this.contraption = contraption;
        if (this.contraption == null) {
            this.contraption = Contraption.assemble(this, pos, exclude);
        }
        this.cachedAxis = this.bearing.getFacing().getAxis();
        this.cachedAngle = this.bearing.angle;
    }
    public EntityContraptionBearing(TileEntityBearingBase bearing) {
        this(bearing.getWorld());

        BlockPos pos = bearing.getPos().offset(bearing.getFacing());
        this.setPositionAndUpdateInternal(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);

        this.bearing = bearing;
        this.bearingPos = bearing.getPos();
        this.cachedAxis = this.bearing.getFacing().getAxis();
        this.cachedAngle = this.bearing.angle;
    }

    private static final Matrix4d TRANSFORMS = new Matrix4d();
    public BlockPos bearingPos;
    public TileEntityBearingBase bearing;
    public Contraption contraption;
    public float cachedAngleOld, cachedAngle;
    public EnumFacing.Axis cachedAxis;

    @Override
    public Contraption attachedContraption() {
        return this.contraption;
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        this.setFire(0);
        if (this.bearing == null) {
            if (this.bearingPos == null) {
                if (!this.world.isRemote) {
                    this.specialCondition = true;
                    this.world.removeEntity(this);
                }
                return;
            }
            TileEntity te = this.world.getTileEntity(this.bearingPos);
            if (te instanceof TileEntityBearingBase) {
                this.bearing = (TileEntityBearingBase) te;
                this.cachedAxis = ((TileEntityBearingBase) te).getFacing().getAxis();
                this.resetBB();
            } else {
                if (!this.world.isRemote) this.world.removeEntity(this);
                return;
            }
        } else {
            this.bearing.attachedContraptionEntity = this;
            this.bearing.attachedContraptionUUID = this.getPersistentID();
            this.bearingPos = this.bearing.getPos();
        }

        if (!this.bearing.isAssembled()) {
            if (!this.world.isRemote) {
                this.specialCondition = true;
                this.world.removeEntity(this);
            }
            return;
        }
        if (this.bearing.isInvalid()) this.bearing = null;

        if (!this.contraption.actors.isEmpty()) {
            BlockPos anchor = this.getPosition();
            Vector3f vec = new Vector3f();
            Vector3f movement = new Vector3f();
            for (ActorContext context : this.contraption.actors) {
                vec.set(context.worldPos);
                BlockRotationHelper.rotateNormal(context.pos, this.cachedAxis, this.cachedAngle, context.worldPos);
                context.worldPos.add(0.5F, 0.5F, 0.5F);
                int oldX = context.actorWorldPos.getX();
                int oldY = context.actorWorldPos.getY();
                int oldZ = context.actorWorldPos.getZ();
                context.actorWorldPos.setPos(
                        context.worldPos.x + anchor.getX(),
                        context.worldPos.y + anchor.getY(),
                        context.worldPos.z + anchor.getZ()
                );
                movement.set(context.worldPos);
                movement.sub(vec);
                context.actor.contraptionTick(
                        this.contraptionAccessor, this.world,
                        context.worldPos, context.actorWorldPos,
                        oldX != context.actorWorldPos.getX() ||
                                oldY != context.actorWorldPos.getY() ||
                                oldZ != context.actorWorldPos.getZ(),
                        movement
                );
            }
        }
        if (!this.contraption.poufs.isEmpty()) {
            Matrix4d mat = TRANSFORMS.identity();
            EnumFacing.Axis axis = this.cachedAxis;
            double angle = this.cachedAngle * (double) BlockRotationHelper.RADIANS;
            switch (axis) {
                case X:
                    mat.rotateX(angle);
                    break;
                case Y:
                    mat.rotateY(angle);
                    break;
                case Z:
                    mat.rotateZ(angle);
                    break;
            }
            this.contraption.updatePoufs(this.world, this.posX, this.posY, this.posZ, mat);
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void applyRenderTransforms(float pt) {
        //Float bearingAngleDebug = this.bearing != null ? this.bearing.angle : null;
        //Float bearingAngleOldDebug = this.bearing != null ? this.bearing.angleOld : null;
        //CreateLegacy.logger.debug("a:{} ba:{} bao:{} a:{} ao:{}", this.cachedAxis, bearingAngleDebug, bearingAngleOldDebug, this.cachedAngle, this.cachedAngleOld);
        GlStateManager.translate(
                MathHelper.clampedLerp(this.prevPosX, this.posX, pt),
                MathHelper.clampedLerp(this.prevPosY, this.posY, pt),
                MathHelper.clampedLerp(this.prevPosZ, this.posZ, pt)
        );
        if (this.bearing != null) this.cachedAxis = this.bearing.getFacing().getAxis();
        if (this.cachedAxis != null) {
            float angle = (float)MathHelper.clampedLerp(this.cachedAngleOld, this.cachedAngle, pt);
            switch (this.cachedAxis) {
                case X:
                    GlStateManager.rotate(angle, 1.0F, 0.0F, 0.0F);
                    break;
                case Y:
                    GlStateManager.rotate(angle, 0.0F, 1.0F, 0.0F);
                    break;
                case Z:
                    GlStateManager.rotate(angle, 0.0F, 0.0F, 1.0F);
                    break;
            }
        }
        GlStateManager.translate(-0.5F, -0.5F, -0.5F);
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        if (compound.hasKey("Contraption", 10)) {
            this.contraption = new Contraption(this);
            this.contraption.loadNBT(compound.getCompoundTag("Contraption"));
        } else throw new IllegalStateException("No contraption data (this should not happen)");
        if (compound.hasKey("BearingPos", 10)) {
            this.bearingPos = NBTUtil.getPosFromTag(compound.getCompoundTag("BearingPos"));
        } else throw new IllegalStateException("No bearingPos data (this should not happen)");
    }
    @Override
    protected void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        if (this.contraption != null) compound.setTag("Contraption", this.contraption.saveNBT(new NBTTagCompound()));
        else throw new IllegalStateException("Contraption is null (this should not happen)");
        if (this.bearingPos != null) compound.setTag("BearingPos", NBTUtil.createPosTag(this.bearingPos));
        else throw new IllegalStateException("BearingPos is null (this should not happen)");
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

    private boolean specialCondition = false;
    @Override
    public void setDead() {
        super.setDead();
    }

    @Override
    public void placeBlocks() {
        if (this.contraption == null) return;
        List<TileEntityKinetic> attachables = new ArrayList<>();
        BlockPos self = this.getPosition();
        Rotation rotation = BlockRotationHelper.getRotationForAngle(this.bearing != null ? this.bearing.angle : this.cachedAngle);
        EnumFacing.Axis axis = this.bearing != null ? this.bearing.getFacing().getAxis() : this.cachedAxis;
        for (Map.Entry<BlockPos, IBlockState> entry : this.contraption.blocks.entrySet()) {
            BlockPos pos = BlockRotationHelper.transform(self, axis, rotation, entry.getKey()); //self.add(entry.getKey());
            this.world.setBlockState(pos, BlockRotationHelper.rotate(entry.getValue(), axis, rotation));
            this.world.removeTileEntity(pos);
            TileEntity te = this.contraption.tileEntities.get(entry.getKey());
            if (te != null) {
                te.setPos(pos);
                te.validate();
                TileEntity copy = TileEntity.create(this.world, te.writeToNBT(new NBTTagCompound()));
                if (copy != null) {
                    copy.validate();
                    this.world.setTileEntity(pos, copy);
                    if (copy instanceof TileEntityKinetic) {
                        attachables.add((TileEntityKinetic) copy);
                    }
                    if (copy instanceof IContraptionActor) {
                        ((IContraptionActor) te).setOnContraption(false);
                    }
                    if (copy instanceof ISyncedTE) ((ISyncedTE)copy).sync();
                    else copy.markDirty();
                }
            }
        }
        for (GluedSurface surface : this.contraption.gluedSurfaces) {
            BlockPos pos = BlockRotationHelper.transform(self, axis, rotation, surface.pos); // self.add(surface.pos);
            EntityGlue entityGlue = new EntityGlue(this.world, new GluedSurface(pos, BlockRotationHelper.rotate(surface.side, axis, rotation)));
            entityGlue.wasCovered = true;
            this.world.spawnEntity(entityGlue);
        }
        for (TrackedPouf pouf : this.contraption.poufs) {
            if (pouf.entity != null) this.world.removeEntity(pouf.entity);
        }

        for (TileEntityKinetic te : attachables) {
            te.speed = 0.0F;
            te.attachKinetics();
            te.sync();
        }
    }

    @Override
    public void writeSpawnData(ByteBuf buf) {
        super.writeSpawnData(buf);
        buf.writeLong(this.bearingPos.toLong());

        new PacketBuffer(buf).writeCompoundTag(this.contraption.saveNBT(new NBTTagCompound()));
    }

    @Override
    public void readSpawnData(ByteBuf buf) {
        super.readSpawnData(buf);
        try {
            this.bearingPos = BlockPos.fromLong(buf.readLong());

            this.contraption = new Contraption(this);
            this.contraption.loadNBT(Objects.requireNonNull(new PacketBuffer(buf).readCompoundTag()));
        } catch (IOException e) {
            throw new RuntimeException("Failed to read contraption spawn data", e);
        }
    }

    public void pauseContraption() {
        if (this.bearing != null) {
            this.bearing.pauseContraption();
        }
    }
}
