package nl.melonstudios.create.tesr.actor;

import net.minecraft.util.EnumFacing;
import nl.melonstudios.create.tesr.TESRKineticBase;
import nl.melonstudios.create.tileentity.actor.TileEntityMixer;

public class TESRMixer extends TESRKineticBase<TileEntityMixer> {
    @Override
    protected void render(TileEntityMixer te, float pt, float alpha) {
        this.spinShaftlessCog(te, te.getSpeed(), EnumFacing.Axis.Y, pt);
    }
}
