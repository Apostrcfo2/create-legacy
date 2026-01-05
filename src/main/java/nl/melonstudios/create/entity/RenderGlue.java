package nl.melonstudios.create.entity;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import nl.melonstudios.create.item.ItemGlue;
import nl.melonstudios.ponder.world.WorldPonder;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
@SideOnly(Side.CLIENT)
public class RenderGlue extends Render<EntityGlue> {
    private static final ResourceLocation GLUE_TEXTURES = new ResourceLocation("create", "textures/entity/glue.png");
    public RenderGlue(RenderManager renderManager) {
        super(renderManager);
    }

    @Override
    public boolean shouldRender(EntityGlue livingEntity, ICamera camera, double camX, double camY, double camZ) {
        return (Minecraft.getMinecraft().player.getHeldItemMainhand().getItem() instanceof ItemGlue
                || Minecraft.getMinecraft().player.getHeldItemOffhand().getItem() instanceof ItemGlue)
                && super.shouldRender(livingEntity, camera, camX, camY, camZ);
    }

    @Override
    public void doRender(EntityGlue entity, double x, double y, double z, float entityYaw, float partialTicks) {
        GlStateManager.pushMatrix();
        GlStateManager.disableCull();
        GlStateManager.disableLighting();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.translate(x-0.5, y, z-0.5);
        this.bindEntityTexture(entity);
        GlStateManager.enableAlpha();
        GlStateManager.enableBlend();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder builder = tessellator.getBuffer();
        builder.begin(7, DefaultVertexFormats.POSITION_TEX);
        switch (entity.getSurface().side.getAxis()) {
            case X:
                builder.pos(1.01F, 0.0F, 0.0F).tex(0.0F, 0.0F).endVertex();
                builder.pos(1.01F, 0.0F, 1.0F).tex(1.0F, 0.0F).endVertex();
                builder.pos(1.01F, 1.0F, 1.0F).tex(1.0F, 1.0F).endVertex();
                builder.pos(1.01F, 1.0F, 0.0F).tex(0.0F, 1.0F).endVertex();

                builder.pos(0.99F, 0.0F, 0.0F).tex(0.0F, 0.0F).endVertex();
                builder.pos(0.99F, 0.0F, 1.0F).tex(1.0F, 0.0F).endVertex();
                builder.pos(0.99F, 1.0F, 1.0F).tex(1.0F, 1.0F).endVertex();
                builder.pos(0.99F, 1.0F, 0.0F).tex(0.0F, 1.0F).endVertex();
                break;
            case Y:
                builder.pos(0.0F, 1.01F, 0.0F).tex(0.0F, 0.0F).endVertex();
                builder.pos(1.0F, 1.01F, 0.0F).tex(1.0F, 0.0F).endVertex();
                builder.pos(1.0F, 1.01F, 1.0F).tex(1.0F, 1.0F).endVertex();
                builder.pos(0.0F, 1.01F, 1.0F).tex(0.0F, 1.0F).endVertex();

                builder.pos(0.0F, 0.99F, 0.0F).tex(0.0F, 0.0F).endVertex();
                builder.pos(1.0F, 0.99F, 0.0F).tex(1.0F, 0.0F).endVertex();
                builder.pos(1.0F, 0.99F, 1.0F).tex(1.0F, 1.0F).endVertex();
                builder.pos(0.0F, 0.99F, 1.0F).tex(0.0F, 1.0F).endVertex();
                break;
            case Z:
                builder.pos(0.0F, 0.0F, 1.01F).tex(0.0F, 0.0F).endVertex();
                builder.pos(1.0F, 0.0F, 1.01F).tex(1.0F, 0.0F).endVertex();
                builder.pos(1.0F, 1.0F, 1.01F).tex(1.0F, 1.0F).endVertex();
                builder.pos(0.0F, 1.0F, 1.01F).tex(0.0F, 1.0F).endVertex();

                builder.pos(0.0F, 0.0F, 0.99F).tex(0.0F, 0.0F).endVertex();
                builder.pos(1.0F, 0.0F, 0.99F).tex(1.0F, 0.0F).endVertex();
                builder.pos(1.0F, 1.0F, 0.99F).tex(1.0F, 1.0F).endVertex();
                builder.pos(0.0F, 1.0F, 0.99F).tex(0.0F, 1.0F).endVertex();
                break;
        }
        tessellator.draw();
        GlStateManager.enableLighting();
        GlStateManager.enableCull();
        GlStateManager.popMatrix();
    }

    @Nullable
    @Override
    protected ResourceLocation getEntityTexture(EntityGlue entity) {
        return GLUE_TEXTURES;
    }
}
