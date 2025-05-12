package nl.melonstudios.create.event;

import net.minecraft.block.Block;
import net.minecraftforge.fml.common.eventhandler.Event;

import java.util.HashMap;
import java.util.Map;

public class RegisterStressValuesEvent extends Event {
    private final Map<Block, Float> blockStressMap = new HashMap<>();
    private final Map<Block, Float> blockCapacityMap = new HashMap<>();

    public void registerStress(Block block, float stress) {
        this.blockStressMap.put(block, stress);
    }
    public void registerCapacity(Block block, float capacity) {
        this.blockCapacityMap.put(block, capacity);
    }
    public void load(Map<Block, Float> otherStress, Map<Block, Float> otherCapacity) {
        otherStress.putAll(this.blockStressMap);
        otherCapacity.putAll(this.blockCapacityMap);
    }
}
