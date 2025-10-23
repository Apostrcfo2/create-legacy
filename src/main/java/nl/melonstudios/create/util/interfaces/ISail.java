package nl.melonstudios.create.util.interfaces;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;

public interface ISail {
    EnumFacing getFacing(IBlockState state);
}
