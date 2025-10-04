package nl.melonstudios.create.kinetics.contraption;

import net.minecraft.util.math.AxisAlignedBB;

import java.util.List;

public interface ITileEntityWithContraption {
    /**
     * Collects collisions for contraptions...
     * PLS PLS PLS only add collisions if the entity will interact!!
     * performance!!
     * @param aabb The bounding box that the collisions are for
     * @param collisions The list that the collisions should be added to
     */
    void collectCollisions(AxisAlignedBB aabb, List<AxisAlignedBB> collisions);
}
