package com.yungnickyoung.minecraft.bettercaves.init;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.yungnickyoung.minecraft.bettercaves.BetterCaves;
import com.yungnickyoung.minecraft.bettercaves.config.BCSettings;
import com.yungnickyoung.minecraft.bettercaves.config.Configuration;
import com.yungnickyoung.minecraft.bettercaves.world.feature.CarverFeature;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.carver.ConfiguredCarver;
import net.minecraft.world.gen.feature.*;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class BCFeature {
    public static final CarverFeature BETTERCAVES_FEATURE = new CarverFeature(DefaultFeatureConfig.CODEC);
    public static final ConfiguredFeature<?, ?> CONFIGURED_BETTERCAVES_FEATURE = new ConfiguredFeature<>(BETTERCAVES_FEATURE, new DefaultFeatureConfig());

    /**
     * Register
     */
    public static void init() {
    }

    public static void registerFeature() {
        Registry.register(Registry.FEATURE, new Identifier(BCSettings.MOD_ID, "bettercaves"), BETTERCAVES_FEATURE);
    }

    public static void configChanged() {
        String rawStringofList = BetterCaves.BC_CONFIG.whiteListedDimensions;
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

    /**
     * Delayed setup to ensure we collect any carvers added by other mods.
     */
    public static void lateSetup() {
        ServerWorldEvents.UNLOAD.register((unload, serverWorld) -> worldUnload(serverWorld));


        BetterCaves.LOGGER.info("Replacing biome carvers with Better Caves carvers...");

        // Replace biome carvers with Better Caves carvers
        for (Biome biome : BuiltinRegistries.BIOME) {
            convertImmutableFeatures(biome);

            // Save all pre-existing carvers for biome.
            // These will be used in dimensions where Better Caves is not whitelisted.
            List<Supplier<ConfiguredCarver<?>>> defaultAirCarvers = biome.getGenerationSettings().getCarversForStep(GenerationStep.Carver.AIR);
            List<Supplier<ConfiguredCarver<?>>> defaultLiquidCarvers = biome.getGenerationSettings().getCarversForStep(GenerationStep.Carver.LIQUID);
            BetterCaves.defaultBiomeAirCarvers.put(biome.toString(), convertImmutableList(defaultAirCarvers));
            BetterCaves.defaultBiomeLiquidCarvers.put(biome.toString(), convertImmutableList(defaultLiquidCarvers));

            // Use Access Transformer to make carvers field public so we can replace with empty list
            biome.getGenerationSettings().carvers = Maps.newHashMap();

            // Use Access Transformer to make features field public so we can put our carver
            // at the front of the list to give it guaranteed priority.
            List<List<Supplier<ConfiguredFeature<?, ?>>>> biomeFeatures = biome.getGenerationSettings().features;
            while (biomeFeatures.size() <= GenerationStep.Feature.RAW_GENERATION.ordinal()) {
                biomeFeatures.add(Lists.newArrayList());
            }
            List<Supplier<ConfiguredFeature<?, ?>>> rawGenSuppliers = convertImmutableList(biomeFeatures.get(GenerationStep.Feature.RAW_GENERATION.ordinal()));
            rawGenSuppliers.add(0, () -> CONFIGURED_BETTERCAVES_FEATURE);
            biomeFeatures.set(GenerationStep.Feature.RAW_GENERATION.ordinal(), rawGenSuppliers);
        }
    }

    /**
     * Removes the unloaded dimension's carver from the active carvers map.
     */


    public static void worldUnload(ServerWorld event) {
        BetterCaves.LOGGER.debug("UNLOADING WORLD");
        try {
            String key = Objects.requireNonNull((World)event).getRegistryKey().getValue().toString();
            BetterCaves.activeCarversMap.remove(key);
        } catch (NullPointerException e) {
            BetterCaves.LOGGER.error("ERROR: Unable to unload carver for dimension!");
        }
    }

    /**
     * In 1.16.2, many lists were made immutable. Other modders seemingly have confused themselves and made
     * mutable lists immutable after processing them. This method serves to help avoid problems arising from
     * attempting to modify immutable collections.
     */
    private static void convertImmutableFeatures(Biome biome) {
        if (biome.getGenerationSettings().features instanceof ImmutableList) {
            biome.getGenerationSettings().features = biome.getGenerationSettings().features.stream().map(Lists::newArrayList).collect(Collectors.toList());
        }
    }

    /**
     * In 1.16.2, many lists were made immutable. Other modders seemingly have confused themselves and made
     * mutable lists immutable after processing them. This method serves to help avoid problems arising from
     * attempting to modify immutable collections.
     */
    private static <T> List<T> convertImmutableList(List<T> list) {
        return new ArrayList<>(list);
    }
}
