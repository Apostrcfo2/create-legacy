package nl.melonstudios.create.util;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.function.Supplier;

public class ModTabs extends CreativeTabs {
    public ModTabs(String label, Supplier<ItemStack> icon) {
        super(label);
        this.icon = icon;
    }

    private final Supplier<ItemStack> icon;

    @Override
    @SideOnly(Side.CLIENT)
    public ItemStack getTabIconItem() {
        return this.icon.get();
    }
}
