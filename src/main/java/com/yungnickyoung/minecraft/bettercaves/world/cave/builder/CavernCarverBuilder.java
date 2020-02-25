package com.yungnickyoung.minecraft.bettercaves.world.cave.builder;


import com.yungnickyoung.minecraft.bettercaves.config.ConfigHolder;
import com.yungnickyoung.minecraft.bettercaves.enums.CavernType;
import com.yungnickyoung.minecraft.bettercaves.noise.FastNoise;
import com.yungnickyoung.minecraft.bettercaves.world.cave.CarverSettings;
import com.yungnickyoung.minecraft.bettercaves.world.cave.CavernCarver;
import net.minecraft.block.state.IBlockState;
import net.minecraft.world.World;

/**
 * Builder class for CavernCarver.
 * Fields may be built individually or loaded in bulk via the {@code ofTypeFromCarver} method
 */
public class CavernCarverBuilder {
    private CarverSettings settings;
    private CavernType cavernType;

    public CavernCarverBuilder(World world) {
        settings = new CarverSettings(world);
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
        this.settings.setLiquidAltitude(config.liquidAltitude.get());
        this.settings.setReplaceFloatingGravel(config.replaceFloatingGravel.get());
        this.settings.setEnableTurbulence(false);
        this.settings.getNoiseSettings().setFractalType(FastNoise.FractalType.RigidMulti);
        this.settings.setEnableDebugVisualizer(config.debugVisualizer.get());
        this.cavernType = cavernType;
        switch (cavernType) {
            case LAVA:
                this.settings.setNoiseThreshold(config.lavaCavernNoiseThreshold.get());
                this.settings.getNoiseSettings().setNoiseType(config.lavaCavernNoiseType.get());
                this.settings.getNoiseSettings().setOctaves(config.lavaCavernFractalOctaves.get());
                this.settings.getNoiseSettings().setGain(config.lavaCavernFractalGain.get());
                this.settings.getNoiseSettings().setFrequency(config.lavaCavernFractalFrequency.get());
                this.settings.setNumGens(config.lavaCavernNumGenerators.get());
                this.settings.setyCompression(config.lavaCavernYCompression.get());
                this.settings.setXzCompression(config.lavaCavernXZCompression.get());
                break;
            case FLOORED:
                this.settings.setNoiseThreshold(config.flooredCavernNoiseThreshold.get());
                this.settings.getNoiseSettings().setNoiseType(config.flooredCavernNoiseType.get());
                this.settings.getNoiseSettings().setOctaves(config.flooredCavernFractalOctaves.get());
                this.settings.getNoiseSettings().setGain(config.flooredCavernFractalGain.get());
                this.settings.getNoiseSettings().setFrequency(config.flooredCavernFractalFrequency.get());
                this.settings.setNumGens(config.flooredCavernNumGenerators.get());
                this.settings.setyCompression(config.flooredCavernYCompression.get());
                this.settings.setXzCompression(config.flooredCavernXZCompression.get());
                break;
            case WATER:
                this.settings.setNoiseThreshold(config.waterCavernNoiseThreshold.get());
                this.settings.getNoiseSettings().setNoiseType(config.waterCavernNoiseType.get());
                this.settings.getNoiseSettings().setOctaves(config.waterCavernFractalOctaves.get());
                this.settings.getNoiseSettings().setGain(config.waterCavernFractalGain.get());
                this.settings.getNoiseSettings().setFrequency(config.waterCavernFractalFrequency.get());
                this.settings.setNumGens(config.waterCavernNumGenerators.get());
                this.settings.setyCompression(config.waterCavernYCompression.get());
                this.settings.setXzCompression(config.waterCavernXZCompression.get());
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
    public CavernCarverBuilder debugVisualizerBlock(IBlockState vBlock) {
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

    /* ================================== Builder Getters ================================== */

    public CarverSettings getSettings() {
        return settings;
    }

    public CavernType getCavernType() {
        return cavernType;
    }
}
