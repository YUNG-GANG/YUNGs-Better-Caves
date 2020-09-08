package com.yungnickyoung.minecraft.bettercaves.fabricconfig;


import me.sargunvohra.mcmods.autoconfig1u.ConfigData;
import me.sargunvohra.mcmods.autoconfig1u.annotation.Config;
import me.sargunvohra.mcmods.autoconfig1u.annotation.ConfigEntry;
import me.sargunvohra.mcmods.autoconfig1u.shadowed.blue.endless.jankson.Comment;

@Config(name = "Underground Settings")
public class BCUnderground implements ConfigData {

    @ConfigEntry.Gui.Tooltip()
    @Comment("Cave Spawn Chance.")
    public double caveSpawnChance = 100.0;

    @ConfigEntry.Gui.Tooltip()
    @Comment("Cave Spawn Chance. Accepted values: Small, Medium, Large, ExtraLarge, Custom ")
    public String caveRegionSize = "Small";

    @ConfigEntry.Gui.Tooltip()
    @Comment("Cave Spawn Chance.")
    public double caveRegionSizeCustomValue = 0.00800000037997961;

    @ConfigEntry.Gui.CollapsibleObject
    public Type1Caves type1Caves = new Type1Caves();

    @ConfigEntry.Gui.CollapsibleObject
    public Type2Caves type2Caves = new Type2Caves();

    @ConfigEntry.Gui.CollapsibleObject
    public SurfaceCaves surfaceCaves = new SurfaceCaves();

    @ConfigEntry.Gui.CollapsibleObject
    public VanillaCaves vanillaCaves = new VanillaCaves();

    @ConfigEntry.Gui.CollapsibleObject
    public Caverns caverns = new Caverns();

    public static class Type1Caves {

        @ConfigEntry.Gui.Tooltip()
        @Comment("Type 1 Cave Minimum Altitude")
        public int caveMinAltitude = 1;

        @ConfigEntry.Gui.Tooltip()
        @Comment("Type 1 Cave Maximum Altitude")
        public int caveMaxAltitude = 80;

        @ConfigEntry.Gui.Tooltip()
        @Comment("Type 1 Cave Surface Cutoff depth")
        public int caveCutOffDepth = 15;

        @ConfigEntry.Gui.Tooltip()
        @Comment("Type 1 Cave vertical compression")
        public double verticalCompression = 5.0;

        @ConfigEntry.Gui.Tooltip()
        @Comment("Type 1 Cave Horizontal compression")
        public double horizontalCompression = 1.6;

        @ConfigEntry.Gui.Tooltip()
        @Comment("Type 1 Cave Priority")
        public int cavePriority = 10;
    }

    public static class Type2Caves {

        @ConfigEntry.Gui.Tooltip()
        @Comment("Type 2 Cave Minimum Altitude")
        public int caveMinAltitude = 1;

        @ConfigEntry.Gui.Tooltip()
        @Comment("Type 2 Cave Maximum Altitude")
        public int caveMaxAltitude = 80;

        @ConfigEntry.Gui.Tooltip()
        @Comment("Type 2 Cave Surface Cutoff depth")
        public int caveCutOffDepth = 15;

        @ConfigEntry.Gui.Tooltip()
        @Comment("Type 2 Cave vertical compression")
        public double verticalCompression = 2.2;

        @ConfigEntry.Gui.Tooltip()
        @Comment("Type 2 Cave Horizontal compression")
        public double horizontalCompression = 0.9;

        @ConfigEntry.Gui.Tooltip()
        @Comment("Type 2 Cave Priority")
        public int cavePriority = 5;
    }

    public static class SurfaceCaves {

        @ConfigEntry.Gui.Tooltip()
        @Comment("Enable Surface Caves")
        public boolean enableVanillaCaves = true;

        @ConfigEntry.Gui.Tooltip()
        @Comment("Surface Cave Minimum Altitude")
        public int caveMinAltitude = 1;

        @ConfigEntry.Gui.Tooltip()
        @Comment("Surface Cave Maximum Altitude")
        public int caveMaxAltitude = 80;

        @ConfigEntry.Gui.Tooltip()
        @Comment("Surface Cave Density")
        public int caveDensity = 5;
    }

    public static class VanillaCaves {

        @ConfigEntry.Gui.Tooltip()
        @Comment("Vanilla Cave Minimum Altitude")
        public int caveMinAltitude = 1;

        @ConfigEntry.Gui.Tooltip()
        @Comment("Vanilla Cave Maximum Altitude")
        public int caveMaxAltitude = 80;

        @ConfigEntry.Gui.Tooltip()
        @Comment("Vanilla Cave Density")
        public int caveDensity = 5;

        @ConfigEntry.Gui.Tooltip()
        @Comment("Vanilla Cave Priority")
        public int cavePriority = 5;
    }

    public static class Caverns {

        @ConfigEntry.Gui.Tooltip()
        @Comment("Cavern Spawn Chance")
        public double cavernSpawnChance = 25.0;

        @ConfigEntry.Gui.Tooltip()
        @Comment("Cavern Region Size")
        public int caveMinAltitude = 1;

        @ConfigEntry.Gui.Tooltip()
        @Comment("Vanilla Cave Maximum Altitude")
        public int caveMaxAltitude = 80;

        @ConfigEntry.Gui.Tooltip()
        @Comment("Vanilla Cave Density")
        public int caveDensity = 5;

        @ConfigEntry.Gui.Tooltip()
        @Comment("Vanilla Cave Priority")
        public int cavePriority = 5;
    }
}
