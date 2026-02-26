package nl.melonstudios.create.tesr.actor;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.model.BakedItemModel;
import nl.melonstudios.create.block.BlockRender;
import nl.melonstudios.create.block.actor.BlockSaw;
import nl.melonstudios.create.block.state.EnumSawRotation;
import nl.melonstudios.create.tesr.TESRKineticBase;
import nl.melonstudios.create.tileentity.actor.TileEntitySawProcessing;
import nl.melonstudios.create.util.EnumRenderPart;
import nl.melonstudios.create.util.SubInteractionBox;
import org.lwjgl.opengl.GL11;

public class TESRSawProcessing extends TESRKineticBase<TileEntitySawProcessing> {
    @Override
    protected void render(TileEntitySawProcessing te, float pt, float alpha) {
        EnumSawRotation rot = te.getState().getValue(BlockSaw.FACING);
        EnumFacing.Axis axis = rot == EnumSawRotation.UP_ALONG_X ? EnumFacing.Axis.X : EnumFacing.Axis.Z;
        this.spinShaft(te, pt, axis);
        GlStateManager.pushMatrix();
        GlStateManager.translate(0.0F, 0.25F, 0.0F);
        IBlockState saw = BlockRender.byEnum(EnumRenderPart.getSaw(axis));
        this.spinModel(te, pt, axis, this.mc.getBlockRendererDispatcher().getModelForState(saw), saw, 4.0F);
        GlStateManager.popMatrix();

        //Filter rendering
        if (te.recipeFilter != null) {
            ItemStack filter = te.recipeFilter.getRenderItem();
            if (!filter.isEmpty()) {
                GlStateManager.pushMatrix();
                GlStateManager.enableRescaleNormal();
                GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1f);
                GlStateManager.enableBlend();
                GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);

                IBakedModel model = this.mc.getRenderItem()
                                .getItemModelWithOverrides(filter, te.getWorld(), null);
                GlStateManager.translate(0.5F, 0.75F, 0.5F);
                boolean x = te.getBlockMetadata() == 4;
                if (x) {
                    GlStateManager.rotate(90.0F, 0.0F, 1.0F, 0.0F);
                }

                GlStateManager.pushMatrix();
                GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
                GlStateManager.pushMatrix();
                GlStateManager.translate(0.0F, -0.25F, 0.0F);
                GlStateManager.scale(0.2F, 0.2F, 0.2F);
                this.mc.getRenderItem().renderItem(filter, model);
                GlStateManager.popMatrix();
                GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
                GlStateManager.translate(0.0F, -0.25F, 0.0F);
                GlStateManager.scale(0.2F, 0.2F, 0.2F);
                this.mc.getRenderItem().renderItem(filter, model);
                GlStateManager.popMatrix();

                GlStateManager.disableRescaleNormal();
                GlStateManager.disableBlend();
                GlStateManager.popMatrix();
            }
        }

        //Rendering the item currently being cut
        if (!te.currentlyProcessing.isEmpty()) {
            ItemStack stack = te.currentlyProcessing;
            int maxProgress = te.currentRecipe == null ? te.getProgressTick() * 10 : te.currentRecipe.processingTime * stack.getCount();
            double progress = MathHelper.clampedLerp(te.lastProgress, te.progress, pt);
            double movement = te.getSpeed() != 0.0F ? progress / maxProgress : 0.5;

            GlStateManager.pushMatrix();
            GlStateManager.enableRescaleNormal();
            GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1f);
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);

            IBakedModel model = this.mc.getRenderItem().getItemModelWithOverrides(stack, te.getWorld(), null);
            GlStateManager.translate(0.5F, 0.75F, 0.5F);


            switch (te.getProcessingDirection()) {
                case EAST:
                    GlStateManager.rotate(90.0F, 0.0F, 1.0F, 0.0F);
                    break;
                case NORTH:
                    GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
                    break;
                case WEST:
                    GlStateManager.rotate(-90.0F, 0.0F, 1.0F, 0.0F);
                    break;
            }
            GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
            GlStateManager.translate(0.0F, -0.5F+movement, 0.0F);
            if (model instanceof BakedItemModel) {
                GlStateManager.scale(0.5, 0.5, 0.5);
            } else {
            GlStateManager.scale(0.25, 0.25, 0.25);
            }
            this.mc.getRenderItem().renderItem(stack, model);

            GlStateManager.disableRescaleNormal();
            GlStateManager.disableBlend();
            GlStateManager.popMatrix();
        }

        if (!te.outputQueue.isEmpty()) {
            ItemStack stack = te.outputQueue;

            GlStateManager.pushMatrix();
            GlStateManager.enableRescaleNormal();
            GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1f);
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);

            IBakedModel model = this.mc.getRenderItem().getItemModelWithOverrides(stack, te.getWorld(), null);
            GlStateManager.translate(0.5F, 0.75F, 0.5F);


            switch (te.getProcessingDirection()) {
                case EAST:
                    GlStateManager.rotate(90.0F, 0.0F, 1.0F, 0.0F);
                    break;
                case NORTH:
                    GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
                    break;
                case WEST:
                    GlStateManager.rotate(-90.0F, 0.0F, 1.0F, 0.0F);
                    break;
            }
            GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
            if (te.getSpeed() != 0.0F) GlStateManager.translate(0.0F, 0.5F, 0.0F);
            GlStateManager.scale(0.25, 0.25, 0.25);
            this.mc.getRenderItem().renderItem(stack, model);

            GlStateManager.disableRescaleNormal();
            GlStateManager.disableBlend();
            GlStateManager.popMatrix();
        }

        //Render hovered sub interaction box
        if (!this.mc.gameSettings.hideGUI) {
            GlStateManager.pushMatrix();
            SubInteractionBox.renderPotentialInteractionBoxes(this.mc.objectMouseOver, te);
            GlStateManager.popMatrix();
        }
    }
}
