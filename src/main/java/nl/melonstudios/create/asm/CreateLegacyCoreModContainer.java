package nl.melonstudios.create.asm;

import net.minecraftforge.fml.common.DummyModContainer;
import net.minecraftforge.fml.common.ModMetadata;

import java.util.Collections;

public class CreateLegacyCoreModContainer extends DummyModContainer {
    public CreateLegacyCoreModContainer() {
        super(new ModMetadata());
        ModMetadata meta = this.getMetadata();
        meta.modId = "create_core";
        meta.name = "Create Legacy Core";
        meta.version = "1.0";
        meta.authorList = Collections.singletonList("Siepert");
        meta.description = "Create Legacy core mod ASM nerd emoji";
        meta.screenshots = new String[0];
        meta.logoFile = "";
    }
}
