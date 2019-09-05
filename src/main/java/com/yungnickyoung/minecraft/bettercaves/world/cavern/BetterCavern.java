package com.yungnickyoung.minecraft.bettercaves.world.cavern;

import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkPrimer;

public abstract class BetterCavern {
    private World world;
    private long seed;

    public BetterCavern(World world) {
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
