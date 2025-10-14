package nl.melonstudios.create.kinetics.contraption;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderContraption {
    public final Runnable preRenderLogic;
    public final Contraption contraption;

    public RenderContraption(Runnable preRenderLogic, Contraption contraption) {
        this.preRenderLogic = preRenderLogic;
        this.contraption = contraption;
    }
}
