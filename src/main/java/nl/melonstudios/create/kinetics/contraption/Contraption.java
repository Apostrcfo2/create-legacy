package nl.melonstudios.create.kinetics.contraption;

import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.HashMap;

public class Contraption implements IBlockAccess {
    private final IContraptionHolder holder;

    private final int sizeX;
    private final int sizeY;
    private final int sizeZ;

    private final IBlockState[][][] blocks;
    private final HashMap<BlockPos, TileEntity> tileEntityMap;

    public Contraption(IContraptionHolder holder, int sizeX, int sizeY, int sizeZ) {
        this.holder = holder;

        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.sizeZ = sizeZ;

        this.blocks = new IBlockState[this.sizeX][this.sizeY][this.sizeZ];
        this.tileEntityMap = new HashMap<>();
    }
    public Contraption(IContraptionHolder holder, NBTTagCompound nbt) {
        this(holder, nbt.getInteger("sizeX"), nbt.getInteger("sizeY"), nbt.getInteger("sizeZ"));

        this.loadNBT(nbt);
    }

    private int convertPos(int x, int y, int z) {
        return (x + y * this.sizeX + z * this.sizeX * this.sizeY);
    }
    private void deconvertPos(int pos, BlockPos.MutableBlockPos mutableBlockPos) {
        mutableBlockPos.setPos(
                (pos) % this.sizeX,
                (pos / this.sizeX) % this.sizeY,
                (pos / this.sizeX / this.sizeY) % this.sizeZ
        );
    }

    private void loadNBT(NBTTagCompound nbt) {
        NBTTagList tileEntities = nbt.getTagList("TileEntities", 10);
        for (int i = 0; i < tileEntities.tagCount(); i++) {
            TileEntity te = TileEntity.create(this.holder.getWorld(), tileEntities.getCompoundTagAt(i));
            if (te != null) {
                this.tileEntityMap.put(te.getPos(), te);
            }
        }

        int[] blocks = nbt.getIntArray("Blocks");
        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
        for (int i = 0; i < blocks.length; i++) {
            this.deconvertPos(i, mutable);

            this.blocks[mutable.getX()][mutable.getY()][mutable.getZ()] = Block.getStateById(blocks[i]);
        }
    }

    public NBTTagCompound saveNBT(NBTTagCompound nbt) {
        nbt.setInteger("sizeX", this.sizeX);
        nbt.setInteger("sizeY", this.sizeY);
        nbt.setInteger("sizeZ", this.sizeZ);

        NBTTagList tileEntities = new NBTTagList();
        for (TileEntity te : this.tileEntityMap.values()) {
            NBTTagCompound tag = new NBTTagCompound();
            te.writeToNBT(tag);
            tileEntities.appendTag(tag);
        }

        int[] blocks = new int[this.sizeX*this.sizeY*this.sizeZ];
        for (int x = 0; x < this.sizeX; x++) {
            for (int y = 0; y < this.sizeY; y++) {
                for (int z = 0; z < this.sizeZ; z++) {
                    blocks[this.convertPos(x, y, z)] = Block.getStateId(this.blocks[x][y][z]);
                }
            }
        }
        nbt.setIntArray("Blocks", blocks);

        return nbt;
    }

    public boolean validateBlockPos(BlockPos pos) {
        return pos.getX() >= 0 && pos.getX() < this.sizeX
                && pos.getY() >= 0 && pos.getY() < this.sizeY
                && pos.getZ() >= 0 && pos.getZ() < this.sizeZ;
    }

    @Nullable
    @Override
    public TileEntity getTileEntity(BlockPos pos) {
        return this.validateBlockPos(pos) ? this.tileEntityMap.get(pos) : null;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public int getCombinedLight(BlockPos pos, int lightValue) {
        return this.holder.getCombinedLight(pos, lightValue);
    }

    @Override
    public IBlockState getBlockState(BlockPos pos) {
        return this.validateBlockPos(pos) ? this.blocks[pos.getX()][pos.getY()][pos.getZ()] : Blocks.AIR.getDefaultState();
    }

    @Override
    public boolean isAirBlock(BlockPos pos) {
        IBlockState state = this.getBlockState(pos);
        return state.getBlock().isAir(state, this, pos);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public Biome getBiome(BlockPos pos) {
        return this.holder.getBiome();
    }

    @Override
    public int getStrongPower(BlockPos pos, EnumFacing direction) {
        return this.getBlockState(pos).getStrongPower(this, pos, direction);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public WorldType getWorldType() {
        return WorldType.DEFAULT;
    }

    @Override
    public boolean isSideSolid(BlockPos pos, EnumFacing side, boolean _default) {
        if (!this.validateBlockPos(pos)) return _default;
        return this.getBlockState(pos).isSideSolid(this, pos, side);
    }
}
