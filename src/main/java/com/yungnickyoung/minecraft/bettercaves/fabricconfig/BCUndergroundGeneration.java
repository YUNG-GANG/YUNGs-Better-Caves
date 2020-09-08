package com.yungnickyoung.minecraft.bettercaves.fabricconfig;

import me.sargunvohra.mcmods.autoconfig1u.annotation.Config;
import me.sargunvohra.mcmods.autoconfig1u.annotation.ConfigEntry;
import me.sargunvohra.mcmods.autoconfig1u.shadowed.blue.endless.jankson.Comment;
import net.minecraft.world.gen.feature.FlowerFeature;

@Config(name = "better-caves")
public class BCUndergroundGeneration {


    @ConfigEntry.Category("Miscellaneous")
    @Comment("Miscellaneous settings used in cave and cavern generation.")
    @ConfigEntry.Gui.TransitiveObject
    public Miscellaneous miscellaneous = new Miscellaneous();

    @ConfigEntry.Category("Water_Regions")
    @Comment("Settings used in the generation of water regions.")
    @ConfigEntry.Gui.TransitiveObject
    public WaterRegions waterRegions = new WaterRegions();

    @ConfigEntry.Category("Caves")
    @Comment("Settings used in the generation of caves.")
    @ConfigEntry.Gui.TransitiveObject
    public Caves caves = new Caves();

    @ConfigEntry.Category("Caverns")
    @Comment("Settings used in the generation of caverns. Caverns are spacious caves at low altitudes.")
    @ConfigEntry.Gui.TransitiveObject
    public Caverns caverns = new Caverns();

    @ConfigEntry.Category("Ravines")
    @Comment("Settings used for ravine generation.")
    @ConfigEntry.Gui.TransitiveObject
    public Ravines ravines = new Ravines();

    @ConfigEntry.Category("Debug_Settings")
    @Comment("Don't mess with these settings for normal gameplay.")
    @ConfigEntry.Gui.TransitiveObject
    public Debug debug = new Debug();

    public static class Miscellaneous {

        @ConfigEntry.Gui.Tooltip()
        @Comment("Set to true to enable flooded underground in ocean biomes.\n" +
                "Default: true")
        public boolean enableFloodedUnderGround = true;

        @ConfigEntry.Gui.Tooltip()
        @Comment("Ignores surface detection for closing off caves and caverns, forcing them to spawn up until their max height. Useful for Nether-like dimensions with no real \"surface\".\n" +
                "Default: false")
        public boolean overrideSurfaceDetection = false;


        @ConfigEntry.Gui.Tooltip()
        @Comment("Lava (or water in water regions) spawns at and below this y-coordinate.\n" +
                "Default: 10")
        public int liquidAltitude = 10;

        @ConfigEntry.Gui.Tooltip()
        @Comment("Replace naturally generated floating gravel on the ocean floor with andesite.\n" +
                "Can prevent lag due to cascading gravel falling into caverns under the ocean.\n" +
                "Default: true")
        public boolean preventCascadingGravel = true;

        @ConfigEntry.Gui.Tooltip()
        @Comment("The block used for lava generation at and below the Liquid Altitude.\n" +
                "Defaults to regular lava if an invalid block is given.\n" +
                "Default: minecraft:lava")
        public String lavaBlock = "minecraft:lava";

        @ConfigEntry.Gui.Tooltip()
        @Comment("The block used for water generation in water caves/caverns at and below the Liquid Altitude.\n" +
                "Defaults to regular water if an invalid block is given.\n" +
                "Default: minecraft:water")
        public String waterBlock = "minecraft:water";
    }

    public static class WaterRegions {
        @ConfigEntry.Gui.Tooltip()
        @Comment("# Custom value for water region size. Smaller value = larger regions. This value is very sensitive to change.\n" +
                "ONLY WORKS IF Water Region Size IS Custom.\n" +
                "Provided values:\n" +
                "Small: 0.008\n" +
                "Medium: 0.004\n" +
                "Large: 0.0028\n" +
                "ExtraLarge: 0.001\n" +
                "Default: 0.004\n" +
                "Range: 0.0 ~ 0.05")
        public double waterRegionSizeCustomValue = 0.004;

        @ConfigEntry.Gui.Tooltip()
        @Comment(" Percent chance of a region having water instead of lava at low altitudes.\n" +
                "Default: 40%\n" +
                "Range: 0.0 ~ 100.0")
        public double waterRegionSpawnChance = 40.0;


        @ConfigEntry.Gui.Tooltip()
        @Comment("Determines how large water regions are.\n" +
                "Default: Medium (recommended).")
        public String waterRegionSize = "Medium";
    }

    public static class Caves {

        @ConfigEntry.Category("Type1_Caves")
        @Comment("Settings used in the generation of type 1 caves, which are more worm-like.")
        @ConfigEntry.Gui.TransitiveObject
        public Type1Caves type1Caves = new Type1Caves();

        @ConfigEntry.Category("Type2_Caves")
        @Comment("Settings used in the generation of type 2 caves, which are more worm-like.")
        @ConfigEntry.Gui.TransitiveObject
        public Type2Caves type2Caves = new Type2Caves();

        @ConfigEntry.Category("Vanilla_Caves")
        @Comment("Settings used in the generation of vanilla caves, which are more worm-like.")
        @ConfigEntry.Gui.TransitiveObject
        public VanillaCaves vanilla = new VanillaCaves();

        @ConfigEntry.Category("Surface_Caves")
        @Comment("Settings used in the generation of vanilla-like caves near the surface.")
        @ConfigEntry.Gui.TransitiveObject
        public SurfaceCaves surfaceCaves = new SurfaceCaves();


        @ConfigEntry.Gui.Tooltip()
        @Comment("Percent chance of caves spawning in a given region.\n" +
                "Default: caves spawn in 100% of regions.\n" +
                "Range: 0.0 ~ 100.0")
        public double caveSpawnChance = 100.0;

        @ConfigEntry.Gui.Tooltip()
        @Comment("Determines how large cave regions are.\n" +
                "Controls how long a cave system of a certain cave type extends before intersecting with a cave system of another type.\n" +
                "Larger = more cave interconnectivity for a given area, but less variation.\n" +
                "Accepted values: Small, Medium, Large, ExtraLarge, Custom\n" +
                "Default: Small (recommended).")
        public String caveRegionSize = "Small";

        @ConfigEntry.Gui.Tooltip()
        @Comment("Custom value for cave region size. Smaller value = larger regions. This value is very sensitive to change.\n" +
                "ONLY WORKS IF Cave Region Size IS Custom.\n" +
                "Provided values:\n" +
                "Small: 0.008\n" +
                "Medium: 0.005\n" +
                "Large: 0.0032\n" +
                "ExtraLarge: 0.001\n" +
                "Default: 0.008\n" +
                "Range: 0.0 ~ 0.05000000074505806")
        public double caveRegionSizeCustomValue = 0.00800000037997961;


        public static class SurfaceCaves {

            @ConfigEntry.Gui.Tooltip()
            @Comment("Set to true to enable vanilla-like caves which provide nice, natural-looking openings at the surface.\n" +
                    "Default: true")
            public boolean enableSurfaceCaves = true;

            @ConfigEntry.Gui.Tooltip()
            @Comment("The minimum y-coordinate at which surface caves can generate.\n" +
                    "Default: 40\n" +
                    "Range: 0 ~ 255")
            @ConfigEntry.BoundedDiscrete(min = 0, max = 255)
            public int surfaceCaveMinimumAltitude = 40;

            @ConfigEntry.Gui.Tooltip()
            @Comment("The maximum y-coordinate at which surface caves can generate.\n" +
                    "Default: 128\n" +
                    "Range: 0 ~ 255")
            @ConfigEntry.BoundedDiscrete(min = 0, max = 255)
            public int surfaceCaveMaximumAltitude = 128;



            @ConfigEntry.Gui.Tooltip()
            @Comment("The density of surface caves. Higher = more caves, closer together. \n" +
                    "Default: 17\n" +
                    "Range: 0 ~ 100")
            public int surfaceCaveDensity = 17;
        }

        public static class Type1Caves {

            @ConfigEntry.Gui.Tooltip()
            @Comment("The minimum y-coordinate at which type 1 caves can generate.\n" +
                    "Default: 1\n" +
                    "Range: 0 ~ 255")
            @ConfigEntry.BoundedDiscrete(min = 0, max = 255)
            public int type1CaveMinimumAltitude = 1;

            @ConfigEntry.Gui.Tooltip()
            @Comment("The maximum y-coordinate at which type 1 caves can generate.\n" +
                    "Default: 80\n" +
                    "Range: 0 ~ 255")
            @ConfigEntry.BoundedDiscrete(min = 0, max = 255)
            public int type1CaveMaximumAltitude = 80;

            @ConfigEntry.Gui.Tooltip()
            @Comment("Stretches caves horizontally. Lower value = wider caves.\n" +
                    "Default: 1.6 (recommended)\n" +
                    "Range: 0.0 ~ 100.0")
            public double compressionHorizontal = 1.6;

            @ConfigEntry.Gui.Tooltip()
            @Comment("Stretches caves vertically. Lower value = taller caves with steeper drops.\n" +
                    "Default: 5.0 (recommended)\n" +
                    "Range: 0.0 ~ 100.0")
            public double compressionVertical = 5.0;

            @ConfigEntry.Gui.Tooltip()
            @Comment("The depth from a given point on the surface at which type 1 caves start to close off.\n" +
                    "Will use the Max Cave Altitude instead of surface height if it is lower.\n" +
                    "Will use the Max Cave Altitude no matter what if Override Surface Detection is enabled.\n" +
                    "Default: 15 (recommended)\n" +
                    "Range: 0 ~ 255")
            @ConfigEntry.BoundedDiscrete(min = 0, max = 255)
            public int type1CaveSurfaceCutoffDepth = 15;

            @ConfigEntry.Gui.Tooltip()
            @Comment("Determines how frequently Type 1 Caves spawn. 0 = will not spawn at all.\n" +
                    "Default: 10\n" +
                    "Range: 0 ~ 10")
            public int type1CavePriority = 10;

        }

        public static class Type2Caves {

            @ConfigEntry.Gui.Tooltip()
            @Comment("The minimum y-coordinate at which Type 2 caves can generate.\n" +
                    "Default: 1\n" +
                    "Range: 0 ~ 255")
            @ConfigEntry.BoundedDiscrete(min = 0, max = 255)
            public int type2CaveMinimumAltitude = 1;

            @ConfigEntry.Gui.Tooltip()
            @Comment("The maximum y-coordinate at which Type 2 caves can generate.\n" +
                    "Default: 80" +
                    "Range: 0 ~ 255")
            @ConfigEntry.BoundedDiscrete(min = 0, max = 255)
            public int type2CaveMaximumAltitude = 80;

            @ConfigEntry.Gui.Tooltip()
            @Comment("Stretches caves horizontally. Lower value = wider caves.\n" +
                    "Default: 1.6 (recommended)\n" +
                    "Range: 0.0 ~ 100.0")
            public double compressionHorizontal = 0.9;

            @ConfigEntry.Gui.Tooltip()
            @Comment("Stretches caves vertically. Lower value = taller caves with steeper drops.\n" +
                    "Default: 5.0 (recommended)\n" +
                    "Range: 0.0 ~ 100.0")
            public double compressionVertical = 2.2;

            @ConfigEntry.Gui.Tooltip()
            @Comment("The depth from a given point on the surface at which Type 2 caves start to close off.\n" +
                    "Will use the Max Cave Altitude instead of surface height if it is lower.\n" +
                    "Will use the Max Cave Altitude no matter what if Override Surface Detection is enabled.\n" +
                    "Default: 15 (recommended)\n" +
                    "Range: 0 ~ 255")
            @ConfigEntry.BoundedDiscrete(min = 0, max = 255)
            public int type2CaveSurfaceCutoffDepth = 15;

            @ConfigEntry.Gui.Tooltip()
            @Comment("Determines how frequently Type 2 Caves spawn. 0 = will not spawn at all.\n" +
                    "Default: 10\n" +
                    "Range: 0 ~ 10")
            public int type2CavePriority = 5;
        }

        public static class VanillaCaves {

            @ConfigEntry.Gui.Tooltip()
            @Comment("The minimum y-coordinate at which Vanilla caves can generate.\n" +
                    "Default: 1\n" +
                    "Range: 0 ~ 255")
            @ConfigEntry.BoundedDiscrete(min = 0, max = 255)
            public int vanillaCaveMinimumAltitude = 1;

            @ConfigEntry.Gui.Tooltip()
            @Comment("The maximum y-coordinate at which Vanilla caves can generate.\n" +
                    "Default: 80" +
                    "Range: 0 ~ 255")
            @ConfigEntry.BoundedDiscrete(min = 0, max = 255)
            public int vanillaCaveMaximumAltitude = 80;

            @ConfigEntry.Gui.Tooltip()
            @Comment("The density of vanilla caves. Higher = more caves, closer together. \n" +
                    "Default: 14 (value used in vanilla)\n" +
                    "Range: 0 ~ 100")
            @ConfigEntry.BoundedDiscrete(min = 0, max = 100)
            public int vanillaCaveDensity = 14;

            @ConfigEntry.Gui.Tooltip()
            @Comment("Determines how frequently Vanilla Caves spawn. 0 = will not spawn at all.\n" +
                    "Default: 10\n" +
                    "Range: 0 ~ 10")
            public int vanillaCavePriority = 0;
        }
    }

    public static class Caverns {

        @ConfigEntry.Category("Floored_Caverns")
        @Comment("Settings used in the generation of Floored Caverns found at low altitudes.\n" +
                "These have much more ground to walk on than Liquid Caverns.")
        @ConfigEntry.Gui.TransitiveObject
        public FlooredCaverns flooredCaverns = new FlooredCaverns();

        @ConfigEntry.Category("Liquid_Caverns")
        @Comment("Settings used in the generation of Liquid Caverns found at low altitudes.\n" +
                "These are caverns where the floor is predominantly water or lava.")
        @ConfigEntry.Gui.TransitiveObject
        public LiquidCaverns liquidCaverns = new LiquidCaverns();


        @ConfigEntry.Gui.Tooltip()
        @Comment("Percent chance of caverns spawning in a given region.\n" +
                "Default: caverns spawn in 25% of regions.\n" +
                "Range: 0.0 ~ 100.0")
        public double cavernSpawnChance = 50.0;

        @ConfigEntry.Gui.Tooltip()
        @Comment("Determines how large cavern regions are. This controls the average size of caverns.\n" +
                "Accepted values: Small, Medium, Large, ExtraLarge, Custom\n" +
                "Default: Small (recommended).")
        public String cavernRegionSize = "Small";

        @ConfigEntry.Gui.Tooltip()
        @Comment("Custom value for cavern region size. Only works if Cavern Region Size is set to Custom. Smaller value = larger regions. This value is very sensitive to change.\n" +
                "Provided values:\n" +
                "Small: 0.01\n" +
                "Medium: 0.007\n" +
                "Large: 0.005\n" +
                "ExtraLarge: 0.001\n" +
                "Default: 0.01\n" +
                "Range: 0.0 ~ 0.05")
        public double cavernRegionSizeCustomValue = 0.01;

        public static class FlooredCaverns {
            @ConfigEntry.Gui.Tooltip()
            @Comment("The minimum y-coordinate at which Floored Caverns can generate.\n" +
                    "Default: 1\n" +
                    "Range: 0 ~ 255")
            @ConfigEntry.BoundedDiscrete(min = 0, max = 255)
            public int flooredCavernCaveMinimumAltitude = 1;

            @ConfigEntry.Gui.Tooltip()
            @Comment("The maximum y-coordinate at which Floored Caverns can generate.\n" +
                    "Default: 35" +
                    "Range: 0 ~ 255")
            @ConfigEntry.BoundedDiscrete(min = 0, max = 255)
            public int flooredCavernCaveMaximumAltitude = 35;

            @ConfigEntry.Gui.Tooltip()
            @Comment("Stretches caves horizontally. Lower value = wider caves.\n" +
                    "Default: 0.7 (recommended)\n" +
                    "Range: 0.0 ~ 100.0")
            public double compressionHorizontal = 0.7;

            @ConfigEntry.Gui.Tooltip()
            @Comment("Stretches caves vertically. Lower value = taller caves with steeper drops.\n" +
                    "Default: 1.3 (recommended)\n" +
                    "Range: 0.0 ~ 100.0")
            public double compressionVertical = 1.3;

            @ConfigEntry.Gui.Tooltip()
            @Comment("Determines how frequently Floored Caverns spawn. 0 = will not spawn at all.\n" +
                    "Default: 10\n" +
                    "Range: 0 ~ 10")
            public int flooredCavernCavePriority = 10;

        }

        public static class LiquidCaverns {
            @ConfigEntry.Gui.Tooltip()
            @Comment("The minimum y-coordinate at which Liquid Caverns can generate.\n" +
                    "Default: 1\n" +
                    "Range: 0 ~ 255")
            @ConfigEntry.BoundedDiscrete(min = 0, max = 255)
            public int liquidCavernCaveMinimumAltitude = 1;

            @ConfigEntry.Gui.Tooltip()
            @Comment("The maximum y-coordinate at which Liquid Caverns can generate.\n" +
                    "Default: 35" +
                    "Range: 0 ~ 255")
            @ConfigEntry.BoundedDiscrete(min = 0, max = 255)
            public int liquidCavernCaveMaximumAltitude = 35;

            @ConfigEntry.Gui.Tooltip()
            @Comment("Stretches caves horizontally. Lower value = wider caves.\n" +
                    "Default: 0.7 (recommended)\n" +
                    "Range: 0.0 ~ 100.0")
            public double compressionHorizontal = 0.7;

            @ConfigEntry.Gui.Tooltip()
            @Comment("Stretches caves vertically. Lower value = taller caves with steeper drops.\n" +
                    "Default: 1.3 (recommended)\n" +
                    "Range: 0.0 ~ 100.0")
            public double compressionVertical = 1.3;

            @ConfigEntry.Gui.Tooltip()
            @Comment("Determines how frequently Liquid Caverns spawn. 0 = will not spawn at all.\n" +
                    "Default: 10\n" +
                    "Range: 0 ~ 10")
            public int liquidCavernCavePriority = 10;

        }
    }

    public static class Ravines {
        @ConfigEntry.Gui.Tooltip()
        @Comment("Set to true to enable ravines in ocean biomes.\n" +
                "Default: true")
        public boolean enableRavines = true;

        @ConfigEntry.Gui.Tooltip()
        @Comment("Set to true to enable flooded ravines in ocean biomes.\n" +
                "Default: true")
        public boolean enableFloodedRavines = true;
    }
}
