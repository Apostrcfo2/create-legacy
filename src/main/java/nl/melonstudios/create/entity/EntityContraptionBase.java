package nl.melonstudios.create.entity;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MoverType;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import nl.melonstudios.create.kinetics.contraption.Contraption;
import nl.melonstudios.create.kinetics.contraption.IContraptionHolder;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public abstract class EntityContraptionBase extends Entity implements IContraptionHolder {
    public EntityContraptionBase(World worldIn) {
        super(worldIn);
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
            double maxX = 0;
            double maxY = 0;
            double maxZ = 0;
            for (BlockPos pos : contraption.blocks.keySet()) {
                maxX = Math.max(maxX, Math.abs(pos.getX()));
                maxY = Math.max(maxY, Math.abs(pos.getY()));
                maxZ = Math.max(maxZ, Math.abs(pos.getZ()));
            }

            RotationPossibility possibility = this.getRotationPossibility();

            double adjust = ROTATION_BOX_ADJUSTMENT;
            if (possibility == RotationPossibility.NONE) {
                maxX++;
                maxY++;
                maxZ++;
            } else if (possibility == RotationPossibility.X) {
                maxX++;
                maxY = Math.max(maxY , maxZ) * adjust;
                maxZ = maxY;
            } else if (possibility == RotationPossibility.Y) {
                maxX = Math.max(maxX, maxZ) * adjust;
                maxY++;
                maxZ = maxX;
            } else if (possibility == RotationPossibility.Z) {
                maxX = Math.max(maxX, maxY) * adjust;
                maxY = maxX;
                maxZ++;
            } else if (possibility == RotationPossibility.ALL) {
                maxX = Math.max(maxX, Math.max(maxY, maxZ)) * adjust;
                maxY = maxX;
                maxZ = maxY;
            }


            this.contraptionBB = new AxisAlignedBB(
                    this.posX - maxX,
                    this.posY + 0.5 - maxY,
                    this.posZ - maxZ,
                    this.posX + maxX,
                    this.posY + 0.5 + maxY,
                    this.posZ + maxZ
            );
        }
    }

    @Override
    public float getEyeHeight() {
        return 0.0F;
    }

    @Override
    public void setPosition(double x, double y, double z) {
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
}
