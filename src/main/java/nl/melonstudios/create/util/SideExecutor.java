package nl.melonstudios.create.util;

import net.minecraftforge.fml.relauncher.Side;
import nl.melonstudios.create.CreateLegacy;

import java.util.function.Supplier;

public class SideExecutor {
    private SideExecutor() {
        throw new AssertionError("no");
    }

    public static Side getSide() {
        return CreateLegacy.proxy.getSide();
    }
    public static void unsafeWhenRunOn(Side side, Supplier<Runnable> function) {
        if (getSide() == side) function.get().run();
    }
    public static void runOnServer(Supplier<Runnable> function) {
        unsafeWhenRunOn(Side.SERVER, function);
    }
    public static void runOnClient(Supplier<Runnable> function) {
        unsafeWhenRunOn(Side.CLIENT, function);
    }
}
