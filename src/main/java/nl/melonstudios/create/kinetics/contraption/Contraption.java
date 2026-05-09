package nl.melonstudios.create.kinetics.contraption;

import com.melonstudios.melonlib.blockdict.BlockDictionary;
import com.melonstudios.melonlib.misc.AABB;
import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import nl.melonstudios.create.CreateLegacy;
import nl.melonstudios.create.entity.EntityGlue;
import nl.melonstudios.create.entity.EntityPouf;
import nl.melonstudios.create.event.RegisterContraptionInventoriesEvent;
import nl.melonstudios.create.extensions.IExtensionTileEntity;
import nl.melonstudios.create.tileentity.TileEntityDepot;
import nl.melonstudios.create.tileentity.marker.IAssemblyBehavior;
import nl.melonstudios.create.util.Utils;
import org.joml.*;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.lang.Math;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.function.Predicate;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class Contraption implements IBlockAccess {
    private static final IBlockState AIR = Blocks.AIR.getDefaultState();
    private static final int FULL_BRIGHT = 0xFF00000;

    public Contraption(IContraptionHolder holder) {
        this.holder = holder;
    }

    public void setTileEntityBlockData() {
        for (TileEntity te : this.tileEntities.values()) {
            ((IExtensionTileEntity)te).create$setState(this.getBlockState(te.getPos()));
        }
        this.blacklistedForRendering.clear();
    }

    public void loadNBT(NBTTagCompound nbt) {
        this.blocks.clear();
        this.tileEntities.clear();
        this.gluedSurfaces.clear();
        this.poufs.clear();
        this.actors.clear();

        NBTTagList blockList = nbt.getTagList("Blocks", 10);
        for (int i = 0; i < blockList.tagCount(); i++) {
            NBTTagCompound compound = blockList.getCompoundTagAt(i);

            BlockPos pos = NBTUtil.getPosFromTag(compound.getCompoundTag("Pos"));
            IBlockState state = NBTUtil.readBlockState(compound.getCompoundTag("State"));

            this.blocks.put(pos, state);
        }

        NBTTagList tileEntityList = nbt.getTagList("TileEntities", 10);
        for (int i = 0; i < tileEntityList.tagCount(); i++) {
            NBTTagCompound compound = tileEntityList.getCompoundTagAt(i);

            TileEntity te = TileEntity.create(this.holder.getWorld(), compound);
            if (te != null) {
                this.tileEntities.put(te.getPos(), te);
                if (te instanceof IContraptionActor) {
                    IContraptionActor actor = (IContraptionActor) te;
                    te.setWorld(this.holder.getWorld());
                    this.actors.add(new ActorContext(te.getPos(), actor));
                    actor.setOnContraption(true);
                }
            }
        }

        NBTTagList gluedSurfacesList = nbt.getTagList("GluedSurfaces", 10);
        for (int i = 0; i < gluedSurfacesList.tagCount(); i++) {
            NBTTagCompound compound = gluedSurfacesList.getCompoundTagAt(i);

            BlockPos pos = NBTUtil.getPosFromTag(compound.getCompoundTag("Pos"));
            EnumFacing side = EnumFacing.VALUES[compound.getByte("side")];

            this.gluedSurfaces.add(new GluedSurface(pos, side));
        }

        NBTTagList poufsList = nbt.getTagList("Poufs", 10);
        for (int i = 0; i < poufsList.tagCount(); i++) {
            NBTTagCompound compound = poufsList.getCompoundTagAt(i);

            BlockPos pos = NBTUtil.getPosFromTag(compound.getCompoundTag("Pos"));
            UUID uuid = compound.getUniqueId("UUID");

            if (uuid != null) this.poufs.add(new TrackedPouf(pos, uuid));
        }

        this.setTileEntityBlockData();
        this.inventory.reindex(this);

        this.compileLight();
        this.compileAABB();
    }

    public NBTTagCompound saveNBT(NBTTagCompound nbt) {
        NBTTagList blockList = new NBTTagList();
        for (Map.Entry<BlockPos, IBlockState> entry : this.blocks.entrySet()) {
            NBTTagCompound compound = new NBTTagCompound();

            compound.setTag("Pos", NBTUtil.createPosTag(entry.getKey()));
            compound.setTag("State", NBTUtil.writeBlockState(new NBTTagCompound(), entry.getValue()));

            blockList.appendTag(compound);
        }
        nbt.setTag("Blocks", blockList);

        NBTTagList tileEntityList = new NBTTagList();
        for (TileEntity te : this.tileEntities.values()) {
            tileEntityList.appendTag(te.writeToNBT(new NBTTagCompound()));
        }
        nbt.setTag("TileEntities", tileEntityList);

        NBTTagList gluedSurfacesList = new NBTTagList();
        for (GluedSurface surface : this.gluedSurfaces) {
            NBTTagCompound compound = new NBTTagCompound();

            compound.setTag("Pos", NBTUtil.createPosTag(surface.pos));
            compound.setByte("side", (byte)surface.side.getIndex());

            gluedSurfacesList.appendTag(compound);
        }
        nbt.setTag("GluedSurfaces", gluedSurfacesList);

        NBTTagList poufsList = new NBTTagList();
        for (TrackedPouf pouf : this.poufs) {
            NBTTagCompound compound = new NBTTagCompound();

            compound.setTag("Pos", NBTUtil.createPosTag(pouf.localPos));
            compound.setUniqueId("UUID", pouf.entityUUID);

            poufsList.appendTag(compound);
        }
        nbt.setTag("Poufs", poufsList);

        return nbt;
    }

    public boolean invalidated = false;
    public boolean isRendering = false;
    public final IContraptionHolder holder;
    public final HashMap<BlockPos, IBlockState> blocks = new HashMap<>();
    public final HashMap<BlockPos, TileEntity> tileEntities = new HashMap<>();
    public final HashSet<GluedSurface> gluedSurfaces = new HashSet<>();
    public final HashSet<TrackedPouf> poufs = new HashSet<>();
    public final HashSet<TileEntity> blacklistedForRendering = new HashSet<>();
    public final Object2IntOpenHashMap<BlockPos> lightSources = new Object2IntOpenHashMap<>();
    public final HashSet<ActorContext> actors = new HashSet<>();
    public final ContraptionInventory inventory = new ContraptionInventory();
    public List<AxisAlignedBB> optimizedAABB = new ArrayList<>();

    @Nullable
    @Override
    public TileEntity getTileEntity(BlockPos pos) {
        return this.tileEntities.get(pos);
    }

    @Override
    public int getCombinedLight(BlockPos pos, int lightValue) {
        int block = this.getBlockLightAt(pos, lightValue);
        return this.isRendering ? (FULL_BRIGHT | (block << 4)) : this.holder.getCombinedLight(pos, block);
    }

    private void compileLight() {
        for (Map.Entry<BlockPos, IBlockState> entry : this.blocks.entrySet()) {
            int light = entry.getValue().getLightValue(this, entry.getKey());
            if (light > 0) {
                this.lightSources.put(entry.getKey(), light);
            }
        }
    }
    private int getBlockLightAt(BlockPos pos, int light) {
        for (Object2IntMap.Entry<BlockPos> entry : this.lightSources.object2IntEntrySet()) {
            light = Math.max(light, entry.getIntValue() - Utils.dist_manh(pos, entry.getKey()));
        }
        return light;
    }

    private void compileAABB() {
        List<AxisAlignedBB> list = new ArrayList<>();
        for (Map.Entry<BlockPos, IBlockState> entry : this.blocks.entrySet()) {
            BlockPos local = entry.getKey();
            AxisAlignedBB bounds = entry.getValue().getCollisionBoundingBox(this, local);
            if (bounds != null) {
                list.add(bounds.offset(local));
            }
        }
        this.optimizedAABB = AABB.optimize(list, EnumFacing.Axis.Y, 256);
    }

    @Override
    public IBlockState getBlockState(BlockPos pos) {
        return this.blocks.getOrDefault(pos, AIR);
    }

    @Override
    public boolean isAirBlock(BlockPos pos) {
        return !this.blocks.containsKey(pos);
    }

    @Override
    public Biome getBiome(BlockPos pos) {
        return this.holder.getBiome();
    }

    @Override
    public int getStrongPower(BlockPos pos, EnumFacing direction) {
        return 0;
    }

    @Override
    public WorldType getWorldType() {
        return WorldType.DEFAULT;
    }

    @Override
    public boolean isSideSolid(BlockPos pos, EnumFacing side, boolean _default) {
        return this.getBlockState(pos).isSideSolid(this, pos, side);
    }

    public static final Matrix4dc IDENTITY = new Matrix4d().identity();
    private static final Vector4d TEMP_VEC = new Vector4d();
    public void updatePoufs(World world, double x, double y, double z, Matrix4dc transforms) {
        Vector3d vec = new Vector3d();
        for (TrackedPouf pouf : this.poufs) {
            if (pouf.entity == null) {
                List<EntityPouf> list = world.getEntities(EntityPouf.class, e -> e.getPersistentID().equals(pouf.entityUUID));
                if (list.isEmpty()) {
                    continue; //it will probably appear later
                } else {
                    pouf.entity = list.get(0);
                }
            }
            vec.set(pouf.localPos.getX(), pouf.localPos.getY(), pouf.localPos.getZ());
            TEMP_VEC.set(vec, 0);
            TEMP_VEC.mul(transforms);
            vec.set(TEMP_VEC);
            pouf.entity.setPositionAndUpdate(x + vec.x, y + vec.y - 0.4, z + vec.z);
        }
    }

    @Nullable
    public static Contraption assemble(IContraptionHolder holder, BlockPos pos, @Nullable BlockPos exclude) {
        World world = holder.getWorld();

        Set<BlockPos> positions = new HashSet<>();
        Set<EntityGlue> glues = new HashSet<>();
        AtomicBoolean failed = new AtomicBoolean(false);
        StickinessPropagator.propagateStickiness(world, pos, 4096, positions, glues, failed);

        if (failed.get() || positions.isEmpty()) return null;
        Contraption contraption = new Contraption(holder);

        for (BlockPos blockPos : positions) {
            if (blockPos.equals(exclude)) continue;
            IBlockState state = world.getBlockState(blockPos);
            TileEntity te = world.getTileEntity(blockPos);
            BlockPos adjusted = blockPos.subtract(pos);
            contraption.blocks.put(adjusted, state);
            if (!world.isRemote) {
                if (BlockDictionary.isBlockTagged(state, "create:pouf")) {
                    contraption.poufs.add(new TrackedPouf(adjusted, new EntityPouf(world)));
                }
            }
            if (te != null) {
                if (te instanceof IAssemblyBehavior) {
                    ((IAssemblyBehavior)te).onAssembly();
                }
                te.invalidate();
                world.removeTileEntity(blockPos);
                te.setPos(adjusted);
                TileEntity copy = TileEntity.create(world, te.writeToNBT(new NBTTagCompound()));
                if (copy != null) {
                    contraption.tileEntities.put(adjusted, te);
                    if (copy instanceof IContraptionActor) {
                        IContraptionActor actor = (IContraptionActor) copy;
                        contraption.actors.add(new ActorContext(adjusted, actor));
                        copy.setWorld(world);
                        actor.setOnContraption(true);
                    }
                }
            }

            world.setBlockState(blockPos, Blocks.AIR.getDefaultState(), 0b10010);
        }
        contraption.setTileEntityBlockData();
        for (EntityGlue entityGlue : glues) {
            GluedSurface surface = entityGlue.getSurface();
            contraption.gluedSurfaces.add(new GluedSurface(surface.pos.subtract(pos), surface.side));
        }
        glues.forEach(world::removeEntity);
        if (!world.isRemote) {
            for (TrackedPouf pouf : contraption.poufs) {
                pouf.entity.setPosition(
                        pos.getX() + pouf.localPos.getX() + 0.5,
                        pos.getY() + pouf.localPos.getY() - 0.4,
                        pos.getZ() + pouf.localPos.getZ() + 0.5
                );
                world.spawnEntity(pouf.entity);
            }
        }

        contraption.inventory.reindex(contraption);
        contraption.compileLight();
        contraption.compileAABB();
        return contraption;
    }

    public static ContraptionResult assemble(IContraptionHolder holder, BlockPos pos, @Nullable BlockPos exclude, Function<ContraptionAssembly, String> checker) {
        return assemble(holder, pos, exclude != null ? exclude::equals : (obj) -> false, checker);
    }
    public static ContraptionResult assemble(IContraptionHolder holder, BlockPos pos, @Nullable Predicate<BlockPos> exclude, Function<ContraptionAssembly, String> checker) {
        World world = holder.getWorld();

        Set<BlockPos> positions = new HashSet<>();
        Set<EntityGlue> glues = new HashSet<>();
        AtomicBoolean failed = new AtomicBoolean(false);
        ContraptionAssembly assembly = new ContraptionAssembly(new Object2IntArrayMap<>());
        StickinessPropagator.propagateStickiness(world, pos, 4096, positions, glues, failed, assembly);

        if (failed.get()) return new ContraptionResult("assembly_failure.immovable");
        if (positions.isEmpty()) return new ContraptionResult("assembly_failure.no_structure");
        if (exclude != null && positions.stream().anyMatch(exclude)) return new ContraptionResult("assembly_failure.moving_self");
        String err = checker.apply(assembly);
        if (err != null) return new ContraptionResult(err);

        Contraption contraption = new Contraption(holder);

        for (BlockPos blockPos : positions) {
            if (blockPos.equals(exclude)) continue;
            IBlockState state = world.getBlockState(blockPos);
            TileEntity te = world.getTileEntity(blockPos);
            BlockPos adjusted = blockPos.subtract(pos);
            contraption.blocks.put(adjusted, state);
            if (!world.isRemote) {
                if (BlockDictionary.isBlockTagged(state, "create:pouf")) {
                    contraption.poufs.add(new TrackedPouf(adjusted, new EntityPouf(holder.getWorld())));
                }
            }
            if (te != null) {
                if (te instanceof IAssemblyBehavior) {
                    ((IAssemblyBehavior)te).onAssembly();
                }
                te.invalidate();
                world.removeTileEntity(blockPos);
                te.setPos(adjusted);
                TileEntity copy = TileEntity.create(world, te.writeToNBT(new NBTTagCompound()));
                if (copy != null) {
                    contraption.tileEntities.put(adjusted, copy);
                    if (copy instanceof IContraptionActor) {
                        IContraptionActor actor = (IContraptionActor) copy;
                        contraption.actors.add(new ActorContext(adjusted, actor));
                        copy.setWorld(world);
                        actor.setOnContraption(true);
                    }
                }
            }

            //world.setBlockState(blockPos, Blocks.AIR.getDefaultState(), 0b10010);
        }

        for (BlockPos blockPos : positions) {
            if (blockPos.equals(exclude)) continue;
            world.setBlockState(blockPos, Blocks.AIR.getDefaultState(), 0b10010);
        }

        contraption.setTileEntityBlockData();
        for (EntityGlue entityGlue : glues) {
            GluedSurface surface = entityGlue.getSurface();
            contraption.gluedSurfaces.add(new GluedSurface(surface.pos.subtract(pos), surface.side));
        }
        glues.forEach(world::removeEntity);
        if (!world.isRemote) {
            for (TrackedPouf pouf : contraption.poufs) {
                pouf.entity.setPosition(
                        pos.getX() + pouf.localPos.getX() + 0.5,
                        pos.getY() + pouf.localPos.getY() - 0.4,
                        pos.getZ() + pouf.localPos.getZ() + 0.5
                );
                world.spawnEntity(pouf.entity);
            }
        }

        contraption.inventory.reindex(contraption);
        contraption.compileLight();
        contraption.compileAABB();
        return new ContraptionResult(contraption);
    }

    public RenderContraption renderContraption = null;

    private static final Set<Class<? extends TileEntity>> VALID_INVENTORY_CLASSES = new HashSet<>();
    public static void registerValidInventoryClasses() {
        if (!VALID_INVENTORY_CLASSES.isEmpty()) throw new IllegalStateException("Already registered valid inventory classes");
        VALID_INVENTORY_CLASSES.add(TileEntityChest.class);
        VALID_INVENTORY_CLASSES.add(TileEntityDepot.class);
        RegisterContraptionInventoriesEvent event = new RegisterContraptionInventoriesEvent();
        MinecraftForge.EVENT_BUS.post(event);
        event.load(VALID_INVENTORY_CLASSES);
    }
    @Deprecated
    public static boolean isValidInventory(IInventory inventory) {
        throw new UnsupportedOperationException("The contraption inventories have been reworked after 26w04a");
    }
    public static boolean isValidInventory(TileEntity te) {
        return VALID_INVENTORY_CLASSES.contains(te.getClass());
    }

    @SuppressWarnings("unchecked")
    public static void addValidInventoryFromIMC(FMLInterModComms.IMCMessage message) {
        try {
            Class<? extends TileEntity> clazz = (Class<? extends TileEntity>) Class.forName(message.getStringValue());
            VALID_INVENTORY_CLASSES.add(clazz);
            CreateLegacy.logger.debug("Added contraption inventory {} (received from {})", clazz.getSimpleName(), message.getSender());
        } catch (Throwable e) {
            CreateLegacy.logger.warn("Could not add contraption inventory from IMC: {} (received from {})", String.valueOf(e), message.getSender());
        }
    }
}
