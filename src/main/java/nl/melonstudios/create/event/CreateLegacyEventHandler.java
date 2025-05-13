package nl.melonstudios.create.event;

import com.melonstudios.melonlib.misc.Localizer;
import com.melonstudios.melonlib.render.RenderMelon;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.InventoryEffectRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.world.GetCollisionBoxesEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.client.config.GuiUtils;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import nl.melonstudios.create.init.BlockInit;
import nl.melonstudios.create.init.ItemInit;
import nl.melonstudios.create.init.OreDictInit;
import nl.melonstudios.create.item.ItemGoggles;
import nl.melonstudios.create.kinetics.KNManager;
import nl.melonstudios.create.util.interfaces.IGoggleInfo;

import java.util.Iterator;
import java.util.List;

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

    private static final ItemStack goggles = new ItemStack(ItemInit.GOGGLES);
    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public static void renderGoggleOverlay(RenderGameOverlayEvent.Pre event) {
        final Minecraft mc = Minecraft.getMinecraft();
        final FontRenderer font = RenderMelon.getDefaultFontRenderer();
        if (mc.player != null && mc.world != null && event.getType() == RenderGameOverlayEvent.ElementType.ALL) {
            final ItemStack helmet = mc.player.inventory.armorInventory.get(3);
            if (helmet.getItem() instanceof ItemGoggles) {
                RayTraceResult result = mc.objectMouseOver;
                if (result != null && result.typeOfHit == RayTraceResult.Type.BLOCK) {
                    BlockPos pos = result.getBlockPos();
                    World world = mc.world;
                    IBlockState state = world.getBlockState(pos);
                    if (state.getBlock() instanceof IGoggleInfo) {
                        List<String> info = ((IGoggleInfo)state.getBlock()).getGoggleInfo(world, pos, state);
                        if (!info.isEmpty()) {
                            GlStateManager.pushMatrix();
                            float x = event.getResolution().getScaledWidth() / 2.0F;
                            float y = event.getResolution().getScaledHeight() / 2.0F;
                            mc.getRenderItem().renderItemIntoGUI(goggles, (int)x - 24, (int)y - 8);
                            info.add(0, Localizer.translate("goggles.header") + ":");
                            GuiUtils.drawHoveringText(goggles, info, (int)x, (int)y,
                                    event.getResolution().getScaledWidth(), event.getResolution().getScaledHeight(),
                                    Integer.MAX_VALUE, font
                            );
                            GlStateManager.popMatrix();
                        }
                    }
                }
            }
        }
    }
}
