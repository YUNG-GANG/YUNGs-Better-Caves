package com.yungnickyoung.minecraft.bettercaves.config;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Config(modid = Settings.MOD_ID, name = Settings.NAME)
public class Configuration {
    @Config.Name("F Threshold")
    @Config.Comment("The threshold for the F function")
    @Config.RangeDouble(min = 0)
    @Config.RequiresWorldRestart
    public static double FThreshold = 20;

    @Config.Name("Lava Depth")
    @Config.Comment("The y-coordinate below which lava replaces air. Default 10")
    @Config.RangeInt(min = 1)
    @Config.RequiresWorldRestart
    public static int lavaDepth = 10;

    @Config.Name("Use Turbulence")
    @Config.Comment("Enable to apply turbulence")
    @Config.RequiresWorldRestart
    public static boolean enableTurbulence = true;

    @Config.Name("Turbulence X Power")
    @Config.Comment("X-axis turbulence function power")
    @Config.RangeDouble(min = 0.0, max = 1.0)
    @Config.RequiresWorldRestart
    public static double turbPowerX = .25;

    @Config.Name("Turbulence Y Power")
    @Config.Comment("Y-axis turbulence function power")
    @Config.RangeDouble(min = 0.0, max = 1.0)
    @Config.RequiresWorldRestart
    public static double turbPowerY = .25;

    @Config.Name("Turbulence Z Power")
    @Config.Comment("Z-axis turbulence function power")
    @Config.RangeDouble(min = 0.0, max = 1.0)
    @Config.RequiresWorldRestart
    public static double turbPowerZ = .25;
}
