package com.yungnickyoung.minecraft.bettercaves.world.cave.builder;

import com.yungnickyoung.minecraft.bettercaves.config.ConfigHolder;
import com.yungnickyoung.minecraft.bettercaves.enums.CaveType;
import com.yungnickyoung.minecraft.bettercaves.world.cave.CaveCarver;
import com.yungnickyoung.minecraft.bettercaves.world.cave.UndergroundCarver;
import net.minecraft.world.World;

/**
 * Builder class for CaveCarver.
 * Fields may be built individually or loaded in bulk via the {@code ofTypeFromCarver} method
 */
public class CaveCarverBuilder extends UndergroundCarverBuilder {
    private int surfaceCutoff;

    public CaveCarverBuilder(World world) {
        super(world);
    }

    @Override
    public UndergroundCarver build() {
        return new CaveCarver(this);
    }

    /**
     * Helps build a CaveCarver from a ConfigHolder based on its CaveType
     * @param caveType the CaveType of this CaveCarver
     * @param config the config
     */
    public CaveCarverBuilder ofTypeFromConfig(CaveType caveType, ConfigHolder config) {
        this.liquidAltitude = config.liquidAltitude.get();
        this.enableDebugVisualizer = config.debugVisualizer.get();
        this.surfaceCutoff = config.surfaceCutoff.get();
        switch (caveType) {
            case CUBIC:
                this.noiseType = config.cubicCaveNoiseType.get();
                this.noiseThreshold = config.cubicCaveNoiseThreshold.get();
                this.fractalOctaves = config.cubicCaveFractalOctaves.get();
                this.fractalGain = config.cubicCaveFractalGain.get();
                this.fractalFreq = config.cubicCaveFractalFrequency.get();
                this.enableTurbulence = config.cubicCaveEnableTurbulence.get();
                this.turbOctaves = config.cubicCaveTurbulenceOctaves.get();
                this.turbGain = config.cubicCaveTurbulenceGain.get();
                this.turbFreq = config.cubicCaveTurbulenceFrequency.get();
                this.numGens = config.cubicCaveNumGenerators.get();
                this.enableYAdjust = config.cubicCaveEnableVerticalAdjustment.get();
                this.yAdjustF1 = config.cubicCaveYAdjustF1.get();
                this.yAdjustF2 = config.cubicCaveYAdjustF2.get();
                this.xzCompression = config.cubicCaveXZCompression.get();
                this.yCompression = config.cubicCaveYCompression.get();
                break;
            case SIMPLEX:
                this.noiseType = config.simplexCaveNoiseType.get();
                this.noiseThreshold = config.simplexCaveNoiseThreshold.get();
                this.fractalOctaves = config.simplexCaveFractalOctaves.get();
                this.fractalGain = config.simplexCaveFractalGain.get();
                this.fractalFreq = config.simplexCaveFractalFrequency.get();
                this.enableTurbulence = config.simplexCaveEnableTurbulence.get();
                this.turbOctaves = config.simplexCaveTurbulenceOctaves.get();
                this.turbGain = config.simplexCaveTurbulenceGain.get();
                this.turbFreq = config.simplexCaveTurbulenceFrequency.get();
                this.numGens = config.simplexCaveNumGenerators.get();
                this.enableYAdjust = config.simplexCaveEnableVerticalAdjustment.get();
                this.yAdjustF1 = config.simplexCaveYAdjustF1.get();
                this.yAdjustF2 = config.simplexCaveYAdjustF2.get();
                this.xzCompression = config.simplexCaveXZCompression.get();
                this.yCompression = config.simplexCaveYCompression.get();
                break;
        }
        return this;
    }

    public int getSurfaceCutoff() {
        return surfaceCutoff;
    }
}

