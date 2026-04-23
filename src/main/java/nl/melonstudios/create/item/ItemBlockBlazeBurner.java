package nl.melonstudios.create.item;

import com.melonstudios.melonlib.item.ItemBlockVariants;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityBlaze;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import nl.melonstudios.create.extensions.IExtensionMobSpawnerBaseLogic;
import nl.melonstudios.create.util.Utils;

import java.util.Random;

public class ItemBlockBlazeBurner extends ItemBlockVariants {
    public ItemBlockBlazeBurner(Block block) {
        super(block);
    }

    @Override
    public boolean itemInteractionForEntity(ItemStack stack, EntityPlayer playerIn, EntityLivingBase target, EnumHand hand) {
        if (stack.getMetadata() == 0 && hand == EnumHand.MAIN_HAND && target.getClass() == EntityBlaze.class) {
            if (!playerIn.world.isRemote) {
                stack.shrink(1);
                playerIn.addItemStackToInventory(new ItemStack(this, 1, 1));
            } else {
                this.particles(playerIn.world, target.posX, target.posY + target.height * 0.5, target.posZ);
            }
            target.world.playSound(null, target.posX, target.posY, target.posZ,
                    SoundEvents.ENTITY_BLAZE_HURT, SoundCategory.HOSTILE, 1.0F, 1.3F);
            target.world.playSound(null, target.posX, target.posY, target.posZ,
                    SoundEvents.ENTITY_BLAZE_AMBIENT, SoundCategory.HOSTILE, 1.0F, 1.0F);
            target.setDead();
            return true;
        }
        return false;
    }

    private void particles(World world, double x, double y, double z) {
        Random rnd = world.rand;
        for (int i = 0; i < 20; i++) {
            world.spawnParticle(EnumParticleTypes.FLAME, x, y, z, rnd.nextGaussian()*0.1, rnd.nextGaussian()*0.1, rnd.nextGaussian()*0.1);
        }
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        ItemStack stack = player.getHeldItem(hand);
        if (stack.getMetadata() == 0 && hand == EnumHand.MAIN_HAND && worldIn.getBlockState(pos).getBlock() == Blocks.MOB_SPAWNER) {
            TileEntityMobSpawner te = Utils.cast(worldIn.getTileEntity(pos), TileEntityMobSpawner.class);
            if (te == null) return super.onItemUse(player, worldIn, pos, hand, facing, hitX, hitY, hitZ);
            IExtensionMobSpawnerBaseLogic extension = (IExtensionMobSpawnerBaseLogic) te.getSpawnerBaseLogic();
            if (extension.create$getSpawnedEntity() == EntityBlaze.class) {
                if (!worldIn.isRemote) {
                    stack.shrink(1);
                    player.addItemStackToInventory(new ItemStack(this, 1, 1));
                } else {
                    this.particles(worldIn, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.1);
                }
                worldIn.playSound(null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                        SoundEvents.ENTITY_BLAZE_HURT, SoundCategory.HOSTILE, 1.0F, 1.3F);
                worldIn.playSound(null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                        SoundEvents.ENTITY_BLAZE_AMBIENT, SoundCategory.HOSTILE, 1.0F, 1.0F);
                return EnumActionResult.SUCCESS;
            }
        }
        return super.onItemUse(player, worldIn, pos, hand, facing, hitX, hitY, hitZ);
    }
}
