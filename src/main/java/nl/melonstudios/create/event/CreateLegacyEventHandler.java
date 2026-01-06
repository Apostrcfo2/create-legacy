package nl.melonstudios.create.event;

import com.melonstudios.melonlib.blockdict.BlockDictionary;
import com.melonstudios.melonlib.misc.Localizer;
import com.melonstudios.melonlib.render.RenderMelon;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.GetCollisionBoxesEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.client.config.GuiUtils;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import nl.melonstudios.create.CreateLegacy;
import nl.melonstudios.create.entity.EntityContraptionBearing;
import nl.melonstudios.create.entity.EntityGlue;
import nl.melonstudios.create.extensions.IExtensionWorld;
import nl.melonstudios.create.init.BlockInit;
import nl.melonstudios.create.init.ItemInit;
import nl.melonstudios.create.init.OreDictInit;
import nl.melonstudios.create.item.ItemGoggles;
import nl.melonstudios.create.kinetics.KNManager;
import nl.melonstudios.create.kinetics.contraption.ITileEntityWithContraption;
import nl.melonstudios.create.util.PerFrameDebugInfo;
import nl.melonstudios.create.util.interfaces.IBypassBlockUse;
import nl.melonstudios.create.util.interfaces.IGoggleInfo;
import nl.melonstudios.ponder.PonderRegistry;

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
    @SubscribeEvent
    public static void registerEntities(RegistryEvent.Register<EntityEntry> event) {
        event.getRegistry().register(EntityEntryBuilder.create().entity(EntityGlue.class)
                .factory(EntityGlue::new).id("create:glue", 0).name("create.glue")
                .tracker(64, 10, false).build());
        event.getRegistry().register(EntityEntryBuilder.create().entity(EntityContraptionBearing.class)
                .factory(EntityContraptionBearing::new).id("create:contraption_bearing", 1).name("create.contraption")
                .tracker(256, 10, false).build());
        CreateLegacy.proxy.registerEntityRenderers();
    }

    //Other
    @SubscribeEvent
    public static void gatherExtraCollisions(GetCollisionBoxesEvent event) {
        AxisAlignedBB aabb = event.getAabb();
        if (aabb == null) return;
        List<AxisAlignedBB> collisions = event.getCollisionBoxesList();
        for (ITileEntityWithContraption contraption : ((IExtensionWorld)event.getWorld()).create$getContraptionTileEntities()) {
            contraption.collectCollisions(aabb, collisions);
        }
    }

    @SubscribeEvent
    public static void glueBypassBlockUse(PlayerInteractEvent.RightClickBlock event) {
        if (event.getUseBlock() == Event.Result.DENY) return;
        ItemStack stack = event.getItemStack();
        if (stack.getItem() instanceof IBypassBlockUse) {
            if (((IBypassBlockUse)stack.getItem()).bypass(stack)) {
                if (!BlockDictionary.isBlockTagged(event.getWorld().getBlockState(event.getPos()), "create:bypassGlue")) {
                    event.setUseBlock(Event.Result.DENY);
                }
            }
        }
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
        event.registerCapacity(BlockInit.WATER_WHEEL, 16.0F);
        event.registerCapacity(BlockInit.BEARING_WINDMILL, 512.0F);
        event.registerStress(BlockInit.TURNTABLE, 4.0F);
        event.registerStress(BlockInit.BEARING, 4.0F);
        event.registerStress(BlockInit.PRESS, 8.0F);
        event.registerStress(BlockInit.DRILL, 4.0F);
        event.registerStress(BlockInit.SAW, 4.0F);
        event.registerStress(BlockInit.MILLSTONE, 4.0F);
    }

    private static final ItemStack goggles = new ItemStack(ItemInit.GOGGLES);
    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public static void renderGoggleOverlay(RenderGameOverlayEvent.Pre event) {
        final Minecraft mc = Minecraft.getMinecraft();
        if (mc.getRenderManager().options == null) return;
        final FontRenderer font = RenderMelon.getDefaultFontRenderer();
        if (mc.player != null && mc.world != null &&
                !mc.getRenderManager().options.hideGUI &&
                event.getType() == RenderGameOverlayEvent.ElementType.ALL &&
                mc.getRenderViewEntity() == mc.player) {
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
                            GuiUtils.drawHoveringText(goggles, info, (int)x, (int)y,
                                    event.getResolution().getScaledWidth(), event.getResolution().getScaledHeight(),
                                    Integer.MAX_VALUE, font
                            );
                            GlStateManager.disableLighting();
                            GlStateManager.popMatrix();
                        }
                    }
                }
            }
        }
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public static void renderDebugGoggleOverlay(RenderGameOverlayEvent.Post event) {
        if (PerFrameDebugInfo.renderAgain) {
            PerFrameDebugInfo.renderAgain = false;
            final Minecraft mc = Minecraft.getMinecraft();
            final FontRenderer font = RenderMelon.getDefaultFontRenderer();
            if (mc.world != null && mc.getRenderManager().options != null && mc.getRenderManager().options.hideGUI) {
                font.drawStringWithShadow("CREATE LEGACY DEBUG INTERFACE", 2, 2, -1);
                font.drawStringWithShadow("TESRKinetics rendered: " + PerFrameDebugInfo.kineticTileEntitiesRendered,
                        2, 12, -1);
                font.drawStringWithShadow("Total contraption TEs in client world: " +
                                ((IExtensionWorld)mc.world).    create$getContraptionTileEntities().size(),
                        2, 22, -1);
                PerFrameDebugInfo.reset();
            }
        }
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public static void addTooltips(ItemTooltipEvent event) {
        if (PonderRegistry.hasPonder(event.getItemStack())) {
            event.getToolTip().add(Localizer.translate("tooltip.create.pressForPonder"));
        }
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public static void postFrame(TickEvent.RenderTickEvent event) {
        if (event.phase == TickEvent.Phase.END) PerFrameDebugInfo.renderAgain = true;
    }
}