package nl.melonstudios.create.kinetics.contraption;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

@SideOnly(Side.CLIENT)
public class RenderContraption {
    public final BooleanSupplier preRenderPredicate;
    public final Runnable preRenderLogic;
    public final Contraption contraption;

    public RenderContraption(Runnable preRenderLogic, Contraption contraption) {
        this.preRenderPredicate = () -> true;
        this.preRenderLogic = preRenderLogic;
        this.contraption = contraption;
    }
    public RenderContraption(BooleanSupplier preRenderPredicate, Runnable preRenderLogic, Contraption contraption) {
        this.preRenderPredicate = preRenderPredicate;
        this.preRenderLogic = preRenderLogic;
        this.contraption = contraption;
    }
}
