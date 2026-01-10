package nl.melonstudios.ponder.plan;

import com.google.common.collect.ImmutableList;
import it.unimi.dsi.fastutil.longs.Long2ObjectArrayMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import nl.melonstudios.ponder.plan.action.*;
import nl.melonstudios.ponder.world.EnumEntityPonder;
import nl.melonstudios.ponder.world.WorldPonder;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class PonderPlanBuilder {
    private final String name;
    private boolean setInitialScene = false;
    private long currentTime = 0L;
    private int currentTooltipID = 0;
    private final List<IPonderAction> initialization = new ArrayList<>();
    private final Long2ObjectMap<List<IPonderAction>> actions = new Long2ObjectArrayMap<>();

    public PonderPlanBuilder(String name) {
        this.name = name;
    }

    public void pause(long ticks) {
        this.currentTime += ticks;
    }
    public void setBlock(BlockPos pos, IBlockState state) {
        this.addAction(new ActionSetBlock(pos, state));
    }
    public void addEntity(EnumEntityPonder type, Function<WorldPonder, Entity> entitySupplier) {
        this.addAction(new ActionAddEntity(type, entitySupplier));
    }
    public void addEntity(Function<WorldPonder, Entity> entitySupplier) {
        this.addEntity(EnumEntityPonder.NORMAL, entitySupplier);
    }
    public void setScene(String name) {
        this.addAction(new ActionSetScene(name));
    }
    public void setInitialScene(String name) {
        if (this.setInitialScene) throw new IllegalStateException("Cannot set initial scene twice");
        this.addInitAction(new ActionSetScene(name));
        this.setInitialScene = true;
    }
    public void setSubject(String subject) {
        this.addAction(new ActionSetSubject("title.ponder." + subject));
    }
    public void setInitialSubject(String subject) {
        this.addInitAction(new ActionSetSubject("title.ponder." + subject));
    }
    public void setOffset(int x, int y, int z) {
        this.addAction(new ActionSetOffset(x, y, z));
    }
    public void setInitialOffset(int x, int y, int z) {
        this.addInitAction(new ActionSetOffset(x, y, z));
    }
    public void setRotation(float yaw, float pitch) {
        this.addAction(new ActionSetRotation(yaw, pitch));
    }
    public void setScale(float scale) {
        this.addAction(new ActionSetScale(scale));
    }
    public void setInitialScale(float scale) {
        this.addInitAction(new ActionSetScale(scale));
    }
    public void setTileEntity(BlockPos pos, Supplier<TileEntity> teSupplier, boolean shouldTick) {
        this.addAction(new ActionSetTileEntity(pos, teSupplier, shouldTick));
    }
    public void setTileEntity(BlockPos pos, Supplier<TileEntity> teSupplier) {
        this.setTileEntity(pos, teSupplier, true);
    }
    public <T extends TileEntity> void modifyTileEntity(BlockPos pos, Class<T> clazz, Consumer<T> action) {
        this.addAction(new ActionModifyTileEntity<>(pos, clazz, action));
    }
    public void removeTileEntity(BlockPos pos) {
        this.addAction(new ActionRemoveTileEntity(pos));
    }
    public void addTooltip(int ticks, float x, float y, float z, String ignored, Object... format) {
        this.addAction(new ActionAddTooltip(x, y, z, this.currentTime + ticks, "tooltip.ponder." + this.name + "." + this.currentTooltipID++, format));
    }

    public void addInitAction(IPonderAction action) {
        this.initialization.add(action);
    }
    public void addAction(IPonderAction action) {
        List<IPonderAction> list = this.actions.get(this.currentTime);
        if (list == null) {
            list = new ArrayList<>();
            this.actions.put(this.currentTime, list);
        }
        list.add(action);
    }

    public PonderPlan build() {
        if (!this.setInitialScene) throw new IllegalStateException("Initial scene required");
        return new PonderPlan(ImmutableList.copyOf(this.initialization), this.actions);
    }
}
