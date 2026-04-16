package nl.melonstudios.create;

import com.melonstudios.melonlib.recipe.RecipeRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.*;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.common.registry.GameRegistry;
import nl.melonstudios.create.init.*;
import nl.melonstudios.create.kinetics.BlockStressValues;
import nl.melonstudios.create.kinetics.FastStateRendering;
import nl.melonstudios.create.kinetics.contraption.Contraption;
import nl.melonstudios.create.proxy.CommonProxy;
import nl.melonstudios.create.recipe.*;
import nl.melonstudios.create.worldgen.CreateWorldGen;
import org.apache.logging.log4j.Logger;

import java.util.Random;

import static nl.melonstudios.create.CreateLegacy.DEPENDENCIES;

@Mod(modid = CreateLegacy.MODID, name = CreateLegacy.NAME, version = CreateLegacy.VERSION, useMetadata = true, dependencies = DEPENDENCIES, acceptedMinecraftVersions = "1.12.2")
public class CreateLegacy {
    private static final boolean inIDE = true;
    public static final String MODID = "create";
    public static final String NAME = "Create Legacy";
    public static final String VERSION = "26w08a";
    static final String DEPENDENCIES = "required-after:melonlib@[1.10.0,)" + (inIDE ? "" : ";required-after-client:ctm");

    private static SimpleNetworkWrapper network;
    public static SimpleNetworkWrapper getNetwork() {
        return network;
    }
    private static int networkId = 0;
    private static int nextNetworkDiscriminator() {
        return networkId++;
    }

    @SidedProxy(
            serverSide = "nl.melonstudios.create.proxy.CommonProxy",
            clientSide = "nl.melonstudios.create.proxy.ClientProxy"
    )
    public static CommonProxy proxy;

    public static Logger logger;
    public static final Random rand = new Random();

    // Pre pre init
    static {
        FluidInit.init();
    }

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        proxy.clientPreInit(event);

        network = NetworkRegistry.INSTANCE.newSimpleChannel(MODID);

        logger = event.getModLog();

        GameRegistry.registerWorldGenerator(new CreateWorldGen(), 0);

        SoundInit.init();

        FluidInit.register();

        proxy.registerRecipeTypes();
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.clientInit(event);

        CreateLegacy.proxy.initiatePonders();
        OreDictInit.scanMetalTypes();
        RecipeInit.init();
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        proxy.clientPostInit(event);

        proxy.pork();
        BlockStressValues.initialize();
        Contraption.registerValidInventoryClasses();
    }

    @EventHandler
    public void youGotMail(FMLInterModComms.IMCEvent event) {
        for (FMLInterModComms.IMCMessage message : event.getMessages()) {
            if (message.isStringMessage()) {
                if ("addContraptionInventory".equals(message.key)) {
                    Contraption.addValidInventoryFromIMC(message);
                }
            }
        }
    }

    @EventHandler
    public void serverStarting(FMLServerStartingEvent event) {
        CommandInit.addCreateCommand(event);
    }
}
