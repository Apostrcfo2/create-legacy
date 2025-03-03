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
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.IBlockAccess;
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

    public static final AxisAlignedBB[] aabb = {
            new AxisAlignedBB(0, 0, 0, 1, 1, 0.5),
            new AxisAlignedBB(0.5, 0, 0, 1, 1, 1),
            new AxisAlignedBB(0, 0, 0.5, 1, 1, 1),
            new AxisAlignedBB(0, 0, 0, 0.5, 1, 1)
    };

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
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return aabb[state.getValue(FACING).getHorizontalIndex()];
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand,
                                    EnumFacing facing, float hitX, float hitY, float hitZ) {
        TileEntity te = worldIn.getTileEntity(pos.offset(state.getValue(FACING).getOpposite()));
        if (te instanceof IInventory) {
            IInventory container = (IInventory) te;
            int size = container.getSizeInventory();
            if (!playerIn.isSneaking()) {
                if (playerIn.getHeldItem(hand).isEmpty()) return false;
                for (int i = 0; i < size; i++) {
                    if (container.getStackInSlot(i).isEmpty()) {
                        if (!worldIn.isRemote) {
                            container.setInventorySlotContents(i, playerIn.getHeldItem(hand).copy());
                            playerIn.setHeldItem(hand, ItemStack.EMPTY);
                            playerIn.sendStatusMessage(new TextComponentTranslation(
                                    "text.create.item_deposited"), true
                            );
                            worldIn.playSound(null, pos, SoundEvents.BLOCK_IRON_TRAPDOOR_CLOSE, SoundCategory.BLOCKS,
                                    1.0f, 0.9f + worldIn.rand.nextFloat() * 0.2f);
                        }
                        break;
                    }
                }
            } else {
                boolean deposit = false;
                NonNullList<ItemStack> inventory = playerIn.inventory.mainInventory;
                int invSize = inventory.size();
                inv:
                for (int i = 9; i < invSize; i++) {
                    ItemStack invItem = inventory.get(i).copy();
                    if (!invItem.isEmpty()) {
                        for (int slot = 0; slot < size; slot++) {
                            if (container.getStackInSlot(slot).isEmpty()) {
                                inventory.set(i, ItemStack.EMPTY);
                                container.setInventorySlotContents(slot, invItem);
                                deposit = true;
                                continue inv;
                            }
                        }
                    }
                }
                if (deposit) {
                    worldIn.playSound(null, pos, SoundEvents.BLOCK_IRON_TRAPDOOR_CLOSE, SoundCategory.BLOCKS,
                            1.0f, 0.9f + worldIn.rand.nextFloat() * 0.2f);
                    playerIn.sendStatusMessage(new TextComponentTranslation(
                            "text.create.inventory_deposited"), true
                    );
                }
            }
            return true;
        }
        return false;
    }

    private EnumFacing extract(EnumFacing facing, @Nullable EntityLivingBase player) {
        if (facing.getAxis() == EnumFacing.Axis.Y) {
            if (player != null) return player.getHorizontalFacing().getOpposite();
            return EnumFacing.NORTH;
        } else return facing;
    }
    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing,
                                            float hitX, float hitY, float hitZ, int meta,
                                            EntityLivingBase placer, EnumHand hand) {
        return this.getDefaultState().withProperty(FACING, extract(facing, placer));
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
}
