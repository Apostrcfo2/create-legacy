package nl.melonstudios.create.init;

import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import nl.melonstudios.create.command.CommandCreate;

public class CommandInit {
    public static void addCreateCommand(FMLServerStartingEvent event) {
        event.registerServerCommand(new CommandCreate());
    }
}
