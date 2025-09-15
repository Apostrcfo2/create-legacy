package nl.melonstudios.create.block;

import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import nl.melonstudios.create.item.ItemSandpaper;
import nl.melonstudios.create.recipe.MillingRecipes;
import nl.melonstudios.create.tileentity.TileEntityMillstone;
import nl.melonstudios.create.util.interfaces.ICogwheel;

import javax.annotation.Nullable;

public class BlockMillstone extends BlockKineticBase implements ITileEntityProvider, ICogwheel {
    public BlockMillstone(Material blockMaterialIn, MapColor blockMapColorIn) {
        super(blockMaterialIn, blockMapColorIn);
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileEntityMillstone();
    }

    @Override
    public boolean hasShaftTowards(World world, BlockPos pos, IBlockState state, EnumFacing side) {
        return side == EnumFacing.DOWN;
    }

    @Override
    public EnumFacing.Axis getRotationAxis(IBlockState state) {
        return EnumFacing.Axis.Y;
    }

    @Override
    public void onFallenUpon(World worldIn, BlockPos pos, Entity entityIn, float fallDistance) {
        super.onFallenUpon(worldIn, pos, entityIn, fallDistance);

        if (worldIn.isRemote) return;
        if (!(entityIn instanceof EntityItem)) return;
        if (!entityIn.isEntityAlive()) return;
        ItemStack stack = ((EntityItem)entityIn).getItem();
        if (MillingRecipes.instance.getRecipeForInput(stack) == null) return;

        TileEntity te = worldIn.getTileEntity(pos);
        if (te instanceof TileEntityMillstone) {
            TileEntityMillstone millstone = (TileEntityMillstone) te;
            if (millstone.input.isEmpty()) {
                millstone.input = stack;
                entityIn.setDead();
                millstone.sync();
            }
        }
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (playerIn.getHeldItem(hand).isEmpty()) {
            TileEntity te = worldIn.getTileEntity(pos);
            if (te instanceof TileEntityMillstone) {
                TileEntityMillstone millstone = (TileEntityMillstone) te;
                boolean switchToMain = true;
                for (int i = 0; i < millstone.output.length; i++) {
                    ItemStack stack = millstone.output[i];
                    if (!stack.isEmpty()) {
                        switchToMain = false;
                        playerIn.inventory.addItemStackToInventory(stack);
                        millstone.output[i] = ItemStack.EMPTY;
                    }
                }
                if (switchToMain) {
                    if (millstone.input.isEmpty()) return false;
                    playerIn.inventory.addItemStackToInventory(millstone.input);
                    millstone.input = ItemStack.EMPTY;
                }
                return true;
            }
        }
        return false;
    }
}
