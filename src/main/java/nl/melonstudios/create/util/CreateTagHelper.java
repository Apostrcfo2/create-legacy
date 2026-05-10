package nl.melonstudios.create.util;

import com.melonstudios.melonlib.blockdict.BlockDictionary;
import net.minecraft.block.state.IBlockState;

public class CreateTagHelper {
    public static boolean isImmovable(IBlockState state) {
        return BlockDictionary.isBlockTagged(state, "create:immovable");
    }

    public static boolean isWrenchPickup(IBlockState state) {
        return BlockDictionary.isBlockTagged(state, "create:wrenchPickup");
    }
}
