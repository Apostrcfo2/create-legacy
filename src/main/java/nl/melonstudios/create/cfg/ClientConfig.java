package nl.melonstudios.create.cfg;

import net.minecraftforge.common.config.Config;

@Config(modid = "create", name = "Create Client Config", category = "client")
public class ClientConfig {
    @Config.Comment("Render distance of kinetic blocks")
    @Config.RangeInt(min = Byte.MAX_VALUE)
    public static int kineticRenderDistance = Short.MAX_VALUE;
}
