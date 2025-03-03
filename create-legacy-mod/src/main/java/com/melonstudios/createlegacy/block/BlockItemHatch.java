package com.melonstudios.createlegacy.block;

import com.melonstudios.createlegacy.CreateLegacy;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
@SuppressWarnings("deprecation")
public class BlockItemHatch extends Block {
    public static final PropertyEnum<EnumFacing> FACING = PropertyEnum.create("facing", EnumFacing.class,
            EnumFacing.HORIZONTALS);
    public BlockItemHatch() {
        super(Material.IRON, MapColor.GRAY);
        setHardness(5.0f);
        setResistance(6.0f);

        setRegistryName("item_hatch");
        setUnlocalizedName("create.item_hatch");

        setCreativeTab(CreateLegacy.TAB_KINETICS);

        setHarvestLevel("pickaxe", 1);
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, FACING);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(FACING).getHorizontalIndex();
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(FACING, EnumFacing.getHorizontal(meta & 3));
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand,
                                    EnumFacing facing, float hitX, float hitY, float hitZ) {
        TileEntity te = worldIn.getTileEntity(pos.offset(state.getValue(FACING).getOpposite()));
        if (te instanceof IInventory) {
            IInventory inventory = (IInventory) te;
            int size = inventory.getSizeInventory();
            for (int i = 0; i < size; i++) {
                if (inventory.getStackInSlot(i).isEmpty()) {
                    if (!worldIn.isRemote) {
                        inventory.setInventorySlotContents(i, playerIn.getHeldItem(hand));
                        playerIn.setHeldItem(hand, ItemStack.EMPTY);
                        playerIn.playSound(SoundEvents.BLOCK_IRON_TRAPDOOR_CLOSE, 1.0f, 0.9f + worldIn.rand.nextFloat() * 0.2f);
                    }
                    return true;
                }
            }
        }
        return false;
    }

    private EnumFacing extract(EnumFacing facing, @Nullable EntityLivingBase player) {
        if (facing.getAxis() == EnumFacing.Axis.Y) {
            if (player != null) return player.getHorizontalFacing();
            return EnumFacing.NORTH;
        } else return facing;
    }
    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing,
                                            float hitX, float hitY, float hitZ, int meta,
                                            EntityLivingBase placer, EnumHand hand) {
        return this.getDefaultState().withProperty(FACING, extract(facing, placer));
    }
}
