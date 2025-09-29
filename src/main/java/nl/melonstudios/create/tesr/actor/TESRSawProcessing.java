package nl.melonstudios.create.tesr.actor;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.EnumFacing;
import nl.melonstudios.create.block.BlockRender;
import nl.melonstudios.create.block.actor.BlockSaw;
import nl.melonstudios.create.block.state.EnumSawRotation;
import nl.melonstudios.create.tesr.TESRKineticBase;
import nl.melonstudios.create.tileentity.actor.TileEntitySawProcessing;
import nl.melonstudios.create.util.EnumRenderPart;

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
    }
}
