package com.yungnickyoung.minecraft.bettercaves.init;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.yungnickyoung.minecraft.bettercaves.BetterCaves;
import com.yungnickyoung.minecraft.bettercaves.config.BCSettings;
import com.yungnickyoung.minecraft.bettercaves.config.Configuration;
import com.yungnickyoung.minecraft.bettercaves.world.feature.CarverFeature;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.carver.ConfiguredCarver;
import net.minecraft.world.gen.feature.*;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.DeferredWorkQueue;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class BCFeature {
    public static final CarverFeature BETTERCAVES_FEATURE = new CarverFeature(NoFeatureConfig.field_236558_a_);
    public static final ConfiguredFeature<?, ?> CONFIGURED_BETTERCAVES_FEATURE = new ConfiguredFeature<>(BETTERCAVES_FEATURE, new NoFeatureConfig());

    /**
     * Register
     */
    public static void init() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(BCFeature::commonSetup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(BCFeature::configChanged);
        FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(Feature.class, BCFeature::registerFeature);
        MinecraftForge.EVENT_BUS.addListener(BCFeature::worldUnload);
    }

    public static void registerFeature(final RegistryEvent.Register<Feature<?>> event) {
        Registry.register(Registry.FEATURE, new ResourceLocation(BCSettings.MOD_ID, "bettercaves"), BETTERCAVES_FEATURE);
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

        // Replace biome carvers with Better Caves carvers
        for (Biome biome : WorldGenRegistries.field_243657_i) {
            convertImmutableFeatures(biome);

            // Save all pre-existing carvers for biome.
            // These will be used in dimensions where Better Caves is not whitelisted.
            List<Supplier<ConfiguredCarver<?>>> defaultAirCarvers = biome.func_242440_e().func_242489_a(GenerationStage.Carving.AIR);
            List<Supplier<ConfiguredCarver<?>>> defaultLiquidCarvers = biome.func_242440_e().func_242489_a(GenerationStage.Carving.LIQUID);
            BetterCaves.defaultBiomeAirCarvers.put(biome.toString(), defaultAirCarvers);
            BetterCaves.defaultBiomeLiquidCarvers.put(biome.toString(), defaultLiquidCarvers);

            // Use Access Transformer to make carvers field public so we can replace with empty list
            biome.func_242440_e().field_242483_e = Maps.newHashMap();

            // Use Access Transformer to make features field public so we can put our carver
            // at the front of the list to give it guaranteed priority.
            List<List<Supplier<ConfiguredFeature<?, ?>>>> biomeFeatures = biome.func_242440_e().field_242484_f;
            while (biomeFeatures.size() <= GenerationStage.Decoration.RAW_GENERATION.ordinal()) {
                biomeFeatures.add(Lists.newArrayList());
            }
            biomeFeatures.get(GenerationStage.Decoration.RAW_GENERATION.ordinal()).add(0,
                () -> CONFIGURED_BETTERCAVES_FEATURE
            );
        }
    }

    /**
     * Removes the unloaded dimension's carver from the active carvers map.
     */
    public static void worldUnload(WorldEvent.Unload event) {
        BetterCaves.LOGGER.debug("UNLOADING WORLD");
        try {
            String key = Objects.requireNonNull(((World) event.getWorld()).getDimensionKey().func_240901_a_()).toString();
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

    private static void convertImmutableFeatures(Biome biome) {
        if (biome.func_242440_e().field_242484_f instanceof ImmutableList) {
            biome.func_242440_e().field_242484_f = biome.func_242440_e().field_242484_f.stream().map(Lists::newArrayList).collect(Collectors.toList());
        }
    }
}
