package com.yungnickyoung.minecraft.bettercaves.world;

import com.yungnickyoung.minecraft.bettercaves.config.Settings;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.MapGenCaves;

import javax.annotation.ParametersAreNonnullByDefault;

public class BetterCaveGenerator extends MapGenCaves {
    public BetterCaveGenerator() {

    }

    @Override
    @ParametersAreNonnullByDefault
    public void generate(World worldIn, int x, int z, ChunkPrimer primer) {
        Settings.LOGGER.info("generate() cave called: {} | {} | {} | {}", worldIn, x, z, primer);
    }
}
