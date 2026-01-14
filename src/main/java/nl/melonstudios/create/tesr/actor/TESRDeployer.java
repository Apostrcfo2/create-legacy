package nl.melonstudios.create.tesr.actor;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.model.BakedItemModel;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import nl.melonstudios.create.block.BlockRender;
import nl.melonstudios.create.block.actor.BlockDeployer;
import nl.melonstudios.create.tesr.TESRKineticBase;
import nl.melonstudios.create.tileentity.actor.TileEntityDeployer;
import nl.melonstudios.create.util.EnumRenderPart;
import nl.melonstudios.create.util.SubInteractionBox;
import org.lwjgl.opengl.GL11;

import javax.annotation.ParametersAreNonnullByDefault;

@SideOnly(Side.CLIENT)
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class TESRDeployer extends TESRKineticBase<TileEntityDeployer> {
    @Override
    protected void render(TileEntityDeployer te, float pt, float alpha) {
        GlStateManager.pushMatrix();
        SubInteractionBox.renderPotentialInteractionBoxes(this.mc.objectMouseOver, te);
        GlStateManager.popMatrix();

        float adjustedProgress = MathHelper.clamp(te.progressOld + (te.progress - te.progressOld) * pt, te.progressOld, te.progress);
        if (adjustedProgress > 1000) adjustedProgress = Math.abs(1000.0F - (adjustedProgress-1000.0F));
        adjustedProgress *= 0.001F;

        IBlockState state = te.getState();
        EnumFacing facing = state.getValue(BlockDeployer.FACING);
        boolean rotated = state.getValue(BlockDeployer.ROTATED);
        this.spinShaft(te, pt, BlockDeployer.getShaftAxis(facing, rotated));

        if (!te.heldItem.isEmpty()) {
            ItemStack stack = te.heldItem;
            GlStateManager.pushMatrix();
            GlStateManager.enableRescaleNormal();
            GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1f);
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);

            IBakedModel itemModel = this.mc.getRenderItem()
                    .getItemModelWithOverrides(stack, te.getWorld(), null);
            GlStateManager.translate(0.5F, 0.5F, 0.5F);
            if (facing == EnumFacing.UP) {
                GlStateManager.rotate(-90.0F, 1.0F, 0.0F, 0.0F);
            } else if (facing == EnumFacing.DOWN) {
                GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
            } else {
                GlStateManager.rotate(facing.getHorizontalAngle(), 0.0F, 1.0F, 0.0F);
            }


            boolean isFlat = itemModel instanceof BakedItemModel;
            GlStateManager.translate(0.0F, 0.0F, adjustedProgress+0.4375F+(isFlat ? 0.1875F : 0.375F));
            if (!isFlat) GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
            GlStateManager.scale(0.375F, 0.375F, 0.375F);

            this.mc.getRenderItem().renderItem(stack, itemModel);

            GlStateManager.disableRescaleNormal();
            GlStateManager.disableBlend();
            GlStateManager.popMatrix();
        }

        GlStateManager.translate(
                adjustedProgress*facing.getFrontOffsetX(),
                adjustedProgress*facing.getFrontOffsetY(),
                adjustedProgress*facing.getFrontOffsetZ()
        );
        IBlockState hand = BlockRender.byEnum(EnumRenderPart.getDeployer(facing, te.skipRenderItem || te.heldItem.isEmpty()));
        IBakedModel model = this.mc.getBlockRendererDispatcher().getModelForState(hand);
        this.renderBakedModel(1.0F, model, hand);
    }
}
