package nl.melonstudios.create.block.actor;

import com.melonstudios.melonlib.misc.AABB;
import com.melonstudios.melonlib.misc.BlockStateProperties;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import nl.melonstudios.create.block.state.EnumBeltPart;
import nl.melonstudios.create.tileentity.actor.TileEntityBeltBase;
import nl.melonstudios.create.tileentity.actor.TileEntityBeltStraight;

import javax.annotation.Nullable;

@SuppressWarnings("deprecation")
public class BlockBeltStraight extends BlockBeltBase {
    private static final AxisAlignedBB DEFAULT_AABB = AABB.create(0, 4, 0, 16, 12, 16);
    private static final AxisAlignedBB VERTICAL_X_AABB = AABB.create(4, 0, 0, 12, 16, 16);
    private static final AxisAlignedBB VERTICAL_Z_AABB = AABB.create(0, 0, 4, 16, 16, 12);

    public static final PropertyEnum<EnumFacing.Axis> AXIS = BlockStateProperties.HORIZONTAL_AXIS;
    public static final PropertyBool VERTICAL = PropertyBool.create("vertical");
    public BlockBeltStraight() {
        super();

        this.setDefaultState(this.getDefaultState()
                .withProperty(PART, EnumBeltPart.MIDDLE)
                .withProperty(VERTICAL, false)
                .withProperty(AXIS, EnumFacing.Axis.X)
        );
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, PART, VERTICAL, AXIS);
    }

    @Nullable
    @Override
    public TileEntityBeltBase createNewTileEntity(World worldIn, int meta) {
        return new TileEntityBeltStraight();
    }

    @Override
    public EnumFacing.Axis getTransportAxis(IBlockState state) {
        return state.getValue(AXIS);
    }

    @Override
    public boolean isFunctional(IBlockState state) {
        return state.getValue(AXIS) != EnumFacing.Axis.Y;
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        EnumFacing.Axis axis = ((meta >> 3) & 1) != 0 ? EnumFacing.Axis.Z : EnumFacing.Axis.X;
        boolean vertical = ((meta >> 2) & 1) != 0;
        EnumBeltPart part = EnumBeltPart.byId(meta & 3);
        return this.getDefaultState()
                .withProperty(PART, part)
                .withProperty(VERTICAL, vertical)
                .withProperty(AXIS, axis);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(PART).getId() | ((state.getValue(VERTICAL) ? 1 : 0) << 2) | ((state.getValue(AXIS) != EnumFacing.Axis.X ? 1 : 0) << 3);
    }

    @Override
    public EnumFacing.Axis getRotationAxis(IBlockState state) {
        return state.getValue(AXIS) == EnumFacing.Axis.X ? EnumFacing.Axis.Z : EnumFacing.Axis.X;
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return state.getValue(VERTICAL) ? (state.getValue(AXIS) == EnumFacing.Axis.X ? VERTICAL_X_AABB : VERTICAL_Z_AABB) : DEFAULT_AABB;
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.ENTITYBLOCK_ANIMATED;
    }
}
