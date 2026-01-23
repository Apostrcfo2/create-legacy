package nl.melonstudios.create.block;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import nl.melonstudios.create.init.ItemInit;
import nl.melonstudios.create.tileentity.TileEntityBasin;
import nl.melonstudios.create.tileentity.TileEntityOptimizedBase;
import nl.melonstudios.create.util.BlockProperties;
import nl.melonstudios.create.util.TextBuilder;
import nl.melonstudios.create.util.interfaces.IGoggleInfo;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

@SuppressWarnings("deprecation")
public class BlockBasin extends Block implements ITileEntityProvider, IGoggleInfo {
    public BlockBasin() {
        super(Material.IRON, MapColor.IRON);
        this.setSoundType(SoundType.ANVIL);

        this.setHardness(BlockProperties.IRON_HARDNESS);
        this.setResistance(BlockProperties.IRON_RESISTANCE);

        this.setHarvestLevel("pickaxe", 0);

        this.setCreativeTab(ItemInit.TAB_CREATE);
    }

    //region this is not a full block
    @Override
    public int getLightOpacity(IBlockState state, IBlockAccess world, BlockPos pos) {
        return 0;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isFullBlock(IBlockState state) {
        return false;
    }

    @Override
    public boolean isNormalCube(IBlockState state, IBlockAccess world, BlockPos pos) {
        return false;
    }

    @Override
    public boolean isTranslucent(IBlockState state) {
        return true;
    }

    @Override
    public boolean isNormalCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }
    //endregion


    @Override
    public BlockRenderLayer getBlockLayer() {
        return BlockRenderLayer.CUTOUT;
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileEntityBasin();
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        TileEntity te = worldIn.getTileEntity(pos);
        if (te instanceof TileEntityOptimizedBase) ((TileEntityOptimizedBase)te).destroy();
        if (this.hasTileEntity(state)) worldIn.removeTileEntity(pos);
    }

    @Override
    public List<String> getGoggleInfo(World world, BlockPos pos, IBlockState state) {
        List<String> list = BlockKineticBase.withTEDo(world, pos, TileEntityBasin.class, (te) -> {
            boolean inv = !te.isEmpty();
            boolean fluid = te.hasAnyFluid();
            if (!inv && !fluid) return null;
            TextBuilder builder = new TextBuilder();
            if (inv) {
                builder.translate("goggles.inventory_contents").enter();
                for (ItemStack stack : te.inventory) {
                    builder.space().space();
                    builder.formatting(TextFormatting.GRAY).text(stack.getCount() + "x ");
                    builder.formatting(TextFormatting.AQUA).text(stack.getDisplayName()).enter();
                }
            }
            if (fluid) {
                builder.translate("goggles.fluid_contents").enter();
                FluidStack fluid1 = te.tank1.getFluid();
                FluidStack fluid2 = te.tank2.getFluid();
                FluidStack fluid3 = te.tank3.getFluid();

                if (fluid1 != null && fluid1.amount > 0) {
                    builder.space().space();
                    builder.formatting(TextFormatting.GRAY).text(fluid1.amount + "mB ");
                    builder.formatting(TextFormatting.AQUA).text(fluid1.getLocalizedName()).enter();
                }
                if (fluid2 != null && fluid2.amount > 0) {
                    builder.space().space();
                    builder.formatting(TextFormatting.GRAY).text(fluid2.amount + "mB ");
                    builder.formatting(TextFormatting.AQUA).text(fluid2.getLocalizedName()).enter();
                }
                if (fluid3 != null && fluid3.amount > 0) {
                    builder.space().space();
                    builder.formatting(TextFormatting.GRAY).text(fluid3.amount + "mB ");
                    builder.formatting(TextFormatting.AQUA).text(fluid3.getLocalizedName()).enter();
                }
            }
            return builder.build();
        });
        return list != null ? list : Collections.emptyList();
    }
}
