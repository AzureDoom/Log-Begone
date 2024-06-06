package mod.azure.logbegone;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.LoggerConfig;

import java.util.ArrayList;
import java.util.Map;

public final class FabricLibMod implements PreLaunchEntrypoint {

    @Override
    public void onPreLaunch() {
        System.setOut(new CommonMod.SystemPrintFilter(System.out));
        java.util.logging.Logger.getLogger("").setFilter(CommonMod.FILTER);
        ((org.apache.logging.log4j.core.Logger) LogManager.getRootLogger()).addFilter(CommonMod.FILTER);
        ArrayList<LoggerConfig> foundOffshootLog4jLoggers = new ArrayList<>();
        LoggerContext logContext = (LoggerContext) LogManager.getContext(false);
        Map<String, LoggerConfig> map = logContext.getConfiguration().getLoggers();

        for (LoggerConfig logger : map.values()) {
            if (!foundOffshootLog4jLoggers.contains(logger)) {
                logger.addFilter(CommonMod.FILTER);
                foundOffshootLog4jLoggers.add(logger);
            }
        }
    }
}
