package nl.melonstudios.create.tesr.actor;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import nl.melonstudios.create.block.actor.BlockBearingBase;
import nl.melonstudios.create.tileentity.actor.TileEntityBearing;
import nl.melonstudios.create.util.SubInteractionBox;

import java.util.Objects;

public class TESRBearing extends TESRBearingBase<TileEntityBearing> {
    @Override
    protected void render(TileEntityBearing te, float pt, float alpha) {
        super.render(te, pt, alpha);

        if (!this.mc.gameSettings.hideGUI) {
            ItemStack held = this.mc.player.getHeldItem(EnumHand.MAIN_HAND);
            if (SubInteractionBox.Helper.basicScrollRequirements(held, this.mc.player.isSneaking()) &&
                    Objects.equals(this.mc.objectMouseOver.getBlockPos(), te.getPos())) {
                EnumFacing.Axis axis = te.getState().getValue(BlockBearingBase.FACING).getAxis();
                if (axis != EnumFacing.Axis.Z) {
                    drawNumber(mc.fontRenderer, String.valueOf(te.movementType.getId()), 0.5F, 0.5F, -0.01F, 0.0F, 0.0F);
                    drawNumber(mc.fontRenderer, String.valueOf(te.movementType.getId()), 0.5F, 0.5F, 1.01F, 180.0F, 0.0F);
                }
                if (axis != EnumFacing.Axis.X) {
                    drawNumber(mc.fontRenderer, String.valueOf(te.movementType.getId()), 1.01F, 0.5F, 0.5F, 90.0F, 0.0F);
                    drawNumber(mc.fontRenderer, String.valueOf(te.movementType.getId()), -0.01F, 0.5F, 0.5F, -90.0F, 0.0F);
                }
                if (axis != EnumFacing.Axis.Y) {
                    float approxYaw = mc.player != null ? Math.round(mc.player.cameraYaw / 90.0F) * 90.0F : 0.0F;
                    drawNumber(mc.fontRenderer, String.valueOf(te.movementType.getId()), 0.5F, 1.01F, 0.5F, approxYaw, 90.0F);
                    drawNumber(mc.fontRenderer, String.valueOf(te.movementType.getId()), 0.5F, -0.01F, 0.5F, approxYaw, -90.0F);
                }
                SubInteractionBox.renderPotentialInteractionBoxes(this.mc.objectMouseOver, te);
            }
        }
    }

    public static void drawNumber(FontRenderer fontRendererIn, String str, float x, float y, float z, float viewerYaw, float viewerPitch) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, z);
        GlStateManager.glNormal3f(0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(-viewerYaw, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(viewerPitch, 1.0F, 0.0F, 0.0F);
        GlStateManager.scale(-0.025F, -0.025F, 0.025F);
        GlStateManager.disableLighting();

        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);

        fontRendererIn.drawString(str, -fontRendererIn.getStringWidth(str) / 2, -4, -1);
        GlStateManager.enableLighting();
        GlStateManager.disableBlend();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.popMatrix();
    }
}
