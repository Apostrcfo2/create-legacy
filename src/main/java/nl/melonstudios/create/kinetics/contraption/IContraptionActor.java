package nl.melonstudios.create.kinetics.contraption;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import nl.melonstudios.create.kinetics.contraption.accessor.IContraptionAccessor;
import org.lwjgl.util.vector.Vector3f;

public interface IContraptionActor {
    void setOnContraption(boolean onContraption);
    boolean isOnContraption();
    void contraptionTick(IContraptionAccessor contraption, World world, Vector3f position, BlockPos blockPosition, boolean moved, Vector3f movement);
}
