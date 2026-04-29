package nl.melonstudios.create.kinetics.contraption;

import net.minecraft.util.math.BlockPos;
import nl.melonstudios.create.entity.EntityPouf;

import java.util.UUID;

public class TrackedPouf {
    public final BlockPos localPos;
    public final UUID entityUUID;
    public EntityPouf entity;

    public TrackedPouf(BlockPos localPos, UUID entityUUID) {
        this.localPos = localPos;
        this.entityUUID = entityUUID;
    }
    public TrackedPouf(BlockPos localPos, EntityPouf entity) {
        this(localPos, entity.getPersistentID());
        this.entity = entity;
    }
}
