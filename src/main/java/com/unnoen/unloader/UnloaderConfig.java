package com.unnoen.unloader;

import java.io.File;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

public class UnloaderConfig {

    private static class Defaults {
        public static final Integer unloadInterval = 600;
        public static final String[] blacklistDims = {"0", "overworld"};
    }

    private static class Categories {
        public static final String general = "general";
    }

    public static Integer unloadInterval = Defaults.unloadInterval;
    public static String[] blacklistDims = Defaults.blacklistDims;

    public static void syncronizeConfiguration(File configFile) {
        Configuration configuration = new Configuration(configFile);
        configuration.load();

        Property unloadIntervalProperty = configuration.get(
                Categories.general,
                "unloadInterval",
                Defaults.unloadInterval,
                "Time (in ticks) to wait before checking dimensions");
        unloadInterval = unloadIntervalProperty.getInt();

        Property blacklistDimsProperty = configuration.get(
                Categories.general,
                "blacklistDims",
                Defaults.blacklistDims,
                "List of dimensions you don’t want to unload.\nCan be dimension name or ID. Uses regular expressions.");
        blacklistDims = blacklistDimsProperty.getStringList();

        if (configuration.hasChanged()) {
            configuration.save();
        }
    }
}
