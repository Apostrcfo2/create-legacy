package nl.melonstudios.create.block;

import com.melonstudios.melonlib.misc.BlockStateProperties;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import nl.melonstudios.create.kinetics.KineticPropagator;
import nl.melonstudios.create.tileentity.TileEntityGearshift;
import nl.melonstudios.create.tileentity.TileEntityKinetic;

import javax.annotation.Nullable;
import java.util.Random;

public class BlockGearshift extends BlockEncasedShaftBase implements ITileEntityProvider {
    public static final PropertyBool POWERED = BlockStateProperties.POWERED;

    public BlockGearshift(MapColor mapColor, SoundType soundType) {
        super(mapColor, soundType);

        this.setDefaultState(this.getDefaultState().withProperty(POWERED, false));
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, AXIS, POWERED);
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
        return super.getStateForPlacement(world, pos, facing, hitX, hitY, hitZ, meta, placer, hand)
                .withProperty(POWERED, isPosPowered(world, pos));
    }

    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
        if (worldIn.isRemote) return;

        boolean lastPowered = state.getValue(POWERED);
        if (lastPowered != isPosPowered(worldIn, pos)) {
            this.detachKinetics(worldIn, pos, true);
            worldIn.setBlockState(pos, state.cycleProperty(POWERED), 2);
        }
    }

    public void detachKinetics(World world, BlockPos pos, boolean reattachNextTick) {
        TileEntity te = world.getTileEntity(pos);
        if (!(te instanceof TileEntityKinetic)) return;
        KineticPropagator.handleRemoved(world, pos, (TileEntityKinetic) te);

        if (reattachNextTick) world.scheduleUpdate(pos, this, 1);
    }

    @Override
    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
        TileEntity te = worldIn.getTileEntity(pos);
        if (!(te instanceof TileEntityKinetic)) return;
        KineticPropagator.handleAdded(worldIn, pos, (TileEntityKinetic) te);
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileEntityGearshift();
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return super.getMetaFromState(state) | (state.getValue(POWERED) ? 0b1000 : 0b0000);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return super.getStateFromMeta(meta).withProperty(POWERED, (meta & 0b1000) != 0);
    }
}
