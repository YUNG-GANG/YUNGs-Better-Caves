package com.yungnickyoung.minecraft.bettercaves.module;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class ConfigModule {
//    public UndergroundGen undergroundGen = new UndergroundGen();

    public static class UndergroundGen {
//        public Caves caves = new Caves();
//        public Caverns caverns = new Caverns();
//        public WaterRegions waterRegions = new WaterRegions();
//        public Misc misc = new Misc();

        public static class Caves {
//            public CubicCaves cubicCaves = new CubicCaves();
//            public SimplexCaves simplexCaves = new SimplexCaves();
//            public SurfaceCaves surfaceCaves = new SurfaceCaves();
//            public VanillaCaves vanillaCaves = new VanillaCaves();
//            public double caveSpawnChance = 100.0;
//            public String caveRegionSize = "Small"; // equivalent to .008f
//            public double customRegionSize = 0.008;

            public static class CubicCaves {
                public int caveBottom = -63;
                public int caveTop = 80;
                public int caveSurfaceCutoff = 15;
                public double yCompression = 5.0;
                public double xzCompression = 1.6;
                public int cavePriority = 10;
                public Advanced advancedSettings = new Advanced();

                public static class Advanced {
                    public double noiseThreshold = 0.95;
                    public int fractalOctaves = 1;
                    public double fractalGain = 0.3;
                    public double fractalFrequency = 0.03;
                    public int numGenerators = 2;
                    public boolean yAdjust = true;
                    public double yAdjustF1 = 0.9;
                    public double yAdjustF2  = 0.9;
                    public String noiseType = "CubicFractal";
                }
            }

            public static class SimplexCaves {
                public int caveBottom = -63;
                public int caveTop = 80;
                public int caveSurfaceCutoff = 15;
                public double yCompression = 2.2;
                public double xzCompression = 0.9;
                public int cavePriority = 5;
                public Advanced advancedSettings = new Advanced();

                public static class Advanced {
                    public double noiseThreshold = 0.82;
                    public int fractalOctaves = 1;
                    public double fractalGain = 0.3;
                    public double fractalFrequency = 0.025;
                    public int numGenerators = 2;
                    public boolean yAdjust = true;
                    public double yAdjustF1 = 0.95;
                    public double yAdjustF2 = 0.5;
                    public String noiseType = "SimplexFractal";
                }
            }

            public static class SurfaceCaves {
                public boolean enableSurfaceCaves = true;
                public int caveBottom = 40;
                public int caveTop = 128;
                public int caveDensity = 17;
            }

            public static class VanillaCaves {
                public int caveBottom = 8;
                public int caveTop = 128;
                public int caveDensity = 14;
                public int cavePriority = 0;
            }
        }

        public static class Caverns {
            public LiquidCaverns liquidCaverns = new LiquidCaverns();
            public FlooredCaverns flooredCaverns = new FlooredCaverns();
            public double cavernSpawnChance = 25;
            public String cavernRegionSize = "Medium"; // equivalent to .007f
            public double customRegionSize = 0.01;

            public static class LiquidCaverns {
                public int cavernBottom = -63;
                public int cavernTop = -28;
                public double yCompression = 1.3;
                public double xzCompression = 0.7;
                public int cavePriority = 10;
                public Advanced advancedSettings = new Advanced();

                public static class Advanced {
                    public double noiseThreshold = 0.6;
                    public int fractalOctaves = 1;
                    public double fractalGain = 0.3;
                    public double fractalFrequency = 0.03;
                    public int numGenerators = 2;
                    public String noiseType = "SimplexFractal";
                }
            }

            public static class FlooredCaverns {
                public int cavernBottom = -63;
                public int cavernTop = -28;
                public double yCompression = 1.3;
                public double xzCompression = 0.7;
                public int cavePriority = 10;
                public Advanced advancedSettings = new Advanced();

                public static class Advanced {
                    public double noiseThreshold = 0.6;
                    public int fractalOctaves = 1;
                    public double fractalGain = 0.3;
                    public double fractalFrequency = 0.028;
                    public int numGenerators = 2;
                    public String noiseType = "SimplexFractal";
                }
            }
        }

        public static class WaterRegions {
            public double waterRegionSpawnChance = 40.0;
            public double waterRegionSize = 0.001;
        }

        public static class Misc {
//            public int liquidAltitude = -55;
//            public BlockState lavaBlock = Blocks.LAVA.defaultBlockState();
//            public BlockState waterBlock = Blocks.WATER.defaultBlockState();
//            public boolean replaceFloatingGravel = true;
//            public boolean overrideSurfaceDetection = false;
//            public boolean enableFloodedUnderground = true;
        }
    }
}
