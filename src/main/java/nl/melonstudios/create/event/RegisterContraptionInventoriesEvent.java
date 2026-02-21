package nl.melonstudios.create.event;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.common.eventhandler.Event;

import java.util.HashSet;
import java.util.Set;

public class RegisterContraptionInventoriesEvent extends Event {
    private final Set<Class<? extends TileEntity>> validInventories = new HashSet<>();

    public void register(Class<? extends TileEntity> clazz) {
        this.validInventories.add(clazz);
    }
    public void load(Set<Class<? extends TileEntity>> set) {
        set.addAll(this.validInventories);
    }

    public RegisterContraptionInventoriesEvent() {

    }
}
