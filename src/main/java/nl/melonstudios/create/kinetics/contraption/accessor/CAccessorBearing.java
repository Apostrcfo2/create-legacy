package nl.melonstudios.create.kinetics.contraption.accessor;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import nl.melonstudios.create.entity.EntityContraptionBearing;
import nl.melonstudios.create.util.BlockRotationHelper;
import org.lwjgl.util.vector.Vector3f;

public class CAccessorBearing implements IContraptionAccessor {
    private final EntityContraptionBearing entity;

    public CAccessorBearing(EntityContraptionBearing entity) {
        this.entity = entity;
    }

    @Override
    public BlockPos getWorldPos(BlockPos localPos) {
        Vector3f vec = new Vector3f();
        this.getWorldPos(localPos, vec);
        return new BlockPos(vec.x, vec.y, vec.z);
    }

    @Override
    public BlockPos getWorldPos(Vector3f localPos) {
        Vector3f vec = new Vector3f();
        this.getWorldPos(localPos, vec);
        return new BlockPos(vec.x, vec.y, vec.z);
    }

    @Override
    public void getWorldPos(Vector3f localPos, Vector3f store) {
        BlockRotationHelper.rotateNormal(localPos, this.entity.cachedAxis, this.entity.cachedAngle, store);
        store.translate((float) this.entity.posX, (float) this.entity.posY, (float) this.entity.posZ);
    }

    @Override
    public void getWorldPos(BlockPos localPos, Vector3f store) {
        BlockRotationHelper.rotateNormal(localPos, this.entity.cachedAxis, this.entity.cachedAngle, store);
        store.translate((float) this.entity.posX, (float) this.entity.posY, (float) this.entity.posZ);
    }
    @Override
    public void getNormal(EnumFacing facing, Vector3f store) {
        BlockRotationHelper.rotateNormal(facing.getDirectionVec(), this.entity.cachedAxis, this.entity.cachedAngle, store);
    }
    @Override
    public void pauseContraption() {
        this.entity.pauseContraption();
    }
}
