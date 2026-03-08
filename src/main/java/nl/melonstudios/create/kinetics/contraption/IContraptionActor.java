package nl.melonstudios.create.kinetics.contraption;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import nl.melonstudios.create.kinetics.contraption.accessor.IContraptionAccessor;
import org.joml.Vector3fc;

public interface IContraptionActor {
    void setOnContraption(boolean onContraption);
    boolean isOnContraption();
    void contraptionTick(IContraptionAccessor contraption, World world, Vector3fc position, BlockPos blockPosition, boolean moved, Vector3fc movement);
}
