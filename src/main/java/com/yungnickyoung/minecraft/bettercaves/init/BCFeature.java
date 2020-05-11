package com.yungnickyoung.minecraft.bettercaves.init;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.yungnickyoung.minecraft.bettercaves.BetterCaves;
import com.yungnickyoung.minecraft.bettercaves.config.BCSettings;
import com.yungnickyoung.minecraft.bettercaves.config.Configuration;
import com.yungnickyoung.minecraft.bettercaves.world.feature.CarverFeature;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.carver.ConfiguredCarver;
import net.minecraft.world.gen.feature.*;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.DeferredWorkQueue;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BCFeature {
    private static final DeferredRegister<Feature<?>> FEATURES = new DeferredRegister<>(ForgeRegistries.FEATURES, BCSettings.MOD_ID);
    public static final CarverFeature BETTERCAVES_CARVER = new CarverFeature(NoFeatureConfig::deserialize);
    public static final RegistryObject<Feature<?>> BETTERCAVES_CARVER_FEATURE = FEATURES.register("bettercave", () -> BETTERCAVES_CARVER);

    /**
     * Register
     */
    public static void init() {
        FEATURES.register(FMLJavaModLoadingContext.get().getModEventBus());
        FMLJavaModLoadingContext.get().getModEventBus().addListener(BCFeature::commonSetup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(BCFeature::configChanged);
        MinecraftForge.EVENT_BUS.addListener(BCFeature::worldUnload);
    }

    /**
     * Iterates over all biomes, removing their carvers and adding the Better Caves feature
     * (which wraps all the pre-existing carvers) to the front of their feature lists
     */
    public static void commonSetup(FMLCommonSetupEvent event) {
        DeferredWorkQueue.runLater(BCFeature::lateSetup);
    }

    /**
     * Delayed setup to ensure we collect any carvers added by other mods.
     */
    private static void lateSetup() {
        BetterCaves.LOGGER.info("Replacing biome carvers with Better Caves carvers...");

        // Get all registered biomes
        Set<Map.Entry<ResourceLocation, Biome>> biomesList = ForgeRegistries.BIOMES.getEntries();

        // Replace biome carvers with Better Caves carvers
        for (Map.Entry<ResourceLocation, Biome> e : biomesList) {
            Biome biome = e.getValue();

            // Save all pre-existing carvers for biome.
            // These will be used in dimensions where Better Caves is not whitelisted.
            List<ConfiguredCarver<?>> defaultAirCarvers = biome.getCarvers(GenerationStage.Carving.AIR);
            List<ConfiguredCarver<?>> defaultLiquidCarvers = biome.getCarvers(GenerationStage.Carving.LIQUID);
            BetterCaves.defaultBiomeAirCarvers.put(biome.getClass(), defaultAirCarvers);
            BetterCaves.defaultBiomeLiquidCarvers.put(biome.getClass(), defaultLiquidCarvers);

            // Use Access Transformer to make carvers field public so we can replace with empty list
            biome.carvers = Maps.newHashMap();

            // Use Access Transformer to make features field public so we can put our carver
            // at the front of the list to give it guaranteed priority.
            biome.features.get(GenerationStage.Decoration.RAW_GENERATION).add(0, new ConfiguredFeature<>(new CarverFeature(NoFeatureConfig::deserialize), new NoFeatureConfig()));
        }
    }

    /**
     * Removes the unloaded dimension's carver from the active carvers map.
     */
    public static void worldUnload(WorldEvent.Unload event) {
        BetterCaves.LOGGER.debug(String.format("Unloading world: %s (ID %s)", event.getWorld().getSeed(), event.getWorld().getDimension().getType().getId()));
        try {
            String key = Objects.requireNonNull(DimensionType.getKey(event.getWorld().getDimension().getType())).toString();
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

            // Get set of all vanilla and registered modded dimensions' names
            Set<String> existingDimensionNames = Stream.concat(
                Registry.DIMENSION_TYPE.keySet().stream(),
                ForgeRegistries.MOD_DIMENSIONS.getKeys().stream()
            )
                .map(ResourceLocation::toString)
                .collect(Collectors.toSet());

            // Parse list of strings, removing any entries that don't match existing dimension names
            List<String> whitelistedDimensions = Lists.newArrayList();
            for (String dimensionName : inputListOfDimensionStrings) {
                if (existingDimensionNames.contains(dimensionName))
                    whitelistedDimensions.add(dimensionName);
                else
                    BetterCaves.LOGGER.error(String.format("INVALID DIMENSION ENTRY: %s - Skipping...", dimensionName));
            }

            BetterCaves.whitelistedDimensions = whitelistedDimensions;
        }
    }
}
