package nl.melonstudios.create;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.common.registry.GameRegistry;
import nl.melonstudios.create.init.OreDictInit;
import nl.melonstudios.create.init.RecipeInit;
import nl.melonstudios.create.init.SoundInit;
import nl.melonstudios.create.kinetics.BlockStressValues;
import nl.melonstudios.create.proxy.CommonProxy;
import nl.melonstudios.create.worldgen.CreateWorldGen;
import org.apache.logging.log4j.Logger;

import static nl.melonstudios.create.CreateLegacy.DEPENDENCIES;

@Mod(modid = CreateLegacy.MODID, name = CreateLegacy.NAME, version = CreateLegacy.VERSION, useMetadata = true, dependencies = DEPENDENCIES)
public class CreateLegacy {
    private static final boolean inIDE = true;
    public static final String MODID = "create";
    public static final String NAME = "Create Legacy";
    public static final String VERSION = "1.0.0";
    static final String DEPENDENCIES = "required-after:melonlib@[1.4,)" + (inIDE ? "" : ";required-after-client:ctm");

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
    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        network = NetworkRegistry.INSTANCE.newSimpleChannel(MODID);
        logger = event.getModLog();

        GameRegistry.registerWorldGenerator(new CreateWorldGen(), 0);

        SoundInit.init();
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {

    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        proxy.pork();
        OreDictInit.scanMetalTypes();
        RecipeInit.init();
        BlockStressValues.initialize();
    }

    @EventHandler
    public void youGotMail(FMLInterModComms.IMCEvent event) {
    }
}
