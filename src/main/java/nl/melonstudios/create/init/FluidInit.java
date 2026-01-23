package nl.melonstudios.create.init;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

public final class FluidInit {
    private static Fluid milk = new Fluid("milk",
            new ResourceLocation("create:fluid/milk_still"),
            new ResourceLocation("create:fluid/milk_flowing"))
            .setUnlocalizedName("create.milk");
    private static Fluid chocolate = new Fluid("chocolate",
            new ResourceLocation("create:fluid/chocolate_still"),
            new ResourceLocation("create:fluid/chocolate_flowing"))
            .setUnlocalizedName("create.chocolate");
    private static Fluid tea = new Fluid("builders_tea",
            new ResourceLocation("create:fluid/tea_still"),
            new ResourceLocation("create:fluid/tea_flowing"))
            .setUnlocalizedName("create.builders_tea");

    public static void init() {
        FluidRegistry.enableUniversalBucket();
    }
    public static void register() {
        if (!FluidRegistry.registerFluid(milk))
            milk = FluidRegistry.getFluid("milk");
        if (!FluidRegistry.registerFluid(chocolate))
            chocolate = FluidRegistry.getFluid("chocolate");
        FluidRegistry.addBucketForFluid(chocolate);
        if (!FluidRegistry.registerFluid(tea))
            tea = FluidRegistry.getFluid("builders_tea");
        FluidRegistry.addBucketForFluid(tea);
    }

    public static Fluid milk() {
        return milk;
    }
    public static Fluid chocolate() {
        return chocolate;
    }
    public static Fluid tea() {
        return tea;
    }
}
