package nl.melonstudios.create.tesr;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.model.BakedItemModel;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;
import nl.melonstudios.create.tileentity.TileEntityDepot;
import org.lwjgl.opengl.GL11;

import java.util.Random;

@SideOnly(Side.CLIENT)
public class TESRDepot extends TileEntitySpecialRenderer<TileEntityDepot> {
    public TESRDepot() {
        this.rendererDispatcher = TileEntityRendererDispatcher.instance;
        this.mc = Minecraft.getMinecraft();
    }

    protected final Minecraft mc;

    @Override
    public void render(TileEntityDepot te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        if (te.isEmpty()) return;
        GlStateManager.pushMatrix();
        GlStateManager.translate(x+0.5, y+0.75, z+0.5);
        GlStateManager.rotate(te.randomizedItemRotation, 0.0F, 1.0F, 0.0F);
        this.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        GlStateManager.enableRescaleNormal();
        GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1f);
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);
        if (!te.mainItem.isEmpty()) {
            GlStateManager.pushMatrix();
            ItemStack stack = te.mainItem;
            IBakedModel model = this.mc.getRenderItem().getItemModelWithOverrides(stack, te.getWorld(), null);
            if (model instanceof BakedItemModel) {
                if (this.upright(stack)) {
                    this.renderUprightItem(model, stack);
                } else {
                    this.renderFlatItem(model, stack);
                }
            }
            else this.renderCubeItem(model, stack);
            GlStateManager.popMatrix();
        }
        for (int i = 0; i < 8; i++) {
            if (!te.additionalItems[i].isEmpty()) {
                ItemStack stack = te.additionalItems[i];
                GlStateManager.pushMatrix();
                GlStateManager.translate(0.35F, 0.0F, 0.0F);
                GlStateManager.scale(0.8F, 0.8F, 0.8F);
                IBakedModel model = this.mc.getRenderItem().getItemModelWithOverrides(stack, te.getWorld(), null);
                if (model instanceof BakedItemModel) {
                    if (this.upright(stack)) {
                        this.renderUprightItem(model, stack);
                    } else {
                        this.renderFlatItem(model, stack);
                    }
                }
                else this.renderCubeItem(model, stack);
                GlStateManager.popMatrix();
            }
            GlStateManager.rotate(45.0F, 0.0F, 1.0F, 0.0F);
        }

        GlStateManager.disableRescaleNormal();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    private void renderFlatItem(IBakedModel model, ItemStack stack) {
        GlStateManager.scale(0.5F, 0.5F, 0.5F);
        GlStateManager.translate(0.0F, 0.05F, 0.0F);
        GlStateManager.rotate(-90.0F, 1.0F, 0.0F, 0.0F);
        int amount = (stack.getCount() + 6) / 8;
        this.mc.getRenderItem().renderItem(stack, model);
        Random rand = new Random(OFFSET_SEED);
        for (int i = 0; i < amount; i++) {
            GlStateManager.pushMatrix();
            GlStateManager.translate(0.0F, 0.0F, 0.05F * (i+1));
            GlStateManager.rotate(rand.nextInt(360), 0.0F, 0.0F, 1.0F);
            this.mc.getRenderItem().renderItem(stack, model);
            GlStateManager.popMatrix();
        }
    }
    private void renderCubeItem(IBakedModel model, ItemStack stack) {
        GlStateManager.scale(0.25F, 0.25F, 0.25F);
        GlStateManager.translate(0.0F, 0.5F, 0.0F);
        int amount = (stack.getCount() + 6) / 8;
        this.mc.getRenderItem().renderItem(stack, model);
        Random rand = new Random(OFFSET_SEED);
        for (int i = 0; i < amount; i++) {
            GlStateManager.pushMatrix();
            GlStateManager.translate(
                    rand.nextFloat() * 0.5F - 0.25F,
                    rand.nextFloat() * 0.5F - 0.25F,
                    rand.nextFloat() * 0.5F - 0.25F
            );
            GlStateManager.rotate(rand.nextInt(4) * 90.0F, 0.0F, 1.0F, 0.0F);
            this.mc.getRenderItem().renderItem(stack, model);
            GlStateManager.popMatrix();
        }
    }
    private void renderUprightItem(IBakedModel model, ItemStack stack) {
        GlStateManager.scale(0.5F, 0.5F, 0.5F);
        GlStateManager.translate(0.0F, 0.5F, 0.0F);
        int amount = (stack.getCount() + 6) / 8;
        this.mc.getRenderItem().renderItem(stack, model);
        Random rand = new Random(OFFSET_SEED);
        for (int i = 0; i < amount; i++) {
            GlStateManager.pushMatrix();
            GlStateManager.translate(
                    rand.nextFloat() * 0.5F - 0.25F,
                    rand.nextFloat() * 0.5F - 0.25F,
                    rand.nextFloat() * 0.2F - 0.1F
            );
            this.mc.getRenderItem().renderItem(stack, model);
            GlStateManager.popMatrix();
        }
    }

    private boolean upright(ItemStack stack) {
        int oreID = OreDictionary.getOreID("create:uprightOnBelt");
        for (int i : OreDictionary.getOreIDs(stack)) {
            if (oreID == i) return true;
        }
        return false;
    }

    private static final long OFFSET_SEED = 42069L;
}
