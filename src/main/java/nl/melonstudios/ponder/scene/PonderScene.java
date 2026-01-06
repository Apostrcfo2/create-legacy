package nl.melonstudios.ponder.scene;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SideOnly(Side.CLIENT)
public class PonderScene {
    public PonderScene(NBTTagCompound initialStructureNBT) {
        NBTTagList blocksList = initialStructureNBT.getTagList("Blocks", 10);
        this.blocks = new HashMap<>(blocksList.tagCount());
        for (int i = 0; i < blocksList.tagCount(); i++) {
            NBTTagCompound block = blocksList.getCompoundTagAt(i);
            BlockPos pos = NBTUtil.getPosFromTag(block.getCompoundTag("Pos"));
            IBlockState state = NBTUtil.readBlockState(block.getCompoundTag("State"));
            this.blocks.put(pos, state);
        }
    }
    public PonderScene(Map<BlockPos, IBlockState> initialStructure, boolean makeCopy) {
        if (makeCopy) {
            this.blocks = new HashMap<>(initialStructure);
        } else this.blocks = initialStructure;
    }
    public PonderScene(PonderScene other) {
        this(other.blocks, true);
    }

    public final Map<BlockPos, IBlockState> blocks;
    public final List<Entity> entityList = new ArrayList<>();
    public final List<Entity> renderOnlyEntityList = new ArrayList<>();
    public final List<Entity> nonTickingRenderOnlyEntityList = new ArrayList<>();
    public final Map<BlockPos, TileEntity> tileEntities = new HashMap<>();
    public final Map<BlockPos, TileEntity> nonTickingTileEntities = new HashMap<>();
}
