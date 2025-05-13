package nl.melonstudios.create.block;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import nl.melonstudios.create.init.ItemInit;
import nl.melonstudios.create.tileentity.TileEntityKinetic;
import nl.melonstudios.create.tileentity.TileEntityOptimizedBase;
import nl.melonstudios.create.util.BlockProperties;
import nl.melonstudios.create.util.TextBuilder;
import nl.melonstudios.create.util.interfaces.IGoggleInfo;
import nl.melonstudios.create.util.interfaces.IRotate;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Collections;
import java.util.List;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public abstract class BlockKineticBase extends Block implements IRotate, IGoggleInfo {
    public BlockKineticBase(Material blockMaterialIn, MapColor blockMapColorIn) {
        super(blockMaterialIn, blockMapColorIn);

        this.setHardness(BlockProperties.STONE_HARDNESS);
        this.setResistance(BlockProperties.STONE_RESISTANCE);

        this.setHarvestLevel("pickaxe", 0);

        this.setCreativeTab(ItemInit.TAB_CREATE);
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        TileEntity te = worldIn.getTileEntity(pos);
        if (te instanceof TileEntityOptimizedBase) ((TileEntityOptimizedBase)te).destroy();
        if (this.hasTileEntity(state)) worldIn.removeTileEntity(pos);
    }

    @Override
    public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
        super.onBlockAdded(worldIn, pos, state);
    }

    public boolean isPosPowered(World world, BlockPos pos) {
        return world.isBlockPowered(pos) || world.isBlockIndirectlyGettingPowered(pos) > 0;
    }

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

    @Nullable
    public static TileEntityKinetic getKineticTE(World world, BlockPos pos) {
        TileEntity te = world.getTileEntity(pos);
        return te instanceof TileEntityKinetic ? (TileEntityKinetic) te : null;
    }

    @Override
    public List<String> getGoggleInfo(World world, BlockPos pos, IBlockState state) {
        TileEntityKinetic te = getKineticTE(world, pos);
        if (te != null) {
            TextBuilder builder = new TextBuilder();
            float stressCapacity = te.calculateCapacity() * te.getTheoreticalSpeed();
            float stressImpact = te.calculateImpact() * te.getTheoreticalSpeed();
            if (stressCapacity == 0 && stressImpact == 0) return Collections.emptyList();
            builder.translate("goggles.generator_stats").enter();
            if (stressCapacity != 0) {
                builder.formatting(TextFormatting.GRAY)
                        .translate("goggles.generator_capacity")
                        .text(": ").formatting(TextFormatting.AQUA).number(stressCapacity).text("su").enter();
            }
            if (stressImpact != 0) {
                builder.formatting(TextFormatting.GRAY)
                        .translate("goggles.generator_impact")
                        .text(": ").formatting(TextFormatting.AQUA).number(stressImpact).text("su").enter();
            }
            return builder.build();
        }
        return Collections.emptyList();
    }
}
