package nl.melonstudios.create.tesr;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.client.model.BakedItemModel;
import net.minecraftforge.fluids.FluidStack;
import nl.melonstudios.create.tileentity.TileEntityBasin;
import nl.melonstudios.create.util.RenderUtils;
import nl.melonstudios.create.util.SubInteractionBox;
import org.lwjgl.opengl.GL11;

public class TESRBasin extends TileEntitySpecialRenderer<TileEntityBasin> {
    public TESRBasin() {
        this.rendererDispatcher = TileEntityRendererDispatcher.instance;
        this.mc = Minecraft.getMinecraft();
    }

    protected final Minecraft mc;

    @Override
    public void render(TileEntityBasin te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        this.mc.renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

        FluidStack liquid1 = te.tank1.getFluid();
        FluidStack liquid2 = te.tank2.getFluid();
        FluidStack liquid3 = te.tank3.getFluid();

        if (liquid1 != null || liquid2 != null || liquid3 != null) {
            RenderUtils.prepare(x, y, z);
            GlStateManager.disableBlend(); //transparency is an issue at times

            double level = 0.0;
            if (liquid1 != null) level = Math.max(level, liquid1.amount * 0.00085);
            if (liquid2 != null) level = Math.max(level, liquid2.amount * 0.00085);
            if (liquid3 != null) level = Math.max(level, liquid3.amount * 0.00085);
            level += 0.125;

            World world = te.getWorld();
            long time = world.getTotalWorldTime() + Math.abs(te.hashCode() | ((long)te.getPos().hashCode() << 32));
            double lvl1 = Math.sin(Math.toRadians(time % 360))*0.01+level;
            double lvl2 = Math.sin(Math.toRadians((time+90) % 360))*0.01+level;
            double lvl3 = Math.sin(Math.toRadians((time+180) % 360))*0.01+level;
            double lvl4 = Math.sin(Math.toRadians((time+270) % 360))*0.01+level;
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder renderer = tessellator.getBuffer();
            renderer.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);

            if (liquid1 != null) {
                TextureAtlasSprite sprite = this.mc.getTextureMapBlocks().getAtlasSprite(liquid1.getFluid().getStill(liquid1).toString());
                int brightness = world.getCombinedLight(te.getPos(), liquid1.getFluid().getLuminosity(liquid1));
                int l1 = brightness >> 0x10 & 0xFFFF;
                int l2 = brightness & 0xFFFF;
                int color = liquid1.getFluid().getColor(liquid1);
                int a = color >> 24 & 0xFF;
                int r = color >> 16 & 0xFF;
                int g = color >> 8 & 0xFF;
                int b = color & 0xFF;
                RenderUtils.renderFluidSurface(renderer, sprite, 0.1, 0.1, 0.9, 0.9, lvl1, lvl2, lvl3, lvl4, r, g, b, a, l1, l2);
            }
            if (liquid2 != null) {
                TextureAtlasSprite sprite = this.mc.getTextureMapBlocks().getAtlasSprite(liquid2.getFluid().getStill(liquid2).toString());
                int brightness = world.getCombinedLight(te.getPos(), liquid2.getFluid().getLuminosity(liquid2));
                int l1 = brightness >> 0x10 & 0xFFFF;
                int l2 = brightness & 0xFFFF;
                int color = liquid2.getFluid().getColor(liquid2);
                int a = color >> 24 & 0xFF;
                int r = color >> 16 & 0xFF;
                int g = color >> 8 & 0xFF;
                int b = color & 0xFF;
                RenderUtils.renderFluidSurface(renderer, sprite, 0.1, 0.1, 0.9, 0.9, lvl2, lvl3, lvl1, lvl4, r, g, b, a, l1, l2);
            }
            if (liquid3 != null) {
                TextureAtlasSprite sprite = this.mc.getTextureMapBlocks().getAtlasSprite(liquid3.getFluid().getStill(liquid3).toString());
                int brightness = world.getCombinedLight(te.getPos(), liquid3.getFluid().getLuminosity(liquid3));
                int l1 = brightness >> 0x10 & 0xFFFF;
                int l2 = brightness & 0xFFFF;
                int color = liquid3.getFluid().getColor(liquid3);
                int a = color >> 24 & 0xFF;
                int r = color >> 16 & 0xFF;
                int g = color >> 8 & 0xFF;
                int b = color & 0xFF;
                RenderUtils.renderFluidSurface(renderer, sprite, 0.1, 0.1, 0.9, 0.9, lvl4, lvl2, lvl3, lvl1, r, g, b, a, l1, l2);
            }

            tessellator.draw();

            RenderUtils.finish();
        }

        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, z);
        if (te.recipeFilter != null) {
            ItemStack stack = te.recipeFilter.getRenderItem();
            if (!stack.isEmpty()) {
                GlStateManager.pushMatrix();
                GlStateManager.enableRescaleNormal();
                GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1f);
                GlStateManager.enableBlend();
                GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);

                IBakedModel itemModel = this.mc.getRenderItem()
                        .getItemModelWithOverrides(stack, te.getWorld(), null);
                GlStateManager.translate(0.5F, 0.75F, 0.5F);
                for (int i = 0; i < 4; i++) {
                    GlStateManager.pushMatrix();
                    if (i != 0) GlStateManager.rotate(i*90.0F, 0.0F, 1.0F, 0.0F);
                    GlStateManager.translate(0.0F, 0.0F, 0.5F);
                    if (!(itemModel instanceof BakedItemModel)) GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
                    GlStateManager.scale(0.25F, 0.25F, 0.25F);
                    this.mc.getRenderItem().renderItem(stack, itemModel);
                    GlStateManager.popMatrix();
                }
                GlStateManager.disableRescaleNormal();
                GlStateManager.disableBlend();
                GlStateManager.popMatrix();
            }
        }

        SubInteractionBox.renderPotentialInteractionBoxes(this.mc.objectMouseOver, te);
        GlStateManager.popMatrix();
    }
}
