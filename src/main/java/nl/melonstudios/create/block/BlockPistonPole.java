package nl.melonstudios.create.block;

import com.melonstudios.melonlib.misc.BlockStateProperties;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import nl.melonstudios.create.init.ItemInit;
import nl.melonstudios.create.util.BlockProperties;

@SuppressWarnings("deprecation")
public class BlockPistonPole extends Block {
    public static final PropertyEnum<EnumFacing.Axis> AXIS = BlockStateProperties.AXIS;

    public BlockPistonPole() {
        super(Material.ROCK, MapColor.WOOD);
        this.blockSoundType = SoundType.WOOD;

        this.blockHardness = BlockProperties.WOOD_HARDNESS;
        this.blockResistance = BlockProperties.WOOD_RESISTANCE;

        this.setHarvestLevel("axe", -1);

        this.setCreativeTab(ItemInit.TAB_CREATE);
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, AXIS);
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
        return this.getDefaultState().withProperty(AXIS, facing.getAxis());
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        switch (state.getValue(AXIS)) {
            case X: return BlockProperties.POLE_X_AABB;
            case Y: return BlockProperties.POLE_Y_AABB;
            case Z: return BlockProperties.POLE_Z_AABB;
            default:throw new IllegalStateException("Unexpected value");
        }
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(AXIS).ordinal();
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(AXIS, EnumFacing.Axis.values()[meta % 3]);
    }
}
