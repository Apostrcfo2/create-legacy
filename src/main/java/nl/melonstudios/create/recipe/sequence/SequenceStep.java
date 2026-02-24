package nl.melonstudios.create.recipe.sequence;

import com.melonstudios.melonlib.recipe.Ingredient;
import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class SequenceStep {
    public final String name;
    public final NBTTagCompound data;

    public SequenceStep(String name, NBTTagCompound data) {
        this.name = name;
        this.data = data;
    }
    public SequenceStep(String name) {
        this(name, new NBTTagCompound());
    }

    public static SequenceStep pressing() {
        return new SequenceStep("pressing");
    }
    public static SequenceStep deploying(ItemStack applied) {
        return deploying(Ingredient.of(applied, false));
    }
    public static SequenceStep deploying(Ingredient applied) {
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setTag("Applied", applied.serialize(new NBTTagCompound()));
        return new SequenceStep("deploying", nbt);
    }
    public static SequenceStep cutting() {
        return new SequenceStep("cutting");
    }

    public void write(ByteBuf buf) {
        buf.writeInt(this.name.length());
        buf.writeCharSequence(this.name, StandardCharsets.UTF_8);
        new PacketBuffer(buf).writeCompoundTag(this.data);
    }

    public static SequenceStep read(ByteBuf buf) throws IOException {
        int len = buf.readInt();
        String name = buf.readCharSequence(len, StandardCharsets.UTF_8).toString();
        NBTTagCompound nbt = new PacketBuffer(buf).readCompoundTag();
        return new SequenceStep(name, nbt);
    }
}
