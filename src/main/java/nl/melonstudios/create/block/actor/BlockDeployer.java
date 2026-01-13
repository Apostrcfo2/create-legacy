package nl.melonstudios.create.block.actor;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import nl.melonstudios.create.block.BlockKineticDirectionalBase;
import nl.melonstudios.create.block.state.CreateStateProperties;
import nl.melonstudios.create.tileentity.actor.TileEntityDeployer;
import nl.melonstudios.create.util.SubInteractionBox;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class BlockDeployer extends BlockKineticDirectionalBase implements ITileEntityProvider {
    public static final PropertyBool ROTATED = CreateStateProperties.ROTATED;
    public BlockDeployer(MapColor color, SoundType sound) {
        super(Material.ROCK, color);
        this.setSoundType(sound);

        this.setDefaultState(this.blockState.getBaseState()
                .withProperty(FACING, EnumFacing.NORTH)
                .withProperty(ROTATED, false)
        );
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, FACING, ROTATED);
    }

    @Override
    public boolean hasShaftTowards(World world, BlockPos pos, IBlockState state, EnumFacing side) {
        return getRotationAxis(state) == side.getAxis();
    }

    @Override
    public EnumFacing.Axis getRotationAxis(IBlockState state) {
        return getShaftAxis(state.getValue(FACING), state.getValue(ROTATED));
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return super.getMetaFromState(state) | (state.getValue(ROTATED) ? 0b1000 : 0b0000);
    }
    @Override
    public IBlockState getStateFromMeta(int meta) {
        return super.getStateFromMeta(meta).withProperty(ROTATED, (meta & 0b1000) != 0);
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileEntityDeployer();
    }

    @Override
    public boolean onWrenched(World world, BlockPos pos, IBlockState state, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (!super.onWrenched(world, pos, state, side, hitX, hitY, hitZ)) {
            world.setBlockState(pos, state.cycleProperty(ROTATED));
        }
        return true;
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (hand != EnumHand.MAIN_HAND) return false;
        if (SubInteractionBox.handleInteraction(worldIn, pos, playerIn, false,
                playerIn.isSneaking(), playerIn.getHeldItem(hand), hitX, hitY, hitZ)) return true;
        EnumFacing blockFacing = state.getValue(FACING);
        if (facing == blockFacing) {
            return Boolean.TRUE.equals(withTEDo(worldIn, pos, TileEntityDeployer.class, (te) -> {
                return true;
            }));
        } else if (playerIn.getHeldItem(EnumHand.MAIN_HAND).isEmpty()) {
            return Boolean.TRUE.equals(withTEDo(worldIn, pos, TileEntityDeployer.class, (te) -> {
                if (te.cloggedItem.isEmpty()) return false;
                ItemStack stack = te.cloggedItem;
                playerIn.setHeldItem(EnumHand.MAIN_HAND, stack.copy());
                te.sync();
                return true;
            }));
        }
        return false;
    }

    public static EnumFacing.Axis getShaftAxis(EnumFacing facing, boolean rotated) {
        return EnumFacing.Axis.X;
    }
}
