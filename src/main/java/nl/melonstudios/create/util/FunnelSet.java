package nl.melonstudios.create.util;

import nl.melonstudios.create.block.funnel.BlockFunnelWall;
import nl.melonstudios.create.item.ItemBlockFunnel;

public interface FunnelSet {
    BlockFunnelWall getWall();
    ItemBlockFunnel getItem();
}
