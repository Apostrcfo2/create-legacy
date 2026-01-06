package nl.melonstudios.ponder.scene;

import com.melonstudios.melonlib.misc.Localizer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import nl.melonstudios.create.kinetics.contraption.ContraptionRendering;
import nl.melonstudios.ponder.world.RenderWorldPonder;
import nl.melonstudios.ponder.world.WorldPonder;
import org.lwjgl.opengl.GL11;

import java.util.Objects;

@SideOnly(Side.CLIENT)
public class GuiPonder extends GuiScreen {
    private final GuiScreen parent;
    private final WorldPonder ponder;
    public GuiPonder(GuiScreen parent, WorldPonder ponder) {
        this.parent = parent;
        this.ponder = Objects.requireNonNull(ponder);
    }
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawWorldBackground(0);
        this.mc.fontRenderer.drawStringWithShadow(Localizer.translate("ponder.title"), 1, 1, -1);
        this.mc.fontRenderer.drawStringWithShadow(this.ponder.title, 1, 12, -1);
        this.mc.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        GlStateManager.pushMatrix();
        RenderHelper.disableStandardItemLighting();
        ScaledResolution resolution = new ScaledResolution(this.mc);
        GlStateManager.translate(resolution.getScaledWidth_double() * 0.5, resolution.getScaledHeight_double() * 0.5, 800.0);
        GlStateManager.rotate(180.0F, 1.0F, 0.0F, 0.0F);
        double scale = MathHelper.clampedLerp(this.ponder.scaleOld, this.ponder.scale, this.mc.getRenderPartialTicks());
        GlStateManager.scale(scale, scale, scale);
        GlStateManager.translate(this.ponder.offsetX - 0.5, this.ponder.offsetY, this.ponder.offsetZ - 0.5);
        float yaw = (float) MathHelper.clampedLerp(this.ponder.yawOld, this.ponder.yaw, this.mc.getRenderPartialTicks());
        float pitch = (float) MathHelper.clampedLerp(this.ponder.pitchOld, this.ponder.pitch, this.mc.getRenderPartialTicks());
        GlStateManager.rotate(pitch, 1, 0, 0);
        GlStateManager.rotate(yaw, 0, 1, 0);
        GlStateManager.enableDepth();
        GlStateManager.enableCull();
        GlStateManager.disableAlpha();
        RenderWorldPonder.callList(BlockRenderLayer.SOLID);
        GlStateManager.enableAlpha();
        RenderWorldPonder.callList(BlockRenderLayer.CUTOUT_MIPPED);
        RenderWorldPonder.callList(BlockRenderLayer.CUTOUT);
        GlStateManager.shadeModel(7424);
        GlStateManager.alphaFunc(516, 0.1F);
        GlStateManager.enableBlend();
        GlStateManager.depthMask(false);
        GlStateManager.shadeModel(7425);
        RenderWorldPonder.callList(BlockRenderLayer.TRANSLUCENT);
        GlStateManager.shadeModel(7424);
        GlStateManager.depthMask(true);
        GlStateManager.disableCull();
        GlStateManager.disableDepth();
        GlStateManager.popMatrix();
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    private boolean initialized = false;
    @Override
    public void initGui() {
        if (this.initialized) return;
        this.ponder.initialize();
        RenderWorldPonder.reserve(this.ponder);
        this.initialized = true;
    }

    @Override
    public void updateScreen() {
        this.ponder.tick();
    }

    @Override
    public void onGuiClosed() {
        RenderWorldPonder.delete();
        this.initialized = false;
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) {
        if (keyCode == 1) {
            this.mc.displayGuiScreen(this.parent);

            if (this.mc.currentScreen == null) {
                this.mc.setIngameFocus();
            }
        }
    }
}
