package com.yungnickyoung.minecraft.bettercaves.world.cave.builder;


import com.yungnickyoung.minecraft.bettercaves.config.ConfigHolder;
import com.yungnickyoung.minecraft.bettercaves.enums.CavernType;
import com.yungnickyoung.minecraft.bettercaves.world.cave.CavernCarver;
import com.yungnickyoung.minecraft.bettercaves.world.cave.UndergroundCarver;
import net.minecraft.world.World;

/**
 * Builder class for CavernCarver.
 * Fields may be built individually or loaded in bulk via the {@code ofTypeFromCarver} method
 */
public class CavernCarverBuilder extends UndergroundCarverBuilder {
    private CavernType cavernType;

    public CavernCarverBuilder(World world) {
        super(world);
    }

    @Override
    public UndergroundCarver build() {
        return new CavernCarver(this);
    }

    /**
     * Helps build a CavernCarver from a ConfigHolder based on its CavernType
     * @param cavernType the CavernType of this CavernCarver
     * @param config the config
     */
    public CavernCarverBuilder ofTypeFromConfig(CavernType cavernType, ConfigHolder config) {
        this.liquidAltitude = config.liquidAltitude.get();
        this.enableDebugVisualizer = config.debugVisualizer.get();
        this.cavernType = cavernType;
        switch (cavernType) {
            case LAVA:
                this.noiseType = config.lavaCavernNoiseType.get();
                this.noiseThreshold = config.lavaCavernNoiseThreshold.get();
                this.fractalOctaves = config.lavaCavernFractalOctaves.get();
                this.fractalGain = config.lavaCavernFractalGain.get();
                this.fractalFreq = config.lavaCavernFractalFrequency.get();
                this.numGens = config.lavaCavernNumGenerators.get();
                this.yCompression = config.lavaCavernYCompression.get();
                this.xzCompression = config.lavaCavernXZCompression.get();
                break;
            case FLOORED:
                this.noiseType = config.flooredCavernNoiseType.get();
                this.noiseThreshold = config.flooredCavernNoiseThreshold.get();
                this.fractalOctaves = config.flooredCavernFractalOctaves.get();
                this.fractalGain = config.flooredCavernFractalGain.get();
                this.fractalFreq = config.flooredCavernFractalFrequency.get();
                this.numGens = config.flooredCavernNumGenerators.get();
                this.yCompression = config.flooredCavernYCompression.get();
                this.xzCompression = config.flooredCavernXZCompression.get();
                break;
            case WATER:
                this.noiseType = config.waterCavernNoiseType.get();
                this.noiseThreshold = config.waterCavernNoiseThreshold.get();
                this.fractalOctaves = config.waterCavernFractalOctaves.get();
                this.fractalGain = config.waterCavernFractalGain.get();
                this.fractalFreq = config.waterCavernFractalFrequency.get();
                this.numGens = config.waterCavernNumGenerators.get();
                this.yCompression = config.waterCavernYCompression.get();
                this.xzCompression = config.waterCavernXZCompression.get();
                break;
        }
        return this;
    }

    public CavernType getCavernType() {
        return cavernType;
    }
}
