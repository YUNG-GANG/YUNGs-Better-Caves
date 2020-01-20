package com.yungnickyoung.minecraft.bettercaves.noise;

import java.util.ArrayList;
import java.util.List;

/**
 * An n-tuple of noise values represented as floats.
 * The value of n may change dynamically and differ by instance.
 * This class supports basic put, get, and set operations.
 */
public class NoiseTuple {
    private List<Float> noiseValues = new ArrayList<>();
    private int length = 0;

    /**
     *
     * @param vals Variable-length array of float values to initialize the tuple with
     */
    public NoiseTuple(float... vals) {
        for (float val : vals) {
            noiseValues.add(val);
            length++;
        }
    }

    /**
     * Appends the specified noise value to the end of this tuple.
     * @param val Noise value to be appended to this tuple.
     */
    public void put(float val) {
        noiseValues.add(val);
        length++;
    }

    /**
     * Retrieves the noise value at the specified index
     * @param index The index of the desired float value
     * @return The float value of the noise at the given index
     * @throws IndexOutOfBoundsException if the index is out of this tuple's bounds
     */
    public float get(int index) throws IndexOutOfBoundsException {
        if (index < 0 || index >= length)
            throw new IndexOutOfBoundsException("No corresponding noise value in Noise Tuple for index: " + index);

        return noiseValues.get(index);
    }

    /**
     * Overwrites the noise value at the specified index. A value must already exist at the index.
     * @param index The index of the noise value to overwrite
     * @param newValue The new noise value
     * @throws IndexOutOfBoundsException if the index is out of this tuple's bounds
     */
    public void set(int index, float newValue) throws IndexOutOfBoundsException {
        if (index < 0 || index >= length)
            throw new IndexOutOfBoundsException("No corresponding noise value in Noise Tuple for index: " + index);

        noiseValues.set(index, newValue);
    }

    public NoiseTuple times(float magnitude) {
        NoiseTuple result = new NoiseTuple();
        for (int i = 0; i < length; i++) {
            result.put(noiseValues.get(i) * magnitude);
        }
        return result;
    }

    public NoiseTuple plus(NoiseTuple other) {
        NoiseTuple result = new NoiseTuple();
        for (int i = 0; i < length; i++) {
            result.put(noiseValues.get(i) + other.get(i));
        }
        return result;
    }

    /**
     * Retrieves all the noise values of this tuple as a List of Floats
     * @return {@code List<Float>} of all noise values in this tuple
     */
    public List<Float> getNoiseValues() {
        return noiseValues;
    }

    /**
     * @return the number of noise values stored in this tuple
     */
    public int size() {
        return length;
    }
}
