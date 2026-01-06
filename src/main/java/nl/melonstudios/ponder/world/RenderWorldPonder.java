package nl.melonstudios.ponder.world;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Map;
import java.util.Set;

@SideOnly(Side.CLIENT)
public class RenderWorldPonder {
    private static Integer lastWorldPonder = null;
    private static final int[] worldDisplayLists = new int[4];

    public static void reserve(WorldPonder ponder) {
        int hash = System.identityHashCode(ponder);
        if (lastWorldPonder != null) {
            if (hash == lastWorldPonder) return;
            delete();
        }
        lastWorldPonder = hash;

        worldDisplayLists[0] = GLAllocation.generateDisplayLists(1);
        worldDisplayLists[1] = GLAllocation.generateDisplayLists(1);
        worldDisplayLists[2] = GLAllocation.generateDisplayLists(1);
        worldDisplayLists[3] = GLAllocation.generateDisplayLists(1);

        update(ponder);
    }
    public static void update(WorldPonder ponder) {
        if (lastWorldPonder == null) return;

        Minecraft mc = Minecraft.getMinecraft();
        BlockRendererDispatcher rendererDispatcher = mc.getBlockRendererDispatcher();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder builder = tessellator.getBuffer();
        Set<Map.Entry<BlockPos, IBlockState>> blocks = ponder.scene.blocks.entrySet();

        GlStateManager.glNewList(worldDisplayLists[0], 4864);
        builder.begin(7, DefaultVertexFormats.BLOCK);
        for (Map.Entry<BlockPos, IBlockState> entry : blocks) {
            if (entry.getValue().getBlock().canRenderInLayer(entry.getValue(), BlockRenderLayer.SOLID)) {
                rendererDispatcher.renderBlock(entry.getValue(), entry.getKey(), ponder, builder);
            }
        }
        tessellator.draw();
        GlStateManager.glEndList();

        GlStateManager.glNewList(worldDisplayLists[1], 4864);
        builder.begin(7, DefaultVertexFormats.BLOCK);
        for (Map.Entry<BlockPos, IBlockState> entry : blocks) {
            if (entry.getValue().getBlock().canRenderInLayer(entry.getValue(), BlockRenderLayer.CUTOUT_MIPPED)) {
                rendererDispatcher.renderBlock(entry.getValue(), entry.getKey(), ponder, builder);
            }
        }
        tessellator.draw();
        GlStateManager.glEndList();

        GlStateManager.glNewList(worldDisplayLists[2], 4864);
        builder.begin(7, DefaultVertexFormats.BLOCK);
        for (Map.Entry<BlockPos, IBlockState> entry : blocks) {
            if (entry.getValue().getBlock().canRenderInLayer(entry.getValue(), BlockRenderLayer.CUTOUT)) {
                rendererDispatcher.renderBlock(entry.getValue(), entry.getKey(), ponder, builder);
            }
        }
        tessellator.draw();
        GlStateManager.glEndList();

        GlStateManager.glNewList(worldDisplayLists[3], 4864);
        builder.begin(7, DefaultVertexFormats.BLOCK);
        for (Map.Entry<BlockPos, IBlockState> entry : blocks) {
            if (entry.getValue().getBlock().canRenderInLayer(entry.getValue(), BlockRenderLayer.TRANSLUCENT)) {
                rendererDispatcher.renderBlock(entry.getValue(), entry.getKey(), ponder, builder);
            }
        }
        tessellator.draw();
        GlStateManager.glEndList();
    }

    public static void delete() {
        if (lastWorldPonder == null) return;
        lastWorldPonder = null;
        GLAllocation.deleteDisplayLists(worldDisplayLists[0]);
        GLAllocation.deleteDisplayLists(worldDisplayLists[1]);
        GLAllocation.deleteDisplayLists(worldDisplayLists[2]);
        GLAllocation.deleteDisplayLists(worldDisplayLists[3]);
    }

    public static void callList(BlockRenderLayer layer) {
        if (lastWorldPonder == null) throw new RuntimeException("Tried rendering ponder world but it has no compiled layers");
        GlStateManager.callList(worldDisplayLists[layer.ordinal()]);
    }
}
