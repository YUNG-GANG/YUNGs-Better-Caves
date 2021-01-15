package com.yungnickyoung.minecraft.bettercaves.noise;

import com.yungnickyoung.minecraft.yungsapi.noise.FastNoise;

public class NoiseSettings {
    private FastNoise.NoiseType   noiseType   = FastNoise.NoiseType.SimplexFractal;
    private FastNoise.FractalType fractalType = FastNoise.FractalType.FBM;
    private int   octaves   = 3;
    private float gain      = 0.5f;
    private float frequency = 0.01f;

    public NoiseSettings() {
    }

    /** GETTERS **/

    public FastNoise.NoiseType getNoiseType() {
        return noiseType;
    }

    public FastNoise.FractalType getFractalType() {
        return fractalType;
    }

    public int getOctaves() {
        return octaves;
    }

    public float getGain() {
        return gain;
    }

    public float getFrequency() {
        return frequency;
    }

    /** SETTERS **/

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