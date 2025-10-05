package nl.melonstudios.create.tileentity.marker;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public interface IInventoryDebloated extends IInventory {
    @Override
    default int getField(int id) {
        return 0;
    }

    @Override
    default void setField(int id, int value) {

    }

    @Override
    default int getFieldCount() {
        return 0;
    }

    @Override
    default void closeInventory(EntityPlayer player) {

    }

    @Override
    default void openInventory(EntityPlayer player) {

    }

    @Override
    String getName();

    @Override
    default boolean hasCustomName() {
        return false;
    }

    @Override
    default boolean isUsableByPlayer(EntityPlayer player) {
        return true;
    }
}
