package nl.melonstudios.ponder.scene;

import com.melonstudios.melonlib.misc.Localizer;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.client.config.GuiUtils;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import nl.melonstudios.ponder.world.RenderWorldPonder;
import nl.melonstudios.ponder.world.WorldPonder;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import javax.annotation.ParametersAreNonnullByDefault;
import java.nio.FloatBuffer;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;

@SideOnly(Side.CLIENT)
public class GuiPonder extends GuiScreen {
    public static final boolean USE_RENDER_LISTS = true;
    private final Matrix4f matrix = new Matrix4f();
    private final FloatBuffer buf = BufferUtils.createFloatBuffer(16);
    private final Vector3f vec = new Vector3f();
    private final GuiScreen parent;
    private final WorldPonder ponder;
    public GuiPonder(GuiScreen parent, WorldPonder ponder) {
        this.parent = parent;
        this.ponder = Objects.requireNonNull(ponder);
        System.err.println("The continuous stream of OpenGL errors is suppressed while this GUI is open");
    }
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawWorldBackground(0);
        if (!USE_RENDER_LISTS) return;
        this.mc.fontRenderer.drawStringWithShadow(Localizer.translate("ponder.title"), 1, 1, -1);
        this.mc.fontRenderer.drawStringWithShadow(Localizer.translate(this.ponder.title), 1, 12, -1);
        this.mc.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        GlStateManager.pushMatrix();

        //epic matrix transforms
        ScaledResolution resolution = new ScaledResolution(this.mc);
        GlStateManager.translate(resolution.getScaledWidth_double() * 0.5, resolution.getScaledHeight_double() * 0.5, -200.0);
        GlStateManager.rotate(180.0F, 1.0F, 0.0F, 0.0F);
        double scale = MathHelper.clampedLerp(this.ponder.scaleOld, this.ponder.scale, partialTicks) * 16.0;
        GlStateManager.scale(scale, scale, scale);
        GlStateManager.translate(this.ponder.offsetX - 0.5, this.ponder.offsetY - 0.5F, this.ponder.offsetZ - 0.5);
        float yaw = (float) MathHelper.clampedLerp(this.ponder.yawOld, this.ponder.yaw, partialTicks);
        float pitch = (float) MathHelper.clampedLerp(this.ponder.pitchOld, this.ponder.pitch, partialTicks);
        GlStateManager.rotate(pitch, 1, 0, 0);
        GlStateManager.rotate(yaw, 0, 1, 0);

        //matrix stealing
        GlStateManager.getFloat(GL11.GL_MODELVIEW_MATRIX, this.buf);

        //rendering
        GlStateManager.clear(GL11.GL_DEPTH_BITS);
        GlStateManager.depthFunc(GL11.GL_GREATER);
        RenderHelper.disableStandardItemLighting();
        GlStateManager.shadeModel(7425);
        GlStateManager.color(1.0F, 1.0F, 1.0F);
        GlStateManager.enableDepth();
        GlStateManager.enableCull();
        GlStateManager.disableBlend();
        GlStateManager.depthMask(true);
        GlStateManager.disableAlpha();
        this.renderBlocks(BlockRenderLayer.SOLID);
        GlStateManager.enableAlpha();
        this.renderBlocks(BlockRenderLayer.CUTOUT_MIPPED);
        this.renderBlocks(BlockRenderLayer.CUTOUT);
        GlStateManager.shadeModel(7424);
        GlStateManager.alphaFunc(516, 0.1F);
        GlStateManager.enableBlend();
        this.renderTEs(partialTicks);
        this.renderEntities(partialTicks);
        GlStateManager.depthMask(false);
        GlStateManager.shadeModel(7425);
        this.renderBlocks(BlockRenderLayer.TRANSLUCENT);
        GlStateManager.shadeModel(7424);
        GlStateManager.depthMask(true);

        GlStateManager.popMatrix();
        GlStateManager.depthFunc(GL11.GL_LEQUAL);
        GlStateManager.clear(GL11.GL_DEPTH_BITS);
        super.drawScreen(mouseX, mouseY, partialTicks);

        if (!this.ponder.displayedTooltips.isEmpty()) {
            this.matrix.load(this.buf);
            this.buf.clear();
            for (PonderTooltip tooltip : this.ponder.displayedTooltips) {
                float x = tooltip.x;
                float y = tooltip.y;
                float z = tooltip.z;
                float w = 1.0F;
                this.vec.x = this.matrix.m00 * x + this.matrix.m10 * y + this.matrix.m20 * z + this.matrix.m30 * w;
                this.vec.y = this.matrix.m01 * x + this.matrix.m11 * y + this.matrix.m21 * z + this.matrix.m31 * w;
                this.vec.z = this.matrix.m02 * x + this.matrix.m12 * y + this.matrix.m22 * z + this.matrix.m32 * w;
                int adjustedX = (int) (this.vec.x);
                int adjustedY = (int) (this.vec.y);
                GuiUtils.drawHoveringText(
                        Collections.singletonList(tooltip.text),
                        adjustedX, adjustedY,
                        resolution.getScaledWidth(), resolution.getScaledHeight(),
                        100, this.fontRenderer
                );
            }
        }
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
    public boolean doesGuiPauseGame() {
        return false;
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

    private void renderBlocks(BlockRenderLayer layer) {
        if (USE_RENDER_LISTS) {
            RenderWorldPonder.callList(layer);
        } else {
            BlockRendererDispatcher dispatcher = this.mc.getBlockRendererDispatcher();
            for (Map.Entry<BlockPos, IBlockState> entry : this.ponder.scene.blocks.entrySet()) {
                IBlockState state = entry.getValue();
                if (state.getBlock().canRenderInLayer(state, layer)) {
                    BlockPos pos = entry.getKey();
                    GlStateManager.pushMatrix();
                    GlStateManager.translate(pos.getX(), pos.getY(), pos.getZ());
                    dispatcher.renderBlockBrightness(state, 1.0F);
                    GlStateManager.popMatrix();
                }
            }
        }
    }
    private void renderTEs(float pt) {
        TileEntityRendererDispatcher dispatcher = TileEntityRendererDispatcher.instance;
        for (TileEntity te : this.ponder.scene.tileEntities.values()) {
            TileEntitySpecialRenderer<TileEntity> tesr = dispatcher.getRenderer(te);
            if (tesr != null) tesr.render(te, te.getPos().getX(), te.getPos().getY(), te.getPos().getZ(), pt, -1, 1.0F);
        }
        for (TileEntity te : this.ponder.scene.nonTickingTileEntities.values()) {
            TileEntitySpecialRenderer<TileEntity> tesr = dispatcher.getRenderer(te);
            if (tesr != null) tesr.render(te, te.getPos().getX(), te.getPos().getY(), te.getPos().getZ(), pt, -1, 1.0F);
        }
    }
    @SuppressWarnings("unchecked")
    private void renderEntities(float pt) {
        for (Entity entity : this.ponder.scene.entityList) {
            Render<Entity> render = (Render<Entity>) this.mc.getRenderManager().entityRenderMap.get(entity.getClass());
            if (render.shouldRender(entity, FakeCam.FAKE_CAM, 0, 0, 0)) {
                render.doRender(entity, entity.posX, entity.posY, entity.posZ, entity.rotationYaw, pt);
            }
        }
        for (Entity entity : this.ponder.scene.renderOnlyEntityList) {
            Render<Entity> render = (Render<Entity>) this.mc.getRenderManager().entityRenderMap.get(entity.getClass());
            if (render.shouldRender(entity, FakeCam.FAKE_CAM, 0, 0, 0)) {
                render.doRender(entity, entity.posX, entity.posY, entity.posZ, entity.rotationYaw, pt);
            }
        }
        for (Entity entity : this.ponder.scene.nonTickingRenderOnlyEntityList) {
            Render<Entity> render = (Render<Entity>) this.mc.getRenderManager().entityRenderMap.get(entity.getClass());
            if (render.shouldRender(entity, FakeCam.FAKE_CAM, 0, 0, 0)) {
                render.doRender(entity, entity.posX, entity.posY, entity.posZ, entity.rotationYaw, pt);
            }
        }
    }

    @ParametersAreNonnullByDefault
    @MethodsReturnNonnullByDefault
    private static class FakeCam implements ICamera {
        private static final FakeCam FAKE_CAM = new FakeCam();

        @Override
        public boolean isBoundingBoxInFrustum(AxisAlignedBB aabb) {
            return true;
        }

        @Override
        public void setPosition(double xIn, double yIn, double zIn) {

        }
    }
}
