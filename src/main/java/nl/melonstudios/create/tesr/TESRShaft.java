package nl.melonstudios.create.tesr;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import nl.melonstudios.create.tileentity.TileEntityShaft;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@SideOnly(Side.CLIENT)
public class TESRShaft extends TESRKineticBase<TileEntityShaft> {
    @Override
    protected void render(TileEntityShaft te, float pt, float alpha) {
        this.spinShaft(te, pt, te.getRenderAxis());
    }
}
