package nl.melonstudios.create.kinetics.contraption.accessor;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import nl.melonstudios.create.kinetics.contraption.ContraptionInventory;
import org.lwjgl.util.vector.Vector3f;

public interface IContraptionAccessor {
    BlockPos getWorldPos(BlockPos localPos);
    BlockPos getWorldPos(Vector3f localPos);
    void getWorldPos(BlockPos localPos, Vector3f store);
    void getWorldPos(Vector3f localPos, Vector3f store);
    void getNormal(EnumFacing facing, Vector3f store);
    void pauseContraption();
    ContraptionInventory getInventory();
}
