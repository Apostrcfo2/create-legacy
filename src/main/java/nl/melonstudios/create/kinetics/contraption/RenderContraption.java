package nl.melonstudios.create.kinetics.contraption;

import java.util.function.BooleanSupplier;

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
