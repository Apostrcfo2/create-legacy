package nl.melonstudios.create.kinetics.contraption;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import nl.melonstudios.create.CreateLegacy;

import java.util.HashMap;
import java.util.Map;

public class ContraptionRendering {
    private static final HashMap<Contraption, Integer> RENDER_LISTS = new HashMap<>();
    public static void contraptionFinalized(Contraption contraption) {
        if (RENDER_LISTS.containsKey(contraption)) {
            deleteList(RENDER_LISTS.get(contraption));
            RENDER_LISTS.remove(contraption);
        }
    }

    @SideOnly(Side.CLIENT)
    private static void deleteList(int list) {
        CreateLegacy.logger.debug("Deleting contraption display list (#{})", list);
        GLAllocation.deleteDisplayLists(list, BlockRenderLayer.values().length);
    }

    public static int getList(Contraption contraption) {
        if (RENDER_LISTS.containsKey(contraption)) {
            return RENDER_LISTS.get(contraption);
        } else {
            int list = createList(contraption);
            RENDER_LISTS.put(contraption, list);
            return list;
        }
    }

    @SideOnly(Side.CLIENT)
    private static int createList(Contraption contraption) {
        Minecraft mc = Minecraft.getMinecraft();
        BlockRendererDispatcher rendererDispatcher = mc.getBlockRendererDispatcher();
        int list = GLAllocation.generateDisplayLists(BlockRenderLayer.values().length);
        CreateLegacy.logger.debug("Creating new contraption display list (#{})", list);
        GlStateManager.glNewList(list, 4864);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder builder = tessellator.getBuffer();
        builder.begin(7, DefaultVertexFormats.BLOCK);
        for (Map.Entry<BlockPos, IBlockState> entry : contraption.blocks.entrySet()) {
            rendererDispatcher.renderBlock(entry.getValue(), entry.getKey(), contraption, builder);
        }
        tessellator.draw();
        GlStateManager.glEndList();
        return list;
    }
}
