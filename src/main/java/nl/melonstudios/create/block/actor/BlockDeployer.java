package nl.melonstudios.create.block.actor;

import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import nl.melonstudios.create.block.BlockKineticDirectionalBase;
import nl.melonstudios.create.block.state.CreateStateProperties;
import nl.melonstudios.create.tileentity.TileEntityOptimizedBase;
import nl.melonstudios.create.tileentity.actor.TileEntityDeployer;
import nl.melonstudios.create.util.BlockProperties;
import nl.melonstudios.create.util.SubInteractionBox;

import javax.annotation.Nullable;

@SuppressWarnings("deprecation")
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

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
        if (placer.isSneaking()) {
            return this.getDefaultState().withProperty(FACING, facing.getOpposite());
        }
        EnumFacing side = EnumFacing.getDirectionFromEntityLiving(pos, placer);
        return this.getDefaultState().withProperty(FACING, side);
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileEntityDeployer();
    }

    @Override
    public boolean onWrenched(World world, BlockPos pos, IBlockState state, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (!super.onWrenched(world, pos, state, side, hitX, hitY, hitZ)) {
            TileEntity te = world.getTileEntity(pos);
            if (te instanceof TileEntityOptimizedBase) ((TileEntityOptimizedBase)te).preventNextRemoval();
            world.setBlockState(pos, state.cycleProperty(ROTATED));
            if (te != null) {
                te.validate();
                te.updateContainingBlockInfo();
                world.setTileEntity(pos, te);
            }
        }
        return true;
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (hand != EnumHand.MAIN_HAND) return false;
        if (SubInteractionBox.handleInteraction(worldIn, pos, playerIn, false,
                playerIn.isSneaking(), playerIn.getHeldItem(hand), hitX, hitY, hitZ)) return true;
        if (playerIn.isSneaking()) return false;
        EnumFacing blockFacing = state.getValue(FACING);
        if (facing == blockFacing) {
            return Boolean.TRUE.equals(withTEDo(worldIn, pos, TileEntityDeployer.class, (te) -> {
                ItemStack inHand = playerIn.getHeldItemMainhand();
                if (!te.heldItem.isEmpty()) {
                    ItemStack transfer = te.removeStackFromSlot(0);
                    playerIn.addItemStackToInventory(transfer);
                    if (!inHand.isEmpty()) {
                        te.setInventorySlotContents(0, inHand.copy());
                        playerIn.setHeldItem(EnumHand.MAIN_HAND, ItemStack.EMPTY);
                    }
                    return true;
                }
                if (!inHand.isEmpty()) {
                    playerIn.setHeldItem(EnumHand.MAIN_HAND, ItemStack.EMPTY);
                    te.setInventorySlotContents(0, inHand.copy());
                    return true;
                }
                return false;
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

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return BlockProperties.CASING_12PX_MAPPED[state.getValue(FACING).getIndex()];
    }

    @Override
    public boolean isToolEffective(String type, IBlockState state) {
        return "pickaxe".equals(type) || "axe".equals(type);
    }

    public static EnumFacing.Axis getShaftAxis(EnumFacing facing, boolean rotated) {
        return STATE_TO_AXIS_LOOKUP[facing.getIndex()*2 + (rotated ? 1 : 0)];
    }
    public static EnumFacing.Axis getFilterAxis(EnumFacing facing, boolean rotated) {
        return STATE_TO_AXIS_LOOKUP[facing.getIndex()*2 + (rotated ? 0 : 1)];
    }

    private static final EnumFacing.Axis[] STATE_TO_AXIS_LOOKUP = new EnumFacing.Axis[12];
    static {
        STATE_TO_AXIS_LOOKUP[0] = EnumFacing.Axis.X;
        STATE_TO_AXIS_LOOKUP[1] = EnumFacing.Axis.Z;
        STATE_TO_AXIS_LOOKUP[2] = EnumFacing.Axis.X;
        STATE_TO_AXIS_LOOKUP[3] = EnumFacing.Axis.Z;
        STATE_TO_AXIS_LOOKUP[4] = EnumFacing.Axis.X;
        STATE_TO_AXIS_LOOKUP[5] = EnumFacing.Axis.Y;
        STATE_TO_AXIS_LOOKUP[6] = EnumFacing.Axis.X;
        STATE_TO_AXIS_LOOKUP[7] = EnumFacing.Axis.Y;
        STATE_TO_AXIS_LOOKUP[8] = EnumFacing.Axis.Z;
        STATE_TO_AXIS_LOOKUP[9] = EnumFacing.Axis.Y;
        STATE_TO_AXIS_LOOKUP[10] = EnumFacing.Axis.Z;
        STATE_TO_AXIS_LOOKUP[11] = EnumFacing.Axis.Y;
    }
}
