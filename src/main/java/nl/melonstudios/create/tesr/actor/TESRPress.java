package nl.melonstudios.create.tesr.actor;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.util.EnumFacing;
import nl.melonstudios.create.block.BlockRender;
import nl.melonstudios.create.tesr.TESRKineticBase;
import nl.melonstudios.create.tileentity.actor.TileEntityPress;
import nl.melonstudios.create.util.EnumRenderPart;

public class TESRPress<T extends TileEntityPress> extends TESRKineticBase<T> {
    @Override
    protected void render(T te, float pt, float alpha) {
        GlStateManager.pushMatrix();
        IBlockState state = BlockRender.byEnum(te.getRenderAxis() == EnumFacing.Axis.X ? EnumRenderPart.PRESS_X : EnumRenderPart.PRESS_Z);
        IBakedModel model = this.mc.getBlockRendererDispatcher().getModelForState(state);
        this.renderBakedModel(1.0F, model, state);

        this.spinShaft(te, pt, te.getRenderAxis());
        GlStateManager.popMatrix();
    }
}
