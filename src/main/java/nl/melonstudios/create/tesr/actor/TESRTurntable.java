package nl.melonstudios.create.tesr.actor;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.util.EnumFacing;
import nl.melonstudios.create.tesr.TESRKineticBase;
import nl.melonstudios.create.tileentity.actor.TileEntityTurntable;

public class TESRTurntable extends TESRKineticBase<TileEntityTurntable> {
    @Override
    protected void render(TileEntityTurntable te, float pt, float alpha) {
        IBlockState state = te.getState();
        IBakedModel model = this.mc.getBlockRendererDispatcher().getModelForState(state);
        this.spinModel(te, pt, EnumFacing.Axis.Y, model, state, 1.0F);
    }
}
