package com.yungnickyoung.minecraft.bettercaves.world;

import com.yungnickyoung.minecraft.bettercaves.config.Configuration;
import com.yungnickyoung.minecraft.bettercaves.world.cave.*;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.MapGenCaves;

import javax.annotation.Nonnull;

public class MapGenBetterCaves extends MapGenCaves {

    public enum CaveType {
        InvertedPerlinCavern,
        ValueFractalCave,
        SimplexFractalCave,
        CellularCave,
        PerlinFractalCave
    }

    private InvertedPerlinCavern invertedPerlinCavern;
    private ValueFractalCave valueFractalCave;
    private SimplexFractalCave simplexFractalCave;
    private CellularCave cellularCave;
    private PerlinFractalCave perlinFractalCave;

    public MapGenBetterCaves() {
    }

    @Override
    public void generate(World worldIn, int chunkX, int chunkZ, @Nonnull ChunkPrimer primer) {
        if (world == null) { // First call - initialize all cave types
            world = worldIn;
            this.invertedPerlinCavern = new InvertedPerlinCavern(world);
            this.valueFractalCave = new ValueFractalCave(world);
            this.simplexFractalCave = new SimplexFractalCave(world);
            this.cellularCave = new CellularCave(world);
            this.perlinFractalCave = new PerlinFractalCave(world);
        }

//        CaveType caveType = CaveType.InvertedPerlinCavern; // TODO: have this be chosen based on another noise generator that
                                                    // partitions off cave biomes based on real x-y-z coords

        CaveType caveType = Configuration.caveType;

        switch (caveType) {
            case InvertedPerlinCavern:
                invertedPerlinCavern.generate(chunkX, chunkZ, primer);
                break;
            case ValueFractalCave:
                valueFractalCave.generate(chunkX, chunkZ, primer);
                break;
            case SimplexFractalCave:
                simplexFractalCave.generate(chunkX, chunkZ, primer);
                break;
            case CellularCave:
                cellularCave.generate(chunkX, chunkZ, primer);
                break;
            case PerlinFractalCave:
                perlinFractalCave.generate(chunkX, chunkZ, primer);
                break;
            default:
                throw new IllegalArgumentException("generate() called with unsupported cave type " + caveType);
        }
    }
}
