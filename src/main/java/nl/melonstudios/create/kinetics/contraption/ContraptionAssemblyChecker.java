package nl.melonstudios.create.kinetics.contraption;

import javax.annotation.Nullable;
import java.util.function.Function;

@FunctionalInterface
public interface ContraptionAssemblyChecker extends Function<ContraptionAssembly, String> {
    @Nullable
    String check(ContraptionAssembly assembly);

    @Override
    @Nullable
    default String apply(ContraptionAssembly assembly) {
        return this.check(assembly);
    }

    default ContraptionAssemblyChecker and(ContraptionAssemblyChecker checker) {
        return (assembly) -> {
            String err = this.check(assembly);
            if (err != null) return err;
            return checker.check(assembly);
        };
    }
}
