package nl.melonstudios.create.util;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import nl.melonstudios.create.kinetics.contraption.IContraptionActor;
import nl.melonstudios.create.tileentity.marker.ITileEntityWithSubInteractions;
import nl.melonstudios.ponder.IVirtualizable;

import javax.annotation.Nullable;
import java.util.Objects;

public final class SubInteractionBox {
    private final float minX, minY, minZ;
    private final float maxX, maxY, maxZ;
    private final Interaction interaction;

    public SubInteractionBox(float minX, float minY, float minZ, float size, Interaction interaction) {
        this.minX = minX;
        this.minY = minY;
        this.minZ = minZ;
        this.maxX = minX + size;
        this.maxY = minY + size;
        this.maxZ = minZ + size;
        this.interaction = interaction;
    }

    public boolean isInside(float hitX, float hitY, float hitZ) {
        return this.checkX(hitX) && this.checkY(hitY) && this.checkZ(hitZ);
    }

    private boolean checkX(float x) {
        return x >= this.getMinX() && x <= this.getMaxX();
    }
    private boolean checkY(float y) {
        return y >= this.getMinY() && y <= this.getMaxY();
    }
    private boolean checkZ(float z) {
        return z >= this.getMinZ() && z <= this.getMaxZ();
    }

    public float getMinX() {
        return this.minX;
    }
    public float getMinY() {
        return this.minY;
    }
    public float getMinZ() {
        return this.minZ;
    }

    public float getMaxX() {
        return this.maxX;
    }
    public float getMaxY() {
        return this.maxY;
    }
    public float getMaxZ() {
        return this.maxZ;
    }

    public Interaction getInteraction() {
        return this.interaction;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SubInteractionBox)) return false;
        SubInteractionBox that = (SubInteractionBox) o;
        return Float.compare(this.minX, that.minX) == 0
                && Float.compare(this.minY, that.minY) == 0
                && Float.compare(this.minZ, that.minZ) == 0
                && Float.compare(this.maxX, that.maxX) == 0
                && Float.compare(this.maxY, that.maxY) == 0
                && Float.compare(this.maxZ, that.maxZ) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.minX, this.minY, this.minZ, this.maxX, this.maxY, this.maxZ);
    }

    @SideOnly(Side.CLIENT)
    public void render() {
        GlStateManager.disableAlpha();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(
                GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
                GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO
        );
        GlStateManager.glLineWidth(5.0F);
        GlStateManager.disableTexture2D();
        RenderHelper.disableStandardItemLighting();

        RenderGlobal.drawBoundingBox(this.minX, this.minY, this.minZ, this.maxX, this.maxY, this.maxZ, 1.0F, 1.0F, 1.0F, 1.0F);

        RenderHelper.enableStandardItemLighting();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
    }

    @SideOnly(Side.CLIENT)
    public static <T extends TileEntity & ITileEntityWithSubInteractions> boolean renderPotentialInteractionBoxes(RayTraceResult cameraPointer, T te) {
        if (te instanceof IVirtualizable && ((IVirtualizable)te).isVirtual()) return false;
        if (te instanceof IContraptionActor && ((IContraptionActor)te).isOnContraption()) return false;

        boolean status = false;
        if (Objects.equals(te.getPos(), cameraPointer.getBlockPos())) {
            float hitX = (float) (cameraPointer.hitVec.x - cameraPointer.getBlockPos().getX());
            float hitY = (float) (cameraPointer.hitVec.y - cameraPointer.getBlockPos().getY());
            float hitZ = (float) (cameraPointer.hitVec.z - cameraPointer.getBlockPos().getZ());

            for (SubInteractionBox box : te.getSubInteractionBoxes()) {
                if (box.isInside(hitX, hitY, hitZ)) {
                    box.render();
                    status = true;
                }
            }
        }
        return status;
    }

    @FunctionalInterface
    public interface Interaction {
        boolean interact(@Nullable EntityPlayer player, boolean sneaking, ItemStack held);
    }

    @FunctionalInterface
    public interface ScrollInteraction extends Interaction {
        @Override
        default boolean interact(@Nullable EntityPlayer player, boolean sneaking, ItemStack held) {
            return false;
        }

        boolean scroll(EntityPlayer player, boolean sneaking, ItemStack held, int direction);
    }

    public static boolean handleInteraction(World world, BlockPos pos, @Nullable EntityPlayer player, boolean adjust,
                                            boolean sneaking, ItemStack held, float hitX, float hitY, float hitZ) {
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof ITileEntityWithSubInteractions) {
            if (adjust) {
                hitX -= pos.getX();
                hitY -= pos.getY();
                hitZ -= pos.getZ();
            }
            for (SubInteractionBox box : ((ITileEntityWithSubInteractions)te).getSubInteractionBoxes()) {
                if (box.isInside(hitX, hitY, hitZ)) {
                    return box.getInteraction().interact(player, sneaking, held);
                }
            }
        }
        return false;
    }

    public static class Helper {
        private Helper() {}

        public static SubInteractionBox createDefaultAt(float x, float y, float z, Interaction interaction) {
            Objects.requireNonNull(interaction, "Interaction cannot be null!");
            return new SubInteractionBox(x-0.125F, y-0.125F, z-0.125F, 0.25F, interaction);
        }

        public static SubInteractionBox createCenteredSide(@Nullable EnumFacing side, float size, Interaction interaction) {
            Objects.requireNonNull(interaction, "Interaction cannot be null!");
            float halfSize = size * 0.5F;
            if (side == null) {
                return new SubInteractionBox(
                        0.5F - halfSize, 0.5F - halfSize, 0.5F - halfSize,
                        size, interaction
                );
            }
            switch (side) {
                case DOWN:
                    return new SubInteractionBox(
                            0.5F - halfSize, -halfSize, 0.5F - halfSize,
                            size, interaction
                    );
                case UP:
                    return new SubInteractionBox(
                            0.5F - halfSize, 1.0F - halfSize, 0.5F - halfSize,
                            size, interaction
                    );
                case NORTH:
                    return new SubInteractionBox(
                            0.5F - halfSize, 0.5F - halfSize, -halfSize,
                            size, interaction
                    );
                case SOUTH:
                    return new SubInteractionBox(
                            0.5F - halfSize, 0.5F - halfSize, 1.0F - halfSize,
                            size, interaction
                    );
                case WEST:
                    return new SubInteractionBox(
                            -halfSize, 0.5F - halfSize, 0.5F - halfSize,
                            size, interaction
                    );
                case EAST:
                    return new SubInteractionBox(
                            1.0F - halfSize, 0.5F - halfSize, 0.5F - halfSize,
                            size, interaction
                    );
                default:
                    return new SubInteractionBox(
                            0.5F - halfSize, 0.5F - halfSize, 0.5F - halfSize,
                            size, interaction
                    );
            }
        }

        public static SubInteractionBox forFunnel(EnumFacing side, float size, Interaction interaction) {
            Objects.requireNonNull(interaction, "Interaction cannot be null!");
            if (Objects.requireNonNull(side, "Side cannot be null!").getAxis() == EnumFacing.Axis.Y) {
                throw new IllegalArgumentException("Side must be horizontal!");
            }
            float halfSize = size * 0.5F;
            switch (side) {
                default:throw new IllegalArgumentException("???");
            }
        }
    }
}
