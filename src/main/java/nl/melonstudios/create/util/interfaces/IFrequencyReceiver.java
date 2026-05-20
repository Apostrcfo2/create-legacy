package nl.melonstudios.create.util.interfaces;

import net.minecraft.util.math.BlockPos;
import nl.melonstudios.create.savedata.WorldRedstoneSignals;

public interface IFrequencyReceiver {
    void updateSignal(int signal);
    boolean isAttuned(WorldRedstoneSignals.LinkFrequency frequency);
    BlockPos getPos();
}
