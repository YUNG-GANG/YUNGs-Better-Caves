package com.yungnickyoung.minecraft.bettercaves.world;

import com.yungnickyoung.minecraft.bettercaves.world.cave.Cavern;
import com.yungnickyoung.minecraft.bettercaves.world.cave.DynamicCavern;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.MapGenCaves;

import javax.annotation.Nonnull;


public class MapGenBetterCaves extends MapGenCaves {

    public enum CaveType {
        Fractal,
        Cavern,
        DynamicCavern,
        Worleys,
    }

    private Cavern cavern;
    private DynamicCavern dynamicCavern;

    public MapGenBetterCaves() {
    }

    @Override
    public void generate(World worldIn, int chunkX, int chunkZ, @Nonnull ChunkPrimer primer) {
        if (world == null) { // First call - initialize all noise generators using world seed
            world = worldIn;
            this.cavern = new Cavern(world);
            this.dynamicCavern = new DynamicCavern(world);
        }

        CaveType caveType = CaveType.DynamicCavern;

        switch (caveType) {
            case Fractal:

                break;
            case Cavern:
                cavern.generate(chunkX, chunkZ, primer);
                break;
            case DynamicCavern:
                dynamicCavern.generate(chunkX, chunkZ, primer);
                break;
            case Worleys:

                break;
            default:
                throw new IllegalArgumentException("generate() called with unsupported cave type " + caveType);
        }
    }

//    private void generateFractalModelForScreenShots(int chunkX, int chunkZ, ChunkPrimer primer) {
//        for (int localX = 0; localX < 16; localX++) {
//            int realX = localX + 16*chunkX;
//
//            for (int localZ = 0; localZ < 16; localZ++) {
//                int realZ = localZ + 16*chunkZ;
//
//                for (int realY = 128; realY > 0; realY--) {
//                    if (realX < 0) {
//                        primer.setBlockState(localX, realY, localZ, Blocks.AIR.getDefaultState());
//                    } else {
//                        Vector3f f = new Vector3f(realX, realY, realZ);
//
//                        if (Configuration.enableTurbulence)
//                            turbulence.GradientPerturbFractal(f);
//
//                        float noise1 = noiseGenerator1.GetNoise(f.x, f.y, f.z);
//                        float noise2 = noiseGenerator2.GetNoise(f.x, f.y, f.z);
//                        float noise = noise1 * noise2;
//
//                        if (noise > .8) {
//                            primer.setBlockState(localX, realY, localZ, Blocks.QUARTZ_BLOCK.getDefaultState());
//                        } else {
//                            primer.setBlockState(localX, realY, localZ, Blocks.AIR.getDefaultState());
//                        }
//                    }
//                }
//            }
//        }
//    }
}
