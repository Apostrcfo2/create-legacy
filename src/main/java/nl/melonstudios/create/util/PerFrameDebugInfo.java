package nl.melonstudios.create.util;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class PerFrameDebugInfo {
    public static boolean renderAgain = true;
    public static void reset() {
        kineticTileEntitiesRendered = 0;
        contraptionsRendered[0] = 0;
        contraptionsRendered[1] = 0;
        contraptionsRendered[2] = 0;
        contraptionsRendered[3] = 0;
        contraptionsSkipped[0] = 0;
        contraptionsSkipped[1] = 0;
        contraptionsSkipped[2] = 0;
        contraptionsSkipped[3] = 0;
    }
    public static int kineticTileEntitiesRendered = 0;
    public static int[] contraptionsRendered = new int[4];
    public static int[] contraptionsSkipped = new int[4];
}
