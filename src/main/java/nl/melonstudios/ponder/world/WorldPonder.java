package nl.melonstudios.ponder.world;

import com.google.common.base.Predicate;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.profiler.Profiler;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.*;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.SaveHandlerMP;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import nl.melonstudios.ponder.plan.IPonderAction;
import nl.melonstudios.ponder.plan.PonderPlan;
import nl.melonstudios.ponder.scene.PonderScene;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

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

    public PonderScene scene;
    public final PonderPlan plan;
    private long time = -1L;
    public WorldPonder(PonderScene scene, PonderPlan plan, Profiler profilerIn) {
        super(SAVE_HANDLER, WORLD_INFO, WORLD_PROVIDER, profilerIn, true);
        this.scene = scene;
        this.plan = plan;
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
        this.time = Math.incrementExact(this.time);
    }

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
    // endregion
}
