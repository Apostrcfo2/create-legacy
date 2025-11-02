package nl.melonstudios.create.block.state;

import net.minecraft.util.IStringSerializable;

public enum EnumChuteVariant implements IStringSerializable {
    NORMAL("normal", 0),
    WINDOW("window", 1),
    FAT("fat", 2);

    EnumChuteVariant(String name, int id) {
        this.name = name;
        this.id = id;
    }

    private final String name;
    private final int id;
    @Override
    public String getName() {
        return this.name;
    }
    public int getId() {
        return this.id;
    }

    public static final EnumChuteVariant[] VALUES = {
            NORMAL, WINDOW, FAT
    };
}
