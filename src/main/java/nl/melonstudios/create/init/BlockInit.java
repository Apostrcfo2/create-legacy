package nl.melonstudios.create.init;

import com.melonstudios.melonlib.item.ItemBlockVariants;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.registries.GameData;
import nl.melonstudios.create.CreateLegacy;
import nl.melonstudios.create.block.*;
import nl.melonstudios.create.block.actor.*;
import nl.melonstudios.create.block.deco.BlockOrestone;
import nl.melonstudios.create.block.deco.BlockOrestonePillar;
import nl.melonstudios.create.block.generator.BlockBearingWindmill;
import nl.melonstudios.create.block.generator.BlockHandCrank;
import nl.melonstudios.create.block.generator.BlockSail;
import nl.melonstudios.create.block.generator.BlockWaterWheel;
import nl.melonstudios.create.item.ItemBlockSail;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Objects;

public final class BlockInit {
    public static final ArrayList<Block> BLOCKS = new ArrayList<>();

    public static final BlockRender RENDER = registerBlock(new BlockRender());

    public static final BlockOre ORE = registerBlockWithItem(new BlockOre(), true);
    public static final BlockMetal METAL = registerBlockWithItem(BlockMetal.get(), true);

    //region Kinetics
    public static final BlockCasing CASING = registerBlockWithItem(new BlockCasing(), true);

    public static final BlockShaft SHAFT = registerBlockWithItem(new BlockShaft(Material.ROCK, MapColor.STONE));
    public static final BlockCogwheel COG_SMALL = registerBlockWithItem(new BlockCogwheel(MapColor.WOOD, SoundType.WOOD, false));
    public static final BlockCogwheel COG_LARGE = registerBlockWithItem(new BlockCogwheel(MapColor.WOOD, SoundType.WOOD, true));

    public static final BlockGearbox GEARBOX = registerBlockWithItem(new BlockGearbox(MapColor.WOOD, SoundType.WOOD), true);
    public static final BlockGearshift GEARSHIFT = (BlockGearshift)
            registerBlockWithItem(new BlockGearshift(MapColor.WOOD, SoundType.WOOD)
            .setRegistryName("gearshift").setUnlocalizedName("create.gearshift"));
    public static final BlockClutch CLUTCH = (BlockClutch)
            registerBlockWithItem(new BlockClutch(MapColor.WOOD, SoundType.WOOD)
            .setRegistryName("clutch").setUnlocalizedName("create.clutch"));

    public static final BlockHandCrank HAND_CRANK = registerBlockWithItem(new BlockHandCrank(MapColor.WOOD, SoundType.WOOD));
    public static final BlockWaterWheel WATER_WHEEL = registerBlockWithItem(new BlockWaterWheel(MapColor.WOOD, SoundType.WOOD));

    public static final BlockTurntable TURNTABLE = (BlockTurntable)
            registerBlockWithItem(new BlockTurntable(MapColor.WOOD, SoundType.WOOD)
            .setRegistryName("turntable").setUnlocalizedName("create.turntable"));
    public static final BlockBearing BEARING = (BlockBearing)
            registerBlockWithItem(new BlockBearing()
            .setRegistryName("bearing").setUnlocalizedName("create.bearing"));
    public static final BlockBearingWindmill BEARING_WINDMILL = (BlockBearingWindmill)
            registerBlockWithItem(new BlockBearingWindmill()
                    .setRegistryName("bearing_windmill").setUnlocalizedName("create.bearing_windmill"));

    public static final BlockChassisLinear CHASSIS_LINEAR = (BlockChassisLinear)
            registerBlockWithItem(new BlockChassisLinear()
            .setRegistryName("chassis_linear").setUnlocalizedName("create.chassis_linear"), true);
    public static final BlockChassisRadial CHASSIS_RADIAL = (BlockChassisRadial)
            registerBlockWithItem(new BlockChassisRadial()
            .setRegistryName("chassis_radial").setUnlocalizedName("create.chassis_radial"));

    public static final BlockSail SAIL_DOWN = registerBlock(new BlockSail(EnumFacing.DOWN));
    public static final BlockSail SAIL_UP = registerBlock(new BlockSail(EnumFacing.UP));
    public static final BlockSail SAIL_NORTH = registerBlock(new BlockSail(EnumFacing.NORTH));
    public static final BlockSail SAIL_SOUTH = registerBlock(new BlockSail(EnumFacing.SOUTH));
    public static final BlockSail SAIL_WEST = registerBlock(new BlockSail(EnumFacing.WEST));
    public static final BlockSail SAIL_EAST = registerBlock(new BlockSail(EnumFacing.EAST));
    //there is probably a better way to do this... oh well I'm not rewriting it all again
    public static final ItemBlockSail SAIL_ITEM = new ItemBlockSail(SAIL_DOWN);
    static {
        ItemInit.ITEMS.add(SAIL_ITEM);
        GameData.getBlockItemMap().forcePut(SAIL_DOWN, SAIL_ITEM);
        GameData.getBlockItemMap().forcePut(SAIL_UP, SAIL_ITEM);
        GameData.getBlockItemMap().forcePut(SAIL_NORTH, SAIL_ITEM);
        GameData.getBlockItemMap().forcePut(SAIL_SOUTH, SAIL_ITEM);
        GameData.getBlockItemMap().forcePut(SAIL_WEST, SAIL_ITEM);
        GameData.getBlockItemMap().forcePut(SAIL_EAST, SAIL_ITEM);
    }

    public static final BlockGauge SPEEDOMETER = (BlockGauge)
            registerBlockWithItem(new BlockGauge(MapColor.WOOD, SoundType.WOOD, BlockGauge.Type.SPEED)
            .setRegistryName("speedometer").setUnlocalizedName("create.speedometer"));
    public static final BlockGauge STRESSOMETER = (BlockGauge)
            registerBlockWithItem(new BlockGauge(MapColor.WOOD, SoundType.WOOD, BlockGauge.Type.STRESS)
            .setRegistryName("stressometer").setUnlocalizedName("create.stressometer"));

    public static final BlockPress PRESS = (BlockPress)
            registerBlockWithItem(new BlockPress(MapColor.WOOD, SoundType.METAL)
            .setRegistryName("press").setUnlocalizedName("create.press"));

    public static final BlockDrill DRILL = (BlockDrill)
            registerBlockWithItem(new BlockDrill(MapColor.STONE, SoundType.METAL)
            .setRegistryName("drill").setUnlocalizedName("create.drill"));
    public static final BlockSaw SAW = (BlockSaw)
            registerBlockWithItem(new BlockSaw(MapColor.IRON, SoundType.METAL)
            .setRegistryName("saw").setUnlocalizedName("create.saw"));
    public static final BlockDeployer DEPLOYER = (BlockDeployer)
            registerBlockWithItem(new BlockDeployer(MapColor.STONE, SoundType.WOOD)
            .setRegistryName("deployer").setUnlocalizedName("create.deployer"));

    public static final BlockAutoFarm AUTO_FARM = (BlockAutoFarm)
            registerBlockWithItem(new BlockAutoFarm()
            .setRegistryName("auto_farm").setUnlocalizedName("create.auto_farm"), true);
    public static final BlockContraptionInterface CONTRAPTION_INTERFACE = (BlockContraptionInterface)
            registerBlockWithItem(new BlockContraptionInterface()
            .setRegistryName("contraption_interface").setUnlocalizedName("create.contraption_interface"), true);

    public static final BlockMillstone MILLSTONE = (BlockMillstone)
            registerBlockWithItem(new BlockMillstone(Material.ROCK, MapColor.STONE)
            .setRegistryName("millstone").setUnlocalizedName("create.millstone"));

    public static final BlockDepot DEPOT = (BlockDepot)
            registerBlockWithItem(new BlockDepot()
            .setRegistryName("depot").setUnlocalizedName("create.depot"));
    public static final BlockChute CHUTE = (BlockChute)
            registerBlockWithItem(new BlockChute()
            .setRegistryName("chute").setUnlocalizedName("create.chute"));
    //endregion

    //region Decorations
    public static final BlockOrestone ORESTONE = registerBlockWithItem(new BlockOrestone("natural"), true);
    public static final BlockOrestone ORESTONE_CUT = registerBlockWithItem(new BlockOrestone("cut"), true);
    public static final BlockOrestone ORESTONE_POLISHED = registerBlockWithItem(new BlockOrestone("polished"), true);
    public static final BlockOrestone ORESTONE_BRICKS = registerBlockWithItem(new BlockOrestone("bricks"), true);
    public static final BlockOrestone ORESTONE_BRICKS_FANCY = registerBlockWithItem(new BlockOrestone("bricks_fancy"), true);
    public static final BlockOrestone ORESTONE_LAYERED = registerBlockWithItem(new BlockOrestone("layered"), true);
    public static final BlockOrestonePillar ORESTONE_PILLAR_X = registerBlock(new BlockOrestonePillar(EnumFacing.Axis.X));
    public static final BlockOrestonePillar ORESTONE_PILLAR_Y = registerBlockWithItem(new BlockOrestonePillar(EnumFacing.Axis.Y), true);
    public static final BlockOrestonePillar ORESTONE_PILLAR_Z = registerBlock(new BlockOrestonePillar(EnumFacing.Axis.Z));
    //endregion

    private static <T extends Block> T registerBlock(T block) {
        BLOCKS.add(block);
        return block;
    }
    private static <T extends Block> T registerBlockWithItem(@Nonnull T block, @Nonnull Item item) {
        BLOCKS.add(block);
        ItemInit.ITEMS.add(item);
        return block;
    }
    private static <T extends Block> T registerBlockWithItem(@Nonnull T block, boolean variants) {
        Objects.requireNonNull(block.getRegistryName(), "Block has no registry name!!");
        if (variants) return registerBlockWithItem(block, new ItemBlockVariants(block).setRegistryName(block.getRegistryName()));
        return registerBlockWithItem(block, new ItemBlock(block).setRegistryName(block.getRegistryName()));
    }
    private static <T extends Block> T registerBlockWithItem(@Nonnull T block) {
        return registerBlockWithItem(block, false);
    }

    public static void registerTileEntities() {
        CreateLegacy.proxy.registerTileEntities();
    }
}
