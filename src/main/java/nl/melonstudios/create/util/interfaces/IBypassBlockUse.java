package nl.melonstudios.create.util.interfaces;

import net.minecraft.item.ItemStack;

public interface IBypassBlockUse {
    default boolean bypass(ItemStack stack) {
        return true;
    }
}
