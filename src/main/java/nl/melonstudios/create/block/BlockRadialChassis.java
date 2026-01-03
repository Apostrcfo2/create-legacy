package nl.melonstudios.create.block;

import it.unimi.dsi.fastutil.objects.Object2BooleanArrayMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.BlockRotatedPillar;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import nl.melonstudios.create.CreateLegacy;
import nl.melonstudios.create.entity.EntityGlue;
import nl.melonstudios.create.extensions.IExtensionBlock;
import nl.melonstudios.create.init.ItemInit;
import nl.melonstudios.create.kinetics.contraption.GluedSurface;
import nl.melonstudios.create.util.BlockProperties;
import nl.melonstudios.create.util.Utils;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class BlockRadialChassis extends BlockRotatedPillar implements IExtensionBlock {
    public BlockRadialChassis() {
        super(Material.ROCK, MapColor.WOOD);
        this.setSoundType(SoundType.WOOD);

        this.setHardness(BlockProperties.STONE_HARDNESS);
        this.setResistance(BlockProperties.STONE_RESISTANCE);
        this.setHarvestLevel("pickaxe", 0);

        this.setCreativeTab(ItemInit.TAB_CREATE);
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, AXIS);
    }

    @Override
    public void create$addStickyLocations(World world, BlockPos pos, IBlockState state, List<BlockPos> positions) {
        EnumFacing.Axis axis = state.getValue(AXIS);
        boolean[] glues = new boolean[6];
        List<EnumFacing> sides = Utils.getSurrounding(axis);
        for (EnumFacing side : sides) {
            GluedSurface surface = new GluedSurface(pos, side);
            glues[side.getIndex()] = !world.getEntities(EntityGlue.class, (glue) -> surface.equals(glue.getSurface())).isEmpty();
        }

        optimization:
        {
            for (boolean glue : glues) {
                if (glue) break optimization;
            }
            return; //Ignore next steps if there is no glue at all
        }

        int dist = this.getConnectionDistance(world, pos, state);
        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();

        List<BlockPos> wheel = new ArrayList<>();
        for (EnumFacing side : sides) {
            if (glues[side.getIndex()]) {
                mutable.setPos(pos).move(side);
                if (validPropagation(world, mutable)) {
                    BlockPos next = mutable.toImmutable();
                    wheel.add(next);
                    this.propagate(world, wheel, pos, next, mutable, dist*dist+1, sides);
                }
            }
        }

        positions.addAll(wheel);
        wheel.clear();
    }

    private void propagate(
            World world, List<BlockPos> positions, BlockPos middle,
            BlockPos src, BlockPos.MutableBlockPos mutable,
            int maxDist, List<EnumFacing> sides
    ) {
        for (EnumFacing side : sides) {
            mutable.setPos(src).move(side);
            if (validPropagation(world, mutable) && middle.distanceSq(mutable) < maxDist) {
                if (!positions.contains(mutable)) {
                    BlockPos next = mutable.toImmutable();
                    positions.add(next);
                    this.propagate(world, positions, middle, next, mutable, maxDist, sides);
                }
            }
        }
    }

    private static boolean validPropagation(World world, BlockPos pos) {
        return !world.getBlockState(pos).getBlock().isReplaceable(world, pos);
    }

    @SuppressWarnings("all")
    private int getConnectionDistance(World world, BlockPos pos, IBlockState state) {
        return 3;
    }
}
