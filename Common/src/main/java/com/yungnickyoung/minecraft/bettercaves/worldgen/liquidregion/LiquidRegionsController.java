package com.yungnickyoung.minecraft.bettercaves.worldgen.liquidregion;

import com.google.gson.annotations.SerializedName;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Handles LiquidRegions settings.
 */
@NullMarked
public class LiquidRegionsController {
    // Singleton stuff
    public static @Nullable LiquidRegionsController INSTANCE;
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
     * Map of dimension IDs to LiquidRegions settings.
     * This is deserialized from the liquidregions.json config file.
     */
    @SerializedName("liquidRegions")
    private final Map<Identifier, LiquidRegions.Settings> settingsByDimensionId = new ConcurrentHashMap<>();

    public boolean hasSettingsForDimension(Identifier dimensionId) {
        return this.settingsByDimensionId.containsKey(dimensionId);
    }

    public LiquidRegions.Settings getSettingsForDimension(Identifier dimensionId) {
        return this.settingsByDimensionId.get(dimensionId);
    }

    /**
     * Retrieve the liquid regions settings as set by {@link #withSettings(ServerLevel, ScopedValue.CallableOp)}.
     * @return  the current liquid regions settings, if set
     */
    public LiquidRegions.@Nullable Settings getSettings() {
        var context = LiquidRegionsContext.get();
        if (context == null) {
            return null;
        } else {
            return context.settings();
        }
    }

    /**
     * Call the given callable with context of the liquid regions settings for the given level.
     * The liquid region settings can be retrieved in the callable with {@link #getSettings()}.
     */
    public <R, X extends Throwable> R withSettings(final ServerLevel level, final ScopedValue.CallableOp<R, X> callable) throws X {
        var dimensionId = level.dimension().identifier();
        if (!this.hasSettingsForDimension(dimensionId)) {
            return callable.call();
        } else {
            return LiquidRegionsContext.call(this.settingsByDimensionId.get(dimensionId), callable);
        }
    }

    private static record LiquidRegionsContext(LiquidRegions.Settings settings) {
        private static final ScopedValue<LiquidRegionsContext> CONTEXT = ScopedValue.newInstance();

        public static <R, X extends Throwable> R call(LiquidRegions.Settings settings, ScopedValue.CallableOp<R, X> callable) throws X {
            return ScopedValue.where(CONTEXT, new LiquidRegionsContext(settings))
                    .call(callable);
        }

        public static LiquidRegionsController.@Nullable LiquidRegionsContext get() {
            return CONTEXT.isBound() ? CONTEXT.get() : null;
        }
    }
}
