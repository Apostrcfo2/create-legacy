package nl.melonstudios.create.tileentity.marker;

import net.minecraft.item.ItemStack;

public interface IHaltBeltContents {
    boolean shouldHaltItem(ItemStack stack);
}
