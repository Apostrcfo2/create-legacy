package nl.melonstudios.create.kinetics;

import com.melonstudios.melonlib.misc.BlockStateProperties;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import nl.melonstudios.create.tileentity.TileEntityDirectionalShaftHalves;
import nl.melonstudios.create.tileentity.TileEntityGearbox;
import nl.melonstudios.create.tileentity.TileEntityKinetic;
import nl.melonstudios.create.tileentity.TileEntitySplitShaftBase;
import nl.melonstudios.create.util.interfaces.ICogwheel;
import nl.melonstudios.create.util.interfaces.IRotate;

import javax.annotation.Nullable;
import java.util.LinkedList;

import static nl.melonstudios.create.util.Utils.axis_choose;
import static nl.melonstudios.create.util.Utils.dist_manh;

public class KineticPropagator {
    public static final int MAX_FLICKER_SCORE = 128;

    private static float getRotationSpeedModifier(final TileEntityKinetic from, final TileEntityKinetic to) {
        final IBlockState stateFrom = from.getState();
        final IBlockState stateTo = to.getState();
        final Block fromBlock = from.getBlockType();
        final Block toBlock = to.getBlockType();
        if (!(fromBlock instanceof IRotate && toBlock instanceof IRotate)) return 0;
        final IRotate definitionFrom = (IRotate) fromBlock;
        final IRotate definitionTo = (IRotate) toBlock;
        final BlockPos diff = to.getPos().subtract(from.getPos());
        final EnumFacing direction = EnumFacing.getFacingFromVector(diff.getX(), diff.getY(), diff.getZ());
        final World world = from.getWorld();

        boolean alignedAxes = true;
        for (final Axis axis : Axis.values()) {
            if (axis != direction.getAxis()) if (axis_choose(axis, diff.getX(), diff.getY(), diff.getZ()) != 0) alignedAxes = false;
        }

        final boolean connectedByAxis =
                alignedAxes && definitionFrom.hasShaftTowards(world, from.getPos(), stateFrom, direction)
                && definitionTo.hasShaftTowards(world, to.getPos(), stateTo, direction.getOpposite());
        final boolean connectedByGears =
                ICogwheel.isSmallCog(stateFrom) && ICogwheel.isSmallCog(stateTo);

        {
            final float custom = from.propagateRotationTo(to, stateFrom, stateTo, diff, connectedByAxis, connectedByAxis);
            if (custom != 0.0F) return custom;
        }

        if (connectedByAxis) {
            float axisModifier = getAxisModifier(to, direction.getOpposite());
            if (axisModifier != 0.0F) axisModifier = 1.0F / axisModifier;
            return getAxisModifier(from, direction) * axisModifier;
        }

        if (isLargeToLargeGear(stateFrom, stateTo, diff)) {
            final Axis sourceAxis = stateFrom.getValue(BlockStateProperties.AXIS);
            final Axis targetAxis = stateTo.getValue(BlockStateProperties.AXIS);
            final int sourceAxisDiff = axis_choose(sourceAxis, diff.getX(), diff.getY(), diff.getZ());
            final int targetAxisDiff = axis_choose(targetAxis, diff.getX(), diff.getY(), diff.getZ());

            return sourceAxisDiff > 0 ^ targetAxisDiff > 0 ? -1 : 1;
        }

        if (ICogwheel.isLargeCog(stateFrom) && ICogwheel.isSmallCog(stateTo))
            if (isLargeToSmallCog(stateFrom, stateTo, definitionTo, diff)) return -2.0F;
        if (ICogwheel.isLargeCog(stateTo) && ICogwheel.isSmallCog(stateFrom))
            if (isLargeToSmallCog(stateTo, stateFrom, definitionFrom, diff)) return -0.5F;

        if (connectedByGears) {
            if (dist_manh(diff, BlockPos.ORIGIN) != 1) return 0.0F;
            if (ICogwheel.isLargeCog(stateTo)) return 0.0F;
            if (direction.getAxis() == definitionFrom.getRotationAxis(stateFrom)) return 0.0F;
            if (definitionFrom.getRotationAxis(stateFrom) == definitionTo.getRotationAxis(stateTo)) return -1.0F;
        }

        return 0.0F;
    }

    private static float getConveyedSpeed(final TileEntityKinetic from, final TileEntityKinetic to) {
        final IBlockState stateFrom = from.getState();
        final IBlockState stateTo = to.getState();

        final float rotationSpeedModifier = getRotationSpeedModifier(from, to);
        return from.getTheoreticalSpeed() * rotationSpeedModifier;
    }

    private static boolean isLargeToLargeGear(final IBlockState from, final IBlockState to, final BlockPos diff) {
        if (!ICogwheel.isLargeCog(from) || !ICogwheel.isLargeCog(to)) return false;
        final Axis fromAxis = from.getValue(BlockStateProperties.AXIS);
        final Axis toAxis = to.getValue(BlockStateProperties.AXIS);
        if (fromAxis == toAxis) return false;
        for (final Axis axis : Axis.values()) {
            int axisDiff = axis_choose(axis, diff.getX(), diff.getY(), diff.getZ());
            if (axis == fromAxis || axis == toAxis) {
                if (axisDiff == 0) return false;
            }
            else if (axisDiff != 0) {
                return false;
            }
        }
        return true;
    }

    private static float getAxisModifier(final TileEntityKinetic te, final EnumFacing direction) {
        if (!(te.hasSource() || te.isSource()) || !(te instanceof TileEntityDirectionalShaftHalves)) return 1.0F;
        final EnumFacing source = ((TileEntityDirectionalShaftHalves)te).getSourceFacing();

        if (te instanceof TileEntityGearbox) {
            return direction.getAxis() == source.getAxis() ? direction == source ? 1 : -1
                    : direction.getAxisDirection() == source.getAxisDirection() ? -1 : 1;
        }

        if (te instanceof TileEntitySplitShaftBase) {
            return ((TileEntitySplitShaftBase)te).getRotationSpeedModifier(direction);
        }

        return 1.0F;
    }

    private static boolean isLargeToSmallCog(final IBlockState from, final IBlockState to, final IRotate defTo, final BlockPos diff) {
        final Axis axisFrom = from.getValue(BlockStateProperties.AXIS);
        if (axisFrom != defTo.getRotationAxis(to)) return false;
        if (axis_choose(axisFrom, diff.getX(), diff.getY(), diff.getZ()) != 0) return false;
        for (final Axis axis : Axis.values()) {
            if (axis == axisFrom) continue;
            if (Math.abs(axis_choose(axis, diff.getX(), diff.getY(), diff.getZ())) != 1) return false;
        }
        return true;
    }

    private static boolean isLargeCogToSpeedController(final IBlockState from, final IBlockState to, final BlockPos diff) {
        return false; //TODO: implement speed controller
    }

    public static void handleAdded(final World world, final BlockPos pos, final TileEntityKinetic te) {
        if (world.isRemote) return;
        if (!world.isBlockLoaded(pos)) return;
        propagateNewSource(te);
    }

    private static void propagateNewSource(final TileEntityKinetic te) {
        final World world = te.getWorld();
        final BlockPos pos = te.getPos();

        for (final TileEntityKinetic neighbourTE : getConnectedNeighbours(te)) {
            final float speedOfCurrent = te.getTheoreticalSpeed();
            final float speedOfNeighbour = neighbourTE.getTheoreticalSpeed();
            final float newSpeed = getConveyedSpeed(te, neighbourTE);
            float oppositeSpeed = getConveyedSpeed(neighbourTE, te);

            if (newSpeed == 0 && oppositeSpeed == 0) continue;

            final boolean incompatible = Math.signum(newSpeed) != Math.signum(speedOfNeighbour) && (newSpeed != 0 && speedOfNeighbour != 0);
            final boolean tooFast = Math.abs(newSpeed) > 256.0F || Math.abs(oppositeSpeed) > 256.0F;

            final boolean speedChangedTooOften = te.getFlickerScore() > MAX_FLICKER_SCORE;
            if (tooFast || speedChangedTooOften) {
                world.destroyBlock(pos, true);
                return;
            }

            if (incompatible) {
                world.destroyBlock(pos, true);
                return;
            } else {
                if (Math.abs(oppositeSpeed) > Math.abs(speedOfCurrent)) {
                    final float lastSpeed = te.getSpeed();
                    te.setSource(neighbourTE.getPos());
                    te.speed = getConveyedSpeed(neighbourTE, te);
                    te.onSpeedChanged(lastSpeed);
                    te.sync();

                    propagateNewSource(te);
                    return;
                }

                if (Math.abs(newSpeed) >= Math.abs(speedOfNeighbour)) {
                    if (!te.hasNetwork() || te.networkID == neighbourTE.networkID) {
                        final float epsilon = Math.abs(speedOfNeighbour) / 256.0F / 256.0F;
                        if (Math.abs(newSpeed) > Math.abs(speedOfNeighbour) + epsilon) world.destroyBlock(pos, true);
                        continue;
                    }

                    if (te.hasSource() && te.source == neighbourTE.getPos()) te.removeSource();

                    final float lastSpeed = neighbourTE.getSpeed();
                    neighbourTE.setSource(te.getPos());
                    neighbourTE.speed = getConveyedSpeed(te, neighbourTE);
                    neighbourTE.onSpeedChanged(lastSpeed);
                    neighbourTE.sync();
                    propagateNewSource(neighbourTE);
                    continue;
                }
            }

            if (neighbourTE.getTheoreticalSpeed() == newSpeed) continue;

            final float lastSpeed = neighbourTE.getSpeed();
            neighbourTE.speed = newSpeed;
            neighbourTE.setSource(te.getPos());
            neighbourTE.onSpeedChanged(lastSpeed);
            neighbourTE.sync();
            propagateNewSource(neighbourTE);
        }
    }

    public static void handleRemoved(final World world, final BlockPos pos, final TileEntityKinetic te) {
        if (world.isRemote) return;
        if (te == null) return;
        if (te.getTheoreticalSpeed() == 0) return;

        for (final BlockPos neighbourPos : getPotentialNeighbourLocations(te)) {
            final IBlockState neighbourState = world.getBlockState(neighbourPos);
            if (!(neighbourState.getBlock() instanceof IRotate)) continue;
            final TileEntity tileEntity = world.getTileEntity(neighbourPos);
            if (!(tileEntity instanceof TileEntityKinetic)) continue;
            final TileEntityKinetic neighbourTE = (TileEntityKinetic) tileEntity;
            if (!neighbourTE.hasSource() || neighbourTE.source != pos) continue;

            propagateMissingSource(neighbourTE);
        }
    }

    private static void propagateMissingSource(final TileEntityKinetic te) {
        final World world = te.getWorld();

        final LinkedList<TileEntityKinetic> potentialNewSources = new LinkedList<>();
        final LinkedList<BlockPos> frontier = new LinkedList<>();
        frontier.add(te.getPos());
        final BlockPos missingSource = te.hasSource() ? te.source : null;

        while (!frontier.isEmpty()) {
            final BlockPos pos = frontier.remove(0);
            final TileEntity tileEntity = world.getTileEntity(pos);
            if (!(tileEntity instanceof TileEntityKinetic)) continue;
            final TileEntityKinetic currentTE = (TileEntityKinetic) tileEntity;

            currentTE.removeSource();
            currentTE.sync();

            for (final TileEntityKinetic neighbourTE : getConnectedNeighbours(currentTE)) {
                if (neighbourTE.getPos() == missingSource) continue;
                if (!neighbourTE.hasSource()) continue;

                if (neighbourTE.source != pos) {
                    potentialNewSources.add(neighbourTE);
                    continue;
                }

                if (neighbourTE.isSource()) potentialNewSources.add(neighbourTE);

                frontier.add(neighbourTE.getPos());
            }
        }

        for (final TileEntityKinetic newSource : potentialNewSources) {
            if (newSource.hasSource() || newSource.isSource()) {
                propagateNewSource(newSource);
                return;
            }
        }
    }

    @Nullable
    private static TileEntityKinetic findConnectedNeighbour(final TileEntityKinetic te, final BlockPos pos) {
        final IBlockState neighbourState = te.getWorld().getBlockState(pos);
        if (!(neighbourState.getBlock() instanceof IRotate)) return null;
        if (!neighbourState.getBlock().hasTileEntity(neighbourState)) return null;
        final TileEntity neighbourTE = te.getWorld().getTileEntity(pos);
        if (!(neighbourTE instanceof TileEntityKinetic)) return null;
        final TileEntityKinetic kinetic = (TileEntityKinetic) neighbourTE;
        if (!(kinetic.getBlockType() instanceof IRotate)) return null;
        if (!isConnected(te, kinetic) && !isConnected(kinetic, te)) return null;
        return kinetic;
    }

    public static boolean isConnected(final TileEntityKinetic from, final TileEntityKinetic to) {
        final IBlockState stateFrom = from.getState();
        final IBlockState stateTo = to.getState();
        return getRotationSpeedModifier(from, to) != 0 || from.isCustomConnection(to, stateFrom, stateTo);
    }

    private static LinkedList<TileEntityKinetic> getConnectedNeighbours(final TileEntityKinetic te) {
        final LinkedList<TileEntityKinetic> neighbours = new LinkedList<>();
        for (final BlockPos neighbourPos : getPotentialNeighbourLocations(te)) {
            final TileEntityKinetic neighbourTE = findConnectedNeighbour(te, neighbourPos);
            if (neighbourTE == null) continue;
            neighbours.add(neighbourTE);
        }
        return neighbours;
    }

    private static LinkedList<BlockPos> getPotentialNeighbourLocations(final TileEntityKinetic te) {
        final LinkedList<BlockPos> neighbours = new LinkedList<>();
        final BlockPos pos = te.getPos();
        final World world = te.getWorld();

        if (!world.isBlockLoaded(pos)) return neighbours;
        for (final EnumFacing side : EnumFacing.VALUES) {
            final BlockPos relative = pos.offset(side);
            if (world.isBlockLoaded(relative)) neighbours.add(relative);
        }

        final IBlockState state = te.getState();
        if (!(state.getBlock() instanceof IRotate)) return neighbours;
        return te.addPropagationLocations((IRotate) state.getBlock(), state, neighbours);
    }
}
