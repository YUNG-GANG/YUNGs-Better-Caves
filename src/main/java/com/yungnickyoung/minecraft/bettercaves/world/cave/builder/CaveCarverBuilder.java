package com.yungnickyoung.minecraft.bettercaves.world.cave.builder;

import com.yungnickyoung.minecraft.bettercaves.config.ConfigHolder;
import com.yungnickyoung.minecraft.bettercaves.enums.CaveType;
import com.yungnickyoung.minecraft.bettercaves.noise.FastNoise;
import com.yungnickyoung.minecraft.bettercaves.world.cave.CarverSettings;
import com.yungnickyoung.minecraft.bettercaves.world.cave.CaveCarver;
import net.minecraft.block.state.IBlockState;
import net.minecraft.world.World;

/**
 * Builder class for CaveCarver.
 * Fields may be built individually or loaded in bulk via the {@code ofTypeFromCarver} method
 */
public class CaveCarverBuilder {
    private CarverSettings settings;
    private int surfaceCutoff;
    private boolean enableYAdjust;
    private float yAdjustF1;
    private float yAdjustF2;

    public CaveCarverBuilder(World world) {
        settings = new CarverSettings(world);
    }

    public CaveCarver build() {
        return new CaveCarver(this);
    }

    /**
     * Helps build a CaveCarver from a ConfigHolder based on its CaveType
     * @param caveType the CaveType of this CaveCarver
     * @param config the config
     */
    public CaveCarverBuilder ofTypeFromConfig(CaveType caveType, ConfigHolder config) {
        this.settings.setLiquidAltitude(config.liquidAltitude.get());
        this.settings.setReplaceFloatingGravel(config.replaceFloatingGravel.get());
        this.settings.setEnableDebugVisualizer(config.debugVisualizer.get());
        this.settings.getNoiseSettings().setFractalType(FastNoise.FractalType.RigidMulti);
        this.settings.getTurbulenceSettings().setNoiseType(FastNoise.NoiseType.Simplex);
        this.settings.getTurbulenceSettings().setFractalType(FastNoise.FractalType.FBM);
        this.surfaceCutoff = config.surfaceCutoff.get();
        switch (caveType) {
            case CUBIC:
                this.settings.setNoiseThreshold(config.cubicCaveNoiseThreshold.get());
                this.settings.getNoiseSettings().setNoiseType(config.cubicCaveNoiseType.get());
                this.settings.getNoiseSettings().setOctaves(config.cubicCaveFractalOctaves.get());
                this.settings.getNoiseSettings().setGain(config.cubicCaveFractalGain.get());
                this.settings.getNoiseSettings().setFrequency(config.cubicCaveFractalFrequency.get());
                this.settings.setEnableTurbulence(config.cubicCaveEnableTurbulence.get());
                this.settings.getTurbulenceSettings().setOctaves(config.cubicCaveTurbulenceOctaves.get());
                this.settings.getTurbulenceSettings().setGain(config.cubicCaveTurbulenceGain.get());
                this.settings.getTurbulenceSettings().setFrequency(config.cubicCaveTurbulenceFrequency.get());
                this.settings.setNumGens(config.cubicCaveNumGenerators.get());
                this.settings.setXzCompression(config.cubicCaveXZCompression.get());
                this.settings.setyCompression(config.cubicCaveYCompression.get());
                this.enableYAdjust = config.cubicCaveEnableVerticalAdjustment.get();
                this.yAdjustF1 = config.cubicCaveYAdjustF1.get();
                this.yAdjustF2 = config.cubicCaveYAdjustF2.get();
                break;
            case SIMPLEX:
                this.settings.setNoiseThreshold(config.simplexCaveNoiseThreshold.get());
                this.settings.getNoiseSettings().setNoiseType(config.simplexCaveNoiseType.get());
                this.settings.getNoiseSettings().setOctaves(config.simplexCaveFractalOctaves.get());
                this.settings.getNoiseSettings().setGain(config.simplexCaveFractalGain.get());
                this.settings.getNoiseSettings().setFrequency(config.simplexCaveFractalFrequency.get());
                this.settings.setEnableTurbulence(config.simplexCaveEnableTurbulence.get());
                this.settings.getTurbulenceSettings().setOctaves(config.simplexCaveTurbulenceOctaves.get());
                this.settings.getTurbulenceSettings().setGain(config.simplexCaveTurbulenceGain.get());
                this.settings.getTurbulenceSettings().setFrequency(config.simplexCaveTurbulenceFrequency.get());
                this.settings.setNumGens(config.simplexCaveNumGenerators.get());
                this.settings.setXzCompression(config.simplexCaveXZCompression.get());
                this.settings.setyCompression(config.simplexCaveYCompression.get());
                this.enableYAdjust = config.simplexCaveEnableVerticalAdjustment.get();
                this.yAdjustF1 = config.simplexCaveYAdjustF1.get();
                this.yAdjustF2 = config.simplexCaveYAdjustF2.get();
                break;
        }
        return this;
    }

    /* ================================== Builder Setters ================================== */
    /**
     * @param noiseType The type of noise this carver will use
     */
    public CaveCarverBuilder noiseType(FastNoise.NoiseType noiseType) {
        settings.getNoiseSettings().setNoiseType(noiseType);
        return this;
    }

    /**
     * @param fractalOctaves Number of fractal octaves to use in ridged multifractal noise generation
     */
    public CaveCarverBuilder fractalOctaves(int fractalOctaves) {
        settings.getNoiseSettings().setOctaves(fractalOctaves);
        return this;
    }

    /**
     * @param fractalGain Amount of gain to use in ridged multifractal noise generation
     */
    public CaveCarverBuilder fractalGain(float fractalGain) {
        settings.getNoiseSettings().setGain(fractalGain);
        return this;
    }

    /**
     * @param fractalFreq Frequency to use in ridged multifractal noise generation
     */
    public CaveCarverBuilder fractalFrequency(float fractalFreq) {
        settings.getNoiseSettings().setFrequency(fractalFreq);
        return this;
    }

    /**
     * @param numGens Number of noise values to calculate for a given block
     */
    public CaveCarverBuilder numberOfGenerators(int numGens) {
        settings.setNumGens(numGens);
        return this;
    }

    /**
     * @param turbOctaves Number of octaves in turbulence function
     */
    public CaveCarverBuilder turbulenceOctaves(int turbOctaves) {
        settings.getTurbulenceSettings().setOctaves(turbOctaves);
        return this;
    }

    /**
     * @param turbGain Gain of turbulence function
     */
    public CaveCarverBuilder turbulenceGain(float turbGain) {
        settings.getTurbulenceSettings().setGain(turbGain);
        return this;
    }

    /**
     * @param turbFreq Frequency of turbulence function
     */
    public CaveCarverBuilder turbulenceFrequency(float turbFreq) {
        settings.getTurbulenceSettings().setFrequency(turbFreq);
        return this;
    }

    /**
     * Enable turbulence (adds performance overhead, generally not worth it).
     * If not enabled then other turbulence parameters don't matter and are not used.
     */
    public CaveCarverBuilder enableTurbulence(boolean enableTurbulence) {
        settings.setEnableTurbulence(enableTurbulence);
        return this;
    }

    /**
     * @param yCompression Vertical cave gen compression. Use 1.0 for default generation
     */
    public CaveCarverBuilder verticalCompression(float yCompression) {
        settings.setyCompression(yCompression);
        return this;
    }

    /**
     * @param xzCompression Horizontal cave gen compression. Use 1.0 for default generation
     */
    public CaveCarverBuilder horizontalCompression(float xzCompression) {
        settings.setXzCompression(xzCompression);
        return this;
    }

    /**
     * @param yAdjustF1 Adjustment value for the block immediately above. Must be between 0 and 1.0
     */
    public CaveCarverBuilder verticalAdjuster1(float yAdjustF1) {
        this.yAdjustF1 = yAdjustF1;
        return this;
    }

    /**
     * @param yAdjustF2 Adjustment value for the block two blocks above. Must be between 0 and 1.0
     */
    public CaveCarverBuilder verticalAdjuster2(float yAdjustF2) {
        this.yAdjustF2 = yAdjustF2;
        return this;
    }

    /**
     * @param enableYAdjust Whether or not to adjust/increase the height of caves.
     */
    public CaveCarverBuilder enableVerticalAdjustment(boolean enableYAdjust) {
        this.enableYAdjust = enableYAdjust;
        return this;
    }

    /**
     * @param noiseThreshold Noise threshold to determine whether or not a given block will be dug out
     */
    public CaveCarverBuilder noiseThreshold(float noiseThreshold) {
        settings.setNoiseThreshold(noiseThreshold);
        return this;
    }

    /**
     * @param vBlock Block used for this cave type in the debug visualizer
     */
    public CaveCarverBuilder debugVisualizerBlock(IBlockState vBlock) {
        settings.setDebugBlock(vBlock);
        return this;
    }

    /**
     * @param liquidAltitude altitude at and below which air is replaced with liquid
     */
    public CaveCarverBuilder liquidAltitude(int liquidAltitude) {
        settings.setLiquidAltitude(liquidAltitude);
        return this;
    }

    /**
     * Enable the debug visualizer
     */
    public CaveCarverBuilder enableDebugVisualizer(boolean enableDebugVisualizer) {
        settings.setEnableDebugVisualizer(enableDebugVisualizer);
        return this;
    }

    /* ================================== Builder Getters ================================== */

    public CarverSettings getSettings() {
        return settings;
    }

    public int getSurfaceCutoff() {
        return surfaceCutoff;
    }

    public boolean isEnableYAdjust() {
        return enableYAdjust;
    }

    public float getyAdjustF1() {
        return yAdjustF1;
    }

    public float getyAdjustF2() {
        return yAdjustF2;
    }
}

