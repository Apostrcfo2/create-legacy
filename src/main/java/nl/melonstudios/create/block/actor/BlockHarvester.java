package nl.melonstudios.create.block.actor;

import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import nl.melonstudios.create.init.ItemInit;
import nl.melonstudios.create.util.BlockProperties;

public class BlockHarvester extends BlockHorizontal {
    public BlockHarvester() {
        super(Material.ROCK, MapColor.IRON);
        this.setSoundType(SoundType.WOOD);

        this.setHardness(BlockProperties.WOOD_HARDNESS);
        this.setHardness(BlockProperties.WOOD_RESISTANCE);

        this.setHarvestLevel("pickaxe", -1);

        this.setCreativeTab(ItemInit.TAB_CREATE);
    }

    @Override
    public boolean isToolEffective(String type, IBlockState state) {
        return "pickaxe".equals(type) || "axe".equals(type);
    }
}
