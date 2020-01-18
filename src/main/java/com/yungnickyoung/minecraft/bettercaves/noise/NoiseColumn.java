package com.yungnickyoung.minecraft.bettercaves.noise;

import java.util.HashMap;
import java.util.Map;

public class NoiseColumn {
    private Map<Integer, NoiseTuple> columnValues = new HashMap<>();
    private int min = Integer.MAX_VALUE;
    private int max = Integer.MIN_VALUE;

    public NoiseColumn() {
    }

    /**
     * Adds a NoiseTuple to this column.
     * @param y y-value of the new NoiseTuple
     * @param noiseTuple the new NoiseTuple
     */
    public void put(int y, NoiseTuple noiseTuple) {
        columnValues.put(y, noiseTuple);
        if (y < min) min = y;
        if (y > max) max = y;
    }

    /**
     * Retrieves the NoiseTuple at the specified y-value
     * @param y The y-value of the desired NoiseTuple
     * @return The NoiseTuple at the given y-value
     * @throws IndexOutOfBoundsException if the y-value is out of this column's bounds
     */
    public NoiseTuple get(int y) throws IndexOutOfBoundsException {
        if (y < min || y > max)
            throw new IndexOutOfBoundsException("No corresponding noise value in NoiseColumn for y-value: " + y);

        return columnValues.get(y);
    }

    /**
     * Retrieves all the NoiseTuples of this column as a Map of y-coords to NoiseTuples.
     * @return Map of y-coordinates to NoiseTuples.
     */
    public Map<Integer, NoiseTuple> getColumnValues() {
        return this.columnValues;
    }
}
