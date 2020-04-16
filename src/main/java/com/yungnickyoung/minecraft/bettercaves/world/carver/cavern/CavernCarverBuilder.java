package com.yungnickyoung.minecraft.bettercaves.world.carver.cavern;

import com.yungnickyoung.minecraft.bettercaves.config.BetterCavesConfig;
import com.yungnickyoung.minecraft.bettercaves.config.ConfigHolder;
import com.yungnickyoung.minecraft.bettercaves.enums.CavernType;
import com.yungnickyoung.minecraft.bettercaves.noise.FastNoise;
import com.yungnickyoung.minecraft.bettercaves.world.carver.CarverSettings;
import net.minecraft.block.BlockState;

/**
 * Builder class for CavernCarver.
 * Fields may be built individually or loaded in bulk via the {@code ofTypeFromCarver} method
 */
public class CavernCarverBuilder {
    private CarverSettings settings;
    private CavernType cavernType;
    private int bottomY;
    private int topY;

    public CavernCarverBuilder(long seed) {
        settings = new CarverSettings(seed);
    }

    public CavernCarver build() {
        return new CavernCarver(this);
    }

    /**
     * Helps build a CavernCarver from a ConfigHolder based on its CavernType
     * @param cavernType the CavernType of this CavernCarver
     * @param config the config
     */
    public CavernCarverBuilder ofTypeFromConfig(CavernType cavernType, ConfigHolder config) {
        this.settings.setLiquidAltitude(BetterCavesConfig.liquidAltitude);
        this.settings.setReplaceFloatingGravel(BetterCavesConfig.replaceFloatingGravel);
        this.settings.getNoiseSettings().setFractalType(FastNoise.FractalType.RigidMulti);
        this.settings.setEnableDebugVisualizer(BetterCavesConfig.enableDebugVisualizer);
        this.settings.setFastNoise(true);
        this.cavernType = cavernType;
        switch (cavernType) {
            case LIQUID:
                this.settings.setNoiseThreshold(BetterCavesConfig.liquidCavernNoiseThreshold);
                this.settings.getNoiseSettings().setNoiseType(FastNoise.NoiseType.SimplexFractal); // TODO - split into config option
                this.settings.getNoiseSettings().setOctaves(BetterCavesConfig.liquidCavernFractalOctaves);
                this.settings.getNoiseSettings().setGain(BetterCavesConfig.liquidCavernFractalGain);
                this.settings.getNoiseSettings().setFrequency(BetterCavesConfig.liquidCavernFractalFreq);
                this.settings.setNumGens(BetterCavesConfig.liquidCavernNumGenerators);
                this.settings.setyCompression((float)BetterCavesConfig.liquidCavernYComp);
                this.settings.setXzCompression((float)BetterCavesConfig.liquidCavernXZComp);
                this.settings.setPriority(10);                                                      // TODO
                this.bottomY = BetterCavesConfig.liquidCavernBottom;
                this.topY = BetterCavesConfig.liquidCavernTop;
                break;
            case FLOORED:
                this.settings.setNoiseThreshold(BetterCavesConfig.flooredCavernNoiseThreshold);
                this.settings.getNoiseSettings().setNoiseType(FastNoise.NoiseType.SimplexFractal); // TODO - split into config option
                this.settings.getNoiseSettings().setOctaves(BetterCavesConfig.flooredCavernFractalOctaves);
                this.settings.getNoiseSettings().setGain(BetterCavesConfig.flooredCavernFractalGain);
                this.settings.getNoiseSettings().setFrequency(BetterCavesConfig.flooredCavernFractalFreq);
                this.settings.setNumGens(BetterCavesConfig.flooredCavernNumGenerators);
                this.settings.setyCompression((float)BetterCavesConfig.flooredCavernYComp);
                this.settings.setXzCompression((float)BetterCavesConfig.flooredCavernXZComp);
                this.settings.setPriority(10);                                                      // TODO
                this.bottomY = BetterCavesConfig.flooredCavernBottom;
                this.topY = BetterCavesConfig.flooredCavernTop;
                break;
        }
        return this;
    }

    /* ================================== Builder Setters ================================== */
    /**
     * @param noiseType The type of noise this carver will use
     */
    public CavernCarverBuilder noiseType(FastNoise.NoiseType noiseType) {
        settings.getNoiseSettings().setNoiseType(noiseType);
        return this;
    }

    /**
     * @param fractalOctaves Number of fractal octaves to use in ridged multifractal noise generation
     */
    public CavernCarverBuilder fractalOctaves(int fractalOctaves) {
        settings.getNoiseSettings().setOctaves(fractalOctaves);
        return this;
    }

    /**
     * @param fractalGain Amount of gain to use in ridged multifractal noise generation
     */
    public CavernCarverBuilder fractalGain(float fractalGain) {
        settings.getNoiseSettings().setGain(fractalGain);
        return this;
    }

    /**
     * @param fractalFreq Frequency to use in ridged multifractal noise generation
     */
    public CavernCarverBuilder fractalFrequency(float fractalFreq) {
        settings.getNoiseSettings().setFrequency(fractalFreq);
        return this;
    }

    /**
     * @param numGens Number of noise values to calculate for a given block
     */
    public CavernCarverBuilder numberOfGenerators(int numGens) {
        settings.setNumGens(numGens);
        return this;
    }

    /**
     * @param yCompression Vertical cave gen compression. Use 1.0 for default generation
     */
    public CavernCarverBuilder verticalCompression(float yCompression) {
        settings.setyCompression(yCompression);
        return this;
    }

    /**
     * @param xzCompression Horizontal cave gen compression. Use 1.0 for default generation
     */
    public CavernCarverBuilder horizontalCompression(float xzCompression) {
        settings.setXzCompression(xzCompression);
        return this;
    }

    /**
     * @param noiseThreshold Noise threshold to determine whether or not a given block will be dug out
     */
    public CavernCarverBuilder noiseThreshold(float noiseThreshold) {
        settings.setNoiseThreshold(noiseThreshold);
        return this;
    }

    /**
     * @param vBlock Block used for this cave type in the debug visualizer
     */
    public CavernCarverBuilder debugVisualizerBlock(BlockState vBlock) {
        settings.setDebugBlock(vBlock);
        return this;
    }

    /**
     * @param liquidAltitude altitude at and below which air is replaced with liquid
     */
    public CavernCarverBuilder liquidAltitude(int liquidAltitude) {
        settings.setLiquidAltitude(liquidAltitude);
        return this;
    }

    /**
     * Enable the debug visualizer
     */
    public CavernCarverBuilder enableDebugVisualizer(boolean enableDebugVisualizer) {
        settings.setEnableDebugVisualizer(enableDebugVisualizer);
        return this;
    }

    /**
     * Set cavern type
     */
    public CavernCarverBuilder cavernType(CavernType cavernType) {
        this.cavernType = cavernType;
        return this;
    }

    /**
     * Set cavern bottom Y coordinate
     */
    public CavernCarverBuilder bottomY(int bottomY) {
        this.bottomY = bottomY;
        return  this;
    }

    /**
     * Set cavern top Y coordinate
     */
    public CavernCarverBuilder topY(int topY) {
        this.topY = topY;
        return this;
    }

    /* ================================== Builder Getters ================================== */

    public CarverSettings getSettings() {
        return settings;
    }

    public CavernType getCavernType() {
        return cavernType;
    }

    public int getBottomY() {
        return bottomY;
    }

    public int getTopY() {
        return topY;
    }
}
