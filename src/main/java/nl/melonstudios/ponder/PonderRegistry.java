package nl.melonstudios.ponder;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import nl.melonstudios.create.CreateLegacy;
import nl.melonstudios.ponder.event.RegisterPondersEvent;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@SideOnly(Side.CLIENT)
public final class PonderRegistry {
    private static final PonderRegistrar REGISTRAR = new PonderRegistrar();
    private static final Map<ResourceLocation, PonderContainer> PONDERS = new HashMap<>();


    static void registerPonder(ResourceLocation item, PonderContainer container) {
        PONDERS.put(item, container);
    }

    public static PonderContainer getPonder(ItemStack item) {
        return PONDERS.get(ForgeRegistries.ITEMS.getKey(item.getItem()));
    }
    public static boolean hasPonder(ItemStack item) {
        return PONDERS.containsKey(ForgeRegistries.ITEMS.getKey(item.getItem()));
    }

    public static void bootstrap() {
        MinecraftForge.EVENT_BUS.post(new RegisterPondersEvent(REGISTRAR));
    }

    private static NBTTagCompound getClassNBT(String path) throws IOException {
        try (
                InputStream stream = Objects.requireNonNull(CreateLegacy.class.getClassLoader().getResourceAsStream(path), "Null file");
                DataInputStream data = new DataInputStream(new BufferedInputStream(stream))
        ) {
            return CompressedStreamTools.read(data);
        }
    }
}
