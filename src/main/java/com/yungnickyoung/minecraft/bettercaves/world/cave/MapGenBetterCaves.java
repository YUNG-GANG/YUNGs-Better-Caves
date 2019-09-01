package com.yungnickyoung.minecraft.bettercaves.world.cave;

import com.yungnickyoung.minecraft.bettercaves.config.Settings;
import com.yungnickyoung.minecraft.bettercaves.util.BetterCaveUtil;
import com.yungnickyoung.minecraft.bettercaves.util.FastNoise;
import net.minecraft.init.Biomes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.MapGenCaves;

import javax.annotation.Nonnull;
import javax.vecmath.Vector2f;

public class MapGenBetterCaves extends MapGenCaves {
    private CaveBiomeLavaCavern caveBiomeLavaCavern;
    private CaveBiomeFlooredCavern caveBiomeFlooredCavern;
    private CaveBiomeOnlyCaves caveBiomeOnlyCaves;
    private MapGenCaves defaultCaveGen;

    private FastNoise caveBiomeController;
    private FastNoise controllerJitter;

    public MapGenBetterCaves() {
    }

    @Override
    public void generate(World worldIn, int chunkX, int chunkZ, @Nonnull ChunkPrimer primer) {
        if (world == null) { // First call - initialize all cave types
            world = worldIn;
            this.caveBiomeLavaCavern = new CaveBiomeLavaCavern(worldIn);
            this.caveBiomeFlooredCavern = new CaveBiomeFlooredCavern(worldIn);
            this.caveBiomeOnlyCaves = new CaveBiomeOnlyCaves(worldIn);
            defaultCaveGen = new MapGenCaves();

            this.caveBiomeController = new FastNoise();
            this.caveBiomeController.SetSeed((int)worldIn.getSeed());
            this.caveBiomeController.SetFrequency(.01f);
            this.caveBiomeController.SetCellularDistanceFunction(FastNoise.CellularDistanceFunction.Natural);

            this.controllerJitter = new FastNoise();
            this.controllerJitter.SetSeed((int)(worldIn.getSeed()) + 69420);
            this.controllerJitter.SetGradientPerturbAmp(60);
            this.controllerJitter.SetFrequency(.01f);
        }

        // Find the lowest and highest surface altitudes in this chunk
        int maxSurfaceHeight = BetterCaveUtil.getMaxSurfaceHeight(primer);
        int minSurfaceHeight = BetterCaveUtil.getMinSurfaceHeight(primer);

        // Only use Better Caves generation in overworld
        if (worldIn.provider.getDimension() == 0) {
            for (int localX = 0; localX < 16; localX++) {
                for (int localZ = 0; localZ < 16; localZ++) {
                    Vector2f f = new Vector2f(((chunkX * 16) + localX), ((chunkZ * 16) + localZ));
//                    controllerJitter.GradientPerturb(f);
                    float noiseVal = caveBiomeController.GetNoise(f.x, f.y);
                    if (noiseVal < -.3f)
                        caveBiomeLavaCavern.generateColumn(chunkX, chunkZ, primer, localX, localZ, maxSurfaceHeight, minSurfaceHeight);
                    else if (noiseVal >= -.3f && noiseVal <= .3f)
                        caveBiomeFlooredCavern.generateColumn(chunkX, chunkZ, primer, localX, localZ, maxSurfaceHeight, minSurfaceHeight);
                    else
                        caveBiomeOnlyCaves.generateColumn(chunkX, chunkZ, primer, localX, localZ, maxSurfaceHeight, minSurfaceHeight);
                }
            }
        } else
            defaultCaveGen.generate(worldIn, chunkX, chunkZ, primer);
    }
}
