package com.yungnickyoung.minecraft.bettercaves.worldgen.carver;

import com.yungnickyoung.minecraft.bettercaves.noise.NoiseSettings;
import com.yungnickyoung.minecraft.bettercaves.worldgen.liquidregion.LiquidRegions;
import com.yungnickyoung.minecraft.bettercaves.worldgen.liquidregion.LiquidRegionsController;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;

public class CarverSettings {
    private final long seed;

    /* ------------- Ridged Multifractal Params ------------- */
    private final NoiseSettings noiseSettings = new NoiseSettings();
    private boolean isFastNoise; // True if using the FastNoise library; false if using OpenSimplex2S
    private int numGens; // Number of noise values to generate per iteration (block, sub-chunk, etc)

    /* -------------- Noise Processing Params -------------- */
    private float yCompression;   // Vertical cave gen compression
    private float xzCompression;  // Horizontal cave gen compression
    private float noiseThreshold; // Noise threshold for determining whether a block gets dug out

    /* ------------------ Worldgen Params ------------------ */
    private final int liquidAltitude;
    private int spawnWeight;

    /* -------------------- Debug Params ------------------- */
    private BlockState debugBlock;          // Block used to represent this cave/cavern type in the debug visualizer
    private boolean enableDebugVisualizer;  // Set true to enable debug visualization for this carver

    public CarverSettings(ServerLevel serverLevel) {
        this.seed = serverLevel.getSeed();
        if (LiquidRegionsController.getInstance().hasSettingsForDimension(serverLevel.dimension().location())) {
            this.liquidAltitude = LiquidRegionsController.getInstance().getSettingsForDimension(serverLevel.dimension().location()).liquidAltitude();
        } else {
            this.liquidAltitude = LiquidRegions.DEFAULT_ALTITUDE;
        }
    }

    public long getSeed() {
        return seed;
    }

    public int getSpawnWeight() {
        return spawnWeight;
    }

    public void setSpawnWeight(int spawnWeight) {
        this.spawnWeight = spawnWeight;
    }

    public NoiseSettings getNoiseSettings() {
        return noiseSettings;
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

    public float getYCompression() {
        return yCompression;
    }

    public void setYCompression(float yCompression) {
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
