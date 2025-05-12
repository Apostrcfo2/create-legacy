package nl.melonstudios.create.tesr;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.util.EnumFacing;
import nl.melonstudios.create.init.BlockInit;
import nl.melonstudios.create.tileentity.TileEntityCogwheel;
import nl.melonstudios.create.util.Utils;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class TESRCogwheel extends TESRKineticBase<TileEntityCogwheel> {
    public TESRCogwheel() {
        this.smallCogX = BlockInit.COG_SMALL.getStateFromMeta(0);
        this.smallCogY = BlockInit.COG_SMALL.getStateFromMeta(1);
        this.smallCogZ = BlockInit.COG_SMALL.getStateFromMeta(2);

        this.largeCogX = BlockInit.COG_LARGE.getStateFromMeta(0);
        this.largeCogY = BlockInit.COG_LARGE.getStateFromMeta(1);
        this.largeCogZ = BlockInit.COG_LARGE.getStateFromMeta(2);
    }

    protected final IBlockState smallCogX, smallCogY, smallCogZ;
    protected final IBlockState largeCogX, largeCogY, largeCogZ;

    @Override
    protected void render(TileEntityCogwheel te, float pt, float alpha) {
        EnumFacing.Axis axis = te.getRenderAxis();
        IBlockState state = te.isLarge() ?
                Utils.axis_choose(axis, this.largeCogX, this.largeCogY, this.largeCogZ) :
                Utils.axis_choose(axis, this.smallCogX, this.smallCogY, this.smallCogZ);
        IBakedModel model = this.mc.getBlockRendererDispatcher().getModelForState(state);
        this.spinModel(te, pt, axis, model, state, 1.0F);
    }
}
