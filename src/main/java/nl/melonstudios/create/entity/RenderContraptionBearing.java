package nl.melonstudios.create.entity;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import nl.melonstudios.create.CreateLegacy;
import nl.melonstudios.create.kinetics.contraption.ContraptionRendering;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
@SideOnly(Side.CLIENT)
public class RenderContraptionBearing extends Render<EntityContraptionBearing> {
    public RenderContraptionBearing(RenderManager renderManager) {
        super(renderManager);
    }

    @Override
    public boolean shouldRender(EntityContraptionBearing livingEntity, ICamera camera, double camX, double camY, double camZ) {
        return true;
    }

    @Override
    public void doRender(EntityContraptionBearing entity, double x, double y, double z, float entityYaw, float partialTicks) {
        if (entity.bearing == null) return;
        GlStateManager.pushMatrix();
        GlStateManager.disableLighting();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.translate(x, y+0.5, z);
        EnumFacing facing = entity.bearing.getFacing();
        GlStateManager.rotate((float)MathHelper.clampedLerp(entity.bearing.angleOld, entity.bearing.angle, partialTicks),
            Math.abs(facing.getFrontOffsetX()),
            Math.abs(facing.getFrontOffsetY()),
            Math.abs(facing.getFrontOffsetZ())
        );
        this.bindEntityTexture(entity);
        GlStateManager.translate(-0.5, -0.5, -0.5);
        for (BlockRenderLayer layer : BlockRenderLayer.values()) {
            GlStateManager.callList(ContraptionRendering.getList(entity.contraption) + layer.ordinal());
        }
        GlStateManager.enableLighting();

        for (TileEntity te : entity.contraption.tileEntities.values()) {
            if (entity.contraption.blacklistedForRendering.contains(te)) continue;
            TileEntitySpecialRenderer<TileEntity> renderer = TileEntityRendererDispatcher.instance.getRenderer(te);
            if (renderer != null) {
                GlStateManager.pushMatrix();
                try {
                    renderer.render(te, te.getPos().getX(), te.getPos().getY(), te.getPos().getZ(), partialTicks, -1, 1.0F);
                } catch (Throwable e) {
                    CreateLegacy.logger.error("Error rendering contraption tile entity", e);
                    entity.contraption.blacklistedForRendering.add(te);
                }
                GlStateManager.popMatrix();
            }
        }
        GlStateManager.popMatrix();
    }

    @Nullable
    @Override
    protected ResourceLocation getEntityTexture(EntityContraptionBearing entity) {
        return TextureMap.LOCATION_BLOCKS_TEXTURE;
    }
}
