package com.yungnickyoung.minecraft.bettercaves.world.carver.surface;

import com.yungnickyoung.minecraft.bettercaves.config.ConfigHolder;

public class VanillaCaveCarverBuilder {
    private int
        bottomY  = 1,
        topY     = 1,
        density  = 0,
        priority = 0;

    public VanillaCaveCarver build() {
        return new VanillaCaveCarver(this);
    }

    /* ================================== Builder Setters ================================== */
    /**
     * @param bottomY minimum cave y-coordinate
     */
    public VanillaCaveCarverBuilder bottomY(int bottomY) {
        this.bottomY = bottomY;
        return this;
    }

    /**
     * @param topY maximum cave y-coordinate
     */
    public VanillaCaveCarverBuilder topY(int topY) {
        this.topY = topY;
        return this;
    }

    /**
     * @param density density of vanilla caves
     */
    public VanillaCaveCarverBuilder density(int density) {
        this.density = density;
        return this;
    }

    /**
     * @param priority priority of vanilla caves
     */
    public VanillaCaveCarverBuilder priority(int priority) {
        this.priority = priority;
        return this;
    }

    /* ================================== Builder Getters ================================== */
    public int getBottomY() {
        return bottomY;
    }

    public int getTopY() {
        return topY;
    }

    public int getDensity() {
        return density;
    }

    public int getPriority() {
        return priority;
    }
}
