package com.yungnickyoung.minecraft.bettercaves.world.cave.builder;

import com.yungnickyoung.minecraft.bettercaves.noise.FastNoise;
import com.yungnickyoung.minecraft.bettercaves.world.cave.UndergroundCarver;
import net.minecraft.block.state.IBlockState;
import net.minecraft.world.World;

/**
 * Builder class for UndergroundCarver.
 */
public class UndergroundCarverBuilder {
    protected World world;
    protected long seed;
    protected FastNoise.NoiseType noiseType;

    /* ------------------- Fractal Params ------------------ */
    protected int fractalOctaves;
    protected float fractalGain;
    protected float fractalFreq;
    protected int numGens;

    /* ----------------- Turbulence Params ----------------- */
    protected int turbOctaves;
    protected float turbGain;
    protected float turbFreq;
    protected boolean enableTurbulence = false;

    /* -------------- Noise Processing Params -------------- */
    protected float yCompression;
    protected float xzCompression;
    protected float yAdjustF1;
    protected float yAdjustF2;
    protected float noiseThreshold;
    protected boolean enableYAdjust;

    /* ------------------ Worldgen Params ------------------ */
    protected int liquidAltitude;

    /* -------------------- Debug Params ------------------- */
    protected IBlockState debugBlock;
    protected boolean enableDebugVisualizer = false;

    public UndergroundCarverBuilder(World world) {
        this.world = world;
        this.seed = world.getSeed();
    }

    public UndergroundCarver build() {
        return new UndergroundCarver(this);
    }

    /* ================================== Builder Setters ================================== */
    /**
     * @param noiseType The type of noise this carver will use
     */
    public UndergroundCarverBuilder noiseType(FastNoise.NoiseType noiseType) {
        this.noiseType = noiseType;
        return this;
    }

    /**
     * @param fractalOctaves Number of fractal octaves to use in ridged multifractal noise generation
     */
    public UndergroundCarverBuilder fractalOctaves(int fractalOctaves) {
        this.fractalOctaves = fractalOctaves;
        return this;
    }

    /**
     * @param fractalGain Amount of gain to use in ridged multifractal noise generation
     */
    public UndergroundCarverBuilder fractalGain(float fractalGain) {
        this.fractalGain = fractalGain;
        return this;
    }

    /**
     * @param fractalFreq Frequency to use in ridged multifractal noise generation
     */
    public UndergroundCarverBuilder fractalFrequency(float fractalFreq) {
        this.fractalFreq = fractalFreq;
        return this;
    }

    /**
     * @param numGens Number of noise values to calculate for a given block
     */
    public UndergroundCarverBuilder numberOfGenerators(int numGens) {
        this.numGens = numGens;
        return this;
    }

    /**
     * @param turbOctaves Number of octaves in turbulence function
     */
    public UndergroundCarverBuilder turbulenceOctaves(int turbOctaves) {
        this.turbOctaves = turbOctaves;
        return this;
    }

    /**
     * @param turbGain Gain of turbulence function
     */
    public UndergroundCarverBuilder turbulenceGain(float turbGain) {
        this.turbGain = turbGain;
        return this;
    }

    /**
     * @param turbFreq Frequency of turbulence function
     */
    public UndergroundCarverBuilder turbulenceFrequency(float turbFreq) {
        this.turbFreq = turbFreq;
        return this;
    }

    /**
     * Enable turbulence (adds performance overhead, generally not worth it).
     * If not enabled then other turbulence parameters don't matter and are not used.
     */
    public UndergroundCarverBuilder enableTurbulence(boolean enableTurbulence) {
        this.enableTurbulence = enableTurbulence;
        return this;
    }

    /**
     * @param yCompression Vertical cave gen compression. Use 1.0 for default generation
     */
    public UndergroundCarverBuilder verticalCompression(float yCompression) {
        this.yCompression = yCompression;
        return this;
    }

    /**
     * @param xzCompression Horizontal cave gen compression. Use 1.0 for default generation
     */
    public UndergroundCarverBuilder horizontalCompression(float xzCompression) {
        this.xzCompression = xzCompression;
        return this;
    }

    /**
     * @param yAdjustF1 Adjustment value for the block immediately above. Must be between 0 and 1.0
     */
    public UndergroundCarverBuilder verticalAdjuster1(float yAdjustF1) {
        this.yAdjustF1 = yAdjustF1;
        return this;
    }

    /**
     * @param yAdjustF2 Adjustment value for the block two blocks above. Must be between 0 and 1.0
     */
    public UndergroundCarverBuilder verticalAdjuster2(float yAdjustF2) {
        this.yAdjustF2 = yAdjustF2;
        return this;
    }

    /**
     * @param enableYAdjust Whether or not to adjust/increase the height of caves.
     */
    public UndergroundCarverBuilder enableVerticalAdjustment(boolean enableYAdjust) {
        this.enableYAdjust = enableYAdjust;
        return this;
    }

    /**
     * @param noiseThreshold Noise threshold to determine whether or not a given block will be dug out
     */
    public UndergroundCarverBuilder noiseThreshold(float noiseThreshold) {
        this.noiseThreshold = noiseThreshold;
        return this;
    }

    /**
     * @param vBlock Block used for this cave type in the debug visualizer
     */
    public UndergroundCarverBuilder debugVisualizerBlock(IBlockState vBlock) {
        this.debugBlock = vBlock;
        return this;
    }

    /**
     * @param liquidAltitude altitude at and below which air is replaced with liquid
     */
    public UndergroundCarverBuilder liquidAltitude(int liquidAltitude) {
        this.liquidAltitude = liquidAltitude;
        return this;
    }

    /**
     * Enable the debug visualizer
     */
    public UndergroundCarverBuilder enableDebugVisualizer(boolean enableDebugVisualizer) {
        this.enableDebugVisualizer = enableDebugVisualizer;
        return this;
    }

    /* ================================== Public Getters ================================== */
    public World getWorld() {
        return world;
    }

    public long getSeed() {
        return seed;
    }

    public FastNoise.NoiseType getNoiseType() {
        return noiseType;
    }

    public int getFractalOctaves() {
        return fractalOctaves;
    }

    public float getFractalGain() {
        return fractalGain;
    }

    public float getFractalFreq() {
        return fractalFreq;
    }

    public int getNumGens() {
        return numGens;
    }

    public int getTurbOctaves() {
        return turbOctaves;
    }

    public float getTurbGain() {
        return turbGain;
    }

    public float getTurbFreq() {
        return turbFreq;
    }

    public boolean isEnableTurbulence() {
        return enableTurbulence;
    }

    public float getyCompression() {
        return yCompression;
    }

    public float getXzCompression() {
        return xzCompression;
    }

    public float getyAdjustF1() {
        return yAdjustF1;
    }

    public float getyAdjustF2() {
        return yAdjustF2;
    }

    public float getNoiseThreshold() {
        return noiseThreshold;
    }

    public boolean isEnableYAdjust() {
        return enableYAdjust;
    }

    public int getLiquidAltitude() {
        return liquidAltitude;
    }

    public IBlockState getDebugBlock() {
        return debugBlock;
    }

    public boolean isEnableDebugVisualizer() {
        return enableDebugVisualizer;
    }
}
