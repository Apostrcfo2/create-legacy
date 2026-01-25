package nl.melonstudios.create.tileentity.actor;

import net.minecraft.init.SoundEvents;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import nl.melonstudios.create.CreateLegacy;
import nl.melonstudios.create.recipe.MixingRecipe;
import nl.melonstudios.create.recipe.MixingRecipes;
import nl.melonstudios.create.tileentity.TileEntityBasin;
import nl.melonstudios.create.tileentity.TileEntityKinetic;
import nl.melonstudios.create.tileentity.marker.ISpeedRequirement;
import nl.melonstudios.create.util.Utils;

import javax.annotation.Nullable;

public class TileEntityMixer extends TileEntityKinetic implements ISpeedRequirement {
    public TileEntityMixer() {
        super();

        this.setTickRateLazy(5);
    }

    public final BlockPos.MutableBlockPos basinPos = new BlockPos.MutableBlockPos();
    public TileEntityBasin cachedBasin = null;
    public int lowering = 0;
    public int progress = 0;
    public String currentRecipe = null;

    @Override
    public void tick() {
        super.tick();

        TileEntityBasin basin = this.getBasin();
        if (basin == null) this.currentRecipe = null;
        if (this.currentRecipe != null) {
            MixingRecipe recipe = MixingRecipes.instance.getRecipe(this.currentRecipe);
            if (!recipe.matches(basin)) this.currentRecipe = null;
        }
        MixingRecipe recipe = this.getRecipe();
        boolean process = recipe != null && recipe.checkOutputSpace(basin);
        if (process) {
            if (this.lowering < 20) this.lowering++;
        } else {
            this.progress = 0;
            if (this.lowering > 0) this.lowering--;
        }

        if (this.lowering >= 20 && recipe != null && basin != null) {
            this.recipeFX(basin, this.basinPos.getX() + 0.5, this.basinPos.getY() + 0.5, this.basinPos.getZ() + 0.5);
            if ((this.progress += (int) this.getSpeed()) >= recipe.recipeTime) {
                this.progress = 0;
                basin.dumpRecipeResults(recipe);
            }
        }
    }

    @Override
    public void tickLazy() {
        if (this.currentRecipe == null && this.getSpeed() >= this.minimumSpeed() && this.getBasin() != null) {
            this.searchRecipe(this.getBasin());
        }
    }
    private void searchRecipe(TileEntityBasin basin) {
        MixingRecipe recipe = MixingRecipes.instance.getRecipeForInput(basin);
        this.currentRecipe = recipe != null ? recipe.recipeID : null;
    }

    private void recipeFX(TileEntityBasin basin, double x, double y, double z) {
        if (this.world.isRemote) {
            if ((this.world.getTotalWorldTime() & 1) == 0) {
                CreateLegacy.proxy.mixerFX(basin, x, y, z);
                if (basin.hasAnyFluid()) {
                    this.world.playSound(null, x, y, z, SoundEvents.ENTITY_BOAT_PADDLE_WATER, SoundCategory.BLOCKS, 0.125F, 0.5F);
                }
                this.world.playSound(null, x, y, z, SoundEvents.BLOCK_STONE_BREAK, SoundCategory.BLOCKS, 0.125F, 0.5F);
            }
        }
    }

    @Nullable
    public TileEntityBasin getBasin() {
        this.basinPos.setPos(this.pos).move(EnumFacing.DOWN, 2);
        if (this.cachedBasin == null || this.cachedBasin.isInvalid() || !this.cachedBasin.getPos().equals(this.basinPos)) {
            this.cachedBasin = Utils.cast(this.world.getTileEntity(this.basinPos), TileEntityBasin.class);
        }
        return this.cachedBasin;
    }
    @Nullable
    public MixingRecipe getRecipe() {
        return MixingRecipes.instance.getRecipe(this.currentRecipe);
    }

    @Override
    public float minimumSpeed() {
        return 32.0F;
    }
}
