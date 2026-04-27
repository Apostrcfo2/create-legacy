package nl.melonstudios.create.util;

import com.melonstudios.melonlib.network.TrackedByteBuf;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import nl.melonstudios.create.tileentity.actor.TileEntityCrafter;

import java.util.ArrayList;
import java.util.List;

public class CrafterContext implements INBTSerializable<NBTTagCompound> {
    public final List<BlockPos> crafterPositions;
    public boolean passed = false;
    public Int2ObjectMap<ItemStack> currentPattern = null;
    public float progressOld = 0.0F;
    public float progress = 0.0F;
    public BlockPos destination = null;

    public CrafterContext(List<BlockPos> crafterPositions) {
        this.crafterPositions = crafterPositions;
    }
    public CrafterContext(Iterable<? extends TileEntity> crafterPositions) {
        this(new ArrayList<BlockPos>());
        for (TileEntity te : crafterPositions) {
            this.crafterPositions.add(te.getPos());
        }
    }
    public CrafterContext() {
        this(new ArrayList<BlockPos>());
    }

    public void serialize(TrackedByteBuf buf) {
        ByteBuf temp = Unpooled.buffer();
        ByteBufUtils.writeTag(temp, this.serializeNBT());
        buf.writeBytes(temp);
    }
    public void deserialize(ByteBuf buf) {
        this.deserializeNBT(ByteBufUtils.readTag(buf));
    }

    @Override
    public NBTTagCompound serializeNBT() {
        NBTTagCompound nbt = new NBTTagCompound();

        NBTTagList positionsNBT = new NBTTagList();
        for (BlockPos pos : this.crafterPositions) {
            positionsNBT.appendTag(NBTUtil.createPosTag(pos));
        }
        nbt.setTag("CrafterPositions", positionsNBT);

        nbt.setBoolean("passed", this.passed);

        if (this.currentPattern != null) {
            NBTTagList list = new NBTTagList();
            for (Int2ObjectMap.Entry<ItemStack> entry : this.currentPattern.int2ObjectEntrySet()) {
                NBTTagCompound compound = new NBTTagCompound();
                compound.setInteger("key", entry.getIntKey());
                compound.setTag("Value", entry.getValue().serializeNBT());
                list.appendTag(compound);
            }
            nbt.setTag("CurrentPattern", list);
        }

        nbt.setFloat("progress", this.progress);

        if (this.destination != null) {
            nbt.setTag("Destination", NBTUtil.createPosTag(this.destination));
        }

        return nbt;
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt) {
        NBTTagList positionsNBT = nbt.getTagList("CrafterPositions", 10);
        for (int i = 0; i < positionsNBT.tagCount(); i++) {
            this.crafterPositions.add(NBTUtil.getPosFromTag(positionsNBT.getCompoundTagAt(i)));
        }

        this.passed = nbt.getBoolean("passed");

        if (nbt.hasKey("CurrentPattern", 9)) {
            this.currentPattern = new Int2ObjectArrayMap<>();
            NBTTagList list = nbt.getTagList("CurrentPattern", 9);
            for (int i = 0; i < list.tagCount(); i++) {
                NBTTagCompound compound = list.getCompoundTagAt(i);
                int key = compound.getInteger("key");
                ItemStack value = new ItemStack(compound.getCompoundTag("Value"));
                if (value.isEmpty()) continue;
                this.currentPattern.put(key, value);
            }
        }

        this.progress = nbt.getFloat("progress");

        if (nbt.hasKey("Destination", 10)) {
            this.destination = NBTUtil.getPosFromTag(nbt.getCompoundTag("Destination"));
        }
    }

    public void interrupt(World world) {
        for (BlockPos pos : this.crafterPositions) {
            TileEntityCrafter crafter = Utils.cast(world.getTileEntity(pos), TileEntityCrafter.class);
            if (crafter != null) {
                crafter.crafterContext = null;
                crafter.sync();
            }
        }
    }

    public void setProgress(float progress) {
        this.progressOld = this.progress;
        this.progress = progress;
    }
    public void addProgress(float progress) {
        this.setProgress(Math.min(this.progress + progress, 1.0F));
    }
}
