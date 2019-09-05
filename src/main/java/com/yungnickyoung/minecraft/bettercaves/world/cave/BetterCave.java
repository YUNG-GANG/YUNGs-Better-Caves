package com.yungnickyoung.minecraft.bettercaves.world.cave;

import net.minecraft.world.chunk.ChunkPrimer;

public abstract class BetterCave {
    public abstract void generateColumn(int chunkX, int chunkZ, ChunkPrimer primer, int localX, int localZ, int bottomY,
                               int topY, int maxSurfaceHeight, int minSurfaceHeight);

}
