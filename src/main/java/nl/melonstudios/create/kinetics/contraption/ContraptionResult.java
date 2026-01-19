package nl.melonstudios.create.kinetics.contraption;

public class ContraptionResult {
    private final boolean failed;
    private final Object result;

    public ContraptionResult(Contraption contraption) {
        this.failed = false;
        this.result = contraption;
    }
    public ContraptionResult(String error) {
        this.failed = true;
        this.result = new AssemblyFailure(error);
    }

    public boolean hasFailed() {
        return this.failed;
    }

    public Contraption getContraption() {
        return (Contraption) this.result;
    }
    public AssemblyFailure getError() {
        return (AssemblyFailure) this.result;
    }

    public static class AssemblyFailure {
        public final String error;

        public AssemblyFailure(String error) {
            this.error = error;
        }
    }
}
