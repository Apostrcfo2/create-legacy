package nl.melonstudios.create.entity;

import com.melonstudios.melonlib.tileentity.ISyncedTE;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.PacketBuffer;
import net.minecraft.profiler.Profiler;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import nl.melonstudios.create.CreateLegacy;
import nl.melonstudios.create.block.actor.BlockMechanicalPiston;
import nl.melonstudios.create.kinetics.contraption.*;
import nl.melonstudios.create.kinetics.contraption.accessor.IContraptionAccessor;
import nl.melonstudios.create.tileentity.TileEntityKinetic;
import nl.melonstudios.create.tileentity.actor.TileEntityMechanicalPiston;
import org.joml.Matrix4d;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class EntityContraptionPiston extends EntityContraptionBase implements IContraptionHolder {
    public final IContraptionAccessor contraptionAccessor;
    public EntityContraptionPiston(World worldIn) {
        super(worldIn);

        this.setSize(1.0F, 1.0F);

        this.contraptionAccessor = null;
    }

    public EntityContraptionPiston(TileEntityMechanicalPiston piston) {
        this(piston.getWorld());

        BlockPos pos = piston.getPos().offset(piston.getState().getValue(BlockMechanicalPiston.FACING));
        this.setPositionAndUpdateInternal(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);

        this.piston = piston;
        this.pistonPos = piston.getPos();
        this.cachedFacing = piston.getFacing();
        this.cachedExtension = piston.extension;
        this.cachedExtensionOld = piston.extensionOld;
    }

    public AxisAlignedBB updatedContraptionBB = null;
    public void moveBB() {
        EnumFacing facing = this.cachedFacing;
        if (this.contraptionBB == null) this.resetBB();
        if (facing == null) {
            CreateLegacy.logger.debug("HELP");
            return;
        }
        double dx = (this.cachedExtension) * facing.getFrontOffsetX();
        double dy = (this.cachedExtension) * facing.getFrontOffsetY();
        double dz = (this.cachedExtension) * facing.getFrontOffsetZ();
        this.updatedContraptionBB = new AxisAlignedBB(
                this.contraptionBB.minX + dx,
                this.contraptionBB.minY + dy,
                this.contraptionBB.minZ + dz,
                this.contraptionBB.maxX + dx,
                this.contraptionBB.maxY + dy,
                this.contraptionBB.maxZ + dz
        );
    }

    @Override
    public AxisAlignedBB getEntityBoundingBox() {
        return this.updatedContraptionBB != null ? this.updatedContraptionBB : super.getEntityBoundingBox();
    }

    private static final Matrix4d TRANSFORMS = new Matrix4d();
    public BlockPos pistonPos;
    public TileEntityMechanicalPiston piston;
    public Contraption contraption;
    public float cachedExtensionOld, cachedExtension;
    public EnumFacing cachedFacing;

    @Nullable
    @Override
    public Contraption attachedContraption() {
        return this.contraption;
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        this.setFire(0);
        if (this.piston == null) {
            if (this.pistonPos == null) {
                if (!this.world.isRemote) {
                    this.world.removeEntity(this);
                }
                return;
            }
            TileEntity te = this.world.getTileEntity(this.pistonPos);
            if (te instanceof TileEntityMechanicalPiston) {
                this.piston = (TileEntityMechanicalPiston) te;
                this.cachedFacing = ((TileEntityMechanicalPiston) te).getState().getValue(BlockMechanicalPiston.FACING);
                this.resetBB();
            } else {
                if (!this.world.isRemote) this.world.removeEntity(this);
                return;
            }
        } else {
            this.piston.attachedContraptionEntity = this;
            this.piston.attachedContraptionUUID = this.getPersistentID();
            this.pistonPos = this.piston.getPos();
        }

        if (!this.piston.isAssembled()) {
            if (!this.world.isRemote) {
                this.world.removeEntity(this);
            }
            return;
        }
        if (this.piston.isInvalid()) this.piston = null;

        if (!this.contraption.poufs.isEmpty()) {
            Matrix4d mat = TRANSFORMS.identity();
            EnumFacing facing = this.cachedFacing;
            double extension = this.cachedExtension;
            //mat.translate(facing.getFrontOffsetX() * extension, facing.getFrontOffsetY() * extension, facing.getFrontOffsetZ() * extension);
            this.contraption.updatePoufs(this.world, this.posX + facing.getFrontOffsetX() * extension, this.posY + facing.getFrontOffsetY() * extension, this.posZ + facing.getFrontOffsetZ() * extension, mat);
        }

        this.moveBB();

        if (HANDLE_COLLISION_IN_ENTITY) {
            //CreateLegacy.logger.debug("test {} : {} {} {}", this.world.isRemote, this.cachedFacing, this.contraption, this.updatedContraptionBB);
            if (this.cachedFacing != null && this.contraption != null && this.updatedContraptionBB != null) {
                Profiler profiler = this.world.profiler;
                profiler.startSection("pushoutEntities");
                List<EntityLivingBase> entities = this.world.getEntitiesWithinAABB(EntityLivingBase.class, this.updatedContraptionBB);
                if (!entities.isEmpty()) {
                    List<AxisAlignedBB> pushouts = new ArrayList<>();
                    double dx = this.posX - 0.5 + this.cachedFacing.getFrontOffsetX() * this.cachedExtension;
                    double dy = this.posY - 0.5 + this.cachedFacing.getFrontOffsetY() * this.cachedExtension;
                    double dz = this.posZ - 0.5 + this.cachedFacing.getFrontOffsetZ() * this.cachedExtension;
                    profiler.startSection("collectAABB");
                    for (AxisAlignedBB bb : this.contraption.optimizedAABB) {
                        pushouts.add(bb.offset(dx, dy, dz));
                    }
                    if (!pushouts.isEmpty()) {
                        profiler.endStartSection("pushout");
                        EnumFacing.Axis pushAxis = this.cachedFacing.getAxis();
                        loop:
                        for (EntityLivingBase entity : entities) {
                            AxisAlignedBB entityBB = entity.getEntityBoundingBox();
                            Vec3d center = new Vec3d(entity.posX, entity.posY + entity.height * 0.5, entity.posZ);
                            double half = entity.width * 0.5;
                            for (AxisAlignedBB aabb : pushouts) {
                                if (aabb.intersects(entityBB)) {
                                    switch (pushAxis) {
                                        case Y:
                                            if (entity.posY < aabb.maxY && center.y >= aabb.maxY) {
                                                entity.setPosition(entity.posX, aabb.maxY, entity.posZ);
                                                entity.onGround = true;
                                                entity.fallDistance = 0.0F;
                                                entity.motionY = 0.0F;
                                                //entity.prevPosY = entity.posY;
                                                continue loop;
                                            }
                                            if (entity.posY + entity.height > aabb.minY && center.y <= aabb.minY) {
                                                entity.setPosition(entity.posX, aabb.minY - entity.height, entity.posZ);
                                                //entity.prevPosY = entity.posY;
                                                continue loop;
                                            }
                                            break;
                                        case X:
                                            if (entity.posX - half < aabb.maxX && center.x >= aabb.maxX) {
                                                entity.setPosition(aabb.maxX + half, entity.posY, entity.posZ);
                                                entity.motionX = 0.0;
                                                continue loop;
                                            } else if (entity.posX + half > aabb.minX && center.x <= aabb.minX) {
                                                entity.setPosition(aabb.minX - half, entity.posY, entity.posZ);
                                                entity.motionX = 0.0;
                                                continue loop;
                                            }
                                            break;
                                        case Z:
                                            if (entity.posZ - half < aabb.maxZ && center.z >= aabb.maxZ) {
                                                entity.setPosition(entity.posX, entity.posY, aabb.minZ + half);
                                                entity.motionZ = 0.0;
                                                continue loop;
                                            }
                                            if (entity.posZ + half > aabb.minZ && center.z <= aabb.minZ) {
                                                entity.setPosition(entity.posX, entity.posY, aabb.minZ - half);
                                                entity.motionZ = 0.0;
                                                continue loop;
                                            }
                                            break;
                                    }
                                }
                            }
                        }
                    }
                    profiler.endSection();
                }
                profiler.endSection();
            }
        }
    }
    private static final boolean HANDLE_COLLISION_IN_ENTITY = true;
    private static final boolean DO_SIDEWAYS_COLLISION = false;

    @SideOnly(Side.CLIENT)
    @Override
    public void applyRenderTransforms(float pt) {
        GlStateManager.translate(
                MathHelper.clampedLerp(this.prevPosX, this.posX, pt) - 0.5,
                MathHelper.clampedLerp(this.prevPosY, this.posY, pt) - 0.5,
                MathHelper.clampedLerp(this.prevPosZ, this.posZ, pt) - 0.5
        );
        if (this.piston != null) this.cachedFacing = this.piston.getFacing();
        if (this.cachedFacing != null) {
            GlStateManager.translate(
                    MathHelper.clampedLerp(this.cachedExtensionOld, this.cachedExtension, pt) * this.cachedFacing.getFrontOffsetX(),
                    MathHelper.clampedLerp(this.cachedExtensionOld, this.cachedExtension, pt) * this.cachedFacing.getFrontOffsetY(),
                    MathHelper.clampedLerp(this.cachedExtensionOld, this.cachedExtension, pt) * this.cachedFacing.getFrontOffsetZ()
            );
        } else {
            //GlStateManager.translate(0, pt, 0);
        }
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        if (compound.hasKey("Contraption", 10)) {
            this.contraption = new Contraption(this);
            this.contraption.loadNBT(compound.getCompoundTag("Contraption"));
        } else throw new IllegalStateException("No contraption data (this should not happen)");
        if (compound.hasKey("BearingPos", 10)) {
            this.pistonPos = NBTUtil.getPosFromTag(compound.getCompoundTag("BearingPos"));
        } else throw new IllegalStateException("No bearingPos data (this should not happen)");
    }
    @Override
    protected void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        if (this.contraption != null) compound.setTag("Contraption", this.contraption.saveNBT(new NBTTagCompound()));
        else throw new IllegalStateException("Contraption is null (this should not happen)");
        if (this.pistonPos != null) compound.setTag("BearingPos", NBTUtil.createPosTag(this.pistonPos));
        else throw new IllegalStateException("BearingPos is null (this should not happen)");
    }

    @Override
    public void placeBlocks() {
        if (this.contraption == null) return;
        List<TileEntityKinetic> attachables = new ArrayList<>();
        int extension = Math.round(this.cachedExtension);
        final int offsetX, offsetY, offsetZ;
        if (this.cachedFacing != null && extension != 0) {
            offsetX = this.cachedFacing.getFrontOffsetX() * extension;
            offsetY = this.cachedFacing.getFrontOffsetY() * extension;
            offsetZ = this.cachedFacing.getFrontOffsetZ() * extension;
        } else {
            offsetX = 0;
            offsetY = 0;
            offsetZ = 0;
        }

        BlockPos self = this.getPosition().add(offsetX, offsetY, offsetZ);
        for (Map.Entry<BlockPos, IBlockState> entry : this.contraption.blocks.entrySet()) {
            BlockPos pos = self.add(entry.getKey());
            this.world.setBlockState(pos, entry.getValue());
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
            BlockPos pos = self.add(surface.pos);
            EntityGlue glue = new EntityGlue(this.world, new GluedSurface(pos, surface.side));
            glue.wasCovered = true;
            this.world.spawnEntity(glue);
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
        buf.writeLong(this.pistonPos.toLong());

        new PacketBuffer(buf).writeCompoundTag(this.contraption.saveNBT(new NBTTagCompound()));
    }

    @Override
    public void readSpawnData(ByteBuf buf) {
        try {
            this.pistonPos = BlockPos.fromLong(buf.readLong());

            this.contraption = new Contraption(this);
            this.contraption.loadNBT(Objects.requireNonNull(new PacketBuffer(buf).readCompoundTag()));
        } catch (IOException e) {
            throw new RuntimeException("Failed to read contraption spawn data", e);
        }
    }

    public void pauseContraption() {
        if (this.piston != null) {
            //this.piston.pauseContraption();
        }
    }
}
