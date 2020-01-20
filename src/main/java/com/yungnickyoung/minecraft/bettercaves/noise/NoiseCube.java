package com.yungnickyoung.minecraft.bettercaves.noise;

import java.util.ArrayList;
import java.util.List;

public class NoiseCube {
    private List<List<NoiseColumn>> cubeValues = new ArrayList<>();
    private int length;

    public NoiseCube(int edgeLength) {
        this.length = edgeLength;
        for (int x = 0; x < length; x++) {
            List<NoiseColumn> xLayer = new ArrayList<>();
            for (int z = 0; z < length; z++) {
                NoiseColumn col = new NoiseColumn();
                xLayer.add(col);
            }
            cubeValues.add(xLayer);
        }
    }

    public List<NoiseColumn> get(int index) throws IndexOutOfBoundsException {
        if (index < 0 || index >= length)
            throw new IndexOutOfBoundsException("No corresponding noise value in Noise Tuple for index: " + index);

        return cubeValues.get(index);
    }
}
