package nl.melonstudios.create.kinetics.contraption;

import com.melonstudios.melonlib.blockdict.BlockDictionary;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.block.state.IBlockState;

import java.util.function.Function;

public class ContraptionAssembly {
    public final Object2IntMap<IBlockState> stateCounter;

    public ContraptionAssembly(Object2IntMap<IBlockState> stateCounter) {
        this.stateCounter = stateCounter;
    }

    public void incrementCounter(IBlockState state) {
        this.stateCounter.put(state, this.stateCounter.getInt(state) + 1);
    }
    public int getCount(IBlockState state) {
        return this.stateCounter.getInt(state);
    }
    public int getCount(String blockdict) {
        int i = 0;
        for (Object2IntMap.Entry<IBlockState> entry : this.stateCounter.object2IntEntrySet()) {
            if (BlockDictionary.isBlockTagged(entry.getKey(), blockdict)) i += entry.getIntValue();
        }
        return i;
    }

    public static final ContraptionAssemblyChecker NO_CHECKER = (assembly) -> null;
}
