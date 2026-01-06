package nl.melonstudios.ponder.world;

import com.google.common.base.Predicate;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.profiler.Profiler;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.*;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeProvider;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.SaveHandlerMP;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import nl.melonstudios.ponder.PonderContainer;
import nl.melonstudios.ponder.plan.IPonderAction;
import nl.melonstudios.ponder.plan.PonderPlan;
import nl.melonstudios.ponder.scene.PonderScene;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;

@SideOnly(Side.CLIENT)
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
@SuppressWarnings("unchecked")
public class WorldPonder extends World {
    public static final ISaveHandler SAVE_HANDLER = new SaveHandlerMP();
    public static final WorldInfo WORLD_INFO = new WorldInfo(new WorldSettings(
            0L, GameType.NOT_SET, false, false, WorldType.CUSTOMIZED
    ), "PonderWorld");
    public static final WorldProvider WORLD_PROVIDER = new WorldProviderPonder();

    public final PonderContainer container;
    public final Map<String, PonderScene> scenes = new HashMap<>();
    public PonderScene scene;
    public final PonderPlan plan;
    private long time = -1L;
    public float scaleOld = 1.0F;
    public float scale = 1.0F;
    public float yawOld = 45.0F;
    public float yaw = 45.0F;
    public float pitchOld = 30.0F;
    public float pitch = 30.0F;
    public int offsetX = 0, offsetY = 0, offsetZ = 0;
    public String title = "null";
    public WorldPonder(PonderContainer container, Profiler profilerIn) {
        super(SAVE_HANDLER, WORLD_INFO, WORLD_PROVIDER, profilerIn, true);
        this.container = container;
        this.plan = container.plan;
    }

    public void initialize() {
        if (this.time != -1L) throw new IllegalStateException("Invalid initialization call >:(");
        for (IPonderAction action : this.plan.initializationPlan) {
            action.accept(this);
        }
        this.time = 0L;
    }

    @Override
    protected IChunkProvider createChunkProvider() {
        return new ChunkProviderEmpty(this);
    }

    @Override
    protected boolean isChunkLoaded(int x, int z, boolean allowEmpty) {
        return true;
    }

    @Override
    public long getTotalWorldTime() {
        return this.time;
    }

    @Override
    public long getWorldTime() {
        return this.time;
    }

    @Override
    public void tick() {
        this.scaleOld = this.scale;
        this.yawOld = this.yaw;
        this.pitchOld = this.pitch;
        boolean updateMesh = false;
        for (IPonderAction action : this.plan.timePlan.getOrDefault(this.time, Collections.emptyList())) {
            action.accept(this);
            if (action.requiresMeshUpdate()) updateMesh = true;
        }
        for (Entity tick : this.scene.entityList) {
            tick.onUpdate();
        }
        for (Entity tick : this.scene.renderOnlyEntityList) {
            tick.onUpdate();
        }
        if (updateMesh) {
            RenderWorldPonder.update(this);
        }
        this.time = Math.incrementExact(this.time);
    }

    // region Block interaction

    @Override
    public boolean setBlockState(BlockPos pos, IBlockState state) {
        return this.scene.blocks.put(pos, state) != state;
    }
    @Override
    public boolean setBlockState(BlockPos pos, IBlockState newState, int flags) {
        return this.scene.blocks.put(pos, newState) != newState;
    }
    @Override
    public boolean setBlockToAir(BlockPos pos) {
        return this.scene.blocks.remove(pos) != null;
    }
    @Override
    public IBlockState getBlockState(BlockPos pos) {
        return this.scene.blocks.getOrDefault(pos, Blocks.AIR.getDefaultState());
    }

    @Nullable
    @Override
    public TileEntity getTileEntity(BlockPos pos) {
        TileEntity te = this.scene.tileEntities.get(pos);
        if (te == null) return this.scene.nonTickingTileEntities.get(pos);
        return te;
    }
    @Override
    public void setTileEntity(BlockPos pos, @Nullable TileEntity tileEntityIn) {
        this.scene.nonTickingTileEntities.remove(pos);
        if (tileEntityIn != null) {
            this.scene.tileEntities.put(pos.toImmutable(), tileEntityIn);
        } else {
            this.scene.tileEntities.remove(pos);
        }
    }
    @Override
    public void removeTileEntity(BlockPos pos) {
        this.scene.nonTickingTileEntities.remove(pos);
        this.scene.tileEntities.remove(pos);
    }

    // endregion

    // region Light modified to max at all times
    @Override
    public int getLightFromNeighbors(BlockPos pos) {
        return 15;
    }
    @Override
    public int getLight(BlockPos pos) {
        return 15;
    }
    @Override
    public int getLight(BlockPos pos, boolean checkNeighbors) {
        return 15;
    }
    @Override
    public int getLightFromNeighborsFor(EnumSkyBlock type, BlockPos pos) {
        return 15;
    }
    @Override
    public int getLightFor(EnumSkyBlock type, BlockPos pos) {
        return 15;
    }
    @Override
    public int getCombinedLight(BlockPos pos, int lightValue) {
        return 0x0FF00FF0;
    }
    // endregion

    // region Entity getters
    @Override
    public <T extends Entity> List<T> getEntities(Class<? extends T> entityType, Predicate<? super T> filter) {
        List<T> list = new ArrayList<>();
        for (Entity entity : this.scene.entityList) {
            if (entityType.isInstance(entity)) {
                T cast = (T) entity;
                if (filter.apply(cast)) list.add(cast);
            }
        }
        return list;
    }

    @Nullable
    @Override
    public Entity getEntityByID(int id) {
        for (Entity entity : this.scene.entityList) {
            if (entity.getEntityId() == id) return entity;
        }
        for (Entity entity : this.scene.renderOnlyEntityList) {
            if (entity.getEntityId() == id) return entity;
        }
        for (Entity entity : this.scene.nonTickingRenderOnlyEntityList) {
            if (entity.getEntityId() == id) return entity;
        }
        return null;
    }

    @Override
    public <T extends Entity> List<T> getEntitiesWithinAABB(Class<? extends T> clazz, AxisAlignedBB aabb, @Nullable Predicate<? super T> filter) {
        List<T> list = new ArrayList<>();
        for (Entity entity : this.scene.entityList) {
            if (clazz.isInstance(entity) && entity.getEntityBoundingBox().intersects(aabb)) {
                T cast = (T) entity;
                if (filter == null || filter.apply(cast)) list.add(cast);
            }
        }
        return list;
    }

    @Override
    public List<Entity> getEntitiesInAABBexcluding(@Nullable Entity entityIn, AxisAlignedBB boundingBox, @Nullable Predicate<? super Entity> predicate) {
        List<Entity> list = new ArrayList<>();
        for (Entity entity : this.scene.entityList) {
            if (entity != entityIn && entity.getEntityBoundingBox().intersects(boundingBox)) {
                if (predicate == null || predicate.apply(entity)) list.add(entity);
            }
        }
        return list;
    }

    @Override
    public List<Entity> getEntitiesWithinAABBExcludingEntity(@Nullable Entity entityIn, AxisAlignedBB bb) {
        return this.getEntitiesInAABBexcluding(entityIn, bb, null);
    }

    @Override
    public <T extends Entity> List<T> getEntitiesWithinAABB(Class<? extends T> classEntity, AxisAlignedBB bb) {
        return this.getEntitiesWithinAABB(classEntity, bb, null);
    }

    @Nullable
    @Override
    public EntityPlayer getPlayerEntityByName(String name) {
        return null;
    }

    @Nullable
    @Override
    public EntityPlayer getPlayerEntityByUUID(UUID uuid) {
        return null;
    }

    @Override
    public List<Entity> getLoadedEntityList() {
        return this.scene.entityList;
    }

    @Nullable
    @Override
    public EntityPlayer getClosestPlayerToEntity(Entity entityIn, double distance) {
        return null;
    }

    @Override
    public <T extends Entity> List<T> getPlayers(Class<? extends T> playerType, Predicate<? super T> filter) {
        return Collections.emptyList();
    }

    @Override
    public void removeEntity(Entity entityIn) {
        this.scene.entityList.remove(entityIn);
    }

    @Override
    public void removeEntityDangerously(Entity entityIn) {
        this.scene.entityList.remove(entityIn);
    }

    @Override
    public void onEntityRemoved(Entity entityIn) {

    }
    // endregion


    @Override
    public Biome getBiome(BlockPos pos) {
        return Biomes.PLAINS;
    }

    @Override
    public Biome getBiomeForCoordsBody(BlockPos pos) {
        return Biomes.PLAINS;
    }
}
