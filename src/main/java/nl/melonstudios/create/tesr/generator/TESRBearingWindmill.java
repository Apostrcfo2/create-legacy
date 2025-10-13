package nl.melonstudios.create.tesr.generator;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import nl.melonstudios.create.block.BlockRender;
import nl.melonstudios.create.tesr.actor.TESRBearing;
import nl.melonstudios.create.tileentity.generator.TileEntityBearingWindmill;
import nl.melonstudios.create.util.EnumRenderPart;
import nl.melonstudios.create.util.SubInteractionBox;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
@SideOnly(Side.CLIENT)
public class TESRBearingWindmill<T extends TileEntityBearingWindmill> extends TESRBearing<T> {
    @Override
    protected void render(T te, float pt, float alpha) {
        super.render(te, pt, alpha);

        if (SubInteractionBox.renderPotentialInteractionBoxes(this.mc.objectMouseOver, te)) {
            GlStateManager.translate(0.5, 0.5, 0.5);
            this.rotate(te.getFacing());
            GlStateManager.translate(-0.5, -0.5, -0.5);
            IBlockState state = BlockRender.byEnum(te.flipped ?
                    EnumRenderPart.COUNTERCLOCKWISE_ROTATION_INDICATOR : EnumRenderPart.CLOCKWISE_ROTATION_INDICATOR);
            IBakedModel model = this.mc.getBlockRendererDispatcher().getModelForState(state);
            this.renderBakedModel(1.0F, model, state);
        }
    }

    private void rotate(EnumFacing facing) {
        switch (facing) {
            case UP:
                break;
            case DOWN:
                GlStateManager.rotate(180.0F, 1.0F, 0.0F, 0.0F);
                break;
            case NORTH:
                GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
                break;
            case EAST:
                GlStateManager.rotate(90.0F, 0.0F, 1.0F, 0.0F);
                GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
                break;
            case SOUTH:
                GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
                GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
                break;
            case WEST:
                GlStateManager.rotate(-90.0F, 0.0F, 1.0F, 0.0F);
                GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
                break;
        }
    }
}
