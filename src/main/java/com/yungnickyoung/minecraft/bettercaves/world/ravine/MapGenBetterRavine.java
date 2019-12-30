package com.yungnickyoung.minecraft.bettercaves.world.ravine;

import com.yungnickyoung.minecraft.bettercaves.BetterCaves;
import com.yungnickyoung.minecraft.bettercaves.config.Configuration;
import com.yungnickyoung.minecraft.bettercaves.world.MapGenBetterCaves;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.MapGenRavine;

/**
 * Overrides MapGenRavine, disabling ravine generation if the config option
 * is set to false.
 */
public class MapGenBetterRavine extends MapGenRavine {
    @Override
    protected void recursiveGenerate(World worldIn, int chunkX, int chunkZ, int originalX, int originalZ, ChunkPrimer chunkPrimerIn) {
        // Get the Better Caves carver for this dimension, if it exists
        int dimensionID = worldIn.provider.getDimension();
        MapGenBetterCaves carver = BetterCaves.activeCarversMap.get(dimensionID);

        if (carver != null) { // If carver exists, use its config to determine ravine spawning
            if (carver.config.enableVanillaRavines.get())
                super.recursiveGenerate(worldIn, chunkX, chunkZ, originalX, originalZ, chunkPrimerIn);
        } else { // If carver is for some reason not found, use the global Better Caves config setting
            if (Configuration.caveSettings.caves.vanillaCave.enableVanillaRavines)
                super.recursiveGenerate(worldIn, chunkX, chunkZ, originalX, originalZ, chunkPrimerIn);
        }
    }
}
