package nl.melonstudios.create.tesr.actor;

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
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3i;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import nl.melonstudios.create.block.BlockRender;
import nl.melonstudios.create.tesr.TESRKineticBase;
import nl.melonstudios.create.tileentity.actor.TileEntityContraptionInterfaceBase;
import nl.melonstudios.create.util.EnumRenderPart;

import javax.annotation.Nullable;
import java.util.List;

@SideOnly(Side.CLIENT)
public class TESRContraptionInterface<T extends TileEntityContraptionInterfaceBase> extends TileEntitySpecialRenderer<T> {
    public TESRContraptionInterface() {
        this.rendererDispatcher = TileEntityRendererDispatcher.instance;
    }

    @Override
    public void render(T te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        this.bindTexture(TESRKineticBase.bind());

        EnumFacing facing = te.getFacing();

        GlStateManager.pushMatrix();

        GlStateManager.translate(x+0.5, y+0.5, z+0.5);
        if (facing == EnumFacing.DOWN) {
            GlStateManager.rotate(180.0F, 1.0F, 0.0F, 0.0F);
        } else if (facing != EnumFacing.UP) {
            GlStateManager.rotate(-facing.getHorizontalAngle(), 0.0F, 1.0F, 0.0F);
            GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
        }
        float off = te.getConnectorOffset();
        double offset = MathHelper.clampedLerp(te.wasConnected() ? off : 0.0F, te.isConnected() ? off : 0.0F, partialTicks);
        GlStateManager.translate(-0.5F, offset-0.5F, -0.5F);

        IBlockState state = BlockRender.byEnum(EnumRenderPart.SUCTION_CUP);
        IBakedModel model = Minecraft.getMinecraft().getBlockRendererDispatcher().getModelForState(state);
        this.renderBakedModel(model, state);

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
