package nl.melonstudios.create.block;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class BlockEncasedShaftBase extends BlockKineticRotatedPillarBase {
    public BlockEncasedShaftBase(MapColor mapColor, SoundType soundType) {
        super(Material.ROCK, mapColor);
        this.blockSoundType = soundType;
    }

    @Override
    public boolean shouldCheckWeakPower(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
        return false;
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
        if (placer.isSneaking()) return super.getStateForPlacement(world, pos, facing, hitX, hitY, hitZ, meta, placer, hand);
        EnumFacing.Axis preferredAxis = getPreferredAxis(world, pos);
        return this.getDefaultState()
                .withProperty(AXIS, preferredAxis == null ? EnumFacing.getDirectionFromEntityLiving(pos, placer)
                        .getAxis() : preferredAxis);
    }

    @Override
    public boolean hasShaftTowards(World world, BlockPos pos, IBlockState state, EnumFacing side) {
        return side.getAxis() == state.getValue(AXIS);
    }

    @Override
    public EnumFacing.Axis getRotationAxis(IBlockState state) {
        return state.getValue(AXIS);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public float getAmbientOcclusionLightValue(IBlockState state) {
        return 0.2F;
    }

    @Override
    public boolean isToolEffective(String type, IBlockState state) {
        return "pickaxe".equals(type) || "axe".equals(type);
    }
}
