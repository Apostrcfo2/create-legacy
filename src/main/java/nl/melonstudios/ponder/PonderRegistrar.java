package nl.melonstudios.ponder;

import com.melonstudios.melonlib.misc.MetaItem;
import net.minecraft.item.Item;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import nl.melonstudios.create.CreateLegacy;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

public class PonderRegistrar {
    PonderRegistrar() {

    }

    public void register(MetaItem item, PonderContainer container) {
        PonderRegistry.registerPonder(item, container);
    }
    public void register(Item item, PonderContainer container) {
        this.register(MetaItem.of(item, 0), container);
    }
    public NBTTagCompound getClassNBT(String path) throws IOException {
        try (
                InputStream stream = Objects.requireNonNull(CreateLegacy.class.getClassLoader().getResourceAsStream(path), "Null file");
                DataInputStream data = new DataInputStream(new BufferedInputStream(stream))
        ) {
            return CompressedStreamTools.read(data);
        }
    }
    public NBTTagCompound getClassNBT(String modid, String path) throws IOException {
        return this.getClassNBT("assets/" + modid + "/ponders/" + path + ".nbt");
    }
}
