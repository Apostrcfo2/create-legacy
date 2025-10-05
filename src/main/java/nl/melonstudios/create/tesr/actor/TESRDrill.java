package nl.melonstudios.create.tesr.actor;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.util.EnumFacing;
import nl.melonstudios.create.block.BlockRender;
import nl.melonstudios.create.block.actor.BlockDrill;
import nl.melonstudios.create.tesr.TESRKineticBase;
import nl.melonstudios.create.tileentity.actor.TileEntityDrill;
import nl.melonstudios.create.util.EnumRenderPart;

public class TESRDrill<T extends TileEntityDrill> extends TESRKineticBase<T> {
    @Override
    protected void render(T te, float pt, float alpha) {
        EnumFacing facing = te.getState().getValue(BlockDrill.FACING);
        IBlockState state = BlockRender.byEnum(EnumRenderPart.getDrillHead(facing));
        IBakedModel model = this.mc.getBlockRendererDispatcher().getModelForState(state);
        this.spinModel(te, pt, facing.getAxis(), model, state, 1.0F);
    }
}
