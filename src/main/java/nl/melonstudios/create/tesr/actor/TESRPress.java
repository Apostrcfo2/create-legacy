package nl.melonstudios.create.tesr.actor;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import nl.melonstudios.create.block.BlockRender;
import nl.melonstudios.create.tesr.TESRKineticBase;
import nl.melonstudios.create.tileentity.actor.TileEntityPress;
import nl.melonstudios.create.util.EnumRenderPart;
import nl.melonstudios.create.util.Utils;

@SideOnly(Side.CLIENT)
public class TESRPress<T extends TileEntityPress> extends TESRKineticBase<T> {
    @Override
    protected void render(T te, float pt, float alpha) {
        GlStateManager.pushMatrix();
        float adjustedProgress = MathHelper.clamp(Utils.lerp(pt, te.lastProgress, te.progress), te.lastProgress, te.progress);
        adjustedProgress *= 0.001F;
        if (adjustedProgress > 1) adjustedProgress = Math.abs(1.0F - (adjustedProgress-1.0F));
        else adjustedProgress = adjustedProgress * adjustedProgress * adjustedProgress * adjustedProgress;
        if (adjustedProgress > 0.0F && adjustedProgress < 1.0F) adjustedProgress *= -te.multiplier();
        GlStateManager.translate(0.0F, adjustedProgress, 0.0F);
        IBlockState state = BlockRender.byEnum(te.getRenderAxis() == EnumFacing.Axis.X ? EnumRenderPart.PRESS_X : EnumRenderPart.PRESS_Z);
        IBakedModel model = this.mc.getBlockRendererDispatcher().getModelForState(state);
        this.renderBakedModel(1.0F, model, state);
        GlStateManager.popMatrix();

        this.spinShaft(te, pt, te.getRenderAxis());
    }
}
