package mod.azure.logbegone;

import com.moandjiezana.toml.Toml;
import mod.azure.logbegone.platform.Services;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.CopyOption;
import java.nio.file.Files;

public class CommonMod {
    public static final String MOD_ID = "logbegone";
    public static final org.apache.logging.log4j.Logger LOGGER = LogManager.getLogger(MOD_ID);
    public static final Toml CONFIG = getConfig();
    public static final JavaUtilLog4jFilter FILTER = new JavaUtilLog4jFilter();

    public static boolean shouldFilterMessage(String message) {
        var stringIterator = CONFIG.getList("logbegone.phrases").iterator();
        String phrase;

        var regexIterator = CONFIG.getList("logbegone.regex").iterator();
        String regex;

        if (message != null)
            do {
                if (!stringIterator.hasNext()) {
                    do {
                        if (!regexIterator.hasNext())
                            return false;
                        regex = (String) regexIterator.next();
                    } while (!message.matches(regex));
                    return true;
                }
                phrase = (String) stringIterator.next();
            } while (!message.contains(phrase));
        return true;
    }

    private static Toml getConfig() {
        File config = new File(Services.PLATFORM.getConfigLocation() + "/logbegone.toml");

        if (!config.exists()) {
            try {
                Files.copy(CommonMod.class.getResourceAsStream("/assets/logbegone/config.toml"), config.toPath(),
                        new CopyOption[0]);
            } catch (IOException e) {
                LOGGER.error("An error occurred when creating a new config", e);
            }
        }
        return new Toml().read(config);
    }

    public static final class SystemPrintFilter extends PrintStream {
        public SystemPrintFilter(PrintStream stream) {
            super(stream);
        }

        @Override
        public void println(String x) {
            if (!CommonMod.shouldFilterMessage(x))
                super.println(x);
        }

        @Override
        public void print(String s) {
            if (!CommonMod.shouldFilterMessage(s))
                super.print(s);
        }
    }
}
