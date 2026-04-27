package nl.melonstudios.create.util.filter;

import com.melonstudios.melonlib.misc.StackUtil;
import com.melonstudios.melonlib.network.TrackedByteBuf;
import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidStack;

import java.io.IOException;

public interface IItemFilter {
    boolean matches(ItemStack stack);
    NBTTagCompound serialize(NBTTagCompound nbt);
    void serialize(TrackedByteBuf buf);
    ItemStack getRenderItem();

    static IItemFilter deserialize(NBTTagCompound nbt) {
        if (!nbt.hasKey("type")) return null;
        byte type = nbt.getByte("type");
        if (type == 0) {
            return new ItemFilterExact(new ItemStack(nbt.getCompoundTag("ExampleItem")));
        }
        return null;
    }
    static IItemFilter deserialize(ByteBuf buf) throws IOException {
        byte type = buf.readByte();
        if (type == 0) {
            return new ItemFilterExact(StackUtil.readItemStack(buf, false, true));
        }
        return null;
    }

    default boolean matches(FluidStack stack) {
        return false;
    }
}
