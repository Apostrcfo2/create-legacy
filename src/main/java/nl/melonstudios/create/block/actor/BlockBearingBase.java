package nl.melonstudios.create.block.actor;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import nl.melonstudios.create.block.BlockKineticDirectionalBase;
import nl.melonstudios.create.block.state.CreateStateProperties;
import nl.melonstudios.create.extensions.IExtensionBlock;
import nl.melonstudios.create.tileentity.actor.TileEntityBearingBase;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public abstract class BlockBearingBase extends BlockKineticDirectionalBase implements ITileEntityProvider, IExtensionBlock {
    public static final PropertyBool ASSEMBLED = CreateStateProperties.ASSEMBLED;

    public BlockBearingBase() {
        super(Material.ROCK, MapColor.BROWN);

        this.setDefaultState(this.getDefaultState()
                .withProperty(FACING, EnumFacing.UP)
                .withProperty(ASSEMBLED, false)
        );
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, FACING, ASSEMBLED);
    }

    @Override
    public boolean hasShaftTowards(World world, BlockPos pos, IBlockState state, EnumFacing side) {
        return side.getOpposite() == state.getValue(FACING);
    }

    @Override
    public EnumFacing.Axis getRotationAxis(IBlockState state) {
        return state.getValue(FACING).getAxis();
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(FACING).getIndex() | (state.getValue(ASSEMBLED) ? 0b1000 : 0);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState()
                .withProperty(FACING, EnumFacing.VALUES[(meta & 0b0111) % 6])
                .withProperty(ASSEMBLED, (meta & 0b1000) != 0);
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (facing == state.getValue(FACING) || hand == EnumHand.OFF_HAND) return false;
        ItemStack held = playerIn.getHeldItem(hand);
        if (held.isEmpty()) {
            return Boolean.TRUE.equals(withTEDo(worldIn, pos, TileEntityBearingBase.class, TileEntityBearingBase::tryAssemble));
        }
        return false;
    }

    @Override
    public boolean onWrenched(World world, BlockPos pos, IBlockState state, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (super.onWrenched(world, pos, state, side, hitX, hitY, hitZ)) {
            withTEDo(world, pos, TileEntityBearingBase.class, TileEntityBearingBase::disassemble);
            return true;
        }
        return false;
    }

    @Override
    public boolean create$isSideSticky(IBlockState state, EnumFacing side) {
        return state.getValue(FACING) == side;
    }
}
