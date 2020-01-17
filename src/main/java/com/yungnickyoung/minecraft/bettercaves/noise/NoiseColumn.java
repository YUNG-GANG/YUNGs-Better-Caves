package com.yungnickyoung.minecraft.bettercaves.noise;

import java.util.HashMap;
import java.util.Map;

public class NoiseColumn {
    private Map<Integer, NoiseTuple> columnValues = new HashMap<>();
    private int min, max;

    public NoiseColumn() {
    }

    public void put(NoiseTuple noiseTuple, int y) {
        columnValues.put(y, noiseTuple);

    }
}
