package com.yungnickyoung.minecraft.bettercaves.config.io;

import com.electronwill.nightconfig.core.AbstractConfig;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.yungnickyoung.minecraft.bettercaves.BetterCaves;
import com.yungnickyoung.minecraft.bettercaves.config.util.ConfigHolder;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class ConfigLoader {
    /**
     * Loads a config from file for a given dimension.
     * The file must be located at {@code <configdirectory>/bettercaves-1_12_2/DIM<id>_config.cfg}
     * @param dimensionID Unique dimension ID
     * @return ConfigHolder loaded from file for given dimension
     */
    public static ConfigHolder loadConfigFromFileForDimension(int dimensionID) {
        String fileName = "DIM" + dimensionID + "_config.toml";
        File configFile = new File(BetterCaves.customConfigDir, fileName);

        if (!configFile.exists() || configFile.isDirectory()) {
            BetterCaves.LOGGER.info(String.format("Better Caves config file for dimension %d not found. Using global config...", dimensionID));
            return new ConfigHolder();
        }

        if (!configFile.canRead()) {
            BetterCaves.LOGGER.warn(String.format("Better Caves config file for dimension %d not readable. Using global config...", dimensionID));
            return new ConfigHolder();
        }

        BetterCaves.LOGGER.info(String.format("Reading Better Caves config from file for dimension %d...", dimensionID));
        return parseConfigFromFile(configFile);
    }

    /**j
     * Reads a config file and returns a ConfigHolder with those options.
     * The file's format is expected to be compliant with Forge's config file structure.
     * @param file Config File
     * @return ConfigHolder populated with data in file. Any config options not specified in the config file will
     *         use the respective value in the global Better Caves config
     */
    private static ConfigHolder parseConfigFromFile(File file) {
        CommentedFileConfig configData = CommentedFileConfig.builder(file).sync().preserveInsertionOrder().build();
        try {
            configData.load();
        } catch (Exception e) {
            BetterCaves.LOGGER.error(String.format("ERROR - Failed to parse Better Caves config for file %s", file.getName()));
            BetterCaves.LOGGER.error(String.format("RECEIVED ERROR %s", e));
            BetterCaves.LOGGER.info("USING GLOBAL CONFIG FILE INSTEAD...");
            return new ConfigHolder();
        }

        // Check empty file
        if (configData.valueMap().size() == 0) {
            return new ConfigHolder();
        }

        // Check that config has only one topmost level, called "Better Caves"
        if (configData.valueMap().size() != 1 || configData.valueMap().get("Better Caves") == null) {
            BetterCaves.LOGGER.error(String.format("ERROR - Invalid Better Caves config file %s", file.getName()));
            BetterCaves.LOGGER.error("Is there only one topmost category level, called \"Better Caves\"?");
            BetterCaves.LOGGER.info("USING GLOBAL CONFIG FILE INSTEAD...");
            return new ConfigHolder();
        }

        // Populate path map with the config file's contents
        Map<String, Object> pathMap = configToMap((AbstractConfig)configData.valueMap().get("Better Caves"));

        // Clean up I/O
        configData.close();

        // Populate ConfigHolder with config file entries
        ConfigHolder config = new ConfigHolder();
        for (Map.Entry<String, Object> entry : pathMap.entrySet()) {
            String fullName = entry.getKey();
            Object value = entry.getValue();

            ConfigHolder.ConfigOption configOption = config.properties.get(fullName);

            // Verify that fullName (category path + property name) is correct
            if (configOption == null) {
                BetterCaves.LOGGER.error(String.format("ERROR: INVALID PROPERTY %s in config %s. Skipping...", fullName, file.getName()));
                continue;
            }

            Class<?> type = configOption.getType();

            if ((type == Double.TYPE || type == Double.class) && value.getClass() == Integer.class) {
                configOption.set(((Integer) value).doubleValue());
                BetterCaves.LOGGER.debug(String.format("%s: overriding config option: %s", file.getName(), fullName));
            } else if (type != value.getClass()) {
                BetterCaves.LOGGER.error(String.format("ERROR: WRONG TYPE for %s in config %s. Skipping...", fullName, file.getName()));
            } else {
                configOption.set(value);
                BetterCaves.LOGGER.debug(String.format("%s: overriding config option: %s", file.getName(), fullName));
            }
        }

        return config;
    }

    /**
     * Produces a mapping of full paths to values.
     */
    private static Map<String, Object> configToMap(AbstractConfig config) {
        Map<String, Object> pathMap = new HashMap<>();
        fillPathMap(config, pathMap, "Better Caves");
        return pathMap;
    }

    /**
     * Helper method for configToMap.
     * Iterates a Config's map, recursing each entry (depth-first) until the value is not a Config.
     * The value is then added to the provided pathMap.
     */
    private static void fillPathMap(AbstractConfig config, Map<String, Object> pathMap, String currPath) {
        if (config == null) {
            return;
        }

        Map<String, Object> configMap = ObfuscationReflectionHelper.getPrivateValue(AbstractConfig.class, config, "map");

        if (configMap.size() == 0) {
            return;
        }

        for (Map.Entry<String, Object> entry : configMap.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            String path = currPath + "." + key;

            if (value instanceof AbstractConfig) {
                fillPathMap((AbstractConfig) value, pathMap, path);
            }
            else {
                pathMap.put(path, value);
            }
        }
    }
}
