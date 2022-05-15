package mod.azure.logbegone;

import java.io.File;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import com.google.common.collect.Lists;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;

public class LogBegoneConfig {
	public static class Common {
		public final ConfigValue<List<? extends String>> phrases;
		public final ConfigValue<List<? extends String>> regex;

		public Common(ForgeConfigSpec.Builder builder) {
			builder.push("logbegone");
			this.phrases = builder.defineList("phrases", Lists.newArrayList("the_aether"), o -> true);
			this.regex = builder.defineList("regex", Lists.newArrayList(""), o -> true);
			builder.pop();
		}
	}

	public static final Common COMMON;
	public static final ForgeConfigSpec COMMON_SPEC;

	static {
		Pair<Common, ForgeConfigSpec> commonSpecPair = new ForgeConfigSpec.Builder().configure(Common::new);
		COMMON = commonSpecPair.getLeft();
		COMMON_SPEC = commonSpecPair.getRight();
	}

	public static void loadConfig(ForgeConfigSpec config, String path) {
		final CommentedFileConfig file = CommentedFileConfig.builder(new File(path)).sync().autosave()
				.writingMode(WritingMode.REPLACE).build();
		file.load();
		config.setConfig(file);
	}

}
