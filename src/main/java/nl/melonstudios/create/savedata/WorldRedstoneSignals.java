package nl.melonstudios.create.savedata;

import com.melonstudios.melonlib.misc.MetaItem;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import nl.melonstudios.create.CreateLegacy;
import nl.melonstudios.create.util.interfaces.IFrequencyReceiver;

import java.util.*;

public class WorldRedstoneSignals extends WorldSavedData {
    public static final double RANGE = 65536;
    public WorldRedstoneSignals(String name) {
        super(name);
    }

    public static WorldRedstoneSignals get(World world) {
        WorldSavedData data = world.getMapStorage().getOrLoadData(WorldRedstoneSignals.class, "redstone_links");
        if (data != null) return (WorldRedstoneSignals) data;
        data = new WorldRedstoneSignals("redstone_links");
        world.getMapStorage().setData("redstone_links", data);
        return (WorldRedstoneSignals) data;
    }

    private final Map<LinkFrequency, Object2IntMap<BlockPos>> signals = new HashMap<>();
    public final List<IFrequencyReceiver> receivers = Collections.synchronizedList(new ArrayList<>());

    public int getSignal(LinkFrequency frequency, BlockPos pos) {
        Object2IntMap<BlockPos> sources = this.signals.get(frequency);
        if (sources == null) {
            CreateLegacy.logger.debug("No sources for {}", frequency);
            return 0;
        }
        int signal = 0;
        for (Object2IntMap.Entry<BlockPos> entry : sources.object2IntEntrySet()) {
            BlockPos sourcePos = entry.getKey();
            double dist = sourcePos.distanceSq(pos);
            CreateLegacy.logger.debug("Detected {} dist {}", entry.getIntValue(), dist);
            if (dist < RANGE) {
                signal = Math.max(entry.getIntValue(), signal);
            }
        }
        return signal;
    }
    public void setSignal(LinkFrequency frequency, BlockPos pos, int signal) {
        Object2IntMap<BlockPos> sources = this.signals.get(frequency);
        if (sources == null) {
            if (signal <= 0) return;
            sources = new Object2IntOpenHashMap<>();
            this.signals.put(frequency, sources);
        }
        if (signal > 0) {
            sources.put(pos, signal);
        } else {
            sources.remove(pos);
            if (sources.isEmpty()) {
                this.signals.remove(frequency);
            }
        }
        this.setDirty(true);
    }
    public static void updateAllLinksOfFreq(World world, WorldRedstoneSignals signals, LinkFrequency frequency) {
        CreateLegacy.logger.debug("Updating receivers in {} for {}", world.provider.getDimensionType().getName(), frequency);
        synchronized (signals.receivers) {
            for (int i = 0; i < signals.receivers.size(); i++) {
                IFrequencyReceiver receiver = signals.receivers.get(i);
                if (receiver.isAttuned(frequency)) {
                    CreateLegacy.logger.debug("Updating receiver at {}", receiver.getPos());
                    receiver.updateSignal(signals.getSignal(frequency, receiver.getPos()));
                }
            }
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        this.signals.clear();
        NBTTagList frequencies = nbt.getTagList("Frequencies", 10);
        for (int i = 0; i < frequencies.tagCount(); i++) {
            NBTTagCompound compound = frequencies.getCompoundTagAt(i);
            LinkFrequency freq = LinkFrequency.read(compound.getCompoundTag("Frequency"));
            Object2IntMap<BlockPos> map = new Object2IntOpenHashMap<>();
            NBTTagList sources = compound.getTagList("Sources", 10);
            for (int j = 0; j < sources.tagCount(); j++) {
                NBTTagCompound src = sources.getCompoundTagAt(j);
                BlockPos pos = NBTUtil.getPosFromTag(src.getCompoundTag("Pos"));
                int signal = src.getInteger("signal");
                map.put(pos, signal);
            }
            if (!map.isEmpty()) this.signals.put(freq, map);
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        NBTTagList frequencies = new NBTTagList();
        for (Map.Entry<LinkFrequency, Object2IntMap<BlockPos>> freq : this.signals.entrySet()) {
            NBTTagCompound compound = new NBTTagCompound();
            compound.setTag("Frequency", freq.getKey().write(new NBTTagCompound()));
            NBTTagList sources = new NBTTagList();
            for (Object2IntMap.Entry<BlockPos> source : freq.getValue().object2IntEntrySet()) {
                NBTTagCompound src = new NBTTagCompound();
                src.setTag("Pos", NBTUtil.createPosTag(source.getKey()));
                src.setInteger("signal", source.getIntValue());
                sources.appendTag(src);
            }
            compound.setTag("Sources", sources);
            frequencies.appendTag(compound);
        }
        nbt.setTag("Frequencies", frequencies);
        return nbt;
    }

    public static class LinkFrequency {
        public static final LinkFrequency EMPTY = new LinkFrequency(MetaItem.AIR, MetaItem.AIR);
        public final MetaItem first, second;

        private LinkFrequency(MetaItem first, MetaItem second) {
            this.first = first;
            this.second = second;
        }

        @Override
        public String toString() {
            return this == EMPTY ? "EmptyLinkFreq" : "LinkFreq[1:" + this.first.toString() + ";2:" + this.second.toString() + "]";
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (!(obj instanceof LinkFrequency)) return false;
            LinkFrequency other = (LinkFrequency) obj;
            return this.first.equals(other.first) && this.second.equals(other.second);
        }

        @Override
        public int hashCode() {
            return this == EMPTY ? 0 : Objects.hash(this.first, this.second);
        }

        public NBTTagCompound write(NBTTagCompound nbt) {
            if (this == EMPTY) throw new IllegalStateException("Cannot write empty frequency");
            if (!this.first.isAir()) {
                NBTTagCompound first = new NBTTagCompound();
                first.setString("id", ForgeRegistries.ITEMS.getKey(this.first.getItem()).toString());
                first.setInteger("meta", this.first.getMetadata());
                nbt.setTag("First", first);
            }

            if (!this.second.isAir()) {
                NBTTagCompound second = new NBTTagCompound();
                second.setString("id", ForgeRegistries.ITEMS.getKey(this.second.getItem()).toString());
                second.setInteger("meta", this.second.getMetadata());
                nbt.setTag("Second", second);
            }

            return nbt;
        }

        public static LinkFrequency get(ItemStack first, ItemStack second) {
            if (first.isEmpty() && second.isEmpty()) return EMPTY;
            return new LinkFrequency(MetaItem.of(first), MetaItem.of(second));
        }
        public static LinkFrequency get(MetaItem first, MetaItem second) {
            if (first.isAir() && second.isAir()) return EMPTY;
            return new LinkFrequency(first, second);
        }
        public static LinkFrequency read(NBTTagCompound nbt) {
            NBTTagCompound first = nbt.getCompoundTag("First");
            MetaItem firstFreq;
            if (first.hasKey("id", 10)) {
                firstFreq = MetaItem.of(ForgeRegistries.ITEMS.getValue(new ResourceLocation(first.getString("id"))), first.getInteger("meta"));
            } else {
                firstFreq = MetaItem.AIR;
            }
            NBTTagCompound second = nbt.getCompoundTag("Second");
            MetaItem secondFreq;
            if (second.hasKey("id", 10)) {
                secondFreq = MetaItem.of(ForgeRegistries.ITEMS.getValue(new ResourceLocation(second.getString("id"))), second.getInteger("meta"));
            } else {
                secondFreq = MetaItem.AIR;
            }

            return firstFreq.isAir() && secondFreq.isAir() ? EMPTY : new LinkFrequency(firstFreq, secondFreq);
        }
    }
}
