package mod.azure.logbegone;

import java.util.logging.Filter;
import java.util.logging.LogRecord;

import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.filter.AbstractFilter;
import org.jetbrains.annotations.NotNull;

public final class JavaUtilLog4jFilter extends AbstractFilter implements Filter {
	public boolean isLoggable(@NotNull LogRecord record) {
		return !LogBegoneMod.shouldFilterMessage(record.getMessage());
	}

	public Result filter(@NotNull LogEvent event) {
		return LogBegoneMod.shouldFilterMessage(
				"[" + event.getLoggerName() + "]: " + event.getMessage().getFormattedMessage()) ? Result.DENY
						: Result.NEUTRAL;
	}
}
