package nl.melonstudios.create.tesr;

import com.melonstudios.melonlib.tileentity.TileEntityCachedRenderBB;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.Vec3i;
import nl.melonstudios.create.CreateLegacy;
import nl.melonstudios.create.cfg.ClientConfig;
import nl.melonstudios.create.kinetics.FastStateRendering;
import nl.melonstudios.create.util.PerFrameDebugInfo;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.List;

public class TESRBase<T extends TileEntityCachedRenderBB> extends TileEntitySpecialRenderer<T> {
    public TESRBase() {
        this.rendererDispatcher = TileEntityRendererDispatcher.instance;
        this.mc = Minecraft.getMinecraft();
    }

    private static final boolean USE_LOCAL_TIME = true;
    protected final float getAdjustedTime(float pt) {
        return USE_LOCAL_TIME ? CreateLegacy.getRenderTimeF(pt) : this.getWorld().getTotalWorldTime() + pt;
    }

    protected final Minecraft mc;
    private static final HashSet<Class<?>> VIOLATORS = new HashSet<>();
    protected final void renderBakedModel(float brightness, IBakedModel model, @Nullable IBlockState state) {
        long start = System.nanoTime();
        if (ClientConfig.fastKineticRendering) {
            if (state != null) {
                FastStateRendering.INSTANCE.renderFast(state);
                PerFrameDebugInfo.renderTimeNs += (System.nanoTime() - start);
                return;
            } else {
                if (VIOLATORS.add(this.getClass())) {
                    CreateLegacy.logger.error("{} tried to fast render model but state was null", this.getClass().getName());
                }
            }
        }
        for (EnumFacing facing : EnumFacing.VALUES) this.renderBakedQuads(brightness, model.getQuads(state, facing, 0));
        this.renderBakedQuads(brightness, model.getQuads(state, null, 0));
        PerFrameDebugInfo.renderTimeNs += (System.nanoTime() - start);
    }
    protected final void renderBakedQuads(float brightness, List<BakedQuad> quads) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder builder = tessellator.getBuffer();
        for (BakedQuad quad : quads) {
            builder.begin(7, DefaultVertexFormats.ITEM);
            builder.addVertexData(quad.getVertexData());
            builder.putColorRGB_F4(brightness, brightness, brightness);

            Vec3i vec3i = quad.getFace().getDirectionVec();
            builder.putNormal(vec3i.getX(), vec3i.getY(), vec3i.getZ());
            tessellator.draw();
        }
    }
}
