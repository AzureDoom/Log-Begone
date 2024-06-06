package mod.azure.logbegone;

import com.google.gson.*;
import mod.azure.logbegone.platform.Services;
import org.apache.logging.log4j.LogManager;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class CommonMod {
    public static final String MOD_ID = "logbegone";
    public static final org.apache.logging.log4j.Logger LOGGER = LogManager.getLogger(MOD_ID);
    public static final JsonObject CONFIG = getConfig();
    public static final JavaUtilLog4jFilter FILTER = new JavaUtilLog4jFilter();

    public static boolean shouldFilterMessage(String message) {
        JsonArray phrases = CONFIG.has("logbegone") ? CONFIG.getAsJsonObject("logbegone").getAsJsonArray("phrases") : null;
        JsonArray regexes = CONFIG.has("logbegone") ? CONFIG.getAsJsonObject("logbegone").getAsJsonArray("regex") : null;

        if (message != null) {
            if (phrases != null) {
                for (var phraseElement : phrases) {
                    String phrase = phraseElement.getAsString();
                    if (message.contains(phrase)) {
                        return true;
                    }
                }
            }

            if (regexes != null) {
                for (var regexElement : regexes) {
                    String regex = regexElement.getAsString();
                    if (message.matches(regex)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    private static JsonObject getConfig() {
        File config = new File(Services.PLATFORM.getConfigLocation() + "/logbegone.json");

        if (!config.exists()) {
            try {
                Files.copy(CommonMod.class.getResourceAsStream("/assets/logbegone/config.json"), config.toPath(),
                        StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                LOGGER.error("An error occurred when creating a new config", e);
            }
        }

        try (FileReader reader = new FileReader(config)) {
            return JsonParser.parseReader(reader).getAsJsonObject();
        } catch (IOException | JsonSyntaxException e) {
            LOGGER.error("An error occurred when reading the config file", e);
            return new JsonObject();  // Return an empty JSON object in case of error
        }
    }

    private static void saveConfig(JsonObject config) {
        File configFile = new File(Services.PLATFORM.getConfigLocation() + "/logbegone.json");

        try (FileWriter writer = new FileWriter(configFile)) {
            new Gson().toJson(config, writer);
        } catch (IOException e) {
            LOGGER.error("An error occurred when saving the config file", e);
        }
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
