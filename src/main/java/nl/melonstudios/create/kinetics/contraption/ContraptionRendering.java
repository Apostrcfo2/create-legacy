package nl.melonstudios.create.kinetics.contraption;

import io.netty.util.internal.ConcurrentSet;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import nl.melonstudios.create.CreateLegacy;
import nl.melonstudios.create.entity.EntityContraptionBase;

import javax.annotation.Nullable;
import java.util.*;

public class ContraptionRendering {
    public static final Set<EntityContraptionBase> CONTRAPTIONS_TO_RENDER = new ConcurrentSet<>();
    public static final Set<EntityContraptionBase> CONTRAPTIONS_TO_REMOVE = new ConcurrentSet<>();

    private static final HashMap<Contraption, int[]> RENDER_LISTS = new HashMap<>();
    public static void contraptionFinalized(Contraption contraption) {
        int[] list = RENDER_LISTS.remove(contraption);
        if (list != null) {
            deleteList(list);
        }
    }

    private static final List<EntityContraptionBase> COLLECTED_CONTRAPTIONS = new ArrayList<>();
    @SideOnly(Side.CLIENT)
    public static void collectContraptions(World world) {
        COLLECTED_CONTRAPTIONS.clear();
        for (Entity entity : world.loadedEntityList) {
            if (entity instanceof EntityContraptionBase) {
                COLLECTED_CONTRAPTIONS.add((EntityContraptionBase) entity);
            }
        }
    }
    @SideOnly(Side.CLIENT)
    public static List<EntityContraptionBase> getCollectedContraptions() {
        return COLLECTED_CONTRAPTIONS;
    }
    public static void cleanupContraptions(World world) {
        if (!world.isRemote) throw new IllegalArgumentException("Rendering only exists for client worlds!");
        List<EntityContraptionBase> entities = world.getEntities(EntityContraptionBase.class, e -> true);
        for (EntityContraptionBase entity : entities) {
            Contraption ctr = entity.attachedContraption();
            if (ctr != null && available(ctr)) {
                contraptionFinalized(ctr);
            }
        }
    }

    @SideOnly(Side.CLIENT)
    private static void deleteList(int[] list) {
        CreateLegacy.logger.info("Deleting contraption display list {}", formatList(list));
        GLAllocation.deleteDisplayLists(list[0], 1);
        GLAllocation.deleteDisplayLists(list[1], 1);
        GLAllocation.deleteDisplayLists(list[2], 1);
        GLAllocation.deleteDisplayLists(list[3], 1);
    }

    public static int[] getList(Contraption contraption) {
        if (RENDER_LISTS.containsKey(contraption)) {
            return RENDER_LISTS.get(contraption);
        } else {
            int[] list = createList(contraption);
            RENDER_LISTS.put(contraption, list);
            return list;
        }
    }
    @Nullable
    public static int[] getListNoCreate(Contraption contraption) {
        return RENDER_LISTS.get(contraption);
    }
    public static boolean available(Contraption contraption) {
        return RENDER_LISTS.containsKey(contraption);
    }

    @SideOnly(Side.CLIENT)
    private static final Object listLock = new Object();
    @SideOnly(Side.CLIENT)
    private static int[] createList(Contraption contraption) {
        synchronized (listLock) {
            contraption.isRendering = true;
            Minecraft mc = Minecraft.getMinecraft();
            BlockRendererDispatcher rendererDispatcher = mc.getBlockRendererDispatcher();
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder builder = tessellator.getBuffer();

            int solid = GLAllocation.generateDisplayLists(1);
            GlStateManager.glNewList(solid, 4864);
            builder.begin(7, DefaultVertexFormats.BLOCK);
            for (Map.Entry<BlockPos, IBlockState> entry : contraption.blocks.entrySet()) {
                if (entry.getValue().getBlock().canRenderInLayer(entry.getValue(), BlockRenderLayer.SOLID)) {
                    rendererDispatcher.renderBlock(entry.getValue(), entry.getKey(), contraption, builder);
                }
            }
            tessellator.draw();
            GlStateManager.glEndList();

            int cutout_mipped = GLAllocation.generateDisplayLists(1);
            GlStateManager.glNewList(cutout_mipped, 4864);
            builder.begin(7, DefaultVertexFormats.BLOCK);
            for (Map.Entry<BlockPos, IBlockState> entry : contraption.blocks.entrySet()) {
                if (entry.getValue().getBlock().canRenderInLayer(entry.getValue(), BlockRenderLayer.CUTOUT_MIPPED)) {
                    rendererDispatcher.renderBlock(entry.getValue(), entry.getKey(), contraption, builder);
                }
            }
            tessellator.draw();
            GlStateManager.glEndList();

            int cutout = GLAllocation.generateDisplayLists(1);
            GlStateManager.glNewList(cutout, 4864);
            builder.begin(7, DefaultVertexFormats.BLOCK);
            for (Map.Entry<BlockPos, IBlockState> entry : contraption.blocks.entrySet()) {
                if (entry.getValue().getBlock().canRenderInLayer(entry.getValue(), BlockRenderLayer.CUTOUT)) {
                    rendererDispatcher.renderBlock(entry.getValue(), entry.getKey(), contraption, builder);
                }
            }
            tessellator.draw();
            GlStateManager.glEndList();

            int translucent = GLAllocation.generateDisplayLists(1);
            GlStateManager.glNewList(translucent, 4864);
            builder.begin(7, DefaultVertexFormats.BLOCK);
            for (Map.Entry<BlockPos, IBlockState> entry : contraption.blocks.entrySet()) {
                if (entry.getValue().getBlock().canRenderInLayer(entry.getValue(), BlockRenderLayer.TRANSLUCENT)) {
                    rendererDispatcher.renderBlock(entry.getValue(), entry.getKey(), contraption, builder);
                }
            }
            tessellator.draw();
            GlStateManager.glEndList();
            contraption.isRendering = false;

            int[] list = new int[]{solid, cutout_mipped, cutout, translucent};
            CreateLegacy.logger.info("Creating contraption display list {}", formatList(list));
            return list;
        }
    }

    @SideOnly(Side.CLIENT)
    private static final HashSet<RenderContraption> renderContraptions = new HashSet<>();
    @SideOnly(Side.CLIENT)
    public static Collection<RenderContraption> getRenderContraptions() {
        return renderContraptions;
    }
    @SideOnly(Side.CLIENT)
    public static void clearRenderContraptions() {
        renderContraptions.clear();
    }
    @SideOnly(Side.CLIENT)
    public static void addRenderContraption(RenderContraption renderContraption) {
        renderContraptions.add(renderContraption);
    }

    @SideOnly(Side.CLIENT)
    private static final Minecraft mc = Minecraft.getMinecraft();
    @SideOnly(Side.CLIENT)
    public static float pt() {
        return mc.isGamePaused() ? 1.0F : mc.getRenderPartialTicks();
    }

    public static String formatList(int[] list) {
        return String.format("[%s;%s;%s;%s]", list[0], list[1], list[2], list[3]);
    }
}
