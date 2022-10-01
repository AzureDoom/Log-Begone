package mod.azure.logbegone;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.LoggerConfig;

import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.loading.FMLPaths;

@Mod("logbegone")
public class LogBegoneMod {

	public static LogBegoneMod instance;
	public static final org.apache.logging.log4j.Logger LOGGER = LogManager.getLogger("logbegone");
	public static final JavaUtilLog4jFilter FILTER = new JavaUtilLog4jFilter();

	public LogBegoneMod() {
		instance = this;
		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, LogBegoneConfig.COMMON_SPEC, "logbegone.toml");
		LogBegoneConfig.loadConfig(LogBegoneConfig.COMMON_SPEC,
				FMLPaths.CONFIGDIR.get().resolve("logbegone.toml").toString());
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
		List<? extends String> phraseFilter = LogBegoneConfig.COMMON.phrases.get();
		Iterator<? extends String> regexFilter = phraseFilter.iterator();

		String phrase;
		if (message != null)
			do {
				if (!regexFilter.hasNext()) {
					List<? extends String> regexFilter1 = LogBegoneConfig.COMMON.regex.get();
					Iterator<? extends String> phrase1 = regexFilter1.iterator();
					String regex;
					do {
						if (!phrase1.hasNext()) {
							return false;
						}
						regex = (String) phrase1.next();
					} while (!message.matches(regex));
					return true;
				}
				phrase = (String) regexFilter.next();
			} while (!message.contains(phrase));

		return true;
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
