package nl.melonstudios.create.recipe.sequence;

import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.objects.Object2FloatArrayMap;
import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.PacketBuffer;

import java.io.IOException;


public class SequenceResult {
    public final ItemStack expected;
    public final float chance;
    public final Object2FloatMap<ItemStack> waste;

    public SequenceResult(ItemStack expected, float chance, Object2FloatMap<ItemStack> waste) {
        this.expected = expected;
        this.chance = chance;
        this.waste = waste;
    }
    public SequenceResult(ItemStack expected, float chance, Object... waste) {
        this.expected = expected;
        this.chance = chance;
        if ((waste.length & 1) != 0) throw new IllegalArgumentException("Waste products must be in item-chance pairs");
        if (waste.length == 0) throw new IllegalArgumentException("Must have at least one waste product");
        int amount = waste.length / 2;
        this.waste = new Object2FloatArrayMap<>(amount);
        for (int i = 0; i < amount; i++) {
            ItemStack wasteItem = (ItemStack) waste[i*2];
            float wasteChance = (float) waste[i*2+1];
            this.waste.put(wasteItem, wasteChance);
        }
    }
    public SequenceResult(ItemStack result) {
        this.expected = result;
        this.chance = 1.0F;
        this.waste = null;
    }
    public SequenceResult(NBTTagCompound nbt) {
        this.expected = new ItemStack(nbt.getCompoundTag("Result"));
        this.chance = nbt.getFloat("chance");
        NBTTagList list = nbt.getTagList("Waste", 10);
        if (this.chance < 1.0F && list.tagCount() > 0) {
            this.waste = new Object2FloatArrayMap<>();
            for (int i = 0; i < list.tagCount(); i++) {
                NBTTagCompound wasteNBT = list.getCompoundTagAt(i);
                ItemStack stack = new ItemStack(wasteNBT.getCompoundTag("Item"));
                float chance = wasteNBT.getFloat("chance");
                this.waste.put(stack, chance);
            }
        } else {
            this.waste = null;
        }
    }

    public void write(ByteBuf buf) {
        writeItemStack(this.expected, buf);
        buf.writeFloat(this.chance);
        if (this.chance < 1.0F) {
            buf.writeInt(this.waste.size());
            for (Object2FloatMap.Entry<ItemStack> entry : this.waste.object2FloatEntrySet()) {
                writeItemStack(entry.getKey(), buf);
                buf.writeFloat(entry.getFloatValue());
            }
        }
    }
    public static SequenceResult read(ByteBuf buf) throws IOException {
        ItemStack expected = readItemStack(buf);
        float chance = buf.readFloat();
        if (chance < 1.0F) {
            int size = buf.readInt();
            Object2FloatMap<ItemStack> waste = new Object2FloatArrayMap<>(size);
            for (int i = 0; i < size; i++) {
                ItemStack wasteStack = readItemStack(buf);
                float wasteChance = buf.readFloat();
                waste.put(wasteStack, wasteChance);
            }
            return new SequenceResult(expected, chance, waste);
        }
        return new SequenceResult(expected);
    }

    private static void writeItemStack(ItemStack stack, ByteBuf buf) {
        int itemID = Item.getIdFromItem(stack.getItem());
        byte count = (byte)stack.getCount();
        short damage = (short)stack.getItemDamage();
        NBTTagCompound itemNBT = stack.getTagCompound();
        buf.writeInt(itemID);
        buf.writeByte(count);
        buf.writeShort(damage);
        if (itemNBT != null) {
            buf.writeBoolean(true);
            new PacketBuffer(buf).writeCompoundTag(itemNBT);
        } else {
            buf.writeBoolean(false);
        }
    }
    private static ItemStack readItemStack(ByteBuf buf) throws IOException {
        int itemID = buf.readInt();
        int count = buf.readUnsignedByte();
        int damage = buf.readUnsignedShort();
        boolean hasNBT = buf.readBoolean();
        NBTTagCompound itemNBT = hasNBT ? new PacketBuffer(buf).readCompoundTag() : null;
        return new ItemStack(Item.getItemById(itemID), count, damage, itemNBT);
    }
}
