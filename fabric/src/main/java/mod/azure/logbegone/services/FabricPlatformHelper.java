package mod.azure.logbegone.services;

import mod.azure.logbegone.platform.services.IPlatformHelper;
import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Path;

public class FabricPlatformHelper implements IPlatformHelper {
    @Override
    public Path getConfigLocation() {
        return FabricLoader.getInstance().getConfigDir();
    }
}
