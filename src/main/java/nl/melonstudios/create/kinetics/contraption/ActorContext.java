package nl.melonstudios.create.kinetics.contraption;

import net.minecraft.util.math.BlockPos;
import org.joml.Vector3f;

public class ActorContext {
    public final BlockPos pos;
    public final IContraptionActor actor;
    public final Vector3f worldPos;
    public final BlockPos.MutableBlockPos actorWorldPos;

    public ActorContext(BlockPos pos, IContraptionActor actor) {
        this.pos = pos;
        this.actor = actor;
        this.worldPos = new Vector3f();
        this.actorWorldPos = new BlockPos.MutableBlockPos();
    }
}
