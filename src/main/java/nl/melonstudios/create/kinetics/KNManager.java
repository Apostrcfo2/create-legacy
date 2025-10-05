package nl.melonstudios.create.kinetics;

import net.minecraft.world.World;
import nl.melonstudios.create.CreateLegacy;
import nl.melonstudios.create.tileentity.TileEntityKinetic;

import java.util.HashMap;

public class KNManager {
    static final HashMap<World, HashMap<Long, KineticNetwork>> NETWORK_MAP = new HashMap<>();

    public static void loadWorld(World world) {
        NETWORK_MAP.put(world, new HashMap<>());
        CreateLegacy.logger.debug("Prepared kinetic space for {}", world.provider.getDimensionType().getName());
    }
    public static void unloadWorld(World world) {
        NETWORK_MAP.remove(world);
        CreateLegacy.logger.debug("Removed kinetic space for {}", world.provider.getDimensionType().getName());
    }

    public static KineticNetwork getOrCreateNetworkFor(TileEntityKinetic te) {
        Long id = te.networkID;
        if (id == null) return null;
        HashMap<Long, KineticNetwork> map = NETWORK_MAP.get(te.getWorld());
        return map.computeIfAbsent(id, networkID -> {
            KineticNetwork network = new KineticNetwork();
            network.networkID = networkID;
            return network;
        });
    }
}
