package com.yungnickyoung.minecraft.bettercaves.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class ConfigLiquidRegionsForge {
    public final ForgeConfigSpec.ConfigValue<Double> liquidRegionSize;
    public final ForgeConfigSpec.ConfigValue<Double> waterRegionSpawnChance;
    public final ForgeConfigSpec.ConfigValue<Integer> liquidAltitude;

    public ConfigLiquidRegionsForge(final ForgeConfigSpec.Builder BUILDER) {
        BUILDER
                .comment(
                        """
                                ##########################################################################################################
                                # Liquid Region settings.
                                ##########################################################################################################""")
                .push("General");

        liquidRegionSize = BUILDER
                .comment(
                        """
                                Controls the overall size of liquid regions.
                                Smaller number = larger regions. Cannot be less than or equal to 0.
                                This number is highly sensitive to change.
                                Default: 0.001""".indent(1))
                .worldRestart()
                .define("Liquid Region Size", 0.001);

        waterRegionSpawnChance = BUILDER
                .comment(
                        """
                                The percentage of liquid regions that will be filled with water.
                                Should be between 0.0 and 100.0.
                                Default: 40.0""".indent(1))
                .worldRestart()
                .define("Water Region Spawn Chance", 40.0);

        liquidAltitude = BUILDER
                .comment(
                        """
                                The y-level at which caves and caverns are filled with liquid.
                                Default: -55""".indent(1))
                .worldRestart()
                .define("Liquid Altitude", -55);

        BUILDER.pop();
    }
}

