package nl.melonstudios.create.tesr;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.Vec3i;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import nl.melonstudios.create.block.BlockBlazeBurner;
import nl.melonstudios.create.block.BlockRender;
import nl.melonstudios.create.tileentity.TileEntityBlazeBurner;
import nl.melonstudios.create.util.EnumRenderPart;

import javax.annotation.Nullable;
import java.util.List;

@SideOnly(Side.CLIENT)
public class TESRBlazeBurner extends TileEntitySpecialRenderer<TileEntityBlazeBurner> {
    public TESRBlazeBurner() {
        this.rendererDispatcher = TileEntityRendererDispatcher.instance;
    }

    @Override
    public void render(TileEntityBlazeBurner te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        this.bindTexture(TESRKineticBase.bind());
        GlStateManager.pushMatrix();
        GlStateManager.translate(x+0.5, y, z+0.5);
        GlStateManager.rotate(-this.rendererDispatcher.entityYaw, 0.0F, 1.0F, 0.0F);
        GlStateManager.translate(-0.5F, 0.0F, -0.5F);
        IBlockState state = BlockRender.byEnum(EnumRenderPart.getBlaze(te.getState().getValue(BlockBlazeBurner.VARIANT)));
        IBakedModel model = Minecraft.getMinecraft().getBlockRendererDispatcher().getModelForState(state);
        this.renderBakedModel(2.0F, model, state);
        GlStateManager.popMatrix();
    }

    protected final void renderBakedModel(float brightness, IBakedModel model, @Nullable IBlockState state) {
        for (EnumFacing facing : EnumFacing.VALUES) this.renderBakedQuads(brightness, model.getQuads(state, facing, 0));
        this.renderBakedQuads(brightness, model.getQuads(state, null, 0));
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
