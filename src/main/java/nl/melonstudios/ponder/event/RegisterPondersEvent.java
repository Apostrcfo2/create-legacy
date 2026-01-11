package nl.melonstudios.ponder.event;

import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import nl.melonstudios.ponder.PonderRegistrar;

@SideOnly(Side.CLIENT)
public class RegisterPondersEvent extends Event {
    private final PonderRegistrar registrar;
    public RegisterPondersEvent(PonderRegistrar registrar) {
        this.registrar = registrar;
    }
    public PonderRegistrar getRegistrar() {
        return this.registrar;
    }
}
