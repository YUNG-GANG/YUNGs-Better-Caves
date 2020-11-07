package com.yungnickyoung.minecraft.bettercaves.world.carver;

import com.yungnickyoung.minecraft.bettercaves.noise.NoiseSettings;
import net.minecraft.world.level.block.state.BlockState;

public class CarverSettings {
    private long  seed;
    private int   priority;

    /* ============================== Values determined through config ============================== */
    /* ------------- Ridged Multifractal Params ------------- */
    private NoiseSettings noiseSettings = new NoiseSettings();
    private boolean       isFastNoise; // True if using the FastNoise library; false if using OpenSimplex2S
    private int           numGens; // Number of noise values to generate per iteration (block, sub-chunk, etc)

    /* -------------- Noise Processing Params -------------- */
    private float yCompression;   // Vertical cave gen compression
    private float xzCompression;  // Horizontal cave gen compression
    private float noiseThreshold; // Noise threshold for determining whether or not a block gets dug out

    /* ------------------ Worldgen Params ------------------ */
    private int     liquidAltitude;
    private boolean replaceFloatingGravel;

    /* -------------------- Debug Params ------------------- */
    private BlockState debugBlock;             // Block used to represent this cave/cavern type in the debug visualizer
    private boolean    enableDebugVisualizer;  // Set true to enable debug visualization for this carver

    public CarverSettings(long seed) {
        this.seed = seed;
    }

    public long getSeed() {
        return seed;
    }

    public void setSeed(long seed) {
        this.seed = seed;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public NoiseSettings getNoiseSettings() {
        return noiseSettings;
    }

    public void setNoiseSettings(NoiseSettings noiseSettings) {
        this.noiseSettings = noiseSettings;
    }

    public boolean isFastNoise() {
        return isFastNoise;
    }

    public void setFastNoise(boolean fastNoise) {
        isFastNoise = fastNoise;
    }

    public int getNumGens() {
        return numGens;
    }

    public void setNumGens(int numGens) {
        this.numGens = numGens;
    }

    public float getyCompression() {
        return yCompression;
    }

    public void setyCompression(float yCompression) {
        this.yCompression = yCompression;
    }

    public float getXzCompression() {
        return xzCompression;
    }

    public void setXzCompression(float xzCompression) {
        this.xzCompression = xzCompression;
    }

    public float getNoiseThreshold() {
        return noiseThreshold;
    }

    public void setNoiseThreshold(float noiseThreshold) {
        this.noiseThreshold = noiseThreshold;
    }

    public int getLiquidAltitude() {
        return liquidAltitude;
    }

    public void setLiquidAltitude(int liquidAltitude) {
        this.liquidAltitude = liquidAltitude;
    }

    public boolean isReplaceFloatingGravel() {
        return replaceFloatingGravel;
    }

    public void setReplaceFloatingGravel(boolean replaceFloatingGravel) {
        this.replaceFloatingGravel = replaceFloatingGravel;
    }

    public BlockState getDebugBlock() {
        return debugBlock;
    }

    public void setDebugBlock(BlockState debugBlock) {
        this.debugBlock = debugBlock;
    }

    public boolean isEnableDebugVisualizer() {
        return enableDebugVisualizer;
    }

    public void setEnableDebugVisualizer(boolean enableDebugVisualizer) {
        this.enableDebugVisualizer = enableDebugVisualizer;
    }
}