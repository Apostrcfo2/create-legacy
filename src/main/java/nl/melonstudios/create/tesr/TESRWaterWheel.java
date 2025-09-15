package nl.melonstudios.create.tesr;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import nl.melonstudios.create.block.BlockRender;
import nl.melonstudios.create.tileentity.TileEntityWaterWheel;
import nl.melonstudios.create.util.EnumRenderPart;

@SideOnly(Side.CLIENT)
public class TESRWaterWheel extends TESRKineticBase<TileEntityWaterWheel> {
    public TESRWaterWheel() {
        for (EnumFacing facing : EnumFacing.VALUES) this.facings[facing.getIndex()] = BlockRender.byEnum(EnumRenderPart.getWaterWheel(facing));
    }

    protected final IBlockState[] facings = new IBlockState[6];
    @Override
    protected void render(TileEntityWaterWheel te, float pt, float alpha) {
        EnumFacing facing = te.getRenderFacing();
        IBlockState state = this.facings[facing.getIndex()];
        IBakedModel model = this.mc.getBlockRendererDispatcher().getModelForState(state);

        this.spinModel(te, pt, facing.getAxis(), model, state, 1.0F);
    }
}
