package nl.melonstudios.create.block;

import com.melonstudios.melonlib.item.IMetaName;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.BlockRotatedPillar;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import nl.melonstudios.create.entity.EntityGlue;
import nl.melonstudios.create.extensions.IExtensionBlock;
import nl.melonstudios.create.init.ItemInit;
import nl.melonstudios.create.kinetics.contraption.GluedSurface;
import nl.melonstudios.create.kinetics.contraption.IWrenchable;
import nl.melonstudios.create.tileentity.TileEntityDistanceController;
import nl.melonstudios.create.util.BlockProperties;
import nl.melonstudios.create.util.SubInteractionBox;
import nl.melonstudios.create.util.Utils;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class BlockChassisLinear extends BlockRotatedPillar implements IExtensionBlock, IMetaName, IWrenchable, ITileEntityProvider {
    public static final PropertyBool SECONDARY = PropertyBool.create("secondary");
    public BlockChassisLinear() {
        super(Material.ROCK, MapColor.WOOD);
        this.setSoundType(SoundType.WOOD);

        this.setHardness(BlockProperties.STONE_HARDNESS);
        this.setResistance(BlockProperties.STONE_RESISTANCE);
        this.setHarvestLevel("pickaxe", 0);

        this.setCreativeTab(ItemInit.TAB_CREATE);
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, AXIS, SECONDARY);
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        return stack.getMetadata() == 0 ? "tile.create.chassis_linear" : "tile.create.chassis_linear_secondary";
    }

    @Override
    public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> items) {
        items.add(new ItemStack(this, 1, 0));
        items.add(new ItemStack(this, 1, 1));
    }

    @Override
    public IBlockState getStateForPlacement(
            World world, BlockPos pos, EnumFacing facing,
            float hitX, float hitY, float hitZ, int meta,
            EntityLivingBase placer, EnumHand hand
    ) {
        boolean secondary = meta != 0;
        if (placer.isSneaking()) {
            return this.getDefaultState().withProperty(AXIS, facing.getAxis()).withProperty(SECONDARY, secondary);
        }
        IBlockState placedOn = world.getBlockState(pos.offset(facing.getOpposite()));
        if (placedOn.getBlock() == this && placedOn.getValue(SECONDARY) == secondary) {
            return placedOn;
        }
        return this.getDefaultState()
                .withProperty(AXIS, EnumFacing.getDirectionFromEntityLiving(pos, placer).getAxis())
                .withProperty(SECONDARY, secondary);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        int meta = super.getMetaFromState(state);
        return state.getValue(SECONDARY) ? meta | 0b0001 : meta;
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return super.getStateFromMeta(meta).withProperty(SECONDARY, (meta & 0b0001) != 0);
    }

    @Override
    public int damageDropped(IBlockState state) {
        return state.getValue(SECONDARY) ? 1 : 0;
    }

    @Override
    public void create$addStickyLocations(World world, BlockPos pos, IBlockState state, List<BlockPos> positions) {
        EnumFacing.Axis axis = state.getValue(AXIS);
        boolean[] glues = new boolean[6];
        List<EnumFacing> sides = Utils.getAlong(axis);
        for (EnumFacing side : sides) {
            GluedSurface surface = new GluedSurface(pos, side);
            glues[side.getIndex()] = !world.getEntities(EntityGlue.class, (glue) -> surface.equals(glue.getSurface())).isEmpty();
        }

        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
        optimization:
        {
            for (boolean glue : glues) {
                if (glue) break optimization;
            }
            return;
        }

        int dist = this.getConnectionDistance(world, pos);

        if (glues[sides.get(0).getIndex()]) {
            mutable.setPos(pos);
            for (int i = 0; i < dist; i++) {
                mutable.move(sides.get(0));
                if (validPropagation(world, pos)) {
                    if (!positions.contains(mutable)) positions.add(mutable.toImmutable());
                } else break;
            }
        }

        if (glues[sides.get(1).getIndex()]) {
            mutable.setPos(pos);
            for (int i = 0; i < dist; i++) {
                mutable.move(sides.get(1));
                if (validPropagation(world, pos)) {
                    if (!positions.contains(mutable)) positions.add(mutable.toImmutable());
                } else break;
            }
        }
    }

    private static boolean validPropagation(World world, BlockPos pos) {
        return !world.getBlockState(pos).getBlock().isReplaceable(world, pos);
    }

    private int getConnectionDistance(World world, BlockPos pos) {
        TileEntity te = world.getTileEntity(pos);
        return te instanceof TileEntityDistanceController ? ((TileEntityDistanceController)te).setDistance : 8;
    }

    @Override
    public boolean onWrenched(World world, BlockPos pos, IBlockState state, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (state.getValue(AXIS).apply(side)) return false;
        EnumFacing.Axis rotated = EnumFacing.getFacingFromAxis(EnumFacing.AxisDirection.POSITIVE, state.getValue(AXIS))
                .rotateAround(side.getAxis()).getAxis();
        world.setBlockState(pos, state.withProperty(AXIS, rotated));
        return true;
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileEntityDistanceController();
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (hand != EnumHand.MAIN_HAND) return false;
        return SubInteractionBox.handleInteraction(worldIn, pos, playerIn, false,
                playerIn.isSneaking(), playerIn.getHeldItem(hand), hitX, hitY, hitZ);
    }

    @Override
    public boolean isToolEffective(String type, IBlockState state) {
        return "pickaxe".equals(type) || "axe".equals(type);
    }
}
