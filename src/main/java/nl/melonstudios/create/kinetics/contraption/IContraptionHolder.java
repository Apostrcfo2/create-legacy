package nl.melonstudios.create.kinetics.contraption;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

public interface IContraptionHolder {
    World getWorld();
    int getCombinedLight(BlockPos contraptionPos, int min);
    Biome getBiome();
    Contraption attachedContraption();
}
