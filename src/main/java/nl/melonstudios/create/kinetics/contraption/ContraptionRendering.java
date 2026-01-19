package nl.melonstudios.create.kinetics.contraption;

import io.netty.util.internal.ConcurrentSet;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import nl.melonstudios.create.CreateLegacy;
import nl.melonstudios.create.entity.EntityContraptionBase;

import java.util.*;

public class ContraptionRendering {
    public static final Set<EntityContraptionBase> CONTRAPTIONS_TO_RENDER = new ConcurrentSet<>();

    private static final HashMap<Contraption, int[]> RENDER_LISTS = new HashMap<>();
    public static void contraptionFinalized(Contraption contraption) {
        int[] list = RENDER_LISTS.remove(contraption);
        if (list != null) {
            deleteList(list);
        }
    }

    @SideOnly(Side.CLIENT)
    private static void deleteList(int[] list) {
        CreateLegacy.logger.debug("Deleting contraption display list (#{})", list);
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
            CreateLegacy.logger.debug("Creating contraption display list (#{})", list);
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
}
