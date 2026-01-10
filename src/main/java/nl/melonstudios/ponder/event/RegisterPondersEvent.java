package nl.melonstudios.ponder.event;

import net.minecraftforge.fml.common.eventhandler.Event;
import nl.melonstudios.ponder.PonderRegistrar;

public class RegisterPondersEvent extends Event {
    private final PonderRegistrar registrar;
    public RegisterPondersEvent(PonderRegistrar registrar) {
        this.registrar = registrar;
    }
    public PonderRegistrar getRegistrar() {
        return this.registrar;
    }
}
