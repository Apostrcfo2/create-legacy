package nl.melonstudios.create.tesr.actor;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.Vec3i;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import nl.melonstudios.create.block.BlockRender;
import nl.melonstudios.create.kinetics.FastStateRendering;
import nl.melonstudios.create.tileentity.actor.TileEntityHarvester;
import nl.melonstudios.create.util.EnumRenderPart;

import javax.annotation.Nullable;
import java.util.List;

@SideOnly(Side.CLIENT)
public class TESRHarvester extends TileEntitySpecialRenderer<TileEntityHarvester> {
    public TESRHarvester() {
        super();
        this.rendererDispatcher = TileEntityRendererDispatcher.instance;
    }

    @Override
    public void render(TileEntityHarvester te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        this.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        GlStateManager.pushMatrix();
        GlStateManager.translate(x+0.5F, y+0.5F, z+0.5F);
        EnumFacing facing = EnumFacing.HORIZONTALS[(te.getBlockMetadata() >> 1) & 3];
        GlStateManager.rotate(-facing.getHorizontalAngle(), 0.0F, 1.0F, 0.0F);
        GlStateManager.translate(0.0F, -0.125F, 0.0625F);
        GlStateManager.rotate((te.rotationOld + (te.rotation - te.rotationOld) * partialTicks) * 90.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.translate(-0.5F, -0.5F, -0.5F);

        IBlockState state = BlockRender.byEnum(EnumRenderPart.HARVESTER);
        //IBakedModel model = Minecraft.getMinecraft().getBlockRendererDispatcher().getModelForState(state);
        //this.renderBakedModel(model, state);
        FastStateRendering.INSTANCE.renderFast(state);

        GlStateManager.popMatrix();
    }

    protected final void renderBakedModel(IBakedModel model, @Nullable IBlockState state) {
        for (EnumFacing facing : EnumFacing.VALUES) this.renderBakedQuads(model.getQuads(state, facing, 0));
        this.renderBakedQuads(model.getQuads(state, null, 0));
    }
    protected final void renderBakedQuads(List<BakedQuad> quads) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder builder = tessellator.getBuffer();
        for (BakedQuad quad : quads) {
            builder.begin(7, DefaultVertexFormats.ITEM);
            builder.addVertexData(quad.getVertexData());
            builder.putColorRGB_F4((float) 1.0, (float) 1.0, (float) 1.0);

            Vec3i vec3i = quad.getFace().getDirectionVec();
            builder.putNormal(vec3i.getX(), vec3i.getY(), vec3i.getZ());
            tessellator.draw();
        }
    }
}
