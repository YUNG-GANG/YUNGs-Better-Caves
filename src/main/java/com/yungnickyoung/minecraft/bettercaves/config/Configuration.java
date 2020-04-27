package com.yungnickyoung.minecraft.bettercaves.config;

import net.minecraftforge.common.ForgeConfigSpec;

import java.util.Arrays;
import java.util.List;

/**
 * Configuration options for Better Caves.
 * <p>
 * This class and all of its fields provide default config values.
 * The values here are not actually used directly - they are baked into a ConfigHolder each time a new
 * ConfigHolder is created. Separate ConfigHolders are created for each dimension. This allows any or all
 * config values to be overridden differently for each dimension.
 */
public final class Configuration {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    public static final ConfigUndergroundGen caveSettings;
    public static final ConfigBedrockGen bedrockSettings;
    public static final ConfigDebug debugSettings;
    public static final ForgeConfigSpec.ConfigValue<List<Integer>> whitelistedDimensionIDs;
    public static final ForgeConfigSpec.ConfigValue<Boolean> enableGlobalWhitelist;

    static {
        BUILDER.push("Better Caves");

        caveSettings = new ConfigUndergroundGen(BUILDER);
        bedrockSettings = new ConfigBedrockGen(BUILDER);
        debugSettings = new ConfigDebug(BUILDER);

        whitelistedDimensionIDs = BUILDER
            .comment(" List of ID's of dimensions that will have Better Caves. Ignored if Global Whitelisting is enabled.")
            .worldRestart()
            .define("Whitelisted Dimension IDs", Arrays.asList(0));

        enableGlobalWhitelist = BUILDER
            .comment(
                " Automatically enables Better Caves in every possible dimension.\n" +
                "     If this is enabled, the Whitelisted Dimension IDs option is ignored.\n" +
                " Default: false")
            .worldRestart()
            .define("Enable Global Whitelist", false);

        BUILDER.pop();

        SPEC = BUILDER.build();
    }
}
