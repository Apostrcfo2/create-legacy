package nl.melonstudios.create.tileentity.actor;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import nl.melonstudios.create.block.actor.BlockDrill;
import nl.melonstudios.create.kinetics.contraption.IContraptionActor;
import nl.melonstudios.create.kinetics.contraption.accessor.IContraptionAccessor;
import nl.melonstudios.create.tileentity.TileEntityBreakBlockBase;
import org.lwjgl.util.vector.Vector3f;

public class TileEntityDrill extends TileEntityBreakBlockBase implements IContraptionActor {
    public TileEntityDrill() {
        this.setTickRateLazy(10);
    }

    @Override
    protected BlockPos getBreakingPos() {
        return this.pos.offset(this.getState().getValue(BlockDrill.FACING));
    }
    protected void getBreakingPosVec(Vector3f store) {
        store.set(this.pos.getX(), this.pos.getY(), this.pos.getZ());
        EnumFacing facing = this.getState().getValue(BlockDrill.FACING);
        store.translate(facing.getFrontOffsetX(), facing.getFrontOffsetY(), facing.getFrontOffsetZ());
    }

    @Override
    public void setOnContraption(boolean onContraption) {
        this.speed = onContraption ? 64.0F : 0.0F;
    }
    @Override
    public void contraptionTick(IContraptionAccessor contraption, World world, Vector3f position, BlockPos blockPosition, boolean moved, Vector3f movement) {
        if (this._tick(contraption, world, position, blockPosition, moved, movement)) {
            contraption.pauseContraption();
        }
    }

    private final BlockPos.MutableBlockPos lastBreakingPos = new BlockPos.MutableBlockPos();
    private final Vector3f breakingPosVec = new Vector3f();
    private boolean _tick(IContraptionAccessor contraption,  World world, Vector3f position, BlockPos blockPosition, boolean moved, Vector3f movement) {
        if (this.breakingPos != null) this.lastBreakingPos.setPos(this.breakingPos);
        this.getBreakingPosVec(this.breakingPosVec);
        this.breakingPos = contraption.getWorldPos(this.breakingPosVec);
        if (!this.lastBreakingPos.equals(this.breakingPos)) {
            this.ticksUntilNextProgress = 0;
        }
        if (this.ticksUntilNextProgress < 0) {
            return false;
        }
        if (this.ticksUntilNextProgress-- > 0) {
            return true;
        }

        IBlockState stateToBreak = world.getBlockState(this.breakingPos);
        float blockHardness = stateToBreak.getBlockHardness(world, this.breakingPos);

        if (!this.canBreak(stateToBreak, blockHardness)) {
            if (this.destroyProgress != 0) {
                this.destroyProgress = 0;
                if (!world.isRemote) {
                    world.sendBlockBreakProgress(this.breakerId, this.breakingPos, -1);
                }
            }
            return false;
        }

        float breakSpeed = Math.abs(this.speed) * 0.01F;
        this.destroyProgress += MathHelper.clamp((int)(breakSpeed / blockHardness), 1, 10 - this.destroyProgress);
        if (!world.isRemote) {
            world.playSound(null, this.breakingPos, stateToBreak.getBlock().getSoundType().getHitSound(),
                    SoundCategory.BLOCKS, .25F, 1.0F);
        }

        if (this.destroyProgress >= 10) {
            if (!world.isRemote) {
                world.destroyBlock(this.breakingPos, true);
            }
            this.destroyProgress = 0;
            this.ticksUntilNextProgress = -1;
            if (!world.isRemote) {
                world.sendBlockBreakProgress(this.breakerId, this.breakingPos, -1);
            }
            return false;
        }

        this.ticksUntilNextProgress = (int) (blockHardness / breakSpeed);
        if (!world.isRemote) {
            world.sendBlockBreakProgress(this.breakerId, this.breakingPos, this.destroyProgress);
        }
        return true;
    }
}
