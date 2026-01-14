package nl.melonstudios.create.tesr.actor;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import nl.melonstudios.create.block.BlockRender;
import nl.melonstudios.create.block.actor.BlockDeployer;
import nl.melonstudios.create.tesr.TESRKineticBase;
import nl.melonstudios.create.tileentity.actor.TileEntityDeployer;
import nl.melonstudios.create.util.EnumRenderPart;
import nl.melonstudios.create.util.SubInteractionBox;

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
