package nl.melonstudios.create.tesr.actor;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.util.EnumFacing;
import nl.melonstudios.create.block.BlockRender;
import nl.melonstudios.create.tesr.TESRKineticBase;
import nl.melonstudios.create.tileentity.actor.TileEntityMillstone;
import nl.melonstudios.create.util.EnumRenderPart;

public class TESRMillstone extends TESRKineticBase<TileEntityMillstone> {
    @Override
    protected void render(TileEntityMillstone te, float pt, float alpha) {
        IBlockState state = BlockRender.byEnum(EnumRenderPart.MILLSTONE);
        IBakedModel model = this.mc.getBlockRendererDispatcher().getModelForState(state);
        this.spinModel(te, pt, EnumFacing.Axis.Y, model, state, 1.0F);
    }
}
