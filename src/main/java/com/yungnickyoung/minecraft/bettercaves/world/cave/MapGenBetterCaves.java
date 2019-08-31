package com.yungnickyoung.minecraft.bettercaves.world.cave;

import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.MapGenCaves;

import javax.annotation.Nonnull;

public class MapGenBetterCaves extends MapGenCaves {
    private SimplexCavePerlinCavern simplexCavePerlinCavern;
    private MapGenCaves defaultCaveGen;

    public MapGenBetterCaves() {
    }

    @Override
    public void generate(World worldIn, int chunkX, int chunkZ, @Nonnull ChunkPrimer primer) {
        if (world == null) { // First call - initialize all cave types
            world = worldIn;
            this.simplexCavePerlinCavern = new SimplexCavePerlinCavern(worldIn);
            defaultCaveGen = new MapGenCaves();
        }

        // Only use Better Caves generation in overworld
        if (worldIn.provider.getDimension() == 0)
            simplexCavePerlinCavern.generate(chunkX, chunkZ, primer);
        else
            defaultCaveGen.generate(worldIn, chunkX, chunkZ, primer);
    }
}
