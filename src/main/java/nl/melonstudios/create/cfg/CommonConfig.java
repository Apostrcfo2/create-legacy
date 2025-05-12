package nl.melonstudios.create.cfg;

import net.minecraftforge.common.config.Config;

@Config(modid = "create", name = "Create Common Config", category = "common")
public class CommonConfig {
    @Config.RequiresMcRestart
    @Config.Comment("Too many kinetic updates break a component if true")
    public static boolean enableFlickerTally  = true;
}
