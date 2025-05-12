package nl.melonstudios.create.tesr;

import nl.melonstudios.create.tileentity.TileEntityShaft;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class TESRShaft extends TESRKineticBase<TileEntityShaft> {
    @Override
    protected void render(TileEntityShaft te, float pt, float alpha) {
        this.spinShaft(te, pt, te.getRenderAxis());
    }
}
