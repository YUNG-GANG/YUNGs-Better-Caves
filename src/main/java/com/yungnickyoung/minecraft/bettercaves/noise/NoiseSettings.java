package com.yungnickyoung.minecraft.bettercaves.noise;

public class NoiseSettings {
    public FastNoise.NoiseType   noiseType   = FastNoise.NoiseType.SimplexFractal;
    public FastNoise.FractalType fractalType = FastNoise.FractalType.FBM;
    public int   octaves   = 3;
    public float gain      = 0.5f;
    public float frequency = 0.01f;

    public NoiseSettings() {
    }

    public NoiseSettings setNoiseType(FastNoise.NoiseType noiseType) {
        this.noiseType = noiseType;
        return this;
    }

    public NoiseSettings setFractalType(FastNoise.FractalType fractalType) {
        this.fractalType = fractalType;
        return this;
    }

    public NoiseSettings setOctaves(int octaves) {
        this.octaves = octaves;
        return this;
    }

    public NoiseSettings setGain(float gain) {
        this.gain = gain;
        return this;
    }

    public NoiseSettings setFrequency(float frequency) {
        this.frequency = frequency;
        return this;
    }
}
