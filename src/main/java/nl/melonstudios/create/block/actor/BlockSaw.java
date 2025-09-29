package nl.melonstudios.create.block.actor;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import nl.melonstudios.create.block.BlockKineticBase;
import nl.melonstudios.create.block.state.CreateStateProperties;
import nl.melonstudios.create.block.state.EnumSawRotation;
import nl.melonstudios.create.init.DamageSourceInit;
import nl.melonstudios.create.tileentity.TileEntityKinetic;
import nl.melonstudios.create.tileentity.actor.TileEntitySaw;
import nl.melonstudios.create.tileentity.actor.TileEntitySawProcessing;
import nl.melonstudios.create.util.BlockProperties;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@SuppressWarnings("deprecation")
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class BlockSaw extends BlockKineticBase implements ITileEntityProvider {
    public static final PropertyEnum<EnumSawRotation> FACING = CreateStateProperties.SAW_ROTATION;

    public BlockSaw(MapColor mapColor, SoundType soundType) {
        super(Material.ROCK, mapColor);
        this.setSoundType(soundType);

        this.setDefaultState(this.blockState.getBaseState()
                .withProperty(FACING, EnumSawRotation.UP_ALONG_X)
        );
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, FACING);
    }


    @Override
    public void onEntityCollidedWithBlock(World worldIn, BlockPos pos, IBlockState state, Entity entityIn) {
        if (entityIn instanceof EntityItem) return;
        if (!new AxisAlignedBB(pos).shrink(0.1).intersects(entityIn.getEntityBoundingBox())) return;
        withTEDo(worldIn, pos, TileEntityKinetic.class, te ->{
            if (te.getSpeed() == 0) return;
            entityIn.attackEntityFrom(DamageSourceInit.CUTTING, (float) BlockDrill.getDamage(te.getSpeed()));
        });
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return meta < 4 ? new TileEntitySaw() : meta > 5 ? null : new TileEntitySawProcessing();
    }

    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
        withTEDo(worldIn, pos, TileEntitySaw.class, TileEntitySaw::destroyNextTick);
    }

    @Override
    public boolean hasShaftTowards(World world, BlockPos pos, IBlockState state, EnumFacing side) {
        EnumSawRotation rotation = state.getValue(FACING);
        EnumFacing real = rotation.getToEnumFacing();
        if (real != EnumFacing.UP) {
            return side == real.getOpposite();
        }
        switch (side.getAxis()) {
            case X: return rotation == EnumSawRotation.UP_ALONG_X;
            case Z: return rotation == EnumSawRotation.UP_ALONG_Z;
            default:return false;
        }
    }

    @Override
    public EnumFacing.Axis getRotationAxis(IBlockState state) {
        EnumSawRotation rotation = state.getValue(FACING);
        if (rotation.getToEnumFacing() == EnumFacing.UP) {
            return rotation == EnumSawRotation.UP_ALONG_X ? EnumFacing.Axis.X : EnumFacing.Axis.Z;
        }
        if (rotation.getToEnumFacing() == EnumFacing.DOWN) {
            return rotation == EnumSawRotation.DOWN_ALONG_X ? EnumFacing.Axis.X : EnumFacing.Axis.Z;
        }
        return rotation.getToEnumFacing().getAxis();
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(FACING).getMeta();
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(FACING, EnumSawRotation.byMeta(meta & 7));
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
        if (placer.isSneaking()) {
            return this.getDefaultState().withProperty(FACING, EnumSawRotation.findSneakStateWithContext(facing, placer.getHorizontalFacing()));
        }
        return this.getDefaultState().withProperty(FACING, EnumSawRotation.findSneakStateWithContext(placer.getHorizontalFacing(), placer.getHorizontalFacing()));
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return BlockProperties.CASING_12PX_MAPPED[state.getValue(FACING).getToEnumFacing().getIndex()];
    }

    @Override
    public IBlockState withRotation(IBlockState state, Rotation rot) {
        if (rot == Rotation.NONE) return state;
       EnumSawRotation sawRotation = state.getValue(FACING);
       if (sawRotation.getToEnumFacing().getAxis() == EnumFacing.Axis.Y) {
           if (rot == Rotation.CLOCKWISE_180) return state;
           switch (sawRotation) {
               case UP_ALONG_X: return state.withProperty(FACING, EnumSawRotation.UP_ALONG_Z);
               case UP_ALONG_Z: return state.withProperty(FACING, EnumSawRotation.UP_ALONG_X);
               case DOWN_ALONG_X: return state.withProperty(FACING, EnumSawRotation.DOWN_ALONG_Z);
               case DOWN_ALONG_Z: return state.withProperty(FACING, EnumSawRotation.DOWN_ALONG_X);
           }
       }
       EnumFacing real = rot.rotate(sawRotation.getToEnumFacing());
       switch (real) {
           case NORTH: return state.withProperty(FACING, EnumSawRotation.NORTH);
           case EAST: return state.withProperty(FACING, EnumSawRotation.EAST);
           case SOUTH: return state.withProperty(FACING, EnumSawRotation.SOUTH);
           case WEST: return state.withProperty(FACING, EnumSawRotation.WEST);
           default: return state;
       }
    }

    @Override
    public IBlockState withMirror(IBlockState state, Mirror mirrorIn) {
        EnumSawRotation sawRotation = state.getValue(FACING);
        EnumFacing real = sawRotation.getToEnumFacing();
        if (real.getAxis() == EnumFacing.Axis.Y) {
            if (mirrorIn.mirror(real) != real) {
                switch (sawRotation) {
                    case UP_ALONG_X: return state.withProperty(FACING, EnumSawRotation.DOWN_ALONG_X);
                    case DOWN_ALONG_X: return state.withProperty(FACING, EnumSawRotation.UP_ALONG_X);
                    case UP_ALONG_Z: return state.withProperty(FACING, EnumSawRotation.DOWN_ALONG_Z);
                    case DOWN_ALONG_Z: return state.withProperty(FACING, EnumSawRotation.UP_ALONG_Z);
                }
            }
        } else {
            EnumFacing newRot = mirrorIn.mirror(real);
            switch (newRot) {
                case NORTH: state.withProperty(FACING, EnumSawRotation.NORTH);
                case EAST: state.withProperty(FACING, EnumSawRotation.EAST);
                case SOUTH: state.withProperty(FACING, EnumSawRotation.SOUTH);
                case WEST: state.withProperty(FACING, EnumSawRotation.WEST);
            }
        }
        return state;
    }
}
