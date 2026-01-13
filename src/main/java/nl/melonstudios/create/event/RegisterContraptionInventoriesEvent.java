package nl.melonstudios.create.event;

import net.minecraft.inventory.IInventory;
import net.minecraftforge.fml.common.eventhandler.Event;

import java.util.HashSet;
import java.util.Set;

public class RegisterContraptionInventoriesEvent extends Event {
    private final Set<Class<? extends IInventory>> validInventories = new HashSet<>();

    public void register(Class<? extends IInventory> clazz) {
        this.validInventories.add(clazz);
    }
    public void load(Set<Class<? extends IInventory>> set) {
        set.addAll(this.validInventories);
    }
}
