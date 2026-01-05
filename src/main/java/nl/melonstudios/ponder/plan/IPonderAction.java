package nl.melonstudios.ponder.plan;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import nl.melonstudios.ponder.world.WorldPonder;

import java.util.function.Consumer;

@SideOnly(Side.CLIENT)
public interface IPonderAction extends Consumer<WorldPonder> {
    default boolean requiresMeshUpdate() {
        return false;
    }
}
