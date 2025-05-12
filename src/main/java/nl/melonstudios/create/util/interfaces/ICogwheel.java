package nl.melonstudios.create.util.interfaces;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;

public interface ICogwheel extends IRotate {
    static boolean isSmallCog(IBlockState state) {
        return isSmallCog(state.getBlock());
    }
    static boolean isLargeCog(IBlockState state) {
        return isLargeCog(state.getBlock());
    }

    static boolean isSmallCog(Block block) {
        return block instanceof ICogwheel && ((ICogwheel)block).isSmallCog();
    }
    static boolean isLargeCog(Block block) {
        return block instanceof ICogwheel && ((ICogwheel)block).isLargeCog();
    }

    static boolean isDedicatedCogwheel(Block block) {
        return block instanceof ICogwheel && ((ICogwheel)block).isDedicatedCogwheel();
    }

    default boolean isLargeCog() {
        return false;
    }
    default boolean isSmallCog() {
        return !this.isLargeCog();
    }
    default boolean isDedicatedCogwheel() {
        return false;
    }
}
