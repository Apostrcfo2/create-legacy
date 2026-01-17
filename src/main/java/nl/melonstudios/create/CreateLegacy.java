package nl.melonstudios.create;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.*;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.common.registry.GameRegistry;
import nl.melonstudios.create.init.CommandInit;
import nl.melonstudios.create.init.OreDictInit;
import nl.melonstudios.create.init.RecipeInit;
import nl.melonstudios.create.init.SoundInit;
import nl.melonstudios.create.kinetics.BlockStressValues;
import nl.melonstudios.create.kinetics.contraption.Contraption;
import nl.melonstudios.create.proxy.CommonProxy;
import nl.melonstudios.create.recipe.*;
import nl.melonstudios.create.worldgen.CreateWorldGen;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Random;

import static nl.melonstudios.create.CreateLegacy.DEPENDENCIES;

@Mod(modid = CreateLegacy.MODID, name = CreateLegacy.NAME, version = CreateLegacy.VERSION, useMetadata = true, dependencies = DEPENDENCIES)
public class CreateLegacy {
    private static final boolean inIDE = true;
    public static final String MODID = "create";
    public static final String NAME = "Create Legacy";
    public static final String VERSION = "26w03a";
    static final String DEPENDENCIES = "required-after:melonlib@[1.4,)" + (inIDE ? "" : ";required-after-client:ctm");

    private static final HashMap<String, NBTDecodableRecipeType> DECODABLE_RECIPE_TYPE_MAP = new HashMap<>();
    public static void addNBTDecodableRecipe(NBTDecodableRecipeType type) {
        if (DECODABLE_RECIPE_TYPE_MAP.containsKey(type.getRecipeType())) {
            throw new IllegalArgumentException("Duplicate recipe ID: " + type.getRecipeType());
        }
        DECODABLE_RECIPE_TYPE_MAP.put(type.getRecipeType(), type);
    }

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
    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        network = NetworkRegistry.INSTANCE.newSimpleChannel(MODID);

        logger = event.getModLog();

        GameRegistry.registerWorldGenerator(new CreateWorldGen(), 0);

        SoundInit.init();

        addNBTDecodableRecipe(PressingRecipes.instance);
        addNBTDecodableRecipe(SandingRecipes.instance);
        addNBTDecodableRecipe(MillingRecipes.instance);
        addNBTDecodableRecipe(CuttingRecipes.instance);
        addNBTDecodableRecipe(DeployingRecipes.instance);
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        CreateLegacy.proxy.initiatePonders();
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        proxy.pork();
        OreDictInit.scanMetalTypes();
        RecipeInit.init();
        BlockStressValues.initialize();
        Contraption.registerValidInventoryClasses();
    }

    @EventHandler
    public void youGotMail(FMLInterModComms.IMCEvent event) {
        for (FMLInterModComms.IMCMessage message : event.getMessages()) {
            if (message.getMessageType() == NBTTagCompound.class) {
                NBTTagCompound nbt = message.getNBTValue();

                if (nbt.hasKey("RecipeData", 10)) {
                    NBTTagCompound data = nbt.getCompoundTag("RecipeData");
                    String recipeType = data.getString("type");
                    NBTDecodableRecipeType type = DECODABLE_RECIPE_TYPE_MAP.get(recipeType);
                    if (type != null) {
                        String recipeId = data.getString("id");
                        type.decodeRecipe(recipeId, data);
                    }
                }
            }
        }
    }

    @EventHandler
    public void serverStarting(FMLServerStartingEvent event) {
        CommandInit.addCreateCommand(event);
    }
}
