package nl.melonstudios.create.tesr;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import nl.melonstudios.create.init.ItemInit;
import nl.melonstudios.create.tileentity.TileEntityDistanceController;
import nl.melonstudios.create.util.SubInteractionBox;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class TESRDistanceController extends TileEntitySpecialRenderer<TileEntityDistanceController> {
    public TESRDistanceController() {
        super();

        this.rendererDispatcher = TileEntityRendererDispatcher.instance;
    }

    @Override
    public void render(TileEntityDistanceController te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        Minecraft mc = Minecraft.getMinecraft();
        if (!mc.gameSettings.hideGUI) {
            ItemStack held = mc.player.getHeldItem(EnumHand.MAIN_HAND);
            if (SubInteractionBox.Helper.basicScrollRequirements(held, mc.player.isSneaking())
                    && Objects.equals(mc.objectMouseOver.getBlockPos(), te.getPos())) {
                GlStateManager.pushMatrix();
                GlStateManager.translate(x, y, z);
                this.setLightmapDisabled(true);
                drawNumber(mc.fontRenderer, String.valueOf(te.setDistance), 0.5F, 0.5F, -0.01F, 0.0F, 0.0F);
                drawNumber(mc.fontRenderer, String.valueOf(te.setDistance), 1.01F, 0.5F, 0.5F, 90.0F, 0.0F);
                drawNumber(mc.fontRenderer, String.valueOf(te.setDistance), 0.5F, 0.5F, 1.01F, 180.0F, 0.0F);
                drawNumber(mc.fontRenderer, String.valueOf(te.setDistance), -0.01F, 0.5F, 0.5F, -90.0F, 0.0F);
                float approxYaw = mc.player != null ? Math.round(mc.player.cameraYaw / 90.0F) * 90.0F : 0.0F;
                drawNumber(mc.fontRenderer, String.valueOf(te.setDistance), 0.5F, 1.01F, 0.5F, approxYaw, 90.0F);
                drawNumber(mc.fontRenderer, String.valueOf(te.setDistance), 0.5F, -0.01F, 0.5F, approxYaw, -90.0F);
                SubInteractionBox.renderPotentialInteractionBoxes(mc.objectMouseOver, te);
                this.setLightmapDisabled(false);
                GlStateManager.popMatrix();
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
