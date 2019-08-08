package com.yungnickyoung.minecraft.bettercaves.world;

import com.yungnickyoung.minecraft.bettercaves.world.cave.Cavern;
import com.yungnickyoung.minecraft.bettercaves.world.cave.DynamicCavern;
import com.yungnickyoung.minecraft.bettercaves.world.cave.ReverseCavern;
import com.yungnickyoung.minecraft.bettercaves.world.cave.ReverseDynamicCavern;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.MapGenCaves;

import javax.annotation.Nonnull;

public class MapGenBetterCaves extends MapGenCaves {

    public enum CaveType {
        Cavern,
        DynamicCavern,
        ReverseCavern,
        ReverseDynamicCavern,
    }

    private Cavern cavern;
    private DynamicCavern dynamicCavern;
    private ReverseCavern reverseCavern;
    private ReverseDynamicCavern reverseDynamicCavern;

    public MapGenBetterCaves() {
    }

    @Override
    public void generate(World worldIn, int chunkX, int chunkZ, @Nonnull ChunkPrimer primer) {
        if (world == null) { // First call - initialize all cave types
            world = worldIn;
            this.cavern = new Cavern(world);
            this.dynamicCavern = new DynamicCavern(world);
            this.reverseCavern = new ReverseCavern(world);
            this.reverseDynamicCavern = new ReverseDynamicCavern(world);
        }

        CaveType caveType = CaveType.ReverseDynamicCavern; // TODO: have this be chosen based on another noise generator that
                                                    // partitions off cave biomes based on real x-y-z coords

        switch (caveType) {
            case Cavern:
                cavern.generate(chunkX, chunkZ, primer);
                break;
            case DynamicCavern:
                dynamicCavern.generate(chunkX, chunkZ, primer);
                break;
            case ReverseCavern:
                reverseCavern.generate(chunkX, chunkZ, primer);
                break;
            case ReverseDynamicCavern:
                reverseDynamicCavern.generate(chunkX, chunkZ, primer);
                break;
            default:
                throw new IllegalArgumentException("generate() called with unsupported cave type " + caveType);
        }
    }
}
