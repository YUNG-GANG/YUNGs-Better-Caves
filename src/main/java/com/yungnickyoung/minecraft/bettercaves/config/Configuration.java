package com.yungnickyoung.minecraft.bettercaves.config;

import com.google.common.collect.Lists;
import com.yungnickyoung.minecraft.bettercaves.BetterCaves;
import me.sargunvohra.mcmods.autoconfig1u.ConfigData;
import me.sargunvohra.mcmods.autoconfig1u.annotation.Config;
//import me.sargunvohra.mcmods.autoconfig1u.annotation.ConfigEntry;

import java.util.List;

/**
 * Configuration options for Better Caves.
 * <p>
 * This class and all of its fields provide default config values.
 * The values here are not actually used directly - they are baked into a ConfigHolder each time a new
 * ConfigHolder is created. Separate ConfigHolders are created for each dimension. This allows any or all
 * config values to be overridden differently for each dimension.
 */
@Config(name = BCSettings.BASE_CONFIG_NAME)
public class Configuration implements ConfigData {
//    @ConfigEntry.Category("Better Caves")
//    @ConfigEntry.Gui.TransitiveObject
    public ConfigBetterCaves betterCaves = new ConfigBetterCaves();

    /**
     * Validate whitelisted dimensions on config load.
     */
    @Override
    public void validatePostLoad() throws ValidationException {
        String rawStringofList = betterCaves.whitelistedDimensions;
        int strLen = rawStringofList.length();

        // Validate the string's format
        if (strLen < 2 || rawStringofList.charAt(0) != '[' || rawStringofList.charAt(strLen - 1) != ']') {
            BetterCaves.LOGGER.error("INVALID VALUE FOR SETTING 'Whitelisted Dimension IDs'. Using empty list instead...");
            BetterCaves.whitelistedDimensions = Lists.newArrayList();
            return;
        }

        // Parse string to list
        List<String> inputListOfDimensionStrings = Lists.newArrayList(rawStringofList.substring(1, strLen - 1).split(",\\s*"));

        // Parse list of strings, removing any entries that don't match existing dimension names
        List<String> whitelistedDimensions = Lists.newArrayList();
        whitelistedDimensions.addAll(inputListOfDimensionStrings);

        BetterCaves.whitelistedDimensions = whitelistedDimensions;
    }
}
