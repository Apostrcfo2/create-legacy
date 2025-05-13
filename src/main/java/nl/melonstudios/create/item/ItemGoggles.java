package nl.melonstudios.create.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import nl.melonstudios.create.init.ItemInit;

import javax.annotation.Nullable;

public class ItemGoggles extends ItemArmor {
    public ItemGoggles() {
        super(ArmorMaterial.GOLD, 1, EntityEquipmentSlot.HEAD);
        this.setRegistryName("goggles");
        this.setUnlocalizedName("create.goggles");
        this.setMaxStackSize(1);
        this.setMaxDamage(0);

        this.setCreativeTab(ItemInit.TAB_CREATE);
    }

    @Override
    public boolean isDamageable() {
        return false;
    }

    @Override
    public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
        return false;
    }

    @Nullable
    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type) {
        return "create:textures/armor/goggles.png";
    }

    @Override
    public int getItemEnchantability() {
        return 0;
    }

    @Override
    protected boolean isInCreativeTab(CreativeTabs targetTab) {
        return targetTab == CreativeTabs.SEARCH || targetTab == ItemInit.TAB_CREATE;
    }
}
