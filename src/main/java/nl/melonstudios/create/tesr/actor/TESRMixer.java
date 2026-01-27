package nl.melonstudios.create.tesr.actor;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.MathHelper;
import nl.melonstudios.create.block.BlockRender;
import nl.melonstudios.create.tesr.TESRKineticBase;
import nl.melonstudios.create.tileentity.actor.TileEntityMixer;
import nl.melonstudios.create.util.EnumRenderPart;
import nl.melonstudios.create.util.Utils;

public class TESRMixer extends TESRKineticBase<TileEntityMixer> {
    @Override
    protected void render(TileEntityMixer te, float pt, float alpha) {
        this.spinShaftlessCog(te, te.getSpeed(), EnumFacing.Axis.Y, pt);

        float lowering = Utils.clampedLerp(pt, te.loweringOld, te.lowering) * -0.05F;
        GlStateManager.translate(0.0F, lowering, 0.0F);
        {
            IBlockState state = BlockRender.byEnum(EnumRenderPart.PRESS_Z);
            IBakedModel model = this.mc.getBlockRendererDispatcher().getModelForState(state);
            this.renderBakedModel(1.0F, model, state);
        }
        GlStateManager.translate(0.0F, -1.0F, 0.0F);
        {
            IBlockState state = BlockRender.byEnum(EnumRenderPart.WHISK);
            IBakedModel model = this.mc.getBlockRendererDispatcher().getModelForState(state);
            this.spinModel(te, pt, EnumFacing.Axis.Y, model, state, te.lowering == 20 ? 4.0F : 1.0F);
        }
    }
}
