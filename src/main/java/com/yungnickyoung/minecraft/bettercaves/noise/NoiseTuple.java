package com.yungnickyoung.minecraft.bettercaves.noise;

import java.util.ArrayList;
import java.util.List;

public class NoiseTuple {
    private List<Float> noiseValues = new ArrayList<>();
    private int length = 0;

    public NoiseTuple(float... vals) {
        for (float val : vals) {
            noiseValues.add(val);
            length++;
        }
    }

    public void add(float val) {
        noiseValues.add(val);
        length++;
    }

    public float get(int index) throws IndexOutOfBoundsException {
        if (index < 0 || index >= length)
            throw new IndexOutOfBoundsException("No corresponding noise value in Noise Tuple for index: " + index);

        return noiseValues.get(index);
    }

    public void set(int index, float newValue) throws IndexOutOfBoundsException {
        if (index < 0 || index >= length)
            throw new IndexOutOfBoundsException("No corresponding noise value in Noise Tuple for index: " + index);

        noiseValues.set(index, newValue);
    }

    public List<Float> getNoiseValues() {
        return noiseValues;
    }

    public int size() {
        return length;
    }
}
