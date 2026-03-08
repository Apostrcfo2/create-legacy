package nl.melonstudios.create.cfg;

import net.minecraftforge.common.config.Config;

@Config(modid = "create", name = "create_client", category = "client")
public class ClientConfig {
    @Config.Comment("Render distance of kinetic blocks")
    @Config.RangeInt(min = Byte.MAX_VALUE)
    public static int kineticRenderDistance = Short.MAX_VALUE;

    @Config.Comment({
            "Determines whether the kinetic parts should use the new fast rendering system",
            "true: Block models are rendered to a list, greatly reducing GPU calls",
            "false: Block models are rendered directly, possibly more stable but also more GPU calls"
    })
    public static boolean fastKineticRendering = true;
}
