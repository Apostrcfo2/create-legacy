package nl.melonstudios.create.kinetics.contraption;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import nl.melonstudios.create.entity.EntityGlue;
import nl.melonstudios.create.extensions.IExtensionTileEntity;
import nl.melonstudios.create.tileentity.TileEntityKinetic;
import nl.melonstudios.create.tileentity.marker.IAssemblyBehavior;
import nl.melonstudios.create.util.Utils;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

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
            }
        }

        NBTTagList gluedSurfacesList = nbt.getTagList("GluedSurfaces", 10);
        for (int i = 0; i < gluedSurfacesList.tagCount(); i++) {
            NBTTagCompound compound = gluedSurfacesList.getCompoundTagAt(i);

            BlockPos pos = NBTUtil.getPosFromTag(compound.getCompoundTag("Pos"));
            EnumFacing side = EnumFacing.VALUES[compound.getByte("side")];

            this.gluedSurfaces.add(new GluedSurface(pos, side));
        }

        this.setTileEntityBlockData();

        this.compileLight();
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

        return nbt;
    }

    public boolean isRendering = false;
    public final IContraptionHolder holder;
    public final HashMap<BlockPos, IBlockState> blocks = new HashMap<>();
    public final HashMap<BlockPos, TileEntity> tileEntities = new HashMap<>();
    public final HashSet<GluedSurface> gluedSurfaces = new HashSet<>();
    public final HashSet<TileEntity> blacklistedForRendering = new HashSet<>();
    public final Object2IntOpenHashMap<BlockPos> lightSources = new Object2IntOpenHashMap<>();

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
            if (te != null) {
                te.validate();
                world.removeTileEntity(blockPos);
                te.setPos(adjusted);
                te.validate();
                contraption.tileEntities.put(adjusted, te);

                if (te instanceof IAssemblyBehavior) {
                    ((IAssemblyBehavior)te).onAssembly();
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

        contraption.compileLight();
        return contraption;
    }

    @SideOnly(Side.CLIENT)
    public RenderContraption renderContraption = null;
}
