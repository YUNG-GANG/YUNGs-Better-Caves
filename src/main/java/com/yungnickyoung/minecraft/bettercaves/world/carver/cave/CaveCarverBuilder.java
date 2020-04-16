package com.yungnickyoung.minecraft.bettercaves.world.carver.cave;

import com.yungnickyoung.minecraft.bettercaves.config.BetterCavesConfig;
import com.yungnickyoung.minecraft.bettercaves.config.ConfigHolder;
import com.yungnickyoung.minecraft.bettercaves.enums.CaveType;
import com.yungnickyoung.minecraft.bettercaves.noise.FastNoise;
import com.yungnickyoung.minecraft.bettercaves.world.carver.CarverSettings;
import net.minecraft.block.BlockState;

/**
 * Builder class for CaveCarver.
 * Fields may be built individually or loaded in bulk via the {@code ofTypeFromCarver} method
 */
public class CaveCarverBuilder {
    private CarverSettings settings;
    private int surfaceCutoff;
    private int bottomY;
    private int topY;
    private boolean enableYAdjust;
    private float yAdjustF1;
    private float yAdjustF2;

    public CaveCarverBuilder(long seed) {
        settings = new CarverSettings(seed);
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
        this.settings.setLiquidAltitude(BetterCavesConfig.liquidAltitude);
        this.settings.setReplaceFloatingGravel(BetterCavesConfig.replaceFloatingGravel);
        this.settings.setEnableDebugVisualizer(BetterCavesConfig.enableDebugVisualizer);
        this.settings.getNoiseSettings().setFractalType(FastNoise.FractalType.RigidMulti);
        switch (caveType) {
            case CUBIC:
                this.settings.setFastNoise(true);
                this.settings.setNoiseThreshold(BetterCavesConfig.cubicNoiseThreshold);
                this.settings.getNoiseSettings().setNoiseType(FastNoise.NoiseType.CubicFractal); // TODO - split into config option
                this.settings.getNoiseSettings().setOctaves(BetterCavesConfig.cubicFractalOctaves);
                this.settings.getNoiseSettings().setGain(BetterCavesConfig.cubicFractalGain);
                this.settings.getNoiseSettings().setFrequency(BetterCavesConfig.cubicFractalFreq);
                this.settings.setNumGens(BetterCavesConfig.cubicNumGenerators);
                this.settings.setXzCompression((float)BetterCavesConfig.cubicXZComp);
                this.settings.setyCompression((float)BetterCavesConfig.cubicYComp);
                this.settings.setPriority(10);                                                  // TODO
                this.surfaceCutoff = BetterCavesConfig.surfaceCutoff;                           // TODO - separate by cave
                this.bottomY = BetterCavesConfig.cubicCaveBottom;
                this.topY = BetterCavesConfig.maxCaveAltitude;                                  // TODO - separate by cave
                this.enableYAdjust = BetterCavesConfig.cubicYAdjust;
                this.yAdjustF1 = BetterCavesConfig.cubicYAdjustF1;
                this.yAdjustF2 = BetterCavesConfig.cubicYAdjustF2;
                break;
            case SIMPLEX:
                this.settings.setFastNoise(false);
                this.settings.setNoiseThreshold(BetterCavesConfig.simplexNoiseThreshold);
                this.settings.getNoiseSettings().setNoiseType(FastNoise.NoiseType.SimplexFractal); // TODO - split into config option
                this.settings.getNoiseSettings().setOctaves(BetterCavesConfig.simplexFractalOctaves);
                this.settings.getNoiseSettings().setGain(BetterCavesConfig.simplexFractalGain);
                this.settings.getNoiseSettings().setFrequency(BetterCavesConfig.simplexFractalFreq);
                this.settings.setNumGens(BetterCavesConfig.simplexNumGenerators);
                this.settings.setXzCompression((float)BetterCavesConfig.simplexXZComp);
                this.settings.setyCompression((float)BetterCavesConfig.simplexYComp);
                this.settings.setPriority(10);                                                  // TODO
                this.surfaceCutoff = BetterCavesConfig.surfaceCutoff;                           // TODO - separate by cave
                this.bottomY = BetterCavesConfig.simplexCaveBottom;
                this.topY = BetterCavesConfig.maxCaveAltitude;                                  // TODO - separate by cave
                this.enableYAdjust = BetterCavesConfig.simplexYAdjust;
                this.yAdjustF1 = BetterCavesConfig.simplexYAdjustF1;
                this.yAdjustF2 = BetterCavesConfig.simplexYAdjustF2;
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
     * @param surfaceCutoff Cave surface cutoff depth
     */
    public CaveCarverBuilder surfaceCutoff(int surfaceCutoff) {
        this.surfaceCutoff = surfaceCutoff;
        return this;
    }

    /**
     * @param bottomY Cave bottom y-coordinate
     */
    public CaveCarverBuilder bottomY(int bottomY) {
        this.bottomY = bottomY;
        return this;
    }

    /**
     * @param topY Cave top y-coordinate
     */
    public CaveCarverBuilder topY(int topY) {
        this.topY = topY;
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
    public CaveCarverBuilder debugVisualizerBlock(BlockState vBlock) {
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

    public int getBottomY() {
        return bottomY;
    }

    public int getTopY() {
        return topY;
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