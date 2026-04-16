package nl.melonstudios.create.block.actor;

import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import nl.melonstudios.create.block.BlockKineticBase;
import nl.melonstudios.create.block.state.EnumBeltPart;
import nl.melonstudios.create.init.ItemInit;
import nl.melonstudios.create.tileentity.actor.TileEntityBeltBase;

import javax.annotation.Nullable;
import java.util.Random;

@SuppressWarnings("deprecation")
public abstract class BlockBeltBase extends BlockKineticBase implements ITileEntityProvider {
    public static final PropertyEnum<EnumBeltPart> PART = PropertyEnum.create("part", EnumBeltPart.class);

    public BlockBeltBase() {
        super(Material.ROCK, MapColor.BLACK);
        this.blockSoundType = SoundType.CLOTH;
    }

    @Override
    public boolean hasShaftTowards(World world, BlockPos pos, IBlockState state, EnumFacing side) {
        EnumBeltPart part = state.getValue(PART);
        return part != EnumBeltPart.MIDDLE && this.getRotationAxis(state) == side.getAxis();
    }

    @Nullable
    @Override
    public abstract TileEntityBeltBase createNewTileEntity(World worldIn, int meta);

    public abstract EnumFacing.Axis getTransportAxis(IBlockState state);
    public abstract boolean isFunctional(IBlockState state);

    @Override
    public abstract IBlockState getStateFromMeta(int meta);

    @Override
    public abstract int getMetaFromState(IBlockState state);

    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return ItemInit.BELT_CONNECTOR;
    }

    @Override
    public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
        return new ItemStack(ItemInit.BELT_CONNECTOR);
    }

    @Override
    public void onEntityCollidedWithBlock(World worldIn, BlockPos pos, IBlockState state, Entity entityIn) {
        if (entityIn.onGround && entityIn.isEntityAlive() && !worldIn.isRemote) {
            if (MathHelper.floor(entityIn.posX) == pos.getX() &&
                    MathHelper.floor(entityIn.posY) == pos.getY() &&
                    MathHelper.floor(entityIn.posZ) == pos.getZ()) {
                TileEntity te = worldIn.getTileEntity(pos);
                if (te instanceof TileEntityBeltBase && entityIn instanceof EntityItem) {
                    TileEntityBeltBase belt = (TileEntityBeltBase) te;
                    EntityItem item = (EntityItem) entityIn;

                    if (belt.queue.isEmpty()) {
                        item.setDead();
                        belt.queue = item.getItem();
                        belt.queuePos = 0.0F;
                        belt.sync();
                    }
                }
            }
        }
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.ENTITYBLOCK_ANIMATED;
    }
}
