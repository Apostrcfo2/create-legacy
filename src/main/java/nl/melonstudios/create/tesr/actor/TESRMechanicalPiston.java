package nl.melonstudios.create.tesr.actor;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.MathHelper;
import nl.melonstudios.create.block.BlockPistonPole;
import nl.melonstudios.create.block.actor.BlockMechanicalPiston;
import nl.melonstudios.create.block.actor.BlockMechanicalPistonHead;
import nl.melonstudios.create.init.BlockInit;
import nl.melonstudios.create.kinetics.FastStateRendering;
import nl.melonstudios.create.tesr.TESRKineticBase;
import nl.melonstudios.create.tileentity.actor.TileEntityMechanicalPiston;

public class TESRMechanicalPiston<T extends TileEntityMechanicalPiston> extends TESRKineticBase<T> {
    public TESRMechanicalPiston() {}

    @Override
    protected void render(T te, float pt, float alpha) {
        EnumFacing facing = te.getState().getValue(BlockMechanicalPiston.FACING);
        boolean rotated = te.getState().getValue(BlockMechanicalPiston.ROTATED);
        this.spinShaft(te, pt, BlockMechanicalPiston.getShaftAxis(facing, rotated));

        if (te.numExtensionPoles != -1) {
            IBlockState state = BlockInit.PISTON_POLE.getDefaultState()
                    .withProperty(BlockPistonPole.AXIS, facing.getAxis());
            IBlockState head = BlockInit.PISTON_HEAD.getDefaultState()
                    .withProperty(BlockMechanicalPistonHead.FACING, facing)
                    .withProperty(BlockMechanicalPistonHead.STICKY, te.getBlock().sticky);
            double lerp = MathHelper.clampedLerp(te.extensionOld, te.extension, pt);
            float dx = facing.getFrontOffsetX();
            float dy = facing.getFrontOffsetY();
            float dz = facing.getFrontOffsetZ();
            for (int i = 0; i <= te.numExtensionPoles; i++) {
                GlStateManager.pushMatrix();
                double larp = i + lerp - te.numExtensionPoles;
                GlStateManager.translate(dx * larp, dy * larp, dz * larp);
                FastStateRendering.INSTANCE.renderFast(i == te.numExtensionPoles ? head : state);
                GlStateManager.popMatrix();
            }
        }
    }
}
