package nl.melonstudios.ponder.scene;

import com.melonstudios.melonlib.misc.Localizer;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.EntityRenderer;
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
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import nl.melonstudios.ponder.world.RenderWorldPonder;
import nl.melonstudios.ponder.world.WorldPonder;
import org.lwjgl.opengl.GL11;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Map;
import java.util.Objects;

@SideOnly(Side.CLIENT)
public class GuiPonder extends GuiScreen {
    public static final boolean USE_RENDER_LISTS = true;
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
        GlStateManager.translate(resolution.getScaledWidth_double() * 0.5, resolution.getScaledHeight_double() * 0.5, -200.0);
        GlStateManager.rotate(180.0F, 1.0F, 0.0F, 0.0F);
        double scale = MathHelper.clampedLerp(this.ponder.scaleOld, this.ponder.scale, partialTicks);
        GlStateManager.scale(scale, scale, scale);
        GlStateManager.translate(this.ponder.offsetX - 0.5, this.ponder.offsetY, this.ponder.offsetZ - 0.5);
        float yaw = (float) MathHelper.clampedLerp(this.ponder.yawOld, this.ponder.yaw, partialTicks);
        float pitch = (float) MathHelper.clampedLerp(this.ponder.pitchOld, this.ponder.pitch, partialTicks);
        GlStateManager.rotate(pitch, 1, 0, 0);
        GlStateManager.rotate(yaw, 0, 1, 0);
        GlStateManager.clear(GL11.GL_DEPTH_BITS);
        GlStateManager.depthFunc(GL11.GL_GREATER);
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
