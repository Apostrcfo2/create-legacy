package nl.melonstudios.ponder.world;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.EmptyChunk;
import net.minecraft.world.chunk.IChunkProvider;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ChunkProviderEmpty implements IChunkProvider {
    private final EmptyChunk emptyChunk;

    public ChunkProviderEmpty(World world) {
        this.emptyChunk = new EmptyChunk(world, 0, 0);
    }

    @Nullable
    @Override
    public Chunk getLoadedChunk(int x, int z) {
        return null;
    }

    @Override
    public Chunk provideChunk(int x, int z) {
        return this.emptyChunk;
    }

    @Override
    public boolean tick() {
        return false;
    }

    @Override
    public String makeString() {
        return "pondering";
    }

    @Override
    public boolean isChunkGeneratedAt(int x, int z) {
        return false;
    }
}
