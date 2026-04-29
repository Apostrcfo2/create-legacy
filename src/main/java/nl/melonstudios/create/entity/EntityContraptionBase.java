package nl.melonstudios.create.entity;

import io.netty.buffer.ByteBuf;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MoverType;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.relauncher.Side;
import nl.melonstudios.create.CreateLegacy;
import nl.melonstudios.create.kinetics.contraption.Contraption;
import nl.melonstudios.create.kinetics.contraption.ContraptionRendering;
import nl.melonstudios.create.kinetics.contraption.IContraptionHolder;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public abstract class EntityContraptionBase extends Entity implements IContraptionHolder, IEntityAdditionalSpawnData {
    public EntityContraptionBase(World worldIn) {
        super(worldIn);

        if (worldIn.isRemote && CreateLegacy.proxy.getSide() == Side.CLIENT) {
            ContraptionRendering.CONTRAPTIONS_TO_RENDER.add(this);
        }
    }

    private static final double HALF_SQRT_2 = 0.5 * MathHelper.SQRT_2;
    private static final double ROTATION_BOX_ADJUSTMENT = ((1-HALF_SQRT_2) / HALF_SQRT_2) + 1;
    public enum RotationPossibility {
        NONE, X, Y, Z, ALL
    }
    protected RotationPossibility getRotationPossibility() {
        return RotationPossibility.NONE;
    }

    @Override
    protected void entityInit() {
        this.resetBB();
    }
    protected AxisAlignedBB contraptionBB;

    protected void resetBB() {
        Contraption contraption = this.attachedContraption();
        if (contraption != null) {
            double minX = 0;
            double minY = 0;
            double minZ = 0;
            double maxX = 0;
            double maxY = 0;
            double maxZ = 0;
            for (BlockPos pos : contraption.blocks.keySet()) {
                minX = Math.min(minX, pos.getX());
                minY = Math.min(minY, pos.getY());
                minZ = Math.min(minZ, pos.getZ());
                maxX = Math.max(maxX, pos.getX());
                maxY = Math.max(maxY, pos.getY());
                maxZ = Math.max(maxZ, pos.getZ());
            }

            RotationPossibility possibility = this.getRotationPossibility();

            double adjust = ROTATION_BOX_ADJUSTMENT;
            minX -= 0.5;
            minY -= 0.5;
            minZ -= 0.5;
            maxX += 0.5;
            maxY += 0.5;
            maxZ += 0.5;
            if (possibility == RotationPossibility.X) {
                double extents = Math.max(Math.abs(Math.min(minY, minZ)), Math.abs(Math.max(maxY, maxZ))) * adjust;
                minY = minZ = -extents;
                maxY = maxZ = extents;
            } else if (possibility == RotationPossibility.Y) {
                double extents = Math.max(Math.abs(Math.min(minX, minZ)), Math.abs(Math.max(maxX, maxZ))) * adjust;
                minX = minZ = -extents;
                maxX = maxZ = extents;
            } else if (possibility == RotationPossibility.Z) {
                double extents = Math.max(Math.abs(Math.min(minX, minY)), Math.abs(Math.max(maxX, maxY))) * adjust;
                minX = minY = -extents;
                maxX = maxY = extents;
            } else if (possibility == RotationPossibility.ALL) {
                double extents = Math.max(Math.abs(Math.min(minX, Math.min(minY, minZ))), Math.abs(Math.max(maxX, Math.max(maxY, maxZ)))) * adjust;
                minX = minY = minZ = -extents;
                maxX = maxY = maxZ = extents;
            }

            this.contraptionBB = new AxisAlignedBB(
                    this.posX + minX,
                    this.posY + minY,
                    this.posZ + minZ,
                    this.posX + maxX,
                    this.posY + maxY,
                    this.posZ + maxZ
            );
        }
    }

    @Override
    public void onUpdate() {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
    }

    @Override
    public float getEyeHeight() {
        return 0.0F;
    }

    @Override
    public BlockPos getPosition() {
        return new BlockPos(this.posX, this.posY, this.posZ);
    }

    @Override
    public void setPosition(double x, double y, double z) {

    }

    @Override
    public void setPositionAndRotation(double x, double y, double z, float yaw, float pitch) {

    }

    @Override
    public void setPositionAndRotationDirect(double x, double y, double z, float yaw, float pitch, int posRotationIncrements, boolean teleport) {

    }

    @Override
    public void setPositionAndUpdate(double x, double y, double z) {

    }

    public void setPositionInternal(double x, double y, double z) {
        super.setPosition(x, y, z);
        this.resetBB();
    }

    @Override
    public AxisAlignedBB getEntityBoundingBox() {
        return this.contraptionBB != null ? this.contraptionBB : super.getEntityBoundingBox();
    }

    @Override
    public void move(MoverType type, double x, double y, double z) {

    }

    @Nullable
    public abstract Contraption attachedContraption();

    @Override
    public World getWorld() {
        return this.world;
    }

    @Override
    public int getCombinedLight(BlockPos contraptionPos, int min) {
        return this.world.getCombinedLight(contraptionPos, min);
    }

    @Override
    public Biome getBiome() {
        return this.world.getBiome(this.getPosition());
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound compound) {

    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound compound) {

    }

    @Override
    public void writeSpawnData(ByteBuf buf) {

    }

    @Override
    public void readSpawnData(ByteBuf buf) {

    }

    @Override
    public void setDead() {
        super.setDead();
        if (this.world.isRemote && CreateLegacy.proxy.getSide() == Side.CLIENT) {
            ContraptionRendering.CONTRAPTIONS_TO_RENDER.remove(this);
            Contraption ctr = this.attachedContraption();
            if (ctr != null)
                ContraptionRendering.contraptionFinalized(ctr);
        }
    }
}
