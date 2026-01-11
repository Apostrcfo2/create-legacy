package nl.melonstudios.create.ponder;

import net.minecraft.init.Biomes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import nl.melonstudios.create.kinetics.contraption.Contraption;
import nl.melonstudios.create.kinetics.contraption.IContraptionHolder;
import nl.melonstudios.ponder.world.WorldPonder;

@SideOnly(Side.CLIENT)
public class PonderContraptionHolder implements IContraptionHolder {
    private final WorldPonder world;

    public PonderContraptionHolder(WorldPonder world) {
        this.world = world;
    }

    @Override
    public World getWorld() {
        return this.world;
    }

    @Override
    public int getCombinedLight(BlockPos contraptionPos, int min) {
        return 0x0FF00FF0;
    }

    @Override
    public Biome getBiome() {
        return Biomes.PLAINS;
    }

    @Override
    public Contraption attachedContraption() {
        return null;
    }
}
