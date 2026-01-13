package nl.melonstudios.create.tesr.actor;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import nl.melonstudios.create.tesr.TESRKineticBase;
import nl.melonstudios.create.tileentity.actor.TileEntityDeployer;
import nl.melonstudios.create.util.SubInteractionBox;

import javax.annotation.ParametersAreNonnullByDefault;

@SideOnly(Side.CLIENT)
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class TESRDeployer extends TESRKineticBase<TileEntityDeployer> {
    @Override
    protected void render(TileEntityDeployer te, float pt, float alpha) {
        GlStateManager.pushMatrix();
        SubInteractionBox.renderPotentialInteractionBoxes(this.mc.objectMouseOver, te);
        GlStateManager.popMatrix();
    }
}
