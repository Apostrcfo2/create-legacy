package nl.melonstudios.ponder;

import com.melonstudios.melonlib.misc.MetaItem;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import nl.melonstudios.create.CreateLegacy;
import nl.melonstudios.create.init.BlockInit;
import nl.melonstudios.create.init.ItemInit;
import nl.melonstudios.create.tileentity.TileEntityDepot;
import nl.melonstudios.ponder.event.RegisterPondersEvent;
import nl.melonstudios.ponder.plan.PonderPlan;
import nl.melonstudios.ponder.plan.PonderPlanBuilder;
import nl.melonstudios.ponder.plan.action.ActionSetScene;
import nl.melonstudios.ponder.scene.IPonderSceneProvider;
import nl.melonstudios.ponder.world.EnumEntityPonder;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

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
