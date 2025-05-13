package nl.melonstudios.create.util;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class PerFrameDebugInfo {
    public static boolean renderAgain = true;
    public static void reset() {
        kineticTileEntitiesRendered = 0;
    }
    public static int kineticTileEntitiesRendered = 0;
}
