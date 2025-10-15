package nl.melonstudios.create.kinetics.contraption;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import nl.melonstudios.create.entity.EntityContraptionBase;
import org.apache.commons.lang3.tuple.MutablePair;

import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.WeakHashMap;

public class ContraptionCollider {
    private static MutablePair<WeakReference<EntityContraptionBase>, Double> safetyLock = new MutablePair<>();
    private static Map<EntityContraptionBase, Map<EntityPlayer, Double>> remoteSafetyLocks = new WeakHashMap<>();

    public static void collideEntities(EntityContraptionBase entityContraption) {
        World world = entityContraption.getWorld();
        Contraption contraption = entityContraption.attachedContraption();
        AxisAlignedBB aabb = entityContraption.getEntityBoundingBox();

        if (contraption == null) return;
    }
}
