package nl.melonstudios.create.kinetics;

import it.unimi.dsi.fastutil.objects.Reference2ObjectArrayMap;
import net.minecraft.world.World;
import nl.melonstudios.create.tileentity.TileEntityKinetic;

import java.util.HashMap;

public class KNManager {
    static final Reference2ObjectArrayMap<World, HashMap<Long, KineticNetwork>> NETWORK_MAP = new Reference2ObjectArrayMap<>();

    public static void loadWorld(World world) {
        NETWORK_MAP.put(world, new HashMap<>());
    }
    public static void unloadWorld(World world) {
        NETWORK_MAP.remove(world);
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
