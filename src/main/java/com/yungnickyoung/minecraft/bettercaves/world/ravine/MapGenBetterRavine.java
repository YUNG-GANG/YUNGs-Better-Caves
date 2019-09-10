package com.yungnickyoung.minecraft.bettercaves.world.ravine;

import com.yungnickyoung.minecraft.bettercaves.config.Configuration;
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
        if (Configuration.caveSettings.vanillaCave.enableVanillaRavines)
            super.recursiveGenerate(worldIn, chunkX, chunkZ, originalX, originalZ, chunkPrimerIn);
    }

}
