package com.yungnickyoung.minecraft.bettercaves.world.carver;

import com.yungnickyoung.minecraft.bettercaves.noise.NoiseUtils;

/**
 * Couples the bounds of a particular range of noise values with a NoiseCube and carver.
 * Having these pieces of information in one place is kind of bad practice, but proves to be very
 * convenient in the Carver Controllers.
 *
 * The smooth cutoff variables are currently only used by the CavernCarverController, since
 * caves currently do not have boundary smoothing.
 */
public class CarverNoiseRange {
    // Bounds of a range of noise values
    private float bottom, top;

    // Thresholds marking the boundaries for the bottom and top smoothing ranges.
    // Currently only used for caverns.
    private float smoothBottomCutoff, smoothTopCutoff;

    // The carver associated with this range of noises.
    private ICarver carver;

    // The cube of noise values associated with this range of noises.
    private double[][][][] noiseCube;

    // The degree of smoothing on cavern edges. For a given SMOOTH_PERCENT x, both the
    // bottom and top ends of the noise range are each smoothed by (x * 100) percent.
    private static final float SMOOTH_PERCENT = .3f;

    public CarverNoiseRange(float bottom, float top, ICarver carver) {
        this.bottom = bottom;
        this.top = top;
        float smoothRangePercent = getPercentLength() * SMOOTH_PERCENT;
        this.smoothBottomCutoff = NoiseUtils.simplexNoiseOffsetByPercent(bottom, smoothRangePercent);
        this.smoothTopCutoff = NoiseUtils.simplexNoiseNegativeOffsetByPercent(top, smoothRangePercent);
        this.carver = carver;
        this.noiseCube = null;
    }

    public boolean contains(float noiseValue) {
        return bottom <= noiseValue && noiseValue < top;
    }

    public float getSmoothAmp(float noiseValue) {
        if (bottom <= noiseValue && noiseValue <= smoothBottomCutoff) {
            return (noiseValue - bottom) / (smoothBottomCutoff - bottom);
        }
        else if (smoothTopCutoff <= noiseValue && noiseValue < top) {
            return (noiseValue - top) / (smoothTopCutoff - top);
        }
        return 1;
    }

    public float getPercentLength() {
        return (top == 1 ? 1 : NoiseUtils.noiseToCDF(top)) - (bottom == -1 ? 0 : NoiseUtils.noiseToCDF(bottom));
    }

    public ICarver getCarver() {
        return carver;
    }

    public double[][][][] getNoiseCube() {
        return noiseCube;
    }

    public void setNoiseCube(double[][][][] noiseCube) {
        this.noiseCube = noiseCube;
    }

    @Override
    public String toString() {
        return String.format("[%2.2f, %2.2f] (%2.4f%%) -- smooth cutoffs: [%2.2f, %2.2f]", bottom, top, getPercentLength(), smoothBottomCutoff, smoothTopCutoff);
    }
}
