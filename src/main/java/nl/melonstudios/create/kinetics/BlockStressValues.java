package nl.melonstudios.create.kinetics;

import it.unimi.dsi.fastutil.objects.Object2FloatArrayMap;
import net.minecraft.block.Block;
import net.minecraftforge.common.MinecraftForge;
import nl.melonstudios.create.event.RegisterStressValuesEvent;

public class BlockStressValues {
    private static final Object2FloatArrayMap<Block> BLOCK_STRESS_VALUES = new Object2FloatArrayMap<>();
    private static final Object2FloatArrayMap<Block> BLOCK_CAPACITY_VALUES = new Object2FloatArrayMap<>();
    public static void initialize() {
        BLOCK_STRESS_VALUES.clear();
        BLOCK_CAPACITY_VALUES.clear();
        RegisterStressValuesEvent event = new RegisterStressValuesEvent();
        MinecraftForge.EVENT_BUS.post(event);
        event.load(BLOCK_STRESS_VALUES, BLOCK_CAPACITY_VALUES);
    }

    public static float getStressImpact(Block block) {
        return BLOCK_STRESS_VALUES.getFloat(block);
    }
    public static float getStressCapacity(Block block) {
        return BLOCK_CAPACITY_VALUES.getFloat(block);
    }
}
