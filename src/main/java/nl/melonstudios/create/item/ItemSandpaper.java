package nl.melonstudios.create.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import nl.melonstudios.create.init.ItemInit;
import nl.melonstudios.create.init.SoundInit;
import nl.melonstudios.create.recipe.SandingRecipes;

public class ItemSandpaper extends Item {
    public ItemSandpaper() {
        super();
        this.setMaxStackSize(1);
        this.setRegistryName("sandpaper");
        this.setUnlocalizedName("create.sandpaper");
        this.setMaxDamage(8);

        this.setCreativeTab(ItemInit.TAB_CREATE);
    }

    @Override
    public int getItemEnchantability() {
        return 1;
    }

    @Override
    public int getItemStackLimit() {
        return 1;
    }

    @Override
    public int getItemStackLimit(ItemStack stack) {
        return 1;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
        ItemStack paper = playerIn.getHeldItem(EnumHand.MAIN_HAND);
        if (handIn == EnumHand.MAIN_HAND) {
            ItemStack offhand = playerIn.getHeldItem(EnumHand.OFF_HAND);

            ItemStack result = SandingRecipes.instance.getResult(offhand);
            if (!result.isEmpty()) {
                playerIn.inventory.placeItemBackInInventory(worldIn, result);
                if (!playerIn.isCreative()) {
                    paper.damageItem(1, playerIn);
                    playerIn.getCooldownTracker().setCooldown(this, 40);
                    offhand.shrink(1);
                }
                worldIn.playSound(null, playerIn.posX, playerIn.posY, playerIn.posZ,
                        SoundInit.item_sandpaper_used, SoundCategory.PLAYERS, 1.0F, 0.9F + worldIn.rand.nextFloat() * 0.2F);
                return ActionResult.newResult(EnumActionResult.SUCCESS, paper);
            }
            return ActionResult.newResult(EnumActionResult.PASS, paper);
        }
        return ActionResult.newResult(EnumActionResult.PASS, playerIn.getHeldItem(handIn));
    }
}
