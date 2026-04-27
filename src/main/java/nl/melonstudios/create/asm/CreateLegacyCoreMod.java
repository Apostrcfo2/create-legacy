package nl.melonstudios.create.asm;

import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import zone.rong.mixinbooter.IEarlyMixinLoader;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@IFMLLoadingPlugin.MCVersion("1.12.2")
@IFMLLoadingPlugin.Name("Create Legacy core mod")
@IFMLLoadingPlugin.TransformerExclusions("nl.melonstudios.create.asm")
@IFMLLoadingPlugin.SortingIndex(2137)
public class CreateLegacyCoreMod implements IFMLLoadingPlugin, IEarlyMixinLoader {
    @Override
    public String[] getASMTransformerClass() {
        return new String[0];
    }

    @Override
    public String getModContainerClass() {
        return "nl.melonstudios.create.asm.CreateLegacyCoreModContainer";
    }

    @Nullable
    @Override
    public String getSetupClass() {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data) {

    }

    @Override
    public String getAccessTransformerClass() {
        return null;
    }

    @Override
    public List<String> getMixinConfigs() {
        System.out.println("juxtamixin");
        return Collections.singletonList("mixins.create.json");
    }
}
