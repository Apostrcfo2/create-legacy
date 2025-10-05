package nl.melonstudios.create.block;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import nl.melonstudios.create.init.ItemInit;
import nl.melonstudios.create.tileentity.TileEntityCogwheel;
import nl.melonstudios.create.util.BlockProperties;
import nl.melonstudios.create.util.interfaces.ICogwheel;
import nl.melonstudios.create.util.interfaces.IRotate;

import javax.annotation.Nullable;

public class BlockCogwheel extends BlockSimpleShaftBase implements ICogwheel {
    public final boolean isLarge;
    public BlockCogwheel(MapColor blockMapColorIn, SoundType soundTypeIn, boolean large) {
        super(Material.ROCK, blockMapColorIn);
        this.isLarge = large;
        this.blockSoundType = soundTypeIn;

        this.setRegistryName("cogwheel_" + (large ? "large" : "small"));
        this.setUnlocalizedName("create.cogwheel_" + (large ? "large" : "small"));

        this.setHardness(BlockProperties.STONE_HARDNESS);
        this.setResistance(BlockProperties.STONE_RESISTANCE);

        this.setHarvestLevel("pickaxe", 0);

        this.setCreativeTab(ItemInit.TAB_CREATE);
    }

    @Override
    public boolean isLargeCog() {
        return this.isLarge;
    }

    @Override
    public boolean isSmallCog() {
        return !this.isLarge;
    }

    protected EnumFacing.Axis getAxisForPlacement(World world, BlockPos pos, EntityLivingBase placer, EnumFacing side) {
        if (placer.isSneaking()) return side.getAxis();

        IBlockState stateBelow = world.getBlockState(pos.down());

        BlockPos placedOnPos = pos.offset(side.getOpposite());
        IBlockState placedAgainst = world.getBlockState(placedOnPos);

        Block block = placedAgainst.getBlock();
        if (ICogwheel.isSmallCog(placedAgainst)) return ((IRotate)block).getRotationAxis(placedAgainst);

        EnumFacing.Axis preferredAxis = getPreferredAxis(world, pos);
        return preferredAxis != null ? preferredAxis : side.getAxis();
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
        return this.getDefaultState().withProperty(AXIS, this.getAxisForPlacement(world, pos, placer, facing));
    }

    @Override
    public boolean isDedicatedCogwheel() {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileEntityCogwheel();
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        if (this.isLarge) return FULL_BLOCK_AABB;
        switch (state.getValue(AXIS)) {
            case X: return BlockProperties.GEAR_X_AABB;
            case Y: return BlockProperties.GEAR_Y_AABB;
            case Z: return BlockProperties.GEAR_Z_AABB;
            default:return FULL_BLOCK_AABB;
        }
    }
}
