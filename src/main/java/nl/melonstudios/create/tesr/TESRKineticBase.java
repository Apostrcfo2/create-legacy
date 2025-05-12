package nl.melonstudios.create.tesr;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockModelRenderer;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import nl.melonstudios.create.init.BlockInit;
import nl.melonstudios.create.tileentity.TileEntityKinetic;
import nl.melonstudios.create.util.Utils;

import javax.annotation.ParametersAreNonnullByDefault;
import java.io.File;

@ParametersAreNonnullByDefault
public abstract class TESRKineticBase<T extends TileEntityKinetic> extends TileEntitySpecialRenderer<T> {
    private static boolean porkchop = false;
    private static final ResourceLocation PORK = new ResourceLocation("textures/entity/pig/pig.png");

    public static void pork() {
        porkchop = new File(Minecraft.getMinecraft().mcDataDir, "porkchop.gears").exists();
    }

    public TESRKineticBase() {
        this.rendererDispatcher = TileEntityRendererDispatcher.instance;
        this.mc = Minecraft.getMinecraft();
        this.shaftX = BlockInit.SHAFT.getStateFromMeta(0);
        this.shaftY = BlockInit.SHAFT.getStateFromMeta(1);
        this.shaftZ = BlockInit.SHAFT.getStateFromMeta(2);
    }

    protected final Minecraft mc;
    protected final IBlockState shaftX, shaftY, shaftZ;
    @Override
    public void render(T te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, z);
        this.bindTexture(porkchop ? PORK : TextureMap.LOCATION_BLOCKS_TEXTURE);
        this.render(te, partialTicks, alpha);
        GlStateManager.popMatrix();
    }

    protected abstract void render(T te, float pt, float alpha);

    protected final void spinShaft(TileEntityKinetic te, float pt, EnumFacing.Axis axis) {
        IBlockState state = Utils.axis_choose(axis, this.shaftX, this.shaftY, this.shaftZ);
        IBakedModel model = this.mc.getBlockRendererDispatcher().getModelForState(state);
        this.spinModel(te, pt, axis, model, state, 1.0F);
    }

    protected final void spinModel(TileEntityKinetic te, float pt, EnumFacing.Axis axis, IBakedModel model, IBlockState state, float m) {
        this.rotateModel(calculateAngle(te, axis, pt, m, true), axis, model, state, 1.0F);
    }

    protected final void rotateModel(float angle, EnumFacing.Axis axis, IBakedModel model, IBlockState state, float brightness) {
        GlStateManager.pushMatrix();
        this.glRotate(angle, axis);
        GlStateManager.rotate(-90, 0, 1, 0);
        this.mc.getBlockRendererDispatcher().getBlockModelRenderer().renderModelBrightness(model, state, brightness, true);
        GlStateManager.popMatrix();
    }
    protected final void glRotate(float angle, EnumFacing.Axis axis) {
        GlStateManager.translate(0.5F, 0.5F, 0.5F);
        GlStateManager.rotate(angle,
                axis == EnumFacing.Axis.X ? 1 : 0,
                axis == EnumFacing.Axis.Y ? 1 : 0,
                axis == EnumFacing.Axis.Z ? 1 : 0
        );
        GlStateManager.translate(-0.5F, -0.5F, -0.5F);
    }

    protected final float calculateAngle(TileEntityKinetic te, EnumFacing.Axis axis, float pt, float m, boolean addOffset) {
        if (te.getSpeed() == 0) return addOffset && isAxisShifted(te.getPos(), axis) ? 22.5F : 0.0F;
        float time = te.getWorld().getTotalWorldTime() + pt;

        return ((time * 0.3F * te.getSpeed() * m) % 360) + (addOffset && isAxisShifted(te.getPos(), axis) ? 22.5F : 0.0F);
    }

    protected static boolean isAxisShifted(BlockPos pos, EnumFacing.Axis axis) {
        switch (axis) {
            case X: return (pos.getY() & 1) != (pos.getZ() & 1);
            case Y: return (pos.getX() & 1) != (pos.getZ() & 1);
            case Z: return (pos.getX() & 1) != (pos.getY() & 1);
            default:return false;
        }
    }
}
