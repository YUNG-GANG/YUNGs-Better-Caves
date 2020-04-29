package com.yungnickyoung.minecraft.bettercaves.config.io;

import com.electronwill.nightconfig.core.AbstractConfig;
import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.Config;
import com.electronwill.nightconfig.core.UnmodifiableCommentedConfig;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.utils.UnmodifiableConfigWrapper;
import com.electronwill.nightconfig.toml.TomlFormat;
import com.yungnickyoung.minecraft.bettercaves.BetterCaves;
import com.yungnickyoung.minecraft.bettercaves.config.util.ConfigHolder;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.config.ConfigFileTypeHandler;

import java.io.File;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

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

        if (configData.valueMap().size() == 0) { // Empty file?
            return new ConfigHolder();
        }

        Map<String, Object> pathMap = configToMap((AbstractConfig)configData.valueMap().get("Better Caves"));

        pathMap.entrySet().forEach(entry -> {
            String key = entry.getKey();
            Object value = entry.getValue();
            BetterCaves.LOGGER.info(key + ":: " + value);
        });

        configData.close();

        // TODO - process pathMap to create ConfigHolder
        // can use config.properties.get(fullName)  -- should prob use some kind of equalsIgnoreCase

        return null;
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
