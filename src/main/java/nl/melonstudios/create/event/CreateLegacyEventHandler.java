package nl.melonstudios.create.event;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.world.World;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.world.GetCollisionBoxesEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import nl.melonstudios.create.init.BlockInit;
import nl.melonstudios.create.init.ItemInit;
import nl.melonstudios.create.init.OreDictInit;
import nl.melonstudios.create.kinetics.KNManager;

@Mod.EventBusSubscriber(modid = "create")
public class CreateLegacyEventHandler {
    //Registration
    @SubscribeEvent
    public static void registerItemModels(ModelRegistryEvent event) {
        ItemInit.setItemModels();
    }

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event) {
        event.getRegistry().registerAll(BlockInit.BLOCKS.toArray(new Block[0]));
        BlockInit.registerTileEntities();
    }
    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        event.getRegistry().registerAll(ItemInit.ITEMS.toArray(new Item[0]));
        OreDictInit.init();
    }

    //Other
    @SubscribeEvent
    public static void gatherExtraCollisions(GetCollisionBoxesEvent event) {

    }

    @SubscribeEvent
    public static void onWorldLoad(WorldEvent.Load event) {
        KNManager.loadWorld(event.getWorld());
    }
    @SubscribeEvent
    public static void onWorldUnload(WorldEvent.Unload event) {
        KNManager.unloadWorld(event.getWorld());
    }

    @SubscribeEvent
    public static void registerStressValues(RegisterStressValuesEvent event) {
        event.registerCapacity(BlockInit.HAND_CRANK, 8.0F);
    }
}
