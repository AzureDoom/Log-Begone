package mod.azure.logbegone;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.LoggerConfig;

import com.moandjiezana.toml.Toml;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;

public class LogBegoneMod implements PreLaunchEntrypoint {

	public static final org.apache.logging.log4j.Logger LOGGER = LogManager.getLogger("logbegone");
	public static final Toml CONFIG = getConfig();
	public static final JavaUtilLog4jFilter FILTER = new JavaUtilLog4jFilter();

	@Override
	public void onPreLaunch() {
		System.setOut(new SystemPrintFilter(System.out));
		java.util.logging.Logger.getLogger("").setFilter(FILTER);
		((org.apache.logging.log4j.core.Logger) LogManager.getRootLogger()).addFilter(FILTER);
		ArrayList<LoggerConfig> foundOffshootLog4jLoggers = new ArrayList<>();
		LoggerContext logContext = (LoggerContext) LogManager.getContext(false);
		Map<String, LoggerConfig> map = logContext.getConfiguration().getLoggers();

		for (LoggerConfig logger : map.values()) {
			if (!foundOffshootLog4jLoggers.contains(logger)) {
				logger.addFilter(FILTER);
				foundOffshootLog4jLoggers.add(logger);
			}
		}
	}

	public static boolean shouldFilterMessage(String message) {
		Iterator stringIterator = CONFIG.getList("logbegone.phrases").iterator();
		String phrase;

		Iterator regexIterator = CONFIG.getList("logbegone.regex").iterator();
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
		File config = new File(FabricLoader.getInstance().getConfigDir() + "/logbegone.toml");

		if (!config.exists()) {
			try {
				Files.copy(LogBegoneMod.class.getResourceAsStream("/assets/logbegone/config.toml"), config.toPath(),
						new CopyOption[0]);
			} catch (IOException e) {
				LOGGER.error("An error occurred when creating a new config", e);
			}
		}
		return new Toml().read(config);
	}

	public final class SystemPrintFilter extends PrintStream {
		public SystemPrintFilter(PrintStream stream) {
			super(stream);
		}

		@Override
		public void println(String x) {
			if (!LogBegoneMod.shouldFilterMessage(x))
				super.println(x);
		}

		@Override
		public void print(String s) {
			if (!LogBegoneMod.shouldFilterMessage(s))
				super.print(s);
		}
	}

}
