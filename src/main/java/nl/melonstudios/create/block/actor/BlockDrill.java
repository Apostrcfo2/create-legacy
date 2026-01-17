package nl.melonstudios.create.block.actor;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import nl.melonstudios.create.block.BlockKineticDirectionalBase;
import nl.melonstudios.create.init.DamageSourceInit;
import nl.melonstudios.create.tileentity.actor.TileEntityDrill;
import nl.melonstudios.create.util.BlockProperties;

import javax.annotation.Nullable;
import java.util.function.Consumer;

public class BlockDrill extends BlockKineticDirectionalBase implements ITileEntityProvider {
    public BlockDrill(MapColor color, SoundType soundType) {
        super(Material.ROCK, color);
        this.blockSoundType = soundType;
    }

    @Override
    public void onEntityCollidedWithBlock(World worldIn, BlockPos pos, IBlockState state, Entity entityIn) {
        if (entityIn instanceof EntityItem) return;
        if (!new AxisAlignedBB(pos).shrink(0.1).intersects(entityIn.getEntityBoundingBox())) return;
        this.withTEDo(worldIn, pos, te ->{
            if (te.getSpeed() == 0) return;
            entityIn.attackEntityFrom(DamageSourceInit.DRILLING, (float) getDamage(te.getSpeed()));
        });
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileEntityDrill();
    }

    @Override
    public boolean hasShaftTowards(World world, BlockPos pos, IBlockState state, EnumFacing side) {
        return side == state.getValue(FACING).getOpposite();
    }

    @Override
    public EnumFacing.Axis getRotationAxis(IBlockState state) {
        return state.getValue(FACING).getAxis();
    }

    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
        this.withTEDo(worldIn, pos, TileEntityDrill::destroyNextTick);
    }

    public static double getDamage(float speed) {
        speed = Math.abs(speed);
        double sub1 = Math.min(speed / 16, 2);
        double sub2 = Math.min(speed / 32, 4);
        double sub3 = Math.min(speed / 64, 4);
        return MathHelper.clamp(sub1 + sub2 + sub3, 1, 10);
    }

    private void withTEDo(World world, BlockPos pos, Consumer<TileEntityDrill> action) {
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TileEntityDrill) action.accept((TileEntityDrill) te);
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return BlockProperties.CASING_12PX_MAPPED[state.getValue(FACING).getIndex()];
    }

    @Override
    public boolean isToolEffective(String type, IBlockState state) {
        return "pickaxe".equals(type) || "axe".equals(type);
    }
}
