package com.yungnickyoung.minecraft.bettercaves.init;

import com.google.common.collect.Lists;
import com.yungnickyoung.minecraft.bettercaves.BetterCaves;
import com.yungnickyoung.minecraft.bettercaves.config.BCSettings;
import com.yungnickyoung.minecraft.bettercaves.config.Configuration;
import com.yungnickyoung.minecraft.bettercaves.world.feature.CarverFeature;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.World;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.carver.ConfiguredCarver;
import net.minecraft.world.gen.feature.*;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.util.*;
import java.util.function.Supplier;

public class BCFeature {
    public static final CarverFeature BETTERCAVES_FEATURE = new CarverFeature(NoFeatureConfig.field_236558_a_);
    public static final ConfiguredFeature<?, ?> CONFIGURED_BETTERCAVES_FEATURE = new ConfiguredFeature<>(BETTERCAVES_FEATURE, new NoFeatureConfig());

    public static void init() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(BCFeature::configChanged);
        FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(Feature.class, BCFeature::registerFeature);
        MinecraftForge.EVENT_BUS.addListener(EventPriority.HIGH, BCFeature::onBiomeLoad);
        MinecraftForge.EVENT_BUS.addListener(BCFeature::worldUnload);
    }

    public static void registerFeature(final RegistryEvent.Register<Feature<?>> event) {
        event.getRegistry().register(BETTERCAVES_FEATURE.setRegistryName(new ResourceLocation(BCSettings.MOD_ID, "bettercaves")));
        Registry.register(WorldGenRegistries.CONFIGURED_FEATURE, new ResourceLocation(BCSettings.MOD_ID, "bettercaves"), CONFIGURED_BETTERCAVES_FEATURE);
    }

    /**
     * Adds configured Portal Lake (aka Rift) and Monolith features to biomes.
     */
    private static void onBiomeLoad(BiomeLoadingEvent event) {
        // Save all pre-existing carvers for biome.
        // These will be used in dimensions where Better Caves is not whitelisted.
        List<Supplier<ConfiguredCarver<?>>> defaultAirCarvers = new ArrayList<>(event.getGeneration().getCarvers(GenerationStage.Carving.AIR));
        List<Supplier<ConfiguredCarver<?>>> defaultLiquidCarvers = new ArrayList<>(event.getGeneration().getCarvers(GenerationStage.Carving.LIQUID));
        if (event.getName() != null) {
            String biomeName = event.getName().toString();
            BetterCaves.defaultBiomeAirCarvers.put(biomeName, defaultAirCarvers);
            BetterCaves.defaultBiomeLiquidCarvers.put(biomeName, defaultLiquidCarvers);
        } else {
            BetterCaves.LOGGER.error("Found missing name when loading biome. This shouldn't happen! Aborting...");
            return;
        }

        // Clear the biome's carvers
        event.getGeneration().getCarvers(GenerationStage.Carving.AIR).clear();
        event.getGeneration().getCarvers(GenerationStage.Carving.LIQUID).clear();

        // Add the carver feature to the start of the RAW_GENERATION phase, ensuring it runs before all other features.
        // This way we can closely simulate carver behavior.
        event.getGeneration().getFeatures(GenerationStage.Decoration.RAW_GENERATION).add(0, () -> CONFIGURED_BETTERCAVES_FEATURE);
    }

    /**
     * Removes the unloaded dimension's carver from the active carvers map.
     */
    public static void worldUnload(WorldEvent.Unload event) {
        BetterCaves.LOGGER.debug("UNLOADING WORLD");
        try {
            String key = Objects.requireNonNull(((World) event.getWorld()).getDimensionKey().getLocation()).toString();
            BetterCaves.activeCarversMap.remove(key);
        } catch (NullPointerException e) {
            BetterCaves.LOGGER.error("ERROR: Unable to unload carver for dimension!");
        }
    }

    /**
     * Parses the whitelisted dimensions string and updates the stored values.
     */
    public static void configChanged(ModConfig.ModConfigEvent event) {
        ModConfig config = event.getConfig();

        if (config.getSpec() == Configuration.SPEC) {
            String rawStringofList = Configuration.whitelistedDimensions.get();
            int strLen = rawStringofList.length();

            // Validate the string's format
            if (strLen < 2 || rawStringofList.charAt(0) != '[' || rawStringofList.charAt(strLen - 1) != ']') {
                BetterCaves.LOGGER.error("INVALID VALUE FOR SETTING 'Whitelisted Dimension IDs'. Using empty list instead...");
                BetterCaves.whitelistedDimensions = Lists.newArrayList();
                return;
            }

            // Parse string to list
            List<String> inputListOfDimensionStrings = Lists.newArrayList(rawStringofList.substring(1, strLen - 1).split(",\\s*"));

            // Parse list of strings, removing any entries that don't match existing dimension names
            List<String> whitelistedDimensions = Lists.newArrayList();
            whitelistedDimensions.addAll(inputListOfDimensionStrings);

            BetterCaves.whitelistedDimensions = whitelistedDimensions;
        }
    }
}
