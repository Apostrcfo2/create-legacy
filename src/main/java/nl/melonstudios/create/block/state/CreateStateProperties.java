package nl.melonstudios.create.block.state;

import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyEnum;

public class CreateStateProperties {
    public static final PropertyEnum<EnumOrestoneVariant> ORESTONE_VARIANT = PropertyEnum.create("variant", EnumOrestoneVariant.class);
    public static final PropertyEnum<EnumSawRotation> SAW_ROTATION = PropertyEnum.create("facing", EnumSawRotation.class);
    public static final PropertyEnum<EnumDirection> DIRECTION = PropertyEnum.create("direction", EnumDirection.class);

    public static final PropertyBool ASSEMBLED = PropertyBool.create("assembled");
    public static final PropertyBool ROTATED = PropertyBool.create("rotated");
}
