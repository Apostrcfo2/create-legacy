package nl.melonstudios.create.kinetics;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.Vec3i;
import net.minecraftforge.client.resource.IResourceType;
import net.minecraftforge.client.resource.ISelectiveResourceReloadListener;
import net.minecraftforge.client.resource.VanillaResourceType;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import nl.melonstudios.create.CreateLegacy;

import javax.annotation.Nonnull;
import java.util.function.Predicate;

@SideOnly(Side.CLIENT)
public final class FastStateRendering implements ISelectiveResourceReloadListener {
    public static final FastStateRendering INSTANCE = new FastStateRendering();

    private final Minecraft minecraft = Minecraft.getMinecraft();
    private final BlockRendererDispatcher renderer = this.minecraft.getBlockRendererDispatcher();
    private final Tessellator tessellator = Tessellator.getInstance();
    private final Object2IntMap<IBlockState> renderLists = new Object2IntOpenHashMap<>();

    private FastStateRendering() {
        ((IReloadableResourceManager)this.minecraft.getResourceManager()).registerReloadListener(this);
    }

    public void renderFast(IBlockState state) {
        synchronized (this.renderLists) {
            if (!this.renderLists.containsKey(state)) {
                CreateLegacy.logger.debug("Creating fast model list for {}", state);
                BufferBuilder buffer = this.tessellator.getBuffer();
                IBakedModel model = this.renderer.getModelForState(state);

                int list = GlStateManager.glGenLists(1);

                GlStateManager.glNewList(list, 4864);
                buffer.begin(7, DefaultVertexFormats.ITEM);

                for (EnumFacing side : EnumFacing.VALUES) {
                    for (BakedQuad quad : model.getQuads(state, side, 0L)) {
                        buffer.addVertexData(quad.getVertexData());
                        buffer.putColorRGB_F4(1.0F, 1.0F, 1.0F);

                        Vec3i normal = quad.getFace().getDirectionVec();
                        buffer.putNormal(normal.getX(), normal.getY(), normal.getZ());
                    }
                }
                for (BakedQuad quad : model.getQuads(state, null, 0L)) {
                    buffer.addVertexData(quad.getVertexData());
                    buffer.putColorRGB_F4(1.0F, 1.0F, 1.0F);

                    Vec3i normal = quad.getFace().getDirectionVec();
                    buffer.putNormal(normal.getX(), normal.getY(), normal.getZ());
                }

                this.tessellator.draw();
                GlStateManager.glEndList();

                this.renderLists.put(state, list);
            }
            int list = this.renderLists.get(state);
            GlStateManager.callList(list);
        }
    }

    public void cleanup() {
        synchronized (this.renderLists) {
            for (int list : this.renderLists.values()) {
                GlStateManager.glDeleteLists(list, 1);
            }
            this.renderLists.clear();
        }
    }

    @Override
    public void onResourceManagerReload(@Nonnull IResourceManager manager, Predicate<IResourceType> predicate) {
        if (predicate.test(VanillaResourceType.MODELS) || predicate.test(VanillaResourceType.TEXTURES)) {
            CreateLegacy.logger.debug("Clearing fast model lists");
            this.cleanup();
        }
    }
}
