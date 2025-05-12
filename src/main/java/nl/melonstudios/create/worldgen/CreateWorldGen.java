package nl.melonstudios.create.worldgen;

import com.melonstudios.melonlib.predicates.StatePredicateBlock;
import net.minecraft.block.state.pattern.BlockMatcher;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.fml.common.IWorldGenerator;
import nl.melonstudios.create.block.BlockOre;
import nl.melonstudios.create.init.BlockInit;

import java.util.Random;

public class CreateWorldGen implements IWorldGenerator {
    private final WorldGenerator copper, zinc;
    public CreateWorldGen() {
        this.copper = new WorldGenMinable(BlockOre.copper(), 6, BlockMatcher.forBlock(Blocks.STONE));
        this.zinc = new WorldGenMinable(BlockOre.zinc(), 6, BlockMatcher.forBlock(Blocks.STONE));
    }
    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
        if (world.provider.getDimension() == -1) {
            this.genNether(random, chunkX, chunkZ, world, chunkGenerator, chunkProvider);
        } else if (world.provider.getDimension() == 1) {
            this.genEnd(random, chunkX, chunkZ, world, chunkGenerator, chunkProvider);
        } else {
            this.genOverworld(random, chunkX, chunkZ, world, chunkGenerator, chunkProvider);
        }
    }

    private void genNether(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {

    }
    private void genEnd(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {

    }
    private void genOverworld(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
        this.runGenerator(this.copper, world, random, chunkX, chunkZ, 8 + random.nextInt(8), 32, 128);
        this.runGenerator(this.zinc, world, random, chunkX, chunkZ, 8, 4, 64);
    }

    private void runGenerator(WorldGenerator generator, World world, Random random,
                              int chunkX, int chunkZ, int veins, int minHeight, int maxHeight) {
        int heightDiff = maxHeight - minHeight;

        for (int i = 0; i < veins; i++) {
            int x = chunkX * 16 + random.nextInt(16);
            int z = chunkZ * 16 + random.nextInt(16);
            int y = minHeight + random.nextInt(heightDiff);

            generator.generate(world, random, new BlockPos(x, y, z));
        }
    }
}
