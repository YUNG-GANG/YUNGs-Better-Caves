package com.yungnickyoung.minecraft.bettercaves.worldgen.liquidregion;

import com.google.gson.annotations.SerializedName;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class LiquidRegionsController {
    // Singleton stuff
    public static LiquidRegionsController INSTANCE;
    public static LiquidRegionsController getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new LiquidRegionsController();
        }
        return INSTANCE;
    }

    // Default settings
    public static void useDefaultSettings() {
        LiquidRegionsController controller = new LiquidRegionsController();
        controller.settingsByDimensionId.put(Identifier.withDefaultNamespace("overworld"), LiquidRegions.Settings.DEFAULT.copy());
        INSTANCE = controller;
    }

    /**
     * Map of ServerLevels to their corresponding LiquidRegions instances.
     * This is populated at runtime as needed, depending on the settingsByDimensionId map.
     */
    private transient final ConcurrentMap<ServerLevel, LiquidRegions> regionsByLevel = new ConcurrentHashMap<>();

    /**
     * Map of dimension IDs to LiquidRegions settings.
     * This is deserialized from the liquidregions.json config file.
     */
    @SerializedName("liquidRegions")
    private final Map<Identifier, LiquidRegions.Settings> settingsByDimensionId = new ConcurrentHashMap<>();

    public boolean hasSettingsForLevel(ServerLevel serverLevel) {
        Identifier dimensionId = serverLevel.dimension().identifier();
        return this.settingsByDimensionId.containsKey(dimensionId);
    }

    public LiquidRegions getLiquidRegionsForServerLevel(ServerLevel serverLevel) {
        return this.regionsByLevel.computeIfAbsent(serverLevel, sl -> {
            LiquidRegions.Settings settings = this.settingsByDimensionId.get(sl.dimension().identifier());
            if (settings == null) {
                throw new IllegalStateException("No LiquidRegions settings found for dimension: " + sl.dimension().identifier());
            }
            return new LiquidRegions(sl, settings);
        });
    }
}
