package nl.melonstudios.create.tesr.actor;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.model.BakedItemModel;
import net.minecraftforge.oredict.OreDictionary;
import nl.melonstudios.create.block.actor.BlockBeltBase;
import nl.melonstudios.create.block.state.EnumBeltPart;
import nl.melonstudios.create.tesr.TESRKineticBase;
import nl.melonstudios.create.tileentity.actor.TileEntityBeltBase;
import org.lwjgl.opengl.GL11;

import java.util.Random;

public class TESRBeltBase<T extends TileEntityBeltBase> extends TESRKineticBase<T> {
    public TESRBeltBase() {

    }

    @Override
    protected void render(T te, float pt, float alpha) {
        IBlockState state = te.getState();
        BlockBeltBase block = (BlockBeltBase) state.getBlock();
        if (state.getValue(BlockBeltBase.PART) != EnumBeltPart.MIDDLE) {
            this.spinShaft(te, pt, block.getRotationAxis(state));
        }

        if (block.isFunctional(state)) {
            if (te.transport.isEmpty() && te.queue.isEmpty()) return;
            GlStateManager.enableRescaleNormal();
            GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1f);
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);

            EnumFacing facing = EnumFacing.getFacingFromAxis(EnumFacing.AxisDirection.POSITIVE, block.getTransportAxis(state));
            if (!te.transport.isEmpty()) {
                double pos = te.getTransportPos(pt);
                GlStateManager.pushMatrix();
                IBakedModel model = this.mc.getRenderItem().getItemModelWithOverrides(te.transport, this.getWorld(), null);
                boolean isFlat = model instanceof BakedItemModel;
                GlStateManager.translate(0.5F + facing.getFrontOffsetX() * pos, 0.75F, 0.5F + facing.getFrontOffsetZ() * pos);

                if (isFlat) {
                    if (this.upright(te.transport)) {
                        this.renderUprightItem(model, te.transport);
                    } else {
                        this.renderFlatItem(model, te.transport);
                    }
                } else this.renderCubeItem(model, te.transport);
                GlStateManager.popMatrix();
            }
            if (!te.queue.isEmpty()) {
                double pos = te.getQueuePos(pt);
                GlStateManager.pushMatrix();
                IBakedModel model = this.mc.getRenderItem().getItemModelWithOverrides(te.queue, this.getWorld(), null);
                boolean isFlat = model instanceof BakedItemModel;
                GlStateManager.translate(0.5F + facing.getFrontOffsetX() * pos, 0.75F, 0.5F + facing.getFrontOffsetZ() * pos);

                if (isFlat) {
                    if (this.upright(te.queue)) {
                        this.renderUprightItem(model, te.queue);
                    } else {
                        this.renderFlatItem(model, te.queue);
                    }
                } else this.renderCubeItem(model, te.queue);
                GlStateManager.popMatrix();
            }

            GlStateManager.disableRescaleNormal();
            GlStateManager.disableBlend();
        }
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
