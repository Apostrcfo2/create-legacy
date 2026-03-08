package nl.melonstudios.create.kinetics.contraption.accessor;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import nl.melonstudios.create.kinetics.contraption.ContraptionInventory;
import nl.melonstudios.create.kinetics.contraption.ContraptionInventoryLegacy;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public interface IContraptionAccessor {
    BlockPos getWorldPos(BlockPos localPos);
    BlockPos getWorldPos(Vector3fc localPos);
    void getWorldPos(BlockPos localPos, Vector3f store);
    void getWorldPos(Vector3fc localPos, Vector3f store);
    void getNormal(EnumFacing facing, Vector3f store);
    void pauseContraption();
    @Deprecated
    default ContraptionInventoryLegacy getInventoryLegacy() {
        throw new UnsupportedOperationException("The contraption inventories have been reworked after 26w04a");
    }
    ContraptionInventory getInventory();
}
