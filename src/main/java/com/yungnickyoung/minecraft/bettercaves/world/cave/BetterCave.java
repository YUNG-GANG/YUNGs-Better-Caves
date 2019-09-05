package com.yungnickyoung.minecraft.bettercaves.world.cave;

import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkPrimer;

public abstract class BetterCave {
    private World world;
    private long seed;

    public BetterCave(World world) {
        this.world = world;
        this.seed = world.getSeed();
    }

    public World getWorld() {
        return this.world;
    }

    public long getSeed() {
        return this.seed;
    }

    public abstract void generateColumn(int chunkX, int chunkZ, ChunkPrimer primer, int localX, int localZ, int bottomY,
                      int topY, int maxSurfaceHeight, int minSurfaceHeight);

}
