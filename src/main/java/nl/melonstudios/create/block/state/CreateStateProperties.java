package nl.melonstudios.create.block.state;

import net.minecraft.block.properties.PropertyEnum;

public class CreateStateProperties {
    public static final PropertyEnum<EnumOrestoneVariant> ORESTONE_VARIANT = PropertyEnum.create("variant", EnumOrestoneVariant.class);
    public static final PropertyEnum<EnumBeltSlope> BELT_SLOPE = PropertyEnum.create("belt_slope", EnumBeltSlope.class);
    public static final PropertyEnum<EnumBeltPart> BELT_PART = PropertyEnum.create("belt_part", EnumBeltPart.class);
    public static final PropertyEnum<EnumSawRotation> SAW_ROTATION = PropertyEnum.create("facing", EnumSawRotation.class);
}
