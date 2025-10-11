package nl.melonstudios.create.item;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import nl.melonstudios.create.extensions.IExtensionWorld;
import nl.melonstudios.create.init.ItemInit;
import nl.melonstudios.create.kinetics.contraption.GluedSurface;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ItemGlue extends Item {
    public ItemGlue(int durability) {
        this.canRepair = false;
        this.setHasSubtypes(false);
        this.setMaxDamage(durability);
        this.setMaxStackSize(1);

        this.setCreativeTab(ItemInit.TAB_CREATE);
    }

    @Override
    public boolean onBlockStartBreak(ItemStack itemstack, BlockPos pos, EntityPlayer player) {
        World world = player.world;
        RayTraceResult result = player.rayTrace(6.0F, 1.0F);
        if (result != null && result.typeOfHit == RayTraceResult.Type.BLOCK && pos.equals(result.getBlockPos())) {
            GluedSurface surface = new GluedSurface(pos, result.sideHit);
            if (((IExtensionWorld)world).create$removeGluedSurface(surface)) {
                player.playSound(SoundEvents.BLOCK_SLIME_BREAK, 1.0F, 0.5F);
                return true;
            }
        }
        return false;
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        GluedSurface surface = new GluedSurface(pos, facing);
        if (((IExtensionWorld)worldIn).create$addGluedSurface(surface)) {
            player.playSound(SoundEvents.BLOCK_SLIME_PLACE, 1.0F, 1.0F);
            if (!player.isCreative()) {
                player.getHeldItem(hand).damageItem(1, player);
            }
            return EnumActionResult.SUCCESS;
        }
        return EnumActionResult.FAIL;
    }
}
