package nl.melonstudios.ponder;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import nl.melonstudios.create.CreateLegacy;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

@SideOnly(Side.CLIENT)
public class PonderRegistrar {
    PonderRegistrar() {

    }

    public void register(Block block, PonderContainer container) {
        this.register(Item.getItemFromBlock(block), container);
    }
    public void register(Item item, PonderContainer container) {
        PonderRegistry.registerPonder(ForgeRegistries.ITEMS.getKey(item), container);
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
