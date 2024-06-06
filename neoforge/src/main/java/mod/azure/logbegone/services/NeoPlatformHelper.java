package mod.azure.logbegone.services;

import mod.azure.logbegone.platform.services.IPlatformHelper;
import net.neoforged.fml.loading.FMLPaths;

import java.nio.file.Path;

public class NeoPlatformHelper implements IPlatformHelper {
    @Override
    public Path getConfigLocation() {
        return FMLPaths.CONFIGDIR.get();
    }
}
