package nl.melonstudios.create.block.state;

import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.util.IStringSerializable;

public enum EnumFunnelState implements IStringSerializable {
    INSERTING(0, "inserting"),
    EXTRACTING(1, "extracting");

    public static final PropertyEnum<EnumFunnelState> STATE_PROPERTY = PropertyEnum.create("funnel_state", EnumFunnelState.class);

    private final int id;
    private final String name;
    EnumFunnelState(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return this.id;
    }
    @Override
    public String getName() {
        return this.name;
    }

    public static final EnumFunnelState[] VALUES = {INSERTING, EXTRACTING};
}
